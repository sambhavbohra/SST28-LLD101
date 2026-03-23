package com.pen;

import com.pen.enums.InkColour;

public interface Pen {
    void open();

    void close();

    void write(String text);

    void refill(InkColour colour);

    InkColour getCurrentInkColour();

    double getInkLevel();

    boolean isOpen();
}
