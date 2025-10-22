package learning.creational.factorymethod.pattern.transport;

public class Truck implements Transport {
  @Override
  public void deliver() {
    System.out.println("🚚 Delivering by land in a truck");
  }

  @Override
  public String getType() {
    return "Truck";
  }
}
