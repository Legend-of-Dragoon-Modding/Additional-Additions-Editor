package additionaladditions;

import it.unimi.dsi.fastutil.ints.Int2IntMap;
import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import legend.core.IoHelper;
import legend.core.QueuedModelTmd;
import legend.core.gpu.Rect4i;
import legend.core.gte.MV;
import legend.core.gte.ModelPart10;
import legend.game.tim.Tim;
import legend.game.tmd.Tmd;
import legend.game.tmd.UvAdjustmentMetrics14;
import legend.game.types.CContainer;
import legend.game.types.Model124;
import legend.game.types.TmdAnimationFile;
import legend.game.unpacker.FileData;
import legend.game.unpacker.Loader;
import legend.game.unpacker.Unpacker;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static legend.core.GameEngine.GPU;
import static legend.core.GameEngine.RENDERER;
import static legend.game.DrgnFiles.loadDrgnDirSync;
import static legend.game.DrgnFiles.loadFile;
import static legend.game.Graphics.GsGetLw;
import static legend.game.Graphics.GsSetLightMatrix;
import static legend.game.Graphics.lightColourMatrix_800c3508;
import static legend.game.Graphics.lightDirectionMatrix_800c34e8;
import static legend.game.Graphics.tmdGp0Tpage_1f8003ec;
import static legend.game.Graphics.zOffset_1f8003e8;
import static legend.game.Models.adjustModelUvs;
import static legend.game.Models.animateModel;
import static legend.game.Models.applyModelRotationAndScale;
import static legend.game.Models.loadModelAndAnimation;
import static legend.game.Models.loadModelStandardAnimation;
import static legend.game.Scus94491BpeSegment.getCharacterName;

public class ModelManager {
  private final UvAdjustmentMetrics14 uvAdjustment = new UvAdjustmentMetrics14( 1, 320, 496, 320, 256);

  private final Model124 model = new Model124("Character");
  private final MV lw = new MV();
  private Tmd tmd;
  private TmdAnimationFile idle;

  private final List<TmdAnimationFile>[] additionAnimationParts = new List[9];
  private final Int2IntMap fileToCrcMap = new Int2IntOpenHashMap();
  private final Int2ObjectMap<String>[] fileToFilenameMap = new Int2ObjectOpenHashMap[9];

  public ModelManager() {
    this.model.uvAdjustments_9d = this.uvAdjustment;

    for(int charId = 0; charId < this.additionAnimationParts.length; charId++) {
      this.additionAnimationParts[charId] = new ArrayList<>();
      this.fileToFilenameMap[charId] = new Int2ObjectOpenHashMap<>();

      final Int2IntMap knownCrcs = new Int2IntOpenHashMap();

      for(int additionIndex = 0; additionIndex < 8; additionIndex++) {
        final int additionPackageIndex = 4031 + charId * 8 + additionIndex;

        final List<FileData> files = new ArrayList<>();
        loadDrgnDirSync(0, additionPackageIndex, files::addAll);

        for(int fileIndex = 16; fileIndex < files.size(); fileIndex++) {
          FileData file = files.get(fileIndex);

          if(file.readInt(0x4) == 0x1a45_5042) { // BPE
            file = new FileData(Unpacker.decompress(file));
          }

          final int crc = IoHelper.crc32(file.getBytes());

          if(!knownCrcs.containsKey(crc)) {
            knownCrcs.put(crc, this.additionAnimationParts[charId].size());
            this.fileToFilenameMap[charId].put(this.additionAnimationParts[charId].size(), "SECT/DRGN0.BIN/%d/%d".formatted(additionPackageIndex, fileIndex));
            this.additionAnimationParts[charId].add(new TmdAnimationFile(file));
          }

          this.fileToCrcMap.put(additionPackageIndex << 8 | fileIndex - 16, knownCrcs.get(crc));
        }
      }
    }
  }

  public int getCacheIndexForAdditionPackage(final int additionPackageIndex, final int animationIndex) {
    return this.fileToCrcMap.get(additionPackageIndex << 8 | animationIndex);
  }

  public String getFilenameForCacheIndex(final int charId, final int cacheIndex) {
    return this.fileToFilenameMap[charId].get(cacheIndex);
  }

  public int getCacheIndexForFilename(final int charId, final String filename) {
    for(final var entry : this.fileToFilenameMap[charId].int2ObjectEntrySet()) {
      if(entry.getValue().equals(filename)) {
        return entry.getIntKey();
      }
    }

    throw new RuntimeException("Couldn't find animation " + filename);
  }

  public void loadCharacter(final int charId) {
    synchronized(this) {
      this.tmd = null;
      final String charName = getCharacterName(charId).toLowerCase();
      this.onCharacterModelLoaded(charName, Loader.loadDirectory("characters/%s/models/combat".formatted(charName)));
      loadFile("characters/%s/textures/combat".formatted(charName), this::onCharacterTexture);
    }
  }

  private void onCharacterModelLoaded(final String charName, final List<FileData> files) {
    this.idle = new TmdAnimationFile(files.get(0));
    final CContainer c = new CContainer(charName, files.get(32));

    this.model.modelParts_00 = new ModelPart10[c.tmdPtr_00.tmd.header.nobj];
    Arrays.setAll(this.model.modelParts_00, i -> new ModelPart10());

    loadModelAndAnimation(this.model, c, this.idle);
    adjustModelUvs(this.model);

    this.tmd = c.tmdPtr_00.tmd;
  }

  private void onCharacterTexture(final FileData file) {
    final Tim tim = new Tim(file);

    final Rect4i imageRect = tim.getImageRect();
    imageRect.x = this.uvAdjustment.tpageX;
    imageRect.y = this.uvAdjustment.tpageY;
    GPU.uploadData15(imageRect, tim.getImageData());

    final Rect4i clutRect = tim.getClutRect();
    clutRect.x = this.uvAdjustment.clutX;
    clutRect.y = this.uvAdjustment.clutY;
    GPU.uploadData15(clutRect, tim.getClutData());
  }

  public int getAnimationCount(final int charId) {
    return this.additionAnimationParts[charId].size();
  }

  public void applyIdleAnimation() {
    loadModelStandardAnimation(this.model, this.idle);
  }

  public void applyAnimation(final int charId, final int animationIndex) {
    loadModelStandardAnimation(this.model, this.additionAnimationParts[charId].get(animationIndex));
    this.model.disableInterpolation_a2 = true;
  }

  public void restartAnimation() {
    this.model.animationState_9c = 0;
  }

  public int getAnimationCurrentFrame() {
    return this.model.currentKeyframe_94;
  }

  public int getAnimationTotalFrames() {
    return this.model.totalFrames_9a / 2;
  }

  public void render() {
    this.render(false);
  }

  public void render(final boolean alwaysAnimate) {
    synchronized(this) {
      if(this.tmd == null) {
        return;
      }

      applyModelRotationAndScale(this.model);

      if(alwaysAnimate || this.model.remainingFrames_9e != 0) {
        animateModel(this.model);
      }

      tmdGp0Tpage_1f8003ec = this.model.tpage_108;
      zOffset_1f8003e8 = this.model.zOffset_a0;

      //LAB_800ec9d0
      for(int i = 0; i < this.model.modelParts_00.length; i++) {
        if((this.model.partInvisible_f4 & 1L << i) == 0) {
          final ModelPart10 part = this.model.modelParts_00[i];

          GsGetLw(part.coord2_04, this.lw);
          GsSetLightMatrix(this.lw);

          RENDERER.queueModel(this.model.modelParts_00[i].tmd_08.getObj(), this.lw, QueuedModelTmd.class)
            .lightDirection(lightDirectionMatrix_800c34e8)
            .lightColour(lightColourMatrix_800c3508)
            .backgroundColour(new Vector3f(0.75f, 0.75f, 0.75f))
          ;
        }
      }
    }
  }
}
