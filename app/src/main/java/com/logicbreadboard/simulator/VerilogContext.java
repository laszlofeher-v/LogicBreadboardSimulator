package com.logicbreadboard.simulator;

import java.util.LinkedList;

/**
 * Created on 11/21/15.
 */
public class VerilogContext {
    public static final int DEFAULT_GATE_DELAY = 1;
    VerilogToken token;
    VerilogSymTable symTable;

    enum Action {
        NON_ANSI_LIST_OF_PORTS,
        INPUT_DECLARATION,
        OUTPUT_DECLARATION,
        INOUT_DECLARATION, WIRE_DECLARATION, AND_GATE_DECLARATION, OR_GATE_DECLARATION, NAND_GATE_DECLARATION, NOR_GATE_DECLARATION, XNOR_GATE_DECLARATION, XOR_GATE_DECLARATION, BUF_GATE_DECLARATION, NOT_GATE_DECLARATION,
    }

    Action action;
    VerilogRider.Link link;
    public PortViewer portViewer;
    public String lastIdent;
    public int unsignedValue;
    public LinkedList<DeviceModel> deviceModels;
    EditorModel editorModel;

    public VerilogContext() {
        deviceModels = new LinkedList<>();
    }
}
