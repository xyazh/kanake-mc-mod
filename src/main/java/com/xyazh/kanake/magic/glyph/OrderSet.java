package com.xyazh.kanake.magic.glyph;

import java.util.LinkedList;

/**
 * 符文上的两套纹章顺序：普通区与终结区。
 */
public class OrderSet {
    public final LinkedList<GlyphOrder> normal;
    public final LinkedList<GlyphOrder> finish;

    public OrderSet(LinkedList<GlyphOrder> normal, LinkedList<GlyphOrder> finish) {
        this.normal = normal == null ? new LinkedList<>() : normal;
        this.finish = finish == null ? new LinkedList<>() : finish;
    }
}
