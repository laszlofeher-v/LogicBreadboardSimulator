package com.logicbreadboard.simulator;

import java.util.HashMap;
import java.util.LinkedList;

/**
 * Created by laci on 9/4/15.
 */
public class VerilogSymTable {
    private HashMap<String, VObj> T;
    Module universe;
    Module actScope;

    VerilogSymTable() {
        universe = new Module();
        actScope = universe;
        T = new HashMap<String, VObj>();
    }

    public boolean addModule(String id) {
        if (actScope.desc.containsKey(id)) {
            return false;
        }
        Module m = new Module();
        m.id = id;
        actScope.desc.put(id, m);
        m.parent = actScope;
        actScope = m;
        return true;
    }

    public void leaveModule() {
        actScope = actScope.parent;
    }

    public boolean addPort(String id, PortViewer portViewer) {
        if (actScope.desc.containsKey(id)) {
            return false;
        }
        Port p = new Port();
        p.parent = actScope;
        p.id = id;
        p.pos = actScope.portPos++;
        portViewer.setLabel(Integer.toString(p.pos));
        actScope.desc.put(id, p);
        p.connectedIOs = portViewer.firstIoViewer.model;
        return true;
    }

    public boolean setInputPort(VerilogContext context) {
        VObj p = actScope.desc.get(context.token.ident);
        if (p != null) {
            p.setInput();
            context.portViewer.setLabel(Integer.toString(((Port) p).pos));
            return true;
        }
        return false;
    }

    public boolean setOutputPort(VerilogContext context) {
        VObj p = actScope.desc.get(context.token.ident);
        if (p != null) {
            p.setOutput();
            context.portViewer.setLabel(((Port) p).id);
            return true;
        }
        return false;
    }

    public boolean setInoutPort(VerilogContext context) {
        VObj p = actScope.desc.get(context.token.ident);
        if (p != null) {
            p.setInout();
            return true;
        }
        return false;
    }

    enum PortType {IN_PORT, OUT_PORT, IN_OUT_PORT}

    public boolean addWire(String id) {
        if (actScope.desc.containsKey(id)) {
            return false;
        }
        Wire w = new Wire();
        w.parent = actScope;
        w.id = id;
        actScope.desc.put(id, w);
        return true;
    }

    public Net getNet(String id) {
        if (!actScope.desc.containsKey(id)) {
            return null;
        }
        VObj net = actScope.desc.get(id);
        if (net instanceof Net) {
            return (Net) net;
        } else {
            return null;
        }
    }

    public class VObj {
        String id;
        Module parent;

        public void setInput() {
        }

        public void setOutput() {
        }

        public void setInout() {
        }
    }

    public class Net extends VObj {
        DeviceIOModel connectedIOs;

        Net() {
            connectedIOs = null;
        }
    }

    public class Port extends Net {
        int pos;
        PortType type;

        @Override
        public void setInput() {
            type = PortType.IN_PORT;
        }

        @Override
        public void setOutput() {
            type = PortType.OUT_PORT;
        }

        @Override
        public void setInout() {
            type = PortType.IN_OUT_PORT;
        }

    }

    public class Wire extends Net {
    }

    public class Module extends VObj {
        VObj outerScope;
        HashMap<String, VObj> desc;
        int portPos;
        LinkedList<DeviceModel> models;

        Module() {
            models = new LinkedList<>();
            desc = new HashMap<>();
            portPos = 1;
        }
    }

}
