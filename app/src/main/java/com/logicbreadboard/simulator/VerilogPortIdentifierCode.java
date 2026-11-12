package com.logicbreadboard.simulator;

/**
 * Created on 11/21/15.
 */
public class VerilogPortIdentifierCode extends VerilogCode {
    @Override
    public boolean gen(VerilogContext context) {
        if (context.action == VerilogContext.Action.NON_ANSI_LIST_OF_PORTS) {
            PortViewer portViewer = new PortViewer();
            portViewer.setDefaultNumberOfIos();
//            portViewer.setLabel(context.token.ident);
            context.editorModel.add(portViewer);
            context.symTable.addPort(context.token.ident,portViewer);
        } else if (context.action == VerilogContext.Action.INPUT_DECLARATION) {
            context.portViewer = new PortViewer();
            context.symTable.setInputPort(context);
        } else if (context.action == VerilogContext.Action.OUTPUT_DECLARATION) {
            context.portViewer = new PortViewer();
            context.symTable.setOutputPort(context);
        } else if (context.action == VerilogContext.Action.INOUT_DECLARATION) {
            context.portViewer = new PortViewer();
            context.symTable.setInoutPort(context);
        }
        return true;
    }

}
