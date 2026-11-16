package com.xyazh.kanake.magic;

import com.xyazh.kanake.magic.command.*;

import java.util.HashMap;

public class Magic {
    public static final HashMap<Integer, Command> ORDER_MAP = new HashMap<>();
    
    //增加一个方向随机的小速度
    public static final Command RANDOM_SPEED_LOW = new CommandRandomSpeed(4,false,0.5);
    //增加一个方向随机的中速度
    public static final Command RANDOM_SPEED_MEDIUM = new CommandRandomSpeed(5,false,1.0);
    //增加一个方向随机的大速度
    public static final Command RANDOM_SPEED_HIGH = new CommandRandomSpeed(6,false,2.0);
    //短时间内执行下面两条指令
    public static final Command CONCURRENT = new OrderCommand(10,false);
    //使魔法温度升高
    public static final Command HEAT = new CommandTemperature(18,true,100);
    //使魔法温度降低
    public static final Command COOL = new CommandTemperature(19,true,-100);
    //使魔法停止
    public static final Command STOP = new CommandStop(21,true,true);
    //切换魔法重力状态
    public static final Command GRAVITY = new CommandGravity(22,true);
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
    //使魔法反向速度
    public static final Command OPPOSITE_VELOCITY = new CommandOppositeVelocity(32,false);
    //骑乘
    public static final Command RIDE = new CommandRide(36,false);
    //增加重力
    public static final Command ADD_GRAVITY = new CommandSetGravity(37,true,-0.0048f);
    //减少重力
    public static final Command SUB_GRAVITY = new CommandSetGravity(38,true,0.0048f);
    //不受爆炸影响
    public static final Command KEEP_EXPLODE = new CommandKeepExplode(39,false);
    //增加寿命小
    public static final Command ADD_LIFE_LOW = new CommandAddLife(40,false,200);
    //增加寿命中
    public static final Command ADD_LIFE_MEDIUM = new CommandAddLife(41,false,400);
    //增加寿命大
    public static final Command ADD_LIFE_HIGH = new CommandAddLife(42,false,800);
    //觅踪趋向
    public static final Command LOOK_AT_1 = new CommandLookAt(43, true,1);
    //觅踪锁定
    public static final Command LOOK_AT_2 = new CommandLookAt(44, true,2);
    //魔法隐形（XX1 决定隐身是否继承）
    public static final Command HIDDEN = new CommandHidden(45,true);
    //远距离施放
    public static final Command REMOTE_CAST = new CommandReleaseCast(46,true,CommandReleaseCast.Mode.REMOTE);
    //发射施放
    public static final Command LAUNCH_CAST = new CommandReleaseCast(47,true,CommandReleaseCast.Mode.LAUNCH);
    //原位施放
    public static final Command PLACE_CAST = new CommandReleaseCast(48,true,CommandReleaseCast.Mode.PLACE);
    //向下施放
    public static final Command DOWN_CAST = new CommandReleaseCast(49,true,CommandReleaseCast.Mode.DOWN);
    //向上飞行
    public static final Command FLY_UP = new CommandFly(50,true,CommandFly.Mode.UP);
    //平面环绕
    public static final Command ORBIT = new CommandOrbit(51,true);
    //发射（运动）
    public static final Command LAUNCH_MOVE = new CommandFly(52,true,CommandFly.Mode.LAUNCH);
    //向下飞行
    public static final Command FLY_DOWN = new CommandFly(53,true,CommandFly.Mode.DOWN);
    //延迟施法
    public static final Command DELAY_CAST = new CommandDelayCast(54,false);
    //多次执行
    public static final Command MULTI_CAST = new CommandMultiCast(55,false);
    //颜色闪光
    public static final Command COLOR_FLASH = new CommandColorFlash(56,true);
    //不继承
    public static final Command NO_INHERIT = new CommandNoInherit(57,false);
    //终止
    public static final Command TERMINATE = new CommandTerminate(58,true);
    //终结
    public static final Command FINAL = new CommandFinal(59,true);
    //死亡十字
    public static final Command DEATH_CROSS = new CommandDeathCross(60,true);
    //爆炸
    public static final Command EXPLODE_MAGIC = new CommandExplodeMagic(61,true);
    //多次爆炸
    public static final Command MULTI_EXPLODE = new CommandMultiExplode(62,true);
    //点火
    public static final Command IGNITE = new CommandIgnite(63,true);
}
