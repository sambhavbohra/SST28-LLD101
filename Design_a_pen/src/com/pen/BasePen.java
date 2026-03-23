package com.pen;

import com.pen.enums.InkColour;
import com.pen.exception.InkFinishedException;
import com.pen.exception.InvalidPenConfigurationException;
import com.pen.exception.PenIsClosedException;
import com.pen.model.Ink;

public abstract class BasePen implements Pen {
    private static final int WRITE_THRESHOLD_CHARS = 1;

    private Ink ink;
    private boolean penIsOpen;

    protected BasePen(InkColour initialColour) {
        validateInkColour(initialColour);
        this.ink = Ink.fullCartridge(initialColour);
        this.penIsOpen = false;
    }

    protected abstract void performOpen();

    protected abstract void performClose();

    protected abstract String penTypeName();

    @Override
    public void open() {
        if (penIsOpen) {
            return;
        }
        performOpen();
        penIsOpen = true;
    }

    @Override
    public void close() {
        if (!penIsOpen) {
            return;
        }
        performClose();
        penIsOpen = false;
    }

    @Override
    public void write(String text) {
        ensurePenIsOpen();
        if (text == null || text.isBlank()) {
            return;
        }
        ensureInkIsNotFinished();
        consumeInkForText(text);
        System.out.println(text);
    }

    @Override
    public void refill(InkColour colour) {
        validateInkColour(colour);
        ink = Ink.fullCartridge(colour);
    }

    @Override
    public InkColour getCurrentInkColour() {
        return ink.getColour();
    }

    @Override
    public double getInkLevel() {
        return ink.getLevel();
    }

    @Override
    public boolean isOpen() {
        return penIsOpen;
    }

    private void ensurePenIsOpen() {
        if (penIsOpen) {
            return;
        }
        throw new PenIsClosedException("Cannot write with " + penTypeName() + " - pen is closed.");
    }

    private void ensureInkIsNotFinished() {
        if (!ink.isEmpty()) {
            return;
        }
        throw new InkFinishedException("Ink finished - current colour is " + ink.getColour() + ". Please refill.");
    }

    private void consumeInkForText(String text) {
        int writesNeeded = calculateWritesNeeded(text);
        for (int writeCount = 0; writeCount < writesNeeded; writeCount++) {
            ink.consume();
        }
    }

    private int calculateWritesNeeded(String text) {
        int textLength = text.length();
        return (textLength + WRITE_THRESHOLD_CHARS - 1) / WRITE_THRESHOLD_CHARS;
    }

    private void validateInkColour(InkColour colour) {
        if (colour != null) {
            return;
        }
        throw new InvalidPenConfigurationException("Ink colour cannot be null.");
    }
}
