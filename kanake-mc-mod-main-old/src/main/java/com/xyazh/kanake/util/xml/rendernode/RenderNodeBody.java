package com.xyazh.kanake.util.xml.rendernode;

import org.w3c.dom.Element;

import javax.annotation.Nullable;

public class RenderNodeBody extends RenderNode {
    public long color;

    public RenderNodeBody(Element element, @Nullable RenderNode parent) {
        super(element, parent);
    }

    public RenderNodeBody(Element element) {
        super(element);
    }
}
