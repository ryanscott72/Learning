package learning.creational.abstractfactory.pattern.factories;

import learning.creational.abstractfactory.pattern.family.Button;
import learning.creational.abstractfactory.pattern.family.Checkbox;
import learning.creational.abstractfactory.pattern.family.mac.MacButton;
import learning.creational.abstractfactory.pattern.family.mac.MacCheckbox;

public class MacFactoryEager implements GUIFactory {
  private static final MacFactoryEager instance = new MacFactoryEager();

  private MacFactoryEager() {}

  public static MacFactoryEager getInstance() {
    return instance;
  }

  @Override
  public Button createButton() {
    return new MacButton();
  }

  @Override
  public Checkbox createCheckbox() {
    return new MacCheckbox();
  }
}
