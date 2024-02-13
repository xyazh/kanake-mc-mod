package com.xyazh.kanake.util.xml.rendernode;

import org.w3c.dom.Element;

import javax.annotation.Nullable;

public class RenderNodeText extends RenderNode {
    public long color;

    public RenderNodeText(Element element, @Nullable RenderNode parent) {
        super(element, parent);
    }

    public RenderNodeText(Element element) {
        super(element);
    }
}
