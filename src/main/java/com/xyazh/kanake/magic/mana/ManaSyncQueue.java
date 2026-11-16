package com.xyazh.kanake.magic.mana;

import com.xyazh.kanake.Kanake;
import com.xyazh.kanake.data.ManaData;
import com.xyazh.kanake.network.PlayerManaPacket;
import net.minecraft.entity.player.EntityPlayerMP;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * 同一 tick 内多个法术实体扣魔力时，只向每个玩家发送一次 HUD 同步包。
 */
public final class ManaSyncQueue {
    private static final Map<UUID, EntityPlayerMP> PENDING = new HashMap<>();

    private ManaSyncQueue() {
    }

    public static void request(EntityPlayerMP player) {
        if (player != null) {
            PENDING.put(player.getUniqueID(), player);
        }
    }

    public static void flush() {
        if (PENDING.isEmpty()) {
            return;
        }
        for (EntityPlayerMP player : PENDING.values()) {
            if (player == null || !player.isEntityAlive() || player.connection == null) {
                continue;
            }
            PlayerManaPacket packet = new PlayerManaPacket();
            packet.mana = ManaData.get(player);
            Kanake.network.sendTo(packet, player);
        }
        PENDING.clear();
    }
}
