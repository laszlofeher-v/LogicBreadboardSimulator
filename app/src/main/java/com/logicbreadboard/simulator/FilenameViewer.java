package com.logicbreadboard.simulator;

import android.util.Log;

import java.io.DataOutputStream;

/**
 * Created on 12/14/15.
 */

public class FilenameViewer extends Viewer {

    @Override
    void save(DataOutputStream outputStream) throws java.io.IOException {
        super.save(outputStream);
        outputStream.writeShort(SchematicViewerFactory.META_VIEWER_GROUP);
        outputStream.writeShort(SchematicViewerFactory.FILENAME_VIEWER_SYM);
        String filename=Settings.getProjectFileName().substring(Settings.getProjectFileName().lastIndexOf("/") + 1);
        Log.i("META","filename="+filename);
        outputStream.writeUTF(filename);
    }

}

