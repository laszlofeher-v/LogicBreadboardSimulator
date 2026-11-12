package com.logicbreadboard.simulator;

import android.view.View;

/**
 * Created by laci on 6/18/15.
 */
public class MenuStateMachine {

    enum BackAction {
        QUIT, MAIN_ACTIVITY, OPTION_MENU, DEVICE_SELECTION
    }

    enum State {
        EDITING, BB_EDITING, DEVICE_GROUP_SELECTION, DEVICE_SELECTION, BB_DEVICE_GROUP_SELECTION, BB_DEVICE_SELECTION, SIMULATION, WEB, BB_WEB
    }

    static BackAction backAction = BackAction.QUIT;

    static private State state;

    static void backHandler(MainActivity activity) {
        View v;
        switch (state) {
            case EDITING:
                //TODO popup
                activity.areYouSure();
                activity.editorViewer.save(Settings.getProjectFileName());
//                Settings.saveSettings(activity);
                break;
            case DEVICE_GROUP_SELECTION:
                state = State.EDITING;
                DeviceSelectionMenu.dismiss();
                break;
            case DEVICE_SELECTION:
                state = State.DEVICE_GROUP_SELECTION;
                DeviceSelectionMenu.dismiss();
                v = activity.editorViewer.getCanvas();
                DeviceSelectionMenu.displayDeviceMenu(activity, v);
                break;
            case BB_EDITING:
                //TODO popup
                activity.areYouSure();
                activity.editorViewer.save(Settings.getProjectFileName());
//                Settings.saveSettings(activity);
                break;
            case BB_DEVICE_GROUP_SELECTION:
                state = State.BB_EDITING;
                BBDeviceSelectionMenu.dismiss();
                break;
            case BB_DEVICE_SELECTION:
                state = State.BB_DEVICE_GROUP_SELECTION;
                BBDeviceSelectionMenu.dismiss();
                v = activity.editorViewer.getCanvas();
                BBDeviceSelectionMenu.displayDeviceMenu(activity, v);
                break;
            case WEB:
                state = State.EDITING;
//                Settings.setLayoutType(activity);
                activity.newSchematic();
                activity.editorViewer.restore(Settings.getProjectFileName());
                activity.editorViewer.invalidate();
                Settings.setProjectFileName(Settings.getProjectFileName());
                activity.writeTitle(Settings.getProjectFileName());
                Settings.enableSave();
//                activity.editorViewer.restore(Settings.getProjectFileName());
//                activity.editorViewer.invalidate();
                //               activity.setContentView(activity.editorViewer);
                break;
            case BB_WEB:
                state = State.BB_EDITING;
//                Settings.setLayoutType(activity);
                activity.newBreadBoard();
                activity.editorViewer.restore(Settings.getProjectFileName());
                activity.editorViewer.invalidate();
                Settings.setProjectFileName(Settings.getProjectFileName());
                activity.writeTitle(Settings.getProjectFileName());
                Settings.enableSave();
//                activity.editorViewer.restore(Settings.getProjectFileName());
//                activity.editorViewer.invalidate();
                //               activity.setContentView(activity.editorViewer);
                break;
            default:
        }
    }

    public static void gotoDeviceGroupSelection() {
        state = State.DEVICE_GROUP_SELECTION;
    }

    public static void gotoBBDeviceGroupSelection() {
        state = State.BB_DEVICE_GROUP_SELECTION;
    }

    public static void gotoDeviceSelection() {
        state = State.DEVICE_SELECTION;
    }

    public static void gotoBBDeviceSelection() {
        state = State.BB_DEVICE_SELECTION;
    }

    static boolean inDeviceGroupSelection() {
        return (state == State.DEVICE_GROUP_SELECTION);
    }

    static boolean inBBDeviceGroupSelection() {
        return (state == State.BB_DEVICE_GROUP_SELECTION);
    }

    public static void gotoEditing() {
        state = State.EDITING;
    }

    public static void gotoBBEditing() {
        state = State.BB_EDITING;
    }

    public static void gotoWeb() {
        state = State.WEB;
    }

    public static State getState() {
        return state;
    }

    public static void gotoBBWeb() {
        state = State.BB_WEB;
    }
}
