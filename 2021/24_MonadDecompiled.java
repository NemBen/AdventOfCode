package nemben.aoc.d24;

public class MonadDecompiled {
  private static long decompiled(long[] input) {
    long var1 = (((input[0] + 2) * 26 + input[1] + 16) * 26 + input[2] + 9) * 26 + input[3];
    long var2 = eql(eql(var1 % 26 + -8, input[4]), 0);
    long var3 = ((var1 / 26) * (25 * var2 + 1) + (input[4] + 1) * var2) * 26 + input[5] + 12;
    long var4 = eql(eql(var3 % 26 + -16, input[6]), 0);
    long var5 = (var3 / 26) * (25 * var4 + 1) + (input[6] + 6) * var4;
    long var6 = eql(eql(var5 % 26 + -4, input[7]), 0);
    long var7 = ((var5 / 26) * (25 * var6 + 1) + (input[7] + 6) * var6) * 26 + input[8] + 3;
    long var8 = eql(eql(var7 % 26 + -3, input[9]), 0);
    long var9 = ((var7 / 26) * (25 * var8 + 1) + (input[9] + 5) * var8) * 26 + input[10] + 9;
    long var10 = eql(eql(var9 % 26 + -7, input[11]), 0);
    long var11 = (var9 / 26) * (25 * var10 + 1) + (input[11] + 3) * var10;
    long var12 = eql(eql(var11 % 26 + -15, input[12]), 0);
    long var13 = (var11 / 26) * (25 * var12 + 1) + (input[12] + 2) * var12;
    long var14 = eql(eql(var13 % 26 + -7, input[13]), 0);
    return (var13 / 26) * (25 * var14 + 1) + (input[13] + 3) * var14;
  }

  private static long eql(long i, long j) {
    return i == j ? 1 : 0;
  }

  private static long refactored(long[] input) {
    long value;
    value = input[0] + 2;
    value = push(value, input[1] + 16);
    value = push(value, input[2] + 9);
    if (input[3] - 8 != input[4]) {
      value = push(value, input[4] + 1);
    }
    if (input[5] - 4 != input[6]) {
      value = push(value, input[6] + 6);
    }
    if (peek(value) - 4 != input[7]) {
      value = push(pop(value), input[7] + 6);
    } else {
      value = pop(value);
    }
    if (input[8] != input[9]) {
      value = push(value, input[9] + 5);
    }
    if (input[10] + 2 != input[11]) {
      value = push(value, input[11] + 3);
    }
    if (peek(value) - 15 != input[12]) {
      value = push(pop(value), input[12] + 2);
    } else {
      value = pop(value);
    }
    if (peek(value) - 7 != input[13]) {
      value = push(pop(value), input[13] + 3);
    } else {
      value = pop(value);
    }
    return value;
  }

  private static long push(long v, long d) {
    return v * 26 + d;
  }

  private static long pop(long v) {
    return v / 26;
  }

  private static long peek(long v) {
    return v % 26;
  }
}
