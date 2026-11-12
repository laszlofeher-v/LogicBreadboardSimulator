package com.logicbreadboard.simulator;

import java.io.DataInputStream;
import java.io.DataOutputStream;

/**
 * Created by laci on 11/3/15.
 */
public class DIPIoViewer extends DeviceIOViewer {
    String internalId;

    DIPIoViewer(short x0, short y0, short x1, short y1, boolean inv) {
        super(x0, y0, x1, y1, inv);
        internalId = new String("");
    }

    DIPIoViewer(short x0, short y0, short x1, short y1, boolean inv, int pos) {
        super(x0, y0, x1, y1, inv);
        internalId = new String(Integer.toString(pos));
    }

    @Override
    void load(DataInputStream inputStream) throws java.io.IOException {
        super.load(inputStream);
        internalId = inputStream.readUTF();
    }

    void save(DataOutputStream outputStream) throws java.io.IOException {
        super.save(outputStream);
        outputStream.writeUTF(internalId);
    }


}
