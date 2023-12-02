package com.xyazh.kanake.recipes.mono;

import com.xyazh.kanake.Kanake;
import net.minecraft.item.Item;

import javax.annotation.Nullable;
import java.util.*;

public class RecipesInItems {
    private final String outItemString;
    private final Map<String,Integer> inItems = new HashMap<>();

    RecipesInItems(String outItem){
        this.outItemString = outItem;
    }

    @Nullable
    public Item getOutItem(){
        Item item = Item.getByNameOrId(this.outItemString);
        if(item==null){
            Kanake.logger.warn("Mono recipe out item is null.");
        }
        return item;
    }

    public void addItem(String s){
        if(this.inItems.containsKey(s)){
            this.inItems.put(s, this.inItems.get(s)+1);
        }else {
            this.inItems.put(s,1);
        }
    }

    public void addItems(Collection<String> collection){
        for(String s:collection){
            this.addItem(s);
        }
    }

    public boolean checkItems(Set<Set<String>> checkItems){
        Map<Integer,Set<String>> a = new HashMap<>();
        int i = 0;
        for(Set<String> stringSet:checkItems){
            a.put(i,stringSet);
            i += 1;
        }
        Map<String,Integer> inItemCopy = new HashMap<>(this.inItems);
        while (true){
            BipartiteMatching bipartiteMatching = new BipartiteMatching(a, inItemCopy.keySet());
            Map<Integer,String> map = bipartiteMatching.maxMatching();
            if(map.size()<=0){
                break;
            }
            for(int key:map.keySet()){
                String itemName = map.get(key);
                a.remove(key);
                if (inItemCopy.containsKey(itemName)){
                    int n = inItemCopy.get(itemName) - 1;
                    if(n<=0){
                        inItemCopy.remove(itemName);
                    }else {
                        inItemCopy.put(itemName,n);
                    }
                }
            }
        }
        return inItemCopy.size()<=0;
    }
}
