package com.logicbreadboard.simulator;

/**
 * Created on 11/21/15.
 */
public class VerilogOutputDeclarationCode extends VerilogCode {
    @Override
    public boolean gen(VerilogContext context) {
        context.action = VerilogContext.Action.OUTPUT_DECLARATION;
//        if (context.link.node.canCallFromHere(context.link.back.node)) {
//            context.link = context.link.back;
////            System.out.println(context.link.node.getIdent());
//            return context.link.node.code.gen(context);
//        } else {
            return true;
//        }
    }
}

