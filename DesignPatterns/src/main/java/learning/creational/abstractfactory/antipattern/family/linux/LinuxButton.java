package learning.creational.abstractfactory.antipattern.family.linux;

import learning.creational.abstractfactory.antipattern.family.Button;

public class LinuxButton implements Button {
  @Override
  public void render() {
    System.out.println("Rendering Windows-style button with square corners");
  }

  @Override
  public void onClick() {
    System.out.println("Windows button clicked - playing Windows sound");
  }
}
