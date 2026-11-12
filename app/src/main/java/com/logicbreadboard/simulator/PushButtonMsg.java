package com.logicbreadboard.simulator;

/**
 * Created by laci on 7/30/15.
 */
public class PushButtonMsg  extends Msg {
    //query
    float pointerX;
    float pointerY;
    // answer
//    Viewer viewer;
    int ioIndex;
    float distanceSqr;

    PushButtonMsg() {
        super();
//        viewer = null;
    }
}
