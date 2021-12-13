package nemben.aoc.d13;

import com.google.auto.value.AutoValue;
import com.google.common.collect.ImmutableList;
import nemben.aoc.InputReader;
import nemben.aoc.InputReader.InputPair;
import nemben.aoc.Point;

import static com.google.common.collect.ImmutableList.toImmutableList;

public class TransparentOrigami {
  @AutoValue
  abstract static class Fold {
    enum Axis {
      X,
      Y;
    }

    abstract Axis axis();

    abstract int value();

    Point apply(Point p) {
      switch (axis()) {
        case X:
          return p.x() > value() ? Point.of(2 * value() - p.x(), p.y()) : p;
        case Y:
          return p.y() > value() ? Point.of(p.x(), 2 * value() - p.y()) : p;
        default:
          throw new AssertionError();
      }
    }

    static Fold of(Axis axis, int value) {
      return new AutoValue_TransparentOrigami_Fold(axis, value);
    }

    static Fold parse(String instruction) {
      String[] parts = instruction.substring(FOLD_INSTRUCTION_PREFIX.length()).split("=");
      return Fold.of(Axis.valueOf(parts[0].toUpperCase()), Integer.valueOf(parts[1]));
    }
  }

  private static InputPair<ImmutableList<Point>, ImmutableList<Fold>> readInput(String name) {
    return InputReader.readInputPair(
        name,
        scn -> {
          ImmutableList.Builder<Point> builder = ImmutableList.builder();
          while (scn.hasNext()) {
            String line = scn.nextLine();
            if (line.isEmpty()) {
              break;
            }
            builder.add(Point.parse(line));
          }
          return builder.build();
        },
        scn -> {
          ImmutableList.Builder<Fold> builder = ImmutableList.builder();
          while (scn.hasNext()) {
            builder.add(Fold.parse(scn.nextLine()));
          }
          return builder.build();
        });
  }

  private static final String FOLD_INSTRUCTION_PREFIX = "fold along ";

  private static long part1(ImmutableList<Point> points, Fold fold) {
    return points.stream().map(fold::apply).distinct().count();
  }

  private static String part2(ImmutableList<Point> points, ImmutableList<Fold> folds) {
    return printPoints(
        points.stream()
            .map(
                point -> {
                  for (Fold fold : folds) {
                    point = fold.apply(point);
                  }
                  return point;
                })
            .distinct()
            .collect(toImmutableList()));
  }

  private static String printPoints(ImmutableList<Point> points) {
    int maxX = points.stream().mapToInt(p -> p.x()).max().orElse(0);
    int maxY = points.stream().mapToInt(p -> p.y()).max().orElse(0);
    boolean[][] visible = new boolean[maxX + 1][maxY + 1];
    for (Point p : points) {
      visible[p.x()][p.y()] = true;
    }

    StringBuilder builder = new StringBuilder();
    for (int j = 0; j <= maxY; ++j) {
      for (int i = 0; i <= maxX; ++i) {
        builder.append(visible[i][j] ? "@" : " ");
      }
      builder.append("\n");
    }
    return builder.toString();
  }

  public static void main(String[] args) {
    InputPair<ImmutableList<Point>, ImmutableList<Fold>> input = readInput(args[0]);
    System.out.println(part1(input.first(), input.second().get(0)));
    System.out.println(part2(input.first(), input.second()));
    return;
  }
}
