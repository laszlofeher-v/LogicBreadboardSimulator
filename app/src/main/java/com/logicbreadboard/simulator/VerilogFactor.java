package com.logicbreadboard.simulator;

/**
 * Created on 11/21/15.
 */

import java.util.HashSet;

public class VerilogFactor extends VerilogParserNode {
    boolean repeatable;
    boolean conditional;
    VerilogToken.Symbols terminal;
    VerilogProduct product;
    VerilogExpression expression;

//    public void codeSkipTerminal(VerilogContext context) {
//        if (terminal != null) {
//            if (terminal == context.token.sym) { // terminal symbol is all right
//                context.token = context.token.next;
//            } else {
//                System.out.println("$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$");
//            }
//        }
//    }

    public String getTerminal() {
        if (terminal != null) {
            return terminal.name();
        } else {
            return "";
        }
    }


    private boolean checkNext(VerilogRider rider) {
        boolean retVal = false;
        if (terminal != null) {
            if (terminal == rider.token.sym) { // terminal symbol is all right
                if (terminal == VerilogToken.Symbols.ENDMODULE) {
//                    tabulate(rider);
//                    System.out.println(terminal + "  OK " + rider.token.sym + "  " + rider.token.ident + " " + VerilogParserNode.maxMapSize);
                    nextToken(rider);
//                    System.exit(0);
                } else if (terminal == VerilogToken.Symbols.SYM_RIGHT_BRACKET) {
//                    tabulate(rider);
//                    System.out.println("++++" + terminal + "  OK " + rider.token.sym + "  " + rider.token.ident);
                    nextToken(rider);
                } else {
//                    tabulate(rider);
//                    System.out.println("++++" + terminal + "  OK " + rider.token.sym + "  " + rider.token.ident);
                    nextToken(rider);
                }
                return true;
            } else {
//                System.out.println("----" + terminal + "  NOT OK " + rider.token.sym + "  " + rider.token.ident);
                return false;
            }
        } else if (product != null) {
//            tabulate(rider);
//            System.out.println(product.ident + "  " + rider.token.sym + "  " + rider.token.ident);
            retVal = product.next(rider);
//            tabulate(rider);
//            System.out.println(product.ident + "  " + rider.token.sym + "  " + rider.token.ident + " " + retVal);
            return retVal;
        } else if (expression != null) {
            retVal = expression.next(rider);
//            tabulate(rider);
//            System.out.println(" expression " + retVal);
            return retVal;
        } else {
//            System.out.println("                 false");
            return false;
        }
    }

    public boolean next(VerilogRider rider) {
//        if (rider.token.timeStamp != timeStamp) {  // avoid infinite loop with unchanged token
//        if (!enter(rider.token)) {
//            return false;
//        }
        if (alreadyVisited(rider)) {
            return false;
        }
        HashSet<VerilogParserNode> pathToHere;
        VerilogRider.Link savedLink = rider.link;
        rider.linkThis(this);
        rider.indent++;
        if (conditional) {
//            tabulate(rider);
//            System.out.println("  [");
            pathToHere = (HashSet) rider.token.path.clone();
            if (!checkNext(rider)) {
                rider.token.path = (HashSet<VerilogParserNode>) pathToHere.clone();
                rider.cutFrom(savedLink);
            }
//            tabulate(rider);
//            System.out.println("  ]");
//                VerilogParserNode.stamp(rider.token);
            rider.indent--;
//            tabulate(rider);
//            System.out.println(true);
            return true;
        } else if (repeatable) {
//            tabulate(rider);
//            System.out.println("  {");
            pathToHere = (HashSet) rider.token.path.clone();
            boolean ok = false;
            while (checkNext(rider)) {
                ok = true;
            }
            if (!ok) {
                rider.token.path = (HashSet<VerilogParserNode>) pathToHere.clone();
                rider.cutFrom(savedLink);
            }
//                VerilogParserNode.stamp(rider.token);
//            tabulate(rider);
//            System.out.println("  }");
            rider.indent--;
//            tabulate(rider);
//            System.out.println(true);
            return true;
        } else {
            rider.indent--;
            boolean retVal = checkNext(rider);
            if (!retVal) {
                rider.cutFrom(savedLink);
//                leave(rider.token);
            }
            return retVal;
        }
//        } else {
//            tabulate(rider);
//            System.out.println(rider.token.sym + "  " + rider.token.ident + " " + rider.token.timeStamp);
//            System.out.println("------------------------------------------ timestamp");
//            return false;
//        }
    }

//    @Override
//    public boolean canCallFromHere(VerilogParserNode node) {
//        if (product == node) {
//            return true;
//
//        } else if (expression == node) {
//            return true;
//        }
//        if (terminal!=null && terminal== VerilogToken.Symbols.SYM_RIGHT_BRACKET) {
//            System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
//        }
//        return false;
//    }

}
