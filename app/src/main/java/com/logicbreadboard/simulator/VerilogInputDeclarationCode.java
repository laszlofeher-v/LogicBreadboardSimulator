package com.logicbreadboard.simulator;

/**
 * Created on 11/21/15.
 */
public class VerilogInputDeclarationCode extends VerilogCode {
    @Override
    public boolean gen(VerilogContext context) {
        context.action = VerilogContext.Action.INPUT_DECLARATION;
            return true;
    }
}
