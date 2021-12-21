package nemben.aoc.d17;

import com.google.auto.value.AutoValue;
import nemben.aoc.InputReader;
import nemben.aoc.Point;

import java.util.Optional;

public class TrickShot {
  @AutoValue
  abstract static class Box {
    abstract Point min();

    abstract Point max();

    boolean contains(int x, int y) {
      return min().x() <= x && x <= max().x() && min().y() <= y && y <= max().y();
    }

    static Box of(Point min, Point max) {
      return new AutoValue_TrickShot_Box(min, max);
    }

    static Box boundingBox(Point p1, Point p2) {
      return Box.of(
          Point.of(Math.min(p1.x(), p2.x()), Math.min(p1.y(), p2.y())),
          Point.of(Math.max(p1.x(), p2.x()), Math.max(p1.y(), p2.y())));
    }
  }

  private static Box readInput(String name) {
    return InputReader.readInput(
        name,
        scn -> {
          String[] parts = scn.nextLine().substring("target area: ".length()).split(",\\s");
          String[] xParts = parts[0].substring("x=".length()).split("\\.\\.");
          String[] yParts = parts[1].substring("y=".length()).split("\\.\\.");

          Point p1 = Point.of(Integer.parseInt(xParts[0]), Integer.parseInt(yParts[0]));
          Point p2 = Point.of(Integer.parseInt(xParts[1]), Integer.parseInt(yParts[1]));

          return Box.boundingBox(p1, p2);
        });
  }

  private static Optional<Integer> simulate(Box target, int vX, int vY) {
    int x = 0, y = 0;
    int maxY = 0;
    boolean hit = false;
    while (vX != 0 || !(vY < 0 && y < target.max().y())) {
      x += vX;
      y += vY;

      if (y > maxY) {
        maxY = y;
      }
      if (target.contains(x, y)) {
        hit = true;
      }

      vX += (vX > 0) ? -1 : (vX < 0) ? 1 : 0;
      vY -= 1;
    }
    return hit ? Optional.of(maxY) : Optional.empty();
  }

  private static int part1(Box target) {
    int bX = Math.max(Math.abs(target.min().x()), Math.abs(target.max().x()));
    int bY = Math.max(Math.abs(target.min().y()), Math.abs(target.max().y()));

    int maxY = 0;
    for (int vX = -bX; vX <= bX; ++vX) {
      for (int vY = 0; vY <= bY; ++vY) {
        Optional<Integer> maybeMaxY = simulate(target, vX, vY);
        if (maybeMaxY.isPresent() && maybeMaxY.get() > maxY) {
          maxY = maybeMaxY.get();
        }
      }
    }
    return maxY;
  }

  private static int part2(Box target) {
    int bX = Math.max(Math.abs(target.min().x()), Math.abs(target.max().x()));
    int bY = Math.max(Math.abs(target.min().y()), Math.abs(target.max().y()));

    int cnt = 0;
    for (int vX = -bX; vX <= bX; ++vX) {
      for (int vY = -bY; vY <= bY; ++vY) {
        Optional<Integer> maybeMaxY = simulate(target, vX, vY);
        if (maybeMaxY.isPresent()) {
          cnt += 1;
        }
      }
    }
    return cnt;
  }

  public static void main(String[] args) {
    Box target = readInput(args[0]);
    System.out.println(part1(target));
    System.out.println(part2(target));
  }
}
