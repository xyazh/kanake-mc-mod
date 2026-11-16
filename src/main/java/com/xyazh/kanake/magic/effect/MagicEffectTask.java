package com.xyazh.kanake.magic.effect;

/**
 * 非阻塞攻击效果的运行时任务。攻击纹章只负责把任务加入实体，之后每 tick 由实体结算。
 */
public class MagicEffectTask {
    public static final int TYPE_DEATH_CROSS = 1;

    public final int type;
    public int ticksLeft;
    public final double damage;
    public final double halfLength;
    public final double halfWidth;
    public final double manaCostPerTick;
    public double clientParticleBudget = 0;

    public MagicEffectTask(int type, int duration, double damage, double halfLength, double halfWidth, double manaCostPerTick) {
        this.type = type;
        this.ticksLeft = duration;
        this.damage = damage;
        this.halfLength = halfLength;
        this.halfWidth = halfWidth;
        this.manaCostPerTick = manaCostPerTick;
    }
}
