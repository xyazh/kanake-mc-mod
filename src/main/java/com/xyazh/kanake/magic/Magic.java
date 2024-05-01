package com.xyazh.kanake.magic;

import com.xyazh.kanake.magic.command.*;

import java.util.HashMap;

public class Magic {
    public static final HashMap<Integer, Command> ORDER_MAP = new HashMap<>();
    
    //在原地生成一个魔法球体，并复制指令
    public static final Command SPAWN = new CommandSpawn(0,false);
    //增加一个方向为发射者朝向的小速度
    public static final Command SPEED_LOW = new CommandSpeed(1,true,0.5);
    //增加一个方向为发射者朝向的中速度
    public static final Command SPEED_MEDIUM = new CommandSpeed(2,true,1.0);
    //增加一个方向为发射者朝向的大速度
    public static final Command SPEED_HIGH = new CommandSpeed(3,true,1.5);
    //增加一个方向随机的小速度
    public static final Command RANDOM_SPEED_LOW = new CommandRandomSpeed(4,true,0.5);
    //增加一个方向随机的中速度
    public static final Command RANDOM_SPEED_MEDIUM = new CommandRandomSpeed(5,true,1.0);
    //增加一个方向随机的大速度
    public static final Command RANDOM_SPEED_HIGH = new CommandRandomSpeed(6,true,1.5);
    //停止执行指令20tick，1秒
    public static final Command NOP_LOW = new CommandNop(7,false,20);
    //停止执行指令80tick，4秒
    public static final Command NOP_MEDIUM = new CommandNop(8,false,80);
    //停止执行指令320tick，16秒
    public static final Command NOP_HIGH = new CommandNop(9,false,320);
    //短时间内执行下面两条指令
    public static final Command CONCURRENT = new OrderCommand(10,false);
    //产生小爆炸
    public static final Command EXPLODE_SMALL = new CommandExplode(11,false,3);
    //产生中爆炸
    public static final Command EXPLODE_MEDIUM = new CommandExplode(12,false,5);
    //产生大爆炸
    public static final Command EXPLODE_BIG = new CommandExplode(13,false,8);
    //使魔法小爆炸
    public static final Command EXPLODE_SMALL_DEAD = new CommandExplodeDead(14,false,4);
    //使魔法中爆炸
    public static final Command EXPLODE_MEDIUM_DEAD = new CommandExplodeDead(15,false,5);
    //使魔法大爆炸
    public static final Command EXPLODE_BIG_DEAD = new CommandExplodeDead(16,false,9);
    //结束魔法
    public static final Command END = new CommandEnd(17,false);
    //使魔法温度升高
    public static final Command HEAT = new CommandTemperature(18,false,100);
    //使魔法温度降低
    public static final Command COOL = new CommandTemperature(19,false,-100);
    //下一条指令在魔法结束时执行效果
    public static final Command CALLBACK = new OrderCommand(20,false);
}
