package com.logicbreadboard.simulator;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

/**
 * Created by laci on 8/6/15.
 */
public class FaultViewer extends Viewer {
    enum FaultType {
        NONE, SHORT_CIRCUIT, T_SETUP, T_HOLD, T_REMOVAL, T_RECOVERY, SPIKE,
        NOT_CONNECTED, RECURSION, BADLY_PLACED_RESISTOR, POWER_ERROR, LED_OVERCURRENT
    }

    public static final int RADIUS = 8;
    static private final int CONTEXT_MENU_MASK = ContextMenuIcons.DELETE;
    public boolean positioned = false;
    int time;
    State.Level level;
    State.Impedance impedance;
    DeviceIOModel faultyIo;
    int x;
    int y;
    FaultType faultType;

    FaultViewer(DeviceIOModel faultyIo) {
        this.time = Event.simulationTime;
        this.level = faultyIo.level;
        this.impedance = faultyIo.impedance;
        this.faultyIo = faultyIo;
        faultType = FaultType.SHORT_CIRCUIT;
    }

    FaultViewer(DeviceIOModel faultyIo, FaultType t) {
        this.time = Event.simulationTime;
        this.level = faultyIo.level;
        this.faultType = t;
        this.faultyIo = faultyIo;
    }

    static public void newFaultViewer(DeviceIOModel faultyModel, FaultViewer.FaultType faultType) {
        Event.displayEvent = true;
        FaultViewer fv = EditorModel.faultViewers.get(faultyModel); // this io already has a fault
        if (fv == null) {
            fv = new FaultViewer(faultyModel, faultType);
        }
        fv.faultType=faultType;
        fv.time = Event.simulationTime;
        fv.level = faultyModel.level;
        fv.impedance = faultyModel.impedance;
        fv.faultyIo = faultyModel;
        fv.positioned = false;
        EditorModel.faultViewers.put(fv.faultyIo, fv);
    }

    // TODO: 9/17/15 boardConnection outputs with same level without input causes short circuit fault
    public int getContextMenuMask() {
        return CONTEXT_MENU_MASK;
    }

    @Override
    public void draw(Canvas canvas) {
        int savedColor = currentPaint.getColor();
        int color;
        if (currentPaint == Context.selectedPaint || currentPaint == Context.multiSelectedPaint) {
            color = Color.YELLOW;
        } else {
            color = Color.MAGENTA;
        }
        currentPaint.setColor(color);
        currentPaint.setStyle(Paint.Style.FILL);
        canvas.drawCircle(x, y, RADIUS, currentPaint);
        currentPaint.setStyle(Paint.Style.STROKE);
        currentPaint.setStrokeWidth(STROKE_WITH / 2);
        if (faultType == FaultType.NONE) {
            canvas.drawText(faultType.toString() + " time:" + time + "  level:" + State.Level
                    .values()[level
                    .ordinal()], x, y - RADIUS, currentPaint);
        } else if (faultType == FaultType.NOT_CONNECTED) {
            canvas.drawText(faultType.toString(), x, y - RADIUS, currentPaint);
        } else if (faultType == FaultType.BADLY_PLACED_RESISTOR) {
            canvas.drawText(faultType.toString(), x, y - RADIUS, currentPaint);
        } else if (faultType == FaultType.LED_OVERCURRENT) {
            canvas.drawText(faultType.toString(), x, y - RADIUS, currentPaint);
        } else {
            canvas.drawText(faultType.toString() + " time:" + time + "  level:" + State.Level
                    .values()[level
                    .ordinal()], x, y - RADIUS, currentPaint);
        }
        currentPaint.setStrokeWidth(STROKE_WITH);
        currentPaint.setColor(savedColor);
    }

    @Override
    public void select(SelectMsg msg) {
        int enclosingLeft;
        int enclosingTop;
        int enclosingRight;
        int enclosingBottom;
        enclosingLeft = x - RADIUS;
        enclosingRight = x + RADIUS;
        enclosingBottom = y + RADIUS;
        enclosingTop = y - RADIUS;
//        System.out.println("Fault select?" + msg.pointerX + "," + msg.pointerY + " x=" + x + " y=" + y);
        if (enclosingLeft - msg.epsilon[0] <= msg.pointerX &&
                enclosingRight + msg.epsilon[0] >= msg.pointerX &&
                enclosingBottom + msg.epsilon[0] >= msg.pointerY &&
                enclosingTop - msg.epsilon[0] <= msg.pointerY) {
//            System.out.println("Fault selected");
            msg.viewer = this;
            msg.epsilon[0] = 0;//  Fault viewer has selection priority
        }
    }

    public void drawEnlarged(Canvas canvas, float zoom) {
    }

}
