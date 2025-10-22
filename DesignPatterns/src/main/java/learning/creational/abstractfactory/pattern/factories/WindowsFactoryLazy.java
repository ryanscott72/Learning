package learning.creational.abstractfactory.pattern.factories;

import learning.creational.abstractfactory.pattern.family.Button;
import learning.creational.abstractfactory.pattern.family.Checkbox;
import learning.creational.abstractfactory.pattern.family.windows.WindowsButton;
import learning.creational.abstractfactory.pattern.family.windows.WindowsCheckbox;

public class WindowsFactoryLazy implements GUIFactory {
  private static WindowsFactoryLazy instance;

  private WindowsFactoryLazy() {}

  // Thread-safe lazy initialization
  public static synchronized WindowsFactoryLazy getInstance() {
    if (instance == null) {
      instance = new WindowsFactoryLazy();
    }
    return instance;
  }

  @Override
  public Button createButton() {
    return new WindowsButton();
  }

  @Override
  public Checkbox createCheckbox() {
    return new WindowsCheckbox();
  }
}
