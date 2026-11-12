package com.logicbreadboard.simulator;

/**
 * Created by laci on 7/17/15.
 */
public class SelectMsg extends Msg {
    //query
    float pointerX;
    float pointerY;
    float epsilon[];
    // answer
//    Viewer viewer;
    Viewer viewer;
    DeviceIOViewer ioViewer;

    SelectMsg() {
        super();
        epsilon=new float[1];
        viewer = null;
    }

}
