package com.logicbreadboard.simulator;

import android.app.Activity;
import android.graphics.Canvas;
import android.graphics.Paint;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.util.HashMap;
import java.util.LinkedList;

/**
 * Created on 12/31/14.
 */
public class DeviceViewer extends Viewer {
    static final int CLEARANCE = 4;
    //    static protected final int OUT_LENGTH = 8;
//    static protected final int IN_LENGTH = 8;
//    static protected final int INVERTER_CIRCLE_R = 2;
    static private final int CONTEXT_MENU_MASK =
            ContextMenuIcons.COPY |
                    ContextMenuIcons.DELETE |
                    ContextMenuIcons.DELAY |
                    ContextMenuIcons.ROTATE |
                    ContextMenuIcons.ALIGN_HORIZONTAL | ContextMenuIcons.ALIGN_VERTICAL |
                    ContextMenuIcons.LOCK | ContextMenuIcons.UNLOCK |
                    ContextMenuIcons.UNSELECT;
    static protected float selectionZoom = 1f;
    // The reason of distinction:
    static DeviceIOViewer staticIO = new DeviceIOViewer((short) 0, (short) 0, (short) 0, (short) 0, false);
    protected int left; // when not rotated
    protected int top;
    protected int right;
    protected int bottom;
    protected int enclosingLeft; // when rotated
    protected int enclosingTop;
    protected int enclosingRight;
    protected int enclosingBottom;
    protected int angleIndex;
    protected short numberOfIos;
    Model model;
    DeviceIOViewer firstIoViewer;

    DeviceViewer() {
    }

    public void powerOff() {

    }

    public void registrateModelForChange(int time) {
        if (model != null && model instanceof DeviceModel) {
            DeviceModel.registrateModelForChange((DeviceModel) model, time);
        }
    }


    // The editor calls this when zoom changed
    static public void setSelectionZoom(float zoom) {
        selectionZoom = zoom;
    }

    @Override
    public int getContextMenuMask() {
        return CONTEXT_MENU_MASK;
    }

    public int getLeft() {
        return enclosingLeft;
    }

    public int getTop() {
        return enclosingTop;
    }

    public int getBottom() {
        return enclosingBottom;
    }

    public int getRight() {
        return enclosingRight;
    }

    public void reset() {
        model.reset();
    }

    public void delete() {
        model.delete();
    }

    public void restoreCanvas(Canvas canvas) {
        if (angleIndex != 0) {
            canvas.restore();
        }
    }

    public void connectPower(Powered powered) {
        if (model != null) {
            model.connectPower(powered);
        }
    }

    void initPosition(int left, int bottom, int angleIndex) {
        this.left = left;
        this.bottom = bottom;
        this.angleIndex = angleIndex;
    }

    private void copyInvertedFlag(DeviceIOViewer from, DeviceIOViewer to) {
        while (from != null) {
            to.inverted = from.inverted;
            from = from.next;
            to = to.next;
        }
    }

    @Override
    void clone(int x, int y, Viewer old) {
        this.numberOfIos = ((DeviceViewer) old).numberOfIos;
        initPosition(x, y, (((DeviceViewer) old).angleIndex));
        setNumberOfIos(((DeviceViewer) old).numberOfIos);
        copyInvertedFlag(((DeviceViewer) old).firstIoViewer, firstIoViewer);
        if (model.delays != null) {
            System.arraycopy(((DeviceViewer) old).model.delays, 0, model.delays, 0, model.delays.length);
        }
    }

    @Override
    public void select(SelectMsg msg) {
        if (enclosingLeft - msg.epsilon[0] <= msg.pointerX &&
                enclosingRight + msg.epsilon[0] >= msg.pointerX &&
                enclosingBottom + msg.epsilon[0] >= msg.pointerY &&
                enclosingTop - msg.epsilon[0] <= msg.pointerY) {
            msg.viewer = this;
        }
    }

    @Override
    public void search(SearchMsg msg) {
        if (enclosingLeft <= msg.pointerX &&
                enclosingRight >= msg.pointerX &&
                enclosingBottom >= msg.pointerY &&
                enclosingTop <= msg.pointerY) {
            msg.addFound(this);
        }
    }

    @Override
    public void cursorStep(Cursor cursor) {
        switch (angleIndex) {
            case Rotate.R_0:
                cursor.set(cursor.getX(), cursor.getY() - Math.round(3 * Math.abs(enclosingTop -
                        enclosingBottom) / 2));
                break;
            case Rotate.R_90:
                cursor.set(cursor.getX() - Math.round(3 * Math.abs(enclosingLeft -
                        enclosingRight) / 2), cursor.getY());
                break;
            case Rotate.R_180:
                cursor.set(cursor.getX(), cursor.getY() + Math.round(3 * Math.abs(enclosingTop -
                        enclosingBottom) / 2));
                break;
            case Rotate.R_270:
                cursor.set(cursor.getX() + Math.round(3 * Math.abs(enclosingLeft -
                        enclosingRight) / 2), cursor.getY());
                break;
            default:
        }
    }

    @Override
    int getBaseX() {
        return left;
    }

    @Override
    int getBaseY() {
        return bottom;
    }

    @Override
    void save(DataOutputStream outputStream) throws java.io.IOException {
//        Log.i("DEVSAVE", "left=" + left + " bottom=" + bottom);
        outputStream.writeShort(getGroup());
        outputStream.writeShort(getSymbol());
        outputStream.writeInt(left);
        outputStream.writeInt(bottom);
        outputStream.write(angleIndex);
        outputStream.writeShort(numberOfIos);
        saveIos(outputStream, firstIoViewer);
    }

    void load(DataInputStream inputStream) throws java.io.IOException {
        int left = inputStream.readInt();
        int bottom = inputStream.readInt();
//        Log.i("DEVLOAD", "left=" + left + " bottom=" + bottom);
        byte angleIndex = inputStream.readByte();
        numberOfIos = inputStream.readShort();
        initPosition(left, bottom, angleIndex);
        setNumberOfIos(numberOfIos);
        loadIos(inputStream, firstIoViewer);
    }

    protected void loadIos(DataInputStream inputStream, DeviceIOViewer io) throws java.io
            .IOException {
        while (io != null) {
            io.load(inputStream);
//            System.out.println("Load net=" + io.netNumber + " y=" + io.endY);
            io = io.next;
        }
    }

    protected void saveIos(DataOutputStream outputStream, DeviceIOViewer io) throws java.io.IOException {
        while (io != null) {
//            System.out.println("Save net=" + io.netNumber + " y=" + io.endY);
            io.save(outputStream);
            io = io.next;
        }
    }

    public void rotateCanvas(Canvas canvas) {
        if (angleIndex != 0) {
            canvas.save();
            canvas.rotate(Rotate.angles[angleIndex], (left + right) / 2, (top + bottom) / 2);
        }
    }

    public void rotate(int n) {
        if (n < 0) {
            n = n + Rotate.NUM_OF_ROT;
        }
        angleIndex = (angleIndex + n) % Rotate.NUM_OF_ROT;
        setEnclosingRect();
    }

    public void rotateWithoutCheck(int n) {
        if (n < 0) {
            n = n + Rotate.NUM_OF_ROT;
        }
        angleIndex = (angleIndex + n) % Rotate.NUM_OF_ROT;
        setEnclosingRectWithoutCheck();
    }

    public void drawEnlarged(Canvas canvas, float zoom) {
        selectionZoom = zoom;
        canvas.save();
//        xOrigo = canvasWidth / 2;
//        yOrigo = canvasHeight / 2;
//        canvas.translate(xOffset, yOffset);
//        canvas.scale(scaleFactor, scaleFactor, xOrigo - xOffset, yOrigo - yOffset);


        canvas.scale(zoom, zoom, left + (right - left) / 2, top + (bottom - top) / 2);
        draw(canvas);
        canvas.restore();
    }

    @Override
    float getSelectionZoom(int canvasHeight, int canvasWidth, float scaleFactor) {
        return Math.min(canvasWidth / (2 * (right - left)),
                canvasHeight / (2 * (bottom - top))) / (scaleFactor);
//        return Math.max(canvasWidth / (8 * (right - left)),
//                canvasHeight / (8 * (bottom - top))) / (scaleFactor);
    }

    public void setNumberOfIos(short numberOfIos) {
        this.numberOfIos = numberOfIos;
    }

    @Override
    public boolean alignHorizontal(EditorViewer editorViewer, int x, int toBottom) {
        int dy = toBottom - bottom;
        int dx = x - left;
        if (moveCheck(dx, dy)) {
            move(editorViewer, dx, dy, CHECK_INSIDE);
        } else {
            return false;
        }
        return true;
    }

    @Override
    public boolean moveCheck(int dx, int dy) {
        int testEnclosingLeft = 0;
        int testEnclosingRight = 0;
        int testEnclosingTop = 0;
        int testEnclosingBottom = 0;

        if (angleIndex == Rotate.R_0 || angleIndex == Rotate.R_180) {
            testEnclosingLeft = left + STROKE_WITH + dx;
            testEnclosingRight = right + STROKE_WITH + dx;
            testEnclosingTop = top + STROKE_WITH + dy;
            testEnclosingBottom = bottom + STROKE_WITH + dy;
        } else if (angleIndex == Rotate.R_90 || angleIndex == Rotate.R_270) {
            final float xOrigo = (left + right) / 2 + STROKE_WITH;
            final float yOrigo = (bottom + top) / 2 + STROKE_WITH;
            final float halfHeight = (bottom - top) / 2;
            final float halfWidth = (right - left) / 2;
            testEnclosingLeft = Math.round(xOrigo - halfHeight - STROKE_WITH) + dx;
            testEnclosingRight = Math.round(xOrigo + halfHeight + STROKE_WITH) + dx;
            testEnclosingTop = Math.round(yOrigo - halfWidth - STROKE_WITH) + dy;
            testEnclosingBottom = Math.round(yOrigo + halfWidth + STROKE_WITH) + dy;
        } else {
//            System.out.println("Wrong angleIndex:" + angleIndex);
        }
        boolean ok = true;
        float underX = testEnclosingLeft - DeviceIOViewer.LENGTH - 1;
        if (underX < 0) {
            ok = false;
        }
        float overX = testEnclosingRight - EditorModel.MAX_X + DeviceIOViewer.LENGTH + 1;
        if (overX > 0) {
            ok = false;
        }
        float underY = testEnclosingTop - DeviceIOViewer.LENGTH - 1;
        if (underY < 0) {
            ok = false;
        }
        float overY = testEnclosingBottom - EditorModel.MAX_Y + DeviceIOViewer.LENGTH + 1;
        if (overY > 0) {
            ok = false;
        }
        return ok;
    }

    @Override
    void move(EditorViewer editorViewer, int dx, int dy, boolean checkInside) {
        left += dx;
        right += dx;
        top += dy;
        bottom += dy;
//        Log.i("LRTB", left + " " + right + " " + top + " " + bottom);
        if (checkInside) {
            setEnclosingRect();
        } else {
            setEnclosingRectWithoutCheck();
        }
    }

    @Override
    public void putRestrictedMask(Router router, Viewer vStart, Viewer vEnd) {
        router.addRectMask(enclosingLeft - CLEARANCE,
                enclosingBottom + CLEARANCE - STROKE_WITH,
                enclosingRight + CLEARANCE,
                enclosingTop - CLEARANCE);
        DeviceIOViewer ioViewer = firstIoViewer;
        while (ioViewer != null) {
            DeviceIOViewer p = getIoCanvasPos(ioViewer);
            if (ioViewer.netNumber != router.startNet && ioViewer.netNumber != router.targetNet || (
                    ioViewer == vStart || ioViewer == vEnd)) {
                p.putMask(router);
            }
            ioViewer = ioViewer.next;
        }
    }

    @Override
    public void putMask(Router router) {
        router.addRectMask(enclosingLeft - CLEARANCE,
                enclosingBottom + CLEARANCE - STROKE_WITH,
                enclosingRight + CLEARANCE,
                enclosingTop - CLEARANCE);
        DeviceIOViewer ioViewer = firstIoViewer;
        while (ioViewer != null) {
            DeviceIOViewer p = getIoCanvasPos(ioViewer);
            p.putMask(router);
            ioViewer = ioViewer.next;
        }
    }

    @Override
    public void putStartAndTarget(Router router) {
        DeviceIOViewer ioViewer = firstIoViewer;
        while (ioViewer != null) {
            DeviceIOViewer p = getIoCanvasPos(ioViewer);
            p.putStartAndTarget(router);
            ioViewer = ioViewer.next;
        }
    }

    @Override
    public void putRestrictedStartAndTarget(Router router, Viewer vStart, Viewer vEnd) {
        DeviceIOViewer ioViewer = firstIoViewer;
        while (ioViewer != null) {
            DeviceIOViewer p = getIoCanvasPos(ioViewer);
            if (ioViewer.netNumber != router.startNet && ioViewer.netNumber != router.targetNet || (
                    ioViewer == vStart || ioViewer == vEnd)) {
                p.putStartAndTarget(router);
            }
            ioViewer = ioViewer.next;
        }
    }


    @Override
    public void showDelayPopup(Activity activity) {
        model.showDelayPopup(activity);
    }

    @Override
    public void setNetNumberAt(EditorModel model, int netNumber, int x, int y) {
        DeviceIOViewer ioViewer = firstIoViewer;
        while (ioViewer != null) {
            if (ioViewer.netNumber != netNumber) {
                DeviceIOViewer ip = getIoCanvasPos(ioViewer);
                if (ip.endX == x && ip.endY == y) {
                    //               NetNumbers.drop(ioViewer.netNumber);
                    ioViewer.netNumber = netNumber;
                }
            }
            ioViewer = ioViewer.next;
        }
    }

    @Override
    public int reconnect(EditorModel model, int netNumber, int x, int y) {
        int found = 0;
        DeviceIOViewer ioViewer = firstIoViewer;
        while (ioViewer != null) {
//            if (ioViewer.netNumber != netNumber) {
            DeviceIOViewer ip = getIoCanvasPos(ioViewer);
            if (ip.endX == x && ip.endY == y) {
                ioViewer.netNumber = netNumber;
                ioViewer.locked = true;
                found = DEVICE_IO_FOUND;
            }
//            }
            ioViewer = ioViewer.next;
        }
        return found;
    }

    @Override
    public int reconnectInX(EditorModel editorModel, int netNumber, int startX, int endX, int y) {
        int found = 0;
        DeviceIOViewer ioViewer = firstIoViewer;
        while (ioViewer != null) {
//            if (ioViewer.netNumber != netNumber) {
            DeviceIOViewer ip = getIoCanvasPos(ioViewer);
            if (startX <= ip.endX && ip.endX <= endX && ip.endY == y) {
                ioViewer.netNumber = netNumber;
                ioViewer.locked = true;
                found = DEVICE_IO_FOUND;
            }
//            }
            ioViewer = ioViewer.next;
        }
        return found;
    }

    @Override
    public int reconnectInY(EditorModel editorModel, int netNumber, int x, int startY, int endY) {
        int found = 0;
        DeviceIOViewer ioViewer = firstIoViewer;
        while (ioViewer != null) {
//            if (ioViewer.netNumber != netNumber) {
            DeviceIOViewer ip = getIoCanvasPos(ioViewer);
            if (ip.endX == x && startY <= ip.endY && ip.endY <= endY) {
                ioViewer.netNumber = netNumber;
                ioViewer.locked = true;
                found = DEVICE_IO_FOUND;
            }
//            }
            ioViewer = ioViewer.next;
        }
        return found;
    }

    public void reconnectFrom(EditorModel em) {
        // the device ios have new, unconnected netnumbers
        DeviceIOViewer ioViewer = firstIoViewer;
        while (ioViewer != null) {
//            if (ioViewer.locked) {
            ioViewer.netNumber = NetNumbers.get();
//                ioViewer.locked = false;
//            }
            ioViewer = ioViewer.next;
        }
        ioViewer = firstIoViewer;
        while (ioViewer != null) {
            DeviceIOViewer ioCanvas = getIoCanvasPos(ioViewer);
            ioViewer.locked = em.reconnect(this, ioViewer.netNumber, ioCanvas.endX, ioCanvas
                    .endY) == Viewer.WIRE_ENDPOINT_FOUND;
//            System.out.println("netNumber=" + ioViewer.netNumber + "   ioViewer.locked=" + ioViewer
//                    .locked);
//            System.out.flush();
            if (!ioViewer.locked) {
                ioViewer.netNumber = NetNumbers.get();
            }
            ioViewer = ioViewer.next;
        }
    }

    @Override
    public void disconnect(EditorModel editorModel, int netNumber, int x, int y) {
        DeviceIOViewer ioViewer = firstIoViewer;
        while (ioViewer != null) {
            DeviceIOViewer ioCanvas = getIoCanvasPos(ioViewer);
            if (netNumber == ioViewer.netNumber &&
                    ioCanvas.endX == x && ioCanvas.endY == y) {
//                editorModel.disconnect(this, editorModel, ioCanvas.netNumber, ioCanvas.endX, ioCanvas.endY);
                ioViewer.locked = false;
            }
            ioViewer = ioViewer.next;
        }
    }

    @Override
    public void disconnectFrom(EditorModel editorModel) {
        DeviceIOViewer ioViewer = firstIoViewer;
        while (ioViewer != null) {
            DeviceIOViewer ioCanvas = getIoCanvasPos(ioViewer);
            editorModel.disconnect(this, editorModel, ioCanvas.netNumber, ioCanvas.endX, ioCanvas.endY);
            ioViewer.locked = false;
            ioViewer = ioViewer.next;
        }
    }

    public void checkConnection(EditorModel em) {
        // the device ios have new, unconnected netnumbers
        DeviceIOViewer ioViewer = firstIoViewer;
        while (ioViewer != null) {
            DeviceIOViewer ioCanvas = getIoCanvasPos(ioViewer);
            int outerNetNumber = em.getNetNumberAt(this, ioCanvas.endX, ioCanvas
                    .endY);
            if (ioViewer.netNumber != outerNetNumber) {
                if (outerNetNumber == NetNumbers.NOT_CONNECTED) {
                    ioViewer.locked = false;
                } else {
//                    System.out.println("*****************Network is corrupted**************");
//                    System.out.flush();
                    FaultViewer.newFaultViewer(ioViewer.model, FaultViewer.FaultType.NOT_CONNECTED);
                }
            } else {
                ioViewer.locked = true;
            }
            ioViewer = ioViewer.next;
        }
    }

    @Override
    public void prepareReroute(LinkedList<Integer> netList) {
        DeviceIOViewer ioViewer = firstIoViewer;
        while (ioViewer != null) {
            netList.add(ioViewer.netNumber);
            ioViewer = ioViewer.next;
        }
    }

    @Override
    public String reroute(EditorModel editorModel) {
        DeviceIOViewer ioViewer = firstIoViewer;
        while (ioViewer != null) {
            int savedNetNumber = ioViewer.netNumber;
            String res = editorModel.rerouteNet(ioViewer.netNumber);
            if (res != null) {
                ioViewer.netNumber = savedNetNumber;
                return res;
            }
            ioViewer = ioViewer.next;
        }
        return null;
    }


    @Override
    public boolean setFaultPos(FaultViewer fv) {
        DeviceIOViewer ioViewer = firstIoViewer;
        while (ioViewer != null) {
            if (ioViewer.model == fv.faultyIo) {
                DeviceIOViewer cio = getIoCanvasPos(ioViewer);
                fv.x = cio.endX;
                fv.y = cio.endY;
                return true;
            }
            ioViewer = ioViewer.next;
        }
        return false;
    }


    @Override
    public void routeNetNumber(Router router) {
        DeviceIOViewer ioViewer = firstIoViewer;
        while (ioViewer != null) {
            if (ioViewer.netNumber == router.targetNet) {
                ioViewer.netNumber = router.startNet;
            }
            ioViewer = ioViewer.next;
        }
//        ioViewer = firstOutputViewer;
//        while (ioViewer != null) {
//            if (ioViewer.getNetNumber() == router.targetNet) {
//                ioViewer.netNumber = router.startNet;
//            }
//            ioViewer = ioViewer.next;
//        }
    }

    @Override
    public DeviceIOViewer getIoCanvasPos(DeviceIOViewer ioViewer) {
        if (angleIndex == Rotate.R_0) {
            staticIO.startX = (short) ((right + left) / 2 + ioViewer.startX);
            staticIO.endX = (short) ((right + left) / 2 + ioViewer.endX);
            staticIO.startY = (short) ((bottom + top) / 2 + ioViewer.startY);
            staticIO.endY = (short) ((bottom + top) / 2 + ioViewer.endY);
        } else if (angleIndex == Rotate.R_180) {
            staticIO.startX = (short) ((right + left) / 2 - ioViewer.startX);
            staticIO.endX = (short) ((right + left) / 2 - ioViewer.endX);
            staticIO.startY = (short) ((bottom + top) / 2 - ioViewer.startY);
            staticIO.endY = (short) ((bottom + top) / 2 - ioViewer.endY);
        } else if (angleIndex == Rotate.R_90) {
            staticIO.startX = (short) ((right + left) / 2 - ioViewer.startY);
            staticIO.endX = (short) ((right + left) / 2 - ioViewer.endY);
            staticIO.startY = (short) ((bottom + top) / 2 + ioViewer.startX);
            staticIO.endY = (short) ((bottom + top) / 2 + ioViewer.endX);
        } else if (angleIndex == Rotate.R_270) {
            staticIO.startX = (short) ((right + left) / 2 + ioViewer.startY);
            staticIO.endX = (short) ((right + left) / 2 + ioViewer.endY);
            staticIO.startY = (short) ((bottom + top) / 2 - ioViewer.startX);
            staticIO.endY = (short) ((bottom + top) / 2 - ioViewer.endX);
        } else {
//            System.out.println("Wrong angleIndex:" + angleIndex);
        }
        //       staticIO.selected = ioViewer.selected;
        staticIO.inverted = ioViewer.inverted;
        staticIO.netNumber = ioViewer.netNumber;
        staticIO.locked = ioViewer.locked;
        //       staticIO.maskEnabled=ioViewer.maskEnabled;
        return staticIO;
    }

    void reverseEnclosingRect() {
        if (angleIndex == Rotate.R_0 || angleIndex == Rotate.R_180) {
            left = enclosingLeft - STROKE_WITH;
            right = enclosingRight - STROKE_WITH;
            top = enclosingTop - STROKE_WITH;
            bottom = enclosingBottom - STROKE_WITH;
        } else if (angleIndex == Rotate.R_90 || angleIndex == Rotate.R_270) {
            final float xOrigo = (enclosingLeft + enclosingRight) / 2;
            final float yOrigo = (enclosingBottom + enclosingTop) / 2;
            final float halfHeight = (enclosingBottom - enclosingTop - 2 * STROKE_WITH) / 2;
            final float halfWidth = (enclosingRight - enclosingLeft - 2 * STROKE_WITH) / 2;
            left = Math.round(xOrigo - halfHeight);
            right = Math.round(xOrigo + halfHeight);
            top = Math.round(yOrigo - halfWidth);
            bottom = Math.round(yOrigo + halfWidth);
        } else {
//            System.out.println("Wrong angleIndex:" + angleIndex);
        }
    }

    @Override
    public void setEnclosingRect() {
//             System.out.println("setEnclosingRect:" + angleIndex);
        enclosingRect();
        boolean modified = false;
        float underX = enclosingLeft - DeviceIOViewer.LENGTH - 1;
        if (underX < 0) {
            enclosingLeft -= underX;
            enclosingRight -= underX;
            modified = true;
        }
        float overX = enclosingRight - EditorModel.MAX_X + DeviceIOViewer.LENGTH + 1;
        if (overX > 0) {
            enclosingLeft -= overX;
            enclosingRight -= overX;
            modified = true;
        }
        float underY = enclosingTop - DeviceIOViewer.LENGTH - 1;
        if (underY < 0) {
            enclosingTop -= underY;
            enclosingBottom -= underY;
            modified = true;
        }
        float overY = enclosingBottom - EditorModel.MAX_Y + DeviceIOViewer.LENGTH + 1;
        if (overY > 0) {
            enclosingTop -= overY;
            enclosingBottom -= overY;
            modified = true;
        }
        if (modified) {
            reverseEnclosingRect();
        }
    }

    public void enclosingRect() {
        if (angleIndex == Rotate.R_0 || angleIndex == Rotate.R_180) {
            enclosingLeft = left + STROKE_WITH;
            enclosingRight = right + STROKE_WITH;
            enclosingTop = top + STROKE_WITH;
            enclosingBottom = bottom + STROKE_WITH;
        } else if (angleIndex == Rotate.R_90 || angleIndex == Rotate.R_270) {
            final float xOrigo = (left + right) / 2 + STROKE_WITH;
            final float yOrigo = (bottom + top) / 2 + STROKE_WITH;
            final float halfHeight = (bottom - top) / 2;
            final float halfWidth = (right - left) / 2;
            enclosingLeft = Math.round(xOrigo - halfHeight - STROKE_WITH);
            enclosingRight = Math.round(xOrigo + halfHeight + STROKE_WITH);
            enclosingTop = Math.round(yOrigo - halfWidth - STROKE_WITH);
            enclosingBottom = Math.round(yOrigo + halfWidth + STROKE_WITH);
        } else {
//            System.out.println("Wrong angleIndex:" + angleIndex);
        }
    }

    public void setEnclosingRectWithoutCheck() {
//             System.out.println("setEnclosingRect:" + angleIndex);
        enclosingRect();
    }

    @Override
    public void draw(Canvas canvas) {
        rotateCanvas(canvas);
        drawRotated(canvas, Viewer.currentPaint);
        DeviceIOViewer ioViewer = firstIoViewer;
        int xo = Math.round((float) (left + right) / 2f);
        int yo = Math.round((float) (bottom + top) / 2f);
        do {
            ioViewer.draw(canvas, xo, yo, currentPaint);
            ioViewer = ioViewer.next;
        } while (ioViewer != null);
        restoreCanvas(canvas);
    }

//    public void drawToolTip(Canvas canvas) {
////        canvas.drawCircle((left + right) / 2, (top + bottom) / 2, Math.min(right - left, bottom - top), Context.toolTipPaint);
////        canvas.drawCircle((left + right) / 2, (top + bottom) / 2, Math.min(right - left, bottom - top) /4, Context.toolTipPaint);
//        canvas.drawCircle((left + right) / 2, (top + bottom) / 2, Math.min(right - left, bottom - top) / Viewer.tipRound, Context.toolTipPaint);
//    }

    @Override
    public boolean hasSelectedIO(Selections selection) {
        DeviceIOViewer ioViewer = firstIoViewer;
        while (ioViewer != null) {
            if (selection.isRoutableSelected(ioViewer)) {
                return true;
            }
            ioViewer = ioViewer.next;
        }
//        ioViewer = firstOutputViewer;
//        if (checkIOList(ioViewer)) {
//            return true;
//        }
        return false;
    }

    @Override
    public DeviceIOViewer findIO(int x, int y) {
        DeviceIOViewer selectedIo = null;
        x = x - (left + right) / 2;
        y = y - (top + bottom) / 2;
        if (selectionZoom != 1) {
            x = Math.round(x / selectionZoom);
            y = Math.round(y / selectionZoom);
        }
        switch (angleIndex) {
            case Rotate.R_0:
                break;
            case Rotate.R_90: {
                int tmp = x;
                x = y;
                y = -tmp;
            }
            break;
            case Rotate.R_180: {
                x = -x;
                y = -y;
            }
            break;
            case Rotate.R_270: {
                int tmp = x;
                x = -y;
                y = tmp;
            }
            break;
            default:
        }
        DeviceIOViewer ioViewer = firstIoViewer;
        float distance = Router.MAX_CONNECTION_DISTANCE;
        while (ioViewer != null) {
            float newDistance = Math.abs(x - ioViewer.endX) + Math.abs(y - ioViewer.endY);
//            System.out.println("newDistance=" + newDistance);
            if (newDistance < distance) {
                distance = newDistance;
                selectedIo = ioViewer;
            }
            ioViewer = ioViewer.next;
        }
        return selectedIo;
    }

    protected void drawRotated(Canvas canvas, Paint paint) {

    }

    @Override
    public void disconnectLogical() {
        DeviceIOViewer ioViewer = firstIoViewer;
        while (ioViewer != null) {
            ioViewer.model.disConnect();
            ioViewer = ioViewer.next;
        }
    }

    public void changeNetNumber(int netNumber, int newNetNumber) {
        DeviceIOViewer ioViewer = firstIoViewer;
        while (ioViewer != null) {
            if (ioViewer.netNumber == netNumber) {
                ioViewer.netNumber = newNetNumber;
            }
            ioViewer = ioViewer.next;
        }
    }

    @Override
    public void makeLogicalConnection(HashMap<Integer, DeviceIOModel> nets) {
        DeviceIOViewer ioViewer = firstIoViewer;
        while (ioViewer != null) {
            ioViewer.makeLogicalConnection(nets);
            ioViewer = ioViewer.next;
        }
    }

    @Override
    public void compressNetNumbers(HashMap<Integer, Integer> nets, int[] newNetNumber) {
        DeviceIOViewer ioViewer = firstIoViewer;
        while (ioViewer != null) {
            ioViewer.compressNetNumbers(nets, newNetNumber);
            ioViewer = ioViewer.next;
        }
    }

    public void renumber(int netNumber, LinkedList<Integer> newNetNumbers) {
        DeviceIOViewer ioViewer = firstIoViewer;
        while (ioViewer != null) {
            ioViewer.renumber(netNumber, newNetNumbers);
            ioViewer = ioViewer.next;
        }
    }

    private void unselectIOs(DeviceIOViewer ioViewer, Selections selections) {
        while (ioViewer != null) {
//            ioViewer.selected = false;
//            selection.syncSelection(ioViewer);
            selections.unSelectRoutable(ioViewer); // called method checks if it was selected
            ioViewer = ioViewer.next;
        }
    }

    @Override
    public void unselect(Selections selections) {
        unselectIOs(firstIoViewer, selections);
//        unselectIOs(firstOutputViewer);
    }

}
