package additionaladditions.screens;

import additionaladditions.Additional;
import additionaladditions.ModelManager;
import legend.game.i18n.I18n;
import legend.game.inventory.screens.FontOptions;
import legend.game.inventory.screens.HorizontalAlign;
import legend.game.inventory.screens.MenuScreen;
import legend.game.inventory.screens.MessageBoxScreen;
import legend.game.inventory.screens.TextColour;
import legend.game.inventory.screens.controls.Label;
import legend.game.inventory.screens.controls.NumberSpinner;
import legend.game.types.MessageBoxResult;

import static legend.game.Text.renderText;
import static legend.game.modding.coremod.CoreMod.INPUT_ACTION_MENU_BACK;

public class EditHitScreen extends MenuScreen {
  private static final FontOptions FONT = new FontOptions().colour(TextColour.WHITE).shadowColour(TextColour.BLACK).size(0.5f).horizontalAlign(HorizontalAlign.RIGHT);

  private final ModelManager modelManager;
  private final Additional additional;
  private final Additional.Hit hit;
  private final Additional.Hit original;
  private final int charId;

  private int tickDelay;
  private int frameIndex;

  public EditHitScreen(final ModelManager modelManager, final Additional additional, final Additional.Hit hit, final int charId) {
    this.modelManager = modelManager;
    this.additional = additional;
    this.hit = hit;
    this.original = new Additional.Hit(charId);
    this.original.set(hit);
    this.charId = charId;

    this.addHotkey(I18n.translate("additional_additions_editor.screens.edit_hit.back"), INPUT_ACTION_MENU_BACK, this::menuBack);

    final Label animationName = this.addControl(new Label(I18n.translate("additional_additions_editor.screens.edit_hit.animation")));
    animationName.setScale(0.5f);

    final NumberSpinner<Integer> animation = this.addControl(NumberSpinner.intSpinner(hit.animationIndex, 0, modelManager.getAnimationCount(charId) - 1));
    animation.setScale(0.5f);
    animation.setPos(animationName.getX() + animationName.getWidth() + 2, animationName.getY());
    animation.setSize(25, animationName.getHeight());
    animation.onChange(index -> {
      this.hit.animationIndex = index;
      this.initHit();
    });

    final Label totalFramesName = this.addControl(new Label(I18n.translate("additional_additions_editor.screens.edit_hit.total_frames")));
    totalFramesName.setScale(0.5f);
    totalFramesName.setPos(animationName.getX(), animationName.getY() + animationName.getHeight() + 2);

    final NumberSpinner<Integer> totalFrames = this.addControl(NumberSpinner.intSpinner(hit.totalFrames, 0, 999));
    totalFrames.setScale(0.5f);
    totalFrames.setPos(totalFramesName.getX() + totalFramesName.getWidth() + 2, totalFramesName.getY());
    totalFrames.setSize(25, totalFramesName.getHeight());
    totalFrames.onChange(frames -> this.hit.totalFrames = frames);

    final Label buttonDelayName = this.addControl(new Label(I18n.translate("additional_additions_editor.screens.edit_hit.button_delay")));
    buttonDelayName.setScale(0.5f);
    buttonDelayName.setPos(totalFramesName.getX(), totalFramesName.getY() + totalFramesName.getHeight() + 2);

    final NumberSpinner<Integer> buttonDelay = this.addControl(NumberSpinner.intSpinner(hit.buttonDelay, 0, 999));
    buttonDelay.setScale(0.5f);
    buttonDelay.setPos(buttonDelayName.getX() + buttonDelayName.getWidth() + 2, buttonDelayName.getY());
    buttonDelay.setSize(25, buttonDelayName.getHeight());
    buttonDelay.onChange(delay -> this.hit.buttonDelay = delay);

    final Label buttonWindowName = this.addControl(new Label(I18n.translate("additional_additions_editor.screens.edit_hit.button_window")));
    buttonWindowName.setScale(0.5f);
    buttonWindowName.setPos(buttonDelayName.getX(), buttonDelayName.getY() + buttonDelayName.getHeight() + 2);

    final NumberSpinner<Integer> successFrames = this.addControl(NumberSpinner.intSpinner(hit.buttonWindow, 0, 999));
    successFrames.setScale(0.5f);
    successFrames.setPos(buttonWindowName.getX() + buttonWindowName.getWidth() + 2, buttonWindowName.getY());
    successFrames.setSize(25, buttonWindowName.getHeight());
    successFrames.onChange(frames -> this.hit.buttonWindow = frames);

    final Label moveFramesName = this.addControl(new Label(I18n.translate("additional_additions_editor.screens.edit_hit.move_frames")));
    moveFramesName.setScale(0.5f);
    moveFramesName.setPos(buttonWindowName.getX(), buttonWindowName.getY() + buttonWindowName.getHeight() + 2);

    final NumberSpinner<Integer> moveFrames = this.addControl(NumberSpinner.intSpinner(hit.moveFrames, 0, 999));
    moveFrames.setScale(0.5f);
    moveFrames.setPos(moveFramesName.getX() + moveFramesName.getWidth() + 2, moveFramesName.getY());
    moveFrames.setSize(25, moveFramesName.getHeight());
    moveFrames.onChange(frames -> this.hit.moveFrames = frames);

    final Label knockbackName = this.addControl(new Label(I18n.translate("additional_additions_editor.screens.edit_hit.knockback")));
    knockbackName.setScale(0.5f);
    knockbackName.setPos(moveFramesName.getX(), moveFramesName.getY() + moveFramesName.getHeight() + 2);

    final NumberSpinner<Integer> knockback = this.addControl(NumberSpinner.intSpinner(hit.knockback, 0, 999));
    knockback.setScale(0.5f);
    knockback.setPos(knockbackName.getX() + knockbackName.getWidth() + 2, knockbackName.getY());
    knockback.setSize(25, knockbackName.getHeight());
    knockback.onChange(distance -> this.hit.knockback = distance);

    final Label cameraMovementXName = this.addControl(new Label(I18n.translate("additional_additions_editor.screens.edit_hit.camera_movement_x")));
    cameraMovementXName.setScale(0.5f);
    cameraMovementXName.setPos(knockbackName.getX(), knockbackName.getY() + knockbackName.getHeight() + 2);

    final NumberSpinner<Integer> cameraMovementX = this.addControl(NumberSpinner.intSpinner(hit.cameraMovementX, -999, 999));
    cameraMovementX.setScale(0.5f);
    cameraMovementX.setPos(cameraMovementXName.getX() + cameraMovementXName.getWidth() + 2, cameraMovementXName.getY());
    cameraMovementX.setSize(25, cameraMovementXName.getHeight());
    cameraMovementX.onChange(distance -> this.hit.cameraMovementX = distance);

    final Label cameraMovementZName = this.addControl(new Label(I18n.translate("additional_additions_editor.screens.edit_hit.camera_movement_z")));
    cameraMovementZName.setScale(0.5f);
    cameraMovementZName.setPos(cameraMovementXName.getX(), cameraMovementXName.getY() + cameraMovementXName.getHeight() + 2);

    final NumberSpinner<Integer> cameraMovementZ = this.addControl(NumberSpinner.intSpinner(hit.cameraMovementZ, -999, 999));
    cameraMovementZ.setScale(0.5f);
    cameraMovementZ.setPos(cameraMovementZName.getX() + cameraMovementZName.getWidth() + 2, cameraMovementZName.getY());
    cameraMovementZ.setSize(25, cameraMovementZName.getHeight());
    cameraMovementZ.onChange(distance -> this.hit.cameraMovementZ = distance);

    final Label cameraMovementTicksName = this.addControl(new Label(I18n.translate("additional_additions_editor.screens.edit_hit.camera_movement_ticks")));
    cameraMovementTicksName.setScale(0.5f);
    cameraMovementTicksName.setPos(cameraMovementZName.getX(), cameraMovementZName.getY() + cameraMovementZName.getHeight() + 2);

    final NumberSpinner<Integer> cameraMovementTicks = this.addControl(NumberSpinner.intSpinner(hit.cameraMovementTicks, 0, 999));
    cameraMovementTicks.setScale(0.5f);
    cameraMovementTicks.setPos(cameraMovementTicksName.getX() + cameraMovementTicksName.getWidth() + 2, cameraMovementTicksName.getY());
    cameraMovementTicks.setSize(25, cameraMovementTicksName.getHeight());
    cameraMovementTicks.onChange(ticks -> this.hit.cameraMovementTicks = ticks);

    final Label damageMultiplierName = this.addControl(new Label(I18n.translate("additional_additions_editor.screens.edit_hit.damage_multiplier")));
    damageMultiplierName.setScale(0.5f);
    damageMultiplierName.setPos(cameraMovementTicksName.getX(), cameraMovementTicksName.getY() + cameraMovementTicksName.getHeight() + 2);

    final NumberSpinner<Integer> damageMultiplier = this.addControl(NumberSpinner.intSpinner(hit.damageMultiplier, 0, 999));
    damageMultiplier.setScale(0.5f);
    damageMultiplier.setPos(damageMultiplierName.getX() + damageMultiplierName.getWidth() + 2, damageMultiplierName.getY());
    damageMultiplier.setSize(25, damageMultiplierName.getHeight());
    damageMultiplier.onChange(multiplier -> this.hit.damageMultiplier = multiplier);

    final Label spName = this.addControl(new Label(I18n.translate("additional_additions_editor.screens.edit_hit.sp")));
    spName.setScale(0.5f);
    spName.setPos(damageMultiplierName.getX(), damageMultiplierName.getY() + damageMultiplierName.getHeight() + 2);

    final NumberSpinner<Integer> sp = this.addControl(NumberSpinner.intSpinner(hit.sp, 0, 999));
    sp.setScale(0.5f);
    sp.setPos(spName.getX() + spName.getWidth() + 2, spName.getY());
    sp.setSize(25, spName.getHeight());
    sp.onChange(val -> this.hit.sp = val);

    this.initHit();
  }

  private void initHit() {
    this.modelManager.applyAnimation(this.charId, this.hit.animationIndex);
    this.modelManager.restartAnimation();
    this.frameIndex = 0;
  }

  @Override
  public int getWidth() {
    return 320;
  }

  @Override
  protected void render() {
    if(this.tickDelay < 2) {
      this.tickDelay++;
    } else {
      this.tickDelay = 0;

      this.frameIndex++;

      if(this.frameIndex >= this.hit.totalFrames) {
        this.modelManager.restartAnimation();
        this.frameIndex = 0;
      }
    }

    this.modelManager.render(true);

    renderText(I18n.translate("additional_additions_editor.screens.edit_hit.hit_frame_display", this.frameIndex + 1, this.hit.totalFrames), this.getWidth() - 2, 2, FONT);
    renderText(I18n.translate("additional_additions_editor.screens.edit_hit.anim_frame_display", this.modelManager.getAnimationCurrentFrame(), this.modelManager.getAnimationTotalFrames()), this.getWidth() - 2, 10, FONT);
  }

  private void menuBack() {
    this.deferAction(() -> {
      if(!this.hit.equals(this.original)) {
        this.getStack().pushScreen(new MessageBoxScreen(I18n.translate("additional_additions_editor.screens.edit_hit.do_you_want_to_save"), 2, this::onBackConfirm));
      } else {
        this.getStack().popScreen();
      }
    });
  }

  private void onBackConfirm(final MessageBoxResult result) {
    if(result == MessageBoxResult.NO) {
      this.hit.set(this.original);
    }

    if(result != MessageBoxResult.CANCEL) {
      this.deferAction(this.getStack()::popScreen);
    }
  }
}
