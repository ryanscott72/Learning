package learning.creational.factorymethod.pattern.logistics;

import learning.creational.factorymethod.pattern.transport.Drone;
import learning.creational.factorymethod.pattern.transport.Transport;

public class DroneLogistics extends LogisticsWithFactoryMethod {
  @Override
  public Transport createTransport() {
    return new Drone();
  }
}
