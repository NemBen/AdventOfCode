package nemben.aoc.d01;

import com.google.common.collect.ImmutableList;
import com.google.common.primitives.Longs;
import nemben.aoc.InputReader;

public class SonarSweep {
  private static ImmutableList<Long> readReport(String name) {
    return InputReader.readSimpleInput(name, scn -> scn.nextLong());
  }

  private static int countIncreases(long[] report, int window) {
    int cnt = 0;
    for (int i = window; i < report.length; ++i) {
      if (report[i] > report[i - window]) {
        ++cnt;
      }
    }
    return cnt;
  }

  public static void main(String[] args) {
    long[] report = Longs.toArray(readReport(args[0]));
    System.out.println(countIncreases(report, 1));
    System.out.println(countIncreases(report, 3));
    return;
  }
}
