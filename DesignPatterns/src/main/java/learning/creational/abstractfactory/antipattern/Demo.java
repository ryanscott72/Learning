package learning.creational.abstractfactory.antipattern;

public class Demo {
  public static void main(String[] args) {
    String osName = System.getProperty("os.name").toLowerCase();
    Application osApplication = new Application(osName);
    osApplication.paint();
    osApplication.interact();
  }
}
