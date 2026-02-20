package additionaladditions.screens;

import additionaladditions.AdditionManager;
import additionaladditions.Additional;
import additionaladditions.ModelManager;
import legend.core.IoHelper;
import legend.game.additions.SimpleAddition;
import legend.game.i18n.I18n;
import legend.game.inventory.screens.FontOptions;
import legend.game.inventory.screens.HorizontalAlign;
import legend.game.inventory.screens.InputBoxScreen;
import legend.game.inventory.screens.MessageBoxScreen;
import legend.game.inventory.screens.TextColour;
import legend.game.inventory.screens.controls.Button;
import legend.game.inventory.screens.controls.Dropdown;
import legend.game.inventory.screens.controls.Label;
import legend.game.inventory.screens.controls.NumberSpinner;
import legend.game.inventory.screens.controls.Textbox;
import legend.game.types.MessageBoxResult;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;

import static additionaladditions.AdditionalAdditionsInputActions.SAVE_ADDITION;
import static legend.game.Text.renderText;
import static legend.game.modding.coremod.CoreMod.INPUT_ACTION_MENU_BACK;

public class EditAdditionScreen extends CameraControllableScreen {
  private static final Logger LOGGER = LogManager.getFormatterLogger(EditAdditionScreen.class);

  private static final FontOptions FONT = new FontOptions().colour(TextColour.WHITE).shadowColour(TextColour.BLACK).size(0.5f).horizontalAlign(HorizontalAlign.RIGHT);

  private final AdditionManager additionManager;
  private final ModelManager modelManager;
  private final Additional additional;
  private final Additional original;
  private final int charId;

  private final Runnable onClose;

  private final Dropdown<Integer> hit;
  private final Button editHit;

  private int tickDelay;
  private int hitIndex;
  private int frameIndex;

  public EditAdditionScreen(final AdditionManager additionManager, final ModelManager modelManager, final Additional additional, final int charId, final Runnable onClose) {
    this.additionManager = additionManager;
    this.modelManager = modelManager;
    this.additional = additional;
    this.charId = charId;
    this.original = new Additional(charId, additional.charRegId);
    this.onClose = onClose;
    this.original.set(additional);

    this.addHotkey(I18n.translate("additional_additions_editor.screens.edit_addition.back"), INPUT_ACTION_MENU_BACK, this::menuBack);
    this.addHotkey(I18n.translate("additional_additions_editor.screens.edit_addition.save"), SAVE_ADDITION, this::saveOrPromptForFilename);

    final Label nameName = this.addControl(new Label(I18n.translate("additional_additions_editor.screens.edit_addition.name")));
    nameName.setScale(0.5f);

    final Textbox name = this.addControl(new Textbox());
    name.setScale(0.5f);
    name.setPos(nameName.getX() + nameName.getWidth() + 2, nameName.getY());
    name.onChanged(text -> additional.name = text);
    name.setSize(40, nameName.getHeight());
    name.setText(additional.name);

    final Label baseDamageName = this.addControl(new Label(I18n.translate("additional_additions_editor.screens.edit_addition.base_damage")));
    baseDamageName.setScale(0.5f);
    baseDamageName.setPos(nameName.getX(), nameName.getY() + nameName.getHeight() + 2);

    final NumberSpinner<Integer> baseDamage = this.addControl(NumberSpinner.intSpinner(this.additional.baseDamage, 0, 999));
    baseDamage.setScale(0.5f);
    baseDamage.setPos(baseDamageName.getX() + baseDamageName.getWidth() + 2, baseDamageName.getY());
    baseDamage.setSize(25, baseDamageName.getHeight());
    baseDamage.onChange(damage -> this.additional.baseDamage = damage);

    final Label overlayFrameName = this.addControl(new Label(I18n.translate("additional_additions_editor.screens.edit_addition.overlay_frame")));
    overlayFrameName.setScale(0.5f);
    overlayFrameName.setPos(baseDamageName.getX(), baseDamageName.getY() + baseDamageName.getHeight() + 2);

    final NumberSpinner<Integer> overlayFrame = this.addControl(NumberSpinner.intSpinner(this.additional.overlayFrame, 0, 999));
    overlayFrame.setScale(0.5f);
    overlayFrame.setPos(overlayFrameName.getX() + overlayFrameName.getWidth() + 2, overlayFrameName.getY());
    overlayFrame.setSize(25, overlayFrameName.getHeight());
    overlayFrame.onChange(frame -> this.additional.overlayFrame = frame);

    final Label hitName = this.addControl(new Label(I18n.translate("additional_additions_editor.screens.edit_addition.hits")));
    hitName.setScale(0.5f);
    hitName.setPos(overlayFrameName.getX(), overlayFrameName.getY() + overlayFrameName.getHeight() + 2);

    this.hit = this.addControl(new Dropdown<>((i, e) -> Integer.toString(i + 1)));
    this.hit.setScale(0.5f);
    this.hit.setPos(hitName.getX() + hitName.getWidth() + 2, hitName.getY());
    this.hit.setSize(40, hitName.getHeight());
    this.hit.onSelection(this::onHitSelected);

    for(int i = 0; i < this.additional.hits.size(); i++) {
      this.hit.addOption(i);
    }

    this.hit.setSelectedIndex(0);

    final Button addHit = this.addControl(new Button(I18n.translate("additional_additions_editor.screens.edit_addition.add_hit")));
    addHit.setScale(0.5f);
    addHit.setY(this.hit.getY() + this.hit.getHeight() + 2);
    addHit.setSize(25, this.hit.getHeight());
    addHit.onPressed(this::onAddHitClick);

    final Button removeHit = this.addControl(new Button(I18n.translate("additional_additions_editor.screens.edit_addition.remove_hit")));
    removeHit.setScale(0.5f);
    removeHit.setPos(addHit.getX() + addHit.getWidth() + 2, addHit.getY());
    removeHit.setSize(25, this.hit.getHeight());
    removeHit.onPressed(this::onRemoveHitClick);

    this.editHit = this.addControl(new Button(I18n.translate("additional_additions_editor.screens.edit_addition.edit_hit")));
    this.editHit.setScale(0.5f);
    this.editHit.setPos(removeHit.getX() + removeHit.getWidth() + 2, removeHit.getY());
    this.editHit.setSize(25, this.hit.getHeight());
    this.editHit.onPressed(this::onEditHitClick);

    final Label damageSpName = this.addControl(new Label(I18n.translate("additional_additions_editor.screens.edit_addition.damage_sp")));
    damageSpName.setScale(0.5f);
    damageSpName.setPos(addHit.getX(), addHit.getY() + addHit.getHeight() + 6);

    for(int i = 0; i < this.additional.levelMultipliers.size(); i++) {
      final int finalI = i;

      final Label damageSpLvl = this.addControl(new Label(I18n.translate("additional_additions_editor.screens.edit_addition.damage_sp_level", i + 1)));
      damageSpLvl.setScale(0.5f);
      damageSpLvl.setPos(damageSpName.getX(), damageSpName.getY() + (damageSpName.getHeight() + 2) * (i + 1));

      final SimpleAddition.LevelMultipliers multipliers = this.additional.levelMultipliers.get(i);

      final NumberSpinner<Float> damage = this.addControl(NumberSpinner.floatSpinner(multipliers.damage, 0.05f, 0.5f, 0.0f, 1000.0f));
      damage.setScale(0.5f);
      damage.setPos(damageSpLvl.getX() + damageSpLvl.getWidth() + 2, damageSpLvl.getY());
      damage.setSize(25, damageSpLvl.getHeight());
      damage.onChange(val -> this.additional.levelMultipliers.set(finalI, new SimpleAddition.LevelMultipliers(val, multipliers.sp)));

      final NumberSpinner<Float> sp = this.addControl(NumberSpinner.floatSpinner(multipliers.sp, 0.05f, 0.5f, 0.0f, 1000.0f));
      sp.setScale(0.5f);
      sp.setPos(damage.getX() + damage.getWidth() + 2, damage.getY());
      sp.setSize(25, damage.getHeight());
      sp.onChange(val -> this.additional.levelMultipliers.set(finalI, new SimpleAddition.LevelMultipliers(multipliers.damage, val)));
    }

    this.onHitSelected(this.hit.getSelectedIndex());

    final Additional.Hit hit = this.additional.hits.get(this.hitIndex);
    this.modelManager.applyAnimation(this.charId, hit.animationIndex, hit.interpolationScale / 100.0f);
  }

  private void onHitSelected(final int index) {
    this.editHit.setVisibility(index != -1);
  }

  private void onAddHitClick() {
    final Additional.Hit hit = new Additional.Hit(this.charId);
    this.additional.hits.add(hit);
    this.hit.addOption(this.hit.size());
    this.hit.setSelectedIndex(this.hit.size() - 1);
    this.onHitSelected(this.hit.getSelectedIndex());
  }

  private void onRemoveHitClick() {
    this.deferAction(() -> this.getStack().pushScreen(new MessageBoxScreen(I18n.translate("additional_additions_editor.screens.edit_addition.hit_remove_confirm", this.hit.getSelectedIndex() + 1), 2, this::onRemoveHitConfirm)));
  }

  private void onRemoveHitConfirm(final MessageBoxResult result) {
    if(result == MessageBoxResult.YES) {
      this.additional.hits.remove(this.hit.getSelectedIndex());
      this.hit.removeOption(this.hit.getSelectedIndex());
      this.onHitSelected(this.hit.getSelectedIndex());
      this.tickDelay = 0;
      this.frameIndex = 0;
      this.hitIndex = 0;
    }
  }

  private void onEditHitClick() {
    this.deferAction(() -> this.getStack().pushScreen(new EditHitScreen(this.modelManager, this.additional, this.additional.hits.get(this.hit.getSelectedIndex()), this.charId)));
  }

  @Override
  public int getWidth() {
    return 320;
  }

  @Override
  protected void render() {
    if(this.additional.hits.isEmpty()) {
      return;
    }

    if(this.tickDelay < 2) {
      this.tickDelay++;
    } else {
      this.tickDelay = 0;

      this.frameIndex++;

      if(this.frameIndex >= this.additional.hits.get(this.hitIndex).totalFrames) {
        this.hitIndex++;

        if(this.hitIndex >= this.additional.hits.size()) {
          this.hitIndex = 0;
        }

        final Additional.Hit hit = this.additional.hits.get(this.hitIndex);

        this.modelManager.applyAnimation(this.charId, hit.animationIndex, hit.interpolationScale / 100.0f);
        this.frameIndex = 0;
      }
    }

    this.modelManager.render();

    renderText(I18n.translate("additional_additions_editor.screens.edit_addition.hit_display", this.hitIndex + 1, this.additional.hits.size()), this.getWidth() - 2, 2, FONT);
    renderText(I18n.translate("additional_additions_editor.screens.edit_addition.hit_frame_display", this.frameIndex + 1, this.additional.hits.get(this.hitIndex).totalFrames), this.getWidth() - 2, 10, FONT);
    renderText(I18n.translate("additional_additions_editor.screens.edit_addition.anim_frame_display", this.modelManager.getAnimationCurrentFrame(), this.modelManager.getAnimationTotalFrames()), this.getWidth() - 2, 18, FONT);
  }

  private void menuBack() {
    if(!this.additional.equals(this.original)) {
      this.deferAction(() -> this.getStack().pushScreen(new MessageBoxScreen(I18n.translate("additional_additions_editor.screens.edit_addition.do_you_want_to_save"), 2, this::onBackConfirm)));
    } else {
      this.deferAction(() -> this.getStack().popScreen());
      this.onClose.run();
    }
  }

  private void onBackConfirm(final MessageBoxResult result) {
    if(result == MessageBoxResult.YES) {
      if(this.additional.filename == null) {
        this.deferAction(() -> this.getStack().pushScreen(new InputBoxScreen(I18n.translate("additional_additions_editor.screens.edit_addition.save_name"), IoHelper.slugName(this.additional.name), this::onSaveNameConfirm)));
      } else {
        this.save();
        this.deferAction(this.getStack()::popScreen);
        this.onClose.run();
      }

      return;
    }

    if(result == MessageBoxResult.NO) {
      this.additional.set(this.original);
      this.deferAction(this.getStack()::popScreen);
      this.onClose.run();
    }
  }

  private void onSaveNameConfirm(final MessageBoxResult result, final String name) {
    if(result == MessageBoxResult.YES) {
      this.additional.filename = name;
      this.save();
      this.deferAction(this.getStack()::popScreen);
      this.onClose.run();
    }
  }

  private void saveOrPromptForFilename() {
    if(this.additional.filename == null) {
      this.deferAction(() -> this.getStack().pushScreen(new InputBoxScreen(I18n.translate("additional_additions_editor.screens.edit_addition.save_name"), IoHelper.slugName(this.additional.name), this::onSaveNameConfirm)));
    } else {
      this.save();
    }
  }

  private void save() {
    try {
      this.additionManager.save(this.modelManager, this.additional);
      this.original.set(this.additional);
    } catch(final IOException e) {
      LOGGER.error("Failed to save", e);
      this.deferAction(() -> this.getStack().pushScreen(new MessageBoxScreen(I18n.translate("additional_additions_editor.screens.edit_addition.save_failed"), 0, result -> {})));
    }
  }
}
