package com.pen;

import com.pen.enums.InkColour;
import com.pen.enums.PenType;
import com.pen.exception.InkFinishedException;
import com.pen.exception.PenIsClosedException;
import com.pen.factory.PenFactory;
import java.util.LinkedHashMap;
import java.util.Map;

public class Main {
    private static final int PERCENTAGE_MULTIPLIER = 100;

    public static void main(String[] args) {
        Map<PenType, Pen> pensByType = createAllPensWithBlueInk();

        printStepSeparator("step 1 create one pen of each type with blue ink");
        printInkLevels("Ink levels before writing", pensByType);

        printStepSeparator("step 2 open each pen and write a short sentence");
        writeWithEveryPen(pensByType);
        printInkLevels("Ink levels after first writing", pensByType);

        printStepSeparator("step 3 refill one pen mid-use with red ink");
        refillOnePenAndWriteAgain(pensByType.get(PenType.GEL));

        printStepSeparator("step 4 use retractable pen through pen and retractable references");
        demonstrateRetractableBehavior(pensByType.get(PenType.RETRACTABLE_BALLPOINT));

        printStepSeparator("step 5 drain ink completely and catch ink finished exception");
        Pen drainedPen = pensByType.get(PenType.INK);
        drainInkUntilFinished(drainedPen);

        printStepSeparator("step 6 try writing on a closed pen and catch exception");
        writeOnClosedPenAndHandleError(pensByType.get(PenType.BALLPOINT));

        printStepSeparator("step 7 refill drained pen, open and write successfully");
        refillDrainedPenAndWrite(drainedPen);

        printStepSeparator("step 8 final ink levels");
        printInkLevels("Final ink levels", pensByType);
    }

    private static Map<PenType, Pen> createAllPensWithBlueInk() {
        Map<PenType, Pen> pensByType = new LinkedHashMap<>();
        for (PenType penType : PenType.values()) {
            pensByType.put(penType, PenFactory.createPen(penType, InkColour.BLUE));
        }
        return pensByType;
    }

    private static void writeWithEveryPen(Map<PenType, Pen> pensByType) {
        for (Map.Entry<PenType, Pen> entry : pensByType.entrySet()) {
            Pen pen = entry.getValue();
            pen.open();
            pen.write("Hi");
            pen.close();
        }
    }

    private static void refillOnePenAndWriteAgain(Pen gelPen) {
        gelPen.open();
        gelPen.write("This line is written before refill");
        gelPen.refill(InkColour.RED);
        gelPen.write("This line is written after refill in " + gelPen.getCurrentInkColour());
        gelPen.close();
    }

    private static void demonstrateRetractableBehavior(Pen retractablePenReference) {
        retractablePenReference.open();
        retractablePenReference.write("Pen mode");
        retractablePenReference.close();

        if (retractablePenReference instanceof Retractable) {
            Retractable retractableReference = (Retractable) retractablePenReference;
            retractableReference.extend();
            retractablePenReference.write("Click mode");
            retractableReference.retract();
        }
    }

    private static void drainInkUntilFinished(Pen penToDrain) {
        penToDrain.open();
        try {
            while (true) {
                penToDrain.write("a");
            }
        } catch (InkFinishedException exception) {
            System.out.println("Handled ink drain: " + exception.getMessage());
        } finally {
            penToDrain.close();
        }
    }

    private static void writeOnClosedPenAndHandleError(Pen closedPen) {
        closedPen.close();
        try {
            closedPen.write("This should fail because pen is closed");
        } catch (PenIsClosedException exception) {
            System.out.println("Handled closed pen: " + exception.getMessage());
        }
    }

    private static void refillDrainedPenAndWrite(Pen drainedPen) {
        drainedPen.refill(InkColour.GREEN);
        drainedPen.open();
        drainedPen.write("Writing works again after refill");
        drainedPen.close();
    }

    private static void printInkLevels(String label, Map<PenType, Pen> pensByType) {
        System.out.println(label);
        for (Map.Entry<PenType, Pen> entry : pensByType.entrySet()) {
            String levelAsPercentage = toPercentage(entry.getValue().getInkLevel());
            System.out.println(entry.getKey() + " -> " + levelAsPercentage);
        }
    }

    private static String toPercentage(double level) {
        int percentage = (int) Math.round(level * PERCENTAGE_MULTIPLIER);
        return percentage + "%";
    }

    private static void printStepSeparator(String stepName) {
        System.out.println("--- " + stepName + " ---");
    }
}
