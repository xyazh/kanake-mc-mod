package com.xyazh.kanake.magic.command;

public class CommandRandomSpeed extends OrderCommand {
    protected double speed;
    public CommandRandomSpeed(int order, boolean need_sync, double speed) {
        super(order, need_sync);
        this.speed = speed;
    }
}
