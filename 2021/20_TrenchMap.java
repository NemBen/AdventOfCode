package nemben.aoc.d20;

import nemben.aoc.InputReader;

import java.util.ArrayList;

public class TrenchMap {
  private static InputReader.InputPair<boolean[], boolean[][]> readInput(String name) {
    return InputReader.readInputPair(
        name,
        scn -> {
          String line = scn.nextLine();
          boolean[] enhancement = new boolean[line.length()];
          for (int i = 0; i < line.length(); ++i) {
            enhancement[i] = line.charAt(i) == '#';
          }
          scn.nextLine();
          return enhancement;
        },
        scn -> {
          ArrayList<String> lines = new ArrayList<>();
          while (scn.hasNext()) {
            lines.add(scn.nextLine());
          }
          boolean[][] img = new boolean[lines.size() + 4][lines.get(0).length() + 4];
          for (int i = 0; i < img.length - 4; ++i) {
            for (int j = 0; j < img[i].length - 4; ++j) {
              img[i + 2][j + 2] = lines.get(i).charAt(j) == '#';
            }
          }
          return img;
        });
  }

  private static boolean[][] enhance(boolean[] enhancement, boolean[][] img) {
    boolean[][] newImg = new boolean[img.length + 2][img[0].length + 2];
    if (enhancement[0] && !img[0][0]) {
      for (int i = 0; i < newImg.length; ++i) {
        newImg[i][0] = true;
        newImg[i][1] = true;
        newImg[i][newImg[i].length - 2] = true;
        newImg[i][newImg[i].length - 1] = true;
      }
      for (int i = 0; i < newImg[0].length; ++i) {
        newImg[0][i] = true;
        newImg[1][i] = true;
        newImg[newImg.length - 2][i] = true;
        newImg[newImg.length - 1][i] = true;
      }
    }
    for (int i = 1; i < img.length - 1; ++i) {
      for (int j = 1; j < img[i].length - 1; ++j) {
        newImg[i + 1][j + 1] = enhancement[getValue(img, i, j)];
      }
    }
    return newImg;
  }

  private static int getValue(boolean[][] img, int baseX, int baseY) {
    int agg = 0;
    for (int i = baseX - 1; i <= baseX + 1; ++i) {
      for (int j = baseY - 1; j <= baseY + 1; ++j) {
        agg *= 2;
        agg += img[i][j] ? 1 : 0;
      }
    }
    return agg;
  }

  private static int countPixels(boolean[][] img) {
    int cnt = 0;
    for (int i = 0; i < img.length; ++i) {
      for (int j = 0; j < img[i].length; ++j) {
        cnt += img[i][j] ? 1 : 0;
      }
    }
    return cnt;
  }

  private static int solve(boolean[] enhancement, boolean[][] img, int steps) {
    for (int i = 0; i < steps; ++i) {
      img = enhance(enhancement, img);
    }
    return countPixels(img);
  }

  private static void print(boolean[][] img) {
    System.out.println();
    for (int i = 0; i < img.length; ++i) {
      for (int j = 0; j < img[i].length; ++j) {
        System.out.print(img[i][j] ? '#' : '.');
      }
      System.out.println();
    }
    System.out.println();
  }

  private static int part1(boolean[] enhancement, boolean[][] img) {
    return solve(enhancement, img, 2);
  }

  private static int part2(boolean[] enhancement, boolean[][] img) {
    return solve(enhancement, img, 50);
  }

  public static void main(String[] args) {
    InputReader.InputPair<boolean[], boolean[][]> input = readInput(args[0]);
    System.out.println(part1(input.first(), input.second()));
    System.out.println(part2(input.first(), input.second()));
  }
}
