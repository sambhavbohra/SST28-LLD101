package com.pen.pens;

import com.pen.BasePen;
import com.pen.enums.InkColour;

public class GelPen extends BasePen {
    public GelPen(InkColour colour) {
        super(colour);
    }

    @Override
    protected void performOpen() {
    }

    @Override
    protected void performClose() {
    }

    @Override
    protected String penTypeName() {
        return "Gel pen";
    }
}
