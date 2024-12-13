package com.github.bin.command;

import com.github.bin.entity.master.RoomRole;
import com.github.bin.service.RoomConfig;
import com.github.bin.util.CacheMap;
import com.github.bin.util.DiceResult;
import lombok.val;

import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;

import static com.github.bin.util.NumberUtil.toIntOr;

/**
 * @author bin
 * @since 2023/08/23
 */
public interface CocSbiScope {
    static List<Command> getCommands() {
        return List.of(
                new S(),
                new Sp()
        );
    }

    CacheMap<Integer, DiceResult> CACHE = new CacheMap<>();

    private static String sbiResult(int[] list) {
        if (list.length < 3) {
            return "数量过少";
        }
        val ints = Arrays.stream(new int[]{list[0], list[1], list[2]})
                .distinct()
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
    class S extends Command.Regex {
        public S() {
            super("^s\\s*(?<num>\\d*)d(?<max>\\d*)");
        }

        @Override
        protected boolean handler(RoomConfig roomConfig, String id, Matcher matcher, RoomRole roomRole) {
            val num = Math.max(toIntOr(matcher.group("num"), 0), 3);
            val max = toIntOr(matcher.group("max"), 0);
            val diceResult = new DiceResult(num, max);
            if (!roomConfig.cocService.cheater) {
                diceResult.dice();
            }
            CACHE.set(roomRole.getId(), diceResult);
            val msg = String.format("%s：%s（%s）",
                    diceResult.getOrigin(), Arrays.toString(diceResult.getList()), sbiResult(diceResult.getList())
            );
            roomConfig.sendAsBot(msg);
            return true;
        }
    }

    // sp
    class Sp extends Command.Regex {
        public Sp() {
            super("^sp\\s*(?<num>\\d*)");
        }

        @Override
        protected boolean handler(RoomConfig roomConfig, String id, Matcher matcher, RoomRole roomRole) {
            val num = toIntOr(matcher.group("num"), 1);
            var diceResult = CACHE.get(roomRole.getId());
            if (diceResult == null) {
                roomConfig.sendAsBot("10分钟之内没有投任何骰子");
                return true;
            }
            val dice = new DiceResult(num, diceResult.getMax());
            if (!roomConfig.cocService.cheater) {
                dice.dice();
            }
            diceResult = diceResult.plus(dice);
            CACHE.set(roomRole.getId(), diceResult);
            val msg = String.format("%s：%s=%s\n%s（%s）",
                    dice.getOrigin(), Arrays.toString(dice.getList()), dice.getSum(),
                    Arrays.toString(diceResult.getList()), sbiResult(diceResult.getList()));
            roomConfig.sendAsBot(msg);
            return true;
        }
    }

    static void main(String[] args) {
        val gbk = Charset.forName("GBK");
        val result = new DiceResult(23, 6);
        int success = 0, bigSuccess = 0, fail = 0, bigFail = 0;
        for (int i = 0; i < 100; i++) {
            result.dice();
            val msg = sbiResult(result.getList());
            if (msg.startsWith("大")) {
                if (msg.charAt(1) == '成') {
                    bigSuccess++;
                } else {
                    bigFail++;
                }
            } else {
                if (msg.charAt(0) == '成') {
                    success++;
                } else {
                    fail++;
                }
            }
            val format = String.format("%s（%s）", Arrays.toString(result.getList()), msg);
            System.out.println(new String(format.getBytes(), gbk));
        }
        System.out.println("bigSuccess: " + bigSuccess);
        System.out.println("success: " + success);
        System.out.println("fail: " + fail);
        System.out.println("bigFail: " + bigFail);
    }
}
