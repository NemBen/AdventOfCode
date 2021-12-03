package nemben.aoc.d03;

import com.google.common.collect.ImmutableList;
import nemben.aoc.InputReader;

public class BinaryDiagnostic {

  private static ImmutableList<String> readReport(String name) {
    return InputReader.readSimpleInput(name, scn -> scn.next());
  }

  private static int parseBinary(String binary) {
    int val = 0;
    for (int i = 0; i < binary.length(); ++i) {
      val *= 2;
      val += Integer.parseInt("" + binary.charAt(i));
    }
    return val;
  }

  private static int bitAt(int val, int width, int pos) {
    int i = width - 1 - pos;
    return (val & (1 << i)) == (1 << i) ? 1 : 0;
  }

  private static int part1(int[] report, int width) {
    int[] cntOnes = new int[width];
    for (int i = 0; i < report.length; ++i) {
      for (int j = 0; j < width; ++j) {
        cntOnes[j] += bitAt(report[i], width, j);
      }
    }

    int agg = 0;
    for (int i = 0; i < width; ++i) {
      agg *= 2;
      agg += 2 * cntOnes[i] > report.length ? 1 : 0;
    }

    return agg * (~agg & ((1 << width) - 1));
  }

  private static int part2(int[] report, int width) {
    return part2(report, width, 0, 0) * part2(report, width, 0, 1);
  }

  private static int part2(int[] report, int width, int pos, int select) {
    if (report.length == 1) {
      return report[0];
    }

    int cntOnes = 0;
    for (int i = 0; i < report.length; ++i) {
      cntOnes += bitAt(report[i], width, pos);
    }

    int filter = 2 * cntOnes >= report.length ? select : (1 - select);

    int[] filtered = new int[filter == 1 ? cntOnes : report.length - cntOnes];
    for (int i = 0, j = 0; i < report.length; ++i) {
      if (bitAt(report[i], width, pos) == filter) {
        filtered[j++] = report[i];
      }
    }

    return part2(filtered, width, pos + 1, select);
  }

  public static void main(String[] args) {
    ImmutableList<String> rawReport = readReport(args[0]);

    int[] report = rawReport.stream().mapToInt(v -> parseBinary(v)).toArray();
    int width = rawReport.get(0).length();

    System.out.println(part1(report, width));
    System.out.println(part2(report, width));

    return;
  }
}
