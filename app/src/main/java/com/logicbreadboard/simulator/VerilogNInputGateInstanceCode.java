package com.logicbreadboard.simulator;

/**
 * Created on 12/4/15.
 */
public class VerilogNInputGateInstanceCode extends VerilogCode {
    @Override
    public boolean gen(VerilogContext context) {
        GateModel gateModel = null;
        switch (context.action) {
            case AND_GATE_DECLARATION:
                gateModel = new AndModel();
                break;
            case OR_GATE_DECLARATION:
                gateModel = new OrModel();
                break;
            case NAND_GATE_DECLARATION:
                gateModel = new NandModel();
                break;
            case NOR_GATE_DECLARATION:
                gateModel = new NorModel();
                break;
            case XOR_GATE_DECLARATION:
                gateModel = new ExorModel();
                break;
            case XNOR_GATE_DECLARATION:
                gateModel = new ExnorModel();
                break;
            default:
        }
        if (gateModel != null) {
            gateModel.delays[0] = context.unsignedValue;
            context.deviceModels.add(gateModel);
        }
        return true;
    }

}
