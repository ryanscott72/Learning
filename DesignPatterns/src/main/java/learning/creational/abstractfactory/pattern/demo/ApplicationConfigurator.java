package learning.creational.abstractfactory.pattern.demo;

import learning.creational.abstractfactory.pattern.factories.GUIFactory;
import learning.creational.abstractfactory.pattern.factories.MacFactoryEager;
import learning.creational.abstractfactory.pattern.factories.WindowsFactoryLazy;

public class ApplicationConfigurator {
  public static Application configureApplication() {
    GUIFactory factory;
    String osName = System.getProperty("os.name").toLowerCase();

    if (osName.contains("mac")) {
      factory = MacFactoryEager.getInstance(); // Use Singleton
      System.out.println("Detected Mac OS - using Mac factory (Singleton)\n");
    } else if (osName.contains("win")) {
      factory = WindowsFactoryLazy.getInstance(); // Use Singleton
      System.out.println("Detected Windows OS - using Windows factory (Singleton)\n");
    } else {
      // Default to Windows
      factory = WindowsFactoryLazy.getInstance(); // Use Singleton
      System.out.println("Unknown OS - defaulting to Windows factory (Singleton)\n");
    }

    return new Application(factory);
  }
}
