package nemben.aoc.d14;

import com.google.common.collect.ImmutableMultimap;
import nemben.aoc.InputReader;

import java.util.HashMap;
import java.util.Map;

import static java.util.Comparator.comparingLong;

public class ExtendedPolymerization {
  private static InputReader.InputPair<String, ImmutableMultimap<String, String>> readInput(
      String name) {
    return InputReader.readInputPair(
        name,
        scn -> {
          String line = scn.nextLine();
          scn.nextLine();
          return line;
        },
        scn -> {
          ImmutableMultimap.Builder<String, String> builder = ImmutableMultimap.builder();
          while (scn.hasNext()) {
            String[] parts = scn.nextLine().split(" -> ");
            builder.put(parts[0], parts[0].charAt(0) + parts[1]);
            builder.put(parts[0], parts[1] + parts[0].charAt(1));
          }
          return builder.build();
        });
  }

  private static HashMap<String, Long> toPairCounts(String base) {
    HashMap<String, Long> cnts = new HashMap<>();
    for (int i = 1; i < base.length(); ++i) {
      cnts.compute(base.substring(i - 1, i + 1), (p, cnt) -> (cnt == null ? 0 : cnt) + 1);
    }
    return cnts;
  }

  private static HashMap<String, Long> polymerize(
      HashMap<String, Long> cnts, ImmutableMultimap<String, String> rules) {
    HashMap<String, Long> nextCnts = new HashMap<>();
    for (Map.Entry<String, Long> entry : cnts.entrySet()) {
      for (String val : rules.get(entry.getKey())) {
        nextCnts.compute(val, (p, cnt) -> (cnt == null ? 0 : cnt) + entry.getValue());
      }
    }
    return nextCnts;
  }

  private static long getDifference(String base, HashMap<String, Long> cnts) {
    HashMap<Character, Long> values = new HashMap<>();
    for (Map.Entry<String, Long> entry : cnts.entrySet()) {
      for (char c : entry.getKey().toCharArray()) {
        values.compute(c, (k, v) -> (v == null ? 0 : v) + entry.getValue());
      }
    }
    values.compute(base.charAt(0), (k, v) -> (v == null ? 0 : v) + 1);
    values.compute(base.charAt(base.length() - 1), (k, v) -> (v == null ? 0 : v) + 1);

    Long max =
        values.entrySet().stream()
            .max(comparingLong(Map.Entry::getValue))
            .map(Map.Entry::getValue)
            .orElse(0L);
    Long min =
        values.entrySet().stream()
            .min(comparingLong(Map.Entry::getValue))
            .map(Map.Entry::getValue)
            .orElse(0L);
    return (max - min) / 2;
  }

  private static long solve(String base, ImmutableMultimap<String, String> rules, int steps) {
    HashMap<String, Long> cnts = toPairCounts(base);
    for (int i = 0; i < steps; ++i) {
      cnts = polymerize(cnts, rules);
    }
    return getDifference(base, cnts);
  }

  private static long part1(String base, ImmutableMultimap<String, String> rules) {
    return solve(base, rules, 10);
  }

  private static long part2(String base, ImmutableMultimap<String, String> rules) {
    return solve(base, rules, 40);
  }

  public static void main(String[] args) {
    InputReader.InputPair<String, ImmutableMultimap<String, String>> input = readInput(args[0]);
    System.out.println(part1(input.first(), input.second()));
    System.out.println(part2(input.first(), input.second()));
  }
}
