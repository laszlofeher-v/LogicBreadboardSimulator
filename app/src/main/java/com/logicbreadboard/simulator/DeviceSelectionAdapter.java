package com.logicbreadboard.simulator;

/**
 * Created by laci on 6/17/15.
 */

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.logicbreadboard.simulator.SchematicViewerFactory.DeviceGroup;
import com.logicbreadboard.simulator.SchematicViewerFactory.Flip_Flops;
import com.logicbreadboard.simulator.SchematicViewerFactory.Gates;

public class DeviceSelectionAdapter extends BaseAdapter {

    private final static String[] demuxNameList = SchematicViewerFactory.Demux.names();
    private final static String[] muxNameList = SchematicViewerFactory.Mux.names();
    private final static String[] gateNameList = Gates.names();
    private final static String[] flipFlopNameList = Flip_Flops.names();
    private final static String[] displayNameList = SchematicViewerFactory.Displays.names();
    private final static String[] sourceNameList = SchematicViewerFactory.Sources.names();
    private final static String[] connectionNameList = SchematicViewerFactory.Connections.names();
    private final static String[] transistorNameList = SchematicViewerFactory.Transistors.names();
    private final static String[] annotationNameList = SchematicViewerFactory.Annotations.names();
    private final static String[] boxNameList = SchematicViewerFactory.Box.names();
    //    private final static String[] BBDeviceNameList = SchematicViewerFactory.BreadBoardDevice.names();
    public static String[] deviceGroupList = DeviceGroup.names();
    public static int[] deviceImages = {
            R.drawable.ansi_and,
            R.drawable.ansi_jkflipflopviewer,
            R.drawable.ansi_labelviewer,
            R.drawable.ansi_clocklhviewer,
            R.drawable.ansi_ledviewer,
            R.drawable.ansi_mux8to1viewer,
            R.drawable.ansi_demux1to8viewer,
            R.drawable.ansi_nmosviewer,
            R.drawable.annotation,
            R.drawable.ansi_dipviewer14,
            R.drawable.ansi_dipviewer14,
            R.drawable.ansi_dipviewer14,
    };
    static String[] result;
    static int[] imageId;
    static byte selectedGroup;
    private static int[] gateImages = {
            R.drawable.ansi_buffer,
            R.drawable.ansi_inverter,
            R.drawable.ansi_and,
            R.drawable.ansi_nand,
            R.drawable.ansi_or,
            R.drawable.ansi_nor,
            R.drawable.ansi_exor,
            R.drawable.ansi_exnor,
            R.drawable.ansi_buffertsh,
            R.drawable.ansi_buffertsl,
            R.drawable.ansi_invertertsh,
            R.drawable.ansi_invertertsl};
    private static int[] flipFlopImages = {
            R.drawable.ansi_dflipflopviewer,
            R.drawable.ansi_dlatchviewer,
            R.drawable.ansi_jkflipflopviewer,
            R.drawable.ansi_monostableviewer
    };
    private static int[] displayImages = {
            R.drawable.ansi_ledviewer,
            R.drawable.ansi_probeviewer,
            R.drawable.ansi_decimaldisplayviewer,
            R.drawable.ansi_hexadecimaldisplayviewer,
    };
    private static int[] sourceImages = {
            R.drawable.ansi_logicaltogglebuttonviewerl,
            R.drawable.ansi_logicaltogglebuttonviewerh,
            R.drawable.ansi_highviewer,
            R.drawable.ansi_lowviewer,
            R.drawable.ansi_pushbuttonviewerl,
            R.drawable.ansi_pushbuttonviewerh,
            R.drawable.ansi_clockhlviewer,
            R.drawable.ansi_clocklhviewer,
    };
    private static int[] connectionImages = {
            R.drawable.ansi_labelviewer,
            R.drawable.ansi_bus_wire,
            R.drawable.ansi_portviewer,
            R.drawable.ansi_powerconnectorviewer,
    };
    private static int[] muxImages = {
            R.drawable.ansi_mux2to1viewer,
            R.drawable.ansi_mux4to1viewer,
            R.drawable.ansi_mux8to1viewer,
    };

    private static int[] demuxImages = {
            R.drawable.ansi_demux1to2viewer,
            R.drawable.ansi_demux1to4viewer,
            R.drawable.ansi_demux1to8viewer,
    };

    private static int[] transistorImages = {
            R.drawable.ansi_nmosviewer,
            R.drawable.ansi_pmosviewer,
    };

    private static int[] boxImages = {
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

    private static LayoutInflater inflater = null;
    Context context;

    public DeviceSelectionAdapter(Activity activity, String[] nameList, int[] images) {
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
                DeviceSelectionAdapter deviceSelectionAdapter = null;
                if (MenuStateMachine.inDeviceGroupSelection()) {
                    selectedGroup = (byte) (position + 1); //+1 because BBWires excluded
                    switch (DeviceGroup.values()[selectedGroup]) {
                        case Gates:
                            deviceSelectionAdapter = new DeviceSelectionAdapter((Activity) context,
                                    gateNameList, gateImages);
                            break;
                        case Flip_Flops:
                            deviceSelectionAdapter = new DeviceSelectionAdapter((Activity) context,
                                    flipFlopNameList, flipFlopImages);
                            break;
                        case Displays:
                            deviceSelectionAdapter = new DeviceSelectionAdapter((Activity) context,
                                    displayNameList, displayImages);
                            break;
                        case Sources:
                            deviceSelectionAdapter = new DeviceSelectionAdapter((Activity) context,
                                    sourceNameList, sourceImages);
                            break;
                        case Connections:
                            deviceSelectionAdapter = new DeviceSelectionAdapter((Activity) context,
                                    connectionNameList, connectionImages);
                            break;
                        case Mux:
                            deviceSelectionAdapter = new DeviceSelectionAdapter((Activity) context,
                                    muxNameList, muxImages);
                            break;
                        case Demux:
                            deviceSelectionAdapter = new DeviceSelectionAdapter((Activity) context,
                                    demuxNameList, demuxImages);
                            break;
                        case Transistors:
                            deviceSelectionAdapter = new DeviceSelectionAdapter((Activity) context,
                                    transistorNameList, transistorImages);
                            break;
                        case Annotation:
                            createAndPut(0);
                            return;
                        case Box:
                            deviceSelectionAdapter = new DeviceSelectionAdapter((Activity) context,
                                    boxNameList, boxImages);
                            break;
                        default:
                    }
                    DeviceSelectionMenu.gv.setAdapter(deviceSelectionAdapter);
                    MenuStateMachine.gotoDeviceSelection();
                } else {
                    createAndPut(position);
                }
//                DeviceSelectionMenu.popupWindow.dismiss();
            }
        });
        rowView.setBackgroundColor(0xfaffffbb);
        return rowView;
    }

    public void createAndPut(int position) {
        Viewer device = ViewerFactory.factory.newViewer(selectedGroup,
                position);
        if (device != null) {
            device.setDefaultNumberOfIos();
            if (!DeviceSelectionMenu.editorViewer.cursor.enabled) {
                EditorViewer.CursorPosOnCanvas.x = Math.round(DeviceSelectionMenu.editorViewer.xOrigo - DeviceSelectionMenu.editorViewer.xOffset);
                EditorViewer.CursorPosOnCanvas.y = Math.round(DeviceSelectionMenu.editorViewer.yOrigo - DeviceSelectionMenu.editorViewer.yOffset);
                DeviceSelectionMenu.editorViewer.cursor.set(EditorViewer.CursorPosOnCanvas.x, EditorViewer.CursorPosOnCanvas.y);
            }
            device.initPosition(DeviceSelectionMenu.editorViewer.cursor.getX(),
                    DeviceSelectionMenu.editorViewer.cursor.getY(), 0);
            device.cursorStep(DeviceSelectionMenu.editorViewer.cursor);
            DeviceSelectionMenu.editorViewer.add(device);
        }
        DeviceSelectionMenu.dismiss();
        MenuStateMachine.gotoEditing();
        ((MainActivity) context).setContextMenuVisibility();
    }

    public class Holder {
        TextView tv;
        ImageView img;
    }
}

