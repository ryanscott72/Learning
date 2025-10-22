package learning.creational.factorymethod.pattern.logistics;

import learning.creational.factorymethod.pattern.transport.Airplane;
import learning.creational.factorymethod.pattern.transport.Transport;

public class AirLogistics extends LogisticsWithFactoryMethod {
  @Override
  public Transport createTransport() {
    return new Airplane();
  }
}
