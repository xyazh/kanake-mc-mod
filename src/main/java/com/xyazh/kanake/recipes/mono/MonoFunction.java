package com.xyazh.kanake.recipes.mono;

import net.minecraft.nbt.NBTTagCompound;

import javax.annotation.Nonnull;
import java.util.LinkedList;


public class MonoFunction{
    public final String type;

    public MonoFunction(String type){
        this.type = "mono_function_" + type;
    }

    protected final LinkedList<String> func = new LinkedList<>();
    public boolean callFuc(MonoWorkingRecipe workingRecipe) {
        boolean success = true;
        for(String s:this.func){
            if(MonoFunctions.MONO_FUCNTIONS.containsKey(s)){
                success = success && MonoFunctions.MONO_FUCNTIONS.get(s).apply(workingRecipe);
            }
        }
        return success;
    }

    @Nonnull
    public NBTTagCompound writeToNBT(@Nonnull NBTTagCompound compound) {
        NBTTagCompound subCompound = new NBTTagCompound();
        int size = this.func.size();
        subCompound.setInteger("size",size);
        for(int i=0;i<size;i++){
            subCompound.setString(String.valueOf(i),this.func.get(i));
        }
        compound.setTag(this.type,subCompound);
        return compound;
    }

    public void readFromNBT(@Nonnull NBTTagCompound compound) {
        NBTTagCompound subCompound = compound.getCompoundTag(this.type);
        int size = subCompound.getInteger("size");
        for(int i=0;i<size;i++){
            this.func.add(subCompound.getString(String.valueOf(i)));
        }
    }
}
