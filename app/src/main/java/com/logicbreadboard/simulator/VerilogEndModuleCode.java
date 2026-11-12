package com.logicbreadboard.simulator;

/**
 * Created on 11/21/15.
 */
public class VerilogEndModuleCode extends VerilogCode {
    @Override
    public boolean gen(VerilogContext context) {
        context.symTable.leaveModule();
        return true;
    }
}
