package com.logicbreadboard.simulator;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.util.HashMap;

/**
 * Created by laci on 7/9/15.
 */

public class LabelViewer extends DeviceViewer {
    public static final int MAX_LENGTH = 6;
    private static final int HEIGHT = 16;
    private static final int WIDTH = 48;
    static private final int CONTEXT_MENU_MASK = ContextMenuIcons.COPY |
            ContextMenuIcons.DELETE |
            ContextMenuIcons.EDIT |
            ContextMenuIcons.ROTATE |
            ContextMenuIcons.ALIGN_HORIZONTAL | ContextMenuIcons.ALIGN_VERTICAL |
            ContextMenuIcons.LOCK | ContextMenuIcons.UNLOCK |
            ContextMenuIcons.UNSELECT;
    String id;

    LabelViewer() {
        model = new LabelModel();
        id = new String("L0");
    }

//    LabelViewer(String id) {
//        model = new LabelModel();
//        id = new String(id);
//    }

    @Override
    public short getGroup() {
        return (short) SchematicViewerFactory.DeviceGroup.Connections.ordinal();
    }

    @Override
    public short getSymbol() {
        return (short) SchematicViewerFactory.Connections.Label.ordinal();
    }

    @Override
    public void setDefaultNumberOfIos() {
        setNumberOfIos((short) 1);
    }

    @Override
    public int getContextMenuMask() {
        return CONTEXT_MENU_MASK;
    }

    protected void rotateIo() {
        int startX = firstIoViewer.startX;
        int startY = firstIoViewer.startY;
        int endX = firstIoViewer.endX;
        int endY = firstIoViewer.endY;
        if (angleIndex == 0) {
            startX = -WIDTH / 2;
            startY = HEIGHT / 2;
            endX = startX - DeviceIOViewer.LENGTH;
            endY = startY + DeviceIOViewer.LENGTH;
        } else if (angleIndex == 2) {
            startX = -WIDTH / 2;
            startY = -HEIGHT / 2;
            endX = startX - DeviceIOViewer.LENGTH;
            endY = startY - DeviceIOViewer.LENGTH;
        } else if (angleIndex == 4) {
            startX = WIDTH / 2;
            startY = -HEIGHT / 2;
            endX = startX + DeviceIOViewer.LENGTH;
            endY = startY - DeviceIOViewer.LENGTH;
        } else if (angleIndex == 6) {
            startX = WIDTH / 2;
            startY = HEIGHT / 2;
            endX = startX + DeviceIOViewer.LENGTH;
            endY = startY + DeviceIOViewer.LENGTH;
        }
        firstIoViewer.startX = (short) startX;
        firstIoViewer.startY = (short) startY;
        firstIoViewer.endX = (short) endX;
        firstIoViewer.endY = (short) endY;
    }

    @Override
    public void rotate(int n) {
        if (n < 0) {
            n = n + Rotate.NUM_OF_ROT;
        }
        angleIndex = (angleIndex + n) % Rotate.NUM_OF_ROT;
        rotateIo();
    }

    protected void createOneIo() {
//        DeviceIOViewer iov = new DeviceIOViewer((short) (WIDTH / 2), (short) (0),
//                (short) (DeviceIOViewer.LENGTH + WIDTH / 2), (short) (0), false);
        DeviceIOViewer iov = new DeviceIOViewer((short) (-WIDTH / 2), (short) (HEIGHT / 2),
                (short) (-DeviceIOViewer.LENGTH - WIDTH / 2), (short) (DeviceIOViewer
                .LENGTH + HEIGHT / 2),
                false);
        iov.model = ((DeviceModel) model).firstInputModel;
        iov.model.level= State.Level.undefined;
        firstIoViewer = iov;
        ((LabelModel) model).iov = iov;
    }

    @Override
    public void setNumberOfIos(short numberOfIos) {
        this.numberOfIos = numberOfIos;
        model.init(numberOfIos, 0);
        createOneIo();
        rotateIo();
    }

    @Override
    public void draw(Canvas canvas) {
        drawRotated(canvas, currentPaint);
        rotateIo();
        DeviceIOViewer ioViewer = firstIoViewer;
        int xo = Math.round((float) (left + right) / 2f);
        int yo = Math.round((float) (bottom + top) / 2f);
        do {
            ioViewer.draw(canvas, xo, yo, currentPaint);
            ioViewer = ioViewer.next;
        } while (ioViewer != null);
    }

    @Override
    void load(DataInputStream inputStream) throws java.io.IOException {
        super.load(inputStream);
        id = inputStream.readUTF();
    }

    @Override
    void save(DataOutputStream outputStream) throws java.io.IOException {
        super.save(outputStream);
        outputStream.writeUTF(id);
    }

    @Override
    void reverseEnclosingRect() {
        left = enclosingLeft - STROKE_WITH;
        right = enclosingRight - STROKE_WITH;
        top = enclosingTop - STROKE_WITH;
        bottom = enclosingBottom - STROKE_WITH;
    }
    @Override
    float getSelectionZoom(int canvasHeight, int canvasWidth, float scaleFactor) {
        return Math.min(canvasWidth / (8 * (right - left)),
                canvasHeight / (8 * (bottom - top))) / (scaleFactor);
    }

    @Override
    public void setEnclosingRect() {
//             System.out.println("setEnclosingRect:" + angleIndex);
        enclosingLeft = left + STROKE_WITH;
        enclosingRight = right + STROKE_WITH;
        enclosingTop = top + STROKE_WITH;
        enclosingBottom = bottom + STROKE_WITH;
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

    @Override
    public void initPosition(int x, int y, int angleIndex) {
        this.left = x;
        this.bottom = y;
        this.right = x + WIDTH;
        this.top = y - HEIGHT;
        this.angleIndex = angleIndex;
        setEnclosingRect();
    }

    @Override
    void clone(int x, int y, Viewer old) {
        super.clone(x, y, old);
        String s = ((LabelViewer) old).id;
        int n = 0;
        try {
            n = Integer.parseInt(s);
        } catch (NumberFormatException e) {
            this.id = new String(((LabelViewer) old).id);
            return;
        }
        n++;
        this.id = new String(Integer.toString(n));
    }

    @Override
    public String getLabel() {
        return id;
    }

    @Override
    public void setLabel(String label) {
        id = new String(label);
    }

    public void connectLabels(HashMap<String, DeviceIOModel> labelNets) {
        DeviceIOModel m = labelNets.get(id);
        if (m != null) {
            m.connectTo(firstIoViewer.model);
        } else {
            labelNets.put(id, firstIoViewer.model);
        }
    }

    public String getEditMessage() {
        return "Enter a label";
    }

    public String getEditTitle() {
        return "Label editor";
    }

    public int getMaxIdLength() {
        return MAX_LENGTH;
    }


    @Override
    protected void drawRotated(Canvas canvas, Paint paint) {
        canvas.drawLine(left, bottom, left, top, paint);
        canvas.drawLine(right, bottom, right, top, paint);
        canvas.drawLine(left, bottom, right, bottom, paint);
        canvas.drawLine(left, top, right, top, paint);
        float size = paint.getTextSize();
        paint.setTextSize(bottom - top - 4 * STROKE_WITH);
        paint.setAntiAlias(true);
        paint.setTypeface(Typeface.create(Typeface.MONOSPACE, Typeface.NORMAL));
        float strokeWith = paint.getStrokeWidth();
        paint.setStrokeWidth(STROKE_WITH / 2);
        paint.setLinearText(true);
        paint.setSubpixelText(true);
        paint.setFlags(Paint.EMBEDDED_BITMAP_TEXT_FLAG | Paint.FILTER_BITMAP_FLAG | Paint
                .ANTI_ALIAS_FLAG | Paint.DITHER_FLAG | Paint.SUBPIXEL_TEXT_FLAG);
        canvas.drawText(id, left + STROKE_WITH, bottom - 4 * STROKE_WITH, paint);
        paint.setStrokeWidth(strokeWith);
        paint.setTextSize(size);
    }

    @Override
    public DeviceIOViewer getIoCanvasPos(DeviceIOViewer ioViewer) {
//        if (angleIndex == Rotate.R_0) {
        staticIO.startX = (short) ((right + left) / 2 + ioViewer.startX);
        staticIO.endX = (short) ((right + left) / 2 + ioViewer.endX);
        staticIO.startY = (short) ((bottom + top) / 2 + ioViewer.startY);
        staticIO.endY = (short) ((bottom + top) / 2 + ioViewer.endY);
        staticIO.inverted = ioViewer.inverted;
        staticIO.netNumber = ioViewer.netNumber;
        staticIO.locked = ioViewer.locked;
        //       staticIO.maskEnabled=ioViewer.maskEnabled;
        return staticIO;
    }

    @Override
    public DeviceIOViewer findIO(int x, int y) {
        DeviceIOViewer selectedIo = null;
        x = x - Math.round((left + right) / 2.f);
        y = y - Math.round((top + bottom) / 2.f);
        if (selectionZoom != 1) {
            x = Math.round(x / selectionZoom);
            y = Math.round(y / selectionZoom);
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

    public int getWidth() {
        return WIDTH;
    }

    public int getHeight() {
        return HEIGHT;
    }
}
