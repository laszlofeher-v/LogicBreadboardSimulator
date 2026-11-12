package com.logicbreadboard.simulator;

/**
 * Created on 11/28/15.
 */
public class VerilogNetIdentifierCode extends VerilogCode {
    @Override
    public boolean gen(VerilogContext context) {
        if (context.action == VerilogContext.Action.WIRE_DECLARATION) {
            context.symTable.addWire(context.token.ident);
        }
        return true;
    }

}
