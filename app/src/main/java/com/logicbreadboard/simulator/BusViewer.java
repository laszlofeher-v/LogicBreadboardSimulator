package com.logicbreadboard.simulator;

import android.graphics.Canvas;
import android.graphics.Paint;

import java.io.DataInputStream;
import java.io.DataOutputStream;

/**
 * Created on 1/19/15.
 */
public class BusViewer extends DeviceViewer {
    public static final int MAX_LENGTH = 6;
    private static final int HEIGHT = 4;
    //    private static final int WIDTH = 48;
    static private final int CONTEXT_MENU_MASK = ContextMenuIcons.COPY |
            ContextMenuIcons.DELETE |
            ContextMenuIcons.ROTATE |
            ContextMenuIcons.ALIGN_HORIZONTAL | ContextMenuIcons.ALIGN_VERTICAL |
            ContextMenuIcons.LOCK | ContextMenuIcons.UNLOCK |
            // TODO: 9/24/15 unlocked move does not cut net
            ContextMenuIcons.UNSELECT;
    private static final int MIN_LENGTH = 64;

    private static final int NO_SIDE = 0;
    private static final int LEFT_SIDE = 1;
    private static final int RIGHT_SIDE = 2;
    private static final int TOP_SIDE = 4;
    private static final int BOTTOM_SIDE = 8;
    private static final int ENTIRE_MOVE = 16;

    static BusIOViewer staticBusIOViewer = new BusIOViewer((short) 0, (short) 0, (short) 0, (short) 0, false);
    static private int selectedSide = NO_SIDE;
    static private float yMoveMaxDiff = 0;
    static private float yMoveDiff = 0;
    private int length = 100;
    private boolean firstMove = true;
    private float remainder = 0;

    BusViewer() {
        model = new BusModel();
    }

    @Override
    public short getGroup() {
        return (short) SchematicViewerFactory.DeviceGroup.Connections.ordinal();
    }

    @Override
    public short getSymbol() {
        return (short) SchematicViewerFactory.Connections.Bus_wire.ordinal();
    }

    @Override
    public void setDefaultNumberOfIos() {
        setNumberOfIos((short) 1);
    }

    @Override
    public int getContextMenuMask() {
        return CONTEXT_MENU_MASK;
    }

    boolean isOnY(BusIOViewer ip, int x, int y) {
        if (ip.startY == ip.endY && ip.startY == y) {
            if (ip.startX <= x && x <= ip.endX ||
                    ip.startX >= x && x >= ip.endX) {
                return true;
            }
        }
        return false;
    }

    boolean isOnX(BusIOViewer ip, int x, int y) {
        if (ip.startX == ip.endX && ip.startX == x) {
            if (ip.startY <= y && y <= ip.endY ||
                    ip.startY >= y && y >= ip.endY) {
                return true;
            }
        }
        return false;
    }

    public void endMove() {
        selectedSide = NO_SIDE;
        yMoveMaxDiff = 0;
        yMoveDiff = 0;
        firstMove = true;
        remainder = 0;
    }

    @Override
    public void drawEnlarged(Canvas canvas, float zoom) {
        draw(canvas);
    }

    private void setIo() {
        length = right - left;
//        if (angleIndex == Rotate.R_0 || angleIndex == Rotate.R_180) {
//        } else if (angleIndex == Rotate.R_90 || angleIndex == Rotate.R_270) {
//        }
        firstIoViewer.startX = (short) (-length / 2);
        firstIoViewer.endX = (short) (length / 2);
    }

    public boolean moveSegmentOrPoint(EditorViewer editorViewer, float fromX, float fromY, int
            xOffset, int yOffset) {
        float epsilon[] = {8};
//        BusIOViewer ioCanvas = getIoCanvasPos(firstIoViewer);
//        System.out.println("left=" + left + " right=" + right + " fromX=" + fromX + " " +
//                "fromY=" + fromY + " xOffset=" + xOffset + " yOffset=" + yOffset);
        fromX = fromX - (left + right) / 2;
        fromY = fromY - (top + bottom) / 2;
//        if (selectionZoom != 1) {
//            fromX = Math.round(fromX / selectionZoom);
//            fromY = Math.round(fromY / selectionZoom);
//        }
        switch (angleIndex) {
            case Rotate.R_0:
                break;
            case Rotate.R_90: {
                float tmp = fromX;
                fromX = fromY;
                fromY = -tmp;
            }
            break;
            case Rotate.R_180: {
                fromX = -fromX;
                fromY = -fromY;
            }
            break;
            case Rotate.R_270: {
                float tmp = fromX;
                fromX = -fromY;
                fromY = tmp;
            }
            break;
            default:
        }
        if (firstMove) {
            selectedSide = NO_SIDE;
            if (Math.abs((bottom - top) / 2 - fromY) < epsilon[0]) {
//                    if (Math.abs(enclosingLeft - fromX) < epsilon[0]) {
                int leftX = Math.min(firstIoViewer.startX, firstIoViewer.endX);
                int rightX = Math.max(firstIoViewer.startX, firstIoViewer.endX);
                if (Math.abs(leftX - fromX) < epsilon[0]) {
                    if (angleIndex == Rotate.R_0) {
                        selectedSide = LEFT_SIDE;
                    } else if (angleIndex == Rotate.R_180) {
                        selectedSide = RIGHT_SIDE;
                    } else if (angleIndex == Rotate.R_90) {
                        selectedSide = TOP_SIDE;
                    } else if (angleIndex == Rotate.R_270) {
                        selectedSide = BOTTOM_SIDE;
                    }
                    firstMove = false;
                } else if (Math.abs(rightX - fromX) < epsilon[0]) {
                    if (angleIndex == Rotate.R_0) {
                        selectedSide = RIGHT_SIDE;
                    } else if (angleIndex == Rotate.R_180) {
                        selectedSide = LEFT_SIDE;
                    } else if (angleIndex == Rotate.R_90) {
                        selectedSide = BOTTOM_SIDE;
                    } else if (angleIndex == Rotate.R_270) {
                        selectedSide = TOP_SIDE;
                    }
                    firstMove = false;
                }
            }
            if (selectedSide == NO_SIDE) {
                selectedSide = ENTIRE_MOVE;
            }
            firstMove = false;
        }
        if (selectedSide == LEFT_SIDE) {
            int newLeft = left + xOffset;
            int newRight = right;
//            System.out.println("LEFT_SIDE left=" + left + " right=" + right + " newLeft=" +
//                    newLeft + " " + "MIN_X=" + EditorModel.MIN_X);
            if (newRight - MIN_LENGTH > newLeft && newLeft > EditorModel.MIN_X && newRight < EditorModel
                    .MAX_X) {
                left = newLeft;
                right = newRight;
                setIo();
            }
        } else if (selectedSide == RIGHT_SIDE) {
            int newLeft = left;
            int newRight = right + xOffset;
//            System.out.println("RIGHT_SIDE left=" + left + " right=" + right + " newRight=" +
//                    newRight + " " + "MIN_X=" + EditorModel.MIN_X);
            if (newRight - MIN_LENGTH > newLeft && newLeft > EditorModel.MIN_X && newRight <
                    EditorModel.MAX_X) {
                left = newLeft;
                right = newRight;
                setIo();
//                left += xOffset;
//                right += xOffset;
            }

//            canvas.rotate(Rotate.angles[angleIndex], (left + right) / 2, (top + bottom) / 2);

        } else if (selectedSide == TOP_SIDE) {
            float o = (float) yOffset + remainder;
            yOffset = Math.round(o / 2f);
            remainder = o - 2 * (float) yOffset;
            int newTop = enclosingTop + yOffset;
            int newBottom = enclosingBottom + yOffset;
//            System.out.println("TOP_SIDE left=" + left + " right=" + right + " newTop=" + newTop
//                    + " " + "MIN_Y=" + EditorModel.MIN_Y);
            if (newTop < enclosingBottom - MIN_LENGTH && newTop > EditorModel.MIN_Y) {
                if (right - 2 * yOffset > left + 8) {
                    if (newBottom < EditorModel.MAX_Y) {
                        yOffset = yOffset;
                        right -= yOffset;
                        left += yOffset;
                        setIo();
                        top += yOffset;
                        bottom += yOffset;
                        //                   move(editorViewer, 0, yOffset);
//                        System.out.println("BOTTOM_SIDE left=" + left + " right=" + right + " " +
//                                "MIN_X=" + EditorModel.MIN_X);
                    }
                }
            }
        } else if (selectedSide == BOTTOM_SIDE) {
            float o = (float) yOffset + remainder;
            yOffset = Math.round(o / 2f);
            remainder = o - 2 * (float) yOffset;
            int newBottom = enclosingBottom + yOffset;
            int newTop = enclosingTop + yOffset;
            if (newBottom - MIN_LENGTH > enclosingTop && newBottom < EditorModel.MAX_Y) {
                if (right - 2 * yOffset > left + 8) {
                    if (newTop > EditorModel.MIN_Y) {
                        left -= yOffset;
                        right += yOffset;
                        setIo();
                        top += yOffset;
                        bottom += yOffset;

//                        left += yOffset;
//                    move(editorViewer, yOffset, 0);
                    }
                }
            }
        } else if (selectedSide == ENTIRE_MOVE) {
            return false;
            //           move(editorViewer, xOffset, yOffset);
        }
        setEnclosingRect();
        return true;
    }

    @Override
    public void setNetNumberAt(EditorModel editorModel, int netNumber, int x, int y) {
        DeviceIOViewer ioViewer = firstIoViewer;
        if (ioViewer.netNumber != netNumber) {
            BusIOViewer ioCanvas = getIoCanvasPos(ioViewer);
            if (isOnY(ioCanvas, x, y)) {
                ioViewer.netNumber = netNumber;
                editorModel.setNetNumberInsideX(netNumber,
                        Math.min(ioCanvas.startX, ioCanvas.endX),
                        Math.max(ioCanvas.startX, ioCanvas.endX), ioCanvas.startY);
            } else if (isOnX(ioCanvas, x, y)) {
                ioViewer.netNumber = netNumber;
                editorModel.setNetNumberInsideY(netNumber, ioCanvas.startX,
                        Math.min(ioCanvas.startY, ioCanvas.endY),
                        Math.max(ioCanvas.startY, ioCanvas.endY));
            }
        }
    }

    @Override
    public int reconnect(EditorModel editorModel, int netNumber, int x, int y) {
        int found = 0;
//        System.out.println(" BusViewer.reconnect netNumber:" + netNumber + " x=" + x + " y=" + y);
//        System.out.flush();
        DeviceIOViewer ioViewer = firstIoViewer;
//            if (ioViewer.netNumber != netNumber) {
        BusIOViewer ioCanvas = getIoCanvasPos(ioViewer);
        if (isOnY(ioCanvas, x, y)) {
//        System.out.println(" BusViewer.reconnect netNumber isOnY:" + netNumber + " x=" + x + " " +
//                "y=" + y);
            ioViewer.netNumber = netNumber;
            ioViewer.locked = true;
            found = DEVICE_IO_FOUND;
            editorModel.setNetNumberInsideX(netNumber,
                    Math.min(ioCanvas.startX, ioCanvas.endX),
                    Math.max(ioCanvas.startX, ioCanvas.endX), ioCanvas.startY);
        } else if (isOnX(ioCanvas, x, y)) {
//        System.out.println(" BusViewer.reconnect netNumber isOnX:" + netNumber + " x=" + x + " " +
//                "y=" + y);
            ioViewer.netNumber = netNumber;
            ioViewer.locked = true;
            found = DEVICE_IO_FOUND;
            editorModel.setNetNumberInsideY(netNumber, ioCanvas.startX,
                    Math.min(ioCanvas.startY, ioCanvas.endY),
                    Math.max(ioCanvas.startY, ioCanvas.endY));
        }
//            }
        return found;
    }

    public void reconnectFrom(EditorModel em) {
        // the device ios have new, unconnected netnumbers
//        System.out.println(" BusViewer.reconnectFrom ");
//        System.out.flush();
        firstIoViewer.netNumber = NetNumbers.get();
        DeviceIOViewer ioCanvas = getIoCanvasPos(firstIoViewer);
        if (ioCanvas.startY == ioCanvas.endY) {
            firstIoViewer.locked = em.reconnectInX(this, firstIoViewer.netNumber,
                    Math.min(ioCanvas.startX, ioCanvas.endX),
                    Math.max(ioCanvas.startX, ioCanvas.endX), ioCanvas.startY) == Viewer.WIRE_ENDPOINT_FOUND;
        } else {
            firstIoViewer.locked = em.reconnectInY(this, firstIoViewer.netNumber, ioCanvas.startX,
                    Math.min(ioCanvas.startY, ioCanvas.endY),
                    Math.max(ioCanvas.startY, ioCanvas.endY)) == Viewer.WIRE_ENDPOINT_FOUND;
        }

//            System.out.println("netNumber=" + ioViewer.netNumber + "   ioViewer.locked=" + ioViewer
//                    .locked);
//            System.out.flush();
//            if (!ioViewer.locked) {
//            System.out.println("netNumber=" + ioViewer.netNumber + " BusViewer.reconnectFrom");
//            System.out.flush();
        if (!firstIoViewer.locked) {
            firstIoViewer.netNumber = NetNumbers.get();
        }
//            }
    }

    @Override
    public void disconnect(EditorModel editorModel, int netNumber, int x, int y) {
        // TODO: 9/25/15
//        System.out.println(" BusViewer.disconnect netNumber:" + netNumber + " x=" + x + " y=" + y);
//        System.out.flush();
    }

    @Override
    public void cutNet(EditorModel editorModel) {
        disconnectFrom(editorModel);
    }

    @Override
    public void disconnectFrom(EditorModel editorModel) {
        DeviceIOViewer ioViewer = firstIoViewer;
        while (ioViewer != null) {
            DeviceIOViewer ioCanvas = getIoCanvasPos(ioViewer);
            if (ioCanvas.startY == ioCanvas.endY) {
                editorModel.disconnectInX(this, editorModel, ioCanvas.netNumber,
                        Math.min(ioCanvas.startX, ioCanvas.endX),
                        Math.max(ioCanvas.startX, ioCanvas.endX),
                        ioCanvas.startY);
            } else {
                editorModel.disconnectInY(this, editorModel, ioCanvas.netNumber, ioCanvas.startX,
                        Math.min(ioCanvas.startY, ioCanvas.endY),
                        Math.max(ioCanvas.startY, ioCanvas.endY));
            }
//            ioViewer.locked = false;
            ioViewer = ioViewer.next;
        }
    }

    @Override
    public int getNetNumber() {
        return firstIoViewer.netNumber;
    }

    public void checkConnection(EditorModel em) {
        // the device ios have new, unconnected netnumbers
        DeviceIOViewer ioViewer = firstIoViewer;
        while (ioViewer != null) {
            DeviceIOViewer ioCanvas = getIoCanvasPos(ioViewer);
            int outerNetNumber;
            if (ioCanvas.startY == ioCanvas.endY) {
                outerNetNumber = em.getNetNumbertInX(this, ioCanvas.netNumber,
                        Math.min(ioCanvas.startX, ioCanvas.endX),
                        Math.max(ioCanvas.startX, ioCanvas.endX),
                        ioCanvas.startY);
            } else {
                outerNetNumber = em.getNetNumbertInY(this, ioCanvas.netNumber, ioCanvas.startX,
                        Math.min(ioCanvas.startY, ioCanvas.endY),
                        Math.max(ioCanvas.startY, ioCanvas.endY));
            }
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

    protected void createOneIo() {
        DeviceIOViewer iov = new BusIOViewer((short) (length / 2), (short) 0,
                (short) (-length / 2), (short) 0,
                false);
        iov.model = ((DeviceModel) model).firstInputModel;
        firstIoViewer = iov;
    }

    @Override
    public void setNumberOfIos(short numberOfIos) {
        this.numberOfIos = numberOfIos;
        model.init(numberOfIos, 0);
        createOneIo();
    }

    @Override
    public void putMask(Router router) {
        DeviceIOViewer ioViewer = firstIoViewer;
        DeviceIOViewer p = getIoCanvasPos(ioViewer);
        p.putMask(router);
    }

    @Override
    public void putStartAndTarget(Router router) {
        DeviceIOViewer ioViewer = firstIoViewer;
        DeviceIOViewer p = getIoCanvasPos(ioViewer);
        p.putStartAndTarget(router);
    }

    @Override
    public void putRestrictedMask(Router router, Viewer vStart, Viewer vEnd) {
        DeviceIOViewer ioViewer = firstIoViewer;
        DeviceIOViewer p = getIoCanvasPos(ioViewer);
        if (ioViewer.netNumber != router.startNet && ioViewer.netNumber != router.targetNet || (
                ioViewer == vStart || ioViewer == vEnd)) {
            p.putMask(router);
        }
    }

    @Override
    public void putRestrictedStartAndTarget(Router router, Viewer vStart, Viewer vEnd) {
        DeviceIOViewer ioViewer = firstIoViewer;
        DeviceIOViewer p = getIoCanvasPos(ioViewer);
        if (ioViewer.netNumber != router.startNet && ioViewer.netNumber != router.targetNet || (
                ioViewer == vStart || ioViewer == vEnd)) {
            p.putStartAndTarget(router);
        }
    }

    @Override
    void load(DataInputStream inputStream) throws java.io.IOException {
        int left = inputStream.readInt();
        int bottom = inputStream.readInt();
        byte angleIndex = inputStream.readByte();
        length = inputStream.readInt();
        numberOfIos = inputStream.readShort();
        initPosition(left, bottom, angleIndex);
        setNumberOfIos(numberOfIos);
        loadIos(inputStream, firstIoViewer);
    }

    @Override
    void save(DataOutputStream outputStream) throws java.io.IOException {
        outputStream.writeShort(getGroup());
        outputStream.writeShort(getSymbol());
        outputStream.writeInt(left);
        outputStream.writeInt(bottom);
        outputStream.write(angleIndex);
        outputStream.writeInt(length);
        outputStream.writeShort(numberOfIos);
        saveIos(outputStream, firstIoViewer);
    }

    @Override
    public void initPosition(int x, int y, int angleIndex) {
        this.left = x;
        this.bottom = y;
        this.right = x + length;
        this.top = y - HEIGHT;
        this.angleIndex = angleIndex;
        setEnclosingRect();
    }

    @Override
    void clone(int x, int y, Viewer old) {
        this.numberOfIos = ((DeviceViewer) old).numberOfIos;
        length = ((BusViewer) old).length;
        initPosition(x, y, (((DeviceViewer) old).angleIndex));
        setNumberOfIos(((DeviceViewer) old).numberOfIos);
    }

    @Override
    public void setEnclosingRect() {
//        System.out.println("setEnclosingRect:" + angleIndex);
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
        boolean modified = false;
        float underX = enclosingLeft - 1;
        if (underX < 0) {
            enclosingLeft -= underX;
            enclosingRight -= underX;
            modified = true;
        }
        float overX = enclosingRight - EditorModel.MAX_X + 1;
        if (overX > 0) {
            enclosingLeft -= overX;
            enclosingRight -= overX;
            modified = true;
        }
        float underY = enclosingTop - 1;
        if (underY < 0) {
            enclosingTop -= underY;
            enclosingBottom -= underY;
            modified = true;
        }
        float overY = enclosingBottom - EditorModel.MAX_Y + 1;
        if (overY > 0) {
            enclosingTop -= overY;
            enclosingBottom -= overY;
            modified = true;
        }
        if (modified) {
            reverseEnclosingRect();
        }
    }

    @Override
    public void search(SearchMsg msg) {
        BusIOViewer canvasIOViwer = getIoCanvasPos(firstIoViewer);
        if (canvasIOViwer.startY == canvasIOViwer.endY) {
            if (Math.abs(canvasIOViwer.startY - msg.pointerY) <= msg.epsilon[0]) {
                if (canvasIOViwer.startX < msg.pointerX && msg.pointerX < canvasIOViwer.endX ||
                        canvasIOViwer.startX > msg.pointerX && msg.pointerX > canvasIOViwer.endX) {
                    msg.addFound(this);
                }
            }
        } else {
            if (Math.abs(canvasIOViwer.startX - msg.pointerX) <= msg.epsilon[0]) {
                if (canvasIOViwer.startY < msg.pointerY && msg.pointerY < canvasIOViwer.endY ||
                        canvasIOViwer.startY > msg.pointerY && msg.pointerY > canvasIOViwer.endY) {
                    msg.addFound(this);
                }
            }
        }
    }


    @Override
    public void select(SelectMsg selectMsg) {
//        if (enclosingLeft - msg.epsilon[0] <= msg.pointerX &&
//                enclosingRight + msg.epsilon[0] >= msg.pointerX &&
//                enclosingBottom + msg.epsilon[0] >= msg.pointerY &&
//                enclosingTop - msg.epsilon[0] <= msg.pointerY) {
//            msg.viewer = this;
//        }
        BusIOViewer canvasIOViwer = getIoCanvasPos(firstIoViewer);
        if (canvasIOViwer.startY == canvasIOViwer.endY) {
            if (Math.abs(canvasIOViwer.startY - selectMsg.pointerY) <= selectMsg.epsilon[0]) {
                if (canvasIOViwer.startX <= selectMsg.pointerX && selectMsg.pointerX <=
                        canvasIOViwer.endX ||
                        canvasIOViwer.startX >= selectMsg.pointerX && selectMsg.pointerX >=
                                canvasIOViwer.endX) {
                    selectMsg.viewer = this;
                    selectMsg.ioViewer = firstIoViewer;
                }
            }
        } else {
            if (Math.abs(canvasIOViwer.startX - selectMsg.pointerX) <= selectMsg.epsilon[0]) {
                if (canvasIOViwer.startY <= selectMsg.pointerY && selectMsg.pointerY <=
                        canvasIOViwer.endY ||
                        canvasIOViwer.startY >= selectMsg.pointerY && selectMsg.pointerY >=
                                canvasIOViwer.endY) {
                    selectMsg.viewer = this;
                    selectMsg.ioViewer = firstIoViewer;
                }
            }
        }
    }

    public void forcedSelect(SelectMsg selectMsg) {
        selectMsg.viewer = this;
        selectMsg.ioViewer = firstIoViewer;
    }


    @Override
    public void unselect(Selections selections) {
        selections.unSelectRoutable(firstIoViewer);
        firstIoViewer.setUnSelected();
//        System.out.println("unselect");
    }

    @Override
    protected void drawRotated(Canvas canvas, Paint paint) {
        if (paint== Context.selectedPaint || paint==Context.multiSelectedPaint) {
            canvas.drawLine(left, bottom, left, top, paint);
            canvas.drawLine(right, bottom, right, top, paint);
            canvas.drawLine(left, bottom, right, bottom, paint);
            canvas.drawLine(left, top, right, top, paint);
        }
    }

    @Override
    public BusIOViewer getIoCanvasPos(DeviceIOViewer ioViewer) {
        if (angleIndex == Rotate.R_0) {
            staticBusIOViewer.startX = (short) ((right + left) / 2 + ioViewer.startX);
            staticBusIOViewer.endX = (short) ((right + left) / 2 + ioViewer.endX);
            staticBusIOViewer.startY = (short) ((bottom + top) / 2 + ioViewer.startY);
            staticBusIOViewer.endY = (short) ((bottom + top) / 2 + ioViewer.endY);
        } else if (angleIndex == Rotate.R_180) {
            staticBusIOViewer.startX = (short) ((right + left) / 2 - ioViewer.startX);
            staticBusIOViewer.endX = (short) ((right + left) / 2 - ioViewer.endX);
            staticBusIOViewer.startY = (short) ((bottom + top) / 2 - ioViewer.startY);
            staticBusIOViewer.endY = (short) ((bottom + top) / 2 - ioViewer.endY);
        } else if (angleIndex == Rotate.R_90) {
            staticBusIOViewer.startX = (short) ((right + left) / 2 - ioViewer.startY);
            staticBusIOViewer.endX = (short) ((right + left) / 2 - ioViewer.endY);
            staticBusIOViewer.startY = (short) ((bottom + top) / 2 + ioViewer.startX);
            staticBusIOViewer.endY = (short) ((bottom + top) / 2 + ioViewer.endX);
        } else if (angleIndex == Rotate.R_270) {
            staticBusIOViewer.startX = (short) ((right + left) / 2 + ioViewer.startY);
            staticBusIOViewer.endX = (short) ((right + left) / 2 + ioViewer.endY);
            staticBusIOViewer.startY = (short) ((bottom + top) / 2 - ioViewer.startX);
            staticBusIOViewer.endY = (short) ((bottom + top) / 2 - ioViewer.endX);
        } else {
//            System.out.println("Wrong angleIndex:" + angleIndex);
        }
        //       staticIO.selected = ioViewer.selected;
        staticBusIOViewer.inverted = ioViewer.inverted;
        staticBusIOViewer.netNumber = ioViewer.netNumber;
        staticBusIOViewer.locked = ioViewer.locked;
        //       staticIO.maskEnabled=ioViewer.maskEnabled;
        return staticBusIOViewer;
    }

    @Override
    public DeviceIOViewer findIO(int x, int y) {
//        System.out.println("findIO");
//        DeviceIOViewer selectedIo = null;
//        x = x - Math.round((left + right) / 2.f);
//        y = y - Math.round((top + bottom) / 2.f);
//        if (selectionZoom != 1) {
//            x = Math.round(x / selectionZoom);
//            y = Math.round(y / selectionZoom);
//        }
//        switch (angleIndex) {
//            case Rotate.R_0:
//                break;
//            case Rotate.R_90: {
//                int tmp = x;
//                x = y;
//                y = -tmp;
//            }
//            break;
//            case Rotate.R_180: {
//                x = -x;
//                y = -y;
//            }
//            break;
//            case Rotate.R_270: {
//                int tmp = x;
//                x = -y;
//                y = tmp;
//            }
//            break;
//            default:
//        }
//        DeviceIOViewer ioViewer = firstIoViewer;
//        float distance = Router.MAX_CONNECTION_DISTANCE;
////        System.out.println("x=" + x + " ioViewer.endX=" + ioViewer.endX);
//        float newDistance;
//        if (ioViewer.startY == ioViewer.endY) {
//            if (ioViewer.startX < x && x < ioViewer.endX ||
//                    ioViewer.startX > x && x > ioViewer.endX) {
//                newDistance = Math.abs(y - ioViewer.startY);
//            } else {
//                newDistance = Math.abs(x - ioViewer.endX) + Math.abs(y - ioViewer.endY);
//                newDistance = Math.min(newDistance, Math.abs(x - ioViewer.startX) + Math.abs(y -
//                        ioViewer.startY));
//            }
//        } else {
//            if (ioViewer.startY < y && y < ioViewer.endY ||
//                    ioViewer.startY > y && y > ioViewer.endY) {
//                newDistance = Math.abs(x - ioViewer.startX);
//            } else {
//                newDistance = Math.abs(x - ioViewer.endX) + Math.abs(y - ioViewer.endY);
//                newDistance = Math.min(newDistance, Math.abs(x - ioViewer.startX) + Math.abs(y -
//                        ioViewer.startY));
//            }
//        }
////            System.out.println("newDistance=" + newDistance);
//        if (newDistance < distance) {
//            selectedIo = ioViewer;
//        }
        return null;
//        return selectedIo;
    }

    public int getWidth() {
        return length;
    }

    public int getHeight() {
        return HEIGHT;
    }

}
