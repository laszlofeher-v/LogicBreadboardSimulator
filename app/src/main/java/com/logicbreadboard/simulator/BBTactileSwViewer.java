package com.logicbreadboard.simulator;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.Log;

/**
 * Created on 5/10/16.
 */
public class BBTactileSwViewer extends BBSwViewer {

    @Override
    public boolean pushButton(PushButtonMsg pushButtonMsg) {
        if (enclosingLeft <= pushButtonMsg.pointerX &&
                enclosingRight >= pushButtonMsg.pointerX &&
                enclosingBottom >= pushButtonMsg.pointerY &&
                enclosingTop <= pushButtonMsg.pointerY) {
            ((BBTactileModel) model).on();
            return true;
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
            ((BBTactileModel) model).off();
            return true;
        } else {
            return false;
        }
    }

    @Override
    protected void drawRotated(Canvas canvas, Paint paint) {
//        Log.i("DRAWROTATED", "left=" + left + " bottom=" + bottom);
        float w = 0;
        if (floating) {
            w = paint.getStrokeWidth();
            paint.setStrokeWidth(4 * w);
//            Log.i("strokewidth1", "w=" + w);
        }
        canvas.drawLine(left, bottom, left, top, paint);
        canvas.drawLine(right, bottom, right, top, paint);
        canvas.drawLine(left, bottom, right, bottom, paint);
        canvas.drawLine(left, top, right, top, paint);
        int cx = (left + right) / 2;
        int cy = (bottom + top) / 2;
        int d = 8;
        if (((BBTactileModel) model).isOn) {
            d = 16;
        }
        canvas.drawCircle(cx, cy, 3 * getWidth() / d, paint);
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
    public void rotateWithoutCheck(int n) {
        if (n < 0) {
            n = n + Rotate.NUM_OF_ROT;
        }
        angleIndex = (angleIndex + n) % Rotate.NUM_OF_ROT;
        int offset = rasterSize;
        if ((angleIndex % 4) == 0) {
            offset = -offset;
        }
        left = left + offset;
        right = right + offset;
        top = top + offset;
        bottom = bottom + offset;
        setEnclosingRectWithoutCheck();
    }

    @Override
    public boolean setHolesUsed(EditorViewer editorViewer) {
        Log.i("HOLUSED", "        left=" + left + " bottom=" + bottom);
        int xo = Math.round((float) (left + right) / 2f);
        int yo = Math.round((float) (bottom + top) / 2f);
        DeviceIOViewer iov = firstIoViewer;
        boolean result = false;
        while (iov != null) {
            result = iov.setHoleUsed(editorViewer, xo, yo, angleIndex);
            if (!result) {
                break;
            }
            iov = iov.next;
        }
        DeviceIOViewer iov2 = firstIoViewer;
        if (!result) {
            floating = true;
            while (iov2 != iov) {
                iov2.setHoleFree(editorViewer, xo, yo, angleIndex);
                iov2 = iov2.next;
            }
        } else {
            floating = false;
            iov = firstIoViewer;
            while (iov != null) {
                iov.netNumber = NetNumbers.get();
                iov.propagateNetNumberAt(editorViewer, xo, yo, angleIndex);
                iov = iov.next;
            }
        }
        return true;
    }

    @Override
    public void setHolesFree(EditorViewer editorViewer) {
        int xo = Math.round((float) (left + right) / 2f);
        int yo = Math.round((float) (bottom + top) / 2f);
        DeviceIOViewer iov = firstIoViewer;
        boolean result = false;
        while (iov != null) {
            iov.setHoleFree(editorViewer, xo, yo, angleIndex);
            iov = iov.next;
        }
    }


}
