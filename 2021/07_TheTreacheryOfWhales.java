package nemben.aoc.d07;

import com.google.common.collect.ImmutableList;
import nemben.aoc.InputReader;

import java.util.Arrays;
import java.util.function.ToLongFunction;

import static com.google.common.collect.ImmutableList.sortedCopyOf;
import static com.google.common.collect.ImmutableList.toImmutableList;

public class TheTreacheryOfWhales {
  private static ImmutableList<Integer> readInput(String name) {
    return InputReader.readInput(
        name,
        scn ->
            Arrays.stream(scn.nextLine().split(","))
                .map(Integer::parseInt)
                .collect(toImmutableList()));
  }

  private static long part1(ImmutableList<Integer> positions) {
    ImmutableList<Integer> sorted = sortedCopyOf(positions);
    int median = sorted.get(sorted.size() % 2 == 0 ? sorted.size() / 2 : sorted.size() / 2 + 1);
    return distanceSum(positions, i -> simpleDist(median, i));
  }

  private static long part2(ImmutableList<Integer> positions) {
    long average = (long) Math.floor(positions.stream().mapToLong(i -> i).average().orElse(0.0d));
    return Math.min(
        distanceSum(positions, i -> sqrDist(average, i)),
        distanceSum(positions, i -> sqrDist(average + 1, i)));
  }

  private static long simpleDist(long a, long b) {
    return Math.abs(a - b);
  }

  private static long sqrDist(long a, long b) {
    long dist = Math.abs(a + 1 - b);
    return dist * (dist + 1) / 2;
  }

  private static long distanceSum(
      ImmutableList<Integer> positions, ToLongFunction<Integer> distFn) {
    return positions.stream().mapToLong(distFn).sum();
  }

  public static void main(String[] args) {
    ImmutableList<Integer> positions = readInput(args[0]);
    System.out.println(part1(positions));
    System.out.println(part2(positions));
    return;
  }
}
