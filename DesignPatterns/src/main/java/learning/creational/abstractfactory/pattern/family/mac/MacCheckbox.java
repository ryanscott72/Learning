package learning.creational.abstractfactory.pattern.family.mac;

import learning.creational.abstractfactory.pattern.family.Checkbox;

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
