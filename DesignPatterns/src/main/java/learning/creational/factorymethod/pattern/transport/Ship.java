package learning.creational.factorymethod.pattern.transport;

public class Ship implements Transport {
  @Override
  public void deliver() {
    System.out.println("🚢 Delivering by sea in a ship");
  }

  @Override
  public String getType() {
    return "Ship";
  }
}
