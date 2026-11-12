package com.logicbreadboard.simulator;

import java.io.DataOutputStream;

/**
 * Created on 11/29/15.
 */
public class ZoomPosViewer extends Viewer {
    float zoom;
    float xOffset;
    float yOffset;

    @Override
    void save(DataOutputStream outputStream) throws java.io.IOException {
        super.save(outputStream);
        outputStream.writeShort(SchematicViewerFactory.META_VIEWER_GROUP);
        outputStream.writeShort(SchematicViewerFactory.ZOOMPOS_VIEWER_SYM);
        outputStream.writeFloat(zoom);
        outputStream.writeFloat(xOffset);
        outputStream.writeFloat(yOffset);
    }

}
