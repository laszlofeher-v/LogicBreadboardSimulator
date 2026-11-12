package com.logicbreadboard.simulator;

/**
 * Created on 11/28/15.
 */
public class VerilogWireDeclarationCode extends VerilogCode {
    @Override
    public boolean gen(VerilogContext context) {
        context.action = VerilogContext.Action.WIRE_DECLARATION;
        return true;
    }

}
