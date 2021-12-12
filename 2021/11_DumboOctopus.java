package nemben.aoc.d11;

import com.google.common.collect.ImmutableList;
import nemben.aoc.InputReader;
import nemben.aoc.Point;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;

public class DumboOctopus {

  private static int[][] readInput(String name) {
    ImmutableList<String> lines = InputReader.readSimpleInput(name, scn -> scn.nextLine());
    int[][] grid = new int[lines.size()][lines.get(0).length()];
    for (int i = 0; i < lines.size(); ++i) {
      String line = lines.get(i);
      for (int j = 0; j < line.length(); ++j) {
        grid[i][j] = Integer.parseInt(line.substring(j, j + 1));
      }
    }
    return grid;
  }

  private static int part1(int[][] grid) {
    int cnt = 0;
    for (int i = 0; i < 100; ++i) {
      cnt += step(grid);
    }
    return cnt;
  }

  private static int part2(int[][] grid) {
    int step = 1;
    while (step(grid) != grid.length * grid[0].length) {
      step += 1;
    }
    return step;
  }

  private static int step(int[][] grid) {
    Queue<Point> q = new LinkedList<>();
    for (int i = 0; i < grid.length; ++i) {
      for (int j = 0; j < grid[i].length; ++j) {
        grid[i][j] += 1;
        if (grid[i][j] > 9) {
          q.add(Point.of(i, j));
        }
      }
    }
    HashSet<Point> flashed = new HashSet<>();
    while (!q.isEmpty()) {
      Point p = q.poll();
      if (flashed.contains(p)) {
        continue;
      }
      flashed.add(p);

      for (Point n :
          ImmutableList.of(
              Point.of(p.x() - 1, p.y() - 1),
              Point.of(p.x() - 1, p.y()),
              Point.of(p.x() - 1, p.y() + 1),
              Point.of(p.x(), p.y() - 1),
              Point.of(p.x(), p.y() + 1),
              Point.of(p.x() + 1, p.y() - 1),
              Point.of(p.x() + 1, p.y()),
              Point.of(p.x() + 1, p.y() + 1))) {
        if (n.x() < 0 || n.x() >= grid.length || n.y() < 0 || n.y() >= grid[n.x()].length) {
          continue;
        }
        grid[n.x()][n.y()] += 1;
        if (grid[n.x()][n.y()] > 9 && !flashed.contains(n)) {
          q.add(n);
        }
      }
    }
    for (int i = 0; i < grid.length; ++i) {
      for (int j = 0; j < grid[i].length; ++j) {
        if (grid[i][j] > 9) {
          grid[i][j] = 0;
        }
      }
    }
    return flashed.size();
  }

  public static void main(String[] args) {
    int[][] input = readInput(args[0]);
    System.out.println(part1(input));
    System.out.println(part2(input));
    return;
  }
}
