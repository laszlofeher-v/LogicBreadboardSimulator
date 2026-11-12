package com.logicbreadboard.simulator;

/**
 * Created on 11/21/15.
 */
public class VerilogListOfPortsCode extends VerilogCode {
    @Override
    public boolean gen(VerilogContext context) {
        context.action = VerilogContext.Action.NON_ANSI_LIST_OF_PORTS;
        return true;
    }
}
