package com.github.bin.command;

import com.github.bin.entity.master.RoomRole;
import com.github.bin.service.CocService;
import com.github.bin.service.RoomConfig;
import com.github.bin.util.DiceResult;
import lombok.val;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.stream.Collectors;

import static com.github.bin.util.NumberUtil.toIntOr;

/**
 * @author bin
 * @since 2023/08/23
 */
public interface CocScope {
    // d "掷骰子，附带简单计算（+-*），形如 '9#9d9+9'"
    @Component
    class D extends Command.Regex {
        public D() {
            super("^d\\s*(?:(?<times>\\d+)#)?(?<dice>[+\\-*d\\d]+)");
        }

        @Override
        protected boolean handler(RoomConfig roomConfig, String id, Matcher matcher, RoomRole roomRole) {
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
            val str = Arrays.stream(new String[times])
                    .map(it -> CocService.dice(dice, roomRole.getId()))
                    .collect(Collectors.joining("\n"));
            roomConfig.sendAsBot(str);
            return true;
        }
    }

    // dall1
    @Component
    class Dall1 extends Command.Regex {
        public Dall1() {
            super("^dall1");
        }

        @Override
        protected boolean handler(RoomConfig roomConfig, String id, Matcher matcher, RoomRole roomRole) {
            CocService.cheater = !CocService.cheater;
            roomConfig.sendAsBot("全1" + (CocService.cheater ? "开" : "关"));
            return true;
        }
    }

    // dp
    @Component
    class Dp extends Command.Regex {
        public Dp() {
            super("^dp\\s*(?<num>\\d*)");
        }

        @Override
        protected boolean handler(RoomConfig roomConfig, String id, Matcher matcher, RoomRole roomRole) {
            val num = toIntOr(matcher.group("num"), 1);
            var cacheResult = CocService.CACHE.get(roomRole.getId());
            if (cacheResult == null) {
                roomConfig.sendAsBot("10分钟之内没有投任何骰子");
                return true;
            }
            val dice = new DiceResult(num, cacheResult.getMax());
            if (!CocService.cheater) {
                dice.dice();
            }
            cacheResult = cacheResult.plus(dice);
            CocService.CACHE.set(roomRole.getId(), cacheResult);
            val msg = String.format("%s：%s=%s\n%s",
                    dice.getOrigin(), Arrays.toString(dice.getList()), dice.getSum(),
                    Arrays.toString(cacheResult.getList()));
            roomConfig.sendAsBot(msg);
            return true;
        }
    }

    // r
    @Component
    class R extends Command.Regex {
        public R() {
            super("^r\\s*(?<num>\\d*)d(?<max>\\d+)\\s*(?<type>\\S*)");
        }

        @Override
        protected boolean handler(RoomConfig roomConfig, String id, Matcher matcher, RoomRole roomRole) {
            val num = toIntOr(matcher.group("num"), 1);
            val max = toIntOr(matcher.group("max"), 1);
            val type = matcher.group("type");
            val dice = new DiceResult(num, max);
            if (!CocService.cheater && max > 1) {
                dice.dice();
            }
            val role = roomConfig.getRole(id);
            val roleName = role == null ? "" : role.getName();
            val msg = num == 1 ?
                    String.format("%s = %s", dice.getOrigin(), dice.getSum()) :
                    String.format("%s = %s = %s", dice.getOrigin(), Arrays.toString(dice.getList()), dice.getSum());
            roomConfig.sendAsBot(roleName + "进行" + type + "检定：\n" + msg);
            return true;
        }
    }
}
