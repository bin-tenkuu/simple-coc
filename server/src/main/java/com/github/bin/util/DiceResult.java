package com.github.bin.util;

import lombok.Getter;
import lombok.val;

import java.util.Arrays;
import java.util.Random;

/**
 * @author bin
 * @since 2023/08/22
 */
@Getter
public class DiceResult {
    private final int size;
    private final int max;
    private long sum;
    private final int[] list;

    public DiceResult(int size, int max) {
        this.size = Math.max(Math.min(size, 999), 1);
        this.max = Math.max(Math.min(max, 999_999_999), 1);
        this.sum = this.size;
        this.list = new int[this.size];
        Arrays.fill(this.list, 1);
    }

    public DiceResult(long sum, int[] list, int max) {
        this.size = list.length;
        this.max = Math.max(max, 1);
        this.sum = sum;
        this.list = list;
    }

    public String getOrigin() {
        return String.format("%dd%d", this.size, this.max);
    }

    public void dice() {
        long sum = 0;
        val random = new Random();
        val max = this.max + 1;
        for (int i = 0; i < list.length; i++) {
            val it = random.nextInt(1, max);
            list[i] = it;
            sum += it;
        }
        this.sum = sum;
    }

    public DiceResult plus(DiceResult dice) {
        if (this.max != dice.max) {
            throw new IllegalArgumentException("max must be equal");
        }
        int max = this.max;
        int[] list = new int[this.size + dice.size];
        System.arraycopy(this.list, 0, list, 0, this.size);
        System.arraycopy(dice.list, 0, list, this.size, dice.size);
        return new DiceResult(sum + dice.sum, list, max);
    }

    @Override
    public String toString() {
        return "<" + getOrigin() + ">";
    }
}
