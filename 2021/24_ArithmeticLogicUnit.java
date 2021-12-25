package nemben.aoc.d24;

import com.google.auto.value.AutoValue;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Range;
import nemben.aoc.InputReader;

import java.util.*;
import java.util.stream.Stream;

import static com.google.common.collect.ImmutableList.toImmutableList;
import static java.util.Comparator.comparingInt;

public class ArithmeticLogicUnit {

  @AutoValue
  abstract static class Instruction {
    abstract String operation();

    abstract ImmutableList<String> operands();

    static Instruction of(String operation, ImmutableList<String> operands) {
      return new AutoValue_ArithmeticLogicUnit_Instruction(operation, operands);
    }
  }

  interface Value {}

  @AutoValue
  abstract static class Ref implements Value {
    abstract int line();

    static Ref of(int line) {
      return new AutoValue_ArithmeticLogicUnit_Ref(line);
    }
  }

  @AutoValue
  abstract static class Const implements Value {
    abstract long value();

    static Const of(long value) {
      return new AutoValue_ArithmeticLogicUnit_Const(value);
    }
  }

  interface Operation {
    int line();
  }

  @AutoValue
  abstract static class Input implements Operation {
    abstract int idx();

    static Input of(int line, int idx) {
      return new AutoValue_ArithmeticLogicUnit_Input(line, idx);
    }
  }

  @AutoValue
  abstract static class Arithmetic implements Operation {
    enum Type {
      ADD,
      MUL,
      DIV,
      MOD,
      EQL;
    }

    abstract Type type();

    abstract Value left();

    abstract Value right();

    static Arithmetic of(int line, Type type, Value left, Value right) {
      return new AutoValue_ArithmeticLogicUnit_Arithmetic(line, type, left, right);
    }
  }

  private static ImmutableList<Instruction> readInput(String name) {
    return InputReader.readSimpleInput(
        name,
        scn -> {
          String line = scn.nextLine();
          String[] parts = line.split("\\s", 2);
          return Instruction.of(parts[0], ImmutableList.copyOf(parts[1].split("\\s")));
        });
  }

  private static String decompile(ImmutableList<Instruction> instructions) {
    return generateFunction(parseInstructions(instructions));
  }

  private static ImmutableList<Operation> parseInstructions(
      ImmutableList<Instruction> instructions) {
    Value w = Const.of(0), x = Const.of(0), y = Const.of(0), z = Const.of(0);
    int inputIdx = 0;
    int line = 0;
    ArrayList<Operation> ops = new ArrayList<>();
    for (Instruction instruction : instructions) {
      ArrayList<Value> operands = new ArrayList<>();
      if (!instruction.operation().equals("inp")) {
        for (String operand : instruction.operands()) {
          switch (operand) {
            case "w":
              operands.add(w);
              break;
            case "x":
              operands.add(x);
              break;
            case "y":
              operands.add(y);
              break;
            case "z":
              operands.add(z);
              break;
            default:
              operands.add(Const.of(Integer.parseInt(operand)));
          }
        }
      }
      Operation op;
      switch (instruction.operation()) {
        case "inp":
          op = Input.of(line, inputIdx++);
          break;
        case "add":
        case "mul":
        case "div":
        case "mod":
        case "eql":
          op =
              Arithmetic.of(
                  line,
                  Arithmetic.Type.valueOf(instruction.operation().toUpperCase()),
                  operands.get(0),
                  operands.get(1));
          break;
        default:
          throw new AssertionError();
      }
      ops.add(op);

      Value value = Ref.of(line);
      switch (instruction.operands().get(0)) {
        case "w":
          w = value;
          break;
        case "x":
          x = value;
          break;
        case "y":
          y = value;
          break;
        case "z":
          z = value;
          break;
        default:
          throw new AssertionError();
      }

      ++line;
    }

    return optimize(filterReachable(ImmutableList.copyOf(ops), z));
  }

  private static ImmutableList<Operation> filterReachable(
      ImmutableList<Operation> operations, Value result) {

    HashSet<Integer> lines = new HashSet<>();
    Queue<Value> q = new LinkedList<>();
    q.add(result);
    while (!q.isEmpty()) {
      Value v = q.poll();
      if (!(v instanceof Ref)) {
        continue;
      }
      Operation op = operations.get(((Ref) v).line());
      if (lines.contains(op.line())) {
        continue;
      }
      lines.add(op.line());

      if (!(op instanceof Arithmetic)) {
        continue;
      }
      Arithmetic arithmetic = (Arithmetic) op;
      q.add(arithmetic.left());
      q.add(arithmetic.right());
    }

    return renumberOperations(
        operations.stream()
            .filter(op -> lines.contains(op.line()))
            .sorted(comparingInt(Operation::line))
            .collect(toImmutableList()));
  }

  private static ImmutableList<Operation> renumberOperations(ImmutableList<Operation> operations) {
    ImmutableList.Builder<Operation> builder = ImmutableList.builder();
    HashMap<Integer, Integer> remap = new HashMap<>();
    for (int i = 0; i < operations.size(); ++i) {
      Operation op = operations.get(i);
      remap.put(op.line(), i);
      if (op instanceof Input) {
        Input input = (Input) op;
        op = Input.of(i, input.idx());
      } else if (op instanceof Arithmetic) {
        Arithmetic a = (Arithmetic) op;
        op =
            Arithmetic.of(
                i,
                a.type(),
                renumberReference(remap, a.left()),
                renumberReference(remap, a.right()));
      }
      builder.add(op);
    }
    return builder.build();
  }

  private static Value renumberReference(HashMap<Integer, Integer> remap, Value left) {
    if (left instanceof Ref) {
      Ref leftRef = (Ref) left;
      left = Ref.of(remap.get(leftRef.line()));
    }
    return left;
  }

  private static ImmutableList<Operation> optimize(ImmutableList<Operation> reachable) {
    ImmutableList<Operation> optimized = reachable;
    for (int i = 0; i < 2; ++i) {
      optimized = optimize(optimized, ArithmeticLogicUnit::reduceBySimplify);
      optimized = optimize(optimized, ArithmeticLogicUnit::reduceByRange);
    }
    return optimized;
  }

  interface Optimizer {
    Optional<Value> getSubstitute(ArrayList<Operation> operations, Operation op);
  }

  private static ImmutableList<Operation> optimize(
      ImmutableList<Operation> operations, Optimizer optimizer) {
    ArrayList<Operation> ops = new ArrayList<>(operations);
    HashMap<Ref, Value> remap = new HashMap<>();
    for (int i = 0; i < ops.size(); ++i) {
      Operation operation = ops.get(i);

      if (operation instanceof Arithmetic) {
        Arithmetic arithmetic = (Arithmetic) operation;
        operation =
            Arithmetic.of(
                arithmetic.line(),
                arithmetic.type(),
                remap.getOrDefault(arithmetic.left(), arithmetic.left()),
                remap.getOrDefault(arithmetic.right(), arithmetic.right()));
        ops.set(i, operation);
      }

      Optional<Value> maybeSubstitute = optimizer.getSubstitute(ops, operation);
      if (maybeSubstitute.isPresent()) {
        remap.put(Ref.of(operation.line()), maybeSubstitute.get());
      }
    }
    return filterReachable(ImmutableList.copyOf(ops), Ref.of(ops.size() - 1));
  }

  private static Optional<Value> reduceBySimplify(ArrayList<Operation> ops, Operation operation) {
    if (!(operation instanceof Arithmetic)) {
      return Optional.empty();
    }
    Arithmetic arithmetic = (Arithmetic) operation;

    switch (arithmetic.type()) {
      case ADD:
        if (arithmetic.left().equals(Const.of(0))) {
          return Optional.of(arithmetic.right());
        } else if (arithmetic.right().equals(Const.of(0))) {
          return Optional.of(arithmetic.left());
        } else if (arithmetic.left() instanceof Const && arithmetic.right() instanceof Const) {
          Const left = (Const) arithmetic.left();
          Const right = (Const) arithmetic.right();
          return Optional.of(Const.of(left.value() + right.value()));
        }
        break;
      case MUL:
        if (arithmetic.left().equals(Const.of(0)) || arithmetic.right().equals(Const.of(0))) {
          return Optional.of(Const.of(0));
        } else if (arithmetic.left().equals(Const.of(1))) {
          return Optional.of(arithmetic.right());
        } else if (arithmetic.right().equals(Const.of(1))) {
          return Optional.of(arithmetic.left());
        } else if (arithmetic.left() instanceof Const && arithmetic.right() instanceof Const) {
          Const left = (Const) arithmetic.left();
          Const right = (Const) arithmetic.right();
          return Optional.of(Const.of(left.value() * right.value()));
        }
        break;
      case DIV:
        if (arithmetic.right().equals(Const.of(1))) {
          return Optional.of(arithmetic.left());
        } else if (arithmetic.left() instanceof Const && arithmetic.right() instanceof Const) {
          Const left = (Const) arithmetic.left();
          Const right = (Const) arithmetic.right();
          return Optional.of(Const.of(left.value() / right.value()));
        }
        break;
      case MOD:
        if (arithmetic.left().equals(Const.of(0))) {
          return Optional.of(Const.of(0));
        } else if (arithmetic.left() instanceof Const && arithmetic.right() instanceof Const) {
          Const left = (Const) arithmetic.left();
          Const right = (Const) arithmetic.right();
          return Optional.of(Const.of(left.value() % right.value()));
        }
        break;
      case EQL:
        if (arithmetic.right().equals(arithmetic.left())) {
          return Optional.of(Const.of(1));
        } else if (arithmetic.left() instanceof Const && arithmetic.right() instanceof Const) {
          Const left = (Const) arithmetic.left();
          Const right = (Const) arithmetic.right();
          return Optional.of(Const.of(left.value() == right.value() ? 1 : 0));
        }
        break;
    }
    return Optional.empty();
  }

  private static Optional<Value> reduceByRange(ArrayList<Operation> ops, Operation operation) {
    Optional<Value> maybeSubstitute = Optional.empty();
    Range<Long> range = getOperationRange(ops, operation);
    if (range.lowerEndpoint() == range.upperEndpoint()) {
      maybeSubstitute = Optional.of(Const.of(range.lowerEndpoint()));
    }
    return maybeSubstitute;
  }

  private static Range<Long> getOperationRange(ArrayList<Operation> operations, Operation op) {
    if (op instanceof Input) {
      return Range.closed(1L, 9L);
    }

    if (op instanceof Arithmetic) {
      Arithmetic arithmetic = (Arithmetic) op;
      switch (arithmetic.type()) {
        case ADD:
          {
            Range<Long> lR = getValueRange(operations, arithmetic.left());
            Range<Long> rR = getValueRange(operations, arithmetic.right());
            long c1 = lR.lowerEndpoint() + rR.lowerEndpoint();
            long c2 = lR.upperEndpoint() + rR.upperEndpoint();
            return Range.closed(c1, c2);
          }
        case MUL:
          {
            Range<Long> lR = getValueRange(operations, arithmetic.left());
            Range<Long> rR = getValueRange(operations, arithmetic.right());
            long c1 = lR.lowerEndpoint() * rR.lowerEndpoint();
            long c2 = lR.upperEndpoint() * rR.upperEndpoint();
            return Range.closed(Math.min(c1, c2), Math.max(c1, c2));
          }
        case DIV:
          {
            // Only div is by 26.
            Range<Long> lR = getValueRange(operations, arithmetic.left());
            Range<Long> rR = getValueRange(operations, arithmetic.right());
            long c1 = lR.lowerEndpoint() / rR.upperEndpoint();
            long c2 = lR.upperEndpoint() / rR.lowerEndpoint();
            return Range.closed(c1, c2);
          }
        case MOD:
          {
            Range<Long> rR = getValueRange(operations, arithmetic.right());
            return Range.closed(0L, Math.max(0L, rR.upperEndpoint()));
          }
        case EQL:
          {
            Range<Long> lR = getValueRange(operations, arithmetic.left());
            Range<Long> rR = getValueRange(operations, arithmetic.right());
            if (!lR.isConnected(rR) || lR.intersection(rR).isEmpty()) {
              return Range.closed(0L, 0L);
            } else {
              return Range.closed(0L, 1L);
            }
          }
        default:
          throw new AssertionError();
      }
    }

    throw new AssertionError();
  }

  private static Range<Long> getValueRange(ArrayList<Operation> operations, Value left) {
    if (left instanceof Const) {
      Const c = (Const) left;
      return Range.closed(c.value(), c.value());
    } else if (left instanceof Ref) {
      Ref r = (Ref) left;
      return getOperationRange(operations, operations.get(r.line()));
    } else {
      throw new AssertionError();
    }
  }

  private static String generateFunction(ImmutableList<Operation> operations) {
    ImmutableList<String> code = generateLines(operations);
    StringBuilder builder = new StringBuilder();
    builder.append("private static long decompiled(long[] input) {\n");
    for (String c : code) {
      builder.append("  ").append(c).append("\n");
    }
    builder.append("}");
    return builder.toString();
  }

  private static ImmutableList<String> generateLines(ImmutableList<Operation> operations) {
    ImmutableList.Builder<String> builder = ImmutableList.builder();
    ImmutableMap<Integer, Integer> statements = findStatements(operations);
    boolean[] done = new boolean[operations.size()];
    for (int i = operations.size() - 1; i >= 0; --i) {
      if (done[i]) {
        continue;
      }
      builder.add(
          String.format(
              "%s %s;",
              (i == operations.size() - 1
                  ? "return"
                  : String.format("long var%s =", statements.get(i))),
              generateOperation(operations, i, done, statements, false, Optional.empty())));
    }
    return builder.build().reverse();
  }

  private static ImmutableMap<Integer, Integer> findStatements(
      ImmutableList<Operation> operations) {
    ImmutableList<Integer> references =
        operations.stream()
            .filter(op -> op instanceof Arithmetic)
            .map(op -> (Arithmetic) op)
            .flatMap(arithmetic -> Stream.of(arithmetic.left(), arithmetic.right()))
            .filter(value -> value instanceof Ref)
            .map(value -> (Ref) value)
            .map(ref -> ref.line())
            .collect(toImmutableList());
    HashSet<Integer> visited = new HashSet<>();
    HashSet<Integer> repeated = new HashSet<>();
    for (int ref : references) {
      if (visited.contains(ref)) {
        repeated.add(ref);
      } else {
        visited.add(ref);
      }
    }
    operations.stream()
        .filter(op -> op instanceof Input)
        .map(op -> (Input) op)
        .map(input -> input.line())
        .forEach(r -> repeated.remove(r));

    ImmutableList<Integer> list = repeated.stream().sorted().collect(toImmutableList());
    ImmutableMap.Builder<Integer, Integer> builder = ImmutableMap.builder();
    for (int i = 0; i < list.size(); ++i) {
      builder.put(list.get(i), i + 1);
    }
    return builder.build();
  }

  private static String generateOperation(
      ImmutableList<Operation> ops,
      int opIdx,
      boolean[] done,
      ImmutableMap<Integer, Integer> statements,
      boolean par,
      Optional<Arithmetic.Type> parentType) {
    done[opIdx] = true;
    Operation op = ops.get(opIdx);
    if (op instanceof Arithmetic) {
      Arithmetic arithmetic = (Arithmetic) op;

      Optional<Arithmetic.Type> type = Optional.of(arithmetic.type());
      if (arithmetic.type() != Arithmetic.Type.EQL) {
        String operation;
        switch (arithmetic.type()) {
          case ADD:
            operation = "+";
            break;
          case MUL:
            operation = "*";
            break;
          case DIV:
            operation = "/";
            break;
          case MOD:
            operation = "%";
            break;
          default:
            throw new AssertionError();
        }
        boolean needsParentheses =
            par && parentType.filter(pType -> pType != Arithmetic.Type.ADD).isPresent();
        return String.format(
            "%s%s %s %s%s",
            needsParentheses ? "(" : "",
            generateValue(ops, done, statements, arithmetic.left(), true, type),
            operation,
            generateValue(ops, done, statements, arithmetic.right(), true, type),
            (needsParentheses ? ")" : ""));
      } else {
        return String.format(
            "%s(%s, %s)",
            arithmetic.type().toString().toLowerCase(),
            generateValue(ops, done, statements, arithmetic.left(), false, type),
            generateValue(ops, done, statements, arithmetic.right(), false, type));
      }
    } else if (op instanceof Input) {
      Input input = (Input) op;
      return "input[" + input.idx() + "]";
    } else {
      throw new AssertionError();
    }
  }

  private static String generateValue(
      ImmutableList<Operation> operations,
      boolean[] used,
      ImmutableMap<Integer, Integer> statements,
      Value value,
      boolean parentheses,
      Optional<Arithmetic.Type> parentType) {
    if (value instanceof Const) {
      Const c = (Const) value;
      return Long.toString(c.value());
    } else if (value instanceof Ref) {
      Ref r = (Ref) value;
      Optional<Integer> statementIdx = Optional.ofNullable(statements.get(r.line()));
      return statementIdx.isPresent()
          ? "var" + statementIdx.get()
          : generateOperation(operations, r.line(), used, statements, parentheses, parentType);
    } else {
      throw new AssertionError();
    }
  }

  public static void main(String[] args) {
    ImmutableList<Instruction> input = readInput(args[0]);
    System.out.println(input);
    System.out.println(decompile(input));
  }
}
