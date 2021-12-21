package nemben.aoc.d19;

import com.google.auto.value.AutoValue;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import nemben.aoc.InputReader;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.Optional;
import java.util.Queue;
import java.util.stream.IntStream;

import static com.google.common.collect.ImmutableList.toImmutableList;
import static com.google.common.collect.ImmutableSet.toImmutableSet;

public class BeaconScanner {

  enum Permutation {
    P1(1, 2, 3, +1),
    P2(1, 3, 2, -1),
    P3(2, 1, 3, -1),
    P4(2, 3, 1, +1),
    P5(3, 1, 2, +1),
    P6(3, 2, 1, -1);

    private final int s1;
    private final int s2;
    private final int s3;
    private final int mul;

    Permutation(int s1, int s2, int s3, int mul) {
      this.s1 = s1;
      this.s2 = s2;
      this.s3 = s3;
      this.mul = mul;
    }

    private Coord apply(Coord c, int step) {
      switch (step % 4) {
        case 0:
          return Coord.of(mul * select(c, s1), mul * select(c, s2), mul * select(c, s3));
        case 1:
          return Coord.of(mul * select(c, s1), -mul * select(c, s2), -mul * select(c, s3));
        case 2:
          return Coord.of(-mul * select(c, s1), mul * select(c, s2), -mul * select(c, s3));
        case 3:
          return Coord.of(-mul * select(c, s1), -mul * select(c, s2), mul * select(c, s3));
        default:
          throw new AssertionError();
      }
    }

    private int select(Coord c, int select) {
      return select == 1 ? c.c1() : (select == 2 ? c.c2() : c.c3());
    }
  }

  @AutoValue
  abstract static class Rotation {
    abstract Permutation permutation();

    abstract int step();

    Coord apply(Coord c) {
      return permutation().apply(c, step());
    }

    private static Rotation of(Permutation permutation, int step) {
      return new AutoValue_BeaconScanner_Rotation(permutation, step);
    }

    static final Rotation NONE = Rotation.of(Permutation.P1, 0);

    static final ImmutableList<Rotation> ROTATIONS =
        Arrays.stream(Permutation.values())
            .flatMap(p -> IntStream.range(0, 4).mapToObj(step -> Rotation.of(p, step)))
            .collect(toImmutableList());
  }

  @AutoValue
  abstract static class Coord {
    abstract int c1();

    abstract int c2();

    abstract int c3();

    Coord plus(Coord base) {
      return Coord.of(c1() + base.c1(), c2() + base.c2(), c3() + base.c3());
    }

    Coord minus(Coord base) {
      return Coord.of(c1() - base.c1(), c2() - base.c2(), c3() - base.c3());
    }

    int manhattanNorm() {
      return Math.abs(c1()) + Math.abs(c2()) + Math.abs(c3());
    }

    static Coord of(int c1, int c2, int c3) {
      return new AutoValue_BeaconScanner_Coord(c1, c2, c3);
    }

    static Coord parse(String val) {
      String[] parts = val.split(",");
      return of(Integer.parseInt(parts[0]), Integer.parseInt(parts[1]), Integer.parseInt(parts[2]));
    }

    static final Coord ORIGIN = Coord.of(0, 0, 0);
  }

  @AutoValue
  abstract static class Box {
    abstract Coord min();

    abstract Coord max();

    Box minus(Coord base) {
      return Box.of(min().minus(base), max().minus(base));
    }

    boolean contains(Coord c) {
      return min().c1() <= c.c1()
          && c.c1() <= max().c1()
          && min().c2() <= c.c2()
          && c.c2() <= max().c2()
          && min().c3() <= c.c3()
          && c.c3() <= max().c3();
    }

    Optional<Box> intersect(Box other) {
      return Optional.of(
              Box.of(
                  Coord.of(
                      min().c1() >= other.min().c1() ? min().c1() : other.min().c1(),
                      min().c2() >= other.min().c2() ? min().c2() : other.min().c2(),
                      min().c3() >= other.min().c3() ? min().c3() : other.min().c3()),
                  Coord.of(
                      max().c1() <= other.max().c1() ? max().c1() : other.max().c1(),
                      max().c2() <= other.max().c2() ? max().c2() : other.max().c2(),
                      max().c3() <= other.max().c3() ? max().c3() : other.max().c3())))
          .filter(
              box ->
                  box.min().c1() <= box.max().c1()
                      && box.min().c2() <= box.max().c2()
                      && box.min().c3() <= box.max().c3());
    }

    static Box of(Coord min, Coord max) {
      return new AutoValue_BeaconScanner_Box(min, max);
    }
  }

  @AutoValue
  abstract static class Scan {
    abstract int scan();

    abstract ImmutableList<Coord> beacons();

    Scan rotate(Rotation r) {
      return of(scan(), beacons().stream().map(b -> r.apply(b)).collect(toImmutableList()));
    }

    ImmutableList<Beacon> toBeacons() {
      return beacons().stream()
          .map(
              beacon ->
                  Beacon.of(
                      scan(),
                      Coord.ORIGIN.minus(beacon),
                      Box.of(Coord.of(-1000, -1000, -1000), Coord.of(1000, 1000, 1000))
                          .minus(beacon),
                      beacons().stream().map(b -> b.minus(beacon)).collect(toImmutableList())))
          .collect(toImmutableList());
    }

    static Scan of(int scan, ImmutableList<Coord> beacons) {
      return new AutoValue_BeaconScanner_Scan(scan, beacons);
    }
  }

  @AutoValue
  abstract static class Beacon {
    abstract int scan();

    abstract Coord scanner();

    abstract Box box();

    abstract ImmutableList<Coord> relative();

    boolean matches(Beacon other) {
      Box intersection = box().intersect(other.box()).get();
      if (boxScore(intersection) != other.boxScore(intersection)) {
        return false;
      }
      return intersectBox(intersection).equals(other.intersectBox(intersection));
    }

    private int boxScore(Box box) {
      return relative().stream().filter(box::contains).mapToInt(Coord::manhattanNorm).sum();
    }

    private ImmutableSet<Coord> intersectBox(Box box) {
      return relative().stream().filter(box::contains).collect(toImmutableSet());
    }

    static Beacon of(int scan, Coord scanner, Box box, ImmutableList<Coord> relative) {
      return new AutoValue_BeaconScanner_Beacon(scan, scanner, box, relative);
    }
  }

  @AutoValue
  abstract static class ScannerPosition {
    abstract Coord position();

    abstract Rotation rotation();

    static ScannerPosition of(Coord position, Rotation rotation) {
      return new AutoValue_BeaconScanner_ScannerPosition(position, rotation);
    }
  }

  private static ImmutableList<Scan> readInput(String name) {
    return InputReader.readSimpleInput(
        name,
        scn -> {
          String h = scn.nextLine();
          int scanner =
              Integer.parseInt(h.substring("--- scanner ".length(), h.length() - " ---".length()));

          ImmutableList.Builder<Coord> builder = ImmutableList.builder();
          while (scn.hasNext()) {
            String line = scn.nextLine();
            if (line.isEmpty()) {
              break;
            }
            builder.add(Coord.parse(line));
          }
          ImmutableList<Coord> beacons = builder.build();

          return Scan.of(scanner, beacons);
        });
  }

  private static ScannerPosition[] findScanners(ImmutableList<Scan> scans) {
    ScannerPosition[] scanners = new ScannerPosition[scans.size()];
    scanners[0] = ScannerPosition.of(Coord.ORIGIN, Rotation.NONE);

    Queue<ImmutableList<Beacon>> known = new LinkedList<>();
    Queue<Scan> pending = new LinkedList<>();
    Queue<Scan> stillPending = new LinkedList<>();

    known.add(scans.get(0).toBeacons());
    for (int i = 1; i < scans.size(); ++i) {
      pending.add(scans.get(i));
    }

    while (!known.isEmpty()) {
      ImmutableList<Beacon> knownBeacons = known.poll();

      while (!pending.isEmpty()) {
        Scan pendingScan = pending.poll();

        ScannerPosition pos = tryRotations(knownBeacons, pendingScan);
        if (pos != null) {
          known.add(pendingScan.rotate(pos.rotation()).toBeacons());

          scanners[pendingScan.scan()] =
              ScannerPosition.of(
                  scanners[knownBeacons.get(0).scan()].position().plus(pos.position()),
                  pos.rotation());
        } else {
          stillPending.add(pendingScan);
        }
      }
      pending = stillPending;
      stillPending = new LinkedList<>();
    }
    return scanners;
  }

  private static ScannerPosition tryRotations(ImmutableList<Beacon> known, Scan pending) {
    for (Rotation rotation : Rotation.ROTATIONS) {
      Coord p = matchBeacons(known, pending.rotate(rotation).toBeacons());
      if (p != null) {
        return ScannerPosition.of(p, rotation);
      }
    }
    return null;
  }

  private static Coord matchBeacons(
      ImmutableList<Beacon> leftBeacons, ImmutableList<Beacon> rightBeacons) {
    int cnt = 0;
    for (Beacon left : leftBeacons) {
      for (Beacon right : rightBeacons) {
        if (!left.matches(right)) {
          continue;
        }
        cnt += 1;
        if (cnt >= 12) {
          return right.scanner().minus(left.scanner());
        }
      }
    }
    return null;
  }

  private static long part1(ImmutableList<Scan> scans, ScannerPosition[] scanners) {
    return scans.stream()
        .flatMap(
            scan -> {
              ScannerPosition scanner = scanners[scan.scan()];
              return scan.rotate(scanner.rotation()).toBeacons().stream()
                  .map(b -> scanner.position().minus(b.scanner()));
            })
        .distinct()
        .count();
  }

  private static int part2(ImmutableList<Scan> scans, ScannerPosition[] scanners) {
    int max = Integer.MIN_VALUE;
    for (int i = 0; i < scans.size() - 1; ++i) {
      for (int j = i + 1; j < scans.size(); ++j) {
        int mDist = scanners[i].position().minus(scanners[j].position()).manhattanNorm();
        if (mDist > max) {
          max = mDist;
        }
      }
    }
    return max;
  }

  public static void main(String[] args) {
    ImmutableList<Scan> input = readInput(args[0]);
    ScannerPosition[] scanners = findScanners(input);
    System.out.println(part1(input, scanners));
    System.out.println(part2(input, scanners));
  }
}
