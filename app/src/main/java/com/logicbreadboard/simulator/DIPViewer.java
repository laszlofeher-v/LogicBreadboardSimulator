package com.logicbreadboard.simulator;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.Log;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.util.HashMap;

/**
 * Created by laci on 9/29/15.
 */
public class DIPViewer extends DeviceViewer {
    final static int MAX_LENGTH_OF_FILENAME = 256;
    protected boolean floating = false;
    public static int rasterSize = 5;
    protected static int unit = 2 * rasterSize;
    static private final int CONTEXT_MENU_MASK =
            ContextMenuIcons.COPY |
                    ContextMenuIcons.HDL |
                    ContextMenuIcons.DELETE |
                    ContextMenuIcons.ROTATE |
                    ContextMenuIcons.ALIGN_HORIZONTAL | ContextMenuIcons.ALIGN_VERTICAL |
                    ContextMenuIcons.LOCK | ContextMenuIcons.UNLOCK |
                    ContextMenuIcons.UNSELECT;

    DIPViewer() {
        model = new DIPModel();
    }

    public static void setRasterSize(int newSize) {
        rasterSize = newSize;
        unit = 2 * rasterSize;
    }

    public boolean isFloating() {
        return floating;
    }

    public void setFloating() {
        floating = true;
    }

    public void clearFloating() {
        floating = false;
    }

    @Override
    public String getLabel() {
        return ((DIPModel) model).fileName;
    }

    @Override
    public void setLabel(String label) {
        model = new DIPModel();
        ((DIPModel) model).fileName = label.replace(Settings.defaultDirectory, "");
        ((DIPModel) model).loadContent();
    }

    @Override
    void clone(int x, int y, Viewer old) {
        super.clone(x, y, old);
        ((DIPModel) this.model).fileName = new String((
                (DIPModel) (((DIPViewer) old)
                        .model)).fileName);
        ((DIPModel) model).loadContent();
    }

    @Override
    public void compressNetNumbers(HashMap<Integer, Integer> nets, int[] newNetNumber) {
        DeviceIOViewer ioViewer = firstIoViewer;
        while (ioViewer != null) {
            ioViewer.compressNetNumbers(nets, newNetNumber);
            ioViewer = ioViewer.next;
        }
        ((DIPModel) model).editorModel.compressViewersNetNumbers(nets, newNetNumber);
//        ListIterator<Viewer> listIterator = ((DIPModel) model).editorModel.viewerLinkedList.listIterator();
//        while (listIterator.hasNext()) {
//            Viewer v = listIterator.next();
//            v.compressNetNumbers(nets, newNetNumber);
//        }
    }

    @Override
    float getSelectionZoom(int canvasHeight, int canvasWidth, float scaleFactor) {
        float n = 4;
        if (numberOfIos >= 18) {
            n = 2;
        }
        return Math.min(canvasWidth / (n * (right - left)),
                canvasHeight / (n * (bottom - top))) / (scaleFactor);
    }

    @Override
    void load(DataInputStream inputStream) throws java.io.IOException {
        super.load(inputStream);
        ((DIPModel) model).fileName = inputStream.readUTF();
        ((DIPModel) model).loadContent();
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
    void save(DataOutputStream outputStream) throws java.io.IOException {
        super.save(outputStream);
        outputStream.writeUTF(((DIPModel) model).fileName);
    }

    @Override
    public short getGroup() {
        return (short) ViewerFactory.factory.getGroupBox();
    }

    public String getEditMessage() {
        return "Enter an existing project file uniqueName";
    }

    public String getEditTitle() {
        return "Subcircuit uniqueName editor";
    }

    @Override
    public int getContextMenuMask() {
        return CONTEXT_MENU_MASK;
    }

//    @Override
//    public short getGroup() {
//        return (short) ViewerFactory.DeviceGroup.Box.ordinal();
//    }

    public int getMaxIdLength() {
        return MAX_LENGTH_OF_FILENAME;
    }

    @Override
    public void setNumberOfIos(short numberOfIos) {
        this.numberOfIos = numberOfIos;
        model.init(numberOfIos, 0);
        createAllInputs();
    }

    protected void createOneInput(DeviceIOModel ioModel, int iPos, short yPos, int index) {
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
        iov.model = ioModel;
        iov.next = firstIoViewer;
        firstIoViewer = iov;
    }

    public void connectLabels(HashMap<String, DeviceIOModel> labelNets) {
        if (((DIPModel) model).hdl) {
            ((DIPModel) model).editorModel.makeLogicalConnections();  // make connections in subcircuit
        } else {
            ((DIPModel) model).editorModel.destroyLogicalConnections();  // make connections in subcircuit
            ((DIPModel) model).editorModel.makeLogicalConnections();  // make connections in subcircuit
        }
        ((DIPModel) model).editorModel.connectDip((DIPIoViewer) firstIoViewer);
    }

    protected void createAllInputs() {
        DeviceIOModel ioModel = ((DeviceModel) model).firstInputModel;
        for (short i = 0; i < numberOfIos / 2; i++) {
            createOneInput(ioModel, i, (short) (getHeight() / 2), i + 1);
            ioModel = ioModel.next;
        }
        for (short i = (short) (numberOfIos / 2); i < numberOfIos; i++) {
            createOneInput(ioModel, i - numberOfIos / 2, (short) (-getHeight() / 2), i + 1);
            ioModel = ioModel.next;
        }
        ((DIPModel) model).firstIoViewer = firstIoViewer;
    }

    @Override
    public void initPosition(int x, int y, int angleIndex) {
        if (MainActivity.editorMode == MainActivity.EditorMode.SCH) {
            Log.i("INITPOS1", x + " " + y + " " + angleIndex);
            this.left = x;
            this.bottom = y;
            this.right = x + getWidth();
            this.top = y - getHeight();
            this.angleIndex = angleIndex;
            setEnclosingRect();
        } else {
            initPosition(x, y, angleIndex, BreadBoardViewer.rasterSize);
        }
        //       this.numberOfIos = numberOfIos;
        //       this.selectedIos = new boolean[numberOfIos];
    }

    public void initPosition(int x, int y, int angleIndex, int raster) {
        int raster2 = raster * 2;
        Log.i("INITPOS2", x + " " + y + " " + angleIndex + " " + raster);
        int d = y % raster2;
        y = y - d + raster;
        Log.i("INITPOS2", d + " " + y);
        d = x % raster2;
        x = x - d;
        Log.i("INITPOS2", d + " " + x);
        this.left = x;
        this.bottom = y;
        this.right = x + getWidth();
        this.top = y - getHeight();
        this.angleIndex = angleIndex;
        setEnclosingRectWithoutCheck();
        //       this.numberOfIos = numberOfIos;
        //       this.selectedIos = new boolean[numberOfIos];
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
            Log.i("HOLUSED", " floating = true");
            floating = true;
            while (iov2 != iov) {
                iov2.setHoleFree(editorViewer, xo, yo, angleIndex);
                iov2 = iov2.next;
            }
        } else {
            Log.i("HOLUSED", " floating = false");
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
        Log.i("HOLFREE", "        left=" + left + " bottom=" + bottom);
        int xo = Math.round((float) (left + right) / 2f);
        int yo = Math.round((float) (bottom + top) / 2f);
        DeviceIOViewer iov = firstIoViewer;
        boolean result = false;
        while (iov != null) {
            iov.setHoleFree(editorViewer, xo, yo, angleIndex);
            iov = iov.next;
        }
    }


    @Override
    public short getSymbol() {
        return (short) SchematicViewerFactory.Box.DIP_8.ordinal();
    }


    public void drawEnlarged(Canvas canvas, float zoom) {
        selectionZoom = zoom;
        canvas.save();
        canvas.scale(zoom, zoom, left + (right - left) / 2, top + (bottom - top) / 2);
        draw(canvas);
        canvas.restore();
    }

    @Override
    protected void drawRotated(Canvas canvas, Paint paint) {
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
        int d = unit / 2;
        parameterRectF.left = left - d;
        parameterRectF.bottom = (bottom + top) / 2 + d;
        parameterRectF.right = left + d;
        parameterRectF.top = (bottom + top) / 2 - d;
        canvas.drawArc(parameterRectF, 270, 180, false, paint);
        int index = ((DIPModel) model).fileName.lastIndexOf("/");
        index = ((DIPModel) model).fileName.lastIndexOf("_");
        if (index == -1) {
            index = ((DIPModel) model).fileName.lastIndexOf("/");
        }
        writeText(canvas, index);
        writeInternalLabels(canvas);
        if (floating) {
            paint.setStrokeWidth(w);
        }
    }

    protected void writeInternalLabels(Canvas canvas) {
    }

    protected void writeText(Canvas canvas, int index) {
        float size = Context.textPaint.getTextSize();
        Context.textPaint.setTextSize(getHeight() / 3);
        String s = ((DIPModel) model).fileName.substring(index + 1);
        canvas.drawText(s, left + unit + STROKE_WITH, bottom - (getHeight() / 3), Context.textPaint);
        Context.textPaint.setTextSize(size);
//
//        String s = ((DIPModel) model).fileName.substring(index + 1);
//        canvas.drawText(s, left + unit + STROKE_WITH, bottom - (getHeight() / 2), Context.textPaint);
    }
}
