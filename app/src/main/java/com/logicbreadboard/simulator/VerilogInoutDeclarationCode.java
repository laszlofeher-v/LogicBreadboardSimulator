package com.logicbreadboard.simulator;

/**
 * Created on 11/21/15.
 */
public class VerilogInoutDeclarationCode extends VerilogCode {
    @Override
    public boolean gen(VerilogContext context) {
        context.action = VerilogContext.Action.INOUT_DECLARATION;
            return true;
    }
}
