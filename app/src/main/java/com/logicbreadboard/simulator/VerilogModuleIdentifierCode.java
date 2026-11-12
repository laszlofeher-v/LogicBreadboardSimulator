package com.logicbreadboard.simulator;

/**
 * Created on 11/21/15.
 */
public class VerilogModuleIdentifierCode extends VerilogCode {
    @Override
    public boolean gen(VerilogContext context) {
        context.symTable.addModule(context.token.ident);
//        if (context.link.node.canCallFromHere(context.link.back.node)) {
//            context.link = context.link.back;
////            System.out.println(context.link.node.getIdent());
//            return context.link.node.code.gen(context);
//        } else {
            return true;
//        }
    }
}
