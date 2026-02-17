package additionaladditions.screens;

import additionaladditions.AdditionManager;
import additionaladditions.Additional;
import additionaladditions.ModelManager;
import legend.game.SItem;
import legend.game.additions.Addition;
import legend.game.i18n.I18n;
import legend.game.inventory.screens.MenuScreen;
import legend.game.inventory.screens.MessageBoxScreen;
import legend.game.inventory.screens.controls.Button;
import legend.game.inventory.screens.controls.Dropdown;
import legend.game.modding.coremod.CoreEngineStateTypes;
import legend.game.types.MessageBoxResult;
import org.legendofdragoon.modloader.registries.RegistryDelegate;
import org.legendofdragoon.modloader.registries.RegistryId;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static legend.game.EngineStates.engineStateOnceLoaded_8004dd24;
import static legend.game.Scus94491BpeSegment.resetSubmapToNewGame;
import static legend.game.Scus94491BpeSegment_8004.CHARACTER_ADDITIONS;
import static legend.game.modding.coremod.CoreMod.INPUT_ACTION_MENU_BACK;
import static legend.game.sound.Audio.playMenuSound;

public class TopLevelScreen extends MenuScreen {
  private final ModelManager modelManager;
  private final AdditionManager additionManager;

  private final Dropdown<RegistryId> characterDropdown;
  private final Dropdown<Additional> selectedAdditionDropdown;
  private final Button removeAddition;
  private final Button editAddition;

  @Nullable
  private Additional selectedAdditional;

  public TopLevelScreen(final ModelManager modelManager, final AdditionManager additionManager) {
    this.modelManager = modelManager;
    this.additionManager = additionManager;

    //TODO temporary until char registry is implemented
    final Map<RegistryId, String> nameMap = new LinkedHashMap<>();
    for(int i = 0; i < SItem.characterNames_801142dc.length; i++) {
      nameMap.put(AdditionManager.CHAR_IDS.get(i), SItem.characterNames_801142dc[i]);
    }

    this.addHotkey(I18n.translate("additional_additions_editor.screens.top_level.quit"), INPUT_ACTION_MENU_BACK, this::quit);

    this.characterDropdown = this.addControl(new Dropdown<>(nameMap::get));
    this.characterDropdown.setScale(0.5f);
    this.characterDropdown.setHeight(10);
    this.characterDropdown.onSelection(this::onCharacterSelected);

    for(final RegistryId charId : nameMap.keySet()) {
      this.characterDropdown.addOption(charId);
    }

    this.selectedAdditionDropdown = this.addControl(new Dropdown<>(additional -> additional.name));
    this.selectedAdditionDropdown.setScale(0.5f);
    this.selectedAdditionDropdown.setHeight(10);
    this.selectedAdditionDropdown.onSelection(this::onSelectedAdditionSelected);
    this.selectedAdditionDropdown.setY(this.characterDropdown.getY() + this.characterDropdown.getHeight() + 2);

    final Button addAddition = this.addControl(new Button(I18n.translate("additional_additions_editor.screens.top_level.add_addition")));
    addAddition.setScale(0.5f);
    addAddition.setY(this.selectedAdditionDropdown.getY() + this.selectedAdditionDropdown.getHeight() + 2);
    addAddition.setSize(25, this.selectedAdditionDropdown.getHeight());
    addAddition.onPressed(this::onAddAdditionClick);

    this.removeAddition = this.addControl(new Button(I18n.translate("additional_additions_editor.screens.top_level.remove_addition")));
    this.removeAddition.setScale(0.5f);
    this.removeAddition.setPos(addAddition.getX() + addAddition.getWidth() + 2, addAddition.getY());
    this.removeAddition.setSize(25, this.selectedAdditionDropdown.getHeight());
    this.removeAddition.onPressed(this::onRemoveAdditionClick);

    this.editAddition = this.addControl(new Button(I18n.translate("additional_additions_editor.screens.top_level.edit_addition")));
    this.editAddition.setScale(0.5f);
    this.editAddition.setPos(this.removeAddition.getX() + this.removeAddition.getWidth() + 2, this.removeAddition.getY());
    this.editAddition.setSize(25, this.selectedAdditionDropdown.getHeight());
    this.editAddition.onPressed(this::onEditAdditionClick);

    this.onCharacterSelected(0);
  }

  private void onCharacterSelected(final int charId) {
    this.modelManager.loadCharacter(charId);
    this.selectedAdditionDropdown.clearOptions();

    for(final Additional additional : this.additionManager.getAdditionals(this.characterDropdown.getSelectedOption())) {
      this.selectedAdditionDropdown.addOption(additional);
    }

    this.selectedAdditionDropdown.setSelectedIndex(0);
    this.onSelectedAdditionSelected(this.selectedAdditionDropdown.getSelectedIndex());
  }

  private void onSelectedAdditionSelected(final int additionIndex) {
    this.removeAddition.setVisibility(additionIndex != -1);
    this.editAddition.setVisibility(additionIndex != -1);

    if(additionIndex == -1) {
      this.selectedAdditional = null;
    } else {
      this.selectedAdditional = this.additionManager.getAdditionals(this.characterDropdown.getSelectedOption()).get(additionIndex);
    }
  }

  private void onAddAdditionClick() {
    final List<Addition> additions = new ArrayList<>();

    for(final RegistryDelegate<Addition> addition : CHARACTER_ADDITIONS[this.characterDropdown.getSelectedIndex()]) {
      additions.add(addition.get());
    }

    this.deferAction(() -> this.getStack().pushScreen(new SelectAdditionScreen(I18n.translate("additional_additions_editor.screens.top_level.select_addition"), additions, this::onAddAdditionNameInput)));
  }

  private void onAddAdditionNameInput(final MessageBoxResult result, final Addition addition, final int additionIndex) {
    if(result == MessageBoxResult.YES) {
      final int additionFile = 4031 + this.characterDropdown.getSelectedIndex() * 8 + additionIndex;

      final Additional additional = new Additional(this.characterDropdown.getSelectedIndex(), this.characterDropdown.getSelectedOption());
      additional.name = I18n.translate(addition);
      additional.set(addition);

      for(int i = 0; i < additional.hits.size(); i++) {
        additional.hits.get(i).animationIndex = this.modelManager.getCacheIndexForAdditionPackage(additionFile, i);
      }

      this.additionManager.getAdditionals(this.characterDropdown.getSelectedOption()).add(additional);
      this.selectedAdditionDropdown.addOption(additional);
      this.selectedAdditionDropdown.setSelectedIndex(this.selectedAdditionDropdown.size() - 1);
      this.onSelectedAdditionSelected(this.selectedAdditionDropdown.getSelectedIndex());
    }
  }

  private void onRemoveAdditionClick() {
    if(this.selectedAdditional == null) {
      playMenuSound(40);
      return;
    }

    this.deferAction(() -> this.getStack().pushScreen(new MessageBoxScreen(I18n.translate("additional_additions_editor.screens.top_level.addition_remove_confirm", this.selectedAdditional.name), 2, this::onRemoveAdditionConfirm)));
  }

  private void onRemoveAdditionConfirm(final MessageBoxResult result) {
    if(result == MessageBoxResult.YES) {
      this.additionManager.getAdditionals(this.characterDropdown.getSelectedOption()).remove(this.selectedAdditional);
      this.selectedAdditionDropdown.removeOption(this.selectedAdditionDropdown.getSelectedIndex());
      this.onSelectedAdditionSelected(this.selectedAdditionDropdown.getSelectedIndex());
    }
  }

  private void onEditAdditionClick() {
    this.deferAction(() -> this.getStack().pushScreen(new EditAdditionScreen(this.additionManager, this.modelManager, this.selectedAdditional, this.characterDropdown.getSelectedIndex(), this::onEditAdditionClose)));
  }

  private void onEditAdditionClose() {

  }

  private void quit() {
    resetSubmapToNewGame();
    engineStateOnceLoaded_8004dd24 = CoreEngineStateTypes.TITLE.get();
  }

  @Override
  public int getWidth() {
    return 320;
  }

  @Override
  protected void render() {
    this.modelManager.render();
  }
}
