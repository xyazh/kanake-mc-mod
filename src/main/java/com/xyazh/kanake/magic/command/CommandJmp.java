package com.xyazh.kanake.magic.command;

import com.xyazh.kanake.entity.EntityEmptyMagic;
import net.minecraft.entity.Entity;

public class CommandJmp extends OrderCommand {
    public final int jmp;
    public final boolean isSubMagic;
    public CommandJmp(int order, boolean needSync,boolean isSubMagic, int jmp) {
        super(order, needSync);
        this.jmp = jmp;
        this.isSubMagic = isSubMagic;
    }

    public void execute(Entity entity) {
        if (entity instanceof EntityEmptyMagic) {
            EntityEmptyMagic magic = (EntityEmptyMagic) entity;
            if(magic.isSubMagic==this.isSubMagic){
                return;
            }
            for(int i = 0; i < this.jmp; i++){
                if (magic.order.size() <= 0) {
                    return;
                }
                magic.order.poll();
            }
        }
    }
}
