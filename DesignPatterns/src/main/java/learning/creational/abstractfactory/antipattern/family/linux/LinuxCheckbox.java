package learning.creational.abstractfactory.antipattern.family.linux;

import learning.creational.abstractfactory.antipattern.family.Checkbox;

public class LinuxCheckbox implements Checkbox {
  @Override
  public void render() {
    System.out.println("Rendering Windows-style checkbox with checkmark");
  }

  @Override
  public void toggle() {
    System.out.println("Windows checkbox toggled");
  }
}
