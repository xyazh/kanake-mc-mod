package com.xyazh.kanake.magic.mana;

import com.xyazh.kanake.api.IManaStorage;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.FMLCommonHandler;

import java.util.UUID;

public final class ManaSourceResolver {
    private ManaSourceResolver() {
    }

    public static IManaSource resolve(World world, ManaSourceRef ref) {
        if (world == null || world.isRemote || ref == null) {
            return null;
        }
        switch (ref.type) {
            case ManaSourceRef.PLAYER:
                return resolvePlayer(ref.uuid);
            case ManaSourceRef.ENTITY:
                return resolveEntity(world, ref.uuid);
            case ManaSourceRef.TILE:
                return resolveTile(world, ref);
            default:
                return null;
        }
    }

    private static IManaSource resolvePlayer(UUID uuid) {
        if (uuid == null) {
            return null;
        }
        MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();
        if (server == null) {
            return null;
        }
        EntityPlayerMP player = server.getPlayerList().getPlayerByUUID(uuid);
        if (player == null || !player.isEntityAlive()) {
            return null;
        }
        return new PlayerManaSource(player);
    }

    private static IManaSource resolveEntity(World world, UUID uuid) {
        if (uuid == null) {
            return null;
        }
        for (Entity entity : world.loadedEntityList) {
            if (!uuid.equals(entity.getUniqueID())) {
                continue;
            }
            if (entity.isDead) {
                return null;
            }
            if (entity instanceof IManaStorage) {
                return new StorageManaSource((IManaStorage) entity);
            }
            return null;
        }
        return null;
    }

    private static IManaSource resolveTile(World world, ManaSourceRef ref) {
        if (ref.pos == null) {
            return null;
        }
        if (ref.dimension != world.provider.getDimension()) {
            return null;
        }
        TileEntity tile = world.getTileEntity(ref.pos);
        if (tile instanceof IManaStorage) {
            return new StorageManaSource((IManaStorage) tile);
        }
        return null;
    }

    private static class StorageManaSource implements IManaSource {
        private final IManaStorage storage;

        private StorageManaSource(IManaStorage storage) {
            this.storage = storage;
        }

        @Override
        public boolean isValid() {
            if (this.storage instanceof TileEntity) {
                TileEntity tile = (TileEntity) this.storage;
                return tile.getWorld() != null && tile.getWorld().getTileEntity(tile.getPos()) == tile;
            }
            if (this.storage instanceof Entity) {
                return !((Entity) this.storage).isDead;
            }
            return this.storage != null;
        }

        @Override
        public double getMana() {
            return this.storage == null ? 0 : this.storage.getManaStored();
        }

        @Override
        public boolean consume(double amount) {
            if (amount <= 0) {
                return true;
            }
            if (!this.isValid() || this.storage.getManaStored() < amount) {
                return false;
            }
            return this.storage.extractMana(amount) >= amount;
        }
    }
}
