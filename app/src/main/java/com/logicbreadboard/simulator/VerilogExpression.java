package com.logicbreadboard.simulator;

/**
 * Created on 11/21/15.
 */

import java.util.HashSet;

/**
 * Created by laci on 11/1/15.
 */
public class VerilogExpression extends VerilogParserNode {
    VerilogTerm[] terms;

    public boolean next(VerilogRider rider) {
        HashSet<VerilogParserNode> pathToHere;
        VerilogToken savedToken;
        VerilogToken bestToken;
        boolean retVal = false;
        savedToken = rider.token;
        bestToken = rider.token;
        int maxLength = -1;
        rider.indent++;
        VerilogRider.Link bestLink = rider.link;
//        if (!enter(rider.token)) {
//            return false;
//        }
        if (!alreadyVisited(rider)) {
            pathToHere = (HashSet) rider.token.path.clone();
//            System.out.println("enter expression timeStamp:"+timeStamp+" token.timeStamp:"+rider.token.timeStamp);
            VerilogRider.Link savedLink = rider.link;
            rider.linkThis(this);
            for (int i = 0; i < terms.length; i++) {
//                System.out.println("for expression timeStamp:"+timeStamp+" token.timeStamp:"+rider.token.timeStamp);
                if (terms[i].next(rider)) {
                    retVal = true;
                    int newLength = rider.token.pos - savedToken.pos;
                    if (newLength > maxLength) {
                        maxLength = newLength;
                        bestToken = rider.token;
                        bestLink = rider.link;
                        if (rider.token.sym == VerilogToken.Symbols.SYM_SEMICOLON ||
                                rider.token.sym == VerilogToken.Symbols.ENDMODULE) {
//                            System.out.println("SYM_SEMICOLON");
                            break;
                        }
                    }
                    rider.cutFrom(savedLink);
                    rider.token = savedToken;
                    rider.token.path = (HashSet<VerilogParserNode>) pathToHere.clone();
                }
            }
        }
        rider.indent--;
        if (retVal) {
            rider.token = bestToken;
            rider.cutFrom(bestLink);
            return true;
        } else {
            rider.token = savedToken;
            return false;
        }
    }

//    @Override
//    public boolean canCallFromHere(VerilogParserNode node) {
//        for (int i = 0; i < terms.length; i++) {
//            if (terms[i] == node) {
//                return true;
//            }
//        }
//        return false;
//    }

}
