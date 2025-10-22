package learning.creational.abstractfactory.antipattern.family.mac;

import learning.creational.abstractfactory.antipattern.family.Checkbox;

public class MacCheckbox implements Checkbox {
  @Override
  public void render() {
    System.out.println("Rendering Mac-style checkbox with smooth animation");
  }

  @Override
  public void toggle() {
    System.out.println("Mac checkbox toggled with fade effect");
  }
}
