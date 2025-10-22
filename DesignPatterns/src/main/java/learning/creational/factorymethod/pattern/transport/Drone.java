package learning.creational.factorymethod.pattern.transport;

public class Drone implements Transport {
  @Override
  public void deliver() {
    System.out.println("🚁 Delivering by drone");
  }

  @Override
  public String getType() {
    return "Drone";
  }
}
