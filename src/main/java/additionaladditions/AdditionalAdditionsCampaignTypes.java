package additionaladditions;

import additionaladditions.campaigntypes.AdditionEditorCampaignType;
import legend.game.saves.CampaignType;
import legend.game.saves.RegisterCampaignTypesEvent;
import org.legendofdragoon.modloader.registries.Registrar;
import org.legendofdragoon.modloader.registries.RegistryDelegate;

import static legend.core.GameEngine.REGISTRIES;

public final class AdditionalAdditionsCampaignTypes {
  private AdditionalAdditionsCampaignTypes() { }

  private static final Registrar<CampaignType, RegisterCampaignTypesEvent> REGISTRAR = new Registrar<>(REGISTRIES.campaignTypes, AdditionalAdditionsEditorMod.MOD_ID);

  public static final RegistryDelegate<AdditionEditorCampaignType> ADDITION_EDITOR = REGISTRAR.register("addition_editor", AdditionEditorCampaignType::new);

  static void register(final RegisterCampaignTypesEvent event) {
    REGISTRAR.registryEvent(event);
  }
}
