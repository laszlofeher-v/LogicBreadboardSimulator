package com.logicbreadboard.simulator;

import android.graphics.Canvas;
import android.util.Log;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created on 12/18/14.
 */
public class EditorModel {
    // TODO: 8/29/15 check heap, set max, check open file
    static public final int MAX_X = 2048;
    static public final int MIN_X = 0;
    static public final int MAX_Y = 2048;
    static public final int MIN_Y = 0;
    static public final int MAX_SIM_TIME = 20000000;
    static public final int MAX_NUMBER_OF_SNAPSHOTS = 8;
    static ConcurrentHashMap<DeviceIOModel, FaultViewer> faultViewers;
    //    public LinkedList<Viewer> deletedViewers;
    static boolean simulationIsRunning = false;

    static {
        faultViewers = new ConcurrentHashMap<DeviceIOModel, FaultViewer>();
    }

    // TODO: 8/12/15 list of devices, list of wires ...
    // viewer knows where to add, delete
    public boolean enableScope = false;
    int snapshotIndex = 0;
    byte[][] snapshots = new byte[MAX_NUMBER_OF_SNAPSHOTS][];
    Router router;
    boolean enableDrawRaster = false;
    //    LinkedList<Viewer> selectionList;
    private LinkedList<Viewer> viewerLinkedList;
    Selections selections;
    private String selectedLabelText;

    public void remove(Viewer v) {
        viewerLinkedList.remove(v);
    }

    public boolean contains(Viewer v) {
        return viewerLinkedList.contains(v);
    }

    public int size() {
        return viewerLinkedList.size();
    }


    Powered powered;

    EditorModel() {
        viewerLinkedList = new LinkedList<Viewer>();
//        deletedViewers = new LinkedList<Viewer>();
        selections = new Selections();
        simulationIsRunning = false;
        enableScope = false;
        router = new Router();
        faultViewers.clear();
        powered = new Powered(this);
    }

    public void reset() {
        ListIterator<Viewer> listIterator = viewerLinkedList.listIterator();
        while (listIterator.hasNext()) {
            Viewer v = listIterator.next();
            v.reset();
        }
    }

    public void destroyLogicalConnections() {
        ListIterator<Viewer> listIterator = viewerLinkedList.listIterator();
        while (listIterator.hasNext()) {
            Viewer v = listIterator.next();
            v.disconnectLogical();
        }
    }

    public void makeLogicalConnections() {
        ListIterator<Viewer> listIterator = viewerLinkedList.listIterator();
        HashMap<Integer, DeviceIOModel> nets = new HashMap<>();
        listIterator = viewerLinkedList.listIterator();
        while (listIterator.hasNext()) {
            Viewer v = listIterator.next();
            v.makeLogicalConnection(nets);
        }
        HashMap<String, DeviceIOModel> labelNets = new HashMap<>();
        listIterator = viewerLinkedList.listIterator();
        while (listIterator.hasNext()) {
            Viewer v = listIterator.next();
            v.connectLabels(labelNets);
        }
    }

    public void compressNetNumbers() {
        ListIterator<Viewer> listIterator = viewerLinkedList.listIterator();
        HashMap<Integer, Integer> nets = new HashMap<>();
        int[] newNetNumber = new int[1];
        newNetNumber[0] = 1;
        listIterator = viewerLinkedList.listIterator();
        while (listIterator.hasNext()) {
            Viewer v = listIterator.next();
            v.compressNetNumbers(nets, newNetNumber);
        }
        NetNumbers.maxNum = newNetNumber[0];
    }

    public void compressViewersNetNumbers(HashMap<Integer, Integer> nets, int[] newNetNumber) {
        ListIterator<Viewer> listIterator = viewerLinkedList.listIterator();
        while (listIterator.hasNext()) {
            Viewer v = listIterator.next();
            v.compressNetNumbers(nets, newNetNumber);
        }
    }

    public void connectDip(DIPIoViewer firstIoViewer) {
        ListIterator<Viewer> listIterator = viewerLinkedList
                .listIterator(); // for all device viewers in the subcircuit
        while (listIterator.hasNext()) {
            Viewer v = listIterator.next();
            String s = v.getPortLabel();
            if (s != null) { // if device is port
                DIPIoViewer dio = firstIoViewer; // find box pin having the same uniqueName
                // (number)
                while (dio != null) {
                    if (dio.internalId.equals(s)) {
                        dio.model.connectTo(((PortViewer) v).firstIoViewer.model);
                    }
                    dio = (DIPIoViewer) dio.next;
                }
            }
        }
    }

    public void checkConnections() {
        ListIterator<Viewer> listIterator = viewerLinkedList.listIterator();
        while (listIterator.hasNext()) {
            Viewer v = listIterator.next();
            v.checkConnection(this);
        }
    }

    private void makeConnection() {
        ListIterator<Viewer> listIterator = viewerLinkedList.listIterator();
        while (listIterator.hasNext()) {
            Viewer v = listIterator.next();
            v.routeNetNumber(router);
        }
    }

    public boolean selectIOGlobal(int x, int y) {
        boolean retVal = false;
        DeviceIOViewer ioViewer = null;
        ListIterator<Viewer> listIterator = viewerLinkedList.listIterator();
        while (listIterator.hasNext()) {
            Viewer v = listIterator.next();
            ioViewer = v.findIO(x, y);
            if (ioViewer != null) {
                break;
            }
        }
        if (ioViewer != null) {
            retVal = true;
            if (selections.routableSelectionList.contains(ioViewer)) {
                selections.unSelectRoutable(ioViewer);
            } else {
                selections.selectRoutable(ioViewer);
            }
//                ioViewer.selected = !ioViewer.selected;
//                syncSelection(ioViewer);
        }
        return retVal;
    }

    public void removeWires(int netNumber) {
        LinkedList<Viewer> removedWires = new LinkedList<>();
        ListIterator<Viewer> listIterator = viewerLinkedList.listIterator();
        while (listIterator.hasNext()) {
            Viewer v = listIterator.next();
            if (v.isWire() && v.getNetNumber() == netNumber) {
                removedWires.add(v);
            }
        }
        listIterator = removedWires.listIterator();
        while (listIterator.hasNext()) {
            Viewer v = listIterator.next();
            viewerLinkedList.remove(v);
            selections.unselect(v);
        }
    }

    private void renumber(int netNumber, LinkedList<Integer> newNetNumbers) {
        ListIterator<Viewer> listIterator = viewerLinkedList.listIterator();
        while (listIterator.hasNext()) {
            Viewer v = listIterator.next();
            v.renumber(netNumber, newNetNumbers);
        }
    }

    public String rerouteMoved() {

        return selections.rerouteAllSelected(this);
    }

    public String rerouteNet(int netNumber) {
        removeWires(netNumber);
        LinkedList<Integer> newNetNumbers = new LinkedList<Integer>();
        renumber(netNumber, newNetNumbers);
        ListIterator<Integer> listIterator = newNetNumbers.listIterator();
        if (listIterator.hasNext()) {
            int net1 = listIterator.next();
            while (listIterator.hasNext()) {
                int net2 = listIterator.next();
//                System.out.println("net2=" + net2 + " net1=" + net1);
                if (!connect(net2, net1)) {
                    //                   rollback(removedWires, newWires);
                    return "Could not reroute";
                }
                net1 = net2;

            }
        }
        return null;
    }

    private boolean connect(int net1, int net2) {
        router = new Router();
        router.init(MIN_X, MIN_Y, MAX_X, MAX_Y);
        router.startNet = net1;
        router.targetNet = net2;
        prepareRaster();
        int[] distance = new int[1];
        WirePoint nextPoint = router.findRoute(distance);
//        System.out.println("distance=" + distance[0]);
//        System.out.flush();
        if (nextPoint != null) {
            if (distance[0] == 0) {
//                System.out.println
//                        ("split-------------------------------------------------------------");
//                System.out.flush();
                split(nextPoint);
                makeConnection();
//                selections.unselect();
                return true;
            } else {
                WireViewer wire = (WireViewer) ViewerFactory.factory.newViewer((byte) (SchematicViewerFactory.DeviceGroup
                        .Wires.ordinal()), SchematicViewerFactory.Wires.Wire.ordinal());
                wire.addPoint(nextPoint);
                int res = router.getNextPoint(wire);
                while (res == 1) {
                    res = router.getNextPoint(wire);
                }
                if (res < 0) {
                    return false;
                }
                add(wire);
                split(wire.startWirePoint);
                split(wire.endWirePoint);
                //           selections.unselect();
                //TODO Is this really necessary?
                DeviceViewer.setSelectionZoom(1f);
                wire.netNumber = router.startNet;
                makeConnection();
                //           selections.unselect();
                checkConnections();
                return true;
            }
        } else {
            return false;
        }
    }

    private void prepareRaster() {
        ListIterator<Viewer> listIterator = viewerLinkedList.listIterator();
        listIterator = viewerLinkedList.listIterator();
        while (listIterator.hasNext()) {
            Viewer v = listIterator.next();
            v.putMask(router);
        }
        listIterator = viewerLinkedList.listIterator();
        while (listIterator.hasNext()) {
            Viewer v = listIterator.next();
            v.putStartAndTarget(router);
        }
    }

    private void split(WirePoint p) {
        LinkedList<Viewer> splitList = new LinkedList<Viewer>();
        ListIterator<Viewer> listIterator = viewerLinkedList.listIterator();
        while (listIterator.hasNext()) {
            Viewer v = listIterator.next();
// Every split changes netnumbers therefore no netnumber filter can be used
            Viewer newViewer = v.split(p);
            if (newViewer != null) {
                splitList.add(newViewer);
            }
        }
        listIterator = splitList.listIterator();
        while (listIterator.hasNext()) {
            Viewer v = listIterator.next();
            viewerLinkedList.add(v);
        }
    }

    void joinAt(int netNumber, int x, int y) {
        int count = 0;
        ListIterator<Viewer> listIterator = viewerLinkedList.listIterator();
        WireViewer v0 = null;
        WireViewer v1 = null;
        while (listIterator.hasNext()) {
            Viewer v = listIterator.next();
            if (v.getNetNumberAt(x, y) == netNumber) {
                if (v.startsOrEndsAt(x, y)) {
                    if (count == 0) {
                        if (v instanceof WireViewer) {
                            v0 = (WireViewer) v;
                        }
                    } else if (count == 1) {
                        if (v instanceof WireViewer) {
                            v1 = (WireViewer) v;
                        }
                    } else {
                        return;
                    }
                    count++;
                }
            }
        }
        if (count == 2 && v0 != null && v1 != null) {
            WireViewer dropped = v0.join(v1);
            viewerLinkedList.remove(dropped);
        }
    }

    public String routing(float xo, float yo) {
        String result;
        if (selections.getNumberOfRoutableSelections() == 1) {
            result = rerouteNet(selections.getLastRoutableSelection().getNetNumber());
            return result;
        }
        while (selections.getNumberOfRoutableSelections() >= 2) {
            router = new Router();
            router.init(MIN_X + xo, MIN_Y + yo, MAX_X + xo, MAX_Y + yo);
            result = router.setStartTarget(selections);
            if (result != null) {
                return result;
            }
            prepareRaster();
            int[] distance = new int[1];
            WirePoint nextPoint = router.findRoute(distance);
//            System.out.println("firstPoint=" + nextPoint.x + " " + nextPoint.y + " " +
//                    "   distance=" + distance[0]);
//            System.out.flush();
            if (nextPoint != null) {
                if (distance[0] == 0) {
                    split(nextPoint);
//                    System.out.println
//                            ("split-------------------------------------------------------------");
//                    System.out.flush();
                    makeConnection();
//                    selections.unselect();
                    selections.removeFirstRoutable();
                } else {
                    WireViewer wire = (WireViewer) ViewerFactory.factory.newViewer((byte) (SchematicViewerFactory.DeviceGroup
                            .Wires.ordinal()), SchematicViewerFactory.Wires.Wire.ordinal());
                    wire.addPoint(nextPoint);
                    int res = router.getNextPoint(wire);
                    while (res == 1) {
                        res = router.getNextPoint(wire);
                    }
                    if (res < 0) {
                        return "No route found";
                    }
                    add(wire);
                    split(wire.startWirePoint);
                    split(wire.endWirePoint);
//                    selections.unselect();
                    //TODO Is this really necessary?
                    DeviceViewer.setSelectionZoom(1f);
                    wire.netNumber = router.startNet;
                    makeConnection();
//                selections.unselect();
                    checkConnections();
                    selections.removeFirstRoutable();
//                    return null;
                }
            } else {
                return "No route found";

            }
        }
        selections.unselect();
        return null;
    }

    public String restrictedRouting(float xo, float yo) {
        String result;
        if (selections.getNumberOfRoutableSelections() == 1) {
            result = rerouteNet(selections.getLastRoutableSelection().getNetNumber());
            return result;
        }
        router = new Router();
        router.init(MIN_X + xo, MIN_Y + yo, MAX_X + xo, MAX_Y + yo);
        Viewer vStart = selections.getFirstRoutableSelection();
        Viewer vEnd = selections.getLastRoutableSelection();
        result = router.setStartTarget(selections);
        if (result != null) {
            return result;
        }
        ListIterator<Viewer> listIterator; //= viewerLinkedList.listIterator();
        listIterator = viewerLinkedList.listIterator();
        while (listIterator.hasNext()) {
            Viewer v = listIterator.next();
            v.putRestrictedMask(router, vStart, vEnd);
        }
        listIterator = viewerLinkedList.listIterator();
        while (listIterator.hasNext()) {
            Viewer v = listIterator.next();
            v.putRestrictedStartAndTarget(router, vStart, vEnd);
        }
        int[] distance = new int[1];
        WirePoint nextPoint = router.findRoute(distance);
//            System.out.println("firstPoint=" + nextPoint.x + " " + nextPoint.y + " " +
//                    "   distance=" + distance[0]);
//            System.out.flush();
        if (nextPoint != null) {
            if (distance[0] == 0) {
                split(nextPoint);
//                System.out.println
//                        ("split-------------------------------------------------------------");
//                System.out.flush();
                makeConnection();
//                    selections.unselect();
                selections.removeFirstRoutable();
            } else {
                WireViewer wire = (WireViewer) ViewerFactory.factory.newViewer((byte) (SchematicViewerFactory.DeviceGroup
                        .Wires.ordinal()), SchematicViewerFactory.Wires.RestrictedWire.ordinal());
                wire.addPoint(nextPoint);
                int res = router.getNextPoint(wire);
                while (res == 1) {
                    res = router.getNextPoint(wire);
                }
                if (res < 0) {
                    return "No route found";
                }
                add(wire);
                split(wire.startWirePoint);
                split(wire.endWirePoint);
//                    selections.unselect();
                //TODO Is this really necessary?
                DeviceViewer.setSelectionZoom(1f);
                wire.netNumber = router.startNet;
                makeConnection();
//                selections.unselect();
                checkConnections();
                selections.removeFirstRoutable();
//                    return null;
            }
        } else {
            return "No route found";

        }
        selections.unselect();
        return null;
    }

    public boolean isEmpty() {
        if (viewerLinkedList == null) {
            return true;
        }
        int length = viewerLinkedList.size();
        Log.i("MODEL", "length=" + length);
        return viewerLinkedList.isEmpty();
    }

    public void deleteAll() {
        viewerLinkedList = new LinkedList<Viewer>();
        selections = new Selections();
    }

    public void save(DataOutputStream out) throws java.io.IOException {
        ListIterator<Viewer> listIterator = viewerLinkedList.listIterator();
        while (listIterator.hasNext()) {
            Viewer v = listIterator.next();
            v.save(out);
        }
    }

    public void makeSnapshot() {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);
        try {
            ListIterator<Viewer> listIterator = viewerLinkedList.listIterator();
            while (listIterator.hasNext()) {
                Viewer v = listIterator.next();
                v.save(dos);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        byte[] result;
        result = baos.toByteArray();
        if (snapshotIndex == 0) {
            snapshots[snapshotIndex++] = result;
            dropSnapshots();
        } else {
            if (!Arrays.equals(snapshots[snapshotIndex - 1], result)) {
                if (snapshotIndex < MAX_NUMBER_OF_SNAPSHOTS) {
                    snapshots[snapshotIndex++] = result;
                    Log.i("MAKE", "dropSnapshots snapshotindex=" + snapshotIndex);
                    dropSnapshots();
                } else {
                    for (int i = 0; i < MAX_NUMBER_OF_SNAPSHOTS - 1; i++) {
                        snapshots[i] = snapshots[i + 1];
                    }
                    snapshots[snapshotIndex - 1] = result;
                }
            } else {
                Log.i("MAKE", "snapshot is equal snapshotindex=" + snapshotIndex);
            }
        }
        Log.i("MAKE", "                     snapshotindex=" + snapshotIndex);
    }

    public void dropSnapshots() {
        for (int i = snapshotIndex; i < MAX_NUMBER_OF_SNAPSHOTS; i++) {
            snapshots[i] = null;
        }
    }

    public void undo(EditorViewer editorViewer) {
        Log.i("UNDO", "index=" + snapshotIndex);
        if (snapshotIndex > 0) {
            snapshotIndex--;
            restoreSnapshot(snapshotIndex - 1, "undo snapshotindex=" + snapshotIndex, editorViewer);
        }
    }

    public void redo(EditorViewer editorViewer) {
        if (snapshotIndex < MAX_NUMBER_OF_SNAPSHOTS) {
            if (snapshots[snapshotIndex] == null) {
                return;
            }
        } else {
            return;
        }
        restoreSnapshot(snapshotIndex, "redo snapshotindex=" + snapshotIndex, editorViewer);
        snapshotIndex++;
    }

    public void restoreSnapshot(int pos, String str, EditorViewer editorViewer) {
        Log.i("RESTORE", "pos=" + pos);
        viewerLinkedList = new LinkedList<Viewer>();
        selections.unselect();
        if (pos < 0 || pos > snapshots.length) {
            return;
        }
        ByteArrayInputStream bais = new ByteArrayInputStream(snapshots[pos]);
        DataInputStream dis = new DataInputStream(bais);
        try {
            NetNumbers.reset();
            while (dis.available() > 0) {
                short group = dis.readShort();
                short sym = dis.readShort();
                if (group != SchematicViewerFactory.META_VIEWER_GROUP) {
                    Viewer v;
                    v = ViewerFactory.factory.newViewer(group, sym);
                    if (v != null) {
                        v.load(dis);
                        add(v);
                        v.setHolesUsed(editorViewer);
                    } else {
                        Log.i("RESTORE", "break group=" + group + " sym=" + sym);
                        break;
                    }
                } else {
                    if (sym == SchematicViewerFactory.ZOOMPOS_VIEWER_SYM) {
                        float z = dis.readFloat();
                        float dx = dis.readFloat();
                        float dy = dis.readFloat();
                    } else if (sym == SchematicViewerFactory.FILENAME_VIEWER_SYM) {
//                        if (Settings.getProjectFileName().equals(Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + Settings
//                                .defaultDownloadDirectory + "/tmp.tmp")) {
                        String name = dis.readUTF();
//                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (dis != null) {
//                System.out.println("Undo dis!=null");
//                System.out.println("Checkconnection 1");
                checkConnections();
                compressNetNumbers();
            } else {
//                System.out.println("Undo dis==null");
            }
        }
//        System.out.println("Checkconnection 2");
        checkConnections();
    }

    void drawRaster(Canvas canvas) {
//        Paint paint = new Paint();
//        paint.setStrokeWidth(1.0f);
//        paint.setColor(Color.BLUE);
//        for (int x = 0; x < router.rasterWidth; x++) {
//            float xx = x - router.rasterXOffset;
//            for (int y = 0; y < router.rasterHeigth; y++) {
//                byte m = router.mask[x][y];
//                int color = 0xff000000;
//                if ((m == Router.ALL_DISABLED)) {
//                    color = Color.RED;
//                }
//                if ((m & Router.VERTICAL_ALLOWED) != 0) {
//                    color |= Color.GREEN;
//                }
//                if ((m & Router.DOWN_ALLOWED) != 0) {
//                    color |= Color.MAGENTA;
//                }
//                if ((m & Router.UP_ALLOWED) != 0) {
//                    color |= Color.CYAN;
//                }
//                if ((m & Router.HORIZONTAL_ALLOWED) != 0) {
//                    color |= Color.YELLOW;
//                }
//                if (m == Router.allEnabled) {
//                    if (router.raster[x][y] < Short.MAX_VALUE) {
//                        color = 0xff000000 | router.raster[x][y];
//                    }
//                }
//                if ((m == Router.TARGET_PIXEL)) {
//                    color = Color.WHITE;
//                }
//                paint.setColor(color);
//                canvas.drawPoint(xx, y - router.rasterYOffset, paint);
//            }
//        }
    }

    public void selectFullNet() {
        ListIterator<Viewer> listIterator = viewerLinkedList.listIterator();
        int netNumber = selections.getFirstRoutableSelection().getNetNumber();
        SelectMsg selectMsg = new SelectMsg();
        while (listIterator.hasNext()) {
            Viewer v = listIterator.next();
            if (v.getNetNumber() == netNumber) {
                forcedSelect(v);
            }
        }
    }

    public void drawAll(Canvas canvas, int canvasWidth, int canvasHeight, float scaleFactor,
                        float xOffset, float yOffset) {
        if (enableDrawRaster) {
            drawRaster(canvas);
            enableDrawRaster = false;
        }
        ListIterator<Viewer> listIterator = viewerLinkedList.listIterator();
        while (listIterator.hasNext()) {
            Viewer v = listIterator.next();
            v.draw(canvas);
        }
        for (DeviceIOModel ioModel : faultViewers.keySet()) {
            FaultViewer fv = faultViewers.get(ioModel);
            fv.draw(canvas);
        }
        selections.draw(canvas, canvasWidth, canvasHeight, scaleFactor, this);

    }

    protected int numberOfChannels;

    public void showScope(Canvas canvas, int left, int xStart, int canvasWidth, float
            scaleFactor, float scopeZoom, float top) {
        // left is the length of WWWWWW string the longest possible uniqueName of the channel
        // xStart is the display start time
        float y = top + 2 * ProbeViewer.CHANNEL_HEIGHT;
        float xOffset = left / scopeZoom + xStart;
        float sw = Viewer.Context.scopeTimePaint.getStrokeWidth();
        Viewer.Context.scopeTimePaint.setStrokeWidth(0.1f);
        float endTime = (canvasWidth) / (scaleFactor * scopeZoom) - xOffset;
//        System.out.println("scaleFactor=" + scaleFactor);
//        System.out.println("left=" + left + " xStart=" + xStart + " xx=" + startTime +
//                " canvasWidth=" + canvasWidth + " scopeZoom=" + scopeZoom +
//                " i=" + i + " " + "m=" + m + " d=" + d + " endTime=" + endTime);
        numberOfChannels = 0;
        ListIterator<Viewer> listIterator = viewerLinkedList.listIterator();
        while (listIterator.hasNext()) {
            Viewer v = listIterator.next();
//            y = v.showRecord(canvas, xx, y, 1);
            if (v.showRecord(canvas, xOffset, endTime, y, scopeZoom)) {
                numberOfChannels++;
                y = y + ProbeViewer.CHANNEL_HEIGHT;
            }
        }
        //        showScopeTime(canvas, left, xStart, canvasWidth,
        //                scaleFactor, scopeZoom, top);
//        showScopeTime(canvas, left, xStart, canvasWidth, scaleFactor, scopeZoom, top, 32);
        Viewer.Context.scopeTimePaint.setStrokeWidth(sw);
    }

    public void showScopeTime(Canvas canvas, int left, int xStart, int canvasWidth, float
            scaleFactor, float scopeZoom, float top, int yStart) {
        float sw = Viewer.Context.scopeTimePaint.getStrokeWidth();
        float xOffset = left / scopeZoom + xStart;
        float endTime = (canvasWidth) / (scaleFactor * scopeZoom) - xOffset;
        float y = top + (numberOfChannels + 1) * ProbeViewer.CHANNEL_HEIGHT;
        int i = Math.round(64 * 64 / scopeZoom);
        int d = 1;
        while (i > 1) {
            d = d * 10;
            i = i / 8;
        }
        d = d / 1000;
        int m = 10 * d;
        int m2 = m / 2;
        if (m == 0) {
            m = 1;
        }
        if (d == 0) {
            d = 1;
        }
        if (m2 == 0) {
            m2 = 1;
        }
        int t0 = -xStart;
        t0 = t0 - t0 % d;
        float yy = yStart + ProbeViewer.CHANNEL_HEIGHT / 2;
        for (int t = t0; t <= endTime; t = t + d) {
            if ((t) % m2 == 0) {
                Viewer.Context.scopeTimePaint.setStrokeWidth(0.5f);
                canvas.drawText(Integer.toString(t), scopeZoom * (t + xOffset), yy, Viewer
                        .Context.scopeTextPaint);
            } else if ((t) % m == 0) {
                Viewer.Context.scopeTimePaint.setStrokeWidth(1f);
                canvas.drawText(Integer.toString(t), scopeZoom * (t + xOffset), yy, Viewer
                        .Context.scopeTextPaint);
            } else {
                Viewer.Context.scopeTimePaint.setStrokeWidth(0.25f);
            }
            canvas.drawLine(scopeZoom * (t + xOffset), top, scopeZoom * (t + xOffset), y,
                    Viewer.Context
                            .scopeTimePaint);
        }
        y = top + ProbeViewer.CHANNEL_HEIGHT / 2;
        Viewer.Context.scopeTimePaint.setStrokeWidth(sw);
        for (int t = 0; t <= endTime; t = t + m2) { // TODO: 9/10/15 move drawText to drawLine loop
            if (t % m2 == 0) {
                canvas.drawText(Integer.toString(t), scopeZoom * (t + xOffset), yy, Viewer.currentPaint);
            } else if (t % m == 0) {
                canvas.drawText(Integer.toString(t), scopeZoom * (t + xOffset), yy, Viewer.currentPaint);
            }
//            if ((t % 10) == 0) {
//                canvas.drawText(Integer.toString(t), scopeZoom * (t + xx), y, Viewer.currentPaint);
////            } else if (x % 100 == 0) {
////                canvas.drawText(Integer.toString(x), x, y, Viewer.currentPaint);
//            }
        }
        Viewer.Context.scopeTimePaint.setStrokeWidth(sw);
    }

    public void showScopeChannelNames(Canvas canvas, float x, float top) {
        ListIterator<Viewer> listIterator = viewerLinkedList.listIterator();
        float y = top + 3 * ProbeViewer.CHANNEL_HEIGHT;
        while (listIterator.hasNext()) {
            Viewer v = listIterator.next();
            y = v.showText(canvas, x, y);
        }
    }

//    public void undelete() {
//        selections.unselect();
//        ListIterator<Viewer> listIterator = deletedViewers.listIterator();
//        while (listIterator.hasNext()) {
//            Viewer v = listIterator.next();
//            viewerLinkedList.add(v);
//        }
//    }

    protected LinkedList<Viewer> broadcastSearchMsg(LinkedList<Viewer> list, float pointerX,
                                                    float pointerY) {
        ListIterator<Viewer> listIterator = list.listIterator();
        SearchMsg searchMsg = new SearchMsg();
        searchMsg.pointerX = pointerX;
        searchMsg.pointerY = pointerY;
        searchMsg.epsilon[0] = 4;
        while (listIterator.hasNext()) {
            Viewer v = listIterator.next();
            v.search(searchMsg);
        }
        return searchMsg.foundList;
    }

    public void setNetNumberAt(int netNumber, int x, int y) {
        ListIterator<Viewer> listIterator = viewerLinkedList.listIterator();
        while (listIterator.hasNext()) {
            Viewer v = listIterator.next();
            v.setNetNumberAt(this, netNumber, x, y);
        }
    }

    public void setNetNumberInsideX(int netNumber, int xMin, int xMax, int y) {
        ListIterator<Viewer> listIterator = viewerLinkedList.listIterator();
        while (listIterator.hasNext()) {
            Viewer v = listIterator.next();
            v.setNetNumberInsideX(this, netNumber, xMin, xMax, y);
        }
    }

    public void setNetNumberInsideY(int netNumber, int x, int yMin, int yMax) {
        ListIterator<Viewer> listIterator = viewerLinkedList.listIterator();
        while (listIterator.hasNext()) {
            Viewer v = listIterator.next();
            v.setNetNumberInsideY(this, netNumber, x, yMin, yMax);
        }
    }

    public void disconnect(Viewer exclude, EditorModel em, int netNumber, int x, int y) {
        ListIterator<Viewer> listIterator = viewerLinkedList.listIterator();
        while (listIterator.hasNext()) {
            Viewer v = listIterator.next();
            if (v != exclude) {
                v.disconnect(em, netNumber, x, y);
            }
        }
        int newNetNumber = NetNumbers.get();
        listIterator = viewerLinkedList.listIterator();
        while (listIterator.hasNext()) {
            Viewer v = listIterator.next();
            if (v != exclude) {
                v.changeNetNumber(netNumber, newNetNumber);
            }
        }
    }

    public void disconnectInX(Viewer exclude, EditorModel em, int netNumber,
                              int startX, int endX, int y) {
        ListIterator<Viewer> listIterator = viewerLinkedList.listIterator();
        while (listIterator.hasNext()) {
            Viewer v = listIterator.next();
            if (v != exclude) {
                netNumber = NetNumbers.get();
                v.disconnectInX(em, netNumber, startX, endX, y);
            }
        }
//        listIterator = viewerLinkedList.listIterator();
//        while (listIterator.hasNext()) {
//            Viewer v = listIterator.next();
//            if (v != exclude) {
//                int newNetNumber = NetNumbers.get();
//                v.changeNetNumber(netNumber, newNetNumber);
//            }
//        }
    }

    public void disconnectInY(Viewer exclude, EditorModel em, int netNumber,
                              int x, int startY, int endY) {
        ListIterator<Viewer> listIterator = viewerLinkedList.listIterator();
        while (listIterator.hasNext()) {
            Viewer v = listIterator.next();
            if (v != exclude) {
                netNumber = NetNumbers.get();
                v.disconnectInY(em, netNumber, x, startY, endY);
            }
        }
//        // TODO: 9/22/15  extract method
//        listIterator = viewerLinkedList.listIterator();
//        while (listIterator.hasNext()) {
//            Viewer v = listIterator.next();
//            if (v != exclude) {
//                int newNetNumber = NetNumbers.get();
//                v.changeNetNumber(netNumber, newNetNumber);
//            }
//        }
    }

    public int reconnect(Viewer exclude, int netNumber, int x, int y) {
        ListIterator<Viewer> listIterator = viewerLinkedList.listIterator();
        int result = 0;
        while (listIterator.hasNext()) {
            Viewer v = listIterator.next();
            if (v != exclude) {
                result |= v.reconnect(this, netNumber, x, y);
            }
        }
//        System.out.println("reconnect result=" + result);
//        System.out.flush();
        return result;
    }

    public int reconnectInX(Viewer exclude, int netNumber, int startX, int endX, int y) {
        ListIterator<Viewer> listIterator = viewerLinkedList.listIterator();
        int result = 0;
        while (listIterator.hasNext()) {
            Viewer v = listIterator.next();
            if (v != exclude) {
                result |= v.reconnectInX(this, netNumber, startX, endX, y);
            }
        }
//        System.out.println("reconnect result=" + result);
//        System.out.flush();
        return result;
    }

    public int reconnectInY(Viewer exclude, int netNumber, int x, int startY, int endY) {
        ListIterator<Viewer> listIterator = viewerLinkedList.listIterator();
        int result = 0;
        while (listIterator.hasNext()) {
            Viewer v = listIterator.next();
            if (v != exclude) {
                result |= v.reconnectInY(this, netNumber, x, startY, endY);
            }
        }
//        System.out.println("reconnect result=" + result);
//        System.out.flush();
        return result;
    }

    public int getNetNumbertInX(Viewer exclude, int netNumber, int minX, int maxX, short y) {
        ListIterator<Viewer> listIterator = viewerLinkedList.listIterator();
        int result = NetNumbers.NOT_CONNECTED;
        while (listIterator.hasNext()) {
            Viewer v = listIterator.next();
            if (v != exclude) {
                int lastNet = v.getNetNumberInX(minX, maxX, y);
                if (lastNet != NetNumbers.NOT_CONNECTED) {
                    result = lastNet;
                }
            }
        }
        return result;
    }


    public int getNetNumbertInY(Viewer exclude, int netNumber, short x, int minY, int maxY) {
        ListIterator<Viewer> listIterator = viewerLinkedList.listIterator();
        int result = NetNumbers.NOT_CONNECTED;
        while (listIterator.hasNext()) {
            Viewer v = listIterator.next();
            if (v != exclude) {
                int lastNet = v.getNetNumberInY(x, minY, maxY);
                if (lastNet != NetNumbers.NOT_CONNECTED) {
                    result = lastNet;
                }
            }
        }
        return result;
    }


    public int getNetNumberAt(Viewer exclude, int x, int y) {
        ListIterator<Viewer> listIterator = viewerLinkedList.listIterator();
        int result = NetNumbers.NOT_CONNECTED;
        while (listIterator.hasNext()) {
            Viewer v = listIterator.next();
            if (v != exclude) {
                int lastNet = v.getNetNumberAt(x, y);
                if (lastNet != NetNumbers.NOT_CONNECTED) {
                    result = lastNet;
                }
            }
        }
        return result;
    }

    public void add(Viewer viewer) {
        viewer.connectPower(powered);
        viewerLinkedList.add(viewer);
    }

    public void delete(Viewer viewer) {
        viewerLinkedList.remove(viewer);
    }

    public void forcedSelect(Viewer viewer) {
        SelectMsg selectMsg = new SelectMsg();
        selectMsg.pointerX = 0;
        selectMsg.pointerY = 0;
        selectMsg.epsilon[0] = 4;
        selectMsg.viewer = null;
        selectMsg.ioViewer = null;
        viewer.forcedSelect(selectMsg);
        if (selectMsg.viewer != null) {
            selections.select(selectMsg.viewer);
            if (selectMsg.ioViewer != null) {
                if (selections.isSelected(selectMsg.viewer)) {
                    selections.selectRoutable(selectMsg.ioViewer);
                }
            }
        }
    }

    public boolean processSelectResult(SelectMsg selectMsg) {
        if (selectMsg.viewer != null) {
            selections.invertSelection(selectMsg.viewer);
            if (selectMsg.ioViewer != null) {
                if (selectMsg.ioViewer.selected) {
                    selections.unSelectRoutable(selectMsg.ioViewer);
                } else {
                    if (selections.isSelected(selectMsg.viewer)) {
                        selections.selectRoutable(selectMsg.ioViewer);
                    }
                }
            }
//            makeSnapshot();
            if (selections.routableSelectionList.size() >= 2) {
                routing(0, 0);
                makeSnapshot();
            }
            return true;
        } else {
            return false;
        }
    }

    public boolean select(int x, int y) {
        ListIterator<Viewer> listIterator = viewerLinkedList.listIterator();
        SelectMsg selectMsg = new SelectMsg();
        selectMsg.pointerX = x;
        selectMsg.pointerY = y;
        selectMsg.epsilon[0] = 4;
        selectMsg.viewer = null;
        selectMsg.ioViewer = null;
        for (DeviceIOModel ioModel : faultViewers.keySet()) {
            FaultViewer fv = faultViewers.get(ioModel);
            fv.select(selectMsg);
        }
        while (listIterator.hasNext()) {
            Viewer v = listIterator.next();
            v.select(selectMsg);
        }
        return processSelectResult(selectMsg);
    }

    public void selectFast(int x, int y) {
        ListIterator<Viewer> listIterator = viewerLinkedList.listIterator();
        SelectMsg selectMsg = new SelectMsg();
        selectMsg.pointerX = x;
        selectMsg.pointerY = y;
        selectMsg.epsilon[0] = 4;
        while (listIterator.hasNext()) {
            Viewer v = listIterator.next();
            v.select(selectMsg);
        }
        if (selectMsg.viewer != null) {
            selections.select(selectMsg.viewer);
            if (selectMsg.ioViewer != null) {
                if (selections.isSelected(selectMsg.viewer)) {
                    selections.selectRoutable(selectMsg.ioViewer);
                }
            }
        }
    }

    public boolean selectIO(int x, int y) {
        boolean retVal = false;
        ListIterator<Viewer> listIterator = viewerLinkedList.listIterator();
        DeviceIOViewer ioViewer = null;
        while (listIterator.hasNext()) {
            Viewer v = listIterator.next();
            ioViewer = v.findIO(x, y);
            if (ioViewer != null) {
                retVal = true;
                if (selections.routableSelectionList.contains(ioViewer)) {
                    selections.unSelectRoutable(ioViewer);
                } else {
                    selections.selectRoutable(ioViewer);
                }
//                ioViewer.selected = !ioViewer.selected;
//                syncSelection(ioViewer);
            }
        }
        if (retVal && selections.routableSelectionList.size() >= 2) {
            routing(0, 0);
            makeSnapshot();
        }
        return retVal;
    }

    public boolean pushButton(int x, int y) {
        boolean res = false;
        ListIterator<Viewer> listIterator = viewerLinkedList.listIterator();
        PushButtonMsg pushButtonMsg = new PushButtonMsg();
        pushButtonMsg.pointerX = x;
        pushButtonMsg.pointerY = y;
        while (listIterator.hasNext()) {
            Viewer v = listIterator.next();
            if (v.pushButton(pushButtonMsg)) {
                res = true;
                break;
            }
        }
        return res;
    }

    public boolean releaseButton(int x, int y) {
        boolean res = false;
        ListIterator<Viewer> listIterator = viewerLinkedList.listIterator();
        PushButtonMsg pushButtonMsg = new PushButtonMsg();
        pushButtonMsg.pointerX = x;
        pushButtonMsg.pointerY = y;
        while (listIterator.hasNext()) {
            Viewer v = listIterator.next();
            if (v.releaseButton(pushButtonMsg)) {
                res = true;
                break;
            }
        }
        return res;
    }

    public void doCenter(EditorViewer editorViewer) {
        int top = Integer.MAX_VALUE;
        int bottom = Integer.MIN_VALUE;
        int left = Integer.MAX_VALUE;
        int right = Integer.MIN_VALUE;
        ListIterator<Viewer> listIterator = viewerLinkedList.listIterator();
        while (listIterator.hasNext()) {
            Viewer v = listIterator.next();
            top = Math.min(top, v.getTop());
            bottom = Math.max(bottom, v.getBottom());
            left = Math.min(left, v.getLeft());
            right = Math.max(right, v.getRight());
        }
        int leftMargin = left - MIN_X;
        int topMargin = top - MIN_Y;
        int rightMargin = MAX_X - right;
        int bottomMargin = MAX_Y - bottom;
        int dx = (rightMargin - leftMargin) / 2;
        int dy = (bottomMargin - topMargin) / 2;
        listIterator = viewerLinkedList.listIterator();
        while (listIterator.hasNext()) {
            Viewer v = listIterator.next();
            v.move(editorViewer, dx, dy, Viewer.CHECK_INSIDE);
        }
        makeSnapshot();
    }

    public void setFaultPos() {
        ListIterator<Viewer> listIterator = viewerLinkedList.listIterator();
        while (listIterator.hasNext()) {
            Viewer v = listIterator.next();
            for (DeviceIOModel ioModel : faultViewers.keySet()) {
                FaultViewer fv = faultViewers.get(ioModel);
                if (!fv.positioned) {
                    if (v.setFaultPos(fv)) {
                        fv.positioned = true;
//                        return;
                    }
//                } else {
//                    System.out.println("positioned:"+fv.x+","+fv.y);
//                    System.out.flush();
                }
            }
        }
    }

    public int getMaxSimTime() {
        int maxSimTime = 0;
        ListIterator<Viewer> listIterator = viewerLinkedList.listIterator();
        while (listIterator.hasNext()) {
            Viewer v = listIterator.next();
            maxSimTime = v.getMaxSimTime(maxSimTime);
        }
        if (maxSimTime >= MAX_SIM_TIME) {
            maxSimTime = MAX_SIM_TIME;
        } else if (maxSimTime == 0) {
            maxSimTime = MAX_SIM_TIME;
        }
        return maxSimTime;
    }

    public void setWireColor(int netNumber, int level) {
        ListIterator<Viewer> listIterator = viewerLinkedList.listIterator();
        while (listIterator.hasNext()) {
            Viewer v = listIterator.next();
            if (v.getNetNumber() == netNumber) {
                v.setLevel(level);
            }
        }
    }

    public void displayWireLevel() {
        Integer netNumber = Event.changedNetsAndLevels.poll();
        while (netNumber != null) {
            Integer level = Event.changedNetsAndLevels.poll();
            if (level != null) {
                setWireColor(netNumber.intValue(), level.intValue());
            }
            netNumber = Event.changedNetsAndLevels.poll();
        }
    }

    public void setHolesUsed(BreadBoardViewer breadBoardViewer) {
        LinkedList<Viewer> corruptedList = null;
        ListIterator<Viewer> listIterator = viewerLinkedList.listIterator();
        while (listIterator.hasNext()) {
            Viewer v = listIterator.next();
            if (!v.setHolesUsed(breadBoardViewer)) {
                if (corruptedList == null) {
                    corruptedList = new LinkedList<>();
                }
                corruptedList.add(v);
            }
        }
        if (corruptedList != null) {
            listIterator = corruptedList.listIterator();
            while (listIterator.hasNext()) {
                Viewer v = listIterator.next();
                Log.i("ADD", "Restore error: left=" + Integer.toString(v.getLeft()) + " right=" + v.getRight() + " top=" + v.getTop() + " bottom=" + v.getBottom());
                viewerLinkedList.remove(v);
            }
        }
    }

    public void deleteAt(int x, int y, BreadBoardViewer breadBoardViewer) {
        ListIterator<Viewer> listIterator = viewerLinkedList.listIterator();
        Viewer found = null;
        while (listIterator.hasNext()) {
            Viewer v = listIterator.next();
            found = v.deleteAt(x, y, breadBoardViewer);
            if (found != null && (found instanceof BBWireViewer ||
                    found instanceof BBTwoTerminalViewer)) {
                selections.unselect(found);
//                found.cutNet(this);
                viewerLinkedList.remove(found);
                break;
            }
        }
    }

    public void selectWireAt(int x, int y, BreadBoardViewer breadBoardViewer) {
        ListIterator<Viewer> listIterator = viewerLinkedList.listIterator();
        Viewer found = null;
        while (listIterator.hasNext()) {
            Viewer v = listIterator.next();
            found = v.selectAt(x, y, breadBoardViewer);
            if (found != null && (found instanceof BBWireViewer ||
                    found instanceof BBTwoTerminalViewer)) {
                selections.invertSelection(found);
//                found.cutNet(this);
                break;
            }
        }
    }

    public void powerOff() {
        ListIterator<Viewer> listIterator = viewerLinkedList.listIterator();
        int result = 0;
        while (listIterator.hasNext()) {
            Viewer v = listIterator.next();
            v.powerOff();
        }
    }

    public Viewer findName(String name) {
        ListIterator<Viewer> listIterator = viewerLinkedList.listIterator();
        while (listIterator.hasNext()) {
            Viewer v = listIterator.next();
            if (v.isName(name)) {
                return v;
            }
        }
        return null;
    }

    public int getNumberOfChannels() {
        return numberOfChannels;
    }

    public int getNumberOfPorts() {
        int numberOfPorts = 0;
        ListIterator<Viewer> listIterator = viewerLinkedList.listIterator();
        while (listIterator.hasNext()) {
            Viewer v = listIterator.next();
            String id = v.getPortLabel();
            if (id != null) {
                try {
                    numberOfPorts = Math.max(numberOfPorts, Integer.parseInt(id));
                } catch (NumberFormatException nfe) {
                    System.out.println("NumberFormatException: " + nfe.getMessage());
                }
            }
        }
        return numberOfPorts;
    }

    public void registrateAllBut(int time, DeviceModel excluded) {
        ListIterator<Viewer> listIterator = viewerLinkedList.listIterator();
        while (listIterator.hasNext()) {
            Viewer v = listIterator.next();
            if (v instanceof DeviceViewer) {
                if (((DeviceViewer) v).model != null) {
                    if (((DeviceViewer) v).model != excluded) {
                        v.registrateModelForChange(time);
                    }
                }
            }
        }
    }

    public String getPortAnnotation() {
        ListIterator<Viewer> listIterator = viewerLinkedList.listIterator();
        while (listIterator.hasNext()) {
            Viewer v = listIterator.next();
            if (v instanceof AnnotationViewer) {
                String s = ((AnnotationViewer) v).getLabel();
                if (s.startsWith(".")) {
                    return s;
                }
            }
        }
        return null;
    }
//
//    public void append(FaultViewer fv) {
//        ListIterator<Viewer> listIterator = viewerLinkedList.listIterator();
//        while (listIterator.hasNext()) {
//            Viewer v = listIterator.next();
//            if (v.appendFaultViewer(fv)) {
//                if (!faultViewerLinkedList.contains(fv)) {
//                    faultViewerLinkedList.add(fv);
//                    return;
//                }
//            }
//        }
//    }
}
