package learning.creational.abstractfactory.antipattern;

import learning.creational.abstractfactory.pattern.family.Button;
import learning.creational.abstractfactory.pattern.family.Checkbox;
import learning.creational.abstractfactory.pattern.family.linux.LinuxButton;
import learning.creational.abstractfactory.pattern.family.linux.LinuxCheckbox;
import learning.creational.abstractfactory.pattern.family.mac.MacButton;
import learning.creational.abstractfactory.pattern.family.mac.MacCheckbox;
import learning.creational.abstractfactory.pattern.family.windows.WindowsButton;
import learning.creational.abstractfactory.pattern.family.windows.WindowsCheckbox;

public class Application {
  private Button button;
  private Checkbox checkbox;

  public Application(String osName) {
    if (osName.contains("mac")) {
      button = new MacButton();
      checkbox = new MacCheckbox();
    } else if (osName.contains("win")) {
      button = new WindowsButton();
      checkbox = new WindowsCheckbox();
    } else {
      // Assume it is Linux
      button = new LinuxButton();
      checkbox = new LinuxCheckbox();

      // And on and on and on
    }
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
