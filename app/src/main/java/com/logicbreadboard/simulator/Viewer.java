package com.logicbreadboard.simulator;

import android.app.Activity;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Typeface;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.util.HashMap;
import java.util.LinkedList;

/**
 * Created on 12/18/14.
 */
public abstract class Viewer {
    public final static RectF parameterRectF = new RectF();
    static final public int DEVICE_IO_FOUND = 1;
    static final public int WIRE_ENDPOINT_FOUND = 2;
    protected static final int STROKE_WITH = 1;
    public static final boolean DO_NOT_CHECK_INSIDE = false;
    //TODO sizes or rather the default zoom must depend on screen size and resolution
    static Paint currentPaint = Context.normalPaint;
    static Paint currentTextPaint = Context.textPaint;
    static boolean netNumberShowEnabled = false;
    static int tipRound;
    static final boolean CHECK_INSIDE = true;

    Viewer() {
    }

//    public void drawToolTip(Canvas canvas) {
//    }

    public short getGroup() {
        return -1;
    }

    public short getSymbol() {
        return -1;
    }

    public boolean isFloating() {
        return false;
    }

    public void setFloating() {
    }

    public void clearFloating() {
    }

    void rotate(int angleIndex) {
    }

    public void rotateWithoutCheck(int n) {
    }

    void clone(int x, int y, Viewer v) {
    }

    ;

    public void reset() {

    }

    ;

    int getBaseX() {
        return 0;
    }

    int getBaseY() {
        return 0;
    }

    void load(DataInputStream in) throws java.io.IOException {
    }

    public void setEnclosingRect() {
    }

    void save(DataOutputStream outputStream) throws java.io.IOException {
    }

    public void search(SearchMsg msg) {
    }

    public void select(SelectMsg msg) {
    }

    public void setSelected() {
    }

    public String getEditMessage() {
        return "Edit message";
    }

    public String getEditTitle() {
        return "Edit title";
    }

    public void setUnSelected() {

    }

    public void draw(Canvas canvas) {
    }

    public void setDefaultNumberOfIos() {
    }

    public DeviceIOViewer findIO(int x, int y) {
        return null;
    }

    public boolean hasSelectedIO(Selections selection) {
        return false;
    }

    public void plusIO(EditorViewer editorViewer) {
    }
    public void minusIO(EditorViewer editorViewer) {
    }

    public boolean isRoutable() {
        return false;
    }

    public boolean isWire() {
        return false;
    }

    public boolean startsOrEndsAt(int x, int y) {
        return false;
    }

    public boolean removeSelectedIO(Selections selections) {
        return false;
    }

    public void changeInv(Selections selections) {
    }

    public void unselect(Selections selections) {
    }

    public void makeLogicalConnection(HashMap<Integer, DeviceIOModel> nets) {
    }

    public void connectLabels(HashMap<String, DeviceIOModel> labelNets) {
    }

    public float showText(Canvas canvas, float x, float y) {
        return y;
    }

    public boolean showRecord(Canvas canvas, float startTime, float endTime, float y, float zoom) {
        return false;
    }

    public void disconnectLogical() {
    }

    public void putMask(Router router) {
    }

    public void putRestrictedMask(Router router, Viewer vStart, Viewer vEnd) {
    }

    public void putStartAndTarget(Router router) {
    }

    public void putRestrictedStartAndTarget(Router router, Viewer vStart, Viewer vEnd) {
    }

    public DeviceIOViewer getIoCanvasPos(DeviceIOViewer ioViewer) {
        return null;
    }

    public void cursorStep(Cursor cursor) {
    }

    public void drawEnlarged(Canvas canvas, float zoom) {
    }

    float getSelectionZoom(int canvasHeight, int canvasWidth, float scaleFactor) {
        return 1;
    }

    void move(EditorViewer editorViewer, int dx, int dy, boolean checkInside) {
    }

    public void setNetNumberAt(EditorModel model, int netNumber, int x, int y) {
    }

    public int getNetNumber() {
        return 0;
    }

    public void setNetNumber(int netNumber) {
    }

    public void routeNetNumber(Router router) {
    }

    public void cutNet(EditorModel model) {
    }

    public boolean isName(String s) {
        return false;
    }

    public Viewer split(WirePoint p) {
        return null;
    }

    public void checkConnection(EditorModel em) {
    }

    public boolean pushButton(PushButtonMsg pushButtonMsg) {
        return false;
    }

    public boolean releaseButton(PushButtonMsg pushButtonMsg) {
        return false;
    }

    public void compressNetNumbers(HashMap<Integer, Integer> nets, int[] newNetNumber) {
        int oldNetNumber = getNetNumber();
        if (!nets.containsKey(oldNetNumber)) {
            nets.put(oldNetNumber, newNetNumber[0]);
            setNetNumber(newNetNumber[0]++);
        } else {
            setNetNumber(nets.get(oldNetNumber));
        }
    }

    public int reconnect(EditorModel model, int netNumber, int x, int y) {
        return 0;
    }

    public int reconnectInX(EditorModel editorModel, int netNumber, int startX, int endX, int y) {
        return 0;
    }

    public int reconnectInY(EditorModel editorModel, int netNumber, int x, int startY, int endY) {
        return 0;
    }

    public void reconnectFrom(EditorModel em) {
    }

    public boolean setFaultPos(FaultViewer fv) {
        return false;
    }

    public int getContextMenuMask() {
        return 0xffffffff;
    }

    public int getLeft() {
        return Integer.MIN_VALUE;
    }

    public int getTop() {
        return Integer.MIN_VALUE;
    }

    public int getBottom() {
        return Integer.MAX_VALUE;
    }

    public int getRight() {
        return Integer.MAX_VALUE;
    }

    public int getWidth() {
        return 0;
    }

    public int getHeight() {
        return 0;
    }

    public void renumber(int netNumber, LinkedList<Integer> newNetNumbers) {
    }

    public void showDelayPopup(Activity activity) {
    }

    public void disconnectFrom(EditorModel em) {
    }

    public boolean setHolesUsed(EditorViewer editorViewer) {
        return true;
    }

    public void setHolesFree(EditorViewer editorViewer) {

    }

    public void disconnect(EditorModel editorModel, int netNumber, int x, int y) {
    }

    public String reroute(EditorModel editorModel) {
        return null;
    }

    public void prepareReroute(LinkedList<Integer> netList) {
    }

    public String getLabel() {
        return null;
    }

    public String getPortLabel() {
        return null;
    }

    public void setLabel(String label) {
    }

    public int getMaxSimTime(int soFar) {
        return soFar;
    }

    public boolean moveCheck(int dx, int dy) {
        return false;
    }

    public boolean alignHorizontal(EditorViewer editorViewer, int x, int toBottom) {
        return true;
    }

    public void changeNetNumber(int netNumber, int newNetNumber) {
    }

    public void endMove() {
    }

    public void setLevel(int level) {
    }

    public boolean moveSegmentOrPoint(EditorViewer editorViewer, float fromX, float fromY, int
            xOffset, int yOffset) {
        return false;
    }

    public void endSegmentMove(EditorModel model) {

    }

    public void setNetNumberInsideX(EditorModel editorModel, int netNumber, int xMin, int xMax, int y) {
    }

    public void setNetNumberInsideY(EditorModel editorModel, int netNumber, int x, int yMin, int yMax) {
    }

    public void disconnectInX(EditorModel em, int netNumber, int startX, int endX, int y) {
    }

    public void disconnectInY(EditorModel em, int netNumber, int x, int startY, int endY) {
    }

    public void forcedSelect(SelectMsg selectMsg) {
        selectMsg.viewer = this;
    }

    void initPosition(int left, int bottom, int angleIndex) {
    }

    public void initPosition(int x, int y, int angleIndex, int raster) {
    }

    public int getNetNumberAt(int x, int y) {
        return NetNumbers.NOT_CONNECTED;
    }

    public int getNetNumberInX(int minX, int maxX, short y) {
        return NetNumbers.NOT_CONNECTED;
    }

    public int getNetNumberInY(short x, int minY, int maxY) {
        return NetNumbers.NOT_CONNECTED;
    }

    public int getMaxIdLength() {
        return 0;
    }

    public Viewer deleteAt(int x, int y, BreadBoardViewer breadBoardViewer) {
        return null;
    }

    public void delete(EditorViewer editorViewer) {
    }

    public Viewer selectAt(int x, int y, BreadBoardViewer breadBoardViewer) {
        return null;
    }

    public void propagateNetNumber(BreadBoardViewer breadBoardViewer, int netNumber) {
    }

    public void powerOff() {
    }

    ;

    public void setLToH() {

    }

    public void setHToL() {
    }

    public void setDisabled() {

    }

    public boolean isMovable() {
        return true;
    }

    public void connectPower(Powered powered) {

    }

    public  void registrateModelForChange(int time){

    }

    static protected class Context {
        enum Style {ANSI, BS, IEC, DIN, NEMA}

        public static final int DARK_GREEN = 0xff00aa00;
        public static final int LT_RED = 0xffff7777;
        public static Paint probeOverflowPaint;
        public static Paint scopePaint;
        public static Paint scopeUndefPaint;
        public static Paint unlockedPaint;
        public static Paint lockedPaint;
        public static Paint netNumberPaint;
        static Paint normalPaint;
        static Paint selectedPaint;
        static Paint filledPaint;
        static Paint multiSelectedPaint;
        static Style style = Style.ANSI;
        static Paint cannotDeletePaint;
        static Paint textPaint;
        static Paint selectedTextPaint;
        static Paint scopeTextPaint;
        static Paint displayPaint;
        static Paint scopeModuloTimePaint;
        static Paint scopeTimePaint;
        static Paint cursorPaint;
        static Paint normalCursorPaint;
        static Paint redCursorPaint;
        static Paint borderPaint;
        static Paint highLevelPaint;
        static Paint lowLevelPaint;
        static Paint undefinedLevelPaint;
        static Paint iconBackgroundPaint;
        static Paint toolTipPaint;
        static Paint bbWirePaint;
        static Paint bbSelectedWirePaint;

        static {
            setWhiteOnBlack();
        }

        public static Paint getLevelColorPaint(int level) {
            if (level == State.Level.high.ordinal()) {
                return highLevelPaint;

            } else if (level == State.Level.low.ordinal()) {
                return lowLevelPaint;
            } else {
                return undefinedLevelPaint;
            }
        }

        static void createBBWirePaint(int color) {
            bbWirePaint = onePaint(160, color);
            bbWirePaint.setStrokeWidth(3.0f);
            bbWirePaint.setStrokeCap(Paint.Cap.BUTT);
            bbWirePaint.setAntiAlias(true);
            bbSelectedWirePaint = onePaint(160, 0xff0000);
            bbSelectedWirePaint.setStrokeWidth(3.0f);
            bbSelectedWirePaint.setStrokeCap(Paint.Cap.BUTT);
            bbSelectedWirePaint.setAntiAlias(true);
        }

        static void createCursorPaints(int color) {
            normalCursorPaint = onePaint(color);
            normalCursorPaint.setAntiAlias(true);
            normalCursorPaint.setStyle(Paint.Style.STROKE);

            redCursorPaint = onePaint(Color.RED);
            redCursorPaint.setAntiAlias(true);
            redCursorPaint.setStyle(Paint.Style.STROKE);

            cursorPaint = normalCursorPaint;
        }

        static void createIconBackgroundPaint(int color) {
            iconBackgroundPaint = onePaint(color);
            iconBackgroundPaint.setStyle(Paint.Style.FILL);
        }

        static void createToolTipPaint(int color) {
            toolTipPaint = onePaint(color);
            toolTipPaint.setStyle(Paint.Style.FILL);
        }

        static void createNetnumberPaints(int color) {
            netNumberPaint = onePaint(color);
            netNumberPaint.setTextSize(12); //4
            netNumberPaint.setAntiAlias(true);
            netNumberPaint.setTypeface(Typeface.create(Typeface.MONOSPACE, Typeface.NORMAL));
            netNumberPaint.setStrokeWidth(STROKE_WITH / 2);
            netNumberPaint.setLinearText(true);
            netNumberPaint.setSubpixelText(true);
            netNumberPaint.setFlags(Paint.EMBEDDED_BITMAP_TEXT_FLAG | Paint.FILTER_BITMAP_FLAG | Paint
                    .ANTI_ALIAS_FLAG | Paint.DITHER_FLAG | Paint.SUBPIXEL_TEXT_FLAG);
        }

        private static void setTextPaint(Paint textPaint) {
            textPaint.setAntiAlias(true);
            textPaint.setTypeface(Typeface.create(Typeface.MONOSPACE, Typeface.NORMAL));
            textPaint.setStrokeWidth(STROKE_WITH / 2);
            textPaint.setLinearText(true);
            textPaint.setSubpixelText(true);
            textPaint.setFlags(Paint.EMBEDDED_BITMAP_TEXT_FLAG | Paint.FILTER_BITMAP_FLAG | Paint
                    .ANTI_ALIAS_FLAG | Paint.DITHER_FLAG | Paint.SUBPIXEL_TEXT_FLAG);
        }

        static void setWhiteOnBlack() {
            normalPaint = onePaint(Color.WHITE);
            currentPaint = normalPaint;
            highLevelPaint = onePaint(0xffff7777);
            lowLevelPaint = onePaint(Color.GREEN);
            undefinedLevelPaint = onePaint(Color.YELLOW);
            borderPaint = onePaint(Color.WHITE);
            borderPaint.setStyle(Paint.Style.STROKE);
            borderPaint.setStrokeWidth(20);
            createCursorPaints(Color.WHITE);
            createNetnumberPaints(Color.YELLOW);
            createIconBackgroundPaint(Color.LTGRAY);
            createToolTipPaint(0xaaaa00aa);
            setTextPaint(toolTipPaint);
            unlockedPaint = onePaint(Color.YELLOW);
            lockedPaint = onePaint(Color.WHITE);
            createBBWirePaint(Color.CYAN);
            selectedPaint = onePaint(Color.RED);
            multiSelectedPaint = onePaint(0xffff4444);
            cannotDeletePaint = onePaint(Color.GRAY);
            textPaint = onePaint(Color.WHITE);
            selectedTextPaint = onePaint(Color.RED);
            scopeTextPaint = onePaint(Color.WHITE);
            displayPaint = onePaint(Color.RED);
            filledPaint = onePaint(Color.BLUE);
            filledPaint.setStyle(Paint.Style.FILL);
            filledPaint.setStrokeWidth(2 * STROKE_WITH);
            probeOverflowPaint = onePaint(Color.CYAN);
            probeOverflowPaint.setStyle(Paint.Style.FILL);
            scopePaint = onePaint(Color.GREEN);
            scopePaint.setStrokeWidth(2 * STROKE_WITH);
            scopeUndefPaint = onePaint(Color.GREEN);
            scopeUndefPaint.setStyle(Paint.Style.FILL);
            scopeModuloTimePaint = onePaint(Color.WHITE);
            scopeModuloTimePaint.setStrokeWidth(STROKE_WITH / 2);
            scopeTimePaint = onePaint(Color.WHITE);
            scopeTimePaint.setStrokeWidth(STROKE_WITH / 4);
//            textPaint.setTextSize(TEXT_SIZE);
            setTextPaint(textPaint);
            currentTextPaint = textPaint;
            setTextPaint((selectedTextPaint));
            setTextPaint(scopeTextPaint);
            displayPaint.setAntiAlias(true);
            displayPaint.setTypeface(Typeface.create(Typeface.MONOSPACE, Typeface.NORMAL));
            displayPaint.setStrokeWidth(STROKE_WITH / 2);
            displayPaint.setLinearText(true);
            displayPaint.setSubpixelText(true);
            displayPaint.setFlags(Paint.EMBEDDED_BITMAP_TEXT_FLAG | Paint.FILTER_BITMAP_FLAG | Paint
                    .ANTI_ALIAS_FLAG | Paint.DITHER_FLAG | Paint.SUBPIXEL_TEXT_FLAG);

//        paint.setTextAlign(Paint.Align.CENTER);
        }

        static void setBlackOnWhite() {
            normalPaint = onePaint(Color.BLACK);
            currentPaint = normalPaint;
            createCursorPaints(Color.BLACK);
            createIconBackgroundPaint(Color.DKGRAY);
            createToolTipPaint(0xaaaa00aa);
            setTextPaint(toolTipPaint);

            borderPaint = onePaint(Color.BLACK);
            borderPaint.setStyle(Paint.Style.STROKE);
            borderPaint.setStrokeWidth(20);
            highLevelPaint = onePaint(Color.RED);
            lowLevelPaint = onePaint(DARK_GREEN);
            undefinedLevelPaint = onePaint(Color.YELLOW);

            unlockedPaint = onePaint(Color.GRAY);
            lockedPaint = onePaint(Color.BLACK);
            createBBWirePaint(Color.BLUE);
            createNetnumberPaints(Color.BLUE);

            selectedPaint = onePaint(Color.RED);
            multiSelectedPaint = onePaint(0xffff4444);
            cannotDeletePaint = onePaint(Color.GRAY);
            textPaint = onePaint(Color.BLACK);
            selectedTextPaint = onePaint(Color.RED);
            scopeTextPaint = onePaint(Color.BLACK);
            displayPaint = onePaint(Color.RED);
            filledPaint = onePaint(Color.BLUE);
            filledPaint.setStyle(Paint.Style.FILL);
            filledPaint.setStrokeWidth(2 * STROKE_WITH);
            probeOverflowPaint = onePaint(Color.CYAN);
            probeOverflowPaint.setStyle(Paint.Style.FILL);
            scopePaint = onePaint(DARK_GREEN);
            scopePaint.setStrokeWidth(2 * STROKE_WITH);
            scopeUndefPaint = onePaint(DARK_GREEN);
            scopeUndefPaint.setStyle(Paint.Style.FILL);
            scopeModuloTimePaint = onePaint(Color.BLACK);
            scopeModuloTimePaint.setStrokeWidth(STROKE_WITH / 2);
            scopeTimePaint = onePaint(Color.BLACK);
            scopeTimePaint.setStrokeWidth(STROKE_WITH / 4);
//            textPaint.setTextSize(TEXT_SIZE);
            setTextPaint(textPaint);
            currentTextPaint = textPaint;
            setTextPaint((selectedTextPaint));
            setTextPaint(scopeTextPaint);
            displayPaint.setAntiAlias(true);
            displayPaint.setTypeface(Typeface.create(Typeface.MONOSPACE, Typeface.NORMAL));
            displayPaint.setStrokeWidth(STROKE_WITH / 2);
            displayPaint.setLinearText(true);
            displayPaint.setSubpixelText(true);
            displayPaint.setFlags(Paint.EMBEDDED_BITMAP_TEXT_FLAG | Paint.FILTER_BITMAP_FLAG | Paint
                    .ANTI_ALIAS_FLAG | Paint.DITHER_FLAG | Paint.SUBPIXEL_TEXT_FLAG);

//        paint.setTextAlign(Paint.Align.CENTER);

        }

        static void setBlackOnSilver() {
            normalPaint = onePaint(Color.BLACK);
            currentPaint = normalPaint;
            createCursorPaints(Color.BLACK);
            createIconBackgroundPaint(Color.DKGRAY);
            createToolTipPaint(0xaaaa00aa);
            setTextPaint(toolTipPaint);

            borderPaint = onePaint(Color.BLACK);
            borderPaint.setStyle(Paint.Style.STROKE);
            borderPaint.setStrokeWidth(20);
            highLevelPaint = onePaint(Color.RED);
            lowLevelPaint = onePaint(DARK_GREEN);
            undefinedLevelPaint = onePaint(Color.YELLOW);

            unlockedPaint = onePaint(Color.GRAY);
            lockedPaint = onePaint(Color.BLACK);
            createBBWirePaint(Color.BLUE);
            createNetnumberPaints(Color.BLUE);

            selectedPaint = onePaint(Color.RED);
            multiSelectedPaint = onePaint(0xffff4444);
            cannotDeletePaint = onePaint(Color.GRAY);
            textPaint = onePaint(Color.BLACK);
            selectedTextPaint = onePaint(Color.RED);
            scopeTextPaint = onePaint(Color.BLACK);
            displayPaint = onePaint(Color.RED);
            filledPaint = onePaint(Color.BLUE);
            filledPaint.setStyle(Paint.Style.FILL);
            filledPaint.setStrokeWidth(2 * STROKE_WITH);
            probeOverflowPaint = onePaint(Color.CYAN);
            probeOverflowPaint.setStyle(Paint.Style.FILL);
            scopePaint = onePaint(DARK_GREEN);
            scopeUndefPaint = onePaint(DARK_GREEN);
            scopeUndefPaint.setStyle(Paint.Style.FILL);
            scopePaint.setStrokeWidth(2 * STROKE_WITH);
            scopeModuloTimePaint = onePaint(Color.BLACK);
            scopeModuloTimePaint.setStrokeWidth(STROKE_WITH / 2);
            scopeTimePaint = onePaint(Color.BLACK);
            scopeTimePaint.setStrokeWidth(STROKE_WITH / 4);
            setTextPaint(textPaint);
            currentTextPaint = textPaint;
            setTextPaint((selectedTextPaint));
            setTextPaint(scopeTextPaint);
            displayPaint.setAntiAlias(true);
            displayPaint.setTypeface(Typeface.create(Typeface.MONOSPACE, Typeface.NORMAL));
            displayPaint.setStrokeWidth(STROKE_WITH / 2);
            displayPaint.setLinearText(true);
            displayPaint.setSubpixelText(true);
            displayPaint.setFlags(Paint.EMBEDDED_BITMAP_TEXT_FLAG | Paint.FILTER_BITMAP_FLAG | Paint
                    .ANTI_ALIAS_FLAG | Paint.DITHER_FLAG | Paint.SUBPIXEL_TEXT_FLAG);

//        paint.setTextAlign(Paint.Align.CENTER);

        }

        static void setBlackOnGreen() {
            normalPaint = onePaint(Color.BLACK);
            currentPaint = normalPaint;
            createCursorPaints(Color.BLACK);
            createIconBackgroundPaint(Color.DKGRAY);
            createToolTipPaint(0xaaaa00aa);
            setTextPaint(toolTipPaint);

            borderPaint = onePaint(Color.BLACK);
            borderPaint.setStyle(Paint.Style.STROKE);
            borderPaint.setStrokeWidth(20);

            highLevelPaint = onePaint(Color.RED);
            lowLevelPaint = onePaint(DARK_GREEN);
            undefinedLevelPaint = onePaint(Color.YELLOW);

            unlockedPaint = onePaint(Color.GRAY);
            lockedPaint = onePaint(Color.BLACK);
            createBBWirePaint(Color.BLACK);
            createNetnumberPaints(Color.BLUE);

            selectedPaint = onePaint(Color.RED);
            multiSelectedPaint = onePaint(0xffff4444);
            cannotDeletePaint = onePaint(Color.GRAY);
            textPaint = onePaint(Color.BLACK);
            selectedTextPaint = onePaint(Color.RED);
            scopeTextPaint = onePaint(Color.BLACK);
            displayPaint = onePaint(Color.RED);
            filledPaint = onePaint(Color.BLUE);
            filledPaint.setStyle(Paint.Style.FILL);
            filledPaint.setStrokeWidth(2 * STROKE_WITH);
            probeOverflowPaint = onePaint(Color.CYAN);
            probeOverflowPaint.setStyle(Paint.Style.FILL);
            scopePaint = onePaint(Color.BLUE);
            scopePaint.setStrokeWidth(2 * STROKE_WITH);
            scopeUndefPaint = onePaint(Color.BLUE);
            scopeUndefPaint.setStyle(Paint.Style.FILL);
            scopeModuloTimePaint = onePaint(Color.BLACK);
            scopeModuloTimePaint.setStrokeWidth(STROKE_WITH / 2);
            scopeTimePaint = onePaint(Color.BLACK);
            scopeTimePaint.setStrokeWidth(STROKE_WITH / 4);
            setTextPaint(textPaint);
            currentTextPaint = textPaint;
            setTextPaint((selectedTextPaint));
            setTextPaint(scopeTextPaint);
            displayPaint.setAntiAlias(true);
            displayPaint.setTypeface(Typeface.create(Typeface.MONOSPACE, Typeface.NORMAL));
            displayPaint.setStrokeWidth(STROKE_WITH / 2);
            displayPaint.setLinearText(true);
            displayPaint.setSubpixelText(true);
            displayPaint.setFlags(Paint.EMBEDDED_BITMAP_TEXT_FLAG | Paint.FILTER_BITMAP_FLAG | Paint
                    .ANTI_ALIAS_FLAG | Paint.DITHER_FLAG | Paint.SUBPIXEL_TEXT_FLAG);

//        paint.setTextAlign(Paint.Align.CENTER);

        }

        static Paint onePaint(int color) {
            Paint paint = new Paint();
            paint.setColor(color);
            paint.setAntiAlias(true);
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(STROKE_WITH);
            paint.setStrokeJoin(Paint.Join.ROUND);    // set the join to round you want
            paint.setStrokeCap(Paint.Cap.ROUND);      // set the paint cap to round too
            return paint;
        }

        static Paint onePaint(int alpha, int color) {
            Paint paint = new Paint();
            paint.setARGB(alpha, (color >> 16) & 0xff, (color >> 8) & 0xff, color & 0xff);
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(STROKE_WITH);
            return paint;
        }

    }

}
