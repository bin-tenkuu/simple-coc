package com.github.bin.command;

import com.github.bin.service.CocService;
import com.github.bin.service.RoomConfig;
import com.github.bin.util.CacheMap;
import com.github.bin.util.DiceResult;
import lombok.val;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author bin
 * @since 2023/08/23
 */
public interface CocSbiScope {

    CacheMap<Long, DiceResult> CACHE = new CacheMap<>();

    private static int toIntOr(String str, int def) {
        if (str == null || str.isEmpty()) {
            return def;
        }
        try {
            return Integer.parseInt(str);
        } catch (NumberFormatException e) {
            return def;
        }
    }

    private static String sbiResult(int[] list) {
        if (list.length < 3) {
            return "数量过少";
        }
        val ints = Set.of(list[0], list[1], list[2]).stream()
                .mapToInt(Integer::intValue)
                .sorted()
                .toArray();
        if (ints.length == 1) {
            return "大失败";
        }
        if (ints.length == 3 && Arrays.stream(ints).sum() == 6) {
            return "大成功，成功度 " + Arrays.stream(list)
                    .filter(it -> it == 1)
                    .count();
        }
        val intArray = Arrays.stream(list).distinct().sorted().toArray();
        val arr = new int[]{intArray[0], 0};
        for (val i : intArray) {
            if (i - arr[0] == 1) {
                if (arr[1] == 1) {
                    return "成功，成功度 " + Arrays.stream(list)
                            .filter(it -> it == 1)
                            .count();
                } else {
                    arr[1] = 1;
                }
            } else {
                arr[1] = 0;
            }
            arr[0] = i;
        }
        return "失败";
    }

    // s
    @Component
    class S extends Command.Regex {
        public S() {
            super("^s\\t*(?<num>\\d*)d(?<max>\\d*)", Pattern.CASE_INSENSITIVE);
        }

        @Override
        protected boolean handler(RoomConfig roomConfig, String id, Matcher matcher) {
            val num = Math.max(toIntOr(matcher.group("num"), 0), 3);
            val max = toIntOr(matcher.group("max"), 0);
            val diceResult = new DiceResult(num, max);
            if (!CocService.cheater) {
                diceResult.dice();
            }
            val roleId = roomConfig.getRole(id);
            CACHE.set(roleId, diceResult);
            val msg = String.format("%s：[%s]（%s）",
                    diceResult.getOrigin(), Arrays.toString(diceResult.getList()), sbiResult(diceResult.getList())
            );
            roomConfig.sendAsBot(msg);
            return true;
        }
    }

    // sp
    @Component
    class Sp extends Command.Regex {
        public Sp() {
            super("^sp\\t*(?<num>\\d*)");
        }

        @Override
        protected boolean handler(RoomConfig roomConfig, String id, Matcher matcher) {
            val roleId = roomConfig.getRole(id);
            val num = toIntOr(matcher.group("num"), 1);
            var diceResult = CACHE.get(roleId);
            if (diceResult == null) {
                roomConfig.sendAsBot("10分钟之内没有投任何骰子");
                return true;
            }
            val dice = new DiceResult(num, diceResult.getMax());
            if (!CocService.cheater) {
                dice.dice();
            }
            diceResult = diceResult.plus(dice);
            CACHE.set(roleId, diceResult);
            val msg = String.format("%s：[%s]=%s\n[%s]（%s）",
                    dice.getOrigin(), Arrays.toString(dice.getList()), dice.getSum(),
                    Arrays.toString(diceResult.getList()), sbiResult(diceResult.getList()));
            roomConfig.sendAsBot(msg);
            return true;
        }
    }
}
