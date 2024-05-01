package com.xyazh.kanake.magic.command;

import com.xyazh.kanake.magic.Magic;
import net.minecraft.entity.Entity;

public class Command {
    public int order;
    public boolean need_sync;
    public Command(int order, boolean need_sync)
    {
        this.order = order;
        this.need_sync = need_sync;
        Magic.ORDER_MAP.put(order,this);
    }

    public void execute(Entity entity)
    {
    }
}
