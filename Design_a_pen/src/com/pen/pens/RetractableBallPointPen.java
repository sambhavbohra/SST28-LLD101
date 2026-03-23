package com.pen.pens;

import com.pen.BasePen;
import com.pen.Retractable;
import com.pen.enums.InkColour;

public class RetractableBallPointPen extends BasePen implements Retractable {
    public RetractableBallPointPen(InkColour colour) {
        super(colour);
    }

    @Override
    public void extend() {
        super.open();
    }

    @Override
    public void retract() {
        super.close();
    }

    @Override
    public void open() {
        extend();
    }

    @Override
    public void close() {
        retract();
    }

    @Override
    protected void performOpen() {
    }

    @Override
    protected void performClose() {
    }

    @Override
    protected String penTypeName() {
        return "Retractable ballpoint pen";
    }
}
