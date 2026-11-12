package com.logicbreadboard.simulator;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import androidx.core.view.GestureDetectorCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.widget.TextView;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

/**
 * Created on 12/17/15.
 */

public class BreadBoardViewer extends EditorViewer {
    private static final String CLOCK_NAME = "clock";
    private static final String PROBE_NAME = "ch";
    int width = 0;
    int height = 0;
    Path path;
    //Bitmap bitmap;
    static final int rasterSize = 16;
    final static int scrollMargin = 100;
    int breadboardResourceIndex = 1;
    private int holeSize;
    private int halfHoleSize;
    private InvisibleHighViewer powerHighViewer;
    private InvisibleGndViewer powerGroundViewer;
    private Viewer[] clockViewers;
    private Hole startWire = null;
    private int maxX;
    private int maxY;
    //    final static int[] BREADBOARD_IDS = {R.raw.breadboard_not_eq, R.raw.breadboard_eq};
    private String[] breadBoardNames = {"breadboard_not_eq.txt", "breadboard_eq.txt"};
    PowerHigh powerHigh;
    PowerGround powerGround;
    ArrayList<ClockHole> clockHoles;
    ArrayList<ScopeChannel> scopeChannelHoles;
    private TextView timeView;
    private Viewer[] probeViewers;
    private int clockIndex;
    private int probeIndex;
    private boolean firstMove;

    @Override
    public void propagateNetNumberAt(int x, int y, int netNumber) {
        x = (x) / rasterSize - 1;
        y = (y) / rasterSize - 1;
//        int ix = Math.round(x);
//        int iy = Math.round(y);
        if (x >= 0 && y >= 0 && x < board.length && y < board[x].length) {
            Hole hp = (Hole) board[x][y];
            if (hp != null) {
                hp.propagateNetNumberOnBoard(netNumber);
            } else {
                Log.i("HP", "NULL");
            }
        } else {
            Log.i("HP", "OUTSIDE");
        }
    }

    class RasterPoint {

        public void connectNeighbours(int x, int y) {
//            Log.i("BREAD", "connect error " + x + " " + y);
        }

        void connectHolesOnBoard(RasterPoint rp) {
        }

        void connectWire(Hole start) {
        }

        public boolean setUsed(Viewer v) {
            return false;
        }

        public boolean isUsed() {
            return true;
        }

        public void setFree() {
        }

        public boolean select() {
            return false;
        }

        public void unSelect() {
        }

        public void draw(Canvas canvas) {
        }
    }

    class Hole extends RasterPoint {
        //  DeviceIOViewer io;
        boolean selected = false;
        Viewer usedBy = null;
        int x;
        int y;
        Hole boardConnection;
        Hole wiredConnection;

//        Hole() {
//            boardConnection = this;
//            wiredConnection = this;
//        }

        Hole(int x, int y) {
            this.x = (x + 1) * rasterSize;
            this.y = (y + 1) * rasterSize;
            boardConnection = this;
            wiredConnection = this;
        }

        @Override
        void connectHolesOnBoard(RasterPoint rp) {
            if (rp instanceof Hole) {
                Hole h = (Hole) rp;
                if (!h.isConnectedOnBoard(this)) {
                    h.boardConnection = this.boardConnection;
                    this.boardConnection = h;
                }
            }
        }

        private boolean isConnectedOnBoard(Hole hole) {
            Hole hp = this.boardConnection;
            while (hp != this) {
                if (hp == hole) {
                    return true;
                }
                hp = boardConnection;
            }
            return false;
        }

        @Override
        void connectWire(Hole toHole) {
            this.wiredConnection = toHole;
            toHole.wiredConnection = this;
        }

//        private boolean isConnectedWithWire(Hole hole) {
//            Hole hp = this.wiredConnection;
//            while (hp != this) {
//                if (hp == hole) {
//                    return true;
//                }
//                hp = wiredConnection;
//            }
//            return false;
//        }
//

        public void disconnect(Hole a, Hole b) {
            a.wiredConnection = a;
            b.wiredConnection = b;
            a.propagateNetNumberOnBoard(NetNumbers.get());
        }

        public void unSelect() {
            selected = false;
        }

        public boolean select() {
            if (usedBy == null) {
                if (selected) {
                    startWire = null;
                    selected = false;
                } else {
                    selected = true;
                    if (startWire == null) {
                        startWire = this;
                    } else if (startWire != this) {
                        BBWireViewer bbWire = new BBWireViewer(startWire.x, startWire.y, x, y);
                        add(bbWire);
                        startWire.setUsed(bbWire);
                        this.setUsed(bbWire);
                        connectWire(startWire);
                        this.unSelect();
                        startWire.unSelect();
                        startWire = null;
                    } else {
                        startWire = null;
                        selected = false;
                    }
                }
                return true;
            } else {
                if (startWire != null) {
                    disconnect(startWire, this);
                    startWire.unSelect();
                }
                startWire = null;
                model.selectWireAt(x, y, BreadBoardViewer.this);
//                model.deleteAt(x, y, BreadBoardViewer.this);
//                model.makeSnapshot();
                return false;
            }
        }

        public boolean setUsed(Viewer v) {
            if (usedBy != null) {
                return false;
            } else {
                usedBy = v;
                propagateNetNumberOnBoard(v.getNetNumber());
                return true;
            }
        }

        protected void propagateNetNumberOnBoard(int netNumber) {
            Hole hp = this.boardConnection;
            while (hp != this) {
                if (hp.usedBy != null) {
                    hp.usedBy.propagateNetNumber(BreadBoardViewer.this, netNumber);
                }
                hp = hp.boardConnection;
            }
        }

        public void setFree() {
            usedBy = null;
            wiredConnection = this;
        }

        public boolean isUsed() {
            return usedBy != null;
        }

        @Override
        public void draw(Canvas canvas) {
            Paint paint;
//            this.x = x;
//            this.y = y;
            if (selected) {
                paint = Viewer.Context.selectedPaint;
            } else {
                paint = Viewer.Context.normalPaint;
            }
            canvas.drawRect(x - holeSize, y - holeSize, x + holeSize, y + holeSize, paint);
//            canvas.drawRect(x - holeSize, y - holeSize, x + holeSize, y + holeSize, Viewer.Context.normalPaint);
//            canvas.drawRoundRect(x - holeSize, y - holeSize, x + holeSize, y + holeSize, 2, 2, Viewer.Context.normalPaint);
            int size = rasterSize / 2;
            if (usedBy != null) {
                canvas.drawCircle(x, y, size, Viewer.Context.normalPaint);
            }
        }
    }

    class PowerHigh extends Hole {
        PowerHigh(int x, int y) {
            super(x, y);
            powerHigh = this;
        }

        protected void propagateNetNumberOnBoard(int netNumber) {
            super.propagateNetNumberOnBoard(netNumber);
            powerHighViewer.firstIoViewer.setNetNumber(netNumber);
        }
    }

    class PowerGround extends Hole {
        PowerGround(int x, int y) {
            super(x, y);
            powerGround = this;
        }

        protected void propagateNetNumberOnBoard(int netNumber) {
            super.propagateNetNumberOnBoard(netNumber);
            powerGroundViewer.firstIoViewer.setNetNumber(netNumber);
        }
    }

    class ScopeChannel extends Hole {
        int index;

        ScopeChannel(int x, int y, int index) {
            super(x, y);
            this.index = index;
            scopeChannelHoles.add(this);
        }

        protected void propagateNetNumberOnBoard(int netNumber) {
            super.propagateNetNumberOnBoard(netNumber);
            ((BBProbeViewer) probeViewers[index]).firstIoViewer.setNetNumber(netNumber);
        }
    }

    class ClockHole extends Hole {
        int index;

        ClockHole(int x, int y, int index) {
            super(x, y);
            this.index = index;
            clockHoles.add(this);
        }

        protected void propagateNetNumberOnBoard(int netNumber) {
            super.propagateNetNumberOnBoard(netNumber);
            ((BBClockViewer) clockViewers[index]).firstIoViewer.setNetNumber(netNumber);
        }
    }

    enum Direction {H, V}

    class Route extends RasterPoint {
        Direction dir;

        Route(Direction d) {
            dir = d;
        }

        @Override
        public void connectNeighbours(int x, int y) {
            if (dir == Direction.H) {
                if (x > 0 && x < width - 1) {
                    RasterPoint left = board[x - 1][y];
                    RasterPoint right = board[x + 1][y];
                    if (left != null && right != null) {
                        left.connectHolesOnBoard(right);
                        right.connectHolesOnBoard(left);
                    }
                    if (x > 1 && x < width - 2) {
                        left = board[x - 2][y];
                        right = board[x + 2][y];
                        if (left != null && right != null) {
                            left.connectHolesOnBoard(right);
                            right.connectHolesOnBoard(left);
                        }
                    }
                }
            } else if (dir == Direction.V) {
                if (y > 0 && y < height - 1) {
                    RasterPoint up = board[x][y - 1];
                    RasterPoint down = board[x][y + 1];
                    if (up != null && down != null) {
                        up.connectHolesOnBoard(down);
                        down.connectHolesOnBoard(up);
                    }
                    if (y > 1 && y < height - 2) {
                        up = board[x][y - 2];
                        down = board[x][y + 2];
                        if (up != null && down != null) {
                            up.connectHolesOnBoard(down);
                            down.connectHolesOnBoard(up);
                        }
                    }
                }
            }
        }
    }

    class Label extends RasterPoint {
        char ch;
        int x;
        int y;

        Label(char c, int x, int y) {
            this.x = (x + 1) * rasterSize;
            this.y = (y + 1) * rasterSize;
            ch = c;
        }

        public void draw(Canvas canvas) {
            canvas.drawText(String.valueOf(ch), x - halfHoleSize, y + halfHoleSize, Viewer.Context.textPaint);
        }
    }

    RasterPoint[][] board;

    /*
    o o   1 o=o=o=o=o     o o o o o     o o
    | |                                 | |
    o o   2 o o o o o     o o o o o     o o

    o o   3 o o o o o     o o o o o     o o

    o o   4 o o o o o     o o o o o     o o

    o o   5 o o o o o     o o o o o     o o

    o o   6 o o o o o     o o o o o     o o

    o o   7 o o o o o     o o o o o     o o

    o o   8 o o o o o     o o o o o     o o

    o o   9 o o o o o     o o o o o     o o

    o o  10 o o o o o     o o o o o     o o

    o o  11 o o o o o     o o o o o     o o
    */


    void createDefaultBoard() {

    }

    char b = 'b';
    int j = 10;
    char r = 'r';
    char e = 'e';
    char a = 'a';
    char d = 'd';
    char o = 'o';
    char s = 's';
    char m = 'm';
    char k = 'k';
    char l = 'l';

    void createBreadBoard() {
        FileInputStream fis = null;
        InputStream rIn;
        DataInputStream in;
        try {
//            rIn = getResources().openRawResource(BREADBOARD_IDS[breadboardResourceIndex]);
            rIn = MainActivity.assetManager.open("breadboardlayouts/" + breadBoardNames[breadboardResourceIndex]);
            in = new DataInputStream(rIn);
            boolean widthSet = false;
            int w = 0;
            while (in.available() > 0) {
                byte b = in.readByte();
                if (!widthSet) {
                    if (b != '\n') {
                        width++;
                    } else {
                        height++;
                        widthSet = true;
                    }
                } else {
                    if (b != '\n') {
                        w++;
                    } else {
                        height++;
                        if (width != w) {
                            Log.i("BREAD", "Wrong board file");
                            return;
                        } else {
                            w = 0;
                        }
                    }
                }
                if (fis != null) {
                    fis.close();
                }
            }
        } catch (FileNotFoundException e1) {
            e1.printStackTrace();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
//        width--;
        height--;
//        createBoard(fis, localWidth, localHeight);
//        loadBoardFile(fis);
        loadAndRotateBoardFile(fis);
    }

    private void loadAndRotateBoardFile(FileInputStream fis) {
        InputStream rIn;
        DataInputStream in;
        int tmp = width;
        width = height;
        height = tmp;
        maxX = width * rasterSize;
        maxY = height * rasterSize;
        board = new RasterPoint[width][height];
//            fis = new FileInputStream(fileName);
//            in = new DataInputStream(fis);
        int x = 0;
        int y = 0;
        probeIndex = 0;
        clockIndex = 0;
        try {
//            rIn = getResources().openRawResource(BREADBOARD_IDS[breadboardResourceIndex]);
            rIn = MainActivity.assetManager.open("breadboardlayouts/" + breadBoardNames[breadboardResourceIndex]);
            in = new DataInputStream(rIn);
            for (x = 0; x < width; x++) {
                for (y = 0; y < height; y++) {
//                    Log.i("LOADBB", "x=" + x + "y=" + y);
                    loadLine(in, y, x, Direction.V, Direction.H);
                }
                if (in.available() > 0) {
                    in.readByte();
                }
            }
            if (fis != null) {
                fis.close();
            }
        } catch (
                FileNotFoundException e1
                ) {
            e1.printStackTrace();
        } catch (
                IOException e1
                ) {
//            System.out.println("LOADBB " + "width=" + width + " height=" + height);
//            System.out.println("LOADBB " + "x=" + x + " y=" + y);
            e1.printStackTrace();
        }
        makeConnections();
    }

    private void loadBoardFile(FileInputStream fis) {
        InputStream rIn;
        DataInputStream in;
        probeIndex = 0;
        clockIndex = 0;
        board = new RasterPoint[width][height];
        try {
//            rIn = getResources().openRawResource(BREADBOARD_IDS[breadboardResourceIndex]);
            rIn = MainActivity.assetManager.open("breadboardlayouts/" + breadBoardNames[breadboardResourceIndex]);
            in = new DataInputStream(rIn);
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    loadLine(in, y, x, Direction.H, Direction.V);
                }
                if (in.available() > 0) {
                    in.readByte();
                }
            }
            if (fis != null) {
                fis.close();
            }
        } catch (
                FileNotFoundException e1
                ) {
            e1.printStackTrace();
        } catch (
                IOException e1
                ) {
            e1.printStackTrace();
        }
        makeConnections();
    }

    private void loadLine(DataInputStream in, int y, int x, Direction dirH, Direction dirV) throws IOException {
        byte b = in.readByte();
//                    Log.i("READ", "x=" + x + " y=" + y + " b=" + (char) b);
        switch (b) {
            case 'o':
                board[x][y] = new Hole(x, y);
                break;
            case '=':
                board[x][y] = new Route(dirH);
                break;
            case '#':
                board[x][y] = new Route(dirV);
                break;
            case '+':
            case '-':
            case 'a':
            case 'b':
            case 'c':
            case 'd':
            case 'e':
            case 'f':
            case 'g':
            case 'h':
            case 'i':
            case 'j':
            case 'l':
            case 'k':
            case 'P':
            case 'O':
            case 'W':
            case 'E':
            case 'R':
            case '0':
            case '1':
            case '2':
            case '3':
            case '4':
            case '5':
            case '6':
            case '7':
            case '8':
            case '9':
                board[x][y] = new Label((char) b, x, y);
                break;
            case 'H':
                board[x][y] = new PowerHigh(x, y);
                break;
            case 'G':
                board[x][y] = new PowerGround(x, y);
                break;
            case 'C':
                board[x][y] = new ClockHole(x, y, clockIndex++);
                break;
            case 'S':
                board[x][y] = new ScopeChannel(x, y, probeIndex++);
                break;
            default:
        }
    }

    private void makeConnections() {
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                RasterPoint rp = board[x][y];
                if (rp != null) {
                    rp.connectNeighbours(x, y);
                }
            }
        }
    }

    private void clearHoles() {
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                RasterPoint rp = board[x][y];
                if (rp != null) {
                    rp.setFree();
                }
            }
        }
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        canvasWidth = getWidth();
        canvasHeight = getHeight();
        if (model == null) {
            return;
        }
        if (model.enableScope) {
            dispatchScopeDraw(canvas);
        } else {
            DIPViewer.rasterSize = rasterSize;
            xOrigo = canvasWidth / 2;
            yOrigo = canvasHeight / 2;
            canvas.translate(xOffset, yOffset);
            staticScaleFactor = scaleFactor;
            staticXOffset = xOffset;
            staticYOffset = yOffset;
            canvas.scale(scaleFactor, scaleFactor, xOrigo - xOffset, yOrigo - yOffset);
//            canvas.getClipBounds(clipBounds);
//            Viewer.Context.borderPaint.setStrokeWidth(2 / scaleFactor);
//            Viewer.Context.borderPaint.setStyle(Paint.Style.FILL);
//            canvas.clipRect(EditorModel.MIN_X, EditorModel.MIN_Y, EditorModel
//                    .MAX_X, EditorModel.MAX_Y, Region.Op.DIFFERENCE);
//            canvas.drawARGB(255, 0, 50, 0);
//            //restore full canvas clip for any subsequent operations
//            canvas.clipRect(new Rect(0, 0, canvas.getWidth(), canvas.getHeight())
//                    , Region.Op.REPLACE);
//            canvas.drawRect(EditorModel.MIN_X, EditorModel.MIN_Y, EditorModel
//                    .MAX_X, EditorModel.MAX_Y, Viewer.Context.borderPaint);
//            canvas.drawBitmap(bitmap, 0, 0, Viewer.currentPaint);
            drawBoard(canvas);
//            ContextMenuIcons.left = -xOffset + (xOrigo) - (xOrigo) / scaleFactor;
//            ContextMenuIcons.top = -yOffset + (yOrigo) - (yOrigo) / scaleFactor;
            if (model != null) {
                model.drawAll(canvas, canvasWidth, canvasHeight, scaleFactor, xOffset, yOffset);
//                cursor.draw(canvas, scaleFactor);
                ((MainActivity) activity).setContextMenuVisibility();
            }
        }
    }


    public BreadBoardViewer(Context context) {
        super(context);
        scopeChannelHoles = new ArrayList<>();
        clockHoles = new ArrayList<>();
    }

    private void drawBoard(Canvas canvas) {
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if (board[x][y] != null) {
                    board[x][y].draw(canvas);
                }
            }
        }
    }

    public BreadBoardViewer(Context context, AttributeSet attrs) {
        super(context, attrs);
        scopeChannelHoles = new ArrayList<>();
        clockHoles = new ArrayList<>();
    }

    public BreadBoardViewer(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        scopeChannelHoles = new ArrayList<>();
        clockHoles = new ArrayList<>();
    }

    static Paint paint = new Paint();

    static {
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(Color.BLACK);
        paint.setStrokeWidth(20);
    }

    SimulationTask simulationTask;
    private float savedScaleFactor = 1;
    private float savedXOffset;
    private float savedYOfseet;
    private boolean firstScopeShow = true;
    private float savedScopeScaleFactor;
    private float savedScopeXOffset;
    private float savedScopeYOfseet;
    private int maxSimTime;

    protected void centerCursor() {
//        int x0 = -Math.round((-cursor.getX() + xOrigo) * scaleFactor - xOrigo - xOffset);
//        int y0 = -Math.round((-cursor.getY() + yOrigo) * scaleFactor - yOrigo - yOffset);
//        Log.i("centerCursor", "x0=" + x0);
//        Log.i("centerCursor", "y0=" + y0 + "\n");
        int x0 = cursor.getX();
        int y0 = cursor.getY();
        if (x0 < clipBounds.left || x0 > clipBounds.right) {
            xOffset = -(x0 - xOrigo);
        }
        if (y0 < clipBounds.top || y0 > clipBounds.bottom) {
            yOffset = -(y0 - yOrigo);
        }
    }

    public void add(Viewer v) {
        model.selections.unselect();
//        if (!cursor.enabled) {
//            CursorPosOnCanvas.x = Math.round(xOrigo - xOffset);
//            CursorPosOnCanvas.y = Math.round(yOrigo - yOffset);
//            cursor.set(CursorPosOnCanvas.x, CursorPosOnCanvas.y);
//        } else {
//            centerCursor();
//        }
//        centerCursor();
        model.add(v);
        if (!v.setHolesUsed(BreadBoardViewer.this)) {
            Log.i("ADD", "Placement error: left=" + Integer.toString(v.getLeft()) + " right=" + v.getRight() + " top=" + v.getTop() + " bottom=" + v.getBottom());
            model.delete(v); // a two terminates object could not place
        } else {
            model.makeSnapshot();
            model.forcedSelect(v);
        }
        invalidate();
    }

    synchronized void startSimulation() {
        simulationTask = new SimulationTask();
        simulationTask.execute(model);
    }

    public View getCanvas() {
        View v = findViewById(R.id.breadboard_canvas);
        return v;
    }

    @Override
    public void gotoWeb() {
        MenuStateMachine.gotoBBWeb();
    }


    @Override
    public void init(final MainActivity activity, EditorModel editorModel) {
        super.init(activity, editorModel);
        model = new EditorModel();
        timeView = (TextView) activity.findViewById(R.id.simulationTime);
        cursor = new Cursor(canvasWidth / 2, canvasHeight / 2);
//        ContextMenuIcons.help = false;
        holeSize = rasterSize * 3 / 4;
        halfHoleSize = holeSize / 2;
        createBreadBoard();
        gestureListener = new GestureDetectorCompat(activity, new BreadBoardGestureListener());
    }

    public void addInterfaces() {
//        Log.i("addInterfaces", "enter");
        powerHighViewer = new InvisibleHighViewer();
        powerHighViewer.setDefaultNumberOfIos();
        powerGroundViewer = new InvisibleGndViewer();
        powerGroundViewer.setDefaultNumberOfIos();
        clockViewers = new BBClockViewer[clockHoles.size()];
        for (int i = 0; i < clockViewers.length; i++) {
            String clockName = CLOCK_NAME + Integer.toString(i);
            clockViewers[i] = model.findName(clockName);
            if (clockViewers[i] == null) {
                clockViewers[i] = new BBClockViewer(clockName);
                model.add(clockViewers[i]);
            }
            clockViewers[i].initPosition(clockHoles.get(i).x - clockViewers[i].getWidth() - 8 * rasterSize, clockHoles.get(i).y + clockViewers[i].getHeight() / 2, 0);
            clockViewers[i].setDefaultNumberOfIos();
        }
        probeViewers = new BBProbeViewer[scopeChannelHoles.size()];
        for (int i = 0; i < probeViewers.length; i++) {
            String probeName = PROBE_NAME + Integer.toString(i);
            probeViewers[i] = model.findName(probeName);
            if (probeViewers[i] == null) {
                probeViewers[i] = new BBProbeViewer(probeName, probeName);
                model.add(probeViewers[i]);
            }
            probeViewers[i].initPosition(scopeChannelHoles.get(i).x - probeViewers[i].getWidth() - 8 * rasterSize, scopeChannelHoles.get(i).y + probeViewers[i].getHeight() / 2, 0);
            probeViewers[i].setDefaultNumberOfIos();
        }
    }

    public void showOnlineHelp() {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse
                ("http://www.logicbreadboard.com/home/andlogics"));
        activity.startActivity(browserIntent);
    }

    public void stopSimulation() {
        if (model.simulationIsRunning && simulationTask != null) {
            simulationTask.cancel(true);
        }
        model.simulationIsRunning = false;
        gestureListener = new GestureDetectorCompat(activity, new
                BreadBoardGestureListener());
        invalidate();
        model.powerOff();
        invalidate();
    }

    public void runButtonHandler() {
        cursor.enabled = false;
        moveSelectionEnabled = false;
        model.selections.unselect();
        model.faultViewers.clear();
        if (!model.contains(powerHighViewer)) {
            model.add(powerHighViewer);
        }
        if (!model.contains(powerGroundViewer)) {
            model.add(powerGroundViewer);
        }
        if (powerHigh.usedBy != null) {
            powerHigh.usedBy.propagateNetNumber(BreadBoardViewer.this, powerHighViewer.firstIoViewer.netNumber);
//                                System.out.println("2 plus netnumber=" + powerHighViewer.firstIoViewer.netNumber);
//                                System.out.println("2 wire netnumber=" + powerHigh.usedBy.getNetNumber());
        }
        if (powerGround.usedBy != null) {
            powerGround.usedBy.propagateNetNumber(BreadBoardViewer.this, powerGroundViewer.firstIoViewer.netNumber);
        }
        for (int i = 0; i < clockViewers.length; i++) {
            if (clockHoles.get(i).usedBy != null) {
                clockHoles.get(i).usedBy.propagateNetNumber(BreadBoardViewer.this, ((BBClockViewer) clockViewers[i]).firstIoViewer.netNumber);
            }
        }
        for (int i = 0; i < probeViewers.length; i++) {
            if (scopeChannelHoles.get(i).usedBy != null) {
                scopeChannelHoles.get(i).usedBy.propagateNetNumber(BreadBoardViewer.this, ((BBProbeViewer) probeViewers[i]).firstIoViewer.netNumber);
            }
        }
//        if (clocks.get(0).usedBy != null) {
//            if (!model.viewerLinkedList.contains(clock0)) {
////                no rotate clone del, akkor jelenjen meg amikor a clk hole-ba drot kerul es tunjon el ha nincs drot
////                egy tipus aminek be lehet allitani a kezdo szintjet
//                clock0 = new BBClockViewer();
//                clock0.initPosition(clocks.get(0).x - clock0.getWidth() - 100, clocks.get(0).y, 0, BreadBoardViewer.rasterSize);
//                clock0.setDefaultNumberOfIos();
//                model.add(clock0);
//                clocks.get(0).usedBy.propagateNetNumber(BreadBoardViewer.this, clock0.firstIoViewer.netNumber);
//            }
//        } else {
//            if (model.viewerLinkedList.contains(clock0)) {
//                model.viewerLinkedList.remove(clock0);
//            }
//
//        }
        maxSimTime = model.getMaxSimTime();
//                            model.faultViewerLinkedList.clear();
//                            ShortCircuitFault.faultViewerQueue.clear();
        model.simulationIsRunning = true;
        DeviceModel.destroyRegistrationList();
        Event.clear();
        // todo put compressNetNumbers to load
        model.compressNetNumbers();
//                            System.out.println("3 plus netnumber=" + powerHighViewer.firstIoViewer.netNumber);
        scaleDetector = new
                ScaleGestureDetector(activity, new ScaleListener());
        gestureListener = new GestureDetectorCompat(activity, new
                SimulationGestureListener());
        model.destroyLogicalConnections();
        model.makeLogicalConnections();
        model.reset(); // Sets undefined and puts first events to the queue.
        invalidate();
        startSimulation();
//        if (!model.simulationIsRunning) {
//            cursor.enabled = false;
//            moveSelectionEnabled = false;
////            commandButtonsHandler.buttons[1].setImageResource(R.drawable
////                    .ic_stop_black_24dp);
//            model.selections.unselect();
//            model.faultViewers.clear();
//            maxSimTime = model.getMaxSimTime();
////                            model.faultViewerLinkedList.clear();
////                            ShortCircuitFault.faultViewerQueue.clear();
//            model.simulationIsRunning = true;
//            DeviceModel.destroyRegistrationList();
//            Event.clear();
////            commandButtonsHandler.timeView.setText(Long.toString(Event.simulationTime, 10));
//            // todo put compressNetNumbers to load
//            model.compressNetNumbers();
//            scaleDetector = new
//                    ScaleGestureDetector(activity, new ScaleListener());
//            gestureListener = new GestureDetectorCompat(activity, new
//                    SimulationGestureListener());
//            model.destroyLogicalConnections();
//            model.makeLogicalConnections();
//            model.reset(); // Sets undefined and puts first events to the queue.
//            invalidate();
//            startSimulation();
//        }
    }

    @Override
    public void scopeButtonHandler() {
        if (!model.enableScope) {
            model.enableScope = true;
            savedScaleFactor = scaleFactor;
            savedXOffset = xOffset;
            savedYOfseet = yOffset;
            if (firstScopeShow) {
                firstScopeShow = false;
                xOffset = 0;
                yOffset = 0;
                scaleFactor = 1;
            } else {
                xOffset = savedScopeXOffset;
                yOffset = savedScopeYOfseet;
                scaleFactor = savedScopeScaleFactor;
            }
        }
        invalidate();
    }

    public void runFromScopeButtonHandler() {
        cursor.enabled = false;
        moveSelectionEnabled = false;
//            commandButtonsHandler.buttons[1].setImageResource(R.drawable
//                    .ic_stop_black_24dp);
        model.selections.unselect();
        model.faultViewers.clear();
        maxSimTime = model.getMaxSimTime();
//                            model.faultViewerLinkedList.clear();
//                            ShortCircuitFault.faultViewerQueue.clear();
        model.simulationIsRunning = true;
        DeviceModel.destroyRegistrationList();
        Event.clear();
//            commandButtonsHandler.timeView.setText(Long.toString(Event.simulationTime, 10));
        // todo put compressNetNumbers to load
        model.compressNetNumbers();
        scaleDetector = new
                ScaleGestureDetector(activity, new ScaleListener());
        gestureListener = new GestureDetectorCompat(activity, new
                SimulationGestureListener());
        model.destroyLogicalConnections();
        model.makeLogicalConnections();
        model.reset(); // Sets undefined and puts first events to the queue.
        invalidate();
        startSimulation();
    }

    public void restoreAfterScope() {
        model.enableScope = false;
        savedScopeScaleFactor = scaleFactor;
        savedScopeXOffset = xOffset;
        savedScopeYOfseet = yOffset;
        scaleFactor = savedScaleFactor;
        xOffset = savedXOffset;
        yOffset = savedYOfseet;
        invalidate();
    }

    public void setScopeTimeZoom(float z) {
        //todo preserve start display time
        float zz = scopeTimeZoom;
        zz *= z;
        if ((zz >= 1f / 100f) && (zz <= 100f)) {
            scopeTimeZoom = zz;
        }
    }

    public void save(String projectFileName) {
        String TAG = "breadboard".toString();
//        Log.i(TAG, "copy test files " + projectFileName);
        try {
            Log.i(TAG, projectFileName);
            FilenameViewer filenameViewer = new FilenameViewer();
            model.add(filenameViewer);
            ZoomPosViewer zoomPos = new ZoomPosViewer();
            zoomPos.zoom = scaleFactor;
            zoomPos.xOffset = xOffset;
            zoomPos.yOffset = yOffset;
            model.add(zoomPos);
            NativeSaveLoad.saveBreadboard(projectFileName, model, breadboardResourceIndex);
            model.remove(zoomPos);
            model.remove(filenameViewer);
        } catch (Exception ex) {
//            System.out.println("?????");
            ex.printStackTrace();
        }
        Log.i(TAG, projectFileName);
    }

    public void restore(String projectFileName) {
        try {
            File oldFile = new File(projectFileName);
            if (!oldFile.exists()) {
                return;
            }
            model = new EditorModel();
            NativeSaveLoad.loadBreadboardMain(projectFileName, this.model, this);
            addInterfaces();
            model.setHolesUsed(this);
            model.checkConnections();
            invalidate();
            model.makeSnapshot();
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
//            addInterfaces();
        }
    }

    private void pushButton(MotionEvent ev, int offset) {
        setCursorPosOnCanvas(ev);
        if (model.pushButton(CursorPosOnCanvas.x, CursorPosOnCanvas.y)) {
            invalidate();
//            System.out.println("queue size=" + Event.queue.size());
//            System.out.flush();
            if (Event.queue.size() == 0) {
//                System.out.println("New task started");
//                System.out.flush();
                //           Event.simulationTime++;
                Event.loadButtonEvents();
                startSimulation();
            }
        }
    }

    @Override
    public void rotateLeft() {
        model.selections.setHolesFree(BreadBoardViewer.this);
        model.selections.rotateSelectedWithoutCheck(-4);
        model.selections.setHolesUsed(BreadBoardViewer.this);
        model.makeSnapshot();
    }

    @Override
    void addInput(EditorViewer editorViewer) {
        model.selections.setHolesFree(BreadBoardViewer.this);
        model.selections.addSwPort(model, editorViewer);
        model.selections.setHolesUsed(BreadBoardViewer.this);
        invalidate();
        model.makeSnapshot();
    }

    @Override
    void removeInput(EditorViewer editorViewer) {
        model.selections.setHolesFree(BreadBoardViewer.this);
        model.selections.removeSwPort(model, editorViewer);
        model.selections.setHolesUsed(BreadBoardViewer.this);
        invalidate();
        model.makeSnapshot();
    }


    @Override
    public void fineRotateLeft() {
        model.selections.setHolesFree(BreadBoardViewer.this);
        model.selections.rotateSelectedWithoutCheck(-2);
        model.selections.setHolesUsed(BreadBoardViewer.this);
        model.makeSnapshot();
    }

    @Override
    public void undo() {
        clearHoles();
        model.undo(this);
    }

    @Override
    public void redo() {
        clearHoles();
        model.redo(this);
    }


    public void rPlusLed() {
        BBWireViewer wire = (BBWireViewer) model.selections.getLastSelected();
        wire.delete(BreadBoardViewer.this);
        BBResistorLedViewer resistor = new BBResistorLedViewer(wire.endX, wire.endY, wire.startX, wire.startY);
        resistor.setDefaultNumberOfIos();
        model.selections.setHolesFree(BreadBoardViewer.this);
        model.selections.deleteSelected(model, this);
        model.checkConnections();
        model.add(resistor);
        resistor.setHolesUsed(BreadBoardViewer.this);
        model.checkConnections();
        model.makeSnapshot();
        invalidate();
    }

    public void led() {
        BBWireViewer wire = (BBWireViewer) model.selections.getLastSelected();
        wire.delete(BreadBoardViewer.this);
//                        BBWireViewer resistor = (BBWireViewer) model.selections.getLastSelected();
        BBLedViewer led = new BBLedViewer(wire.endX, wire.endY, wire.startX, wire.startY);
        led.setDefaultNumberOfIos();
        model.selections.setHolesFree(BreadBoardViewer.this);
        model.selections.deleteSelected(model, this);
        model.checkConnections();
        model.add(led);
        led.setHolesUsed(BreadBoardViewer.this);
        model.checkConnections();
        model.makeSnapshot();
        invalidate();
    }

    public void resistor() {
        BBWireViewer wire = (BBWireViewer) model.selections.getLastSelected();
        wire.delete(BreadBoardViewer.this);
        BBResistorViewer resistor = new BBResistorViewer(wire.startX, wire.startY, wire.endX, wire.endY);
        resistor.setDefaultNumberOfIos();
        model.selections.setHolesFree(BreadBoardViewer.this);
        model.selections.deleteSelected(model, this);
        model.checkConnections();
        model.add(resistor);
        resistor.setHolesUsed(BreadBoardViewer.this);
        model.checkConnections();
        model.makeSnapshot();
        invalidate();
    }

    public void lToH() {
        model.selections.setLToH();
    }

    public void hToL() {
        model.selections.setHToL();
    }

    public void disable() {
        model.selections.setDisabled();
    }

    @Override
    public void deleteAction() {
        model.selections.setHolesFree(this);
        model.selections.deleteSelected(model, this);
        model.checkConnections();
        model.makeSnapshot();
    }

    @Override
    public void copyAction() {
        Viewer v = model.selections.getLastSelected();
        Viewer cloned = ViewerFactory.factory.newViewer(v.getGroup(), v.getSymbol());
//            CursorPosOnCanvas.x = Math.round(xOrigo - xOffset);
//            CursorPosOnCanvas.y = Math.round(yOrigo - yOffset);
        CursorPosOnCanvas.x = Math.round(BreadBoardViewer.xOrigo - BreadBoardViewer.staticXOffset);
        CursorPosOnCanvas.y = Math.round(BreadBoardViewer.yOrigo - BreadBoardViewer.staticYOffset);
        cursor.setNoCheck(CursorPosOnCanvas.x, CursorPosOnCanvas.y);
//                        copySingle(model.selections.getLastSelected());
        cloned.clone(cursor.getX(), cursor.getY(), v);
        cloned.initPosition(cursor.getX(), cursor.getY(), 0, BreadBoardViewer.rasterSize);
        cloned.setHolesUsed(BreadBoardViewer.this);
        model.selections.unselect();
        model.add(cloned);
        model.forcedSelect(cloned);
    }

    protected class BreadBoardGestureListener
            extends GestureDetector.SimpleOnGestureListener {

        int delay = 1;

        @Override
        public boolean onDown(MotionEvent ev) {
            setCursorPosOnCanvas(ev);
            Viewer v = model.selections.findInSelectedList(model, CursorPosOnCanvas.x,
                    CursorPosOnCanvas.y);
            if (v != null) {
                Log.i("onDown", "v!=null");
                moveSelectionEnabled = true;
                firstMove = true;
//                model.selections.setHolesFree(BreadBoardViewer.this);
            } else {
                Log.i("onDown", "v==null");
                moveSelectionEnabled = false;
            }
            boolean res = true;
            return res;
        }

        @Override
        public void onShowPress(MotionEvent motionEvent) {
        }

        public boolean onSingleTapConfirmed(MotionEvent ev) {
            boolean processed = false;
            setCursorPosOnCanvas(ev);
            if (processed) {
                invalidate();
                return true;
            }
            select(ev, 0);
            invalidate();
            return true;
        }

        @Override
        public boolean onSingleTapUp(MotionEvent ev) {
            if (model.selections.movingSelection) {
//                if (model.selections.lockedSelection && !model.selections
//                        .lockedSelectionDisabled) { //try reroute
//                    tryReroute();
//                }
                clearMovingSelection();
                model.makeSnapshot();
                activity.setContextMenuVisibility();
                invalidate();
            } else {
            }
            return true;
        }

        @Override
        public void onLongPress(MotionEvent ev) {
//            fastSelectionDisabled = select(ev, Settings.CURSOR__OFFSET_10);
//            if (!fastSelectionDisabled) { // fast selection mode can be started only on free area
//                xDown = ev.getX();
//                yDown = ev.getY();
//                moveCursorEnabled = true;
//                model.selections.fastSelection = true;
//                cursor.redCursor(model.router);
//            }
        }

        public boolean onDoubleTapEvent(MotionEvent ev) {
            return true;
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            if (model.enableScope) {
                scrollScope(distanceX, distanceY);
            } else if (moveSelectionEnabled) {
                moveXOffset -= distanceX / scaleFactor;
                moveYOffset -= distanceY / scaleFactor;
                final int rs = 2 * rasterSize;
                int ix = (int) moveXOffset / rs;
                float fx = moveXOffset - ix * rs;
                if (ix != 0) {
                    moveXOffset = fx;
                }
                int iy = (int) moveYOffset / rs;
                float fy = moveYOffset - iy * rs;
                if (iy != 0) {
                    moveYOffset = fy;
                }
                if (ix != 0 || iy != 0) {
                    if (!model.selections.isMovable()) {
                        return true;
                    }
                    float fromX = e1.getX();
                    float fromY = e1.getY();
                    fromX = (fromX - xOrigo) / scaleFactor + xOrigo - xOffset;
                    fromY = (fromY - yOrigo) / scaleFactor + yOrigo - yOffset;
                    if (firstMove) {
                        model.selections.setHolesFree(BreadBoardViewer.this);
                        firstMove = false;
                    }
                    model.selections.moveWithoutCheck(BreadBoardViewer.this, fromX, fromY, ix * rs, iy * rs);
//                    model.selections.move(BreadBoardViewer.this, fromX, fromY, ix, iy);
//                    model.selections.setHolesUsed(BreadBoardViewer.this);
//
//                    if (!model.selections.lockedSelection || model.selections
//                            .lockedSelectionDisabled) { //try reconnect
//                        model.selections.reconnect(model);
//                    }
                    invalidate();
                }
            } else {
                float newXOffset = xOffset - distanceX / scaleFactor;
                float newYOffset = yOffset - distanceY / scaleFactor;
                int x0 = -Math.round(((0 - xOrigo) / scaleFactor + xOrigo - newXOffset) * scaleFactor);
                int y0 = -Math.round(((0 - yOrigo) / scaleFactor + yOrigo - newYOffset) * scaleFactor);
                int xMax = Math.round(x0 + maxX * scaleFactor);
                int yMax = Math.round(y0 + maxY * scaleFactor);
//                Log.i("onScroll", "x0=" + x0 + " xMax=" + xMax);
//                Log.i("onScroll", "y0=" + y0 + " yMax=" + yMax + " canvasHeight=" + canvasHeight);
                if (x0 <= canvasWidth - scrollMargin && xMax >= scrollMargin) { // screen is inside the board
                    xOffset = newXOffset;
                }
                if (y0 <= canvasHeight - scrollMargin && yMax >= scrollMargin) {
                    yOffset = newYOffset;
                }
                invalidate();
            }
            return true;
        }

        @Override
        public boolean onFling(MotionEvent event1, MotionEvent event2,
                               float velocityX, float velocityY) {
            return false;
        }
    }


    private boolean selectHole(MotionEvent ev) {
        float x = (ev.getX() - xOrigo) / scaleFactor + xOrigo - xOffset;
        float y = (ev.getY() - yOrigo) / scaleFactor + yOrigo - yOffset;
//        Log.i("XY", x + " " + y);
        x = (x) / rasterSize - 1;
        y = (y) / rasterSize - 1;
        int ix = Math.round(x);
        int iy = Math.round(y);
//        Log.i("XY", x + " " + y);
//        Log.i("XY", ix + " " + iy);
        // x =0, 1
        if (selectHoleAt(ix, iy)) {
            return true;
        }
        if (x < ix) {
            if (selectHoleAt(ix - 1, iy)) {
                return true;
            }
            if (y < iy) {
                if (selectHoleAt(ix - 1, iy - 1)) {
                    return true;
                }
            }
            if (y > iy) {
                if (selectHoleAt(ix - 1, iy + 1)) {
                    return true;
                }
            }
        }
        if (x > ix) {
            if (selectHoleAt(ix + 1, iy)) {
                return true;
            }
            if (y < iy) {
                if (selectHoleAt(ix + 1, iy - 1)) {
                    return true;
                }
            }
            if (y > iy) {
                if (selectHoleAt(ix + 1, iy + 1)) {
                    return true;
                }
            }
        }
        if (y < iy) {
            if (selectHoleAt(ix, iy - 1)) {
                return true;
            }
        }
        if (y > iy) {
            if (selectHoleAt(ix, iy + 1)) {
                return true;
            }
        }
        return false;
    }

//    @Override
//    public boolean setHoleUsed(int x, int y) {
//        x = (x) / rasterSize - 1;
//        y = (y) / rasterSize - 1;
////        int ix = Math.round(x);
////        int iy = Math.round(y);
//        if (x >= 0 && y >= 0 && x < board.length && y < board[x].length) {
//            if (board[x][y] != null) {
//                return board[x][y].setUsed();
//            }
//        }
//        return false;
//    }

    @Override
    public boolean setHoleUsed(int x, int y, DeviceIOViewer iov) {
        x = (x) / rasterSize - 1;
        y = (y) / rasterSize - 1;
//        int ix = Math.round(x);
//        int iy = Math.round(y);
        if (x >= 0 && y >= 0 && x < board.length && y < board[x].length) {
            RasterPoint rp = board[x][y];
            if (rp != null) {
                return rp.setUsed(iov);
            }
        }
        return false;
    }

    public boolean setHoleUsed(int x, int y, Viewer wire) {
        x = (x) / rasterSize - 1;
        y = (y) / rasterSize - 1;
//        int ix = Math.round(x);
//        int iy = Math.round(y);
        if (x >= 0 && y >= 0 && x < board.length && y < board[x].length) {
            RasterPoint rp = board[x][y];
            if (rp != null) {
                return rp.setUsed(wire);
            }
        }
        return false;
    }

    public void setHoleFree(int x, int y) {
        x = (x) / rasterSize - 1;
        y = (y) / rasterSize - 1;
//        int ix = Math.round(x);
//        int iy = Math.round(y);
        if (x >= 0 && y >= 0 && x < board.length && y < board[x].length) {
            if (board[x][y] != null) {
                board[x][y].setFree();
            }
        }
    }

    private boolean selectHoleAt(int ix, int iy) {
        if (ix >= 0 && iy >= 0 && ix < board.length && iy < board[ix].length) {
            if (board[ix][iy] != null) {
                return board[ix][iy].select();
            }
        }
        return false;
    }

    private class ScaleListener
            extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            float nf = detector.getScaleFactor();
            scaleFactor *= nf;
            scaleFactor = (float) Math.max(MIN_ZOOM, Math.min(scaleFactor, MAX_ZOOM));
            Android511workaround();
            setOffsetsToScale();
            invalidate();
            return true;
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        int size = ev.getPointerCount();
        int maskedAction = ev.getActionMasked();
        if (size > 1) {
            if (size == 2) {
                if (scaleDetector.onTouchEvent(ev) == true) {
//                System.out.println("not passed  ");
                    return true;
                }
            }
        } else {
            if (gestureListener.onTouchEvent(ev) == true) {
//                System.out.println("not passed  ");
                return true;
            }
        }
//        System.out.println("+++++++++++passed  " + (cursor.left - 1) + "," + (cursor.right + 1) + "   ");
//        System.out.println(cursor.top - 1 + "," + cursor.bottom + 1);
        Log.i("onTouchEvent", "maskedAction=" + maskedAction);
        switch (maskedAction)

        {
            case MotionEvent.ACTION_MOVE:
                Log.i("onTouchEvent", "ACTION_MOVE");
                if (moveCursorEnabled) {
                    Log.i("onTouchEvent", "moveCursorEnabled");
                    setCursorPosOnCanvas(ev);
                    setCursor2(ev, Settings.CURSOR__OFFSET_10);
                }
                break;
            case MotionEvent.ACTION_CANCEL:
                Log.i("onTouchEvent", "ACTION_CANCEL");
                model.selections.setHolesUsed(BreadBoardViewer.this);
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_POINTER_UP:
                Log.i("onTouchEvent", "ACTION_UP ACTION_POINTER_UP");
                if (!model.simulationIsRunning) {
                    if (model.selections.movingSelection) {
                        Log.i("onTouchEvent", "movingSelection");
//                        if (model.selections.lockedSelection && !model.selections
//                                .lockedSelectionDisabled) {
//                            //try reroute
//                            String ret = model.rerouteMoved();
//                            if (ret != null) {
//                                Toast.makeText(context, ret, Toast.LENGTH_LONG).show();
//                            }
//                        }
//                        model.selections.setHolesUsed(BreadBoardViewer.this);
                        model.makeSnapshot();
                        clearMovingSelection();
                    }
//                System.out.println("ACTION_UP");
//                if (moved) {
//                    model.reconnect();
//                }
//                moved = false;
                    moveCursorEnabled = false;
//                    clearMovingSelection();
                    moveXOffset = 0;
                    moveYOffset = 0;
                    cursor.normalCursor();
                    ((MainActivity) activity).setContextMenuVisibility();
                    invalidate();
                } else {
//                System.out.println("ACTION_UP");
                    setCursorPosOnCanvas(ev);
                    if (model.releaseButton(CursorPosOnCanvas.x, CursorPosOnCanvas.y)) {
                        invalidate();
                        if (Event.queue.size() == 0) {
                            Event.loadButtonEvents();
                            startSimulation();
                        }
                    }
                }
                break;
            default:
        }

        return true;
    }

    private class SimulationTask extends AsyncTask<EditorModel, Integer, String> {
        ProgressDialog pd;

        // Runs in UI before background thread is called
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Do something like display a progress bar
        }

        // This is run in a background thread
        @Override
        protected String doInBackground(EditorModel... params) {
            Event event = Event.queue.poll();
            if (event == null) {
                DeviceModel.handleRegistratedModels();
                event = Event.queue.poll();
            }
            while (event != null && Event.simulationTime < maxSimTime) {
//                    System.out.println("?????:" + event.level + " " + event
//                            .time + " " + Event.simulationTime);
                event.deviceOutputModel.eventHandler(event);
                if (Event.displayEvent) {
                    publishProgress(Event.simulationTime);
                    Event.displayEvent = false;
                }
                event = Event.queue.peek();
                if (event == null || (event.time > Event.simulationTime)) {
                    DeviceModel.handleRegistratedModels();
                }
                event = Event.queue.poll();
            }
            Event.simulationTime++;
            publishProgress(Event.simulationTime);
            return "this string is passed to onPostExecute";
        }

        // This is called from background thread but runs in UI
        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            //todo display simulation time
            //todo run stop step
            if (values[0] < maxSimTime) {
                timeView.setText(Long.toString(values[0], 10));
            } else {
                timeView.setText("End:" + Long.toString(values[0], 10));
            }
//            Iterator<FaultViewer> iterator = ShortCircuitFault.faultViewerQueue.iterator();
//            while ((iterator.hasNext())) {
//                FaultViewer fv = iterator.next();
//                model.append(fv);
//            }
            model.setFaultPos();
            model.displayWireLevel();
            invalidate();
            // Do things like update the progress bar
        }

        // This runs in UI when background thread finishes
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
//            pd.dismiss();
            // Do things like hide the progress bar or change a TextView
//            commandButtonsHandler.buttons[1].setImageResource(R.drawable
//                    .ic_play_arrow_black_24dp);
//            Log.i("onPostExecute", "queue size=" + Event.changedNetsAndLevels.size());
            invalidate();
        }
    }

    protected void setOffsetsToScale() {
//        int x0 = Math.round((0 - xOrigo) / scaleFactor + xOrigo - xOffset);
//        int y0 = Math.round((0 - yOrigo) / scaleFactor + yOrigo - yOffset);
//        int xMax = Math.round((canvasWidth - xOrigo) / scaleFactor + xOrigo - xOffset);
//        int yMax = Math.round((canvasHeight - yOrigo) / scaleFactor + yOrigo - yOffset);
//        if (!(xMax >= 10)) {
//            xOffset = (canvasWidth - xOrigo) / scaleFactor + xOrigo - 10;
//        }
//        if (!(x0 <= EditorModel.MAX_X - 10)) {
//            xOffset = (-xOrigo) / scaleFactor + xOrigo - (EditorModel.MAX_X - 10);
//        }
//        if (!(yMax >= 10)) {
//            yOffset = (canvasHeight - yOrigo) / scaleFactor + yOrigo - 10;
//        }
//        if (!(y0 <= EditorModel.MAX_Y - 10)) {
//            yOffset = (-yOrigo) / scaleFactor + yOrigo - (EditorModel.MAX_Y - 10);
//        }
    }


    private class SimulationGestureListener
            extends GestureDetector.SimpleOnGestureListener {

        @Override
        public boolean onDown(MotionEvent ev) {
//            setCursorPosOnCanvas(ev);
//            System.out.println("onDown");
            pushButton(ev, 0);
//            Viewer v = model.selections.findInSelectedList(model, CursorPosOnCanvas.x,
//                    CursorPosOnCanvas.y);
//            if (v != null) {
//                moveSelectionEnabled = true;
//            } else {
//                moveSelectionEnabled = false;
//            }
            boolean res = true;
//            System.out.println("onDown");
            return res;
        }

        @Override
        public void onShowPress(MotionEvent motionEvent) {
//            System.out.println("onShowPress");
        }

        public boolean onSingleTapConfirmed(MotionEvent ev) {
//            System.out.println("onSingleTapConfirmed");
//            pushButton(ev, 0);
            return true;
        }

        @Override
        public boolean onSingleTapUp(MotionEvent ev) {
//            System.out.println("onSingleTapUp");
            setCursorPosOnCanvas(ev);
            if (model.releaseButton(CursorPosOnCanvas.x, CursorPosOnCanvas.y)) {
                invalidate();
                if (Event.queue.size() == 0) {
                    Event.loadButtonEvents();
                    startSimulation();
                }
            }
            return true;
        }

        @Override
        public void onLongPress(MotionEvent ev) {
////            System.out.println("+++++++++++long press");
//            xDown = ev.getX();
//            yDown = ev.getY();
//            moveCursorEnabled = true;
//            cursor.redCursor(model.router);
        }

        public boolean onDoubleTapEvent(MotionEvent ev) {
//            System.out.println("+++++++++++onDoubleTapEvent");
            return true;
        }

//        private void moveSelected(float dx, float dy) {
//            if (moveSelectionEnabled) {
//                model.selections.move(dx, dy);
////                if (lastSelection != null) {
//                invalidate();
//            }
//        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            if (model.enableScope) {
                scrollScope(distanceX, distanceY);
            } else if (moveSelectionEnabled) {
                moveXOffset -= distanceX / scaleFactor;
                moveYOffset -= distanceY / scaleFactor;
                int ix = ((int) moveXOffset);
                float fx = moveXOffset - ix % 16;
                if (ix / 16 != 0) {
                    moveXOffset = fx;
                }
                int iy = ((int) moveYOffset);
                float fy = moveYOffset - iy % 16;
                if (iy / 16 != 0) {
                    moveYOffset = fy;
                }
                float fromX = e1.getX();
                float fromY = e1.getY();
                fromX = (fromX - xOrigo) / scaleFactor + xOrigo - xOffset;
                fromY = (fromY - yOrigo) / scaleFactor + yOrigo - yOffset;
                if ((ix + Math.round(fromX)) / 16 != 0 || (iy + Math.round(fromY)) / 16 != 0) {
//                    model.selections.setHolesFree(BreadBoardViewer.this);
                    model.selections.move(BreadBoardViewer.this, fromX, fromY, ix, iy);
//                    model.selections.setHolesUsed(BreadBoardViewer.this);
//                    if (!model.selections.lockedSelection || model.selections
//                            .lockedSelectionDisabled) { //try reconnect
//                        model.selections.reconnect(model);
//                    }
                    invalidate();
                }
            } else {
                float newXOffset = xOffset - distanceX / scaleFactor;
                float newYOffset = yOffset - distanceY / scaleFactor;
                int x0 = -Math.round(((0 - xOrigo) / scaleFactor + xOrigo - newXOffset) * scaleFactor);
                int y0 = -Math.round(((0 - yOrigo) / scaleFactor + yOrigo - newYOffset) * scaleFactor);
                int xMax = Math.round(x0 + maxX * scaleFactor);
                int yMax = Math.round(y0 + maxY * scaleFactor);
//                Log.i("onScroll", "x0=" + x0 + " xMax=" + xMax);
//                Log.i("onScroll", "y0=" + y0 + " yMax=" + yMax + " canvasHeight=" + canvasHeight);
                if (x0 <= canvasWidth - scrollMargin && xMax >= scrollMargin) { // screen is inside the board
                    xOffset = newXOffset;
                }
                if (y0 <= canvasHeight - scrollMargin && yMax >= scrollMargin) {
                    yOffset = newYOffset;
                }
                invalidate();
            }
            return true;
        }

        @Override
        public boolean onFling(MotionEvent event1, MotionEvent event2,
                               float velocityX, float velocityY) {
//            System.out.println("onFling");
            return true;
        }

    }

    public void clearMovingSelection() {
        Log.i("clearMovingSelection", "enter");
        if (model.selections.movingSelection) {
            Log.i("clearMovingSelection", "movingSelection");
            model.selections.endMove(model);
            model.selections.setHolesUsed(BreadBoardViewer.this);
            invalidate();
        } else {
            Log.i("clearMovingSelection", "else");
            model.selections.setHolesUsed(BreadBoardViewer.this);
            invalidate();
        }
    }

    protected void setCursorPosOnCanvas(MotionEvent ev) {
//        cursor.enabled = false;
        //       invalidate((int) cursor.left - 1, (int) cursor.top - 1, (int) cursor.right + 1, (int) cursor.bottom + 1);
        px = ev.getX();
        py = ev.getY();
        CursorPosOnCanvas.x = Math.round((px - xOrigo) / scaleFactor + xOrigo - xOffset);
        CursorPosOnCanvas.y = Math.round((py - yOrigo) / scaleFactor + yOrigo - yOffset);
//        CursorPosOnCanvas.x = (px) / scaleFactor - xOffset;
//        CursorPosOnCanvas.y = (py) / scaleFactor - yOffset;
    }

    protected void setCursor2(MotionEvent ev, int offset) {
        CursorPosOnCanvas.y = Math.round(CursorPosOnCanvas.y - offset * densityDpi /
                (10 * scaleFactor));
        cursor.set(CursorPosOnCanvas.x, CursorPosOnCanvas.y);
        cursor.enabled = true;
        if (model.selections.fastSelection) {
            model.selectFast(CursorPosOnCanvas.x, CursorPosOnCanvas.y);
        }
        invalidate();
    }

    private boolean select(MotionEvent ev, int offset) {
        setCursorPosOnCanvas(ev);
        if (!model.select(CursorPosOnCanvas.x, CursorPosOnCanvas.y)) {
            if (!selectHole(ev)) {
                setCursor2(ev, offset);
                ((MainActivity) activity).setContextMenuVisibility();
                invalidate();
                return false;
            }
            ((MainActivity) activity).setContextMenuVisibility();
            invalidate();
            return true;

        } else {
            moveSelectionEnabled = true;
            ((MainActivity) activity).setContextMenuVisibility();
            invalidate();
//            model.selections.setHolesUsed(BreadBoardViewer.this);
            return true;
        }
    }
}
