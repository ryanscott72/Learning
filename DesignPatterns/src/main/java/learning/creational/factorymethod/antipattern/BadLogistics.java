package learning.creational.factorymethod.antipattern;

public class BadLogistics {
  private String type;

  public BadLogistics(String type) {
    this.type = type;
  }

  // Violates Open/Closed Principle
  public void planDelivery() {
    if (type.equals("truck")) {
      System.out.println("🚚 Delivering by truck");
    } else if (type.equals("ship")) {
      System.out.println("🚢 Delivering by ship");
    } else if (type.equals("airplane")) {
      System.out.println("✈️  Delivering by airplane");
    } else if (type.equals("drone")) {
      // Need to modify this class every time!
      System.out.println("🚁 Delivering by drone");
    }
  }
}
