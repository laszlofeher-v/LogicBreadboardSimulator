package com.logicbreadboard.simulator;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.widget.RadioGroup;

import java.io.File;

/**
 * Created on 1/18/15.
 */

public class Settings {

    static final int BOTTOM = 0;
    static final int TOP = 1;
    static final int LEFT = 2;
    static final int RIGHT = 3;
    static final int BOTTOM_LEFT = 1;
    static final int TOP_RIGHT = 2;
    static final int TOP_LEFT = 3;
    static final int BOTTOM_RIGHT = 4;
    static final int ANSI = 0;
    static final int IEC = 1;
    static final int DIN = 2;
    static final int CURSOR__OFFSET_10 = 5; // 10 = 1 inch
    static final String CONTROL_POSITION = "CONTROL_POSITION";
    static final String ZOOM_POSITION = "ZOOM_POSITION";
    static final String SPIKE_WIDTH = "SPIKE_WIDTH";
    static final String ORIENTATION = "ORIENTATION";
    static final String SYMBOL_TYPE = "SYMBOL_TYPE";
    //    static final String SAVE_ENABLED = "SAVE_ENABLED";
    final static String DEFAULT_FILENAME = "demo4.s";
//    final static int[] raws = {R.raw.gates, R.raw.demo4, R.raw.demo3, R.raw.ic_74181, R.raw.ic_74180, R.raw.ic_74160,
//            R.raw.ic_74163, R.raw.ic_74147, R.raw.ic_74148, R.raw.cmosdemo, R.raw.boxdemo, R.raw.ic_7400, R.raw.simple, R.raw.test1};
//    final static String[] DEMO_FILENAMES = {"gates.s", "demo4.s", "demo3.s", "ic_74181.s", "ic_74180.s", "ic_74160.s",
//            "ic_74163.s", "ic_74147.s", "ic_74148.s", "cmosdemo.s", "boxdemo.s", "builtin_subc_ics/ic_7400.s", "simple.v", "test1.v"};
    final static String NEW_SCHEMATIC_FILENAME = "new.s";
    final static String NEW_BREADBOARD_FILENAME = "new.b";
    final static String NEW_VERILOG_FILENAME = "new.v";
    final static String LAST_OFFSET_X = "LAST_OFFSET_X";
    final static String LAST_OFFSET_Y = "LAST_OFFSET_Y";
    final static String LAST_ZOOM = "LAST_ZOOM";
    final static String LAST_45 = "LAST_45";
    final static String HIDE_ZOOM_CONTROL = "HIDE_ZOOM_CONTROL";
    final static String FF_CHECK = "FF_CHECK";
    final static String FF_RESET = "FF_RESET";
    final static String COLOR_THEME = "COLOR_THEME";
    final static String DISPLAY_WIRE_LEVEL = "DISPLAY_WIRE_LEVEL";
    final static String EULA_ACCEPTED = "EULA_ACCEPTED";
    final static int WHITE_ON_BLACK = 0;
    final static int BLACK_ON_WHITE = 1;
    final static int BLACK_ON_GREEN = 2;
    final static int BLACK_ON_SILVER = 3;
    private static final String PROJECT_FILE_NAME = "PROJECT_FILE_NAME";
    private static final String AUTO_ZOOM = "AUTO_ZOOM";
    public static int defaultXOffset = 0;
    public static int defaultYOffset = 0;
    public static State.Level ffInit = State.Level.undefined;
    public static boolean auto_zoom = false;
    public static boolean ff_check;
    public static boolean displayWireLevel = false;
    public static boolean hideZoomControl = true;
    static boolean enabled45 = false;
    static String defaultDirectory = Environment.getExternalStorageDirectory().getAbsolutePath()
            + "/" + BuildConfig.APPLICATION_ID;
    static String defaultDownloadDirectory = BuildConfig.APPLICATION_ID + "/Downloads";
    static String DEFAULT_PATH = defaultDirectory + "/demos/" + DEFAULT_FILENAME;
    static String NEW_SCHEMATIC_PATH = defaultDirectory + "/" + NEW_SCHEMATIC_FILENAME;
    static String NEW_BREADBOARD_PATH = defaultDirectory + "/" + NEW_BREADBOARD_FILENAME;
    static String NEW_VERILOG_PATH = defaultDirectory + "/" + NEW_VERILOG_FILENAME;
    static int colorTheme = WHITE_ON_BLACK;
    private static boolean saveEnabled = true;
    private static int controlPosition = BOTTOM;
    private static int zoomPosition = BOTTOM_RIGHT;
    static int symbolType = ANSI;
    private static String projectFileName;
    private static RadioGroup radioGroup;
    private static boolean eulaAccepted = false;
    private static int orientation;

    public static void initDirectories(Context context) {
        File baseDir = context.getExternalFilesDir(null);
        if (baseDir == null) {
            baseDir = context.getFilesDir();
        }
        defaultDirectory = baseDir.getAbsolutePath();
        DEFAULT_PATH = defaultDirectory + "/demos/" + DEFAULT_FILENAME;
        NEW_SCHEMATIC_PATH = defaultDirectory + "/" + NEW_SCHEMATIC_FILENAME;
        NEW_BREADBOARD_PATH = defaultDirectory + "/" + NEW_BREADBOARD_FILENAME;
        NEW_VERILOG_PATH = defaultDirectory + "/" + NEW_VERILOG_FILENAME;
    }

    public static String getTempDownloadFilePath() {
        return defaultDirectory + "/Downloads/tmp.tmp";
    }

    public static String getProjectFileName() {
        return projectFileName;
    }

    public static void setProjectFileName(String projectFileName) {
        Settings.projectFileName = projectFileName;
    }

    public static void enableSave() {
        saveEnabled = true;
    }

    public static boolean isSaveEnabled() {
        return saveEnabled;
    }

    public static boolean createDefaultDirIfNotExists(String path) {
        boolean ret = true;

        File file = new File(path);
        if (file.exists()) {
            if (file.isDirectory()) {
                ret = true;
            } else { // the file is not a directory
                ret = false;
            }
        } else { // the directory does not exist
            if (!file.mkdirs()) {
                Log.e("TravellerLog :: ", "Problem creating deafault folder:" + path);
                ret = false;
            } else {
                ret = true;
            }
        }
        return ret;
    }
//    public static void disableSave() {
//        saveEnabled = false;
//    }

    // Called at screen rotation
    public static void loadSettings(Activity activity, Bundle savedInstanceState) {
        controlPosition = savedInstanceState.getInt(CONTROL_POSITION);
        projectFileName = savedInstanceState.getString(PROJECT_FILE_NAME, DEFAULT_PATH);
        if (projectFileName == null || !new File(projectFileName).exists()) {
            projectFileName = DEFAULT_PATH;
        }
        zoomPosition = savedInstanceState.getInt(ZOOM_POSITION);
        orientation = savedInstanceState.getInt(ORIENTATION, ActivityInfo.SCREEN_ORIENTATION_SENSOR);
        Event.spikeWidth[0] = savedInstanceState.getInt(SPIKE_WIDTH, 0);
        symbolType = savedInstanceState.getInt(SYMBOL_TYPE);
//        saveEnabled = savedInstanceState.getBoolean(SAVE_ENABLED);
        enabled45 = savedInstanceState.getBoolean(LAST_45, false);
        ff_check = savedInstanceState.getBoolean(FF_CHECK, false);
        auto_zoom = savedInstanceState.getBoolean(AUTO_ZOOM, false);
        hideZoomControl = savedInstanceState.getBoolean(HIDE_ZOOM_CONTROL, true);
        displayWireLevel = savedInstanceState.getBoolean(DISPLAY_WIRE_LEVEL, false);
        ffInit = State.Level.values()[(savedInstanceState.getInt(FF_RESET, State.Level.undefined
                .ordinal()))];
        eulaAccepted = savedInstanceState.getBoolean(EULA_ACCEPTED, false);
        colorTheme = savedInstanceState.getInt(COLOR_THEME, WHITE_ON_BLACK);
        ((MainActivity) activity).savedOrDefaultScaleFactor = savedInstanceState.getFloat(LAST_ZOOM,
                1);
        ((MainActivity) activity).savedOrDefaultXOffset = savedInstanceState.getInt(LAST_OFFSET_X,
                defaultXOffset);
        ((MainActivity) activity).savedOrDefaultYOffset = savedInstanceState.getInt(LAST_OFFSET_Y,
                defaultYOffset);
//        setLayoutType(activity);
    }

    // Called at screen rotation
    public static void saveSettings(Activity activity, Bundle savedInstanceState) {
        savedInstanceState.putInt(CONTROL_POSITION, controlPosition);
        savedInstanceState.putInt(ZOOM_POSITION, zoomPosition);
        savedInstanceState.putInt(ORIENTATION, orientation);
        savedInstanceState.putInt(SPIKE_WIDTH, Event.spikeWidth[0]);
        savedInstanceState.putInt(SYMBOL_TYPE, symbolType);
//        savedInstanceState.putBoolean(SAVE_ENABLED, saveEnabled);
        savedInstanceState.putBoolean(LAST_45, enabled45);
        savedInstanceState.putBoolean(FF_CHECK, ff_check);
        savedInstanceState.putBoolean(AUTO_ZOOM, auto_zoom);
        savedInstanceState.putBoolean(HIDE_ZOOM_CONTROL, hideZoomControl);
        savedInstanceState.putBoolean(DISPLAY_WIRE_LEVEL, displayWireLevel);
        savedInstanceState.putInt(FF_RESET, ffInit.ordinal());
        savedInstanceState.putBoolean(EULA_ACCEPTED, eulaAccepted);
        savedInstanceState.putString(PROJECT_FILE_NAME, projectFileName);
        savedInstanceState.putFloat(LAST_ZOOM, ((MainActivity) activity).editorViewer.scaleFactor);
        savedInstanceState.putInt(LAST_OFFSET_X, Math.round(((MainActivity) activity)
                .editorViewer.xOffset));
        savedInstanceState.putInt(LAST_OFFSET_Y, Math.round(((MainActivity) activity)
                .editorViewer.yOffset));
        savedInstanceState.putInt(COLOR_THEME, colorTheme);
    }

    private static void setOrientation(Activity activity, int orientation) {
        activity.setRequestedOrientation(orientation);
    }

    //called at start
    static public void loadSettings(Activity activity) {
        SharedPreferences preferences = activity.getPreferences(Context.MODE_PRIVATE);
        projectFileName = preferences.getString(PROJECT_FILE_NAME, DEFAULT_PATH);
        if (projectFileName == null || !new File(projectFileName).exists()) {
            projectFileName = DEFAULT_PATH;
        }
        controlPosition = preferences.getInt(CONTROL_POSITION, BOTTOM);
        zoomPosition = preferences.getInt(ZOOM_POSITION, BOTTOM_RIGHT);
        orientation = preferences.getInt(ORIENTATION, ActivityInfo.SCREEN_ORIENTATION_SENSOR);
        Event.spikeWidth[0] = preferences.getInt(SPIKE_WIDTH, 0);
        symbolType = preferences.getInt(SYMBOL_TYPE, ANSI);
//        setLayoutType(activity);
        ((MainActivity) activity).savedOrDefaultScaleFactor = preferences.getFloat(LAST_ZOOM,
                1);
        ((MainActivity) activity).savedOrDefaultXOffset = preferences.getInt(LAST_OFFSET_X,
                defaultXOffset);
        ((MainActivity) activity).savedOrDefaultYOffset = preferences.getInt(LAST_OFFSET_Y,
                defaultYOffset);
        enabled45 = preferences.getBoolean(LAST_45, false);
        ff_check = preferences.getBoolean(FF_CHECK, false);
        auto_zoom = preferences.getBoolean(AUTO_ZOOM, false);
        hideZoomControl = preferences.getBoolean(HIDE_ZOOM_CONTROL, true);
        displayWireLevel = preferences.getBoolean(DISPLAY_WIRE_LEVEL, false);
        ffInit = State.Level.values()[(preferences.getInt(FF_RESET, State.Level.undefined
                .ordinal()))];
        eulaAccepted = preferences.getBoolean(EULA_ACCEPTED, false);
        colorTheme = preferences.getInt(COLOR_THEME, WHITE_ON_BLACK);
    }
//
//    public static void setLayoutType(Activity activity) {
//        if (projectFileName.endsWith(".s")) {
//            setLayout(activity, R.layout.activity_schematic_bottom, R.id.schematic_zoomControl);
//        } else if (projectFileName.endsWith(".b")) {
//            setLayout(activity, R.layout.activity_breadboard_bottom, R.id.breadboard_zoomControl);
//        } else {
//            setLayout(activity, R.layout.activity_schematic_bottom, R.id.schematic_zoomControl);
//        }
//    }

    //called at the end
    public static void saveSettings(Activity activity) {
        SharedPreferences preferences = activity.getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor ed = preferences.edit();
        ed.putBoolean(LAST_45, enabled45);
        ed.putBoolean(FF_CHECK, ff_check);
        ed.putBoolean(AUTO_ZOOM, auto_zoom);
        ed.putBoolean(HIDE_ZOOM_CONTROL, hideZoomControl);
        ed.putBoolean(DISPLAY_WIRE_LEVEL, displayWireLevel);
        ed.putInt(FF_RESET, ffInit.ordinal());
        ed.putBoolean(EULA_ACCEPTED, eulaAccepted);
        ed.putString(PROJECT_FILE_NAME, projectFileName);
        ed.putInt(CONTROL_POSITION, controlPosition);
        ed.putInt(ZOOM_POSITION, zoomPosition);
        ed.putInt(ORIENTATION, orientation);
        ed.putInt(SPIKE_WIDTH, Event.spikeWidth[0]);
        ed.putInt(SYMBOL_TYPE, symbolType);
        ed.putFloat(LAST_ZOOM, ((MainActivity) activity).editorViewer.scaleFactor);
        ed.putInt(LAST_OFFSET_X, Math.round(((MainActivity) activity).editorViewer.xOffset));
        ed.putInt(LAST_OFFSET_Y, Math.round(((MainActivity) activity).editorViewer.yOffset));
        ed.putInt(COLOR_THEME, colorTheme);
        ed.apply();
    }

    public static int getOrientation() {
        return orientation;
    }

    public static void setOrientation(int orientation) {
        Settings.orientation = orientation;
    }

//    private static void placeZoomControl(Activity activity, ZoomControls zoomControl) {
//        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) zoomControl.getLayoutParams();
//        params.addRule(RelativeLayout.ALIGN_PARENT_LEFT, 0);
//        params.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0);
//        params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, 0);
//        params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, 0);
//        switch (zoomPosition) {
//            case TOP_LEFT:
//                params.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
//                params.addRule(RelativeLayout.ALIGN_PARENT_TOP);
//                break;
//            case TOP_RIGHT:
//                params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
//                params.addRule(RelativeLayout.ALIGN_PARENT_TOP);
//                break;
//            case BOTTOM_LEFT:
//                params.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
//                params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
//                break;
//            case BOTTOM_RIGHT:
//                params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
//                params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
//                break;
//            default:
//                params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
//                params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
//        }
//        zoomControl.setLayoutParams(params);
//    }

    public static boolean getEulaAccepted() {
        return eulaAccepted;
    }

    public static void setEulaAccepted(boolean v) {
        eulaAccepted = v;
    }
//
//    public static void displaySettings(Activity activity) {
////        RadioButton selected;
//        activity.setContentView(R.layout.activity_schematic_settings);
//        addControlsButtonListener(activity);
//        addZoomControlButtonListener(activity);
//        addOrientationButtonListener(activity);
//        addSymbolButtonListener(activity);
//    }

//    public static int getControlPosition() {
//        return controlPosition;
//    }
//
//    public static void setControlPosition(int controlPosition) {
//        Settings.controlPosition = controlPosition;
//    }
}
