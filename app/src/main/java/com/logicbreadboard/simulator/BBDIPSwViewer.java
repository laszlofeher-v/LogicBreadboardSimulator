package com.logicbreadboard.simulator;

import android.graphics.Canvas;
import android.graphics.Paint;

import java.io.DataInputStream;
import java.io.DataOutputStream;

/**
 * Created on 5/12/16.
 */
public class BBDIPSwViewer extends _74_DIPViewer {
    //    protected final int BOX_WIDTH = 8 * unit;
    protected final int BOX_HEIGHT = 3 * unit;
    static protected final int CONTEXT_MENU_MASK = ContextMenuIcons.COPY |
            ContextMenuIcons.DELETE |
            ContextMenuIcons.DELAY |
            ContextMenuIcons.ROTATE |
            ContextMenuIcons.ADD_SW |
            ContextMenuIcons.REMOVE_SW |
            ContextMenuIcons.UNSELECT;

    @Override
    public short getGroup() {
        return (short) BBViewerFactory.factory.getGroupDIPSw();
    }

    public short getSymbol() {
        return (short) BBViewerFactory.BB_Switch.DIP_SW.ordinal();
    }

    public int getWidth() {
        return unit * (numberOfIos / 2);
    }

    public int getHeight() {
        return BOX_HEIGHT;
    }

    @Override
    public int getContextMenuMask() {
        return CONTEXT_MENU_MASK;
    }

    @Override
    void save(DataOutputStream outputStream) throws java.io.IOException {
        super.save(outputStream);
        ((BBDIPSwModel) model).save(outputStream);
    }

    @Override
    void load(DataInputStream inputStream) throws java.io.IOException {
        super.load(inputStream);
        ((BBDIPSwModel) model).load(inputStream);
    }

    BBDIPSwViewer() {
        model = new BBDIPSwModel();
    }

    @Override
    public boolean pushButton(PushButtonMsg pushButtonMsg) {
        if (enclosingLeft <= pushButtonMsg.pointerX &&
                enclosingRight >= pushButtonMsg.pointerX &&
                enclosingBottom >= pushButtonMsg.pointerY &&
                enclosingTop <= pushButtonMsg.pointerY) {
            for (int pos = 0; pos < ((BBDIPSwModel) model).isOn.length; pos++) {
                // TODO: 6/1/16 call model only when changed
                float x = left + pos * unit;
                if (x <= pushButtonMsg.pointerX && (left + unit + pos * unit > pushButtonMsg.pointerX)) {
                    if (angleIndex != 0) {
                        pos = ((BBDIPSwModel) model).isOn.length - pos - 1;
                        if (enclosingBottom - BOX_HEIGHT / 2 >= pushButtonMsg.pointerY) {
                            if (((BBDIPSwModel) model).isOn[pos]) {
                                ((BBDIPSwModel) model).off(pos);
                            }
                        } else {
                            if (!((BBDIPSwModel) model).isOn[pos]) {
                                ((BBDIPSwModel) model).on(pos);
                            }
                        }
                    } else {
                        if (enclosingBottom - BOX_HEIGHT / 2 >= pushButtonMsg.pointerY) {
                            if (!((BBDIPSwModel) model).isOn[pos]) {
                                ((BBDIPSwModel) model).on(pos);
                            }
                        } else {
                            if (((BBDIPSwModel) model).isOn[pos]) {
                                ((BBDIPSwModel) model).off(pos);
                            }
                        }
                    }
                    return true;
                }
            }
            return false;
        } else {
            return false;
        }
    }

    @Override
    public boolean releaseButton(PushButtonMsg pushButtonMsg) {
        if (enclosingLeft <= pushButtonMsg.pointerX &&
                enclosingRight >= pushButtonMsg.pointerX &&
                enclosingBottom >= pushButtonMsg.pointerY &&
                enclosingTop <= pushButtonMsg.pointerY) {
//            ((BBDIPSwModel) model).off(0);
            return true;
        } else {
            return false;
        }
    }

    @Override
    protected void drawRotated(Canvas canvas, Paint paint) {
        float w = 0;
        if (floating) {
            w = paint.getStrokeWidth();
            paint.setStrokeWidth(4 * w);
//            Log.i("strokewidth1", "w=" + w);
        }
//        canvas.drawLine(left, bottom, left, top, paint);
//        canvas.drawLine(right, bottom, right, top, paint);
//        canvas.drawLine(left, bottom, right, bottom, paint);
//        canvas.drawLine(left, top, right, top, paint);
        canvas.drawRect(left, top, right, bottom, paint);
        canvas.drawText("ON", left + unit / 4, top + unit / 2, paint);
        boolean[] isOn = ((BBDIPSwModel) model).isOn;
        for (int pos = 0; pos < ((BBDIPSwModel) model).isOn.length; pos++) {
            float x = left + unit / 4 + pos * unit;
            canvas.drawRect(x, top + unit, left + 3 * unit / 4 + pos * unit, bottom - unit, paint);
            paint.setStyle(Paint.Style.FILL_AND_STROKE);
            if (isOn[pos]) {
                canvas.drawRect(x, top + unit, left + 3 * unit / 4 + pos * unit, bottom - unit - unit / 2, paint);
            } else {
                canvas.drawRect(x, top + unit + unit / 2, left + 3 * unit / 4 + pos * unit, bottom - unit, paint);
            }
            paint.setStyle(Paint.Style.STROKE);
            canvas.drawText(Integer.toString((pos + 1)), x + unit / 8, bottom - unit / 2, paint);
        }
        if (floating) {
            paint.setStrokeWidth(w);
        }
    }

    protected DeviceIOViewer createOneIO(DeviceIOViewer prev, int iPos, short yPos, int index) {
        short xPos;
        DeviceIOViewer iov;
        if (yPos < 0) {
            xPos = (short) (getWidth() / 2 - iPos * unit - unit / 2);
            iov = new DIPIoViewer(xPos, yPos,
                    xPos, (short) (yPos - DeviceIOViewer.LENGTH), false, index);
        } else {
            xPos = (short) (-getWidth() / 2 + iPos * unit + unit / 2);
            iov = new DIPIoViewer(xPos, yPos,
                    xPos, (short) (yPos + DeviceIOViewer.LENGTH), false, index);
        }
        if (prev != null) {
            prev.next = iov;
        } else {
            firstIoViewer = iov;
        }
        return iov;
    }

    @Override
    protected void createAllInputs() {
        DeviceIOViewer iov = firstIoViewer;
        DeviceOutputModel outputModel = ((DeviceModel) model).firstOutputModel;
        for (short i = 0; i < numberOfIos / 2; i++) {
            iov = createOneIO(iov, i, (short) (getHeight() / 2), i + 1);
            iov.model = outputModel;
            outputModel = (DeviceOutputModel) outputModel.next;
        }
        for (short i = 0; i < numberOfIos / 2; i++) {
            iov = createOneIO(iov, i, (short) -(getHeight() / 2), i + 1);
            iov.model = outputModel;
            outputModel = (DeviceOutputModel) outputModel.next;
        }
        right = left + getWidth();
    }

    @Override
    public void setNumberOfIos(short numberOfIos) {
        this.numberOfIos = numberOfIos;
        model.init(0, numberOfIos);
        createAllInputs();
    }

    @Override
    public void setDefaultNumberOfIos() {
        setNumberOfIos((short) 16);
    }

    @Override
    public void plusIO(EditorViewer editorViewer) {
        firstIoViewer = null;
        ((DeviceModel) model).firstOutputModel = null;
        setNumberOfIos((short) (numberOfIos + 2));
        setEnclosingRectWithoutCheck();
    }

    @Override
    public void minusIO(EditorViewer editorViewer) {
        if (numberOfIos >= 4) {
            firstIoViewer = null;
            ((DeviceModel) model).firstOutputModel = null;
            setNumberOfIos((short) (numberOfIos - 2));
            setEnclosingRectWithoutCheck();
        }
    }

}
