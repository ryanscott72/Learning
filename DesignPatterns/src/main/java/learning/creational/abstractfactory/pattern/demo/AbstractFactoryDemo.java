package learning.creational.abstractfactory.pattern.demo;

import learning.creational.abstractfactory.pattern.factories.GUIFactory;
import learning.creational.abstractfactory.pattern.factories.LinuxFactoryBillPughSingleton;
import learning.creational.abstractfactory.pattern.factories.MacFactoryEager;
import learning.creational.abstractfactory.pattern.factories.WindowsFactoryLazy;

public class AbstractFactoryDemo {
  public static void main(String[] args) {
    System.out.println("=== Abstract Factory Pattern Demo ===\n");

    // Example 1: Auto-detect OS
    System.out.println("--- Auto-detecting OS ---");
    Application app = ApplicationConfigurator.configureApplication();
    app.paint();
    System.out.println();
    app.interact();

    System.out.println("\n" + "=".repeat(50) + "\n");

    // Example 2: Manually create Windows app
    System.out.println("--- Manually creating Windows application ---");
    GUIFactory windowsFactory = WindowsFactoryLazy.getInstance();
    Application windowsApp = new Application(windowsFactory);
    windowsApp.paint();
    System.out.println();
    windowsApp.interact();

    System.out.println("\n" + "=".repeat(50) + "\n");

    // Example 3: Manually create Mac app
    System.out.println("--- Manually creating Mac application ---");
    GUIFactory macFactory = MacFactoryEager.getInstance();
    Application macApp = new Application(macFactory);
    macApp.paint();
    System.out.println();
    macApp.interact();

    System.out.println("\n" + "=".repeat(50) + "\n");

    // Example 4: Demonstrate Singleton behavior
    demonstrateSingletonBehavior();

    System.out.println("\n" + "=".repeat(50) + "\n");

    // Demonstrate the key benefit
    demonstrateKeyBenefit();
  }

  private static void demonstrateKeyBenefit() {
    System.out.println("=== KEY BENEFIT: Easy to switch entire product families ===\n");

    GUIFactory[] factories = {
      WindowsFactoryLazy.getInstance(),
      MacFactoryEager.getInstance(),
      LinuxFactoryBillPughSingleton.getInstance()
    };

    String[] names = {"Windows", "Mac", "Linux"};

    for (int i = 0; i < factories.length; i++) {
      System.out.println("Creating " + names[i] + " UI:");
      Application app = new Application(factories[i]);
      app.paint();
      System.out.println();
    }

    System.out.println("Notice: Client code doesn't change!");
    System.out.println("We just swap the factory to get a completely different UI family.");
  }

  private static void demonstrateSingletonBehavior() {
    System.out.println("=== DEMONSTRATING SINGLETON BEHAVIOR ===\n");

    // Get multiple references to the same factory
    WindowsFactoryLazy windowsFactory1 = WindowsFactoryLazy.getInstance();
    WindowsFactoryLazy windowsFactory2 = WindowsFactoryLazy.getInstance();
    MacFactoryEager macFactory1 = MacFactoryEager.getInstance();
    MacFactoryEager macFactory2 = MacFactoryEager.getInstance();
    LinuxFactoryBillPughSingleton linuxFactory1 = LinuxFactoryBillPughSingleton.getInstance();
    LinuxFactoryBillPughSingleton linuxFactory2 = LinuxFactoryBillPughSingleton.getInstance();

    // Check if they're the same instance
    System.out.println("WindowsFactory - Same instance? " + (windowsFactory1 == windowsFactory2));
    System.out.println("  windowsFactory1 hashCode: " + System.identityHashCode(windowsFactory1));
    System.out.println("  windowsFactory2 hashCode: " + System.identityHashCode(windowsFactory2));
    System.out.println();

    System.out.println("MacFactory - Same instance? " + (macFactory1 == macFactory2));
    System.out.println("  macFactory1 hashCode: " + System.identityHashCode(macFactory1));
    System.out.println("  macFactory2 hashCode: " + System.identityHashCode(macFactory2));
    System.out.println();

    System.out.println("LinuxFactory - Same instance? " + (linuxFactory1 == linuxFactory2));
    System.out.println("  linuxFactory1 hashCode: " + System.identityHashCode(linuxFactory1));
    System.out.println("  linuxFactory2 hashCode: " + System.identityHashCode(linuxFactory2));
    System.out.println();

    System.out.println("Benefits:");
    System.out.println("  ✓ Only ONE instance of each factory exists");
    System.out.println("  ✓ Memory efficient (stateless factories don't need multiple instances)");
    System.out.println("  ✓ Global access point via getInstance()");
  }
}
