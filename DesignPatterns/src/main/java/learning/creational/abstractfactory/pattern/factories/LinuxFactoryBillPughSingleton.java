package learning.creational.abstractfactory.pattern.factories;

import learning.creational.abstractfactory.pattern.family.Button;
import learning.creational.abstractfactory.pattern.family.Checkbox;
import learning.creational.abstractfactory.pattern.family.linux.LinuxButton;
import learning.creational.abstractfactory.pattern.family.linux.LinuxCheckbox;

public class LinuxFactoryBillPughSingleton implements GUIFactory {
  private LinuxFactoryBillPughSingleton() {}

  public static LinuxFactoryBillPughSingleton getInstance() {
    return SingletonHelper.INSTANCE;
  }

  @Override
  public Button createButton() {
    return new LinuxButton();
  }

  @Override
  public Checkbox createCheckbox() {
    return new LinuxCheckbox();
  }

  // Inner static helper class
  private static class SingletonHelper {
    private static final LinuxFactoryBillPughSingleton INSTANCE =
        new LinuxFactoryBillPughSingleton();
  }
}
