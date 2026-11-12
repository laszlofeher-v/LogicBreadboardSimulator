package com.logicbreadboard.simulator;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.util.Log;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created on 6/6/15.
 */
public class NativeSaveLoad {
    private static final boolean ENABLE_ZOOMPOS = true;
    private static final boolean DISABLE_ZOOMPOS = false;
    static private int inputVersion;
    SchematicViewer schematicViever;

    NativeSaveLoad(SchematicViewer sv) {
        schematicViever = sv;
    }

    //
    // symbolType position orientation numberOfIos {net delay}
    // digital signature
    // wire
    // bus
    static int getInputVersion() {
        return inputVersion;
    }

    public static void saveSchematic(String fileName, EditorModel model) throws java.io.IOException {
        FileOutputStream fos = null;
        File file = new File(fileName);
        if (file.exists()) {
            file.renameTo(new File(fileName + ".bak"));
        }
        if (model.size() > 0) {
            try {
                fos = new FileOutputStream(fileName);
                DataOutputStream out = new DataOutputStream(fos);
                int versionCode = MainActivity.context.getPackageManager().getPackageInfo
                        (MainActivity.context.getPackageName(), 0).versionCode;
                out.writeInt(versionCode);
                out.writeInt(EditorModel.MAX_X);
                out.writeInt(EditorModel.MAX_Y);
//                NetNumbers.save(out);
                model.save(out);
                out.flush();
                out.close();
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            } finally {
                if (fos != null) {
                    fos.flush();
                    fos.close();
                    file = new File(fileName);
//                    System.out.println("saved size:" + file.length());
//                    System.out.flush();
                }
            }
        } else {
//            System.out.println("##################### model length=0 ########################");
//            System.out.flush();
        }
    }

    public static void saveBreadboard(String fileName, EditorModel model, int bbId) throws java.io.IOException {
        FileOutputStream fos = null;
        File file = new File(fileName);
        if (file.exists()) {
            if (file.length() == 0) {
                file.renameTo(new File(fileName + ".0"));
            } else {
                file.renameTo(new File(fileName + ".bak"));
            }
        }
        if (model.size() > 0) {
            try {
                fos = new FileOutputStream(fileName);
                DataOutputStream out = new DataOutputStream(fos);
                int versionCode = MainActivity.context.getPackageManager().getPackageInfo
                        (MainActivity.context.getPackageName(), 0).versionCode;
                out.writeInt(versionCode);
                out.writeInt(0);
                out.writeInt(bbId);
//                NetNumbers.save(out);
                model.save(out);
                out.flush();
                out.close();
                Log.i("saveBreadboard", "out flushed closed");
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            } finally {
                if (fos != null) {
                    fos.flush();
                    fos.close();
                    Log.i("saveBreadboard", "fos flushed closed");
//                    file = new File(fileName);
//                    System.out.println("saved size:" + file.length());
//                    System.out.flush();
                }
            }
        } else {
//            System.out.println("##################### model length=0 ########################");
//            System.out.flush();
        }
    }

    private static void loadSchematic(String fileName, EditorModel model, boolean enableZoomPos, EditorViewer editorViewer) throws java.io.IOException {
        FileInputStream fis = null;
        model.deleteAll();
        try {
            fis = new FileInputStream(fileName);
            DataInputStream in = new DataInputStream(fis);
            inputVersion = in.readInt();
            int maxX = in.readInt();
            int maxY = in.readInt();
            if (maxX > EditorModel.MAX_X || maxY > EditorModel.MAX_Y) {
                //// TODO: 8/19/15 error
            }
            while (in.available() > 0) {
                short group = in.readShort();
                short sym = in.readShort();
//                System.out.println("sym=" + sym);
                if (group != SchematicViewerFactory.META_VIEWER_GROUP) {
                    Viewer v = ViewerFactory.schematicViewerFactory.newViewer(group, sym);
                    if (v != null) {
                        v.load(in);
                        model.add(v);
                    } else {
                        break;
                    }
                } else {
                    if (sym == SchematicViewerFactory.ZOOMPOS_VIEWER_SYM) {
                        float z = in.readFloat();
                        float dx = in.readFloat();
                        float dy = in.readFloat();
                        if (enableZoomPos && editorViewer != null) {
                            editorViewer.xOffset = dx;
                            editorViewer.yOffset = dy;
                            editorViewer.scaleFactor = z;
                        }
                    } else if (sym == SchematicViewerFactory.FILENAME_VIEWER_SYM) {
                        String name = in.readUTF();
                        if (Settings.getProjectFileName().equals(Settings.getTempDownloadFilePath())) {
                            if (name != null && name.length() > 0) {
                                Settings.setProjectFileName(Settings.defaultDirectory + "/" + name);
                            }
                        }
                    }
                }
            }
        } finally {
            if (fis != null) {
                fis.close();
            }
        }
    }

    private static void loadSchematic(InputStream fis, EditorModel model, boolean enableZoomPos, EditorViewer editorViewer) throws java.io.IOException {
        model.deleteAll();
        try {
            DataInputStream in = new DataInputStream(fis);
            inputVersion = in.readInt();
            int maxX = in.readInt();
            int maxY = in.readInt();
            if (maxX > EditorModel.MAX_X || maxY > EditorModel.MAX_Y) {
                //// TODO: 8/19/15 error
            }
            while (in.available() > 0) {
                short group = in.readShort();
                short sym = in.readShort();
//                System.out.println("sym=" + sym);
                if (group != SchematicViewerFactory.META_VIEWER_GROUP) {
                    Viewer v = ViewerFactory.schematicViewerFactory.newViewer(group, sym);
                    if (v != null) {
                        v.load(in);
                        model.add(v);
                    } else {
                        break;
                    }
                } else {
                    if (sym == SchematicViewerFactory.ZOOMPOS_VIEWER_SYM) {
                        float z = in.readFloat();
                        float dx = in.readFloat();
                        float dy = in.readFloat();
                        if (enableZoomPos && editorViewer != null) {
                            editorViewer.xOffset = dx;
                            editorViewer.yOffset = dy;
                            editorViewer.scaleFactor = z;
                        }
                    } else if (sym == SchematicViewerFactory.FILENAME_VIEWER_SYM) {
                        String name = in.readUTF();
                        if (Settings.getProjectFileName().equals(Settings.getTempDownloadFilePath())) {
                            if (name != null && name.length() > 0) {
                                Settings.setProjectFileName(Settings.defaultDirectory + "/" + name);
                            }
                        }
                    }
                }
            }
        } finally {
            if (fis != null) {
                fis.close();
            }
        }
    }

    private static void loadBreadboard(String fileName, EditorModel model, boolean enableZoomPos, EditorViewer editorViewer) throws java.io.IOException {
        FileInputStream fis = null;
        model.deleteAll();
        try {
            fis = new FileInputStream(fileName);
            DataInputStream in = new DataInputStream(fis);
            inputVersion = in.readInt();
            int maxX = in.readInt();
            int maxY = in.readInt();
            if (maxX > EditorModel.MAX_X || maxY > EditorModel.MAX_Y) {
                //// TODO: 8/19/15 error
            }
            while (in.available() > 0) {
                short group = in.readShort();
                short sym = in.readShort();
//                System.out.println("sym=" + sym);
                if (group != SchematicViewerFactory.META_VIEWER_GROUP) {
                    Viewer v = ViewerFactory.factory.newViewer(group, sym);
                    if (v != null) {
                        v.load(in);
                        model.add(v);
                    } else {
                        break;
                    }
                } else {
                    if (sym == SchematicViewerFactory.ZOOMPOS_VIEWER_SYM) {
                        float z = in.readFloat();
                        float dx = in.readFloat();
                        float dy = in.readFloat();
                        if (enableZoomPos && editorViewer != null) {
                            editorViewer.xOffset = dx;
                            editorViewer.yOffset = dy;
                            editorViewer.scaleFactor = z;
                        }
                    } else if (sym == SchematicViewerFactory.FILENAME_VIEWER_SYM) {
                        String name = in.readUTF();
                        if (Settings.getProjectFileName().equals(Settings.getTempDownloadFilePath())) {
                            if (name != null && name.length() > 0) {
                                Settings.setProjectFileName(Settings.defaultDirectory + "/" + name);
                            }
                        }
                    }
                }
            }
        } finally {
            if (fis != null) {
                fis.close();
            }
        }
    }

    static String mainFileName;

    public static void loadSchematicMain(String fileName, EditorModel model, EditorViewer editorViewer) {
        mainFileName = new String(fileName);
        NetNumbers.reset();
        try {
            loadSchematic(fileName, model, ENABLE_ZOOMPOS, editorViewer);
        } catch (IOException e) {
            e.printStackTrace();
        }
        model.compressNetNumbers();
    }

    public static void loadBreadboardMain(String fileName, EditorModel model, EditorViewer editorViewer) {
        mainFileName = new String(fileName);
        NetNumbers.reset();
        try {
            loadBreadboard(fileName, model, ENABLE_ZOOMPOS, editorViewer);
        } catch (IOException e) {
            e.printStackTrace();
        }
        model.compressNetNumbers();
    }

    public static boolean loadSub(String fileName, EditorModel model) throws java.io.IOException {
        if (mainFileName == null) {
            return false;
        }
        if (fileName.equals(mainFileName)) {
            AlertDialog.Builder alert;
            alert = new AlertDialog.Builder(MainActivity.context);
            alert.setTitle("Recursive inheritance detected. File not loaded");
            alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                }
            });
            alert.show();
            return false;
        }
        loadSchematic(fileName, model, DISABLE_ZOOMPOS, null);
        return true;
    }

    public static boolean loadSub(String fileName, InputStream fis, EditorModel model) throws java.io.IOException {
        if (mainFileName == null) {
            return false;
        }
        if (fileName.equals(mainFileName)) {
            AlertDialog.Builder alert;
            alert = new AlertDialog.Builder(MainActivity.context);
            alert.setTitle("Recursive inheritance detected. File not loaded");
            alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                }
            });
            alert.show();
            return false;
        }
        loadSchematic(fis, model, DISABLE_ZOOMPOS, null);
        return true;
    }

}