package learning.creational.factorymethod.pattern.transport;

public class Airplane implements Transport {
  @Override
  public void deliver() {
    System.out.println("✈️  Delivering by air in an airplane");
  }

  @Override
  public String getType() {
    return "Airplane";
  }
}
