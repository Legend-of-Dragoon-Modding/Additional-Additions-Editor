package additionaladditions.screens;

import legend.core.memory.types.TriConsumer;
import legend.core.platform.input.InputAction;
import legend.game.additions.Addition;
import legend.game.i18n.I18n;
import legend.game.inventory.screens.Control;
import legend.game.inventory.screens.HorizontalAlign;
import legend.game.inventory.screens.InputPropagation;
import legend.game.inventory.screens.MenuScreen;
import legend.game.inventory.screens.controls.Brackets;
import legend.game.inventory.screens.controls.Button;
import legend.game.inventory.screens.controls.Dropdown;
import legend.game.inventory.screens.controls.Label;
import legend.game.inventory.screens.controls.Panel;
import legend.game.types.MessageBoxResult;

import java.util.List;

import static legend.game.modding.coremod.CoreMod.INPUT_ACTION_MENU_BACK;
import static legend.game.modding.coremod.CoreMod.INPUT_ACTION_MENU_CONFIRM;
import static legend.game.modding.coremod.CoreMod.INPUT_ACTION_MENU_DOWN;
import static legend.game.modding.coremod.CoreMod.INPUT_ACTION_MENU_UP;
import static legend.game.sound.Audio.playMenuSound;

public class SelectAdditionScreen extends MenuScreen {
  private final TriConsumer<MessageBoxResult, Addition, Integer> onResult;

  private final Brackets highlight;
  protected final Dropdown<Addition> text;
  protected final Button accept;
  protected final Button cancel;
  private int selectedIndex;

  public SelectAdditionScreen(final String message, final List<Addition> options, final TriConsumer<MessageBoxResult, Addition, Integer> onResult) {
    this(message, options, onResult, 32);
  }

  public SelectAdditionScreen(final String message, final List<Addition> options, final TriConsumer<MessageBoxResult, Addition, Integer> onResult, final int z) {
    final Panel panel = this.addControl(Panel.panel());
    panel.setPos(50, 75);
    panel.setSize(215, 90);
    panel.setZ(z);

    this.onResult = onResult;

    final Label label = panel.addControl(new Label(message));
    label.setAutoSize(true);
    label.getFontOptions().horizontalAlign(HorizontalAlign.CENTRE);
    label.setPos((panel.getWidth() - label.getWidth()) / 2, 12);
    label.setZ(z - 1);

    this.text = panel.addControl(new Dropdown<>((i, addition) -> I18n.translate(addition)));
    this.text.setSize(165, 16);
    this.text.setPos(25, 28);
    this.text.setZ(z - 1);

    for(final Addition option : options) {
      this.text.addOption(option);
    }

    this.accept = panel.addControl(new Button("Accept"));
    this.accept.setSize(112, 14);
    this.accept.setPos((panel.getWidth() - this.accept.getWidth()) / 2, this.text.getY() + this.text.getHeight() + 4);
    this.accept.setZ(z - 1);
    this.accept.onPressed(() -> {
      this.getStack().popScreen();
      this.onResult.accept(MessageBoxResult.YES, this.text.getSelectedOption(), this.text.getSelectedIndex());
    });

    this.cancel = panel.addControl(new Button("Cancel"));
    this.cancel.setSize(112, 14);
    this.cancel.setPos((panel.getWidth() - this.cancel.getWidth()) / 2, this.accept.getY() + this.accept.getHeight() + 2);
    this.cancel.setZ(z - 1);
    this.cancel.onPressed(this::menuCancel);

    this.highlight = panel.addControl(new Brackets());
    this.highlight.setPos(this.text.getX() - 4, this.text.getY() - 4);
    this.highlight.setSize(this.text.getWidth() + 8, this.text.getHeight() + 8);
    this.highlight.setClut(0xfc29);
    this.highlight.setZ(z - 1);
    this.highlight.setVisibility(false);

    this.text.onHoverIn(this.highlight::show);
    this.text.onHoverOut(this.highlight::hide);

    this.selectedIndex = 1;
    this.getSelectedControl().hoverIn();
  }

  private Control getSelectedControl() {
    return switch(this.selectedIndex) {
      case 0 -> this.text;
      case 1 -> this.accept;
      case 2 -> this.cancel;
      default -> throw new IllegalStateException("Invalid control index " + this.selectedIndex);
    };
  }

  @Override
  protected void render() {

  }

  protected void menuNavigateUp() {
    playMenuSound(1);

    this.getSelectedControl().hoverOut();

    this.selectedIndex--;
    this.selectedIndex = Math.floorMod(this.selectedIndex, 3);

    this.highlight.setVisibility(this.selectedIndex == 0);

    if(this.selectedIndex != 0) {
      this.getSelectedControl().hoverIn();
    }
  }

  protected void menuNavigateDown() {
    playMenuSound(1);

    this.getSelectedControl().hoverOut();

    this.selectedIndex++;
    this.selectedIndex %= 3;

    this.highlight.setVisibility(this.selectedIndex == 0);

    if(this.selectedIndex != 0) {
      this.getSelectedControl().hoverIn();
    }
  }

  private void menuSelect() {
    if(this.selectedIndex == 0) {
      this.deferAction(this.text::focus);
    } else {
      this.deferAction(((Button)this.getSelectedControl())::press);
    }
  }

  private void menuCancel() {
    playMenuSound(3);

    this.getStack().popScreen();
    this.onResult.accept(MessageBoxResult.CANCEL, this.text.getSelectedOption(), this.text.getSelectedIndex());
  }

  @Override
  protected boolean propagateRender() {
    return true;
  }

  @Override
  protected InputPropagation inputActionPressed(final InputAction action, final boolean repeat) {
    if(super.inputActionPressed(action, repeat) == InputPropagation.HANDLED) {
      return InputPropagation.HANDLED;
    }

    if(action == INPUT_ACTION_MENU_UP.get()) {
      this.menuNavigateUp();
      return InputPropagation.HANDLED;
    }

    if(action == INPUT_ACTION_MENU_DOWN.get()) {
      this.menuNavigateDown();
      return InputPropagation.HANDLED;
    }

    if(action == INPUT_ACTION_MENU_CONFIRM.get() && !repeat) {
      this.menuSelect();
      return InputPropagation.HANDLED;
    }

    if(action == INPUT_ACTION_MENU_BACK.get() && !repeat) {
      this.menuCancel();
      return InputPropagation.HANDLED;
    }

    return InputPropagation.PROPAGATE;
  }
}
