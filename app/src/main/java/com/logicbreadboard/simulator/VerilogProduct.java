package com.logicbreadboard.simulator;

/**
 * Created on 11/21/15.
 */
public class VerilogProduct extends VerilogParserNode {
    VerilogExpression expression;
    String ident;

    public void addSymbol() {

    }

    public boolean next(VerilogRider rider) {
        boolean retVal = false;
//        if (!enter(rider.token)) {
//            return false;
//        }
        if (alreadyVisited(rider)) {
            return false;
        }
        rider.indent++;
//        if (rider.token.timeStamp != timeStamp) {
        VerilogToken savedToken = rider.token;
        VerilogRider.Link savedLink = rider.link;
        rider.linkThis(this);
        if (expression.next(rider)) {
            retVal = true;
//            tabulate(rider);
//            System.out.println("+++ product=" + ident);
        } else {
//            tabulate(rider);
//            System.out.println("--- product=" + ident);
            rider.token = savedToken;
            rider.cutFrom(savedLink);
//            leave(savedToken);
//                stamp(rider.token);
            retVal = false;
        }
//        } else {
//            retVal = false;
//        }
        rider.indent--;
        return retVal;
    }

    public String getIdent() {
        return ident;
    }

//    @Override
//    public boolean canCallFromHere(VerilogParserNode node) {
//        if (node == expression) {
//            return true;
//        }
//        return false;
//    }

    VerilogProduct() {
        expression = new VerilogExpression();
    }
}
