package com.logicbreadboard.simulator;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created on 6/19/16.
 */
public class BBBoxDeviceSelectionAdapter extends BBDeviceSelectionAdapter {
    public BBBoxDeviceSelectionAdapter() {
    }

    public BBBoxDeviceSelectionAdapter(Activity activity, String[] nameList) {
        result = nameList;
        context = activity;
        inflater = (LayoutInflater) context.
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub

        Holder holder = new Holder();
        View rowView;
        rowView = inflater.inflate(R.layout.device_text_list, null);
        holder.tv = (TextView) rowView.findViewById(R.id.textView1);
//        holder.img = (ImageView) rowView.findViewById(R.id.imageView1);
//        System.out.println("position=" + position);
//        System.out.flush();
        String name = new String(result[position]);
        int index = name.lastIndexOf("_");
        if (index == -1) {
            index = name.lastIndexOf("/");
        }
        name = name.substring(index + 1);
        name = name.substring(0, name.lastIndexOf('.'));

        holder.tv.setText(name);
//        holder.img.setImageResource(imageId[position]);
        rowView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                //               v.setBackgroundColor(0x88880000);
//                Toast.makeText(context, "You Clicked " + result[position], Toast.LENGTH_LONG).show();
                createAndPut(position);
//                BBDeviceSelectionMenu.popupWindow.dismiss();
            }
        });
        rowView.setBackgroundColor(0xfaffffbb);
        return rowView;
    }


}
