package nemben.aoc.d25;

import nemben.aoc.InputReader;

public class SeaCucumber {
  private static int[][] readInput(String name) {
    return InputReader.readSimpleInput(
            name,
            scn -> {
              String line = scn.nextLine();
              int[] row = new int[line.length()];
              for (int i = 0; i < row.length; ++i) {
                switch (line.charAt(i)) {
                  case '>':
                    row[i] = 1;
                    break;
                  case 'v':
                    row[i] = 2;
                    break;
                }
              }
              return row;
            })
        .toArray(new int[0][0]);
  }

  private static int step(int[][] currMap, int[][] nextMap) {
    int cnt = 0;
    for (int i = 0; i < nextMap.length; ++i) {
      for (int j = 0; j < nextMap[i].length; ++j) {
        nextMap[i][j] = 0;
      }
    }
    for (int i = 0; i < currMap.length; ++i) {
      for (int j = 0; j < currMap[i].length; ++j) {
        if (currMap[i][j] != 1) {
          continue;
        }
        int nextJ = (j + 1) % currMap[i].length;
        if (currMap[i][nextJ] == 0) {
          nextMap[i][nextJ] = 1;
          cnt += 1;
        } else {
          nextMap[i][j] = 1;
        }
      }
    }
    for (int i = 0; i < currMap.length; ++i) {
      for (int j = 0; j < currMap[i].length; ++j) {
        if (currMap[i][j] != 2) {
          continue;
        }
        int nextI = (i + 1) % currMap.length;
        if (currMap[nextI][j] != 2 && nextMap[nextI][j] == 0) {
          nextMap[nextI][j] = 2;
          cnt += 1;
        } else {
          nextMap[i][j] = 2;
        }
      }
    }
    return cnt;
  }

  private static int part1(int[][] map) {
    int[][] nextMap = new int[map.length][map[0].length];

    int step = 0;
    int cnt;
    do {
      cnt = step(map, nextMap);
      step += 1;

      int[][] t = nextMap;
      nextMap = map;
      map = t;
    } while (cnt > 0);

    return step;
  }

  public static void main(String[] args) {
    int[][] map = readInput(args[0]);
    int i = part1(map);
    System.out.println(i);
  }
}
