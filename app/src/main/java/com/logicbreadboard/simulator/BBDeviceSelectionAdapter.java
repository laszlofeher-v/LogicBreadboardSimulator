package com.logicbreadboard.simulator;

/**
 * Created by laci on 6/17/15.
 */

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;


public class BBDeviceSelectionAdapter extends BaseAdapter {
    private final static String[] BBDeviceNameList = BBViewerFactory.BreadBoardDevice.names();
    public static String[] deviceGroupList = BBViewerFactory.BBDeviceGroup.names();
    public static int[] deviceImages = {
            R.drawable.ansi_dipviewer14,
            R.drawable.ansi_bbtactile1_1swviewer,
            R.drawable.ansi_dipviewer14,
            R.drawable.annotation,
            R.drawable.annotation,
            R.drawable.annotation,
            R.drawable.annotation,
            R.drawable.annotation,
            R.drawable.annotation,
            R.drawable.annotation,
            R.drawable.annotation,
            R.drawable.annotation,
            R.drawable.annotation,
            R.drawable.annotation,
            R.drawable.annotation,
            R.drawable.annotation,
            R.drawable.annotation,
    };
    static String[] result;
    static int[] imageId;
    static byte selectedGroup;
    protected final static String[] boxNameList = BBViewerFactory.Box.names();
    protected static int[] boxImages = {
            R.drawable.ansi_dipviewer8,
            R.drawable.ansi_dipviewer14,
            R.drawable.ansi_dipviewer16,
            R.drawable.ansi_dipviewer18,
            R.drawable.ansi_dipviewer24,
            R.drawable.ansi_dipviewer24,
            R.drawable.ansi_dipviewer28,
            R.drawable.ansi_dipviewer32,
            R.drawable.ansi_dipviewer40,
            R.drawable.ansi_dipviewer64,
            R.drawable.ansi_dipviewer14,
            R.drawable.ansi_dipviewer14,
            R.drawable.ansi_dipviewer14,
            R.drawable.ansi_dipviewer14,
            R.drawable.ansi_dipviewer14,
    };
    private final static String[] dipSwNameList = BBViewerFactory.BB_Switch.names();
    private static int[] dipSwImages = {
            R.drawable.ansi_bbtactile1_1swviewer,
            R.drawable.ansi_bbsw16_8viewer,
    };
    private static String[] _74XXNameList;
//    private int[] _74XXImages;


    private boolean listAssetFiles(String path) {
        try {
            String[] list = MainActivity.assetManager.list(path);
            _74XXNameList = new String[list.length];
            int i = 0;
            for (String file : list) {
                _74XXNameList[i++] = file;
            }
        } catch (IOException e) {
            return false;
        }
        return true;
    }

    protected static LayoutInflater inflater = null;
    Context context;

    public BBDeviceSelectionAdapter() {
    }

    public BBDeviceSelectionAdapter(Activity activity, String[] nameList, int[] images) {
        result = nameList;
        context = activity;
        imageId = images;
        inflater = (LayoutInflater) context.
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return result.length;
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub

        Holder holder = new Holder();
        View rowView;
        rowView = inflater.inflate(R.layout.device_list, null);
        holder.tv = (TextView) rowView.findViewById(R.id.textView1);
        holder.img = (ImageView) rowView.findViewById(R.id.imageView1);
//        System.out.println("position=" + position);
//        System.out.flush();
        holder.tv.setText(result[position]);
        holder.img.setImageResource(imageId[position]);
        rowView.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                //               v.setBackgroundColor(0x88880000);
//                Toast.makeText(context, "You Clicked " + result[position], Toast.LENGTH_LONG).show();
                BBDeviceSelectionAdapter bbDeviceSelectionAdapter = null;
                if (MenuStateMachine.inBBDeviceGroupSelection()) {
                    selectedGroup = (byte) (position + BBViewerFactory.BBDeviceGroup.Box.ordinal()); //because groups before Box are excluded
                    switch (BBViewerFactory.BBDeviceGroup.values()[selectedGroup]) {
                        case Box:
                            bbDeviceSelectionAdapter = new BBDeviceSelectionAdapter((Activity) context,
                                    boxNameList, boxImages);
                            break;
                        case Switches:
                            bbDeviceSelectionAdapter = new BBDeviceSelectionAdapter((Activity) context,
                                    dipSwNameList, dipSwImages);
                            break;
                        case _74XX:
                            listAssetFiles("builtin_subc_ics");
                            bbDeviceSelectionAdapter = new BBBoxDeviceSelectionAdapter((Activity) context, _74XXNameList);
                            break;
                        default:
                            Log.i("BBgetview", "null");
                    }
                    BBDeviceSelectionMenu.gv.setAdapter(bbDeviceSelectionAdapter);
                    MenuStateMachine.gotoBBDeviceSelection();
                } else {
                    createAndPut(position);
                }
//                BBDeviceSelectionMenu.popupWindow.dismiss();
            }
        });
        rowView.setBackgroundColor(0xfaffffbb);
        return rowView;
    }

    public void createAndPut(int position) {
//        if ic then create _"7400" and pass _74XXNameList[pos]
        Viewer device;
        if (BBViewerFactory.BBDeviceGroup.values()[selectedGroup] == BBViewerFactory.BBDeviceGroup._74XX) {
            device = ViewerFactory.factory.newViewer(selectedGroup, _74XXNameList[position]);

        } else {
            device = ViewerFactory.factory.newViewer(selectedGroup,
                    position);
        }
        if (device != null) {
            device.setDefaultNumberOfIos();
            if (!BBDeviceSelectionMenu.editorViewer.cursor.enabled) {
                EditorViewer.CursorPosOnCanvas.x = Math.round(BBDeviceSelectionMenu.editorViewer.xOrigo - BBDeviceSelectionMenu.editorViewer.xOffset);
                EditorViewer.CursorPosOnCanvas.y = Math.round(BBDeviceSelectionMenu.editorViewer.yOrigo - BBDeviceSelectionMenu.editorViewer.yOffset);
                BBDeviceSelectionMenu.editorViewer.cursor.set(EditorViewer.CursorPosOnCanvas.x, EditorViewer.CursorPosOnCanvas.y);
            }
            EditorViewer.CursorPosOnCanvas.x = Math.round(BreadBoardViewer.xOrigo - BreadBoardViewer.staticXOffset);
            EditorViewer.CursorPosOnCanvas.y = Math.round(BreadBoardViewer.yOrigo - BreadBoardViewer.staticYOffset);
            BBDeviceSelectionMenu.editorViewer.cursor.setNoCheck(EditorViewer.CursorPosOnCanvas.x, EditorViewer.CursorPosOnCanvas.y);
            Log.i("BBDEVSEL", BBDeviceSelectionMenu.editorViewer.cursor.getX() + " " + BBDeviceSelectionMenu.editorViewer.cursor.getY());
            device.initPosition(BBDeviceSelectionMenu.editorViewer.cursor.getX(),
                    BBDeviceSelectionMenu.editorViewer.cursor.getY(), 0, BreadBoardViewer.rasterSize);
//            device.cursorStep(BBDeviceSelectionMenu.editorViewer.cursor);
            BBDeviceSelectionMenu.editorViewer.add(device);
        }
        BBDeviceSelectionMenu.dismiss();
        MenuStateMachine.gotoBBEditing();
        ((MainActivity) context).setContextMenuVisibility();
    }

    public class Holder {
        TextView tv;
        ImageView img;
    }
}

