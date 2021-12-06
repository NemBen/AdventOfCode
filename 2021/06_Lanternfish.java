package nemben.aoc.d06;

import com.google.common.collect.ImmutableList;
import nemben.aoc.InputReader;
import java.util.Arrays;
import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.toList;

public class Lanternfish {
  private static ImmutableList<Integer> readInput(String name) {
    return InputReader.readInput(
        name,
        scn ->
            Arrays.stream(scn.nextLine().split(","))
                .map(Integer::parseInt)
                .collect(collectingAndThen(toList(), ImmutableList::copyOf)));
  }

  private static long solve(ImmutableList<Integer> timers, int days) {
    long[] cnt = new long[9];
    timers.forEach(t -> cnt[t]++);
    for (int day = 0; day < days; ++day) {
      long ready = cnt[0];
      for (int i = 1; i < cnt.length; ++i) {
        cnt[i - 1] = cnt[i];
      }
      cnt[6] += ready;
      cnt[8] = ready;
    }
    return Arrays.stream(cnt).sum();
  }

  public static void main(String[] args) {
    ImmutableList<Integer> timers = readInput(args[0]);
    System.out.println(solve(timers, 80));
    System.out.println(solve(timers, 256));
    return;
  }
}
