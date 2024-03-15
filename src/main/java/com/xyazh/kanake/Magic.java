package com.xyazh.kanake;

public class Magic {
    //在原地生成一个魔法球体，并复制指令
    public static final int SPAWN = 0;
    //增加一个方向为发射者朝向的小速度
    public static final int SPEED_LOW = 1;
    //增加一个方向为发射者朝向的中速度
    public static final int SPEED_MEDIUM = 2;
    //增加一个方向为发射者朝向的大速度
    public static final int SPEED_HIGH = 3;
    //增加一个方向随机的小速度
    public static final int RANDOM_SPEED_LOW = 4;
    //增加一个方向随机的大速度
    public static final int RANDOM_SPEED_MEDIUM = 5;
    //增加一个方向随机的大速度
    public static final int RANDOM_SPEED_HIGH = 6;
    //停止执行指令20tick，1秒
    public static final int NOP_LOW = 7;
    //停止执行指令80tick，4秒
    public static final int NOP_MEDIUM = 8;
    //停止执行指令320tick，16秒
    public static final int NOP_HIGH = 9;
    //将下面两条指令同时执行
    public static final int CONCURRENT = 10;
    //使魔法小爆炸
    public static final int EXPLODE_SMALL = 11;
    //使魔法中爆炸
    public static final int EXPLODE_MEDIUM = 12;
    //使魔法大爆炸
    public static final int EXPLODE_BIG = 13;
    //使魔法在碰撞时反射
    public static final int REFLEX = 14;
    //使魔法速度反转
    public static final int REVERSE = 15;
    //使魔法停止运动
    public static final int STOP = 16;
    //使魔法不受重力
    public static final int ZERO_GRAVITY = 17;
    //命中时小伤害
    public static final int ON_DAMAGE_LOW = 18;
    //命中时中伤害
    public static final int ON_DAMAGE_MEDIUM = 19;
    //命中时大伤害
    public static final int ON_DAMAGE_HIGH = 20;
    //命中时小爆炸
    public static final int ON_EXPLODE_SMALL= 21;
    //命中时中爆炸
    public static final int ON_EXPLODE_MEDIUM = 22;
    //命中时大爆炸
    public static final int ON_EXPLODE_BIG = 23;
    //命中时点燃
    public static final int ON_FIRE = 24;
    //命中时冻结
    public static final int ON_ICE = 25;
    //命中时火陷阱
    public static final int ON_FIRE_WANA = 26;
    //命中时冰陷阱
    public static final int ON_ICE_WANA = 27;
}
