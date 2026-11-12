package com.logicbreadboard.simulator;

import android.app.Activity;

/**
 * Created on 6/4/15.
 */
public class ContextMenuIcons {
    enum ContextMenuAction {
        NOTHING, UNSELECT, ROTATE_LEFT, ROTATE_RIGHT, DELETE, ROUTE,
        CHANGE_INV, ADD_INPUT, DELAY, LOCK, UNLOCK, UNDO, COPY, ALIGN_LEFT, ALIGN_BOTTOM, REDO, HDL, RESTRICTED_ROUTE, SHOW_NET, EDIT, CHANGE_TO_RESISTOR, CHANGE_TO_LED,
        CHANGE_TO_RESISTOR_LED
    }

    static public final int UNSELECT = 0x1;
    static public final int ROTATE = 0x2;
    static public final int DELETE = 0x4;
    static public final int COPY = 0x8;
    static public final int ADD_INPUT = 0x10;
    static public final int CHANGE_INV = 0x20;
    static public final int ROUTE = 0x40;
    static public final int UNLOCK = 0x80;
    static public final int LOCK = 0x100;
    static public final int DELAY = 0x200;
    static public final int UNDO = 0x400;
    public static final int EDIT = 0x800;
    static public final int REDO = 0x1000;
    public static final int ALIGN_HORIZONTAL = 0x2000;
    public static final int ALIGN_VERTICAL = 0x4000;
    public static final int HDL = 0x8000;
    public static final int RESISTOR = 0x10000;
    public static final int LED = 0x20000;
    public static final int RLED = 0x40000;
    public static final int DISABLE = 0x80000;
    public static final int L_H = 0x100000;
    public static final int H_L = 0x200000;
    public static final int FINE_ROTATE = 0x400000;
    public static final int SIZE = 0x800000;
    public static final int REMOVE_SW = 0x1000000;
    public static final int ADD_SW = 0x2000000;
    public static final int MORPH = 0x4000000;

//    static public int menuMask;
//    public static float top;
//    public static float left;
//    public static float right;
//    static public boolean help = false;
//    static protected RectF lockIcon = new RectF(0, 0, 10, 10);
//    static protected RectF unlockIcon = new RectF(0, 0, 10, 10);
//    static protected RectF delayIcon = new RectF(0, 0, 10, 10);
//    static protected RectF changeInvIcon = new RectF(0, 0, 10, 10);
//    static protected RectF unselectIcon = new RectF(0, 0, 10, 10);
//    static protected RectF deleteIcon = new RectF(0, 0, 10, 10);
//    static protected RectF unDoIcon = new RectF(0, 0, 10, 10);
//    static protected RectF reDoIcon = new RectF(0, 0, 10, 10);
//    static protected RectF rotateLeftIcon = new RectF(0, 0, 10, 10);
//    static protected RectF rotateRightIcon = new RectF(0, 0, 10, 10);
//    static protected RectF routeIcon = new RectF(0, 0, 10, 10);
//    static protected RectF restrictedRouteIcon = new RectF(0, 0, 10, 10);
//    static protected RectF copyIcon = new RectF(0, 0, 10, 10);
//    static protected RectF addInputIcon = new RectF(0, 0, 10, 10);
//    static protected RectF editIcon = new RectF(0, 0, 10, 10);
//    static protected RectF alignHorizontalIcon = new RectF(0, 0, 10, 10);
//    static protected RectF alignVerticalIcon = new RectF(0, 0, 10, 10);
//    static protected RectF hdlIcon = new RectF(0, 0, 10, 10);
//    static protected RectF showNetIcon = new RectF(0, 0, 10, 10);
//    static protected RectF resistorIcon = new RectF(0, 0, 10, 10);
//    static protected RectF ledIcon = new RectF(0, 0, 10, 10);
//    static protected RectF resistorLedIcon = new RectF(0, 0, 10, 10);
//    static float iconWidth = 64;
//    static float iconHeight = 64;
//    private static Bitmap lock;
//    private static Bitmap unlock;
//    private static Bitmap unselect;
//    private static Bitmap rotateLeft;
//    private static Bitmap rotateRight;
//    private static Bitmap delete;
//    private static Bitmap unDo;
//    private static Bitmap reDo;
//    private static Bitmap route;
//    private static Bitmap restrictedRoute;
//    private static Bitmap copy;
//    private static Bitmap delay;
//    private static Bitmap changeInv;
//    private static Bitmap addInput;
//    private static Bitmap edit;
//    private static Bitmap alignHorizontal;
//    private static Bitmap alignVertical;
//    private static Bitmap hdl;
//    private static Bitmap showNet;
//    private static Bitmap resistor;
//    private static Bitmap led;
//    private static Bitmap resistorLed;
//    static private Paint paint;
//    static private int NUMBER_OF_ICONS = 19;

    //   static float menuZoom;
//    static {
//        paint = new Paint();
//        paint.setColor(Color.BLACK);
//        paint.setAntiAlias(true);
//        paint.setStyle(Paint.Style.STROKE);
////        paint.setStrokeWidth(STROKE_WITH);
////        normalPaint.setDither(false);                    // set the dither to true
//        paint.setStrokeJoin(Paint.Join.ROUND);    // set the join to round you want
//        paint.setStrokeCap(Paint.Cap.ROUND);      // set the paint cap to round too
//    }

    ContextMenuIcons(Activity context) {
//        unlock = BitmapFactory.decodeResource(context.getResources(), R.drawable
//                .ic_lock_outline_black_24dp);
//        lock = BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_lock_open_black_24dp);
//        delay = BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_timer_black_24dp);
//        unselect = BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_done_black_24dp);
//        rotateLeft = BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_rotate_left_black_24dp);
//        rotateRight = BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_rotate_right_black_24dp);
//        delete = BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_delete_black_24dp);
//        unDo = BitmapFactory.decodeResource(context.getResources(), R.drawable
//                .ic_undo_black_24dp);
//        reDo = BitmapFactory.decodeResource(context.getResources(), R.drawable
//                .ic_redo_black_24dp);
//        route = BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_route_black_24dp);
//        restrictedRoute = BitmapFactory.decodeResource(context.getResources(), R.drawable
//                .ic_restricted_route_black_24dp);
//        copy = BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_content_copy_black_24dp);
//        changeInv = BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_action_change_inv);
//        addInput = BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_plus_one_black_24dp);
//        edit = BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_text_format_black_24dp);
//        alignHorizontal = BitmapFactory.decodeResource(context.getResources(), R.drawable
//                .ic_align_horizontal_24dp);
//        alignVertical = BitmapFactory.decodeResource(context.getResources(), R.drawable
//                .ic_align_vertical_24dp);
//        hdl = BitmapFactory.decodeResource(context.getResources(), R.drawable
//                .ic_hdl_24dp);
//        showNet = BitmapFactory.decodeResource(context.getResources(), R.drawable
//                .ic_visibility_black_24dp);
//        resistor = BitmapFactory.decodeResource(context.getResources(), R.drawable
//                .ic_visibility_black_24dp);
//        led = BitmapFactory.decodeResource(context.getResources(), R.drawable
//                .ic_visibility_black_24dp);
//        resistorLed = BitmapFactory.decodeResource(context.getResources(), R.drawable
//                .ic_visibility_black_24dp);
//    }
//
//    static void draw(Canvas canvas, float x, float y, RectF icon, Bitmap bitmap) {
//        icon.left = x;
//        icon.right = x + iconWidth;
//        icon.top = y;
//        icon.bottom = y + iconHeight;
//        canvas.drawCircle(x + iconWidth / 2, y + iconHeight / 2, iconWidth / 2, Viewer.Context
//                .iconBackgroundPaint);
//        canvas.drawBitmap(bitmap, null, icon, paint);
//    }
//
//    private static void hideIcon(RectF rect) {
//        rect.left = 0;
//        rect.right = 0;
//        rect.bottom = 0;
//        rect.top = 0;
//    }
//
//    static void drawContextMenu(Canvas canvas, int mask, int canvasHeight, int canvasWidth, float
//            scaleFactor) {
//        iconHeight = canvasHeight / ((NUMBER_OF_ICONS + 2) * scaleFactor);
//        float maxH = 64 / scaleFactor;
//        if (iconHeight > maxH) {
//            iconHeight = maxH;
//        }
//        iconWidth = iconHeight;
//        //       right = ContextMenuIcons.left + canvasWidth;
//        right = left - iconWidth + (canvasWidth) / scaleFactor;
//        float textSize = Viewer.currentTextPaint.getTextSize();
////        Log.i("ctx ", textSize + " " + scaleFactor);
//        Viewer.currentTextPaint.setTextSize(2 * textSize / scaleFactor);
//        int pos = 0;
//        if ((mask & UNSELECT) != 0) {
//            if (help) {
//                canvas.drawText("Unselect", left + iconWidth, top + iconHeight, Viewer.currentTextPaint);
//            }
//            draw(canvas, left, top, unselectIcon, unselect);
//        } else {
//            hideIcon(unselectIcon);
//        }
//        pos = pos + 2;
//        if ((mask & REDO) != 0) {
//            if (help) {
//                canvas.drawText("Redo", left + iconWidth, top + (pos + 1) * iconHeight,
//                        Viewer.currentTextPaint);
//            }
//            draw(canvas, left, top + pos * iconHeight, reDoIcon, reDo);
//        } else {
//            hideIcon(reDoIcon);
//        }
//        pos = pos + 2;
//        if ((mask & ROTATE) != 0) {
//            draw(canvas, left, top + pos * iconHeight, rotateLeftIcon, rotateLeft);
//            if (help) {
//                canvas.drawText("Rotate left", left + iconWidth, top + (pos + 1) * iconHeight,
//                        Viewer.currentTextPaint);
//            }
//            pos = pos + 2;
//            draw(canvas, left, top + pos * iconHeight, rotateRightIcon, rotateRight);
//            if (help) {
//                canvas.drawText("Rotate right", left + iconWidth, top + (pos + 1) * iconHeight,
//                        Viewer.currentTextPaint);
//            }
//            pos = pos + 2;
//        } else {
//            pos = pos + 2;
//            pos = pos + 2;
//            hideIcon(rotateLeftIcon);
//            hideIcon(rotateRightIcon);
//        }
//        if ((mask & ROUTE) != 0) {
//            draw(canvas, left, top + pos * iconHeight, routeIcon, route);
//            if (help) {
//                canvas.drawText("Route", left + iconWidth, top + (pos + 1) * iconHeight,
//                        Viewer.currentTextPaint);
//            }
//        } else {
//            hideIcon(routeIcon);
//        }
//        pos = pos + 2;
//        if ((mask & COPY) != 0) {
//            draw(canvas, left, top + pos * iconHeight, copyIcon, copy);
//            if (help) {
//                canvas.drawText("Clone", left + iconWidth, top + (pos + 1) * iconHeight,
//                        Viewer.currentTextPaint);
//            }
//        } else {
//            hideIcon(copyIcon);
//        }
//        pos = pos + 2;
//        if ((mask & DELETE) != 0) {
//            draw(canvas, left, top + pos * iconHeight, deleteIcon, delete);
//            if (help) {
//                canvas.drawText("Delete", left + iconWidth, top + (pos + 1) * iconHeight,
//                        Viewer.currentTextPaint);
//            }
//        } else {
//            hideIcon(deleteIcon);
//        }
//        pos = pos + 2;
//        if ((mask & CHANGE_INV) != 0) {
//            draw(canvas, left, top + pos * iconHeight, changeInvIcon, changeInv);
//            if (help) {
//                canvas.drawText("Invert IO", left + iconWidth, top + (pos + 1) * iconHeight,
//                        Viewer.currentTextPaint);
//            }
//        } else {
//            hideIcon(changeInvIcon);
//        }
//        pos = pos + 2;
//        if ((mask & ROUTE) != 0) {
//            draw(canvas, left, top + pos * iconHeight, restrictedRouteIcon, restrictedRoute);
//            if (help) {
//                canvas.drawText("Route", left + iconWidth, top + (pos + 1) * iconHeight,
//                        Viewer.currentTextPaint);
//            }
//        } else {
//            hideIcon(restrictedRouteIcon);
//        }
////        pos = pos + 2;
//
//        pos = 0;
//        if ((mask & UNDO) != 0) {
//            draw(canvas, right, top + pos * iconHeight, unDoIcon, unDo);
//            if (help) {
//                rightHelp(canvas, scaleFactor, pos, "Undo");
//            }
//        } else {
//            hideIcon(unDoIcon);
//        }
//        pos = pos + 2;
//        if ((mask & DELAY) != 0) {
//            draw(canvas, right, top + pos * iconHeight, delayIcon, delay);
//            if (help) {
//                rightHelp(canvas, scaleFactor, pos, "Set delay");
//            }
//        } else {
//            hideIcon(delayIcon);
//        }
//        pos = pos + 2;
//        if ((mask & LOCK) != 0) {
//            draw(canvas, right, top + pos * iconHeight, lockIcon, lock);
//            if (help) {
//                rightHelp(canvas, scaleFactor, pos, "Lock");
//            }
//        } else {
//            hideIcon(lockIcon);
//        }
//        if ((mask & RESISTOR) != 0) {
//            draw(canvas, right, top + pos * iconHeight, resistorIcon, resistor);
//            if (help) {
////                rightHelp(canvas, scaleFactor, pos, "Lock");
//            }
//        } else {
//            hideIcon(resistorIcon);
//        }
//        pos = pos + 2;
//        if ((mask & UNLOCK) != 0) {
//            draw(canvas, right, top + pos * iconHeight, unlockIcon, unlock);
//            if (help) {
//                rightHelp(canvas, scaleFactor, pos, "Unlock");
//            }
//        } else {
//            hideIcon(unlockIcon);
//        }
//        if ((mask & LED) != 0) {
//            draw(canvas, right, top + pos * iconHeight, ledIcon, led);
//            if (help) {
////                rightHelp(canvas, scaleFactor, pos, "Lock");
//            }
//        } else {
//            hideIcon(ledIcon);
//        }
//        pos = pos + 2;
//        if ((mask & EDIT) != 0) {
//            draw(canvas, right, top + pos * iconHeight, editIcon, edit);
//            if (help) {
//                rightHelp(canvas, scaleFactor, pos, "Edit text");
//            }
//        } else {
//            hideIcon(editIcon);
//        }
//        pos = pos + 2;
//        if ((mask & ALIGN_HORIZONTAL) != 0) {
//            draw(canvas, right, top + pos * iconHeight, alignHorizontalIcon, alignHorizontal);
//            if (help) {
//                rightHelp(canvas, scaleFactor, pos, "Align horizontally");
//            }
//        } else {
//            hideIcon(alignHorizontalIcon);
//        }
//        if ((mask & RLED) != 0) {
//            draw(canvas, right, top + pos * iconHeight, resistorLedIcon, resistorLed);
//            if (help) {
////                rightHelp(canvas, scaleFactor, pos, "Lock");
//            }
//        } else {
//            hideIcon(resistorLedIcon);
//        }
//        pos = pos + 2;
//        if ((mask & ALIGN_VERTICAL) != 0) {
//            draw(canvas, right, top + pos * iconHeight, alignVerticalIcon, alignVertical);
//            if (help) {
//                rightHelp(canvas, scaleFactor, pos, "Align vertically");
//            }
//        } else {
//            hideIcon(alignVerticalIcon);
//        }
//        pos = pos + 2;
//        if ((mask & ADD_INPUT) != 0) {
//            draw(canvas, right, top + pos * iconHeight, addInputIcon, addInput);
//            if (help) {
//                rightHelp(canvas, scaleFactor, pos, "Add input");
//            }
//        } else {
//            hideIcon(addInputIcon);
//        }
//        pos = pos + 2;
//        if ((mask & ROUTE) != 0) {
//            draw(canvas, right, top + pos * iconHeight, showNetIcon, showNet);
//            if (help) {
//                rightHelp(canvas, scaleFactor, pos, "Extend selection");
//            }
//        } else {
//            hideIcon(showNetIcon);
//        }
//        if ((mask & HDL) != 0) {
//            draw(canvas, right, top + pos * iconHeight, hdlIcon, hdl);
//            if (help) {
//                rightHelp(canvas, scaleFactor, pos, "Load circuit");
//            }
//        } else {
//            hideIcon(hdlIcon);
//        }
//        Viewer.currentTextPaint.setTextSize(textSize);
//    }
//
//    private static void rightHelp(Canvas canvas, float scaleFactor, int pos, String helpText) {
//        final float textLength = Viewer.currentTextPaint.measureText(helpText);
//        float xpos = right - textLength;
////        Log.i("rightHelp", helpText + " " + textLength + " " + xpos);
//        canvas.drawText(helpText, xpos, top + (pos + 1) * iconHeight, Viewer.currentTextPaint);
//    }
//
//    static public ContextMenuIcons.ContextMenuAction handleSchematicSelectedTouch(float x, float y) {
//        ContextMenuIcons.ContextMenuAction res = ContextMenuIcons.ContextMenuAction.NOTHING;
//        if (rotateLeftIcon.contains(x, y)) {
//            res = ContextMenuIcons.ContextMenuAction.ROTATE_LEFT;
//        } else if (rotateRightIcon.contains(x, y)) {
//            res = ContextMenuIcons.ContextMenuAction.ROTATE_RIGHT;
//        } else if (unselectIcon.contains(x, y)) {
//            res = ContextMenuIcons.ContextMenuAction.UNSELECT;
//        } else if (deleteIcon.contains(x, y)) {
//            res = ContextMenuIcons.ContextMenuAction.DELETE;
//        } else if (copyIcon.contains(x, y)) {
//            res = ContextMenuIcons.ContextMenuAction.COPY;
//        } else if (routeIcon.contains(x, y)) {
//            res = ContextMenuIcons.ContextMenuAction.ROUTE;
//        } else if (restrictedRouteIcon.contains(x, y)) {
//            res = ContextMenuIcons.ContextMenuAction.RESTRICTED_ROUTE;
//        } else if (changeInvIcon.contains(x, y)) {
//            res = ContextMenuIcons.ContextMenuAction.CHANGE_INV;
//        } else if (addInputIcon.contains(x, y)) {
//            res = ContextMenuIcons.ContextMenuAction.ADD_INPUT;
//        } else if (delayIcon.contains(x, y)) {
//            res = ContextMenuIcons.ContextMenuAction.DELAY;
//        } else if (lockIcon.contains(x, y)) {
//            res = ContextMenuIcons.ContextMenuAction.LOCK;
//        } else if (unlockIcon.contains(x, y)) {
//            res = ContextMenuIcons.ContextMenuAction.UNLOCK;
//        } else if (unDoIcon.contains(x, y)) {
//            res = ContextMenuIcons.ContextMenuAction.UNDO;
//        } else if (reDoIcon.contains(x, y)) {
//            res = ContextMenuIcons.ContextMenuAction.REDO;
//        } else if (editIcon.contains(x, y)) {
//            res = ContextMenuIcons.ContextMenuAction.EDIT;
//        } else if (alignHorizontalIcon.contains(x, y)) {
//            res = ContextMenuIcons.ContextMenuAction.ALIGN_LEFT;
//        } else if (alignVerticalIcon.contains(x, y)) {
//            res = ContextMenuIcons.ContextMenuAction.ALIGN_BOTTOM;
//        } else if (hdlIcon.contains(x, y)) {
//            res = ContextMenuIcons.ContextMenuAction.HDL;
//        } else if (showNetIcon.contains(x, y)) {
//            res = ContextMenuIcons.ContextMenuAction.SHOW_NET;
//        }
//        if (res != ContextMenuIcons.ContextMenuAction.NOTHING) {
//            help = false;
//        }
//        return res;
//    }
//
//    static public ContextMenuIcons.ContextMenuAction handleBreadboardSelectedTouch(float x, float y) {
//        ContextMenuIcons.ContextMenuAction res = ContextMenuIcons.ContextMenuAction.NOTHING;
//        if (rotateLeftIcon.contains(x, y)) {
//            res = ContextMenuIcons.ContextMenuAction.ROTATE_LEFT;
//        } else if (rotateRightIcon.contains(x, y)) {
//            res = ContextMenuIcons.ContextMenuAction.ROTATE_RIGHT;
//        } else if (unselectIcon.contains(x, y)) {
//            res = ContextMenuIcons.ContextMenuAction.UNSELECT;
//        } else if (deleteIcon.contains(x, y)) {
//            res = ContextMenuIcons.ContextMenuAction.DELETE;
//        } else if (copyIcon.contains(x, y)) {
//            res = ContextMenuIcons.ContextMenuAction.COPY;
//        } else if (routeIcon.contains(x, y)) {
//            res = ContextMenuIcons.ContextMenuAction.ROUTE;
//        } else if (restrictedRouteIcon.contains(x, y)) {
//            res = ContextMenuIcons.ContextMenuAction.RESTRICTED_ROUTE;
//        } else if (changeInvIcon.contains(x, y)) {
//            res = ContextMenuIcons.ContextMenuAction.CHANGE_INV;
//        } else if (addInputIcon.contains(x, y)) {
//            res = ContextMenuIcons.ContextMenuAction.ADD_INPUT;
//        } else if (delayIcon.contains(x, y)) {
//            res = ContextMenuIcons.ContextMenuAction.DELAY;
//        } else if (resistorIcon.contains(x, y)) {
//            res = ContextMenuAction.CHANGE_TO_RESISTOR;
//        } else if (ledIcon.contains(x, y)) {
//            res = ContextMenuAction.CHANGE_TO_LED;
//        } else if (unDoIcon.contains(x, y)) {
//            res = ContextMenuIcons.ContextMenuAction.UNDO;
//        } else if (reDoIcon.contains(x, y)) {
//            res = ContextMenuIcons.ContextMenuAction.REDO;
//        } else if (editIcon.contains(x, y)) {
//            res = ContextMenuIcons.ContextMenuAction.EDIT;
//        } else if (resistorLedIcon.contains(x, y)) {
//            res = ContextMenuAction.CHANGE_TO_RESISTOR_LED;
////        } else if (alignHorizontalIcon.contains(x, y)) {
////            res = ContextMenuIcons.ContextMenuAction.ALIGN_LEFT;
//        } else if (alignVerticalIcon.contains(x, y)) {
//            res = ContextMenuIcons.ContextMenuAction.ALIGN_BOTTOM;
//        } else if (hdlIcon.contains(x, y)) {
//            res = ContextMenuIcons.ContextMenuAction.HDL;
//        } else if (showNetIcon.contains(x, y)) {
//            res = ContextMenuIcons.ContextMenuAction.SHOW_NET;
//        }
//        if (res != ContextMenuIcons.ContextMenuAction.NOTHING) {
//            help = false;
//        }
//        return res;
    }

}
