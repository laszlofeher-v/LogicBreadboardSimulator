package com.logicbreadboard.simulator;

/**
 * Created on 11/21/15.
 */
public class VerilogTerm extends VerilogParserNode {
    VerilogFactor[] factors;

    public boolean next(VerilogRider rider) {
        VerilogToken savedToken;
//        if (!enter(rider.token)) {
//            return false;
//        }
        boolean retVal = false;
        if (alreadyVisited(rider)) {
            return false;
        }
        VerilogRider.Link savedLink = rider.link;
        rider.linkThis(this);
        rider.indent++;
        savedToken = rider.token;
        retVal = true;
        for (int i = 0; i < factors.length; i++) {
            if (!factors[i].next(rider)) {
                rider.token = savedToken;
//                    stamp(rider.token);
//                leave(savedToken);
                rider.cutFrom(savedLink);
                retVal = false;
                break;
            }
        }
//        }
        rider.indent--;
        return retVal;
    }

//    @Override
//    public boolean canCallFromHere(VerilogParserNode node) {
//        for (int i = 0; i < factors.length; i++) {
//            if (factors[i] == node) {
//                return true;
//            }
//        }
//        return false;
//    }

}
