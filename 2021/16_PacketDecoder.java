package nemben.aoc.d16;

import com.google.auto.value.AutoValue;
import com.google.common.collect.ImmutableList;
import nemben.aoc.InputReader;

import java.util.LinkedList;
import java.util.Queue;

public class PacketDecoder {
  private static String readInput(String name) {
    return InputReader.readInput(name, scn -> scn.nextLine());
  }

  @AutoValue
  abstract static class BitStream {
    abstract Queue<Boolean> q();

    boolean isEmpty() {
      return q().isEmpty();
    }

    int nextValue(int bits) {
      int v = 0;
      for (int i = 0; i < bits; ++i) {
        v *= 2;
        v += q().poll() ? 1 : 0;
      }
      return v;
    }

    BitStream nextStream(int bits) {
      Queue<Boolean> q = new LinkedList<>();
      for (int i = 0; i < bits; ++i) {
        q.add(q().poll());
      }
      return of(q);
    }

    Packet decode() {
      int version = nextValue(3);
      int type = nextValue(3);
      switch (type) {
        case 4:
          return Literal.of(version, type, decodeLiteral());
        default:
          if (nextValue(1) == 0) {
            return Operation.of(version, type, decodeOperation0());
          } else {
            return Operation.of(version, type, decodeOperation1());
          }
      }
    }

    private ImmutableList<Integer> decodeLiteral() {
      ImmutableList.Builder<Integer> builder = ImmutableList.builder();
      while (true) {
        int val = nextValue(5);
        if (val >= (1 << 4)) {
          builder.add(val - (1 << 4));
        } else {
          builder.add(val);
          break;
        }
      }
      return builder.build();
    }

    private ImmutableList<Packet> decodeOperation0() {
      int length = nextValue(15);
      BitStream p = nextStream(length);
      ImmutableList.Builder<Packet> builder = ImmutableList.builder();
      while (!p.isEmpty()) {
        builder.add(p.decode());
      }
      return builder.build();
    }

    private ImmutableList<Packet> decodeOperation1() {
      int number = nextValue(11);
      ImmutableList.Builder<Packet> builder = ImmutableList.builder();
      for (int i = 0; i < number; ++i) {
        builder.add(decode());
      }
      return builder.build();
    }

    static BitStream of(Queue<Boolean> q) {
      return new AutoValue_PacketDecoder_BitStream(q);
    }

    static BitStream parse(String v) {
      Queue<Boolean> q = new LinkedList<>();
      for (char c : v.toCharArray()) {
        int d = Integer.decode("0x0" + c);
        for (int i = 3; i >= 0; --i) {
          q.add(((d & (1 << i)) == (1 << i)));
        }
      }
      return of(q);
    }
  }

  interface Packet {
    int version();

    int type();

    long versionScore();

    long evaluate();
  }

  @AutoValue
  abstract static class Literal implements Packet {
    abstract ImmutableList<Integer> value();

    @Override
    public long versionScore() {
      return version();
    }

    @Override
    public long evaluate() {
      switch (type()) {
        case 4: // Literal
          long agg = 0;
          for (int v : value()) {
            agg *= 16;
            agg += v;
          }
          return agg;
        default:
          throw new AssertionError();
      }
    }

    static Literal of(int version, int type, ImmutableList<Integer> value) {
      return new AutoValue_PacketDecoder_Literal(version, type, value);
    }
  }

  @AutoValue
  abstract static class Operation implements Packet {
    abstract ImmutableList<Packet> operands();

    @Override
    public long versionScore() {
      return version() + operands().stream().mapToLong(Packet::versionScore).sum();
    }

    @Override
    public long evaluate() {
      switch (type()) {
        case 0: // Sum
          return operands().stream().mapToLong(Packet::evaluate).sum();
        case 1: // Product
          return operands().stream().mapToLong(Packet::evaluate).reduce((a, b) -> a * b).orElse(0);
        case 2: // Min
          return operands().stream().mapToLong(Packet::evaluate).min().orElse(0);
        case 3: // Max
          return operands().stream().mapToLong(Packet::evaluate).max().orElse(0);
        case 5: // Greater than
          return operands().get(0).evaluate() > operands().get(1).evaluate() ? 1 : 0;
        case 6: // Less than
          return operands().get(0).evaluate() < operands().get(1).evaluate() ? 1 : 0;
        case 7: // Equal to
          return operands().get(0).evaluate() == operands().get(1).evaluate() ? 1 : 0;
        default:
          throw new AssertionError();
      }
    }

    static Operation of(int version, int type, ImmutableList<Packet> operands) {
      return new AutoValue_PacketDecoder_Operation(version, type, operands);
    }
  }

  private static long part1(String v) {
    return BitStream.parse(v).decode().versionScore();
  }

  private static long part2(String v) {
    return BitStream.parse(v).decode().evaluate();
  }

  public static void main(String[] args) {
    String input = readInput(args[0]);
    System.out.println(part1(input));
    System.out.println(part2(input));
  }
}
