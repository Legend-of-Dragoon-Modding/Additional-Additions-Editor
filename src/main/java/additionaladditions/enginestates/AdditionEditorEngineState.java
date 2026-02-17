package additionaladditions.enginestates;

import additionaladditions.AdditionManager;
import additionaladditions.AdditionalAdditionsEngineStateTypes;
import additionaladditions.ModelManager;
import additionaladditions.screens.TopLevelScreen;
import legend.core.MathHelper;
import legend.game.EngineState;
import legend.game.types.GameState52c;
import legend.game.types.GsF_LIGHT;
import legend.game.types.GsRVIEW2;
import legend.game.unpacker.FileData;

import static legend.game.Graphics.GsSetFlatLight;
import static legend.game.Graphics.GsSetRefView2L;
import static legend.game.Graphics.clearBlue_800babc0;
import static legend.game.Graphics.clearGreen_800bb104;
import static legend.game.Graphics.clearRed_8007a3a8;
import static legend.game.Graphics.resizeDisplay;
import static legend.game.Graphics.vsyncMode_8007a3b8;
import static legend.game.SItem.menuStack;

public class AdditionEditorEngineState extends EngineState<AdditionEditorEngineState> {
  private final GsRVIEW2 camera = new GsRVIEW2();
  private final GsF_LIGHT[] lights = new GsF_LIGHT[3];

  private final AdditionManager additionManager;
  private final ModelManager modelManager;

  public AdditionEditorEngineState(final AdditionManager additionManager, final ModelManager modelManager) {
    super(AdditionalAdditionsEngineStateTypes.ADDITION_EDITOR.get());
    this.additionManager = additionManager;
    this.modelManager = modelManager;
  }

  @Override
  public FileData writeSaveData(final GameState52c gameState) {
    return null;
  }

  @Override
  public void readSaveData(final GameState52c gameState, final FileData data) {

  }

  @Override
  public int tickMultiplier() {
    return 1;
  }

  @Override
  public GsRVIEW2 getCamera() {
    return this.camera;
  }

  @Override
  public void init() {
    super.init();
    resizeDisplay(320, 240);
    vsyncMode_8007a3b8 = 3;

    this.additionManager.loadAll(this.modelManager);

    menuStack.pushScreen(new TopLevelScreen(this.modelManager, this.additionManager));

    for(int i = 0; i < 3; i++) {
      final GsF_LIGHT light = new GsF_LIGHT();
      this.lights[i] = light;
      light.r_0c = 0.125f;
      light.g_0d = 0.125f;
      light.b_0e = 0.125f;
      light.direction_00.x = MathHelper.sin(0.2617994f);
      light.direction_00.y = MathHelper.cos(5.497787f);
      light.direction_00.z = MathHelper.cosFromSin(0.2617994f, light.direction_00.x);
      light.direction_00.set(0.24414062f, 0.024414062f, 0.0f);
    }

    clearRed_8007a3a8 = 0;
    clearGreen_800bb104 = 0;
    clearBlue_800babc0 = 0;
  }

  @Override
  public void tick() {
    super.tick();

    for(int i = 0; i < 3; i++) {
      GsSetFlatLight(i, this.lights[i]);
    }

    this.camera.viewpoint_00.set(-2000.0f, -2000.0f, -5000.0f);
    this.camera.refpoint_0c.set(0.0f, -1000.0f, 0.0f);
    GsSetRefView2L(this.camera);

    menuStack.render();
  }
}
