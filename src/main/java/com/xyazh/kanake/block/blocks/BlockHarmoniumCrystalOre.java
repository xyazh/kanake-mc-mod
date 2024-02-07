package com.xyazh.kanake.block.blocks;

import com.xyazh.kanake.init.ModCreativeTab;
import com.xyazh.kanake.item.ModItems;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.monster.EntitySlime;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import java.util.Random;

public class BlockHarmoniumCrystalOre extends BlockBase{
    public BlockHarmoniumCrystalOre(String name) {
        super(name, Blocks.EMERALD_ORE.getDefaultState().getMaterial());
        this.setCreativeTab(ModCreativeTab.KNK_CREATIVE);
    }

    @Nonnull
    public Item getItemDropped(@Nonnull IBlockState state, @Nonnull Random rand, int fortune)
    {
            return ModItems.HARMONIUM_CRYSTAL;
    }

    public void onEntityWalk(@Nonnull World worldIn, @Nonnull BlockPos pos, @Nonnull Entity entityIn)
    {
        if(entityIn instanceof EntitySlime){
            entityIn.attackEntityFrom(DamageSource.CACTUS,2);
        }
    }
}
