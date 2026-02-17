package additionaladditions;

import additionaladditions.enginestates.AdditionEditorEngineState;
import legend.game.EngineStateType;
import legend.game.RegisterEngineStateTypesEvent;
import org.legendofdragoon.modloader.registries.Registrar;
import org.legendofdragoon.modloader.registries.RegistryDelegate;

import static legend.core.GameEngine.REGISTRIES;

public final class AdditionalAdditionsEngineStateTypes {
  private AdditionalAdditionsEngineStateTypes() { }

  private static AdditionManager additionManager;
  private static ModelManager modelManager;

  private static final Registrar<EngineStateType<?>, RegisterEngineStateTypesEvent> REGISTRAR = new Registrar<>(REGISTRIES.engineStateTypes, AdditionalAdditionsMod.MOD_ID);

  public static final RegistryDelegate<EngineStateType<AdditionEditorEngineState>> ADDITION_EDITOR = REGISTRAR.register("addition_editor", () -> new EngineStateType<>(AdditionEditorEngineState.class, () -> new AdditionEditorEngineState(additionManager, modelManager)));

  static void register(final RegisterEngineStateTypesEvent event, final AdditionManager additionManager, final ModelManager modelManager) {
    AdditionalAdditionsEngineStateTypes.additionManager = additionManager;
    AdditionalAdditionsEngineStateTypes.modelManager = modelManager;

    REGISTRAR.registryEvent(event);
  }
}
