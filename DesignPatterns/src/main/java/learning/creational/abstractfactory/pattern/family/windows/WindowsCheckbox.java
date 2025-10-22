package learning.creational.abstractfactory.pattern.family.windows;

import learning.creational.abstractfactory.pattern.family.Checkbox;

public class WindowsCheckbox implements Checkbox {
  @Override
  public void render() {
    System.out.println("Rendering Windows-style checkbox with checkmark");
  }

  @Override
  public void toggle() {
    System.out.println("Windows checkbox toggled");
  }
}
