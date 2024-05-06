package com.xyazh.kanake.magic;

import com.xyazh.kanake.magic.command.*;

import java.util.HashMap;

public class Magic {
    public static final HashMap<Integer, Command> ORDER_MAP = new HashMap<>();
    
    //在原地生成一个魔法球体，并复制指令
    public static final Command SPAWN = new CommandSpawn(0,false,true);
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
    public static final Command DEAD = new CommandDead(17,false);
    //使魔法温度升高
    public static final Command HEAT = new CommandTemperature(18,true,100);
    //使魔法温度降低
    public static final Command COOL = new CommandTemperature(19,true,-100);
    //下一条指令在魔法结束时执行效果
    public static final Command CALLBACK = new OrderCommand(20,false);
    //使魔法停止
    public static final Command STOP = new CommandStop(21,true,true);
    //使魔法不受重力
    public static final Command NO_GRAVITY = new CommandNoGravity(22,true);
    //设置魔法为子魔法
    public static final Command SET_SUB = new CommandSetSub(23,true,true);
    //设置魔法不为子魔法
    public static final Command SET_NOT_SUB = new CommandSetSub(24,true,false);
    //生成朝向4个方向的魔法
    public static final Command SPAWN_CROSS = new CommandCrossSpawn(25,false,true);
    //子魔法跳过1个指令
    public static final Command SUB_JMP = new CommandJmp(26,false,true,1);
    //子魔法跳过10个指令
    public static final Command SUB_JMP_10 = new CommandJmp(27,false,true,10);
    //非子魔法跳过1个指令
    public static final Command JMP = new CommandJmp(28,false,false,1);
    //非子魔法跳过10个指令
    public static final Command JMP_10 = new CommandJmp(29,false,false,10);
    //使魔法取消停止
    public static final Command NOT_STOP = new CommandStop(30,true,false);
    //清除后续指令
    public static final Command CLEAR = new OrderCommand(31,true);
}
