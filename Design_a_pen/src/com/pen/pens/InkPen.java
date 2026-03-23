package com.pen.pens;

import com.pen.BasePen;
import com.pen.enums.InkColour;

public class InkPen extends BasePen {
    public InkPen(InkColour colour) {
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
        return "Ink pen";
    }
}
