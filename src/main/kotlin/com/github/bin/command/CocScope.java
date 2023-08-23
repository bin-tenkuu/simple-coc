package com.github.bin.command;

import com.github.bin.service.CocService;
import com.github.bin.service.RoomConfig;
import com.github.bin.util.DiceResult;
import lombok.val;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static com.github.bin.service.HisMsgService.sendAsBot;

/**
 * @author bin
 * @since 2023/08/23
 */
public interface CocScope {
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

    // d "掷骰子，附带简单计算（+-*），形如 '9#9d9+9'"
    @Component
    class D extends Command.Regex {
        public D() {
            super(Pattern.compile("^d\\t*(?:(?<times>\\d+)#)?(?<dice>[+\\-*d\\d]+)", Pattern.CASE_INSENSITIVE));
        }

        @Override
        protected boolean handler(RoomConfig roomConfig, String id, Matcher matcher) {
            val times = toIntOr(matcher.group("times"), 1);
            if (times < 1) {
                roomConfig.sendAsBot("0");
                return true;
            }
            var dice = matcher.group("dice");
            if (dice == null) {
                roomConfig.sendAsBot("0" + ", 0".repeat(times - 1));
                return false;
            }
            val roleId = roomConfig.getRole(id);
            val str = Arrays.stream(new String[times])
                    .map(it -> CocService.dice(dice, roleId))
                    .collect(Collectors.joining("\n"));
            roomConfig.sendAsBot(str);
            return true;
        }
    }

    // dall1
    @Component
    class Dall1 extends Command.Simple {
        public Dall1() {
            super("dall1");
        }

        @Override
        protected boolean handler(RoomConfig roomConfig, String id, String msg) {
            CocService.cheater = !CocService.cheater;
            sendAsBot(roomConfig, "全1" + (CocService.cheater ? "开" : "关"));
            return true;
        }
    }

    // dp
    @Component
    class Dp extends Command.Regex {
        public Dp() {
            super(Pattern.compile("^dp\\t*(?<num>\\d*)"));
        }

        @Override
        protected boolean handler(RoomConfig roomConfig, String id, Matcher matcher) {
            val num = toIntOr(matcher.group("num"), 1);
            val roleId = roomConfig.getRole(id);
            var cacheResult = CocService.CACHE.get(roleId);
            if (cacheResult == null) {
                roomConfig.sendAsBot("10分钟之内没有投任何骰子");
                return true;
            }
            val dice = new DiceResult(num, cacheResult.getMax());
            if (!CocService.cheater) {
                dice.dice();
            }
            cacheResult = cacheResult.plus(dice);
            CocService.CACHE.set(roleId, cacheResult);
            val msg = String.format("%s：[%s]=%s\n[%s]",
                    dice.getOrigin(), Arrays.toString(dice.getList()), dice.getSum(),
                    Arrays.toString(cacheResult.getList()));
            sendAsBot(roomConfig, msg);
            return true;
        }
    }

    // r
    @Component
    class R extends Command.Regex {
        public R() {
            super(Pattern.compile("^r\\t*(?<num>\\d*)d(?<max>\\d*)"));
        }

        @Override
        protected boolean handler(RoomConfig roomConfig, String id, Matcher matcher) {
            val num = toIntOr(matcher.group("num"), 1);
            val max = toIntOr(matcher.group("max"), 0);
            val dice = new DiceResult(num, max);
            if (!CocService.cheater) {
                dice.dice();
            }
            val roleId = roomConfig.getRole(id);
            val role = roomConfig.getRoom().getRoles().get(roleId);
            val roleName = role == null ? "" : role.getName();
            val msg = num == 1 ?
                    String.format("%s = %s", dice.getOrigin(), dice.getSum()) :
                    String.format("%s = %s = %s", dice.getOrigin(), Arrays.toString(dice.getList()), dice.getSum());
            roomConfig.sendAsBot(roleName + "进行检定：\n" + msg);
            return true;
        }
    }
}
