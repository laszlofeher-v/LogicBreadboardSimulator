package com.logicbreadboard.simulator;

/**
 * Created on 11/21/15.
 */
public class VerilogRider {
    VerilogToken token;
    VerilogParserNode node;
    int indent;
    Link link;

    public void linkThis(VerilogParserNode node) {

        if (link == null) {
            link = new Link();
            link.node = node;
            link.token = token;
        } else {
            Link l = new Link();
            l.node = node;
//            link.token=token;
            l.token = token;
            l.back = link;
            link = l;
        }
    }

    public void cutFrom(Link savedLink) {
        link = savedLink;
    }

    class Link {
        Link back;
        VerilogParserNode node;
        VerilogToken token;
    }
}
