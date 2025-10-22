package learning.creational.factorymethod.pattern.logistics;

import learning.creational.factorymethod.pattern.transport.Ship;
import learning.creational.factorymethod.pattern.transport.Transport;

public class SeaLogistics extends LogisticsWithFactoryMethod {
  @Override
  public Transport createTransport() {
    return new Ship();
  }
}
