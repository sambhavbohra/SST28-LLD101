package com.pen.factory;

import com.pen.Pen;
import com.pen.enums.InkColour;
import com.pen.enums.PenType;
import com.pen.exception.UnknownPenTypeException;
import com.pen.pens.BallPointPen;
import com.pen.pens.GelPen;
import com.pen.pens.InkPen;
import com.pen.pens.RetractableBallPointPen;
import com.pen.pens.RetractableGelPen;

public final class PenFactory {
    private PenFactory() {
    }

    public static Pen createPen(PenType type, InkColour colour) {
        if (type == null) {
            throw new UnknownPenTypeException("No pen implementation found for type: null.");
        }

        switch (type) {
            case BALLPOINT:
                return new BallPointPen(colour);
            case GEL:
                return new GelPen(colour);
            case INK:
                return new InkPen(colour);
            case RETRACTABLE_BALLPOINT:
                return new RetractableBallPointPen(colour);
            case RETRACTABLE_GEL:
                return new RetractableGelPen(colour);
            default:
                throw new UnknownPenTypeException("No pen implementation found for type: " + type + ".");
        }
    }
}
