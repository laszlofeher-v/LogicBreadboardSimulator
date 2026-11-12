package com.logicbreadboard.simulator;

/**
 * Created on 12/5/15.
 */
public class VerilogUnsignedNumberCode extends VerilogCode {
    @Override
    public boolean gen(VerilogContext context) {
        boolean retVal = false;
        context.unsignedValue = context.token.intValue;
        retVal = setNextToken(context, retVal);
        return retVal;
    }
}
