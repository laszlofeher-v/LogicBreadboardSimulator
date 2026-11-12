package com.logicbreadboard.simulator;

import java.util.LinkedList;

/**
 * Created on 6/1/15.
 */
public class SearchMsg extends Msg {
    //query
    float pointerX;
    float pointerY;
    float epsilon[];
    // answer
    LinkedList <Viewer> foundList;
    SearchMsg() {
        super();
        foundList = new LinkedList<Viewer>();
        epsilon=new float[1];
    }

    void addFound(Viewer v) {
        if (!foundList.contains(v)) {
            foundList.add(v);
        }
    }
}
