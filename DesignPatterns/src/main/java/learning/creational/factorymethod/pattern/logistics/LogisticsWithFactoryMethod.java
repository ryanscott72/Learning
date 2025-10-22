package learning.creational.factorymethod.pattern.logistics;

import learning.creational.factorymethod.pattern.transport.Transport;

public abstract class LogisticsWithFactoryMethod {
  // Factory Method - subclasses will override this
  public abstract Transport createTransport();

  // Business logic that uses the factory method
  public void planDelivery() {
    Transport transport = createTransport();
    System.out.println("Planning delivery using: " + transport.getType());
    transport.deliver();
    System.out.println("Delivery completed!\n");
  }
}
