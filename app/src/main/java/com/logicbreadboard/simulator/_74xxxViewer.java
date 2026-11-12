package com.logicbreadboard.simulator;

import android.graphics.Canvas;
import android.util.Log;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.util.regex.Pattern;

/**
 * Created on 11/2/15.
 */
public class _74xxxViewer extends DIPViewer {
    private String name = "7400";

    public String getName() {
        return name;
    }

    static private final int CONTEXT_MENU_MASK =
            ContextMenuIcons.COPY |
                    ContextMenuIcons.DELETE |
                    ContextMenuIcons.ROTATE |
                    ContextMenuIcons.ALIGN_HORIZONTAL | ContextMenuIcons.ALIGN_VERTICAL |
                    ContextMenuIcons.LOCK | ContextMenuIcons.UNLOCK |
                    ContextMenuIcons.UNSELECT;

    @Override
    public int getContextMenuMask() {
        return CONTEXT_MENU_MASK;
    }

    protected int BOX_WIDTH = 7 * unit;
    protected int BOX_HEIGHT = 3 * unit;
    private int numberOfPorts = 0;
    private String[] portLabels;

    public int getWidth() {
        return BOX_WIDTH;
    }

    public int getHeight() {
        return BOX_HEIGHT;
    }

    @Override
    public void setDefaultNumberOfIos() {
        setNumberOfIos((short) numberOfPorts);
    }

    _74xxxViewer() {
        model = new _DIPModel();
    }

    _74xxxViewer(String name) {
        model = new _DIPModel();
        ((_DIPModel) model).fileName = "builtin_subc_ics/" + name;// from assets
        int index = ((DIPModel) model).fileName.lastIndexOf("_");
        if (index == -1) {
            index = ((DIPModel) model).fileName.lastIndexOf("/");
        }
        this.name = ((DIPModel) model).fileName.substring(index + 1);
        this.name = this.name.substring(0, this.name.lastIndexOf('.'));
        ((_DIPModel) model).loadContent();
        setBoxWidth();
        String portLabelString = ((_DIPModel) model).editorModel.getPortAnnotation();
        if (portLabelString != null) {
            portLabels = portLabelString.split(Pattern.quote("."));
            if (portLabels != null) {
                Log.i("_74XXX", "portLabelString=" + portLabelString + " length=" + portLabels.length);
                if (portLabels.length != numberOfPorts + 1) {
                    portLabels = null;
                }
            } else {
                Log.i("_74XXX", "portLabels==null");
            }
        } else {
            Log.i("_74XXX", "portLabelString==null");
        }
    }


    private void setBoxWidth() {
        numberOfPorts = model.getNumberOfPorts();
        if (numberOfPorts <= 8) {
            numberOfPorts = 8;
        } else if (numberOfPorts <= 14) {
            numberOfPorts = 14;
        } else if (numberOfPorts <= 16) {
            numberOfPorts = 16;
        } else if (numberOfPorts <= 18) {
            numberOfPorts = 18;
        } else if (numberOfPorts <= 24) {
            numberOfPorts = 24;
        } else if (numberOfPorts <= 28) {
            numberOfPorts = 28;
        } else if (numberOfPorts <= 32) {
            numberOfPorts = 32;
        } else if (numberOfPorts <= 40) {
            numberOfPorts = 40;
        } else if (numberOfPorts <= 48) {
            numberOfPorts = 48;
        } else if (numberOfPorts <= 64) {
            numberOfPorts = 64;
        }
        BOX_WIDTH = (numberOfPorts / 2) * unit;
    }

    @Override
    public short getGroup() {
        return (short) BBViewerFactory.BBDeviceGroup._74XX.ordinal();
    }

    @Override
    public short getSymbol() {
        return (short) 0; // The loadable file name identifies the device
    }

    @Override
    protected void writeText(Canvas canvas, int index) {
        float size = Context.textPaint.getTextSize();
        Context.textPaint.setTextSize(BOX_HEIGHT / 3);
        canvas.drawText(name, left + unit + STROKE_WITH, bottom - (getHeight() / 3), Context.textPaint);
        int portTextSize = BOX_HEIGHT / 10;
        Context.textPaint.setTextSize(portTextSize);
        if (portLabels != null) {
            for (int pos = 0; pos < portLabels.length - 1; pos++) {
                float x;
                float y;
                if (pos < numberOfPorts / 2) {
                    x = left + unit / 4 + pos * unit + unit / 8;
                    y = bottom - unit / 2;
                } else {
                    x = left + unit / 4 + (numberOfPorts - pos - 1) * unit + unit / 8;
                    y = top + portTextSize + unit / 2;
                }
                canvas.drawText(portLabels[pos + 1], x, y, Context.textPaint);
            }
        }
        Context.textPaint.setTextSize(size);
    }

    @Override
    void load(DataInputStream inputStream) throws java.io.IOException {
        super.load(inputStream);
        setBoxWidth();
//        ((DIPModel) model).fileName = inputStream.readUTF();
//        ((DIPModel) model).loadContent();
        int index = ((DIPModel) model).fileName.lastIndexOf("/");
        index = ((DIPModel) model).fileName.lastIndexOf("_");
        if (index == -1) {
            index = ((DIPModel) model).fileName.lastIndexOf("/");
        }
        this.name = ((DIPModel) model).fileName.substring(index + 1);
        this.name = this.name.substring(0, this.name.lastIndexOf('.'));
        setBoxWidth();
        String portLabelString = ((_DIPModel) model).editorModel.getPortAnnotation();
        if (portLabelString != null) {
            portLabels = portLabelString.split(Pattern.quote("."));
            if (portLabels != null) {
                Log.i("_74XXX", "portLabelString=" + portLabelString + " length=" + portLabels.length);
                if (portLabels.length != numberOfPorts + 1) {
                    portLabels = null;
                }
            } else {
                Log.i("_74XXX", "portLabels==null");
            }
        } else {
            Log.i("_74XXX", "portLabelString==null");
        }
    }

    @Override
    void save(DataOutputStream outputStream) throws java.io.IOException {
        super.save(outputStream);
//        outputStream.writeUTF(((DIPModel) model).fileName);
    }



}
