package learning.creational.abstractfactory.pattern.family.windows;

import learning.creational.abstractfactory.pattern.family.Button;

public class WindowsButton implements Button {
  @Override
  public void render() {
    System.out.println("Rendering Windows-style button with square corners");
  }

  @Override
  public void onClick() {
    System.out.println("Windows button clicked - playing Windows sound");
  }
}
