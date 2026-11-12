package com.logicbreadboard.simulator;

/**
 * Created on 12/4/15.
 */
public class VerilogNInputGateTypeCode extends VerilogCode {
    @Override
    public boolean gen(VerilogContext context) {
        switch (context.token.sym) {
            case AND:context.action = VerilogContext.Action.AND_GATE_DECLARATION;
                break;
            case OR:context.action = VerilogContext.Action.OR_GATE_DECLARATION;
                break;
            case NAND:context.action = VerilogContext.Action.NAND_GATE_DECLARATION;
                break;
            case NOR:context.action = VerilogContext.Action.NOR_GATE_DECLARATION;
                break;
            case XOR:context.action = VerilogContext.Action.XOR_GATE_DECLARATION;
                break;
            case XNOR:context.action = VerilogContext.Action.XNOR_GATE_DECLARATION;
                break;
            default:
        }
        context.unsignedValue=VerilogContext.DEFAULT_GATE_DELAY;
        return true;
    }
}
