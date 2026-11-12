package com.logicbreadboard.simulator;

/**
 * Created by laci on 11/23/15.
 */
public class VerilogDescriptionCode extends VerilogCode {
    public boolean gen(VerilogContext context) {
        context.action = null;
//        if (context.link.node.canCallFromHere(context.link.back.node)) {
//            context.link = context.link.back;
//            System.out.println(context.link.node.getIdent());
//            return context.link.node.code.gen(context);
//        } else {
            return true;
//        }
    }

}
