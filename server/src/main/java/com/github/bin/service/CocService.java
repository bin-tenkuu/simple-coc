package com.github.bin.service;

import com.github.bin.util.CacheMap;
import com.github.bin.util.DiceResult;
import lombok.RequiredArgsConstructor;
import lombok.val;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

import static com.github.bin.util.NumberUtil.toIntOr;

/**
 * @author bin
 * @since 2023/08/22
 */
public class CocService {
    private static final Pattern SPLIT_DICE_REGEX = Pattern.compile("(?=[+\\-*])");

    public final CacheMap<Integer, DiceResult> cache = new CacheMap<>();
    public boolean cheater = false;

    public String dice(String str, Integer qq) {
        List<Calc> handles = new ArrayList<>();
        for (String string : SPLIT_DICE_REGEX.split(str)) {
            Calc castString = castString(string, cheater);
            handles.add(castString);
        }
        if (handles.size() == 1) {
            val calc = handles.getFirst();
            if (calc.list == null) {
                return String.format("%s%s=%s", calc.op, calc.origin, calc.sum);
            } else {
                cache.set(qq, new DiceResult(calc.sum, calc.list, calc.max));
                return String.format("%s：%s=%s", calc.origin, Arrays.toString(calc.list), calc.sum);
            }
        }
        val result = new StringBuilder();
        for (Calc handle : handles) {
            if (handle.list != null) {
                result.append(handle.origin).append("：")
                        .append(Arrays.toString(handle.list)).append("=")
                        .append(handle.sum).append("\n");
            }
        }
        for (Calc it : handles) {
            result.append(it.op).append(it.origin);
        }
        result.append("=").append(calculate(handles));
        return result.toString();
    }

    private static long calculate(List<Calc> list) {
        long[] accumulator = {0, 1};
        for (Calc c : list) {
            accumulator = c.op.invoke(accumulator, c.sum);
        }
        return accumulator[0];
    }

    private static final Pattern CAST_STRING_REGEX = Pattern.compile(
            "^(?<op>[+\\-*])?(?<num>\\d+)?(?:d(?<max>\\d+))?$", Pattern.CASE_INSENSITIVE);

    private static Calc castString(String origin, boolean cheater) {
        val matcher = CAST_STRING_REGEX.matcher(origin);
        if (!matcher.find()) {
            return new Calc(Operator.Add, 0, null, origin, 0);
        }
        val num = toIntOr(matcher.group("num"), 1);
        val opStr = matcher.group("op");
        final Operator op = opStr == null ? Operator.Add : switch (opStr) {
            case "-" -> Operator.Sub;
            case "*" -> Operator.Mul;
            default -> Operator.Add;
        };
        val max = toIntOr(matcher.group("max"), 0);
        if (max == 0) {
            return new Calc(op, num, null, Integer.toString(num), 0);
        }
        val dices = new DiceResult(num, max);
        if (!cheater) {
            dices.dice();
        }
        return new Calc(op, dices.getSum(), dices.getList(), dices.getOrigin(), dices.getMax());
    }

    @RequiredArgsConstructor
    private static class Calc {
        private final Operator op;
        private final long sum;
        private final int[] list;
        private final String origin;
        private final int max;
    }

    @RequiredArgsConstructor
    public enum Operator {
        Add("+") {
            @Override
            public long[] invoke(long[] sc, long num) {
                return new long[]{(sc[0] + num * sc[1]), 1};
            }
        },
        Sub("-") {
            @Override
            public long[] invoke(long[] sc, long num) {
                return new long[]{(sc[0] - num * sc[1]), 1};
            }
        },
        Mul("*") {
            @Override
            public long[] invoke(long[] sc, long num) {
                return new long[]{sc[0], (sc[1] * num)};
            }
        },
        ;
        private final String s;

        @Override
        public String toString() {
            return s;
        }

        abstract long[] invoke(long[] sc, long num);
    }

}
