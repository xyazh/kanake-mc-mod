package com.xyazh.kanake.magic.command;

public class CommandNop extends OrderCommand {
    public final int time;
    public CommandNop(int order, boolean need_sync, int time) {
        super(order, need_sync);
        this.time = time;
    }
}
