package com.logicbreadboard.simulator;

import android.app.Activity;
import android.content.Context;
import android.graphics.Rect;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.GridView;
import android.widget.PopupWindow;

/**
 * Created on 6/3/15.
 */
public class BBDeviceSelectionMenu {
    static GridView gv;
    static PopupWindow popupWindow = null;
    static EditorViewer editorViewer;

    public static Rect locateView(View v) {
        int[] loc_int = new int[2];
        if (v == null) return null;
        try {
            v.getLocationOnScreen(loc_int);
        } catch (NullPointerException e) {
            //Happens when the view doesn't exist on screen anymore.
            return null;
        }
        Rect location = new Rect();
        location.left = loc_int[0];
        location.top = loc_int[1];
        location.right = location.left + v.getWidth();
        location.bottom = location.top + v.getHeight();
        return location;
    }

    public static void displayDeviceMenu(Activity activity, View canvas) {
//        Settings.backAction = Settings.BackAction.MAIN_ACTIVITY;
//        activity.setContentView(R.layout.devices);
//        GridView gv = (GridView) activity.findViewById(R.id.device_grid);
//        DeviceSelectionAdapter deviceSelectionAdapter = new DeviceSelectionAdapter(activity, prgmNameList, prgmImages);
//        gv.setAdapter(deviceSelectionAdapter);
        editorViewer = ((MainActivity) activity).editorViewer;
        LayoutInflater layoutInflater
                = (LayoutInflater) activity.getBaseContext()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View popupView = layoutInflater.inflate(R.layout.devices, null);
        Rect location = locateView(canvas);
        popupWindow = new PopupWindow(
                popupView, location.width(), location.height());
//                LinearLayout.LayoutParams.MATCH_PARENT,
//                LinearLayout.LayoutParams.MATCH_PARENT);
//         View v = activity.findViewById(android.R.id.content);
        //       popupWindow.showAtLocation(v, Gravity.NO_GRAVITY, x, y);
        popupWindow.showAtLocation(canvas, Gravity.TOP | Gravity.LEFT, location.left, location.top);
        //       popupWindow.showAtLocation(v, Gravity.LEFT | Gravity.TOP, 200, 0);
        gv = (GridView) popupView.findViewById(R.id.device_grid);
        BBDeviceSelectionAdapter deviceSelectionAdapter = new BBDeviceSelectionAdapter(activity,
                BBDeviceSelectionAdapter.deviceGroupList, BBDeviceSelectionAdapter.deviceImages);
        gv.setAdapter(deviceSelectionAdapter);
    }

    public static void dismiss() {
        if (popupWindow != null) {
            popupWindow.dismiss();
            popupWindow = null;
        }
    }
}
