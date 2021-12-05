package nemben.aoc.d05;

import com.google.common.collect.ImmutableList;
import nemben.aoc.InputReader;
import nemben.aoc.Line;
import nemben.aoc.Point;
import java.util.HashMap;
import java.util.Iterator;
import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.toList;

public class HydrothermalVenture {
  private static ImmutableList<Line> readInput(String name) {
    return InputReader.readSimpleInput(name, scn -> parseLine(scn.nextLine()));
  }

  private static Line parseLine(String value) {
    String[] points = value.split(" -> ");
    return Line.of(parsePoint(points[0]), parsePoint(points[1]));
  }

  private static Point parsePoint(String value) {
    String[] parts = value.split(",");
    return Point.of(Integer.parseInt(parts[0]), Integer.parseInt(parts[1]));
  }

  private static long part1(ImmutableList<Line> lines) {
    return solve(
        lines.stream()
            .filter(line -> line.isVertical() || line.isHorizontal())
            .collect(collectingAndThen(toList(), ImmutableList::copyOf)));
  }

  private static long part2(ImmutableList<Line> lines) {
    return solve(
            lines.stream()
                    .filter(line -> line.isVertical() || line.isHorizontal() || line.isDiagonal())
                    .collect(collectingAndThen(toList(), ImmutableList::copyOf)));
  }

  private static long solve(ImmutableList<Line> lines) {
    HashMap<Point, Integer> pointCnts = new HashMap<>();
    for (Line line : lines) {
      Iterator<Point> points = line.points();
      while (points.hasNext()) {
        pointCnts.compute(points.next(), (point, cnt) -> cnt == null ? 1 : cnt + 1);
      }
    }
    return pointCnts.values().stream().mapToInt(cnt -> cnt).filter(cnt -> cnt > 1).count();
  }

  public static void main(String[] args) {
    ImmutableList<Line> lines = readInput(args[0]);
    System.out.println(part1(lines));
    System.out.println(part2(lines));
    return;
  }
}
