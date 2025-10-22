package learning.creational.abstractfactory.pattern.demo;

import learning.creational.abstractfactory.pattern.factories.GUIFactory;
import learning.creational.abstractfactory.pattern.family.Button;
import learning.creational.abstractfactory.pattern.family.Checkbox;

public class Application {
  private Button button;
  private Checkbox checkbox;

  public Application(GUIFactory factory) {
    button = factory.createButton();
    checkbox = factory.createCheckbox();
  }

  public void paint() {
    button.render();
    checkbox.render();
  }

  public void interact() {
    button.onClick();
    checkbox.toggle();
  }
}
