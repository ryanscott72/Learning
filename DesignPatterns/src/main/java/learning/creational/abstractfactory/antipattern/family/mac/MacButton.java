package learning.creational.abstractfactory.antipattern.family.mac;

import learning.creational.abstractfactory.antipattern.family.Button;

public class MacButton implements Button {
  @Override
  public void render() {
    System.out.println("Rendering Mac-style button with rounded corners");
  }

  @Override
  public void onClick() {
    System.out.println("Mac button clicked - playing Mac sound");
  }
}
