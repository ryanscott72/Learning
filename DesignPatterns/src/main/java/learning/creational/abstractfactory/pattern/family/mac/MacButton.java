package learning.creational.abstractfactory.pattern.family.mac;

import learning.creational.abstractfactory.pattern.family.Button;

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
