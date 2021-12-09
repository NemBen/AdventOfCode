package nemben.aoc.d09;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Ordering;
import nemben.aoc.InputReader;
import nemben.aoc.Point;

import java.util.LinkedList;
import java.util.Queue;

import static com.google.common.collect.ImmutableList.toImmutableList;

public class SmokeBasin {

  private static int[][] readInput(String name) {
    ImmutableList<String> strings = InputReader.readSimpleInput(name, scn -> scn.nextLine());
    int[][] arr = new int[strings.size()][strings.get(0).length()];
    for (int i = 0; i < strings.size(); ++i) {
      String str = strings.get(i);
      for (int j = 0; j < str.length(); ++j) {
        arr[i][j] = Integer.parseInt(str.substring(j, j + 1));
      }
    }
    return arr;
  }

  private static int[][] padEdges(int[][] input) {
    int[][] padded = new int[input.length + 2][input[0].length + 2];
    for (int i = 0; i < padded.length; ++i) {
      padded[i][0] = 10;
      padded[i][padded[i].length - 1] = 10;
    }
    for (int i = 0; i < padded[0].length; ++i) {
      padded[0][i] = 10;
      padded[padded.length - 1][i] = 10;
    }
    for (int i = 0; i < input.length; ++i) {
      for (int j = 0; j < padded[i].length - 2; ++j) {
        padded[i + 1][j + 1] = input[i][j];
      }
    }
    return padded;
  }

  private static int part1(int[][] input) {
    int[][] heights = padEdges(input);

    return findBasins(heights).stream().mapToInt(basin -> heights[basin.x()][basin.y()] + 1).sum();
  }

  private static int part2(int[][] input) {
    int[][] heights = padEdges(input);

    ImmutableList<Integer> sizes = visitBasins(heights, findBasins(heights));
    ImmutableList<Integer> sortedSizes = Ordering.natural().reverse().immutableSortedCopy(sizes);
    return sortedSizes.get(0) * sortedSizes.get(1) * sortedSizes.get(2);
  }

  private static ImmutableList<Point> findBasins(int[][] heights) {
    ImmutableList.Builder<Point> builder = ImmutableList.builder();
    for (int i = 1; i < heights.length - 1; ++i) {
      for (int j = 1; j < heights[i].length - 1; ++j) {
        int d = heights[i][j];
        if (heights[i - 1][j] > d
            && heights[i + 1][j] > d
            && heights[i][j - 1] > d
            && heights[i][j + 1] > d) {
          builder.add(Point.of(i, j));
        }
      }
    }
    return builder.build();
  }

  private static ImmutableList<Integer> visitBasins(int[][] heights, ImmutableList<Point> basins) {
    boolean[][] visited = new boolean[heights.length][heights[0].length];
    return basins.stream()
        .map(basin -> visitBasin(heights, visited, basin))
        .collect(toImmutableList());
  }

  private static int visitBasin(int[][] heights, boolean[][] visited, Point basin) {
    int size = 0;
    Queue<Point> q = new LinkedList<>();
    q.add(basin);
    visited[basin.x()][basin.y()] = true;
    size++;
    while (!q.isEmpty()) {
      Point p = q.poll();
      int ph = heights[p.x()][p.y()];

      for (Point n :
          ImmutableList.of(
              Point.of(p.x() - 1, p.y()),
              Point.of(p.x() + 1, p.y()),
              Point.of(p.x(), p.y() - 1),
              Point.of(p.x(), p.y() + 1))) {
        if (visited[n.x()][n.y()] || heights[n.x()][n.y()] >= 9) {
          continue;
        }
        if (heights[n.x()][n.y()] > ph) {
          q.add(n);
          visited[n.x()][n.y()] = true;
          size++;
        }
      }
    }
    return size;
  }

  public static void main(String[] args) {
    int[][] input = readInput(args[0]);
    System.out.println(part1(input));
    System.out.println(part2(input));
    return;
  }
}
