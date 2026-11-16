package com.xyazh.kanake.magic.mana;

import com.xyazh.kanake.data.ManaData;
import net.minecraft.entity.player.EntityPlayerMP;

public class PlayerManaSource implements IManaSource {
    private final EntityPlayerMP player;

    public PlayerManaSource(EntityPlayerMP player) {
        this.player = player;
    }

    @Override
    public boolean isValid() {
        return this.player != null
                && this.player.world != null
                && this.player.isEntityAlive()
                && this.player.connection != null;
    }

    @Override
    public double getMana() {
        if (this.player == null) {
            return 0;
        }
        if (this.player.isCreative()) {
            return Double.POSITIVE_INFINITY;
        }
        return ManaData.get(this.player);
    }

    @Override
    public boolean consume(double amount) {
        if (amount <= 0) {
            return true;
        }
        if (!this.isValid()) {
            return false;
        }
        if (this.player.isCreative()) {
            return true;
        }
        if (ManaData.get(this.player) < amount) {
            return false;
        }
        ManaData.sub(this.player, amount);
        ManaSyncQueue.request(this.player);
        return true;
    }
}
