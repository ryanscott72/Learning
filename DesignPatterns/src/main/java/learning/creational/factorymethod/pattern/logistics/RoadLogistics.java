package learning.creational.factorymethod.pattern.logistics;

import learning.creational.factorymethod.pattern.transport.Transport;
import learning.creational.factorymethod.pattern.transport.Truck;

public class RoadLogistics extends LogisticsWithFactoryMethod {
  @Override
  public Transport createTransport() {
    return new Truck();
  }
}
