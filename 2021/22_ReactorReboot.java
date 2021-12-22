package nemben.aoc.d22;

import com.google.auto.value.AutoValue;
import com.google.common.collect.ImmutableList;
import nemben.aoc.InputReader;

import java.util.function.Function;

import static com.google.common.collect.ImmutableList.toImmutableList;

public class ReactorReboot {

  @AutoValue
  abstract static class Coord {
    abstract int x();

    abstract int y();

    abstract int z();

    static Coord of(int x, int y, int z) {
      return new AutoValue_ReactorReboot_Coord(x, y, z);
    }
  }

  @AutoValue
  abstract static class Box {
    abstract Coord min();

    abstract Coord max();

    boolean isEmpty() {
      return min().x() > max().x() || min().y() > max().y() || min().z() > max().z();
    }

    long size() {
      long x = max().x() - min().x() + 1;
      long y = max().y() - min().y() + 1;
      long z = max().z() - min().z() + 1;
      return x * y * z;
    }

    boolean contains(Coord c) {
      return min().x() <= c.x()
          && c.x() <= max().x()
          && min().y() <= c.y()
          && c.y() <= max().y()
          && min().z() <= c.z()
          && c.z() <= max().z();
    }

    static Box of(Coord min, Coord max) {
      return new AutoValue_ReactorReboot_Box(min, max);
    }
  }

  @AutoValue
  abstract static class Instruction {
    abstract boolean set();

    abstract Box box();

    static Instruction of(boolean set, Box box) {
      return new AutoValue_ReactorReboot_Instruction(set, box);
    }
  }

  private static ImmutableList<Instruction> readInput(String name) {
    return InputReader.readSimpleInput(
        name,
        scn -> {
          String line = scn.nextLine();
          boolean set = line.startsWith("on");
          line = line.substring(set ? "on ".length() : "off ".length());
          String[] parts = line.split(",");
          String[] xParts = parts[0].substring("x=".length()).split("\\.\\.");
          String[] yParts = parts[1].substring("y=".length()).split("\\.\\.");
          String[] zParts = parts[2].substring("z=".length()).split("\\.\\.");
          return Instruction.of(
              set,
              Box.of(
                  Coord.of(
                      Math.min(Integer.parseInt(xParts[0]), Integer.parseInt(xParts[1])),
                      Math.min(Integer.parseInt(yParts[0]), Integer.parseInt(yParts[1])),
                      Math.min(Integer.parseInt(zParts[0]), Integer.parseInt(zParts[1]))),
                  Coord.of(
                      Math.max(Integer.parseInt(xParts[0]), Integer.parseInt(xParts[1])),
                      Math.max(Integer.parseInt(yParts[0]), Integer.parseInt(yParts[1])),
                      Math.max(Integer.parseInt(zParts[0]), Integer.parseInt(zParts[1])))));
        });
  }

  private static ImmutableList<Instruction> filterInitializationRegion(
      ImmutableList<Instruction> instructions, int limit) {
    return instructions.stream()
        .filter(
            instruction -> {
              Box box = instruction.box();
              return box.min().x() >= -limit
                  && box.min().y() >= -limit
                  && box.min().z() >= -limit
                  && box.max().x() <= limit
                  && box.max().y() <= limit
                  && box.max().z() <= limit;
            })
        .collect(toImmutableList());
  }

  private static int getMin(ImmutableList<Instruction> instructions, Function<Coord, Integer> fn) {
    return instructions.stream().mapToInt(i -> fn.apply(i.box().min())).min().orElse(0);
  }

  private static int getMax(ImmutableList<Instruction> instructions, Function<Coord, Integer> fn) {
    return instructions.stream().mapToInt(i -> fn.apply(i.box().max())).max().orElse(0);
  }

  private static int part1(ImmutableList<Instruction> instructions) {
    instructions = filterInitializationRegion(instructions, 50);

    int xMin = getMin(instructions, Coord::x);
    int xMax = getMax(instructions, Coord::x);
    int yMin = getMin(instructions, Coord::y);
    int yMax = getMax(instructions, Coord::y);
    int zMin = getMin(instructions, Coord::z);
    int zMax = getMax(instructions, Coord::z);

    boolean[][][] reactor = new boolean[xMax - xMin + 1][yMax - yMin + 1][zMax - zMin + 1];
    for (Instruction instruction : instructions) {
      for (int x = instruction.box().min().x(); x <= instruction.box().max().x(); ++x) {
        for (int y = instruction.box().min().y(); y <= instruction.box().max().y(); ++y) {
          for (int z = instruction.box().min().z(); z <= instruction.box().max().z(); ++z) {
            reactor[x - xMin][y - yMin][z - zMin] = instruction.set();
          }
        }
      }
    }

    int cnt = 0;
    for (int x = 0; x < xMax - xMin + 1; ++x) {
      for (int y = 0; y < yMax - yMin + 1; ++y) {
        for (int z = 0; z < zMax - zMin + 1; ++z) {
          cnt += reactor[x][y][z] ? 1 : 0;
        }
      }
    }
    return cnt;
  }

  private static ImmutableList<Box> subtract(ImmutableList<Box> boxes, Box toRemove) {
    return boxes.stream()
        .flatMap(box -> subtract(box, toRemove).stream())
        .collect(toImmutableList());
  }

  private static ImmutableList<Box> subtract(Box a, Box b) {
    if (a.min().x() > b.max().x()
        || a.max().x() < b.min().x()
        || a.min().y() > b.max().y()
        || a.max().y() < b.min().y()
        || a.min().z() > b.max().z()
        || a.max().z() < b.min().z()) {
      return ImmutableList.of(a);
    }

    return split(a, b).stream()
        .filter(
            box -> {
              Coord center =
                  Coord.of(
                      (box.min().x() + box.max().x()) / 2,
                      (box.min().y() + box.max().y()) / 2,
                      (box.min().z() + box.max().z()) / 2);

              return a.contains(center) && !b.contains(center);
            })
        .collect(toImmutableList());
  }

  private static ImmutableList<Box> split(Box a, Box b) {
    ImmutableList.Builder<Box> boxes = ImmutableList.builder();
    int[] xSplit = buildSplit(a, b, Coord::x);
    int[] ySplit = buildSplit(a, b, Coord::y);
    int[] zSplit = buildSplit(a, b, Coord::z);
    for (int i = 0; i < 3; ++i) {
      for (int j = 0; j < 3; ++j) {
        for (int k = 0; k < 3; ++k) {
          Box box =
              Box.of(
                  Coord.of(xSplit[2 * i], ySplit[2 * j], zSplit[2 * k]),
                  Coord.of(xSplit[2 * i + 1], ySplit[2 * j + 1], zSplit[2 * k + 1]));
          if (box.isEmpty()) {
            continue;
          }
          boxes.add(box);
        }
      }
    }
    return boxes.build();
  }

  private static int[] buildSplit(Box a, Box b, Function<Coord, Integer> fn) {
    if (fn.apply(a.min()) <= fn.apply(b.min()) && fn.apply(b.max()) <= fn.apply(a.max())) {
      return buildSplit(a.min(), b.min(), b.max(), a.max(), fn);
    } else if (fn.apply(b.min()) <= fn.apply(a.min()) && fn.apply(a.max()) <= fn.apply(b.max())) {
      return buildSplit(b.min(), a.min(), a.max(), b.max(), fn);
    } else if (fn.apply(a.min()) <= fn.apply(b.min()) && fn.apply(a.max()) <= fn.apply(b.max())) {
      return buildSplit(a.min(), b.min(), a.max(), b.max(), fn);
    } else if (fn.apply(b.min()) <= fn.apply(a.min()) && fn.apply(b.max()) <= fn.apply(a.max())) {
      return buildSplit(b.min(), a.min(), b.max(), a.max(), fn);
    }
    throw new AssertionError();
  }

  private static int[] buildSplit(
      Coord c1, Coord c2, Coord c3, Coord c4, Function<Coord, Integer> fn) {
    return new int[] {
      fn.apply(c1), fn.apply(c2) - 1, fn.apply(c2), fn.apply(c3), fn.apply(c3) + 1, fn.apply(c4)
    };
  }

  private static long part2(ImmutableList<Instruction> instructions) {
    ImmutableList<Box> on = ImmutableList.of();
    for (Instruction instruction : instructions) {
      if (instruction.set()) {
        ImmutableList<Box> additions = ImmutableList.of(instruction.box());
        for (Box box : on) {
          additions = subtract(additions, box);
        }
        on = ImmutableList.<Box>builder().addAll(on).addAll(additions).build();
      } else {
        on = subtract(on, instruction.box());
      }
    }
    return on.stream().mapToLong(Box::size).sum();
  }

  public static void main(String[] args) {
    ImmutableList<Instruction> input = readInput(args[0]);
    System.out.println(input);
    System.out.println(part1(input));
    System.out.println(part2(input));
  }
}
