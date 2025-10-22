package learning.creational.abstractfactory.antipattern.family.windows;

import learning.creational.abstractfactory.antipattern.family.Checkbox;

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
