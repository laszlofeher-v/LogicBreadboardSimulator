package com.logicbreadboard.simulator;

/**
 * Created on 12/8/15.
 */
public class VerilogNOutputGateInstanceCode extends VerilogCode {
    @Override
    public boolean gen(VerilogContext context) {
        GateModel gateModel = null;
        switch (context.action) {
            case BUF_GATE_DECLARATION:
                gateModel = new BufferModel();
                break;
            case NOT_GATE_DECLARATION:
                gateModel = new InverterModel();
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
