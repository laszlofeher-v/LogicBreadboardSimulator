package com.logicbreadboard.simulator;

import android.graphics.Canvas;
import android.graphics.Paint;

import java.io.DataInputStream;
import java.io.DataOutputStream;

/**
 * Created on 6/10/15.
 */

public class WireViewer extends Viewer {
    static final int DOT_RADIUS = 1;
    static private final int CONTEXT_MENU_MASK = ContextMenuIcons.COPY |
            ContextMenuIcons.DELETE |
            ContextMenuIcons.ROUTE |
//            ContextMenuIcons.LOCK | ContextMenuIcons.UNLOCK | must not enable java.util.ConcurrentModificationException
            ContextMenuIcons.UNSELECT;
    static private final int NO_SEGMENT = -1024;
    static private final int SELECTED_ENDPOINT_X = -16;
    static private final int SELECTED_ENDPOINT_Y = -32;
    private static final int SELECTED_STARTPOINT_X = -64;
    private static final int SELECTED_STARTPOINT_Y = -127;
    static private int selectedSegment = NO_SEGMENT;
    static private float yMoveMaxDiff = 0;
    static private float yMoveDiff = 0;
    WirePoint startWirePoint;
    WirePoint endWirePoint;
    boolean connectedStart = true;
    boolean connectedEnd = true;
    //    WireIoViewer startConnection;
//    WireIoViewer endConnection;
    int netNumber;
    private short cutX0;
    private short cutY0;
    private short cutX1;
    private short cutY1;
    private boolean firstMove = true;
    private int level;

    WireViewer() {
        super();
        netNumber = NetNumbers.get();
        firstMove = true;
    }

    public void endMove() {
        selectedSegment = NO_SEGMENT;
        yMoveMaxDiff = 0;
        yMoveDiff = 0;
        firstMove = true;
    }

    public void unselect() {
        endMove();
    }

    public void reset() {
        level = State.Level.undefined.ordinal();
    }

    @Override
    public int getContextMenuMask() {
        return CONTEXT_MENU_MASK;
    }

    @Override
    public short getGroup() {
        return (short) SchematicViewerFactory.DeviceGroup.Wires.ordinal();
    }

    @Override
    public short getSymbol() {
        return (short) SchematicViewerFactory.Wires.Wire.ordinal();
    }

    public int getNetNumber() {
        return netNumber;
    }

    @Override
    public void setNetNumber(int netNumber) {
        this.netNumber = netNumber;
    }

    @Override
    public void routeNetNumber(Router router) {
        if (netNumber == router.targetNet) {
            netNumber = router.startNet;
        }
    }

    @Override
    public void putRestrictedMask(Router router, Viewer vStart, Viewer vEnd) {
        if (netNumber != router.startNet && netNumber != router.targetNet || (
                this == vStart || this == vEnd)) {
            putMask(router);
        }
    }

    @Override
    public void putRestrictedStartAndTarget(Router router, Viewer vStart, Viewer vEnd) {
        if (netNumber != router.startNet && netNumber != router.targetNet || (
                this == vStart || this == vEnd)) {
            putStartAndTarget(router);
        }
    }

    public boolean isRoutable() {
        return true;
    }

    //    @Override
//    public boolean hasSelectedIO() { // asked if and only if when wire is in selection list. if the
//        // wire is selected then it acts as a selected DeviceIO
//        return false;
//    }
    @Override
    public boolean startsOrEndsAt(int x, int y) {
        if (startWirePoint.x == x && startWirePoint.y == y) {
            return true;
        } else if (endWirePoint.x == x && endWirePoint.y == y) {
            return true;
        }
        return false;
    }

    private WirePoint reverse(WirePoint p) {
        if (p != endWirePoint) {
            WirePoint q = reverse(p.next);
            q.next = p;
            endWirePoint = p;
            p.next = null; //???
        } else {
            startWirePoint = p;
        }
        return p;
    }

    private WireViewer joinedStartStart(WireViewer v) {
        if (startWirePoint.x == v.startWirePoint.x &&
                startWirePoint.y == v.startWirePoint.y) {
            v.reverse(v.startWirePoint);
            return v.joinedEndStart(this);
        } else {
            return null;
        }
    }

    private WireViewer joinedStartEnd(WireViewer v) {
        if (startWirePoint.x == v.endWirePoint.x &&
                startWirePoint.y == v.endWirePoint.y) {
            v.endWirePoint.next = startWirePoint.next;
            v.endWirePoint = endWirePoint;
            return this;
        } else {
            return null;
        }
    }

    private WireViewer joinedEndStart(WireViewer v) {
        if (endWirePoint.x == v.startWirePoint.x &&
                endWirePoint.y == v.startWirePoint.y) {
            endWirePoint.next = v.startWirePoint.next;
            endWirePoint = v.endWirePoint;
            return v;
        } else {
            return null;
        }
    }

    private WireViewer joinedEndEnd(WireViewer v) {
        if (endWirePoint.x == v.endWirePoint.x &&
                endWirePoint.y == v.endWirePoint.y) {
            reverse(startWirePoint);
            return joinedStartEnd(v);
        } else {
            return null;
        }
    }

    private void removeInlinePoints(WireViewer v) {
        WirePoint pStart = startWirePoint;
        WirePoint pMiddle = startWirePoint.next;
        if (pMiddle == null) {
            return;
        }
        WirePoint pEnd = pMiddle.next;
        float epsilon[] = {1};
        while (pEnd != null) {
            if (isOnLine(pStart, pEnd, pMiddle.x, pMiddle.y, epsilon)) {
                pStart.next = pEnd;
                pStart = pEnd;
                pMiddle = pEnd.next;
                if (pMiddle == null) {
                    return;
                }
                pEnd = pMiddle.next;
            } else {
                pStart = pMiddle;
                pMiddle = pEnd;
                pEnd = pEnd.next;
            }
        }
    }

    public WireViewer join(WireViewer v) {
//        System.out.println("-------------------joinedStartStart");
        WireViewer w = joinedStartStart(v);
        if (w != null) {
//            System.out.println("joinedStartStart"); //ok
            removeInlinePoints(w);
            return w;
        }
//        System.out.println("-------------------joinedStartEnd");
        w = joinedStartEnd(v);
        if (w != null) {
//            System.out.println("joinedStartEnd");
            removeInlinePoints(w);
            return w;
        }
//        System.out.println("-------------------joinedEndEnd"); //nok
        w = joinedEndEnd(v);
        if (w != null) {
//            System.out.println("joinedEndEnd");
            removeInlinePoints(w);
            return w;
        }
//        System.out.println("-------------------joinedEndStart");
        w = joinedEndStart(v);
        if (w != null) {
//            System.out.println("joinedEndStart");// ok
            removeInlinePoints(w);
            return w;
        }
//        System.out.println("Could not join");
        return null;
    }

    void addPoint(WirePoint point) {
        if (startWirePoint == null) {
            startWirePoint = point;
            endWirePoint = startWirePoint;
        } else {
            if (point.x != endWirePoint.x || point.y != endWirePoint.y) {
                endWirePoint.next = point;
                endWirePoint = endWirePoint.next;
            }
        }
    }

    void addPoint(short x, short y) {
        if (startWirePoint == null) {
            startWirePoint = new WirePoint(x, y);
            endWirePoint = startWirePoint;
        } else {
            if (x != endWirePoint.x || y != endWirePoint.y) {
                endWirePoint.next = new WirePoint(x, y);
                endWirePoint = endWirePoint.next;
            }
        }
    }

    @Override
    void save(DataOutputStream outputStream) throws java.io.IOException {
        outputStream.writeShort(getGroup());
        outputStream.writeShort(getSymbol());
        WirePoint p = startWirePoint;
        short count = 0;
        while (p != null) {
            count++;
            p = p.next;
        }
        p = startWirePoint;
        outputStream.writeInt(netNumber);
        outputStream.writeShort(count);
        while (p != null) {
            outputStream.writeInt(p.x);
            outputStream.writeInt(p.y);
            p = p.next;
        }
    }

    @Override
    public void drawEnlarged(Canvas canvas, float zoom) {
        draw(canvas);
    }

    @Override
    void load(DataInputStream inputStream) throws java.io.IOException {
        netNumber = inputStream.readInt();
        short count = inputStream.readShort();
//        if (count == 1) {
//            System.out.println("--------------------------------------------Load bad wire");
//            System.out.flush();
//        }
        while (count > 0) {
            int x = inputStream.readInt();
            int y = inputStream.readInt();
            addPoint((short) x, (short) y);
            count--;
        }
    }

    @Override
    public void putMask(Router router) {
        WirePoint p = null;
        WirePoint prev = startWirePoint;
        if (startWirePoint != null) {
            p = startWirePoint.next;
        }
        if (netNumber == router.startNet) {
//            while (p != null) {
//                router.addLineStart(prev.x, prev.y, p.x, p.y);
//                prev = p;
//                p = p.next;
//            }
        } else if (netNumber == router.targetNet) {
//            while (p != null) {
//                router.addLineTarget(prev.x, prev.y, p.x, p.y);
//                prev = p;
//                p = p.next;
//            }
        } else {
            while (p != null) {
                router.addLineMask(prev.x, prev.y, p.x, p.y);
                prev = p;
                p = p.next;
            }
            p = startWirePoint;
            while (p != null) {
                router.addPointMask(p.x, p.y, 1);
                p = p.next;
            }
        }
    }

    @Override
    public void putStartAndTarget(Router router) {
        WirePoint p = null;
        WirePoint prev = startWirePoint;
        if (startWirePoint != null) {
            p = startWirePoint.next;
        }
        if (netNumber == router.startNet) {
            while (p != null) {
                router.addLineStart(prev.x, prev.y, p.x, p.y);
                prev = p;
                p = p.next;
            }
        } else if (netNumber == router.targetNet) {
            while (p != null) {
                router.addLineTarget(prev.x, prev.y, p.x, p.y);
                prev = p;
                p = p.next;
            }
//        } else {
//            while (p != null) {
//                router.addLineMask(prev.x, prev.y, p.x, p.y);
//                prev = p;
//                p = p.next;
//            }
//            p = startWirePoint;
//            while (p != null) {
//                router.addPointMask(p.x, p.y, 1);
//                p = p.next;
//            }
        }
    }

    boolean isOnLine(WirePoint p, WirePoint q, float x, float y, float[] epsilonVar) {
        float epsilon = epsilonVar[0];
        float d;
        float x0 = Math.min(p.x, q.x);
        float x1 = Math.max(p.x, q.x);
        float y0 = Math.min(p.y, q.y);
        float y1 = Math.max(p.y, q.y);
        if (x < x0 - epsilon || x > x1 + epsilon || y < y0 - epsilon || y > y1 + epsilon) {
//            System.out.println("isOnLine -1:" + p.x + "," + p.y + "  " + x + "," + y + "   " + p.next.x + "," + p.next.y);
            return false;
        }
        d = Math.abs(y0 - y);
        if (y0 == y1 && d <= epsilon) {
//            System.out.println("isOnLine +1:" + p.x + "," + p.y + "  " + x + "," + y + "   " + p.next.x + "," + p.next.y);
            epsilonVar[0] = d;
            return true;
        }
        d = Math.abs(x0 - x);
        if (x0 == x1 && d <= epsilon) {
//            System.out.println("isOnLine +2:" + p.x + "," + p.y + "  " + x + "," + y + "   " + p.next.x + "," + p.next.y);
            epsilonVar[0] = d;
            return true;
        }
        if (p.x < q.x) {
            if (p.y < q.y) {
                d = Math.abs((x - p.x) - (y - p.y));
                if (d <= epsilon) {
//                    System.out.println("isOnLine +3:" + p.x + "," + p.y + "  " + x + "," + y + "   " + p.next.x + "," + p.next.y);
                    epsilonVar[0] = d;
                    return true;
                }
            } else {
                d = Math.abs((x - q.x) - (-y + q.y));
                if (d <= epsilon) {
//                    System.out.println("isOnLine +4:" + p.x + "," + p.y + "  " + x + "," + y + "   " + p.next.x + "," + p.next.y);
                    epsilonVar[0] = d;
                    return true;
                }
            }
        } else {
            if (p.y < q.y) {
                d = Math.abs((x - p.x) - (-y + p.y));
                if (d <= epsilon) {
//                    System.out.println("isOnLine +5:" + p.x + "," + p.y + "  " + x + "," + y + "   " + p.next.x + "," + p.next.y);
                    epsilonVar[0] = d;
                    return true;
                }
            } else {
                d = Math.abs((x - q.x) - (y - q.y));
                if (d <= epsilon) {
//                    System.out.println("isOnLine +6:" + p.x + "," + p.y + "  " + x + "," + y + "   " + p.next.x + "," + p.next.y);
                    epsilonVar[0] = d;
                    return true;
                }
            }
        }
//        System.out.println("isOnLine -8:" + p.x + "," + p.y + "  " + x + "," + y + "   " + p.next.x + "," + p.next.y);
        return false;
    }

    @Override
    public void cutNet(EditorModel model) {
        //removes a wire. gets new netnumbers at its ends
        //   int savedNetNumber = netNumber;
        int startNetNumber = NetNumbers.get();
        int endNetNumber = NetNumbers.get();
//        System.out.println("cutNet  startNetNumber=" + startNetNumber + " endNetNumber" + startNetNumber);
        model.setNetNumberAt(startNetNumber, startWirePoint.x, startWirePoint.y);
        model.setNetNumberAt(endNetNumber, endWirePoint.x, endWirePoint.y);
        //   NetNumbers.drop(savedNetNumber);
        model.joinAt(startNetNumber, startWirePoint.x, startWirePoint.y);
        model.joinAt(endNetNumber, endWirePoint.x, endWirePoint.y);
    }

    @Override
    public Viewer split(WirePoint p) {
        if ((p.x == startWirePoint.x && p.y == startWirePoint.y) ||
                (p.x == endWirePoint.x && p.y == endWirePoint.y)) {
            return null;
        }
        float epsilon[] = {0};
        WirePoint lp = startWirePoint;
        //todo try to reproduce the error when vertical wire splitted to non verticals
        while (lp.next != null) {
            if (isOnLine(lp, lp.next, p.x, p.y, epsilon)) {
                WireViewer newWire = remaining(p, lp);
                return newWire;
            }
            lp = lp.next;
        }
        return null;
    }

    @Override
    public void setNetNumberAt(EditorModel model, int netNumber, int x, int y) {
        if (netNumber != this.netNumber) {
            if (startWirePoint.x == x && startWirePoint.y == y) {
                this.netNumber = netNumber;
                model.setNetNumberAt(netNumber, endWirePoint.x, endWirePoint.y);
            } else if (endWirePoint.x == x && endWirePoint.y == y) {
                this.netNumber = netNumber;
                model.setNetNumberAt(netNumber, startWirePoint.x, startWirePoint.y);
            }
        }
    }

    @Override
    public void setNetNumberInsideX(EditorModel model, int netNumber, int xMin, int xMax, int y) {
        if (netNumber != this.netNumber) {
            if (startWirePoint.x >= xMin && startWirePoint.x <= xMax && startWirePoint.y == y) {
                this.netNumber = netNumber;
                model.setNetNumberAt(netNumber, endWirePoint.x, endWirePoint.y);
            } else if (endWirePoint.x >= xMin && endWirePoint.x <= xMax && endWirePoint.y == y) {
                this.netNumber = netNumber;
                model.setNetNumberAt(netNumber, startWirePoint.x, startWirePoint.y);
            }
        }
    }

    @Override
    public void setNetNumberInsideY(EditorModel model, int netNumber, int x, int yMin, int yMax) {
        if (netNumber != this.netNumber) {
            if (startWirePoint.y >= yMin && startWirePoint.y <= yMax && startWirePoint.x == x) {
                this.netNumber = netNumber;
                model.setNetNumberAt(netNumber, endWirePoint.x, endWirePoint.y);
            } else if (endWirePoint.y >= yMin && endWirePoint.y <= yMax && endWirePoint.x == x) {
                this.netNumber = netNumber;
                model.setNetNumberAt(netNumber, startWirePoint.x, startWirePoint.y);
            }
        }
    }

    @Override
    public int getNetNumberInY(short x, int minY, int maxY) {
        if (startWirePoint.x == x && minY <= startWirePoint.y && startWirePoint.y <= maxY) {
            return netNumber;
        } else if (endWirePoint.x == x && minY <= endWirePoint.y && endWirePoint.y <= maxY) {
            return netNumber;
        } else {
            return NetNumbers.NOT_CONNECTED;
        }
    }

    public int getNetNumberInX(int minX, int maxX, short y) {
        if (startWirePoint.x >= minX && startWirePoint.x <= maxX && startWirePoint.y == y) {
            return netNumber;
        } else if (endWirePoint.x >= minX && endWirePoint.x <= maxX && endWirePoint.y == y) {
            return netNumber;
        } else {
            return NetNumbers.NOT_CONNECTED;
        }
    }

    @Override
    public int getNetNumberAt(int x, int y) {
        if (startWirePoint.x == x && startWirePoint.y == y) {
            return netNumber;
        } else if (endWirePoint.x == x && endWirePoint.y == y) {
            return netNumber;
        } else {
            return NetNumbers.NOT_CONNECTED;
        }
    }

    public int reconnect(EditorModel model, int netNumber, int x, int y) {
//        System.out.println("WireViewer reconnect start:" + this.netNumber + " " + netNumber + "  " +
//                "x=" + x + "  y=" + y);
        int found = 0;
        if (netNumber != this.netNumber) {
            if (startWirePoint.x == x && startWirePoint.y == y) {
//                System.out.println("WireViewer reconnect start:" + this.netNumber + " " + netNumber);
                this.netNumber = netNumber;
                found = WIRE_ENDPOINT_FOUND;
//                if (netNumber != this.netNumber) {
                model.setNetNumberAt(netNumber, endWirePoint.x, endWirePoint.y);
//                }
            } else if (endWirePoint.x == x && endWirePoint.y == y) {
                //               System.out.println("WireViewer reconnect end:" + this.netNumber + " " +
                // netNumber);
                this.netNumber = netNumber;
                found = WIRE_ENDPOINT_FOUND;
//                if (netNumber != this.netNumber) {
                model.setNetNumberAt(netNumber, startWirePoint.x, startWirePoint.y);
//                }
            }
        } else {
            if ((startWirePoint.x == x && startWirePoint.y == y) || (endWirePoint.x == x &&
                    endWirePoint.y == y)) {
                //               System.out.println("WireViewer reconnect same net");
                found = WIRE_ENDPOINT_FOUND;
            } else {
//              System.out.println("WireViewer reconnect nothing:" + this.netNumber + " " +
// netNumber);
            }
        }
        return found;
    }

    @Override
    public int reconnectInX(EditorModel model, int netNumber, int startX, int endX, int y) {
        int found = 0;
        if (netNumber != this.netNumber) {
            if (startX <= startWirePoint.x && startWirePoint.x <= endX && startWirePoint.y == y) {
//                System.out.println("WireViewer reconnect start:" + this.netNumber + " " + netNumber);
                this.netNumber = netNumber;
                found = WIRE_ENDPOINT_FOUND;
//                if (netNumber != this.netNumber) {
                model.setNetNumberAt(netNumber, endWirePoint.x, endWirePoint.y);
//                }
            } else if (startX <= endWirePoint.x && endWirePoint.x <= endX && endWirePoint.y == y) {
                //               System.out.println("WireViewer reconnect end:" + this.netNumber + " " +
                // netNumber);
                this.netNumber = netNumber;
                found = WIRE_ENDPOINT_FOUND;
//                if (netNumber != this.netNumber) {
                model.setNetNumberAt(netNumber, startWirePoint.x, startWirePoint.y);
//                }
            }
        } else {
            if ((startX <= startWirePoint.x && startWirePoint.x <= endX && startWirePoint.y == y) ||
                    (startX <= endWirePoint.x && endWirePoint.x <= endX && endWirePoint.y == y)) {
                //               System.out.println("WireViewer reconnect same net");
                found = WIRE_ENDPOINT_FOUND;
            } else {
//              System.out.println("WireViewer reconnect nothing:" + this.netNumber + " " +
// netNumber);
            }
        }
        return found;
    }

    @Override
    public int reconnectInY(EditorModel model, int netNumber, int x, int startY, int endY) {
        int found = 0;
        if (netNumber != this.netNumber) {
            if (startWirePoint.x == x && startY <= startWirePoint.y && startWirePoint.y <= endY) {
//                System.out.println("WireViewer reconnect start:" + this.netNumber + " " + netNumber);
                this.netNumber = netNumber;
                found = WIRE_ENDPOINT_FOUND;
//                if (netNumber != this.netNumber) {
                model.setNetNumberAt(netNumber, endWirePoint.x, endWirePoint.y);
//                }
            } else if (endWirePoint.x == x && startY <= endWirePoint.y && endWirePoint.y <= endY) {
                //               System.out.println("WireViewer reconnect end:" + this.netNumber + " " +
                // netNumber);
                this.netNumber = netNumber;
                found = WIRE_ENDPOINT_FOUND;
//                if (netNumber != this.netNumber) {
                model.setNetNumberAt(netNumber, startWirePoint.x, startWirePoint.y);
//                }
            }
        } else {
            if ((startY <= startWirePoint.y && startWirePoint.y <= endY && startWirePoint.x == x) ||
                    (startY <= endWirePoint.y && endWirePoint.y <= endY && endWirePoint.x == x)) {
                //               System.out.println("WireViewer reconnect same net");
                found = WIRE_ENDPOINT_FOUND;
            } else {
//              System.out.println("WireViewer reconnect nothing:" + this.netNumber + " " +
// netNumber);
            }
        }
        return found;
    }

    private WireViewer remaining(WirePoint p, WirePoint lastOldPoint) {
        WireViewer newWire = new WireViewer();
        newWire.netNumber = netNumber;
        newWire.addPoint(p.x, p.y);
        WirePoint ip = lastOldPoint.next;
        while (ip != null) {
            newWire.addPoint(ip);
            ip = ip.next;
        }
        lastOldPoint.next = null;
        endWirePoint = lastOldPoint;
        addPoint(p.x, p.y);
        return newWire;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    @Override
    public void draw(Canvas canvas) {
        WirePoint p = null;
        WirePoint prev = startWirePoint;
        if (startWirePoint != null) {
            p = startWirePoint.next;
            if (p == null) {
                p = startWirePoint;
//                System.out.println("--------------------------------------------Bad wire");
//                System.out.flush();
            }
        }
        if (netNumberShowEnabled) {
            canvas.drawText(Integer.toString(netNumber), p.x, p.y, Context.netNumberPaint);
        }
        Paint paint;
        if (EditorModel.simulationIsRunning && Settings.displayWireLevel) {
            paint = Viewer.Context.getLevelColorPaint(level);
        } else {
            paint = currentPaint;
        }
        while (p != null) {
            canvas.drawLine(prev.x, prev.y, p.x, p.y, paint);
            prev = p;
            p = p.next;
        }
        if (startWirePoint != null) {
            if (connectedStart) {
                canvas.drawCircle(startWirePoint.x, startWirePoint.y, DOT_RADIUS, paint);
            } else {
                canvas.drawCircle(startWirePoint.x, startWirePoint.y, DOT_RADIUS, Context.filledPaint);
            }
        }
        if (endWirePoint != null) {
            if (connectedEnd) {
                canvas.drawCircle(endWirePoint.x, endWirePoint.y, DOT_RADIUS, paint);
            } else {
                canvas.drawCircle(endWirePoint.x, endWirePoint.y, DOT_RADIUS, Context.filledPaint);
            }
        }
    }

    @Override
    public void select(SelectMsg msg) {
        WirePoint p = startWirePoint;
        if (p.next == null) {
            float d = Math.abs(p.x - msg.pointerX) + Math.abs(p.y - msg.pointerY);
            if (d < msg.epsilon[0]) {
                msg.epsilon[0] = d;
                msg.viewer = this;
            }
        } else {
            while (p.next != null) {
                if (isOnLine(p, p.next, msg.pointerX, msg.pointerY, msg.epsilon)) {
                    msg.viewer = this;
                }
                p = p.next;
            }
        }
    }

    @Override
    public void search(SearchMsg msg) {
        float epsilon[] = {1};
        WirePoint p = startWirePoint;
        while (p.next != null) {
            if (isOnLine(p, p.next, msg.pointerX, msg.pointerY, epsilon)) {
                msg.addFound(this);
            }
            p = p.next;
        }
    }

    @Override
    public boolean isWire() {
        return true;
    }

    @Override
    int getBaseX() {
        return startWirePoint.x;
    }

    @Override
    int getBaseY() {
        return startWirePoint.y;
    }

    public boolean moveCheck(int dx, int dy) {
        int top = getTop();
        int bottom = getBottom();
        int left = getLeft();
        int right = getRight();
        return (top + dy > EditorModel.MIN_Y &&
                bottom + dy < EditorModel.MAX_Y &&
                left + dx > EditorModel.MIN_X &&
                right + dx < EditorModel.MAX_X);
    }

    @Override
    void move(EditorViewer editorViewer, int dx, int dy, boolean check) {
        //todo disconnect, reconnect join, split are necessary
        // only for move to center
        if (firstMove) {
            firstMove = false;
            cutX0 = startWirePoint.x;
            cutY0 = startWirePoint.y;
            cutX1 = endWirePoint.x;
            cutY1 = endWirePoint.y;
        } else {
            if ((cutX0 != startWirePoint.x || cutY0 != startWirePoint.y) &&
                    (cutX1 != endWirePoint.x || cutY1 != endWirePoint.y)) {
                int startNetNumber = NetNumbers.get();
                int endNetNumber = NetNumbers.get();
//                System.out.println("cutNet  startNetNumber=" + startNetNumber + " endNetNumber" + startNetNumber);
                editorViewer.model.setNetNumberAt(startNetNumber, cutX0, cutY0);
                editorViewer.model.setNetNumberAt(endNetNumber, cutX1, cutY1);
                //   NetNumbers.drop(savedNetNumber);
//                editorViewer.model.joinAt(startNetNumber, cutX0, cutY0);
//                editorViewer.model.joinAt(endNetNumber, cutX1, cutY1);
            }

        }
        WirePoint p = startWirePoint;
        while (p != null) {
            p.x += dx;
            p.y += dy;
            p = p.next;
        }
    }

    @Override
    public void disconnectFrom(EditorModel em) {
        // the device ios have nw, unconnected netnumbers
//        System.out.println("WireViewer disconnectFrom netNumber=" + netNumber);
        em.disconnect(this, em, netNumber, startWirePoint.x, startWirePoint.y);
        em.disconnect(this, em, netNumber, endWirePoint.x, endWirePoint.y);
        netNumber = NetNumbers.get();
    }

    @Override
    public String reroute(EditorModel editorModel) {
        return editorModel.rerouteNet(netNumber);
    }

    @Override
    public void disconnect(EditorModel editorModel, int netNumber, int x, int y) {
//        if (startWirePoint.x == x && startWirePoint.y == y) {
//            editorModel.joinAt(netNumber, x, y);
//        } else if (endWirePoint.x == x && endWirePoint.y == y) {
//            editorModel.joinAt(netNumber, x, y);
//        }
    }

    public void disconnectInX(EditorModel em, int netNumber, int xMin, int xMax, int y) {
        if (startWirePoint.x >= xMin && startWirePoint.x <= xMax && startWirePoint.y == y) {
            this.netNumber = netNumber;
            em.setNetNumberAt(netNumber, endWirePoint.x, endWirePoint.y);
        } else if (endWirePoint.x >= xMin && endWirePoint.x <= xMax && endWirePoint.y == y) {
            this.netNumber = netNumber;
            em.setNetNumberAt(netNumber, startWirePoint.x, startWirePoint.y);
        }
    }

    public void disconnectInY(EditorModel em, int netNumber, int x, int yMin, int yMax) {
        if (startWirePoint.y >= yMin && startWirePoint.y <= yMax && startWirePoint.x == x) {
            this.netNumber = netNumber;
            em.setNetNumberAt(netNumber, endWirePoint.x, endWirePoint.y);
        } else if (endWirePoint.y >= yMin && endWirePoint.y <= yMax && endWirePoint.x == x) {
            this.netNumber = netNumber;
            em.setNetNumberAt(netNumber, startWirePoint.x, startWirePoint.y);
        }
    }


    @Override
    public void changeNetNumber(int netNumber, int newNetNumber) {
        if (this.netNumber == netNumber) {
            this.netNumber = newNetNumber;
        }
    }

    public void reconnectFrom(EditorModel em) {
        // the device ios have nw, unconnected netnumbers

        netNumber = NetNumbers.get();
        connectedStart = em.reconnect(this, netNumber, startWirePoint.x, startWirePoint.y) != 0;
        connectedEnd = em.reconnect(this, netNumber, endWirePoint.x, endWirePoint.y) != 0;
//        System.out.println("WireViewer reconnectFrom netNumber=" + netNumber);
//        if (connectedStart) {
//            System.out.println("WireViewer foundStart");
//        }
//        if (connectedEnd) {
//            System.out.println("WireViewer foundEnd");
//        }
    }

    @Override
    void clone(int x, int y, Viewer v) {
        WirePoint point = ((WireViewer) v).startWirePoint;
        short dx = (short) (point.x - x);
        short dy = (short) (point.y - y);
        while (point != null) {
            addPoint((short) (point.x - dx), (short) (point.y - dy));
            point = point.next;
        }
    }

    public int getLeft() {
        int left = Integer.MAX_VALUE;
        WirePoint p = startWirePoint;
        while (p != null) {
            left = Math.min(left, p.x);
            p = p.next;
        }
        return left;
    }

    public int getTop() {
        int top = Integer.MAX_VALUE;
        WirePoint p = startWirePoint;
        while (p != null) {
            top = Math.min(top, p.y);
            p = p.next;
        }
        return top;
    }

    public int getBottom() {
        int bottom = Integer.MIN_VALUE;
        WirePoint p = startWirePoint;
        while (p != null) {
            bottom = Math.max(bottom, p.y);
            p = p.next;
        }
        return bottom;
    }

    public int getRight() {
        int right = Integer.MIN_VALUE;
        WirePoint p = startWirePoint;
        while (p != null) {
            right = Math.max(right, p.x);
            p = p.next;
        }
        return right;
    }

    public boolean moveSegmentOrPoint(EditorViewer editorViewer, float fromX, float fromY, int
            xOffset, int yOffset) {
        if (Settings.enabled45) {
            return false;
        }
        float epsilon[] = {4};
        if (selectedSegment != NO_SEGMENT) {
            if ((cutX0 != startWirePoint.x || cutY0 != startWirePoint.y) &&
                    (cutX1 != endWirePoint.x || cutY1 != endWirePoint.y)) {
                int startNetNumber = NetNumbers.get();
                int endNetNumber = NetNumbers.get();
//                System.out.println("cutNet  startNetNumber=" + startNetNumber + " endNetNumber" + startNetNumber);
                editorViewer.model.setNetNumberAt(startNetNumber, cutX0, cutY0);
                editorViewer.model.setNetNumberAt(endNetNumber, cutX1, cutY1);
                //   NetNumbers.drop(savedNetNumber);
//                editorViewer.model.joinAt(startNetNumber, cutX0, cutY0);
//                editorViewer.model.joinAt(endNetNumber, cutX1, cutY1);
            }
            if (selectedSegment == SELECTED_STARTPOINT_X) {
                moveStartPointX(fromX, fromY, xOffset, yOffset);
            } else if (selectedSegment == SELECTED_STARTPOINT_Y) {
                moveStartPointY(fromX, fromY, xOffset, yOffset);
            } else if (selectedSegment == SELECTED_ENDPOINT_X) {
                moveEndPointX(fromX, fromY, xOffset, yOffset);
            } else if (selectedSegment == SELECTED_ENDPOINT_Y) {
                moveEndPointY(fromX, fromY, xOffset, yOffset);
            } else {
                moveOneSegment(xOffset, yOffset, startWirePoint);
            }
        } else {
            cutX0 = startWirePoint.x;
            cutY0 = startWirePoint.y;
            cutX1 = endWirePoint.x;
            cutY1 = endWirePoint.y;
            if (Math.abs(startWirePoint.x - fromX) < epsilon[0] &&
                    Math.abs(startWirePoint.y - fromY) < epsilon[0]) {
                if (startWirePoint.next.x == startWirePoint.x) {
                    selectedSegment = SELECTED_STARTPOINT_X;
                } else {
                    selectedSegment = SELECTED_STARTPOINT_Y;
                }
                yMoveMaxDiff = Settings.CURSOR__OFFSET_10 * editorViewer.densityDpi /
                        (10 * editorViewer.scaleFactor);
            } else if (Math.abs(endWirePoint.x - fromX) < epsilon[0] &&
                    Math.abs(endWirePoint.y - fromY) < epsilon[0]) {
                WirePoint p = startWirePoint;
                while (p != null && p.next != endWirePoint) {
                    p = p.next;
                }
                if (p.x == endWirePoint.x) {
                    selectedSegment = SELECTED_ENDPOINT_X;
                } else {
                    selectedSegment = SELECTED_ENDPOINT_Y;
                }
                yMoveMaxDiff = Settings.CURSOR__OFFSET_10 * editorViewer.densityDpi /
                        (10 * editorViewer.scaleFactor);
            } else {
                findSegment(fromX, fromY, epsilon, startWirePoint);
            }
        }
        return true;
    }

    private void moveStartPointX(float fromX, float fromY, int xOffset, int yOffset) {
        if (yMoveMaxDiff > yMoveDiff) {
            yMoveDiff++;
            yOffset--;
        }
        startWirePoint.x += xOffset;
        startWirePoint.next.x = startWirePoint.x;
        startWirePoint.y += yOffset;
    }

    private void moveStartPointY(float fromX, float fromY, int xOffset, int yOffset) {
        if (yMoveMaxDiff > yMoveDiff) {
            yMoveDiff++;
            yOffset--;
        }
        startWirePoint.x += xOffset;
        startWirePoint.y += yOffset;
        startWirePoint.next.y = startWirePoint.y;
    }

    private void moveEndPointX(float fromX, float fromY, int xOffset, int yOffset) {
        if (yMoveMaxDiff > yMoveDiff) {
            yMoveDiff++;
            yOffset--;
        }
        WirePoint p = startWirePoint;
        while (p != null && p.next != endWirePoint) {
            p = p.next;
        }
        if (p != null) {
            endWirePoint.x += xOffset;
            endWirePoint.y += yOffset;
            p.x = endWirePoint.x;
        }
    }

    private void moveEndPointY(float fromX, float fromY, int xOffset, int yOffset) {
        if (yMoveMaxDiff > yMoveDiff) {
            yMoveDiff++;
            yOffset--;
        }
        WirePoint p = startWirePoint;
        while (p != null && p.next != endWirePoint) {
            p = p.next;
        }
        if (p != null) {
            endWirePoint.x += xOffset;
            endWirePoint.y += yOffset;
            p.y = endWirePoint.y;
        }
    }

    private void findSegment(float fromX, float fromY, float[] epsilon, WirePoint p) {
        selectedSegment = -1;
        while (p.next != null) {
            selectedSegment++;
            if (isOnLine(p, p.next, fromX, fromY, epsilon)) {
                if (p == startWirePoint) {
                    WirePoint q = new WirePoint(p.x, p.y);
                    q.next = startWirePoint;
                    startWirePoint = q;
//                    System.out.println("first segment move " + selectedSegment);
                    selectedSegment++;
                }
                if (p.next == endWirePoint) {
                    WirePoint q = new WirePoint(p.next.x, p.next.y);
                    endWirePoint.next = q;
                    endWirePoint = q;
//                    System.out.println("end segment move " + selectedSegment);
                }
//                System.out.println("segment move " + selectedSegment);
                break;
            }
            p = p.next;
        }
    }

    private void moveOneSegment(int xOffset, int yOffset, WirePoint p) {
        int i = 0;
        while (i < selectedSegment) {
            p = p.next;
            i++;
        }
        if (selectedSegment == i) {
            if (p.x == p.next.x) {
                p.x += xOffset;
                p.next.x += xOffset;
            } else if (p.y == p.next.y) {
                p.y += yOffset;
                p.next.y += yOffset;
            }
        }
    }

    public void endSegmentMove(EditorModel model) {
        WirePoint p = startWirePoint;
        while (p.next != null) {
            if (p.x == p.next.x && p.y == p.next.y) {
                p.next = p.next.next;
//                System.out.println("endSegmentMove merge ");
                if (p.next == null) {
                    endWirePoint = p;
                    break;
                }
            }
            p = p.next;
        }
        boolean found = true;
        while (found) {
            found = false;
            p = startWirePoint;
            while (p.next != null) {
                if (p.next.next != null) {
                    if (p.x == p.next.x && p.x == p.next.next.x ||
                            p.y == p.next.y && p.y == p.next.next.y) {
                        p.next = p.next.next;
                        found = true;
                    }
                }
                p = p.next;
            }
        }
    }
}
