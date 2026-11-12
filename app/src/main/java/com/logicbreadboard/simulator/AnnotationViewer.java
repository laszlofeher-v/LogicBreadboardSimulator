package com.logicbreadboard.simulator;

import android.graphics.Canvas;
import android.graphics.Rect;

import java.io.DataInputStream;
import java.io.DataOutputStream;

/**
 * Created by laci on 10/15/15.
 */
public class AnnotationViewer extends Viewer {
    public static final int MAX_LENGTH = 64;
    static private final int CONTEXT_MENU_MASK = ContextMenuIcons.COPY |
            ContextMenuIcons.DELETE |
            ContextMenuIcons.EDIT |
//            ContextMenuIcons.ALIGN_HORIZONTAL | ContextMenuIcons.ALIGN_VERTICAL | must not align non device viewer
            ContextMenuIcons.UNSELECT;
    static final private String selectable = new String("W");
    static private Rect bounds = new Rect();
    String id;
    int x;
    int y;

    // TODO: 10/15/15 hide

    AnnotationViewer() {
        id = new String("#");
    }

    @Override
    public String getLabel() {
        return id;
    }

    @Override
    public void setLabel(String label) {
        id = new String(label);
    }

    @Override
    public short getGroup() {
        return (short) SchematicViewerFactory.DeviceGroup.Annotation.ordinal();
    }

    @Override
    public short getSymbol() {
        return (short) SchematicViewerFactory.Annotations.Annotation.ordinal();
    }

    @Override
    public int getContextMenuMask() {
        return CONTEXT_MENU_MASK;
    }

    @Override
    void load(DataInputStream inputStream) throws java.io.IOException {
        x = inputStream.readShort();
        y = inputStream.readShort();
        id = inputStream.readUTF();
//        if (id.equals("\n")){
//            id = "++++++++++++++++++++++++++";
//        } else if ( id.equals("Y=A x B")) {
//            id = "Y=A \u2295 B";
//        }
//        id=id.replace('\n', '\u25ba');
        id = id.replace(">", "\u2295 \u25ba");
//        System.out.println(id);
//        id = new String("\u2295 \u25ba \u2295 \u25ba");
    }

    @Override
    void save(DataOutputStream outputStream) throws java.io.IOException {
        outputStream.writeShort(getGroup());
        outputStream.writeShort(getSymbol());
        outputStream.writeShort(x);
        outputStream.writeShort(y);
        outputStream.writeUTF(id);
    }

    @Override
    public void initPosition(int x, int y, int angleIndex) {
        this.x = x;
        this.y = y;
        setEnclosingRect();
    }

    @Override
    public void select(SelectMsg msg) {
        currentTextPaint.getTextBounds(selectable, 0, selectable.length(), bounds);
        int height = bounds.height();
        int width = bounds.width();
        if (msg.pointerX > x &&
                msg.pointerX < x + width &&
                msg.pointerY < y &&
                msg.pointerY > y - height) {
            msg.viewer = this;
        }
    }

    @Override
    public void search(SearchMsg msg) {
        currentTextPaint.getTextBounds(selectable, 0, selectable.length(), bounds);
        int height = bounds.height();
        int width = bounds.width();
        if (msg.pointerX > x &&
                msg.pointerX < x + width &&
                msg.pointerY < y &&
                msg.pointerY > y - height) {
            msg.addFound(this);
        }
    }

    @Override
    void clone(int x, int y, Viewer old) {
        this.id = new String(((AnnotationViewer) old).id);
        initPosition(x, y, 0);
    }

    @Override
    public void cursorStep(Cursor cursor) {
        currentTextPaint.getTextBounds(selectable, 0, selectable.length(), bounds);
        int height = bounds.height();
        cursor.set(cursor.getX(), cursor.getY() - height * 2);
    }

    @Override
    public boolean moveCheck(int dx, int dy) {
        // not routable, can be anywhere
        return true;
    }

    public String getEditMessage() {
        return "Enter a comment";
    }

    public String getEditTitle() {
        return "Annotation editor";
    }

    public int getMaxIdLength() {
        return MAX_LENGTH;
    }

    @Override
    public void draw(Canvas canvas) {
        canvas.drawText(id, x, y, Context.textPaint);
        canvas.drawText(id.substring(0, 1), x, y, currentTextPaint);
    }

    public int getWidth() {
        return 10;
    }

    public int getHeight() {
        return 10;
    }

    public int getLeft() {
        return x;
    }

    public int getTop() {
        return y;
    }

    public int getBottom() {
        return y + getHeight();
    }

    public int getRight() {
        return x + getWidth();
    }

    @Override
    int getBaseX() {
        return getLeft();
    }

    @Override
    int getBaseY() {
        return getBottom();
    }

    @Override
    void move(EditorViewer editorViewer, int dx, int dy, boolean checkInside) {
        x += dx;
        y += dy;
        boolean ok = true;
        if (x < 0) {
            ok = false;
        }
        if (x > EditorModel.MAX_X) {
            ok = false;
        }
        if (y < 0) {
            ok = false;
        }
        if (y > EditorModel.MAX_Y) {
            ok = false;
        }
        if (!ok) {
            x -= dx;
            y -= dy;
        }
    }

}