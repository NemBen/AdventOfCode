package nemben.aoc.d15;

import com.google.auto.value.AutoValue;
import com.google.common.collect.ImmutableList;
import nemben.aoc.InputReader;
import nemben.aoc.Point;

import java.util.PriorityQueue;

import static java.util.Comparator.comparingInt;

public class Chiton {
  private static int[][] readInput(String name) {
    return InputReader.readSimpleInput(
            name,
            scn -> {
              String line = scn.nextLine();
              int[] row = new int[line.length()];
              for (int i = 0; i < line.length(); ++i) {
                row[i] = Integer.parseInt(line.substring(i, i + 1));
              }
              return row;
            })
        .toArray(new int[0][0]);
  }

  @AutoValue
  abstract static class WeightedPoint {
    abstract Point point();

    abstract int weight();

    static WeightedPoint of(Point point, int weight) {
      return new AutoValue_Chiton_WeightedPoint(point, weight);
    }
  }

  private static int dijkstra(int[][] cave) {
    boolean[][] visited = new boolean[cave.length][cave[0].length];
    int[][] costs = new int[cave.length][cave[0].length];
    for (int i = 0; i < costs.length; ++i) {
      for (int j = 0; j < costs.length; ++j) {
        costs[i][j] = Integer.MAX_VALUE;
      }
    }

    PriorityQueue<WeightedPoint> q = new PriorityQueue<>(comparingInt(WeightedPoint::weight));
    q.add(WeightedPoint.of(Point.of(0, 0), 0));
    costs[0][0] = 0;

    while (!q.isEmpty()) {
      WeightedPoint wp = q.poll();
      Point p = wp.point();
      if (visited[p.x()][p.y()]) {
        continue;
      }
      visited[p.x()][p.y()] = true;

      for (Point n :
          ImmutableList.of(
              Point.of(p.x() - 1, p.y()),
              Point.of(p.x(), p.y() - 1),
              Point.of(p.x(), p.y() + 1),
              Point.of(p.x() + 1, p.y()))) {
        if (n.x() < 0 || n.x() >= cave.length || n.y() < 0 || n.y() >= cave[n.x()].length) {
          continue;
        }
        int alt = wp.weight() + cave[n.x()][n.y()];
        if (alt < costs[n.x()][n.y()]) {
          costs[n.x()][n.y()] = alt;
          q.add(WeightedPoint.of(n, alt));
        }
      }
    }

    return costs[cave.length - 1][cave[cave.length - 1].length - 1];
  }

  private static int part1(int[][] cave) {
    return dijkstra(cave);
  }

  private static int part2(int[][] cave) {
    int[][] bigCave = new int[cave.length * 5][cave[0].length * 5];
    for (int tI = 0; tI < 5; ++tI) {
      for (int tJ = 0; tJ < 5; ++tJ) {
        int oI = tI * cave.length;
        int oJ = tJ * cave[0].length;
        for (int i = 0; i < cave.length; ++i) {
          for (int j = 0; j < cave.length; ++j) {
            bigCave[oI + i][oJ + j] = cave[i][j] + tI + tJ;
            if (bigCave[oI + i][oJ + j] > 9) {
              bigCave[oI + i][oJ + j] -= 9;
            }
          }
        }
      }
    }
    return dijkstra(bigCave);
  }

  public static void main(String[] args) {
    int[][] input = readInput(args[0]);
    System.out.println(part1(input));
    System.out.println(part2(input));
  }
}
