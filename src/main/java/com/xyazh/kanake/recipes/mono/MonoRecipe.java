package com.xyazh.kanake.recipes.mono;

import com.xyazh.kanake.Kanake;
import com.xyazh.kanake.block.blocks.manatable.TileTableCoreMono;
import com.xyazh.kanake.block.blocks.manatable.TileTableMono;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.common.util.RecipeMatcher;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;

public class MonoRecipe {
    @Nonnull
    protected ItemStack output;
    protected Ingredient coreItem;
    protected NonNullList<Ingredient> input = NonNullList.create();
    public final ResourceLocation id;

    @Nonnull
    public ItemStack getOutItem(){
        return this.output;
    }

    public void onFinish(MonoWorkingRecipe workingRecipe){
        TileTableCoreMono coreMono = workingRecipe.coreMono;
        World world = coreMono.getWorld();
        BlockPos pos = coreMono.getPos();
        world.createExplosion(null,pos.getX()+0.5, pos.getY()+1, pos.getZ()+0.5, 0,true);
    }

    public void onFail(MonoWorkingRecipe workingRecipe){

    }

    public MonoRecipe(@Nonnull ResourceLocation id,@Nonnull Object coreItem, @Nonnull ItemStack result, Object[] recipe) {
        this.coreItem = CraftingHelper.getIngredient(coreItem);
        this.output = result.copy();
        this.id = id;
        for (Object in : recipe) {
            Ingredient ing = CraftingHelper.getIngredient(in);
            if (ing != null) {
                input.add(ing);
            }
        }
    }

    public MonoRecipe(@Nonnull ResourceLocation id,@Nonnull Object coreItem, @Nonnull ItemStack result, Collection<Object> recipe) {
        this.coreItem = CraftingHelper.getIngredient(coreItem);
        this.output = result.copy();
        this.id = id;
        for (Object in : recipe) {
            Ingredient ing = CraftingHelper.getIngredient(in);
            if (ing != null) {
                input.add(ing);
            }
        }
    }

    @Nonnull
    public ItemStack getRecipeOutput() {
        return output;
    }

    public int getTime(TileTableCoreMono coreMono, LinkedList<TileTableMono> outTiles, ItemStack itemStack){
        return 60;
    }

    @Nonnull
    public ItemStack getCraftingResult() {
        return output.copy();
    }

    public boolean matches(@Nonnull ItemStack coreItem, @Nonnull List<ItemStack> inv, @Nonnull World world, @Nullable TileEntity te) {
        if (this.coreItem == null) {
            return false;
        }
        if (!this.coreItem.apply(coreItem)) {
            return false;
        }
        return RecipeMatcher.findMatches(inv, this.input) != null;
    }

    @Nonnull
    public NonNullList<Ingredient> getIngredients() {
        return this.input;
    }

    @Nullable
    public static MonoRecipe factory(ResourceLocation id,String recipe) {
        String[] parts = recipe.split("\\|");
        if (parts.length != 3) {
            return null;
        }
        String out;
        out = parts[2].replaceAll("<", "").replaceAll(">", "");
        Item outItem = Item.getByNameOrId(out);
        if (outItem == null) {
            Kanake.logger.warn("Mono recipe out item is null.");
            return null;
        }
        ItemStack outItemStack = new ItemStack(outItem);

        String in = parts[0];
        Object coreItem;
        if (in.contains("<ore:")) {
            in = in.replaceAll("<ore:", "").replaceAll(">", "");
            coreItem = in;
        } else {
            in = in.replaceAll("<", "").replaceAll(">", "");
            Item inItem = Item.getByNameOrId(in);
            if (inItem == null) {
                Kanake.logger.warn("Mono recipe core item is null.");
                return null;
            }
            coreItem = new ItemStack(inItem);
        }

        String[] items;
        items = parts[1].split(",");
        Object[] recipeItems = new Object[items.length];
        for (int i = 0; i < items.length; i++) {
            if (items[i].contains("<ore:")) {
                items[i] = items[i].replaceAll("<ore:", "").replaceAll(">", "");
                recipeItems[i] = items[i];
            } else {
                items[i] = items[i].replaceAll("<", "").replaceAll(">", "");
                Item recipeItem = Item.getByNameOrId(items[i]);
                if (recipeItem == null) {
                    Kanake.logger.warn("Mono recipe items has null.");
                    return null;
                }
                recipeItems[i] = new ItemStack(recipeItem);
            }
        }

        return new MonoRecipe(id,coreItem, outItemStack, recipeItems);
    }
}

