package com.xyazh.kanake.entity;

import com.xyazh.kanake.Kanake;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.ForgeChunkManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Temporary chunk loading for the 384-block Nuclear Strike range and the
 * 88-block-radius crater. Forge caps a single ticket at 25 chunks by default,
 * so the crater square is split across several tickets.
 */
public final class NuclearStrikeChunkLoader {
    private static final int MAX_CHUNKS_PER_TICKET = 25;

    private NuclearStrikeChunkLoader() {
    }

    public static void registerCallback() {
        if (Kanake.instance == null) {
            return;
        }
        ForgeChunkManager.setForcedChunkLoadingCallback(Kanake.instance, (tickets, world) -> {
            // A nuclear strike does not resume after a world reload. Release
            // any persisted tickets instead of keeping a crater loaded forever.
            for (ForgeChunkManager.Ticket ticket : tickets) {
                ForgeChunkManager.releaseTicket(ticket);
            }
        });
    }

    public static void forceSquare(World world, double centerX, double centerZ,
                                   int radiusChunks, List<ForgeChunkManager.Ticket> tickets) {
        if (world == null || world.isRemote || !(world instanceof WorldServer)
                || Kanake.instance == null) {
            return;
        }
        int centerChunkX = MathHelper.floor(centerX) >> 4;
        int centerChunkZ = MathHelper.floor(centerZ) >> 4;
        int maxPerTicket = Math.min(MAX_CHUNKS_PER_TICKET,
                ForgeChunkManager.getMaxChunkDepthFor(Kanake.MODID));
        if (maxPerTicket <= 0) {
            maxPerTicket = 1;
        }
        List<ChunkPos> positions = new ArrayList<ChunkPos>();
        for (int dx = -radiusChunks; dx <= radiusChunks; ++dx) {
            for (int dz = -radiusChunks; dz <= radiusChunks; ++dz) {
                positions.add(new ChunkPos(centerChunkX + dx, centerChunkZ + dz));
            }
        }

        for (int start = 0; start < positions.size(); start += maxPerTicket) {
            ForgeChunkManager.Ticket ticket = ForgeChunkManager.requestTicket(
                    Kanake.instance, world, ForgeChunkManager.Type.NORMAL);
            if (ticket == null) {
                break;
            }
            int end = Math.min(start + maxPerTicket, positions.size());
            ticket.setChunkListDepth(end - start);
            for (int i = start; i < end; ++i) {
                ForgeChunkManager.forceChunk(ticket, positions.get(i));
            }
            tickets.add(ticket);
        }

        // Make the centre chunk available immediately so the newly spawned
        // ritual entity can tick while the remaining ticket chunks load.
        try {
            world.getChunkFromChunkCoords(centerChunkX, centerChunkZ);
        } catch (Throwable ignored) {
            // If generation fails the entity will still fall back to whatever
            // chunks the server already has loaded.
        }
    }

    public static void releaseAll(List<ForgeChunkManager.Ticket> tickets) {
        if (tickets == null) {
            return;
        }
        for (ForgeChunkManager.Ticket ticket : tickets) {
            try {
                ForgeChunkManager.releaseTicket(ticket);
            } catch (Throwable ignored) {
            }
        }
        tickets.clear();
    }
}
