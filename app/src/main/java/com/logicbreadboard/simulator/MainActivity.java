package com.logicbreadboard.simulator;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.AssetManager;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;

import android.os.Environment;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Build;
import android.view.Display;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;
import android.widget.ZoomButtonsController;
import android.widget.ZoomControls;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;


public class MainActivity extends AppCompatActivity {
    //    static final int SCHEMATIC_EDITOR = 0;
//    static final int BREADBOARD_EDITOR = 1;
//    static final int PCB_EDITOR = 2;
//    static final int VHDL_EDITOR = 3;
//    static final int VERILOG_EDITOR = 4;
    static final String EDITOR_TYPE = "EDITOR_TYPE";
    final static private String eulaText =
            " THERE IS NO WARRANTY FOR THE PROGRAM, TO THE EXTENT PERMITTED BY APPLICABLE LAW. " +
                    "EXCEPT WHEN OTHERWISE STATED IN WRITING THE COPYRIGHT HOLDERS AND/OR OTHER " +
                    "PARTIES PROVIDE THE PROGRAM \"AS IS\" WITHOUT WARRANTY OF ANY KIND, EITHER " +
                    "EXPRESSED OR IMPLIED, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES" +
                    " OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE. THE ENTIRE RISK " +
                    "AS TO THE QUALITY AND PERFORMANCE OF THE PROGRAM IS WITH YOU. SHOULD THE " +
                    "PROGRAM PROVE DEFECTIVE, YOU ASSUME THE COST OF ALL NECESSARY SERVICING, " +
                    "REPAIR OR CORRECTION.";

    static Context context;
    static public EditorViewer editorViewer;
    //    int editorType;
    FileBrowser fileBrowser;
    float savedOrDefaultScaleFactor;
    int savedOrDefaultXOffset;
    int savedOrDefaultYOffset;
    private MenuItem ffInitMenuItem = null;
    private WebView myWebView;
    private String previousProjectFileName;

    public static enum EditorMode {SCH, BB}

    public static EditorMode editorMode;
    private boolean downloadReceiverRegistered = false;
    BroadcastReceiver onComplete = new BroadcastReceiver() {
        public void onReceive(Context ctxt, Intent intent) {
            newSchematic();
            String fileName = Settings.getTempDownloadFilePath();
            Settings.setProjectFileName(fileName);
            newSchematicOrBreadboard(fileName);
            editorViewer.restore(fileName);
            editorViewer.invalidate();
            writeTitle(Settings.getProjectFileName());
            Settings.enableSave();
            MenuStateMachine.gotoEditing();
            if (downloadReceiverRegistered) {
                try {
                    unregisterReceiver(onComplete);
                } catch (IllegalArgumentException ignored) {
                }
                downloadReceiverRegistered = false;
            }
        }
    };

    public class MyWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            Log.i("CLIENT", url);
            Uri uri = Uri.parse(url);
            String lastPathSegment = uri.getLastPathSegment();
//            getApplicationContext().getContentResolver().query(uri, null, null, null, null);
            if (lastPathSegment != null && lastPathSegment.length() > 0 && lastPathSegment.equals("uc")) {
                Log.i("CLIENT", uri.getEncodedPath());
                Log.i("CLIENT", lastPathSegment);
                DownloadManager.Request request = new DownloadManager.Request(uri);
                ContextCompat.registerReceiver(MainActivity.this, onComplete, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE), ContextCompat.RECEIVER_EXPORTED);   // appears the same in Notification bar while downloading
                downloadReceiverRegistered = true;
//                request.setDescription("Description for the DownloadManager Bar");
//                request.setTitle("YourApp.apk");
                try {
                    String fileName = Settings.getTempDownloadFilePath();
                    new File(fileName).delete();
                } catch (Exception e) {
                    Log.e("tag", e.getMessage());
                }
                request.setDestinationInExternalFilesDir(MainActivity.this, "Downloads", "tmp.tmp");
                // get download service and enqueue file
                DownloadManager manager = (DownloadManager) MainActivity.this.getSystemService(Context.DOWNLOAD_SERVICE);
                manager.enqueue(request);
            } else {
                view.loadUrl(url);
            }
            return true;
        }
    }

    public void runWebView() {
        editorViewer.save(Settings.getProjectFileName());
        myWebView = new MyWebView(this);
        myWebView.getSettings().setSupportZoom(true);
        myWebView.getSettings().setBuiltInZoomControls(true);
        myWebView.getSettings().setDisplayZoomControls(false);
        ((Activity) context).setContentView(myWebView);
        editorViewer.gotoWeb();
        myWebView.getSettings().setJavaScriptEnabled(true);
        myWebView.setWebViewClient(new MyWebViewClient());
        myWebView.loadUrl("https://github.com/laszlofeher-v/LogicBreadboardSimulator");
//        myWebView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
//        myWebView.setDownloadListener(new DownloadListener() {
//            public void onDownloadStart(String url, String userAgent,
//                                        String contentDisposition, String mimetype,
//                                        long contentLength) {
//                Log.i("CLIENT", "??????????????????????????????????????????????????????????");
//                Uri uri = Uri.parse(url);
//                Intent intent = new Intent(Intent.ACTION_VIEW,uri);
//                startActivity(intent);            }
//        });
//        myWebView.setDownloadListener(new DownloadListener() {
//            public void onDownloadStart(String url, String userAgent,
//                                        String contentDisposition, String mimetype,
//                                        long contentLength) {
//                Uri uri = Uri.parse(url);
//                Log.i("runWebView", "download");
//                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
//                startActivity(intent);
//            }
//        });
    }

    void copydemo() {
        if (Settings.createDefaultDirIfNotExists(Settings.defaultDirectory) == false) {
            new AlertDialog.Builder(this)
                    .setTitle("Error")
                    .setMessage("Failed to create " + Settings.defaultDirectory +
                            " folder. Try \"Save as\" instead of \"Save\"")
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // continue with delete
                        }
                    })
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
            Settings.defaultDirectory = "/";
        }
        if (Settings.createDefaultDirIfNotExists(Settings.defaultDirectory + "/demos") == false) {
            new AlertDialog.Builder(this)
                    .setTitle("Error")
                    .setMessage("Failed to create demos directory:" + Settings.defaultDirectory + "/demos")
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // continue with delete
                        }
                    })
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
            Settings.defaultDirectory = "/";
        }
        if (Settings.createDefaultDirIfNotExists(Settings.defaultDirectory + "/Downloads") == false) {
            new AlertDialog.Builder(this)
                    .setTitle("Error")
                    .setMessage("Failed to create demos directory:" + Settings.defaultDirectory + "/Downloads")
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // continue with delete
                        }
                    })
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
            Settings.defaultDirectory = "/";
        }
        String[] list = null;
        try {
            list = MainActivity.assetManager.list("demos");
        } catch (IOException e) {
        }
        if (list == null) {
            return;
        }
        int i = 0;
        while (i < list.length) {
            InputStream in; //= getResources().openRawResource(Settings.raws[i]);
//            String path = Settings.defaultDirectory + "/demos/" + Settings.DEMO_FILENAMES[i++];
            String path = Settings.defaultDirectory + "/demos/" + list[i];
            try {
                in = MainActivity.assetManager.open("demos/" + list[i]);

            } catch (IOException ex) {
                ex.printStackTrace();
                return;
            }
            i++;
            File file = new File(path);
            if (!(in == null || file == null || (file != null && file.exists()))) {
                FileOutputStream out = null;
                try {
                    out = new FileOutputStream(path);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                if (out == null) {
                    return;
                }
                byte[] buff = new byte[1024];
                int read = 0;

                try {
                    while ((read = in.read(buff)) > 0) {
                        out.write(buff, 0, read);
                    }
                    in.close();
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    protected void onPause() {
        super.onPause();
        if (editorViewer.model.enableScope) {
            editorViewer.restoreAfterScope();
            editorViewer.model.enableScope = false;
        }
        editorViewer.save(Settings.getProjectFileName());
        Settings.saveSettings(this);
        Log.i("onPause", "saved");
    }

    protected void onStop() {
        super.onStop();
        if (editorViewer.model.enableScope) {
            editorViewer.restoreAfterScope();
            editorViewer.model.enableScope = false;
        }
        editorViewer.save(Settings.getProjectFileName());
        Settings.saveSettings(this);
        Log.i("onStop", "saved");
    }

    @Override
    protected void onDestroy() {
        if (downloadReceiverRegistered) {
            try {
                unregisterReceiver(onComplete);
            } catch (IllegalArgumentException ignored) {
            }
            downloadReceiverRegistered = false;
        }
        super.onDestroy();
    }

    private void eula() {
        if (!Settings.getEulaAccepted()) {
            new AlertDialog.Builder(this)
                    .setTitle("EULA")
                    .setMessage(eulaText)
                    .setPositiveButton("Accept", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            Settings.setEulaAccepted(true);
                        }
                    })
                    .setNegativeButton("Decline", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            Settings.setEulaAccepted(false);
                            finish();
                        }
                    })
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setCancelable(false)
                    .show();
        }
    }

    public static Tracker mTracker;
    public static AssetManager assetManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Obtain the shared Tracker instance.
        AnalyticsApplication application = (AnalyticsApplication) getApplication();
        mTracker = application.getDefaultTracker();
        boolean isDebuggable = isDebuggable();
        if (isDebuggable) {
            GoogleAnalytics.getInstance(this).setAppOptOut(true);
        }
// changes real time event collection from default 30 minutes to 15 sec        GoogleAnalytics.getInstance(this).setLocalDispatchPeriod(15);
        int measuredWidth = 0;
        int measuredHeight = 0;
        assetManager = getAssets();
        Settings.initDirectories(this);
        copydemo();
        WindowManager w = getWindowManager();
        Point size = new Point();
        w.getDefaultDisplay().getSize(size);
        measuredWidth = size.x;
        measuredHeight = size.y;
        Settings.defaultXOffset = Math.round((measuredWidth - EditorModel.MAX_X - EditorModel
                .MIN_X) / 2);
        Settings.defaultYOffset = Math.round((measuredHeight - EditorModel.MAX_Y - EditorModel
                .MIN_Y) / 2);
        Settings.loadSettings(this);
        eula();
//        NetNumbers.reset();
//        editorType = SCHEMATIC_EDITOR;
//        newSchematicOrBreadboard(Settings.getProjectFileName());
//        if (Settings.getProjectFileName().endsWith(".s")) {
//            newSchematic();
//        } else if (Settings.getProjectFileName().endsWith(".b")) {
//            // TODO: 2/21/16   breadboard type in file uniqueName extension
//            newBreadBoard();
//        } else {
//            newSchematic();
//        }
//        pcTask = new DisplayClientSSL();
//        pcTask.execute();
//         String s = stringFromJNI(10,20);
//        System.out.println(s);
    }

//    public native String stringFromJNI(int x, int y);


    static {
        System.loadLibrary("crouter-jni");
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (Settings.createDefaultDirIfNotExists(Settings.defaultDirectory) == false) {
            new AlertDialog.Builder(this)
                    .setTitle("Error")
                    .setMessage("Failed to create " + Settings.defaultDirectory +
                            " folder. Try \"Save as\" instead of \"Save\"")
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // continue with delete
                        }
                    })
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
            Settings.defaultDirectory = "/";
        }
        String path = Settings.getProjectFileName();
        MainActivity.context = MainActivity.this;//getApplicationContext();
//        Settings.setLayoutType(this);
        NetNumbers.reset();
        newSchematicOrBreadboard(path);
//        if (Settings.getProjectFileName().endsWith(".s")) {
//            newSchematic();
//        } else if (Settings.getProjectFileName().endsWith(".b")) {
//            newBreadBoard();
//        } else {
//            newSchematic();
//        }
        editorViewer.restore(path);
        editorViewer.invalidate();
        writeTitle(path);
        setOrientation(Settings.getOrientation());
    }

    private void newSchematicOrBreadboard(String path) {
        FileInputStream fis = null;
        int maxX = 0;
        int maxY;
        try {
            fis = new FileInputStream(path);
            DataInputStream in = new DataInputStream(fis);
            int inputVersion = in.readInt();
            maxX = in.readInt();
            maxY = in.readInt();
            if (maxX > 0) {
                newSchematic();
            } else {
                newBreadBoard();
            }
        } catch (Exception ex) {
//            fallBackBreadBoard();
            ex.printStackTrace();
        }
        if (fis != null) {
            try {
                fis.close();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        if (editorViewer == null) {
            if (maxX > 0) {
                newSchematic();
            } else {
                newBreadBoard();
            }
        }
    }

    void setColorTheme() {
        switch (Settings.colorTheme) {
            case Settings.WHITE_ON_BLACK:
                editorViewer.setBackgroundColor(Color.BLACK);
                Viewer.Context.setWhiteOnBlack();
                break;
            case Settings.BLACK_ON_WHITE:
                editorViewer.setBackgroundColor(Color.WHITE);
                Viewer.Context.setBlackOnWhite();
                break;
            case Settings.BLACK_ON_GREEN:
                editorViewer.setBackgroundColor(0xFFBBFFBB);
                Viewer.Context.setBlackOnGreen();
                break;
            case Settings.BLACK_ON_SILVER:
                editorViewer.setBackgroundColor(0xFFc0c0c0);
                Viewer.Context.setBlackOnSilver();
                break;
            default:
        }
    }

    public void scopeButtonClicked(View v) {
//        EditorModel eModel = editorViewer.model;
        LinearLayout schematicButtonLayout = (LinearLayout) findViewById(R.id.schematic_buttons);
        schematicButtonLayout.setVisibility(View.GONE);
        LinearLayout simulatorButtonLayout = (LinearLayout) findViewById(R.id.simulator_buttons);
        simulatorButtonLayout.setVisibility(View.GONE);
        LinearLayout scopeButtonLayout = (LinearLayout) findViewById(R.id.scope_buttons);
        scopeButtonLayout.setVisibility(View.VISIBLE);
        editorViewer.invalidate();
        editorViewer.scopeButtonHandler();
        setContextMenuVisibility();
    }

    public void scopeBackButtonClicked(View v) {
        LinearLayout scopeButtonLayout = (LinearLayout) findViewById(R.id.scope_buttons);
        scopeButtonLayout.setVisibility(View.GONE);
        if (editorViewer.model.simulationIsRunning) {
            LinearLayout simulatorButtonLayout = (LinearLayout) findViewById(R.id.simulator_buttons);
            simulatorButtonLayout.setVisibility(View.VISIBLE);
        } else {
            LinearLayout schematicButtonLayout = (LinearLayout) findViewById(R.id.schematic_buttons);
            schematicButtonLayout.setVisibility(View.VISIBLE);
        }
        editorViewer.invalidate();
        editorViewer.restoreAfterScope();
        setContextMenuVisibility();
    }

    public void showNetClicked(View v) {
        editorViewer.showNet();
        editorViewer.invalidate();
    }

    public void lockClicked(View v) {
        editorViewer.lockAction();
        setContextMenuVisibility();
        editorViewer.invalidate();
    }

    public void labelClicked(View v) {
        editorViewer.editLabel();
        editorViewer.invalidate();
    }

    public void includeClicked(View v) {
        editorViewer.includeFile();
        editorViewer.invalidate();
    }

    public void alignHorizontalClicked(View v) {
        editorViewer.alignHorizontal();
        editorViewer.invalidate();
    }

    public void alignVerticalClicked(View v) {
        editorViewer.alignVertical();
        editorViewer.invalidate();
    }

    public void timeClicked(View v) {
        editorViewer.setDelay();
        editorViewer.invalidate();
    }

    public void addInputClicked(View v) {
        editorViewer.addInput(editorViewer);
        editorViewer.invalidate();
    }

    public void removeInputClicked(View v) {
        editorViewer.removeInput(editorViewer);
        editorViewer.invalidate();
    }

    public void unlockClicked(View v) {
        editorViewer.unlockAction();
        setContextMenuVisibility();
        editorViewer.invalidate();
    }

    public void invertClicked(View v) {
        editorViewer.changeInvert();
        editorViewer.invalidate();
    }

    public void lToHClicked(View v) {
        editorViewer.lToH();
        editorViewer.invalidate();
    }

    public void hToLClicked(View v) {
        editorViewer.hToL();
        editorViewer.invalidate();
    }

    public void disableClicked(View v) {
        editorViewer.disable();
        editorViewer.invalidate();
    }

    public void copyClicked(View v) {
        editorViewer.copyAction();
        editorViewer.invalidate();
    }

    public void morphClicked(View view) {
        editorViewer.morphAction();
        editorViewer.invalidate();
    }


    public void rotateLeftClicked(View v) {
        editorViewer.rotateLeft();
        editorViewer.invalidate();
    }

    public void fineRotateLeftClicked(View v) {
        editorViewer.fineRotateLeft();
        editorViewer.invalidate();
    }

    public void rotateRightClicked(View v) {
        editorViewer.rotateRight();
        editorViewer.invalidate();
    }

    public void unzoomScopeButtonClicked(View v) {
        editorViewer.setScopeTimeZoom(0.5f);
        editorViewer.invalidate();
    }

    public void zoomScopeButtonClicked(View v) {
        editorViewer.setScopeTimeZoom(2f);
        editorViewer.invalidate();
    }

    public void breadBoardAddButtonClicked(View v) {
        MenuStateMachine.gotoBBDeviceGroupSelection();
        View vv = findViewById(R.id.breadboard_canvas);
        BBDeviceSelectionMenu.displayDeviceMenu(this, vv);
    }

    public void addButtonClicked(View v) {
        MenuStateMachine.gotoDeviceGroupSelection();
        View vv = findViewById(R.id.schematic_canvas);
        DeviceSelectionMenu.displayDeviceMenu(this, vv);
    }

    public void runButtonClicked(View v) {
        LinearLayout schematicButtonLayout = (LinearLayout) findViewById(R.id.schematic_buttons);
        schematicButtonLayout.setVisibility(View.GONE);
        LinearLayout simulatorButtonLayout = (LinearLayout) findViewById(R.id.simulator_buttons);
//        editorViewer.scaleFactor = savedOrDefaultScaleFactor;
//        editorViewer.xOffset = savedOrDefaultXOffset;
//        editorViewer.yOffset = savedOrDefaultYOffset;
        editorViewer.invalidate();
        editorViewer.runButtonHandler();
        simulatorButtonLayout.setVisibility(View.VISIBLE);
        setContextMenuVisibility();
    }

    public void runFromScopeButtonClicked(View v) {
        editorViewer.runFromScopeButtonHandler();
    }

    public void editButtonClicked(View v) {
        LinearLayout simulatorButtonLayout = (LinearLayout) findViewById(R.id.simulator_buttons);
        simulatorButtonLayout.setVisibility(View.GONE);
        LinearLayout schematicButtonLayout = (LinearLayout) findViewById(R.id.schematic_buttons);
        schematicButtonLayout.setVisibility(View.VISIBLE);
//        editorViewer.scaleFactor = savedOrDefaultScaleFactor;
//        editorViewer.xOffset = savedOrDefaultXOffset;
//        editorViewer.yOffset = savedOrDefaultYOffset;
        editorViewer.stopSimulation();
        editorViewer.invalidate();
        setContextMenuVisibility();
    }

    public void deleteButtonClicked(View v) {
        editorViewer.deleteAction();
        setContextMenuVisibility();
        editorViewer.invalidate();
    }

    public void undoButtonClicked(View v) {
        editorViewer.undo();
        setContextMenuVisibility();
        editorViewer.invalidate();
    }

    public void redoButtonClicked(View v) {
        editorViewer.redo();
        setContextMenuVisibility();
        editorViewer.invalidate();
    }

    public void rPlusLedClicked(View v) {
        editorViewer.rPlusLed();
        setContextMenuVisibility();
        editorViewer.invalidate();
    }

    public void ledClicked(View v) {
        editorViewer.led();
        setContextMenuVisibility();
        editorViewer.invalidate();
    }

    public void rClicked(View v) {
        editorViewer.resistor();
        setContextMenuVisibility();
        editorViewer.invalidate();
    }

    public void cancelButtonClicked(View v) {
        editorViewer.model.selections.unselect();
        setContextMenuVisibility();
        editorViewer.invalidate();
    }

    public void helpButtonClicked(View v) {
        editorViewer.showOnlineHelp();
    }


    public void newSchematic() {
        MenuStateMachine.gotoEditing();
        setContentView(R.layout.schematic);
        ViewerFactory.factory = new SchematicViewerFactory();
        ViewerFactory.bbViewerFactory = new BBViewerFactory();
        ViewerFactory.schematicViewerFactory = new SchematicViewerFactory();
        DIPViewer.setRasterSize(5);
        _74_DIPViewer.setRasterSize(5);
        new ContextMenuIcons(this); // create icons from resource drawables
        setContentView(R.layout.schematic);
//        Settings.setLayout(this, R.layout.activity_schematic_bottom, R.id.schematic_zoomControl);
        editorViewer = (SchematicViewer) findViewById(R.id.schematic_canvas);
        editorMode = EditorMode.SCH;
        setColorTheme();
        editorViewer.init(this, new EditorModel());
        editorViewer.scaleFactor = savedOrDefaultScaleFactor;
        editorViewer.xOffset = savedOrDefaultXOffset;
        editorViewer.yOffset = savedOrDefaultYOffset;
//        ImageButton button = (ImageButton) (findViewById(R.id.button1));
//        if (button == null) {
//            System.out.println("No button found");
//        }
        Log.i("newSchematic", "Setting screen uniqueName: " + "newSchematic");
        mTracker.setScreenName("Image~" + "newSchematic");
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
        setContextMenuVisibility();
    }

    public void newBreadBoard() {
        MenuStateMachine.gotoBBEditing();
        ViewerFactory.factory = new BBViewerFactory();
        ViewerFactory.bbViewerFactory = new BBViewerFactory();
        ViewerFactory.schematicViewerFactory = new SchematicViewerFactory();
        DIPViewer.setRasterSize(16);
        _74_DIPViewer.setRasterSize(16);
        new ContextMenuIcons(this); // create icons from resource drawables
        setContentView(R.layout.breadboard);                         //breadboard_canvas
        editorViewer = (BreadBoardViewer) findViewById(R.id.breadboard_canvas);
        editorMode = EditorMode.BB;
        editorViewer.init(this, new EditorModel());
        editorViewer.addInterfaces();
        setColorTheme();
        editorViewer.scaleFactor = savedOrDefaultScaleFactor;
        editorViewer.xOffset = savedOrDefaultXOffset;
        editorViewer.yOffset = savedOrDefaultYOffset;
        Log.i("newBreadBoard", "Setting screen uniqueName: " + "newBreadBoard");
        mTracker.setScreenName("Image~" + "newBreadboard");
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
        setContextMenuVisibility();
    }

    void setLeftButtonVisibility(int id, boolean enable) {
        Button button = (Button) findViewById(id);
        if (button != null) {
            if (enable) {
                RelativeLayout.LayoutParams params = getLayoutParams(button);
                if (leftLength == 0) {
                    params.addRule(RelativeLayout.ALIGN_PARENT_TOP, RelativeLayout.TRUE);
                    params.addRule(RelativeLayout.ALIGN_PARENT_LEFT, RelativeLayout.TRUE);
                    leftBottomId = id;
                    leftLength++;
                } else if (leftLength < MAX_LEFT_LENGTH) {
                    params.addRule(RelativeLayout.BELOW, leftBottomId);
                    params.addRule(RelativeLayout.ALIGN_PARENT_LEFT, RelativeLayout.TRUE);
                    leftBottomId = id;
                    leftLength++;
                } else {
                    setRightButtonVisibility(id, enable);
                }
            } else {
                button.setVisibility(View.GONE);
            }
        } else {
//            Log.i("LBUTTON_NULL", "id=" + id);
        }
    }

    @NonNull
    private RelativeLayout.LayoutParams getLayoutParams(Button button) {
        button.setVisibility(View.VISIBLE);
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) button.getLayoutParams();
        params.addRule(RelativeLayout.BELOW, 0);
        params.addRule(RelativeLayout.ALIGN_PARENT_LEFT, 0);
        params.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0);
        params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, 0);
        params.addRule(RelativeLayout.CENTER_HORIZONTAL, 0);
        return params;
    }

    void setMiddleButtonVisibility(int id, boolean enable) {
        Button button = (Button) findViewById(id);
        if (button != null) {
            if (enable) {
                RelativeLayout.LayoutParams params = getLayoutParams(button);
                if (middleLength == 0) {
                    params.addRule(RelativeLayout.ALIGN_PARENT_TOP, RelativeLayout.TRUE);
                    params.addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE);
                    middleBottomId = id;
                    middleLength++;
                } else if (middleLength < MAX_MIDDLE_LENGTH) {
                    params.addRule(RelativeLayout.BELOW, middleBottomId);
                    params.addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE);
                    middleBottomId = id;
                    middleLength++;
                } else {
                }
            } else {
                button.setVisibility(View.GONE);
            }
        } else {
            Log.i("MBUTTON_NULL", "id=" + id);
        }
    }

    void setRightButtonVisibility(int id, boolean enable) {
        Button button = (Button) findViewById(id);
        if (button != null) {
            if (enable) {
                RelativeLayout.LayoutParams params = getLayoutParams(button);
                if (rightLength == 0) {
                    params.addRule(RelativeLayout.ALIGN_PARENT_TOP, RelativeLayout.TRUE);
                    params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE);
                    rightBottomId = id;
                    rightLength++;
                } else if (rightLength < MAX_RIGHT_LENGTH) {
                    params.addRule(RelativeLayout.BELOW, rightBottomId);
                    params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE);
                    rightBottomId = id;
                    rightLength++;
                } else {
                    setMiddleButtonVisibility(id, enable);
                }
            } else {
                button.setVisibility(View.GONE);
            }
        } else {
//            Log.i("RBUTTON_NULL", "id=" + id);
        }
    }

    void setContextMenuVisibility() {
        leftLength = 0;
        leftBottomId = NO_ID;
        rightLength = 0;
        rightBottomId = NO_ID;
        middleLength = 0;
        middleBottomId = NO_ID;
        int orientation = getResources().getConfiguration().orientation;
        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            MAX_LEFT_LENGTH = 4;
            MAX_RIGHT_LENGTH = 4;
            MAX_MIDDLE_LENGTH = 4;
        } else {
            MAX_LEFT_LENGTH = 7;
            MAX_RIGHT_LENGTH = 6;
            MAX_MIDDLE_LENGTH = 4;
        }
        int contextMenuMask = editorViewer.model.selections.getContextMenuMask(editorViewer.model);
        setRightButtonVisibility(R.id.undo, (contextMenuMask & ContextMenuIcons.UNDO) != 0);
        setLeftButtonVisibility(R.id.redo, (contextMenuMask & ContextMenuIcons.REDO) != 0);
        setMiddleButtonVisibility(R.id.cancel, (contextMenuMask & ContextMenuIcons.UNSELECT) != 0);
        setLeftButtonVisibility(R.id.fine_rotate, (contextMenuMask & ContextMenuIcons.FINE_ROTATE) != 0);
        setLeftButtonVisibility(R.id.rotate, (contextMenuMask & ContextMenuIcons.ROTATE) != 0);
        setLeftButtonVisibility(R.id.rotate_left, (contextMenuMask & ContextMenuIcons.ROTATE) != 0);
        setRightButtonVisibility(R.id.rotate_right, (contextMenuMask & ContextMenuIcons.ROTATE) != 0);
        setLeftButtonVisibility(R.id.clone, (contextMenuMask & ContextMenuIcons.COPY) != 0);
        setLeftButtonVisibility(R.id.morph, (contextMenuMask & ContextMenuIcons.MORPH) != 0);
        setLeftButtonVisibility(R.id.del, (contextMenuMask & ContextMenuIcons.DELETE) != 0);
        setLeftButtonVisibility(R.id.invert, (contextMenuMask & ContextMenuIcons.CHANGE_INV) != 0);
        setLeftButtonVisibility(R.id.delay, (contextMenuMask & ContextMenuIcons.DELAY) != 0);
        setLeftButtonVisibility(R.id.lock, (contextMenuMask & ContextMenuIcons.LOCK) != 0);
        setLeftButtonVisibility(R.id.unlock, (contextMenuMask & ContextMenuIcons.UNLOCK) != 0);
        setRightButtonVisibility(R.id.label, (contextMenuMask & ContextMenuIcons.EDIT) != 0);
        setLeftButtonVisibility(R.id.align_horizontal, (contextMenuMask & ContextMenuIcons.ALIGN_HORIZONTAL) != 0);
        setRightButtonVisibility(R.id.align_vertical, (contextMenuMask & ContextMenuIcons.ALIGN_VERTICAL) != 0);
        setRightButtonVisibility(R.id.add_input, (contextMenuMask & ContextMenuIcons.ADD_INPUT) != 0);
        setLeftButtonVisibility(R.id.remove_sw, (contextMenuMask & ContextMenuIcons.REMOVE_SW) != 0);
        setLeftButtonVisibility(R.id.add_sw, (contextMenuMask & ContextMenuIcons.ADD_SW) != 0);
        setRightButtonVisibility(R.id.include, (contextMenuMask & ContextMenuIcons.HDL) != 0);
        setRightButtonVisibility(R.id.show_net, (contextMenuMask & ContextMenuIcons.ROUTE) != 0);
        setRightButtonVisibility(R.id.resistor, (contextMenuMask & ContextMenuIcons.RESISTOR) != 0);
        setRightButtonVisibility(R.id.r_and_led, (contextMenuMask & ContextMenuIcons.RLED) != 0);
        setRightButtonVisibility(R.id.led, (contextMenuMask & ContextMenuIcons.LED) != 0);
        setRightButtonVisibility(R.id.disable, (contextMenuMask & ContextMenuIcons.DISABLE) != 0);
        setRightButtonVisibility(R.id.size, (contextMenuMask & ContextMenuIcons.SIZE) != 0);
        setLeftButtonVisibility(R.id.l_to_h, (contextMenuMask & ContextMenuIcons.L_H) != 0);
        setLeftButtonVisibility(R.id.h_to_l, (contextMenuMask & ContextMenuIcons.H_L) != 0);
        int numberOfSelection = editorViewer.model.selections.getNumberOfSelections();
        if (numberOfSelection == 0) {
            writeTitle(Settings.getProjectFileName());
        } else if (numberOfSelection == 1) {
            writeTitle(Settings.getProjectFileName() + " 1 item selected");
        } else if (numberOfSelection > 1) {
            writeTitle(Settings.getProjectFileName() + " " + numberOfSelection + " items selected");
        }
    }

    final static int NO_ID = -1;
    int MAX_LEFT_LENGTH = 7;
    int MAX_RIGHT_LENGTH = 7;
    int MAX_MIDDLE_LENGTH = 7;
    int leftLength = 0;
    int leftBottomId = NO_ID;
    int rightLength = 0;
    int rightBottomId = NO_ID;
    int middleLength = 0;
    int middleBottomId = NO_ID;


    @Override
    public void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
//        System.out.println("Restore");
        super.onRestoreInstanceState(savedInstanceState);
//        editorType = savedInstanceState.getInt(EDITOR_TYPE);
        Settings.loadSettings(this, savedInstanceState);
        newSchematicOrBreadboard(Settings.getProjectFileName());
        editorViewer.restore(Settings.getProjectFileName());
        editorViewer.invalidate();

//        switch (editorType) {
//            case SCHEMATIC_EDITOR:
//                newSchematic();
//                editorViewer.restore(Settings.getProjectFileName());
//                editorViewer.invalidate();
//                break;
//            case BREADBOARD_EDITOR:
//                newBreadBoard();
//                editorViewer.restore(Settings.getProjectFileName());
//                editorViewer.invalidate();
//                break;
//            default:
//        }
        setOrientation(Settings.getOrientation());
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
//        System.out.println("Save");
//        savedInstanceState.putInt(EDITOR_TYPE, editorType);
        Settings.saveSettings(this, savedInstanceState);
        editorViewer.save(Settings.getProjectFileName());
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        //       int id = findViewById(android.R.id.content).getId();
//        MenuStateMachine.gotoOptionMenu();
        return true;
    }

    void areYouSure() {
        new AlertDialog.Builder(this)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle(R.string.quit)
                .setMessage(R.string.really_quit)
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (editorViewer.model.enableScope) {
                            editorViewer.restoreAfterScope();
                        }
                        finish();
                    }
                })
                .setNegativeButton(R.string.no, null)
                .show();
    }

//    @Override
//    public boolean onKeyUp(int keyCode, KeyEvent event) {
//        // Check if the key event was the Back button and if there's history
//        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
//            if (myWebView.canGoBack()) {
//                myWebView.goBack();
//                Log.i("BACK", "CANGO");
//                return true;
//            } else {
//                Log.i("BACK", "CANNOTGO");
//                MenuStateMachine.backHandler(this);
//                return true;
//            }
//        } else {
//            return super.onKeyUp(keyCode, event);
//        }
//    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // Check if the key event was the Back button and if there's history
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            if (myWebView != null && myWebView.canGoBack()) {
                myWebView.goBack();
                Log.i("BACK", "CANGO");
                return true;
            } else {
                Log.i("BACK", "CANNOTGO");
                MenuStateMachine.backHandler(this);
                return true;
            }
        } else {
            return super.onKeyDown(keyCode, event);
        }
    }

    @Override
    public void onBackPressed() {
//        System.out.println("onBackPressed");

//        if (backReturnsToOptionsMenu) {
//            this.openOptionsMenu();
//            backReturnsToOptionsMenu = false;
//        } else {
        super.onBackPressed();
//        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        editorViewer.invalidate();
        if (id == R.id.action_change_orientation) {
            doOrientation(item);
//            editorViewer.save(Settings.getProjectFileName());
            return true;
        } else if (id == R.id.action_new_schematic) {
            //           askIfNewSchematic();
            doNewSchematic();
            return true;
        } else if (id == R.id.action_new_breadboard) {
//            askIfNewBreadBoard();
            doNewBreadBoard();
            return true;
        } else if (id == R.id.action_open) {
            doOpen();
            return true;
        } else if (id == R.id.action_download) {
            doDownload();
            return true;
        } else if (id == R.id.action_save) {
            editorViewer.save(Settings.getProjectFileName());
            return true;
        } else if (id == R.id.action_save_as) {
            doSaveAs();
            return true;
        } else if (id == R.id.action_last_route) {
            editorViewer.model.enableDrawRaster = true;
            //!editorViewer.enableDrawRaster;
            editorViewer.invalidate();
            return true;
        } else if (id == R.id.action_45) {
            do45(item);
        } else if (id == R.id.ff_check) {
            doFFcheck(item);
            return true;
        } else if (id == R.id.hide_zoom_control) {
            doZoomControl(item);
            return true;
        } else if (id == R.id.action_auto_zoom) {
            doAutoZoom(item);
        } else if (id == R.id.center) {
            editorViewer.model.doCenter(editorViewer);
            return true;
        } else if (id == R.id.net_number) {
            doNetNumber(item);
            editorViewer.invalidate();
            return true;
        } else if (id == R.id.action_about) {
            doAbout();
            return true;
        } else if (id == R.id.reset_level) {
            doResetLevel(item);
        } else if (id == R.id.action_change_gate_symbol) {
            changeGateSymbol(item);
        } else if (id == R.id.action_change_color_theme) {
            changeColorTheme(item);
        } else if (id == R.id.spike) {
            doSpike(item);
        } else if (id == R.id.wire_level_display) {
            doWireLevel(item);
        } else if (id == R.id.action_draw_icons) {
            DrawIcons.draw();
        }
        return super.onOptionsItemSelected(item);
    }

//    private void askIfNewSchematic() {
//        String fileName = Settings.getProjectFileName();
//        if (fileName.endsWith("new.s") || fileName.endsWith("new.b")) {
//            if (editorViewer.model.isEmpty()) { //do not save empty new.b
//                doNewSchematic();
//            } else {
//                new AlertDialog.Builder(this)
//                        .setIcon(android.R.drawable.ic_dialog_alert)
//                        .setTitle("Overwrite new.s?")
//                        .setMessage("")
//                        .setNegativeButton(" Rename new.s and save", new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//                                String m_chosenDir = "";
//                                fileBrowser = new FileBrowser(MainActivity.this, new FileBrowser.ChosenDirectoryListener() {
//                                    @Override
//                                    public boolean onChosenDir(String chosenDir, boolean overwrite) {
//                                        Toast.makeText(MainActivity.this, "Chosen FileOpenDialog File: " +
//                                                chosenDir, Toast.LENGTH_LONG).show();
//                                        Settings.setProjectFileName(chosenDir);
//                                        editorViewer.save(chosenDir);
//                                        writeTitle(chosenDir);
//                                        Settings.enableSave();
//                                        doNewSchematic();
//                                        return true;
//                                    }
//                                },new FileBrowser.CancelListener() {
//                                    @Override
//                                    public void onCancel(String chosenDir) {
//                                        Toast.makeText(MainActivity.this, "Chosen FileOpenDialog File: " +
//                                                chosenDir, Toast.LENGTH_LONG).show();
//                                        Settings.setProjectFileName(chosenDir);
//                                        editorViewer.save(chosenDir);
//                                        writeTitle(chosenDir);
//                                        Settings.enableSave();
//                                        doNewSchematic();
//                                    }
//                                } );
//                                fileBrowser.setNewFolderEnabled(true);
//                                fileBrowser.chooseDirectory(m_chosenDir);
//                            }
//                        })
//                        .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//                                doNewSchematic(); // overwrite non empty new.b
//                            }
//                        })
//                        .show();
//            }
//        } else {
//            editorViewer.stopSimulation();
//            editorViewer.save(Settings.getProjectFileName()); // project uniqueName is not new.b save it
//            doNewSchematic();
//        }
//    }
//
//    private void askIfNewBreadBoard() {
//        String fileName = Settings.getProjectFileName();
//        if (fileName.endsWith("new.s") || fileName.endsWith("new.b")) {
//            if (editorViewer.model.isEmpty()) { //do not save empty new.b
//                doNewBreadBoard();
//            } else {
//                new AlertDialog.Builder(this)
//                        .setIcon(android.R.drawable.ic_dialog_alert)
//                        .setTitle("Overwrite new.b?")
//                        .setMessage("")
//                        .setNegativeButton(" Rename new.b and save", new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//                                String m_chosenDir = "";
//                                fileBrowser = new FileBrowser(MainActivity.this, new FileBrowser.ChosenDirectoryListener() {
//                                    @Override
//                                    public boolean onChosenDir(String chosenDir, boolean overWrite) {
//                                        Toast.makeText(MainActivity.this, "Chosen FileOpenDialog File: " +
//                                                chosenDir, Toast.LENGTH_LONG).show();
//                                        Settings.setProjectFileName(chosenDir);
//                                        editorViewer.save(chosenDir);
//                                        writeTitle(chosenDir);
//                                        Settings.enableSave();
//                                        doNewBreadBoard();
//                                        return true;
//                                    }
//                                });
//                                fileBrowser.setNewFolderEnabled(true);
//                                fileBrowser.chooseDirectory(m_chosenDir);
//                            }
//                        })
//                        .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//                                doNewBreadBoard(); // overwrite non empty new.b
//                            }
//                        })
//                        .show();
//            }
//        } else {
//            editorViewer.stopSimulation();
//            editorViewer.save(Settings.getProjectFileName()); // project uniqueName is not new.b save it
//            doNewBreadBoard();
//        }
//    }


    private void doDownload() {
        runWebView();
    }

    private void doWireLevel(MenuItem item) {
        if (Settings.displayWireLevel) {
            item.setTitle(R.string.disable_wire_level);
            mTracker.send(new HitBuilders.EventBuilder()
                    .setCategory("Action")
                    .setAction("WL 0")
                    .build());
            Settings.displayWireLevel = false;
        } else {
            mTracker.send(new HitBuilders.EventBuilder()
                    .setCategory("Action")
                    .setAction("WL 1")
                    .build());
            item.setTitle(R.string.enable_wire_level);
            Settings.displayWireLevel = true;
        }
    }

    private void doAutoZoom(MenuItem item) {
        if (Settings.auto_zoom) {
            item.setTitle(R.string.auto_zoom);
            mTracker.send(new HitBuilders.EventBuilder()
                    .setCategory("Action")
                    .setAction("AZ 0")
                    .build());
            Settings.auto_zoom = false;
        } else {
            mTracker.send(new HitBuilders.EventBuilder()
                    .setCategory("Action")
                    .setAction("AZ 1")
                    .build());
            item.setTitle(R.string.no_auto_zoom);
            Settings.auto_zoom = true;
        }
    }

    private void doSpike(MenuItem item) {
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.delay_popup_menu);
        dialog.setTitle("Set spike width");
        Button tcoUp = (Button) dialog.findViewById(R.id.buttonUp);
        Button tcoDown = (Button) dialog.findViewById(R.id.buttonDown);
        final EditText tcoEditText = (EditText) dialog.findViewById(R.id.delay0);
        tcoEditText.setText(Integer.toString(Event.spikeWidth[0]));
        TimeDialog.setListeners(tcoUp, tcoEditText, Event.spikeWidth, 0, TimeDialog.Dirs.UP, 0);
        TimeDialog.setListeners(tcoDown, tcoEditText, Event.spikeWidth, 0, TimeDialog.Dirs.DOWN, 0);
        dialog.show();
    }

    private void doAbout() {
        String versionName;
        try {
            versionName = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            versionName = new String("3.10");
            e.printStackTrace();
        }
        new AlertDialog.Builder(this)
                .setTitle("About & logics")
                .setMessage("Version " + versionName + "\nCopyright LogicBreadboard\n2016")
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // continue with delete
                    }
                })
//                    .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
//                        public void onClick(DialogInterface dialog, int which) {
//                            // do nothing
//                        }
//                    })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    private void doNetNumber(MenuItem item) {
        if (Viewer.netNumberShowEnabled) {
            item.setTitle(R.string.show_net_number);
            Viewer.netNumberShowEnabled = false;
        } else {
            item.setTitle(R.string.hide_net_number);
            Viewer.netNumberShowEnabled = true;
        }
    }

    private void doOrientation(MenuItem item) {
        final Dialog settingsDialog = new Dialog(this);
        settingsDialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        settingsDialog.setContentView(getLayoutInflater().inflate(R.layout.orienation_dialog
                , null));
        settingsDialog.show();

        Button auto = (Button) settingsDialog.findViewById(R.id.action_set_auto);
        auto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Settings.ffInit = State.Level.low;
                Settings.setOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
                mTracker.send(new HitBuilders.EventBuilder()
                        .setCategory("Action")
                        .setAction("OR S")
                        .build());
                settingsDialog.cancel();
            }
        });
        Button portrait = (Button) settingsDialog.findViewById(R.id.action_set_portrait);
        portrait.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Settings.ffInit = State.Level.high;
                Settings.setOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                mTracker.send(new HitBuilders.EventBuilder()
                        .setCategory("Action")
                        .setAction("PORTR")
                        .build());
                settingsDialog.cancel();
            }
        });
        Button landscape = (Button) settingsDialog.findViewById(R.id.action_set_landscape);
        landscape.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Settings.ffInit = State.Level.random;
                Settings.setOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                mTracker.send(new HitBuilders.EventBuilder()
                        .setCategory("Action")
                        .setAction("LANDS")
                        .build());
                settingsDialog.cancel();
            }
        });
    }

    void setOrientation(int orientation) {
        switch (orientation) {
            case ActivityInfo.SCREEN_ORIENTATION_PORTRAIT:
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                break;
            case ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE:
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                break;
            case ActivityInfo.SCREEN_ORIENTATION_SENSOR:
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
                break;
            default:
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
        }
    }

    void changeGateSymbol(MenuItem item) {
        final Dialog settingsDialog = new Dialog(this);
        settingsDialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        settingsDialog.setContentView(getLayoutInflater().inflate(R.layout.gate_symbol_selection
                , null));
        settingsDialog.show();

        Button auto = (Button) settingsDialog.findViewById(R.id.distinctive_shape);
        auto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Settings.symbolType = Settings.ANSI;
                mTracker.send(new HitBuilders.EventBuilder()
                        .setCategory("Action")
                        .setAction("ANSI")
                        .build());
                editorViewer.invalidate();
                settingsDialog.cancel();
            }
        });
        Button blackOnWhite = (Button) settingsDialog.findViewById(R.id.rectangular_shape);
        blackOnWhite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Settings.symbolType = Settings.IEC;
                mTracker.send(new HitBuilders.EventBuilder()
                        .setCategory("Action")
                        .setAction("IEC")
                        .build());
                editorViewer.invalidate();
                settingsDialog.cancel();
            }
        });
    }

    void changeColorTheme(MenuItem item) {
        final Dialog settingsDialog = new Dialog(this);
        settingsDialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        settingsDialog.setContentView(getLayoutInflater().inflate(R.layout.color_theme_dialog
                , null));
        settingsDialog.show();

        Button auto = (Button) settingsDialog.findViewById(R.id.white_on_black);
        auto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Settings.colorTheme = Settings.WHITE_ON_BLACK;
                mTracker.send(new HitBuilders.EventBuilder()
                        .setCategory("Action")
                        .setAction("WonB")
                        .build());
                setColorTheme();
                settingsDialog.cancel();
            }
        });
        Button blackOnWhite = (Button) settingsDialog.findViewById(R.id.black_on_white);
        blackOnWhite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Settings.colorTheme = Settings.BLACK_ON_WHITE;
                mTracker.send(new HitBuilders.EventBuilder()
                        .setCategory("Action")
                        .setAction("BonW")
                        .build());
                setColorTheme();
                settingsDialog.cancel();
            }
        });
        Button blackOnSilver = (Button) settingsDialog.findViewById(R.id.black_on_silver);
        blackOnSilver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Settings.colorTheme = Settings.BLACK_ON_SILVER;
                mTracker.send(new HitBuilders.EventBuilder()
                        .setCategory("Action")
                        .setAction("BonS")
                        .build());
                setColorTheme();
                settingsDialog.cancel();
            }
        });
        Button blackOnGreen = (Button) settingsDialog.findViewById(R.id.black_on_green);
        blackOnGreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Settings.colorTheme = Settings.BLACK_ON_GREEN;
                mTracker.send(new HitBuilders.EventBuilder()
                        .setCategory("Action")
                        .setAction("BonG")
                        .build());
                setColorTheme();
                settingsDialog.cancel();
            }
        });
    }

    private void doResetLevel(MenuItem item) {
        final Dialog settingsDialog = new Dialog(this);
        ffInitMenuItem = item;
        settingsDialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        settingsDialog.setContentView(getLayoutInflater().inflate(R.layout.ff_reset_dialog
                , null));
        settingsDialog.show();
        Button undef = (Button) settingsDialog.findViewById(R.id.action_set_ff_init_undefined);
        undef.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Settings.ffInit = State.Level.undefined;
                mTracker.send(new HitBuilders.EventBuilder()
                        .setCategory("Action")
                        .setAction("FF U")
                        .build());
                ffInitMenuItem.setTitle(R.string.reset_level_u);
                settingsDialog.cancel();
            }
        });

        Button low = (Button) settingsDialog.findViewById(R.id.action_set_ff_init_low);
        low.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Settings.ffInit = State.Level.low;
                mTracker.send(new HitBuilders.EventBuilder()
                        .setCategory("Action")
                        .setAction("FF L")
                        .build());
                ffInitMenuItem.setTitle(R.string.reset_level_l);
                settingsDialog.cancel();
            }
        });
        Button high = (Button) settingsDialog.findViewById(R.id.action_set_ff_init_high);
        high.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Settings.ffInit = State.Level.high;
                mTracker.send(new HitBuilders.EventBuilder()
                        .setCategory("Action")
                        .setAction("FF H")
                        .build());
                ffInitMenuItem.setTitle(R.string.reset_level_h);
                settingsDialog.cancel();
            }
        });
        Button random = (Button) settingsDialog.findViewById(R.id.action_set_ff_init_random);
        random.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Settings.ffInit = State.Level.random;
                mTracker.send(new HitBuilders.EventBuilder()
                        .setCategory("Action")
                        .setAction("FF R")
                        .build());
                ffInitMenuItem.setTitle(R.string.reset_level_r);
                settingsDialog.cancel();
            }
        });
    }

    public void do45(MenuItem item) {
        if (Settings.enabled45) {
            item.setTitle(R.string.action_45);
            mTracker.send(new HitBuilders.EventBuilder()
                    .setCategory("Action")
                    .setAction("45 d")
                    .build());
            Settings.enabled45 = false;
        } else {
            item.setTitle(R.string.action_no_45);
            mTracker.send(new HitBuilders.EventBuilder()
                    .setCategory("Action")
                    .setAction("45 e")
                    .build());
            Settings.enabled45 = true;
        }
    }

    public void doFFcheck(MenuItem item) {
        if (Settings.ff_check) {
            item.setTitle(R.string.enable_ff_check);
            mTracker.send(new HitBuilders.EventBuilder()
                    .setCategory("Action")
                    .setAction("FFc f")
                    .build());
            Settings.ff_check = false;
        } else {
            item.setTitle(R.string.disable_ff_check);
            mTracker.send(new HitBuilders.EventBuilder()
                    .setCategory("Action")
                    .setAction("FFc t")
                    .build());
            Settings.ff_check = true;
        }
    }

    public void doZoomControl(MenuItem item) {
        if (Settings.hideZoomControl) {
            item.setTitle(R.string.show_zoom_control);
            Settings.hideZoomControl = false;
        } else {
            item.setTitle(R.string.hide_zoom_control);
            Settings.hideZoomControl = true;
        }
        ZoomControls zoom = (ZoomControls) findViewById(R.id.zoomControl);
        if (zoom != null) {
            if (Settings.hideZoomControl) {
                mTracker.send(new HitBuilders.EventBuilder()
                        .setCategory("Action")
                        .setAction("NoZoomC")
                        .build());
                zoom.setVisibility(View.GONE);
            } else {
                mTracker.send(new HitBuilders.EventBuilder()
                        .setCategory("Action")
                        .setAction("ZoomC")
                        .build());
                zoom.setVisibility(View.VISIBLE);
            }
        }
    }

    private void dismissPopUps() {
        BBDeviceSelectionMenu.dismiss();
        DeviceSelectionMenu.dismiss();
    }

    public void doNewSchematic() {
        dismissPopUps();
        MenuStateMachine.gotoEditing();
        editorViewer.stopSimulation();
        editorViewer.save(Settings.getProjectFileName());
        previousProjectFileName = new String(Settings.getProjectFileName());
        newSchematic();
        Settings.setProjectFileName("");
        editorViewer.invalidate();
        NetNumbers.reset();
        //       writeTitle(Settings.NEW_SCHEMATIC_PATH);
        doSaveAs();
    }

//    public void fallBackBreadBoard() {
//        dismissPopUps();
//        MenuStateMachine.gotoBBEditing();
//        if (editorViewer != null) {
//            editorViewer.stopSimulation();
//        }
////        editorViewer.save(Settings.getProjectFileName());
//        previousProjectFileName = new String(Settings.getProjectFileName());
////        previousProjectFileName = null;
//        newBreadBoard();
////        Settings.setProjectFileName(Settings.NEW_BREADBOARD_PATH);
//        editorViewer.invalidate();
//        NetNumbers.reset();
////        editorViewer.addInterfaces();
//        doSaveAs();
////        writeTitle(Settings.NEW_BREADBOARD_PATH);
//    }

    public void doNewBreadBoard() {
        dismissPopUps();
        MenuStateMachine.gotoBBEditing();
        editorViewer.stopSimulation();
        editorViewer.save(Settings.getProjectFileName());
        previousProjectFileName = new String(Settings.getProjectFileName());
        newBreadBoard();
//        Settings.setProjectFileName(Settings.NEW_BREADBOARD_PATH);
        editorViewer.invalidate();
        NetNumbers.reset();
//        editorViewer.addInterfaces();
        doSaveAs();
//        writeTitle(Settings.NEW_BREADBOARD_PATH);
    }

    public void doOpen() {
        editorViewer.stopSimulation();
        previousProjectFileName = new String(Settings.getProjectFileName());
        editorViewer.save(Settings.getProjectFileName());
        String m_chosenDir = "";
        fileBrowser = new FileBrowser(MainActivity.this, new FileBrowser.ChosenDirectoryListener() {
            @Override
            public boolean onChosenDir(String chosenDir, boolean overWrite) {
                Toast.makeText(MainActivity.this, "Chosen FileOpenDialog File: " +
                        chosenDir, Toast.LENGTH_LONG).show();
                if (chosenDir.substring((chosenDir.lastIndexOf(".") + 1), chosenDir.length())
                        .equals("v")) {
//                    VerilogParser verilogParser = new VerilogParser();
//                    verilogParser.run(chosenDir);
                } else {
                    newSchematicOrBreadboard(chosenDir);
//                    if (chosenDir.substring((chosenDir.lastIndexOf(".") + 1), chosenDir.length())
//                            .equals("s")) {
//                        newSchematic();
//                    } else if (chosenDir.substring((chosenDir.lastIndexOf(".") + 1), chosenDir.length())
//                            .equals("b")) {
//                        newBreadBoard();
//                    } else {
//                        newSchematic();
//                    }
                    editorViewer.restore(chosenDir);
                    editorViewer.invalidate();
                    Settings.setProjectFileName(chosenDir);
                    writeTitle(chosenDir);
                    Settings.enableSave();
                }
                return true;
            }
        }, null);
        fileBrowser.setNewFolderEnabled(false);
        fileBrowser.chooseDirectory(m_chosenDir);
    }

    public void doSaveAs() {
        String m_chosenDir = "";
        previousProjectFileName = new String(Settings.getProjectFileName());
        fileBrowser = new FileBrowser(MainActivity.this, new FileBrowser.ChosenDirectoryListener() {
            @Override
            public boolean onChosenDir(final String chosenDir, final boolean overwrite) {
                File file = new File(chosenDir);
                if (file.exists()) {
                    if (!overwrite) {
                        return false;
                    } else {
                        save(chosenDir);
                        return true;
                    }
                } else {
                    save(chosenDir);
                    return true;
                }
            }

            public void save(String chosenDir) {
                Toast.makeText(MainActivity.this, "Chosen FileOpenDialog File: " +
                        chosenDir, Toast.LENGTH_LONG).show();
                Settings.setProjectFileName(chosenDir);
                editorViewer.save(chosenDir);
                writeTitle(chosenDir);
                Settings.enableSave();
            }
        }, new FileBrowser.CancelListener() {
            @Override
            public void onCancel(final String chosenDir) {
                //restore previous file if exists
//                if (Settings.getProjectFileName().equals("")) {
                Log.i("ONCANCEL", previousProjectFileName);
                Settings.setProjectFileName(previousProjectFileName);
                newSchematicOrBreadboard(previousProjectFileName);
                editorViewer.restore(previousProjectFileName);
//                }
            }
        }
        );
        fileBrowser.setNewFolderEnabled(true);
        fileBrowser.chooseDirectory(m_chosenDir);
    }

    public void writeTitle(String chosenDir) {
        int index = chosenDir.lastIndexOf("/");
        String fileName = chosenDir.substring(index + 1);
//        System.out.println(chosenDir + " &&& " + fileName);
//        setTitle("& dlogic " + fileName);
//        setTitle(getResources().getString(R.string.app_name) + "  " + fileName);
        setTitle(fileName);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(fileName);
        }
//        ActionBar actionBar = getActionBar();
//        actionBar.setTitle("&logic " + fileName);
//        getSupportActionBar().setDisplayShowTitleEnabled(true);
//        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    public void sizeClicked(View view) {
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        MenuItem item = menu.findItem(R.id.action_save);
        item.setEnabled(Settings.isSaveEnabled());
        item = menu.findItem(R.id.action_45);
        if (Settings.enabled45) {
            item.setTitle(R.string.action_no_45);
        } else {
            item.setTitle(R.string.action_45);
        }
        item = menu.findItem(R.id.ff_check);
        if (Settings.ff_check) {
            item.setTitle(R.string.disable_ff_check);
        } else {
            item.setTitle(R.string.enable_ff_check);
        }
        item = menu.findItem(R.id.hide_zoom_control);
        if (Settings.hideZoomControl) {
            item.setTitle(R.string.show_zoom_control);
        } else {
            item.setTitle(R.string.hide_zoom_control);
        }
        item = menu.findItem(R.id.wire_level_display);
        if (Settings.displayWireLevel) {
            item.setTitle(R.string.disable_wire_level);
        } else {
            item.setTitle(R.string.enable_wire_level);
        }
        item = menu.findItem(R.id.action_auto_zoom);
        if (Settings.auto_zoom) {
            item.setTitle(R.string.no_auto_zoom);
        } else {
            item.setTitle(R.string.auto_zoom);
        }
        item = menu.findItem(R.id.reset_level);
        switch (Settings.ffInit) {
            case undefined:
                item.setTitle(R.string.reset_level_u);
                break;
            case low:
                item.setTitle(R.string.reset_level_l);
                break;
            case high:
                item.setTitle(R.string.reset_level_h);
                break;
            case random:
                item.setTitle(R.string.reset_level_r);
                break;
            default:
                item.setTitle(R.string.reset_level_u);
        }
        boolean isDebuggable = isDebuggable();
        if (!isDebuggable) {
//            item = menu.findItem(R.id.net_number);
//            item.setVisible(false);
            item = menu.findItem(R.id.action_last_route);
            item.setVisible(false);
            item = menu.findItem(R.id.action_draw_icons);
            item.setVisible(false);
//            item = menu.findItem(R.id.action_new_breadboard);
//            item.setVisible(false);
//            item = menu.findItem(R.id.reset_level);
//            item.setVisible(false);
        } else {
//            item = menu.findItem(R.id.net_number);
//            item.setTitle(R.string.hide_net_number);
//            Viewer.netNumberShowEnabled = true;
//            item = menu.findItem(R.id.hide_zoom_control);
//            item.setTitle(R.string.show_zoom_control);
//            Settings.hideZoomControl = false;
        }

        return true;
    }

    public boolean isDebuggable() {
        PackageManager pacMan = getPackageManager();
        String pacName = getPackageName();
        ApplicationInfo appInfo = null;
        try {
            appInfo = pacMan.getApplicationInfo(pacName, 0);
        } catch (PackageManager.NameNotFoundException e) {
            Toast.makeText(this, "Could not find package " + pacName, Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
        boolean isDebuggable = false;
        if (appInfo != null) {
            if ((appInfo.flags & ApplicationInfo.FLAG_DEBUGGABLE) != 0) {
                isDebuggable = true;
            }
        }
        Log.i("ISDEBUGGABLE", Boolean.toString(isDebuggable));
        return isDebuggable;
//        return false;
    }

}
