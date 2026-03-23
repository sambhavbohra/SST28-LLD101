package com.pen.pens;

import com.pen.BasePen;
import com.pen.enums.InkColour;

public class BallPointPen extends BasePen {
    public BallPointPen(InkColour colour) {
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
        return "Ballpoint pen";
    }
}
