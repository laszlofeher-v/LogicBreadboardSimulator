package com.logicbreadboard.simulator;

import android.util.Log;
import android.widget.Toast;

/**
 * Created on 11/23/15.
 */
public class VerilogSetImplemented {

    static VerilogCode implementedTerminal = new ImplementedTerminal();
    static VerilogCode implemented = new Implemented();
    static VerilogCode notImplemented = new NotImplemented();
    static boolean toastDisplayed = false;

    static class Implemented extends VerilogCode {
        @Override
        public boolean gen(VerilogContext context) {
            boolean retVal = true;
            VerilogRider.Link link = context.link;
            if (link.node.getIdent().equals("unsigned_number")) {
                Log.i("Verilog", context.link.node.getIdent());
            }
//            while (context.link != null && context.link.node != null && context.link.back != null && context.link.node.canCallFromHere(context.link.back.node)) {
//                context.link = context.link.back;
//                if (context.link.node.getIdent().length() > 0) {
//                    System.out.println(context.link.node.getIdent());
//                }
//                if (context.link != null && context.link.node != null) {
//                    context.link.node.code.gen(context);
//                }
//                retVal = true;
//            }
//            if (context.link != null) {
////                context.link = context.link.back;
//            }
            return retVal;
        }
    }

    static class ImplementedTerminal extends VerilogCode {
        @Override
        public boolean gen(VerilogContext context) {
            boolean retVal = false;
            retVal = setNextToken(context, retVal);
            return retVal;
        }
    }


    static class NotImplemented extends VerilogCode {
        @Override
        public boolean gen(VerilogContext context) {
            Log.i("Verilog", "----------------------------------------- Not yet:" + context.link.node.getIdent() + " " + context.link.node.toString());
//            throw new NotYetImplemented("Not yet implemented: " + context.token + " " + context.link.node.getIdent() + " " + context.link.node.toString());
            if (!toastDisplayed) {
                toastDisplayed = true;
                if (context.token.ident != null) {
                    Toast.makeText(MainActivity.context, "Not yet implemented: " + context.token.ident, Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(MainActivity.context, "Not yet implemented: " + context.token.sym, Toast.LENGTH_LONG).show();
                }
            }
            return false;
        }

    }

    static void set(VerilogParser p) {

        p.descriptionProduct.code = new VerilogDescriptionCode();
        p.descriptionProduct.expression.code = implemented;
        p.descriptionProduct.expression.terms[0].code = implemented;
        p.descriptionProduct.expression.terms[0].factors[0].code = implemented;

        p.module_declarationProduct.code = implemented;
        p.module_declarationProduct.expression.code = implemented;
        p.module_declarationProduct.expression.terms[0].code = implemented;
        p.module_declarationProduct.expression.terms[0].factors[0].code = implemented;
        p.module_declarationProduct.expression.terms[0].factors[2].code = implemented;
        p.module_declarationProduct.expression.terms[0].factors[2].expression.code = implemented;
        p.module_declarationProduct.expression.terms[0].factors[2].expression.terms[0].code = implemented;
        p.module_declarationProduct.expression.terms[0].factors[2].expression.terms[0].factors[0].code = implemented;
        p.module_declarationProduct.expression.terms[0].factors[3].code = implemented;

        p.module_nonansi_headerProduct.code = implemented;
        p.module_nonansi_headerProduct.expression.code = implemented;
        p.module_nonansi_headerProduct.expression.terms[0].code = implemented;
        p.module_nonansi_headerProduct.expression.terms[0].factors[0].code = implementedTerminal;
        p.module_nonansi_headerProduct.expression.terms[0].factors[1].code = implemented;
        p.module_nonansi_headerProduct.expression.terms[0].factors[3].code = implemented;
        p.module_nonansi_headerProduct.expression.terms[0].factors[5].code = implemented;
        p.module_nonansi_headerProduct.expression.terms[0].factors[6].code = implementedTerminal;

        p.module_keywordProduct.code = implemented;
        p.module_keywordProduct.expression.code = implemented;
        p.module_keywordProduct.expression.terms[0].code = implemented;
        p.module_keywordProduct.expression.terms[0].factors[0].code = implementedTerminal;

        p.module_identifierProduct.code = new VerilogModuleItemCode();
        p.module_identifierProduct.expression.code = implemented;
        p.module_identifierProduct.expression.terms[0].code = implemented;
        p.module_identifierProduct.expression.terms[0].factors[0].code = new VerilogModuleIdentifierCode();

        p.list_of_portsProduct.code = new VerilogListOfPortsCode();
        p.list_of_portsProduct.expression.code = implemented;
        p.list_of_portsProduct.expression.terms[0].code = implemented;
        p.list_of_portsProduct.expression.terms[0].factors[0].code = implementedTerminal; // left bracket
        p.list_of_portsProduct.expression.terms[0].factors[1].code = implemented;
        p.list_of_portsProduct.expression.terms[0].factors[2].code = implemented;
        p.list_of_portsProduct.expression.terms[0].factors[2].expression.code = implemented;
        p.list_of_portsProduct.expression.terms[0].factors[2].expression.terms[0].code = implemented;
        p.list_of_portsProduct.expression.terms[0].factors[2].expression.terms[0].factors[0].code = implementedTerminal; // comma
        p.list_of_portsProduct.expression.terms[0].factors[2].expression.terms[0].factors[1].code = implemented; // port product
        p.list_of_portsProduct.expression.terms[0].factors[3].code = implementedTerminal; // right bracket

        p.port_identifierProduct.code = new VerilogPortIdentifierCode();
        p.port_identifierProduct.expression.code = implemented;
        p.port_identifierProduct.expression.terms[0].code = implemented;
        p.port_identifierProduct.expression.terms[0].factors[0].code = implemented;

        p.identifierProduct.code = implemented;
        p.identifierProduct.expression.code = implemented;
        p.identifierProduct.expression.terms[2].code = implemented;
        p.identifierProduct.expression.terms[2].factors[0].code = implementedTerminal;
        p.identifierProduct.expression.terms[3].code = implemented;
        p.identifierProduct.expression.terms[3].factors[0].code = implementedTerminal;

        p.portProduct.code = implemented;
        p.portProduct.expression.code = implemented;
        p.portProduct.expression.terms[0].code = implemented;
        p.portProduct.expression.terms[0].factors[0].code = implemented;
        p.portProduct.expression.terms[0].factors[0].expression.code = implemented;
        p.portProduct.expression.terms[0].factors[0].expression.terms[0].code = implemented;
        p.portProduct.expression.terms[0].factors[0].expression.terms[0].factors[0].code = implemented;

        p.port_expressionProduct.code = implemented;
        p.port_expressionProduct.expression.code = implemented;
        p.port_expressionProduct.expression.terms[0].code = implemented;
        p.port_expressionProduct.expression.terms[0].factors[0].code = implemented;

        p.port_referenceProduct.code = implemented;
        p.port_referenceProduct.expression.code = implemented;
        p.port_referenceProduct.expression.terms[0].code = implemented;
        p.port_referenceProduct.expression.terms[0].factors[0].code = implemented;

        p.module_itemProduct.code = implemented;
        p.module_itemProduct.expression.code = implemented;
        p.module_itemProduct.expression.terms[0].code = implemented;
        p.module_itemProduct.expression.terms[0].factors[0].code = implemented;
        p.module_itemProduct.expression.terms[0].factors[1].code = implementedTerminal;
        p.module_itemProduct.expression.terms[1].code = implemented;
        p.module_itemProduct.expression.terms[1].factors[0].code = implemented;

        p.port_declarationProduct.code = implemented;
        p.port_declarationProduct.expression.code = implemented;
        p.port_declarationProduct.expression.terms[0].code = implemented;
        p.port_declarationProduct.expression.terms[0].factors[1].code = implemented;
        p.port_declarationProduct.expression.terms[1].code = implemented;
        p.port_declarationProduct.expression.terms[1].factors[1].code = implemented;
        p.port_declarationProduct.expression.terms[2].code = implemented;
        p.port_declarationProduct.expression.terms[2].factors[1].code = implemented;

        p.input_declarationProduct.code = new VerilogInputDeclarationCode();
        p.input_declarationProduct.expression.code = implemented;
        p.input_declarationProduct.expression.terms[0].code = implemented;
        p.input_declarationProduct.expression.terms[0].factors[0].code = implementedTerminal;
        p.input_declarationProduct.expression.terms[0].factors[1].code = implemented;
        p.input_declarationProduct.expression.terms[0].factors[2].code = implemented;

        p.port_typeProduct.code = implemented;
        p.port_typeProduct.expression.code = implemented;
        p.port_typeProduct.expression.terms[0].code = implemented;
        p.port_typeProduct.expression.terms[0].factors[2].code = implemented;

        p.list_of_port_identifiersProduct.code = implemented;
        p.list_of_port_identifiersProduct.expression.code = implemented;
        p.list_of_port_identifiersProduct.expression.terms[0].code = implemented;
        p.list_of_port_identifiersProduct.expression.terms[0].factors[0].code = implemented;
        p.list_of_port_identifiersProduct.expression.terms[0].factors[2].code = implemented;
        p.list_of_port_identifiersProduct.expression.terms[0].factors[2].expression.code = implemented;
        p.list_of_port_identifiersProduct.expression.terms[0].factors[2].expression.terms[0].code = implemented;
        p.list_of_port_identifiersProduct.expression.terms[0].factors[2].expression.terms[0].factors[0].code = implementedTerminal;
        p.list_of_port_identifiersProduct.expression.terms[0].factors[2].expression.terms[0].factors[1].code = implemented;

        p.output_declarationProduct.code = new VerilogOutputDeclarationCode();
        p.output_declarationProduct.expression.code = implemented;
        p.output_declarationProduct.expression.terms[0].code = implemented;
        p.output_declarationProduct.expression.terms[0].factors[0].code = implementedTerminal;
        p.output_declarationProduct.expression.terms[0].factors[1].code = implemented;
        p.output_declarationProduct.expression.terms[0].factors[2].code = implemented;

        p.non_port_module_itemProduct.code = implemented;
        p.non_port_module_itemProduct.expression.code = implemented;
        p.non_port_module_itemProduct.expression.terms[1].code = implemented;
        p.non_port_module_itemProduct.expression.terms[1].factors[0].code = implemented;

        p.module_or_generate_itemProduct.code = implemented;
        p.module_or_generate_itemProduct.expression.code = implemented;
        p.module_or_generate_itemProduct.expression.terms[1].code = implemented;
        p.module_or_generate_itemProduct.expression.terms[1].factors[1].code = implemented;
        p.module_or_generate_itemProduct.expression.terms[4].code = implemented;
        p.module_or_generate_itemProduct.expression.terms[4].factors[0].code = implemented;
        p.module_or_generate_itemProduct.expression.terms[4].factors[0].expression.code = implemented;
        p.module_or_generate_itemProduct.expression.terms[4].factors[0].expression.terms[0].code = implemented;
        p.module_or_generate_itemProduct.expression.terms[4].factors[0].expression.terms[0].factors[0].code = implemented;
        p.module_or_generate_itemProduct.expression.terms[4].factors[1].code = implemented;

        p.module_common_itemProduct.code = implemented;
        p.module_common_itemProduct.expression.code = implemented;
        p.module_common_itemProduct.expression.terms[0].code = implemented;
        p.module_common_itemProduct.expression.terms[0].factors[0].code = implemented;

        p.module_or_generate_item_declarationProduct.code = implemented;
        p.module_or_generate_item_declarationProduct.expression.code = implemented;
        p.module_or_generate_item_declarationProduct.expression.terms[0].code = implemented;
        p.module_or_generate_item_declarationProduct.expression.terms[0].factors[0].code = implemented;
        p.module_or_generate_item_declarationProduct.expression.terms[3].factors[3].code = implementedTerminal;

        p.package_or_generate_item_declarationProduct.code = implemented;
        p.package_or_generate_item_declarationProduct.expression.code = implemented;
        p.package_or_generate_item_declarationProduct.expression.terms[0].code = implemented;
        p.package_or_generate_item_declarationProduct.expression.terms[0].factors[0].code = implemented;

        p.net_declarationProduct.code = implemented;
        p.net_declarationProduct.expression.code = implemented;
        p.net_declarationProduct.expression.terms[0].code = implemented;
        p.net_declarationProduct.expression.terms[0].factors[0].code = implemented;
        p.net_declarationProduct.expression.terms[0].factors[6].code = implemented;
        p.net_declarationProduct.expression.terms[0].factors[7].code = implementedTerminal;

        p.net_type_or_triregProduct.code = implemented;
        p.net_type_or_triregProduct.expression.code = implemented;
        p.net_type_or_triregProduct.expression.terms[0].code = implemented;
        p.net_type_or_triregProduct.expression.terms[0].factors[0].code = implemented;

        p.net_typeProduct.code = implemented;
        p.net_typeProduct.expression.code = implemented;
        p.net_typeProduct.expression.terms[7].code = new VerilogWireDeclarationCode();
        p.net_typeProduct.expression.terms[7].factors[0].code = implementedTerminal;

        p.list_of_net_decl_assignmentsProduct.code = implemented;
        p.list_of_net_decl_assignmentsProduct.expression.code = implemented;
        p.list_of_net_decl_assignmentsProduct.expression.terms[0].code = implemented;
        p.list_of_net_decl_assignmentsProduct.expression.terms[0].factors[0].code = implemented;
        p.list_of_net_decl_assignmentsProduct.expression.terms[0].factors[1].code = implemented;
        p.list_of_net_decl_assignmentsProduct.expression.terms[0].factors[1].expression.code = implemented;
        p.list_of_net_decl_assignmentsProduct.expression.terms[0].factors[1].expression.terms[0].code = implemented;
        p.list_of_net_decl_assignmentsProduct.expression.terms[0].factors[1].expression.terms[0].factors[0].code = implementedTerminal;
        p.list_of_net_decl_assignmentsProduct.expression.terms[0].factors[1].expression.terms[0].factors[1].code = implemented;

        p.net_decl_assignmentProduct.code = implemented;
        p.net_decl_assignmentProduct.expression.code = implemented;
        p.net_decl_assignmentProduct.expression.terms[0].code = implemented;
        p.net_decl_assignmentProduct.expression.terms[0].factors[0].code = new VerilogNetIdentifierCode();

//        p.net_decl_assignmentProduct.expression.terms[0].factors[1].code = implemented;
//        p.net_decl_assignmentProduct.expression.terms[0].factors[1].code = implemented;
//        p.net_decl_assignmentProduct.expression.terms[0].factors[1].expression.code = implemented;
//        p.net_decl_assignmentProduct.expression.terms[0].factors[1].expression.terms[0].code = implemented;
//        p.net_decl_assignmentProduct.expression.terms[0].factors[1].expression.terms[0].factors[0].code = implemented;

        p.net_identifierProduct.code = new VerilogNetIdentifierCode();
        p.net_identifierProduct.expression.code = new VerilogNetIdentifierCode();
        p.net_identifierProduct.expression.terms[0].code = new VerilogNetIdentifierCode();
        p.net_identifierProduct.expression.terms[0].factors[0].code = new VerilogNetIdentifierCode();

        p.gate_instantiationProduct.code = implemented;
        p.gate_instantiationProduct.expression.code = implemented;
        p.gate_instantiationProduct.expression.terms[3].code = implemented;
        p.gate_instantiationProduct.expression.terms[3].factors[0].code = implemented;
        p.gate_instantiationProduct.expression.terms[3].factors[2].code = implemented;
        p.gate_instantiationProduct.expression.terms[3].factors[2].expression.code = implemented;
        p.gate_instantiationProduct.expression.terms[3].factors[2].expression.terms[0].code = implemented;
        p.gate_instantiationProduct.expression.terms[3].factors[2].expression.terms[0].factors[0].code = implemented;
        p.gate_instantiationProduct.expression.terms[3].factors[3].code = implemented;
        p.gate_instantiationProduct.expression.terms[3].factors[4].code = implemented;
        p.gate_instantiationProduct.expression.terms[3].factors[4].expression.code = implemented;
        p.gate_instantiationProduct.expression.terms[3].factors[4].expression.terms[0].code = implemented;
        p.gate_instantiationProduct.expression.terms[3].factors[4].expression.terms[0].factors[0].code = implementedTerminal; // comma
        p.gate_instantiationProduct.expression.terms[3].factors[4].expression.terms[0].factors[1].code = implemented;
        p.gate_instantiationProduct.expression.terms[3].factors[5].code = implementedTerminal; // semicolon
        p.gate_instantiationProduct.expression.terms[4].code = implemented;
        p.gate_instantiationProduct.expression.terms[4].factors[0].code = implemented;
        p.gate_instantiationProduct.expression.terms[4].factors[2].code = implemented;
        p.gate_instantiationProduct.expression.terms[4].factors[2].expression.code = implemented;
        p.gate_instantiationProduct.expression.terms[4].factors[2].expression.terms[0].code = implemented;
        p.gate_instantiationProduct.expression.terms[4].factors[2].expression.terms[0].factors[0].code = implemented;
        p.gate_instantiationProduct.expression.terms[4].factors[3].code = implemented;
        p.gate_instantiationProduct.expression.terms[4].factors[4].code = implemented;
        p.gate_instantiationProduct.expression.terms[4].factors[4].expression.code = implemented;
        p.gate_instantiationProduct.expression.terms[4].factors[4].expression.terms[0].code = implemented;
        p.gate_instantiationProduct.expression.terms[4].factors[4].expression.terms[0].factors[0].code = implementedTerminal; // comma
        p.gate_instantiationProduct.expression.terms[4].factors[4].expression.terms[0].factors[1].code = implemented;
        p.gate_instantiationProduct.expression.terms[4].factors[5].code = implementedTerminal; // semicolon

        p.n_input_gatetypeProduct.code = new VerilogNInputGateTypeCode();
        p.n_input_gatetypeProduct.expression.code = implemented;
        p.n_input_gatetypeProduct.expression.terms[0].code = implemented;
        p.n_input_gatetypeProduct.expression.terms[0].factors[0].code = implementedTerminal;
        p.n_input_gatetypeProduct.expression.terms[1].code = implemented;
        p.n_input_gatetypeProduct.expression.terms[1].factors[0].code = implementedTerminal;
        p.n_input_gatetypeProduct.expression.terms[2].code = implemented;
        p.n_input_gatetypeProduct.expression.terms[2].factors[0].code = implementedTerminal;
        p.n_input_gatetypeProduct.expression.terms[3].code = implemented;
        p.n_input_gatetypeProduct.expression.terms[3].factors[0].code = implementedTerminal;
        p.n_input_gatetypeProduct.expression.terms[4].code = implemented;
        p.n_input_gatetypeProduct.expression.terms[4].factors[0].code = implementedTerminal;
        p.n_input_gatetypeProduct.expression.terms[5].code = implemented;
        p.n_input_gatetypeProduct.expression.terms[5].factors[0].code = implementedTerminal;

        p.n_output_gatetypeProduct.code = new VerilogNOutputGateTypeCode();
        p.n_output_gatetypeProduct.expression.code = implemented;
        p.n_output_gatetypeProduct.expression.terms[0].code = implemented;
        p.n_output_gatetypeProduct.expression.terms[0].factors[0].code = implementedTerminal;
        p.n_output_gatetypeProduct.expression.terms[1].code = implemented;
        p.n_output_gatetypeProduct.expression.terms[1].factors[0].code = implementedTerminal;

        p.delay2Product.code = implemented;
        p.delay2Product.expression.code = implemented;
        p.delay2Product.expression.terms[0].code = implemented;
        p.delay2Product.expression.terms[0].factors[0].code = implementedTerminal;
        p.delay2Product.expression.terms[0].factors[1].code = implemented;
//        p.delay2Product.expression.terms[1].factors[0].code = implementedTerminal;
//        p.delay2Product.expression.terms[1].factors[1].code = implementedTerminal;
//        p.delay2Product.expression.terms[1].factors[2].code = implemented;
//        p.delay2Product.expression.terms[1].factors[3].code = implemented;
//        p.delay2Product.expression.terms[1].factors[3].expression.code = implemented;
//        p.delay2Product.expression.terms[1].factors[3].expression.terms[0].code = implemented;
//        p.delay2Product.expression.terms[1].factors[3].expression.terms[0].factors[0].code = implementedTerminal;
//        p.delay2Product.expression.terms[1].factors[3].expression.terms[0].factors[1].code = implemented;
//        p.delay2Product.expression.terms[1].factors[4].code = implementedTerminal;

        p.delay_valueProduct.code = implemented;
        p.delay_valueProduct.expression.code = implemented;
        p.delay_valueProduct.expression.terms[0].code = implemented;
        p.delay_valueProduct.expression.terms[0].factors[0].code = implemented;

        p.unsigned_numberProduct.code = implemented;
        p.unsigned_numberProduct.expression.code = implemented;
        p.unsigned_numberProduct.expression.terms[0].code = implemented;
        p.unsigned_numberProduct.expression.terms[0].factors[0].code = new VerilogUnsignedNumberCode();

        p.n_input_gate_instanceProduct.code = new VerilogNInputGateInstanceCode();
        p.n_input_gate_instanceProduct.expression.code = implemented;
        p.n_input_gate_instanceProduct.expression.terms[0].code = implemented;
        p.n_input_gate_instanceProduct.expression.terms[0].factors[0].code = implemented;
        p.n_input_gate_instanceProduct.expression.terms[0].factors[0].expression.code = implemented;
        p.n_input_gate_instanceProduct.expression.terms[0].factors[0].expression.terms[0].code = implemented;
        p.n_input_gate_instanceProduct.expression.terms[0].factors[0].expression.terms[0].factors[0].code = implemented;
        p.n_input_gate_instanceProduct.expression.terms[0].factors[1].code = implementedTerminal;//left bracket
        p.n_input_gate_instanceProduct.expression.terms[0].factors[2].code = implemented;
        p.n_input_gate_instanceProduct.expression.terms[0].factors[3].code = implementedTerminal; // comma
        p.n_input_gate_instanceProduct.expression.terms[0].factors[4].code = implemented;
        p.n_input_gate_instanceProduct.expression.terms[0].factors[5].code = implemented;
        p.n_input_gate_instanceProduct.expression.terms[0].factors[5].expression.code = implemented;
        p.n_input_gate_instanceProduct.expression.terms[0].factors[5].expression.terms[0].code = implemented;
        p.n_input_gate_instanceProduct.expression.terms[0].factors[5].expression.terms[0].factors[0].code = implementedTerminal;
        p.n_input_gate_instanceProduct.expression.terms[0].factors[5].expression.terms[0].factors[1].code = implemented;
        p.n_input_gate_instanceProduct.expression.terms[0].factors[6].code = implementedTerminal; // right bracket

        p.n_output_gate_instanceProduct.code = new VerilogNOutputGateInstanceCode();
        p.n_output_gate_instanceProduct.expression.code = implemented;
        p.n_output_gate_instanceProduct.expression.terms[0].code = implemented;
        p.n_output_gate_instanceProduct.expression.terms[0].factors[0].code = implemented;
        p.n_output_gate_instanceProduct.expression.terms[0].factors[0].expression.code = implemented;
        p.n_output_gate_instanceProduct.expression.terms[0].factors[0].expression.terms[0].code = implemented;
        p.n_output_gate_instanceProduct.expression.terms[0].factors[0].expression.terms[0].factors[0].code = implemented;
        p.n_output_gate_instanceProduct.expression.terms[0].factors[1].code = implementedTerminal; //left bracket
        p.n_output_gate_instanceProduct.expression.terms[0].factors[2].code = implemented;
        p.n_output_gate_instanceProduct.expression.terms[0].factors[3].code = implementedTerminal; //comma
        p.n_output_gate_instanceProduct.expression.terms[0].factors[4].code = implemented;
        p.n_output_gate_instanceProduct.expression.terms[0].factors[4].expression.code = implemented;
        p.n_output_gate_instanceProduct.expression.terms[0].factors[4].expression.terms[0].code = implemented;
        p.n_output_gate_instanceProduct.expression.terms[0].factors[4].expression.terms[0].factors[0].code = implemented;
        p.n_output_gate_instanceProduct.expression.terms[0].factors[4].expression.terms[0].factors[1].code = implementedTerminal; //comma
        p.n_output_gate_instanceProduct.expression.terms[0].factors[4].expression.terms[0].factors[1].code = implemented;
        p.n_output_gate_instanceProduct.expression.terms[0].factors[5].code = implemented;
        p.n_output_gate_instanceProduct.expression.terms[0].factors[6].code = implementedTerminal; // right bracket


        p.name_of_instanceProduct.code = implemented;
        p.name_of_instanceProduct.expression.code = implemented;
        p.name_of_instanceProduct.expression.terms[0].code = implemented;
        p.name_of_instanceProduct.expression.terms[0].factors[0].code = implemented;

        p.instance_identifierProduct.code = implemented;
        p.instance_identifierProduct.expression.code = implemented;
        p.instance_identifierProduct.expression.terms[0].code = implemented;
        p.instance_identifierProduct.expression.terms[0].factors[0].code = implemented;

        p.output_terminalProduct.code = new VerilogAddOutputCode();
        p.output_terminalProduct.expression.code = implemented;
        p.output_terminalProduct.expression.terms[0].code = implemented;
        p.output_terminalProduct.expression.terms[0].factors[0].code = implemented;

        p.net_lvalueProduct.code = implemented;
        p.net_lvalueProduct.expression.code = implemented;
        p.net_lvalueProduct.expression.terms[0].code = implemented;
        p.net_lvalueProduct.expression.terms[0].factors[0].code = implemented;

        p.ps_or_hierarchical_net_identifierProduct.code = implemented;
        p.ps_or_hierarchical_net_identifierProduct.expression.code = implemented;
        p.ps_or_hierarchical_net_identifierProduct.expression.terms[0].code = implemented;
        p.ps_or_hierarchical_net_identifierProduct.expression.terms[0].factors[1].code = implemented;
//        p.ps_or_hierarchical_net_identifierProduct.expression.terms[1].code = implemented;
//        p.ps_or_hierarchical_net_identifierProduct.expression.terms[1].factors[0].code = implemented;

        p.net_identifierProduct.code = implemented;
        p.net_identifierProduct.expression.code = implemented;
        p.net_identifierProduct.expression.terms[0].code = implemented;
        p.net_identifierProduct.expression.terms[0].factors[0].code = implemented;

        p.input_terminalProduct.code = new VerilogAddIntputCode();
        p.input_terminalProduct.expression.code = implemented;
        p.input_terminalProduct.expression.terms[0].code = implemented;
        p.input_terminalProduct.expression.terms[0].factors[0].code = implemented;

        p.expressionProduct.code = implemented;
        p.expressionProduct.expression.code = implemented;
        p.expressionProduct.expression.terms[0].code = implemented;
        p.expressionProduct.expression.terms[0].factors[0].code = implemented;
        p.expressionProduct.expression.terms[0].factors[1].code = implemented;
        p.expressionProduct.expression.terms[0].factors[1].expression.code = implemented;
        p.expressionProduct.expression.terms[0].factors[1].expression.terms[0].code = implemented;
        p.expressionProduct.expression.terms[0].factors[1].expression.terms[0].factors[0].code = implemented;
        p.expressionProduct.expression.terms[0].factors[1].expression.terms[0].factors[2].code = implemented;

        p.simple_expressionProduct.code = implemented;
        p.simple_expressionProduct.expression.code = implemented;
        p.simple_expressionProduct.expression.terms[0].code = implemented;
        p.simple_expressionProduct.expression.terms[0].factors[0].code = implemented;

        p.primaryProduct.code = implemented;
        p.primaryProduct.expression.code = implemented;
        p.primaryProduct.expression.terms[0].code = implemented;
        p.primaryProduct.expression.terms[0].factors[0].code = implemented;
        p.primaryProduct.expression.terms[1].code = implemented;
        p.primaryProduct.expression.terms[1].factors[1].code = implemented;

        p.primary_literalProduct.code = implemented;
        p.primary_literalProduct.expression.code = implemented;
        p.primary_literalProduct.expression.terms[0].code = implemented;
        p.primary_literalProduct.expression.terms[0].factors[0].code = implemented;

        p.numberProduct.code = implemented;
        p.numberProduct.expression.code = implemented;
        p.numberProduct.expression.terms[0].code = implemented;
        p.numberProduct.expression.terms[0].factors[0].code = implemented;

        p.integral_numberProduct.code = implemented;
        p.integral_numberProduct.expression.code = implemented;
        p.integral_numberProduct.expression.terms[0].code = implemented;
        p.integral_numberProduct.expression.terms[0].factors[0].code = implemented;

        p.decimal_numberProduct.code = implemented;
        p.decimal_numberProduct.expression.code = implemented;
        p.decimal_numberProduct.expression.terms[0].code = implemented;
        p.decimal_numberProduct.expression.terms[0].factors[0].code = implemented;
//        p.decimal_numberProduct.expression.terms[1].code = implemented;
//        p.decimal_numberProduct.expression.terms[1].factors[0].code = implemented;

        p.hierarchical_identifierProduct.code = implemented;
        p.hierarchical_identifierProduct.expression.code = implemented;
        p.hierarchical_identifierProduct.expression.terms[0].code = implemented;
        p.hierarchical_identifierProduct.expression.terms[0].factors[2].code = implemented;

        p.end_moduleProduct.code = new VerilogEndModuleCode();
        p.end_moduleProduct.expression.code = implemented;
        p.end_moduleProduct.expression.terms[0].code = implemented;
        p.end_moduleProduct.expression.terms[0].factors[0].code = implementedTerminal;

//        p.end_moduleProduct.code = new VerilogEndModuleCode();
//        p.end_moduleProduct.expression.code = implemented;
//        p.end_moduleProduct.expression.terms[0].code = implemented;
//        p.end_moduleProduct.expression.terms[0].factors[0].code = implementedTerminal;


    }
}
