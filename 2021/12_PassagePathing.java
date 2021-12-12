package nemben.aoc.d12;

import com.google.common.collect.ImmutableListMultimap;
import com.google.common.collect.ImmutableMultimap;
import nemben.aoc.InputReader;

import java.util.HashSet;

import static com.google.common.collect.ImmutableListMultimap.toImmutableListMultimap;

public class PassagePathing {

  private static ImmutableMultimap<String, String> readInput(String name) {
    ImmutableListMultimap<String, String> forward =
        InputReader.readSimpleInput(name, scn -> scn.nextLine().split("\\-")).stream()
            .collect(toImmutableListMultimap(s -> s[0], s -> s[1]));
    return ImmutableListMultimap.<String, String>builder()
        .putAll(forward)
        .putAll(forward.inverse())
        .build();
  }

  private static int part1(ImmutableMultimap<String, String> graph) {
    HashSet<String> visited = new HashSet<>();
    return dfs1(graph, visited, START);
  }

  private static int dfs1(
      ImmutableMultimap<String, String> graph, HashSet<String> visited, String cave) {
    if (cave.equals(END)) {
      return 1;
    }
    if (isSmall(cave)) {
      if (visited.contains(cave)) {
        return 0;
      }
      visited.add(cave);
    }
    int sum = 0;
    for (String n : graph.get(cave)) {
      sum += dfs1(graph, visited, n);
    }
    if (isSmall(cave)) {
      visited.remove(cave);
    }
    return sum;
  }

  private static int part2(ImmutableMultimap<String, String> graph) {
    HashSet<String> visited = new HashSet<>();
    return dfs2(graph, visited, START, false);
  }

  private static int dfs2(
      ImmutableMultimap<String, String> graph,
      HashSet<String> visited,
      String cave,
      boolean twice) {
    if (cave.equals(END)) {
      return 1;
    }
    if (isSmall(cave)) {
      if (visited.contains(cave)) {
        if (twice || cave.equals(START)) {
          return 0;
        }
        int sum = 0;
        for (String n : graph.get(cave)) {
          sum += dfs2(graph, visited, n, true);
        }
        return sum;
      }
      visited.add(cave);
    }
    int sum = 0;
    for (String n : graph.get(cave)) {
      sum += dfs2(graph, visited, n, twice);
    }
    if (isSmall(cave)) {
      visited.remove(cave);
    }
    return sum;
  }

  private static boolean isSmall(String cave) {
    return cave.toLowerCase().equals(cave);
  }

  private static final String START = "start";
  private static final String END = "end";

  public static void main(String[] args) {
    ImmutableMultimap<String, String> input = readInput(args[0]);
    System.out.println(part1(input));
    System.out.println(part2(input));
    return;
  }
}
