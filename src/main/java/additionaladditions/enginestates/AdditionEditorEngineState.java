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
import org.joml.Vector3f;

import static legend.core.GameEngine.RENDERER;
import static legend.game.Graphics.GsSetFlatLight;
import static legend.game.Graphics.clearBlue_800babc0;
import static legend.game.Graphics.clearGreen_800bb104;
import static legend.game.Graphics.clearRed_8007a3a8;
import static legend.game.Graphics.resizeDisplay;
import static legend.game.Graphics.vsyncMode_8007a3b8;
import static legend.game.SItem.menuStack;

public class AdditionEditorEngineState extends EngineState<AdditionEditorEngineState> {
  private final GsF_LIGHT[] lights = new GsF_LIGHT[3];

  private final AdditionManager additionManager;
  private final ModelManager modelManager;

  private final Vector3f cameraPos = new Vector3f();
  private final Vector3f cameraRef = new Vector3f();

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
    return null;
  }

  @Override
  public void init() {
    super.init();
    resizeDisplay(320, 240);
    vsyncMode_8007a3b8 = 1;

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

    this.cameraPos.set(-2000.0f, -2000.0f, -5000.0f);
    this.cameraRef.set(0.0f, -1000.0f, 0.0f);
    this.updateCamera();
  }

  @Override
  public void tick() {
    super.tick();

    for(int i = 0; i < 3; i++) {
      GsSetFlatLight(i, this.lights[i]);
    }

    menuStack.render();
  }

  private void updateCamera() {
    RENDERER.camera().lookAt(this.cameraPos, this.cameraRef);
  }
}
