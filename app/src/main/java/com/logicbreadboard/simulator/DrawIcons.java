package com.logicbreadboard.simulator;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.Environment;

import java.io.File;
import java.io.FileOutputStream;

/**
 * Created on 6/20/15.
 */
public class DrawIcons {

    //    static final float Y_OFFSET = 20 + GateViewer.GATE_HEIGHT + GateViewer.GATE_HEIGHT / 2;
    static final float X_OFFSET = 20 + GateViewer.GATE_WIDTH + GateViewer.GATE_WIDTH / 2;
    //   final int ANGLE_OFFSET = 180;
    //    int[] angles = {0, 45, 90, 135, 180, 225, 270, 315};
    final static int[] angles = {0, 90, 180, 270};
    static final float ZOOM = 2.0f;
    static int H = 72 + 2 * DeviceIOViewer.LENGTH;
    static int W = 48 + 2 * DeviceIOViewer.LENGTH;

    static private void addOne(DeviceViewer v, float zoom) {
        if (v == null) {
            return;
        }
//        DeviceViewer v = (DeviceViewer) c.newInstance();
        W = v.getWidth() + 2 * DeviceIOViewer.LENGTH;
        H = v.getHeight() + 2 * DeviceIOViewer.LENGTH;
        int xp = (W - v.getWidth()) / 2;
        int yp = H - (H - v.getHeight()) / 2;
        v.initPosition(xp, yp, 0);
        v.setDefaultNumberOfIos();
        DeviceIOViewer io = v.firstIoViewer;
        while (io != null) {
            io.locked = true;
            io = io.next;
        }
        Bitmap bitmap = Bitmap.createBitmap((int) (zoom * (W)),
                (int) (zoom * (H)), Bitmap.Config.ARGB_4444);
        Canvas canvas = new Canvas(bitmap);
        canvas.setBitmap(bitmap);
        canvas.scale(zoom, zoom);
        v.draw(canvas);
        String filename = "ansi_" + v.getClass().getSimpleName().toLowerCase() + ".png";
//        filename = filename.replace("com.logicbreadboard.simulator.", "ansi_");
        filename = filename.replace("ansiviewer", "");
        File sd = new File(Settings.defaultDirectory + "/icons");
        boolean success = true;
        if (!sd.exists()) {
            success = sd.mkdir();
        }
        File dest;
        dest = new File(sd, filename);
//       File sd = Environment.getExternalStorageDirectory();
        // dest = new File(sd, filename);

//Bitmap bitmap = (Bitmap)data.getExtras().get("data");
        try {
            FileOutputStream out = new FileOutputStream(dest);
            bitmap.compress(Bitmap.CompressFormat.PNG, 50, out);
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    static void draw() {
        Viewer v;
        for (SchematicViewerFactory.DeviceGroup group : SchematicViewerFactory.DeviceGroup.values()) {
            v = null;
            switch (group) {
                case Gates:
                    for (SchematicViewerFactory.Gates sym : SchematicViewerFactory.Gates.values()) {
                        v = ViewerFactory.factory.newViewer(group.ordinal(), sym.ordinal());
                        addOne((DeviceViewer) v, ZOOM);
                    }
                    break;
                case Flip_Flops:
                    for (SchematicViewerFactory.Flip_Flops sym : SchematicViewerFactory.Flip_Flops.values()) {
                        v = ViewerFactory.factory.newViewer(group.ordinal(), sym.ordinal());
                        addOne((DeviceViewer) v, ZOOM);
                    }
                    break;
                case Wires:
//                for (ViewerFactory.Gates sym: ViewerFactory.Gates.values()) {
//                    v = ViewerFactory.newViewer(group.ordinal(), sym.ordinal());
//                }
                    break;
                case Connections:
                    for (SchematicViewerFactory.Connections sym : SchematicViewerFactory.Connections.values()) {
                        v = ViewerFactory.factory.newViewer(group.ordinal(), sym.ordinal());
                        addOne((DeviceViewer) v, ZOOM);
                    }
                    break;
                case Displays:
                    for (SchematicViewerFactory.Displays sym : SchematicViewerFactory.Displays.values()) {
                        v = ViewerFactory.factory.newViewer(group.ordinal(), sym.ordinal());
                        addOne((DeviceViewer) v, ZOOM);
                    }
                    break;
                case Sources:
                    for (SchematicViewerFactory.Sources sym : SchematicViewerFactory.Sources.values()) {
                        v = ViewerFactory.factory.newViewer(group.ordinal(), sym.ordinal());
                        addOne((DeviceViewer) v, ZOOM);
                    }
                    break;
                case Mux:
                    for (SchematicViewerFactory.Mux sym : SchematicViewerFactory.Mux.values()) {
                        v = ViewerFactory.factory.newViewer(group.ordinal(), sym.ordinal());
                        addOne((DeviceViewer) v, ZOOM);
                    }
                    break;
                case Demux:
                    for (SchematicViewerFactory.Demux sym : SchematicViewerFactory.Demux.values()) {
                        v = ViewerFactory.factory.newViewer(group.ordinal(), sym.ordinal());
                        addOne((DeviceViewer) v, ZOOM);
                    }
                    break;
                case Box:
                    for (SchematicViewerFactory.Box sym : SchematicViewerFactory.Box.values()) {
                        v = ViewerFactory.factory.newViewer(group.ordinal(), sym.ordinal());
                        addOne((DeviceViewer) v, ZOOM);
                    }
                    break;
                case Transistors:
                    for (SchematicViewerFactory.Transistors sym : SchematicViewerFactory.Transistors.values()) {
                        v = ViewerFactory.factory.newViewer(group.ordinal(), sym.ordinal());
                        addOne((DeviceViewer) v, ZOOM);
                    }
                    break;
//            case Counters:
//                break;
//            case Blocks:
//                break;
//            case Series_7400:
//                break;
//            case Series_4000:
//                break;
                default:
            }
        }
        MainActivity.EditorMode sm = MainActivity.editorMode;
        MainActivity.editorMode = MainActivity.EditorMode.SCH;
        for (BBViewerFactory.BBDeviceGroup group : BBViewerFactory.BBDeviceGroup.values()) {
            v = null;
            switch (group) {
                case Switches:
                    for (BBViewerFactory.BB_Switch sym : BBViewerFactory.BB_Switch.values()) {
                        v = BBViewerFactory.factory.newViewer(group.ordinal(), sym.ordinal());
                        addOne((DeviceViewer) v, ZOOM);
                    }
                    break;
//            case Counters:
//                break;
//            case Blocks:
//                break;
//            case Series_7400:
//                break;
//            case Series_4000:
//                break;
                default:
            }
        }
        MainActivity.editorMode = sm;
    }
}
