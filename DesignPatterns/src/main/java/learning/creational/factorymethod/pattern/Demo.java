package learning.creational.factorymethod.pattern;

import learning.creational.factorymethod.pattern.logistics.*;

public class Demo {
  public static void main(String[] args) {
    System.out.println("=== Factory Method Pattern Demo ===\n");

    // Example 1: Basic usage
    System.out.println("--- Example 1: Basic Factory Method ---");
    LogisticsWithFactoryMethod roadLogistics = new RoadLogistics();
    roadLogistics.planDelivery();

    LogisticsWithFactoryMethod seaLogistics = new SeaLogistics();
    seaLogistics.planDelivery();

    LogisticsWithFactoryMethod airLogistics = new AirLogistics();
    airLogistics.planDelivery();

    System.out.println("=".repeat(50) + "\n");

    // Example 2: Polymorphic usage
    System.out.println("--- Example 2: Polymorphic Array ---");
    LogisticsWithFactoryMethod[] logisticsArray = {
      new RoadLogistics(), new SeaLogistics(), new AirLogistics(), new DroneLogistics()
    };

    for (LogisticsWithFactoryMethod logistics : logisticsArray) {
      logistics.planDelivery();
    }
  }
}
