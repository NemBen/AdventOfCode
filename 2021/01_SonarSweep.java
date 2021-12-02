package nemben.aoc.d01;

import com.google.common.collect.ImmutableList;
import com.google.common.primitives.Longs;
import java.io.InputStream;
import java.util.Scanner;

public class SonarSweep {

  private static ImmutableList<Long> readReport(String name) {
    try (Scanner scn = new Scanner(getInputStream(name))) {
      ImmutableList.Builder<Long> builder = new ImmutableList.Builder<>();
      while (scn.hasNextLong()) {
        builder.add(scn.nextLong());
      }
      return builder.build();
    }
  }

  private static InputStream getInputStream(String name) {
    return SonarSweep.class.getClassLoader().getResourceAsStream(name);
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
