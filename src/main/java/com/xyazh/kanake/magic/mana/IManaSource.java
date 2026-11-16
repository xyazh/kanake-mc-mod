package com.xyazh.kanake.magic.mana;

public interface IManaSource {
    boolean isValid();

    double getMana();

    /**
     * @return 是否成功扣除足量魔力；失败时不应扣除任何魔力。
     */
    boolean consume(double amount);
}
