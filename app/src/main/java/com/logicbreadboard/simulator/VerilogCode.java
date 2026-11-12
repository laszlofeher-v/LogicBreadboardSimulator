package com.logicbreadboard.simulator;

import android.util.Log;

/**
 * Created on 11/21/15.
 */
public class VerilogCode {
    VerilogCode() {

    }
    public boolean setNextToken(VerilogContext context, boolean retVal) {
        if (context.link != null && context.link.node != null) {
            if (context.token.sym.name().equals(context.link.node.getTerminal())) {
                Log.i("Verilog", "token:" + context.token.sym.name() + "  ====================================  " + "node:" + context.link.node.getTerminal
                        ());
                context.token = context.token.next;
            } else {
                Log.i("Verilog", "token:" + context.token.sym.name() + "  ####################################  " + "node:" + context.link.node.getTerminal());
            }
            retVal = true;
        }
        return retVal;
    }

    public boolean gen(VerilogContext context) {
//        System.out.println("Not yet implemented.");
        return false;
    }
}
