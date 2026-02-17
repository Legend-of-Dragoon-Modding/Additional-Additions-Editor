package additionaladditions;

import legend.core.platform.input.InputAction;
import legend.core.platform.input.InputActionRegistryEvent;
import org.legendofdragoon.modloader.registries.Registrar;
import org.legendofdragoon.modloader.registries.RegistryDelegate;

import static legend.core.GameEngine.REGISTRIES;

public final class AdditionalAdditionsInputActions {
  private AdditionalAdditionsInputActions() { }

  private static final Registrar<InputAction, InputActionRegistryEvent> REGISTRAR = new Registrar<>(REGISTRIES.inputActions, AdditionalAdditionsMod.MOD_ID);

  public static final RegistryDelegate<InputAction> SAVE_ADDITION = REGISTRAR.register("save_addition", InputAction.make().build());

  static void register(final InputActionRegistryEvent event) {
    REGISTRAR.registryEvent(event);
  }
}
