package com.logicbreadboard.simulator;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import java.io.DataInputStream;
import java.io.DataOutputStream;

/**
 * Created on 8/26/15.
 */
public class DecimalDisplayViewer extends DeviceViewer {
    static protected final int CONTEXT_MENU_MASK =
            ContextMenuIcons.COPY |
                    ContextMenuIcons.DELETE |
                    ContextMenuIcons.ROTATE |
                    ContextMenuIcons.LOCK | ContextMenuIcons.UNLOCK |
                    ContextMenuIcons.ALIGN_HORIZONTAL | ContextMenuIcons.ALIGN_VERTICAL |
                    ContextMenuIcons.UNSELECT;
    static final int DISPLAY_WIDTH = 20;
    static final int DISPLAY_HEIGHT = 26;
    static final int HIGH_COLOR = Color.RED;
    static final int UNDEFINED_COLOR = Color.YELLOW;
    static final int LOW_COLOR = Color.GREEN;

    DecimalDisplayViewer() {
        model = new DecimalDisplayModel(this);
    }
    @Override
    float getSelectionZoom(int canvasHeight, int canvasWidth, float scaleFactor) {
        return Math.min(canvasWidth / (8 * (right - left)),
                canvasHeight / (8 * (bottom - top))) / (scaleFactor);
    }

    @Override
    public int getContextMenuMask() {
        return CONTEXT_MENU_MASK;
    }

    @Override
    public short getGroup() {
        return (short) SchematicViewerFactory.DeviceGroup.Displays.ordinal();
    }

    @Override
    public short getSymbol() {
        return (short) SchematicViewerFactory.Displays.Decimal.ordinal();
    }

    @Override
    public void setDefaultNumberOfIos() {
        setNumberOfIos((short) 5);
    }

    protected void createEnableInput() {
        DeviceIOViewer iov = new DeviceIOViewer((short) ((DISPLAY_WIDTH / 2)),
                (short) (0),
                (short) (DeviceIOViewer.LENGTH + (DISPLAY_WIDTH / 2)),
                (short) (0), false);
        firstIoViewer = iov;
    }

    protected void createInputs() {
        createEnableInput();
        DeviceIOViewer iov = firstIoViewer;
        DeviceIOModel iom = ((DeviceModel) model).firstInputModel;
        iov.model = iom;
        iom = iom.next;
        for (int i = 1; i < 5; i++) {
            iov.next = new DeviceIOViewer(
                    (short) (-(DISPLAY_WIDTH / 2) + (DISPLAY_WIDTH * i) / 5),
                    (short) (DISPLAY_HEIGHT / 2),
                    (short) (-(DISPLAY_WIDTH / 2) + (DISPLAY_WIDTH * i) / 5),
                    (short) (DISPLAY_HEIGHT / 2 + DeviceIOViewer.LENGTH), false);
            iov = iov.next;
            iov.model = iom;
            iom = iom.next;
        }
    }

    @Override
    public void setNumberOfIos(short numberOfIos) {
        this.numberOfIos = numberOfIos;
        model.init(numberOfIos, 0);
        createInputs();
    }

    public void draw(Canvas canvas) {
        super.draw(canvas);
        rotateCanvas(canvas);
        drawRotated(canvas, currentPaint);
        restoreCanvas(canvas);
    }

    @Override
    void load(DataInputStream inputStream) throws java.io.IOException{
        super.load(inputStream);
    }

    @Override
    void save(DataOutputStream outputStream) throws java.io.IOException {
        super.save(outputStream);
    }

    @Override
    public void initPosition(int x, int y, int angleIndex) {
        this.left = x;
        this.bottom = y;
        this.right = x + DISPLAY_WIDTH;
        this.top = y - DISPLAY_HEIGHT;
        this.angleIndex = angleIndex;
        setEnclosingRect();
        //       this.numberOfIos = numberOfIos;
        //       this.selectedIos = new boolean[numberOfIos];
    }

    protected void drawContent(Canvas canvas, Paint paint) {
        float size = Context.displayPaint.getTextSize();
        Context.displayPaint.setTextSize(32);
        if (((DecimalDisplayModel) model).value == DecimalDisplayModel.BAD_VALUE || (
                (DecimalDisplayModel) model).value > 9) {
            canvas.drawText("X", left + STROKE_WITH / 2, bottom - STROKE_WITH, Context.displayPaint);
        } else {
            if (((DecimalDisplayModel) model).enabled) {
                canvas.drawText(Integer.toString(((DecimalDisplayModel) model).value), left + STROKE_WITH,
                        bottom - STROKE_WITH, Context.displayPaint);
            } else {
                canvas.drawText(" ", left + STROKE_WITH / 2, bottom - STROKE_WITH, paint);
            }
        }
        Context.displayPaint.setTextSize(size);
    }

    @Override
    protected void drawRotated(Canvas canvas, Paint paint) {
        float size = paint.getTextSize();
        canvas.drawLine(left, bottom, left, top, paint);
        canvas.drawLine(right, bottom, right, top, paint);
        canvas.drawLine(left, bottom, right, bottom, paint);
        canvas.drawLine(left, top, right, top, paint);
        paint.setTextSize(4);
        canvas.drawText("1", right - (DISPLAY_WIDTH / 6), bottom + 3 + STROKE_WITH, paint);
        drawContent(canvas, paint);
        paint.setTextSize(size);
    }

    public int getWidth() {
        return DISPLAY_WIDTH;
    }

    public int getHeight() {
        return DISPLAY_HEIGHT;
    }
}
