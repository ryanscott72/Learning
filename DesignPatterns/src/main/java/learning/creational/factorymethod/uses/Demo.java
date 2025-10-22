package learning.creational.factorymethod.uses;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Demo {
  public static void main(String[] args) {
    // List.of() is a factory method
    List<String> list = List.of("a", "b", "c");

    // Set.of() is a factory method
    Set<Integer> set = Set.of(1, 2, 3);

    // Map.of() is a factory method
    Map<String, Integer> map = Map.of("one", 1, "two", 2);

    // Collections class has many factory methods
    List<String> emptyList = Collections.emptyList();
    List<String> singletonList = Collections.singletonList("item");
    List<String> unmodifiableList = Collections.unmodifiableList(list);
  }
}
