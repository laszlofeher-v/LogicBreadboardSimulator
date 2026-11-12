package com.logicbreadboard.simulator;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.Log;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.util.HashMap;
import java.util.LinkedList;

/**
 * Created by laci on 6/26/15.
 */
public class DeviceIOViewer extends Viewer {
    static public final int INVERTER_CIRCLE_R = 2;
    static final short LENGTH = 8;
    static final short CLEARANCE = LENGTH;
    protected int angleIndex;
    DeviceIOViewer next;
    DeviceIOModel model;
    short startX; // relative to device
    short startY;
    short endX;
    short endY;
    boolean inverted = false;
    boolean selected = false;
    boolean locked = false;
    int netNumber;

    //    Viewer connection;

    DeviceIOViewer(short x0, short y0, short x1, short y1, boolean inv) {
        startX = x0;
        startY = y0;
        endX = x1;
        endY = y1;
        inverted = inv;
        netNumber = NetNumbers.get();
    }

    public void setSelected() {
        selected = true;
    }

    public void setUnSelected() {
        selected = false;
    }

    //todo addCandidate save load and call from deviceviewer
    @Override
    void load(DataInputStream inputStream) throws java.io.IOException {
        inverted = inputStream.readBoolean();
        netNumber = inputStream.readInt();
        Log.i("LOAD", "netNumber=" + netNumber);
    }

    void save(DataOutputStream outputStream) throws java.io.IOException {
        outputStream.writeBoolean(inverted);
        outputStream.writeInt(netNumber);
    }

    public void makeLogicalConnection(HashMap<Integer, DeviceIOModel> nets) {
        DeviceIOModel m = nets.get(netNumber);
        if (m != null) {
            m.connectTo(this.model);
        } else {
            nets.put(netNumber, this.model);
        }
        model.setNetNumber(netNumber);
    }

    @Override
    public int getNetNumber() {
        return netNumber;
    }

    @Override
    public void setNetNumber(int netNumber) {
        this.netNumber = netNumber;
    }

    public void putMask(Router router) {
        boolean maskEnabled = (netNumber != router.startNet && netNumber != router.targetNet);
        int left, right, bottom, top;
        if (startX < endX) {
            left = startX;
//            if (selected || !maskEnabled) {
            if (!maskEnabled) {
                right = endX;
            } else {
                right = endX + CLEARANCE;
            }
        } else if (startX > endX) {
//            if (selected || !maskEnabled) {
            if (!maskEnabled) {
                left = endX + 1;
            } else {
                left = endX - CLEARANCE;
            }
            right = startX + 2;
        } else {
            if (inverted) {
                left = startX - INVERTER_CIRCLE_R;
                right = startX + 2 + INVERTER_CIRCLE_R;
            } else {
                left = startX - 2;
                right = startX + 3;
            }
        }
        if (startY < endY) {
            top = startY;
//            if (selected || !maskEnabled) {
            if (!maskEnabled) {
                bottom = endY;
            } else {
                bottom = endY + CLEARANCE;
            }
        } else if (startY > endY) {
//            if (selected || !maskEnabled) {
            if (!maskEnabled) {
                top = endY + 1;
            } else {
                top = endY - CLEARANCE;
            }
            bottom = startY + 2;
        } else {
            if (inverted) {
                top = startY - INVERTER_CIRCLE_R;
                bottom = startY + 2 + INVERTER_CIRCLE_R;
            } else {
                top = startY - 2;
                bottom = startY + 3;
            }
        }
        router.addRectMask(left, bottom, right, top);
//        short m = router.mask[endX][endY];
    }

    public void putStartAndTarget(Router router) {
        if (netNumber == router.startNet) {
//            router.addQueue(endX, endY, (short) 0, Router.allEnabled);
            router.addCQueue(endX, endY, (short) 0, Router.allEnabled);
        } else if (netNumber == router.targetNet) {
            router.setMask(endX, endY, Router.TARGET_PIXEL);
        }
    }

//    @Override
//    public void drawToolTip(Canvas canvas) {
//        canvas.drawCircle((startX + endX) / 2, (startY + endY) / 2, Math.min(Math.abs(startX - endX), Math.abs(startY - endY)) / 8, Context.toolTipPaint);
//    }


    protected void draw(Canvas canvas, int xo, int yo, Paint paint) {
//        int color = paint.getColor();
        if (selected) {
            paint = Context.selectedPaint;
        } else {
            if (!locked) {
                paint = Context.unlockedPaint;
            } else {
                paint = Context.lockedPaint;
            }
        }
        if (EditorModel.simulationIsRunning && Settings.displayWireLevel) {
            paint = Viewer.Context.getLevelColorPaint(model.level.ordinal());
        }
        rawDraw(canvas, xo, yo, paint);
        if (netNumberShowEnabled) {
            if (startX > endX) {
                canvas.drawText(Integer.toString(netNumber), xo + startX, yo + startY, Context.netNumberPaint);
            } else if (startX == endX) {
                canvas.drawText(Integer.toString(netNumber), xo + startX - LENGTH, yo + startY, Context.netNumberPaint);
            } else {
                canvas.drawText(Integer.toString(netNumber), xo + startX, yo + startY,
                        Context.netNumberPaint);
            }
        }
        if (false) {
            if (startX > endX) {
                canvas.drawCircle(xo + (startX + endX) / 2, yo + (startY + endY) / 2, (startX - endX) / 4, Context
                        .toolTipPaint);
            } else if (startX == endX) {
                canvas.drawCircle(xo + (startX + endX) / 2, yo + (startY + endY) / 2, Math.abs(startY - endY) / 4, Context
                        .toolTipPaint);
            } else {
                canvas.drawCircle(xo + (startX + endX) / 2, yo + (startY + endY) / 2, (endX - startX) / 4, Context
                        .toolTipPaint);
            }
        }
        canvas.drawCircle((startX + endX) / 2, (startY + endY) / 2, Math.min(Math.abs(startX - endX), Math.abs(startY - endY)) / 8, Context.toolTipPaint);

//        paint.setColor(color);
    }

    private void rawDraw(Canvas canvas, int xo, int yo, Paint paint) {
        if (inverted) {
            float circleOffset = 2 * (INVERTER_CIRCLE_R + Viewer.STROKE_WITH);
            if (startX < endX) {
                canvas.drawCircle(xo + startX + INVERTER_CIRCLE_R + Viewer.STROKE_WITH,
                        yo + startY, INVERTER_CIRCLE_R, paint);
                canvas.drawLine(xo + startX + circleOffset, yo + startY, xo + endX, yo + endY, paint);
            } else if (startX > endX) {
                canvas.drawCircle(xo + startX - INVERTER_CIRCLE_R - Viewer.STROKE_WITH,
                        yo + startY, INVERTER_CIRCLE_R, paint);
                canvas.drawLine(xo + startX - circleOffset, yo + startY, xo + endX, yo + endY, paint);
            } else if (startY < endY) {
                canvas.drawCircle(xo + startX,
                        yo + startY + INVERTER_CIRCLE_R + Viewer.STROKE_WITH, INVERTER_CIRCLE_R, paint);
                canvas.drawLine(xo + startX, yo + startY + circleOffset, xo + endX, yo + endY, paint);
            } else if (startY > endY) {
                canvas.drawCircle(xo + startX,
                        yo + startY - INVERTER_CIRCLE_R - Viewer.STROKE_WITH,
                        INVERTER_CIRCLE_R, paint);
                canvas.drawLine(xo + startX, yo + startY - circleOffset, xo + endX, yo + endY, paint);
            }
        } else {
            canvas.drawLine(xo + startX, yo + startY, xo + endX, yo + endY, paint);
        }
    }

    public void renumber(int netNumber, LinkedList<Integer> newNetNumbers) {
        if (this.netNumber == netNumber) {
            this.netNumber = NetNumbers.get();
            newNetNumbers.add(this.netNumber);
        }
    }

    public boolean setHoleUsed(EditorViewer editorViewer, int xo, int yo, int angleIndex) {
        Log.i("IOVSETHUSED","xo" + xo + " yo=" + yo + " startX=" + startX + " startY=" + startY);
        if (angleIndex == 0) {
            return editorViewer.setHoleUsed(xo + startX, yo + startY, this);
        } else if (angleIndex == 2) {
            return editorViewer.setHoleUsed(xo - startY, yo - startX, this);
        } else if (angleIndex == 4) {
            return editorViewer.setHoleUsed(xo - startX, yo - startY, this);
        } else if (angleIndex == 6) {
            return editorViewer.setHoleUsed(xo + startY, yo + startX, this);
        } else {
            return editorViewer.setHoleUsed(xo + startX, yo + startY, this);
        }
    }

    @Override
    public void propagateNetNumber(BreadBoardViewer breadBoardViewer, int netNumber) {
        this.netNumber = netNumber;
    }

    public void setHoleFree(EditorViewer editorViewer, int xo, int yo, int angleIndex) {
        Log.i("IOVSETHFREE","xo" + xo + " yo=" + yo + " startX=" + startX + " startY=" + startY);
        if (angleIndex == 0) {
            editorViewer.setHoleFree(xo + startX, yo + startY);
        } else if (angleIndex == 2) {
            editorViewer.setHoleFree(xo - startY, yo - startX);
        } else if (angleIndex == 4) {
            editorViewer.setHoleFree(xo - startX, yo - startY);
        } else if (angleIndex == 6) {
            editorViewer.setHoleFree(xo + startY, yo + startX);
        } else {
            editorViewer.setHoleFree(xo - startX, yo - startY);
        }
    }

    public void propagateNetNumberAt(EditorViewer editorViewer, int xo, int yo, int angleIndex) {
        if (angleIndex == 0) {
            editorViewer.propagateNetNumberAt(xo + startX, yo + startY, netNumber);
        } else if (angleIndex == 2) {
            editorViewer.propagateNetNumberAt(xo - startY, yo - startX, netNumber);
        } else if (angleIndex == 4) {
            editorViewer.propagateNetNumberAt(xo - startX, yo - startY, netNumber);
        } else if (angleIndex == 6) {
            editorViewer.propagateNetNumberAt(xo + startY, yo + startX, netNumber);
        }
    }
}
