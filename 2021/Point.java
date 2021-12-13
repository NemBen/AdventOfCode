package nemben.aoc;

import com.google.auto.value.AutoValue;

@AutoValue
public abstract class Point {
  public abstract int x();

  public abstract int y();

  public static Point of(int x, int y) {
    return new AutoValue_Point(x, y);
  }

  public static Point parse(String value) {
    String[] parts = value.split(",");
    return Point.of(Integer.parseInt(parts[0]), Integer.parseInt(parts[1]));
  }
}
