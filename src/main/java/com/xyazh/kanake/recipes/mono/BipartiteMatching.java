package com.xyazh.kanake.recipes.mono;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class BipartiteMatching {
    protected Map<Integer, Set<String>> grape = new HashMap<>();
    protected Set<String> visited = new HashSet<>();
    protected Map<String,Integer> matching = new HashMap<>();
    public BipartiteMatching(Map<Integer, Set<String>> a, Set<String> b){
        this.getGraph(a,b);
    }

    protected void getGraph(Map<Integer, Set<String>> a, Set<String> b){
        for(int keyA : a.keySet()){
            this.grape.put(keyA,new HashSet<>());
            for(String keyB : b){
                for(String item : a.get(keyA)){
                    if(item.equals(keyB)){
                        this.grape.get(keyA).add(keyB);
                    }
                }
            }
        }
    }

    protected boolean findMatch(int u){
        for(String v : this.grape.get(u)){
            if(!this.visited.contains(v)){
                this.visited.add(v);
                if(!this.matching.containsKey(v)||this.findMatch(this.matching.get(v))){
                    this.matching.put(v,u);
                    return true;
                }
            }
        }
        return false;
    }

    public Map<Integer,String> maxMatching(){
        Map<Integer,String> matchedPairs = new HashMap<>();
        for(int u:this.grape.keySet()){
            this.visited.clear();
            if(this.findMatch(u)){
                for(String v:this.matching.keySet()){
                    if(this.matching.get(v) == u){
                        matchedPairs.put(u,v);
                    }
                }
            }
        }
        return matchedPairs;
    }
}