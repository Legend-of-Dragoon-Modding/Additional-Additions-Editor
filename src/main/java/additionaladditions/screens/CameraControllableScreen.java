package additionaladditions.screens;

import legend.core.MathHelper;
import legend.core.platform.input.InputMod;
import legend.game.inventory.screens.InputPropagation;
import legend.game.inventory.screens.MenuScreen;
import legend.game.modding.coremod.CoreMod;
import org.joml.Vector3f;

import java.util.Set;

import static legend.core.GameEngine.CONFIG;
import static legend.core.GameEngine.PLATFORM;
import static legend.core.GameEngine.RENDERER;

public abstract class CameraControllableScreen extends MenuScreen {
  private boolean movingCamera;
  private float cameraYaw;
  private float cameraPitch;
  private float cameraDistance = -5000;
  private final Vector3f cameraPos = new Vector3f();
  private final Vector3f cameraRef = new Vector3f(0.0f, -1000.0f, 0.0f);

  private void updateCamera() {
    final float sin = MathHelper.sin(this.cameraYaw);
    final float cos = MathHelper.cosFromSin(sin, this.cameraYaw);
    this.cameraPos.set(cos * this.cameraDistance, -2000.0f, sin * this.cameraDistance);
    RENDERER.camera().lookAt(this.cameraPos, this.cameraRef);
  }

  @Override
  protected InputPropagation mouseMove(final double x, final double y) {
    if(super.mouseMove(x, y) == InputPropagation.HANDLED) {
      return InputPropagation.HANDLED;
    }

    if(this.movingCamera) {
      this.cameraYaw -= x / 100.0f;
      this.cameraPitch += y / 100.0f;
      this.updateCamera();
      return InputPropagation.HANDLED;
    }

    return InputPropagation.PROPAGATE;
  }

  @Override
  protected InputPropagation mousePress(final double x, final double y, final int button, final Set<InputMod> mods) {
    if(super.mousePress(x, y, button, mods) == InputPropagation.HANDLED) {
      return InputPropagation.HANDLED;
    }

    if(button == 3) {
      RENDERER.window().disableCursor();
      this.movingCamera = true;
      return InputPropagation.HANDLED;
    }

    return InputPropagation.PROPAGATE;
  }

  @Override
  protected InputPropagation mouseRelease(final double x, final double y, final int button, final Set<InputMod> mods) {
    if(super.mouseRelease(x, y, button, mods) == InputPropagation.HANDLED) {
      return InputPropagation.HANDLED;
    }

    if(button == 3) {
      if(CONFIG.getConfig(CoreMod.DISABLE_MOUSE_INPUT_CONFIG.get()) && PLATFORM.hasGamepad()) {
        RENDERER.window().hideCursor();
      } else {
        RENDERER.window().showCursor();
      }

      this.movingCamera = false;
      return InputPropagation.HANDLED;
    }

    return InputPropagation.PROPAGATE;
  }

  @Override
  protected InputPropagation mouseScrollHighRes(final double deltaX, final double deltaY) {
    if(super.mouseScrollHighRes(deltaX, deltaY) == InputPropagation.HANDLED) {
      return InputPropagation.HANDLED;
    }

    this.cameraDistance += deltaY * 350.0f;
    this.updateCamera();
    return InputPropagation.PROPAGATE;
  }
}
