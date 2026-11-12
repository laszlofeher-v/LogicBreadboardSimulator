package com.logicbreadboard.simulator;

/**
 * Created on 12/8/15.
 */
public class VerilogNOutputGateTypeCode extends VerilogCode {
    @Override
    public boolean gen(VerilogContext context) {
        switch (context.token.sym) {
            case BUF:
                context.action = VerilogContext.Action.BUF_GATE_DECLARATION;
                break;
            case NOT:
                context.action = VerilogContext.Action.NOT_GATE_DECLARATION;
                break;
            default:
        }
        context.unsignedValue = VerilogContext.DEFAULT_GATE_DELAY;
        return true;
    }
}

