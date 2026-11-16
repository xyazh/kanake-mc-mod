package com.xyazh.kanake.util.xml.rendernode;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import org.w3c.dom.Element;

import javax.annotation.Nullable;
import java.util.ArrayList;

public class RenderNode {
    protected final ArrayList<RenderNode> subNodes = new ArrayList<>();
    @Nullable
    public final RenderNode parentNode;
    public double posX = 0;
    public double posY = 0;
    public double w = 0;
    public double h = 0;
    protected final Element element;

    public RenderNode(Element element,@Nullable RenderNode parent){
        this.element = element;
        this.parentNode = parent;
    }

    public RenderNode(Element element){
        this.element = element;
        this.parentNode = null;
    }

    public void draws(BufferBuilder builder, Minecraft mc,int wdX,int wdY){
        this.render(builder,mc,wdX,wdY);
        for(RenderNode node:this.subNodes){
            node.draws(builder,mc,wdX,wdY);
        }
    }

    protected void render(BufferBuilder builder, Minecraft mc,int wdX,int wdY){
    }

    public void addSubNode(RenderNode node){
        this.subNodes.add(node);
    }
}
