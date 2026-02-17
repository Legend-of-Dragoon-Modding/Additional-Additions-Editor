package additionaladditions;

import legend.core.platform.input.ButtonInputActivation;
import legend.core.platform.input.InputActionRegistryEvent;
import legend.core.platform.input.InputButton;
import legend.core.platform.input.InputKey;
import legend.core.platform.input.InputMod;
import legend.core.platform.input.KeyInputActivation;
import legend.game.RegisterEngineStateTypesEvent;
import legend.game.modding.events.input.RegisterDefaultInputBindingsEvent;
import legend.game.saves.RegisterCampaignTypesEvent;
import org.legendofdragoon.modloader.Mod;
import org.legendofdragoon.modloader.events.EventListener;
import org.legendofdragoon.modloader.registries.RegistryId;

import static legend.core.GameEngine.EVENTS;

@Mod(id = AdditionalAdditionsEditorMod.MOD_ID, version = "3.0.0")
public class AdditionalAdditionsEditorMod {
  public static final String MOD_ID = "additional_additions_editor";

  private final AdditionManager additionManager = new AdditionManager();
  private final ModelManager modelManager = new ModelManager();

  public AdditionalAdditionsEditorMod() {
    EVENTS.register(this);
  }

  public static RegistryId id(final String entryId) {
    return new RegistryId(MOD_ID, entryId);
  }

  @EventListener
  public void registerInputActions(final InputActionRegistryEvent event) {
    AdditionalAdditionsInputActions.register(event);
  }

  @EventListener
  public void registerCampaignTypes(final RegisterCampaignTypesEvent event) {
    AdditionalAdditionsCampaignTypes.register(event);
  }

  @EventListener
  public void registerEngineStateTypes(final RegisterEngineStateTypesEvent event) {
    AdditionalAdditionsEngineStateTypes.register(event, this.additionManager, this.modelManager);
  }

  @EventListener
  public void addDefaultInputActionBindings(final RegisterDefaultInputBindingsEvent event) {
    event.add(AdditionalAdditionsInputActions.SAVE_ADDITION.get(), new KeyInputActivation(InputKey.S, InputMod.CTRL));
    event.add(AdditionalAdditionsInputActions.SAVE_ADDITION.get(), new ButtonInputActivation(InputButton.START));
  }
}
