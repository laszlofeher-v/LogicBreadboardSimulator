package com.logicbreadboard.simulator;

/**
 * Created by laci on 9/24/15.
 */
public class RestrictedWireViewer extends WireViewer {
    public short getGroup() {
        return (short) SchematicViewerFactory.DeviceGroup.Wires.ordinal();
    }

    @Override
    public short getSymbol() {
        return (short) SchematicViewerFactory.Wires.RestrictedWire.ordinal();
    }

}
