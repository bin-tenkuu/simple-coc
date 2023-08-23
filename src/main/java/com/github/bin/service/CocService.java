package com.github.bin.service;

import com.github.bin.util.CacheMap;
import com.github.bin.util.DiceResult;
import lombok.RequiredArgsConstructor;
import lombok.val;

import java.util.Arrays;
import java.util.Random;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * @author bin
 * @since 2023/08/22
 */
public class CocService {
    public static final CacheMap<Long, DiceResult> CACHE = new CacheMap<>();

    private static final Pattern SPLIT_DICE_REGEX = Pattern.compile("(?=[+\\-*])");

    public static boolean cheater = false;

    public static Effects specialEffects = Effects.bug;

    public static String dice(String str, Long qq) {
        val handles = Arrays.stream(SPLIT_DICE_REGEX.split(str)).map(it ->
                castString(it, CocService.cheater)
        ).toArray(Calc[]::new);
        if (handles.length == 1) {
            val calc = handles[0];
            if (calc.list == null) {
                return String.format("%s%s=%s", calc.op, calc.origin, calc.sum);
            } else {
                CACHE.set(qq, new DiceResult(calc.sum, calc.list, calc.max));
                specialEffects.invoke(calc);
                return String.format("%s：[%s]=%s%s", calc.origin, Arrays.toString(calc.list), calc.sum, calc.state);
            }
        }
        val preRet = Arrays.stream(handles)
                .filter(it -> it.list != null)
                .map(it -> String.format("%s：[%s]=%s", it.origin, Arrays.toString(it.list), it.sum))
                .collect(Collectors.joining("\n"));
        val s = Arrays.stream(handles)
                .map(it -> String.format("%s%s", it.op, it.origin))
                .collect(Collectors.joining(""));
        return String.format("%s\n%s=%s", preRet, s, calculate(handles));
    }

    private static long calculate(Calc[] list) {
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

    private static int toIntOr(String v, int or) {
        if (v == null || v.isEmpty()) {
            return or;
        }
        try {
            return Integer.parseInt(v);
        } catch (NumberFormatException e) {
            return or;
        }
    }

    @RequiredArgsConstructor
    private static class Calc {
        private final Operator op;
        private final long sum;
        private final int[] list;
        private final String origin;
        private final int max;
        private String state = "";

        public void setState(String v) {
            state = ("".equals(v)) ? "" : "\n" + v;
        }
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


    @RequiredArgsConstructor
    public enum Effects {
        bug("默认") {
            @Override
            public void invoke(Calc calc) {
            }
        },
        wrf("温柔f") {
            @Override
            public void invoke(Calc calc) {
                val it = calc.list;
                if (it.length > 2 && it[0] == it[1]) {
                    ++it[1];
                    calc.setState("[温柔]");
                }
            }
        },
        cbf("残暴f") {
            @Override
            public void invoke(Calc calc) {
                val it = calc.list;
                if (it.length > 2) {
                    it[1] = it[0];
                    calc.setState("[残暴]");
                }
            }
        },
        ajf("傲娇f") {
            @Override
            public void invoke(Calc calc) {
                if (RANDOM.nextDouble() < 0.5) {
                    wrf.invoke(calc);
                } else {
                    cbf.invoke(calc);
                }
            }
        },
        wr("温柔") {
            @Override
            public void invoke(Calc calc) {
                if (RANDOM.nextDouble() < 0.5) {
                    wrf.invoke(calc);
                } else {
                    bug.invoke(calc);
                }
            }
        },
        cb("残暴") {
            @Override
            public void invoke(Calc calc) {
                if (RANDOM.nextDouble() < 0.5) {
                    cbf.invoke(calc);
                } else {
                    bug.invoke(calc);
                }
            }
        },
        aj("傲娇") {
            @Override
            public void invoke(Calc calc) {
                new Effects[]{wrf, cbf, bug}
                        [RANDOM.nextInt(0, 3)]
                        .invoke(calc);
            }
        },
        ;
        private static final Random RANDOM = new Random();
        private final String state;

        abstract void invoke(Calc calc);
    }
}
