package com.xyazh.kanake.util.xml.rendernode;

import org.w3c.dom.Element;

import javax.annotation.Nullable;

public class RenderNodeImg extends RenderNode {
    public long color;

    public RenderNodeImg(Element element, @Nullable RenderNode parent) {
        super(element, parent);
    }

    public RenderNodeImg(Element element) {
        super(element);
    }
}
