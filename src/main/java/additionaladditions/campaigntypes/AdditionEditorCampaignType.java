package additionaladditions.campaigntypes;

import additionaladditions.AdditionalAdditionsEngineStateTypes;
import legend.game.saves.CampaignType;
import legend.game.types.GameState52c;

import static legend.game.EngineStates.engineStateOnceLoaded_8004dd24;

public class AdditionEditorCampaignType extends CampaignType {
  @Override
  public void setUpNewCampaign(final GameState52c gameState) {

  }

  @Override
  public void transitionToNewCampaign(final GameState52c gameState) {
    engineStateOnceLoaded_8004dd24 = AdditionalAdditionsEngineStateTypes.ADDITION_EDITOR.get();
  }

  @Override
  public void setUpLoadedGame(final GameState52c gameState) {

  }

  @Override
  public void transitionToLoadedGame(final GameState52c gameState) {
    engineStateOnceLoaded_8004dd24 = AdditionalAdditionsEngineStateTypes.ADDITION_EDITOR.get();
  }
}
