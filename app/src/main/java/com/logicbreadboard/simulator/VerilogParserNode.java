package com.logicbreadboard.simulator;

/**
 * Created on 11/21/15.
 */

import android.util.Log;

import java.util.LinkedHashSet;

public class VerilogParserNode {
    public void tabulate(VerilogRider rider) {
        if (rider.indent > 0) {
           Log.i("verilog",String.format("%" + rider.indent + "s", ""));
        }
    }


    public static int maxMapSize = 0;
    public VerilogCode code;

    public VerilogParserNode() {
        code = VerilogSetImplemented.notImplemented;
    }

//    public boolean canCallFromHere(VerilogParserNode node) {
//        return false;
//    }

    protected boolean alreadyVisited(VerilogRider rider) {
        maxMapSize = Math.max(maxMapSize, rider.token.path.size());
        if (!rider.token.path.contains(this)) {
            rider.token.path.add(this);
            return false;
        } else {
            return true;
        }
    }

    public void nextToken(VerilogRider rider) {
        if (rider.token.next != null) {
            rider.token = rider.token.next;
            rider.token.path = new LinkedHashSet<>();
        }
    }

    public boolean next(VerilogRider rider) {
        return false;
    }

    public String getIdent() {
        return "";
    }

    public String getTerminal() {
        return "";
    }
}
