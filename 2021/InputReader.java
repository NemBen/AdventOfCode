package nemben.aoc;

import com.google.auto.value.AutoValue;
import com.google.common.collect.ImmutableList;

import java.io.InputStream;
import java.util.Scanner;
import java.util.function.Function;

public class InputReader {
  public static <T> T readInput(String name, Function<Scanner, T> readFn) {
    try (Scanner scn = new Scanner(getInputStream(name))) {
      return readFn.apply(scn);
    }
  }

  public static <T> ImmutableList<T> readSimpleInput(String name, Function<Scanner, T> readFn) {
    return readInput(
        name,
        scn -> {
          ImmutableList.Builder<T> builder = new ImmutableList.Builder<>();
          while (scn.hasNext()) {
            builder.add(readFn.apply(scn));
          }
          return builder.build();
        });
  }

  @AutoValue
  public abstract static class InputPair<T, U> {
    public abstract T first();

    public abstract U second();

    public static <T, U> InputPair<T, U> of(T first, U second) {
      return new AutoValue_InputReader_InputPair(first, second);
    }
  }

  public static <T, U> InputPair<T, U> readInputPair(
      String name, Function<Scanner, T> readFirstFn, Function<Scanner, U> readSecondFn) {
    return readInput(name, scn -> InputPair.of(readFirstFn.apply(scn), readSecondFn.apply(scn)));
  }

  private static InputStream getInputStream(String name) {
    return InputReader.class.getClassLoader().getResourceAsStream(name);
  }
}
