package learning.creational.abstractfactory.pattern.factories;

import learning.creational.abstractfactory.pattern.family.Button;
import learning.creational.abstractfactory.pattern.family.Checkbox;

public interface GUIFactory {
  Button createButton();

  Checkbox createCheckbox();
}
