package com.logicbreadboard.simulator;

/**
 * Created on 12/6/15.
 */
public class VerilogAddIntputCode extends VerilogCode {
    public boolean gen(VerilogContext context) {
        boolean retVal = true;
        DeviceModel deviceModel = context.deviceModels.getLast();
        if (deviceModel != null) {
            DeviceInputModel inputModel = new DeviceInputModel(deviceModel);
            inputModel.next = deviceModel.firstInputModel;
            deviceModel.firstInputModel = inputModel;
            VerilogSymTable.Net net = context.symTable.getNet(context.token.ident);
            if (net != null) {
                if (net.connectedIOs == null) {
                    net.connectedIOs = deviceModel.firstInputModel;
                } else {
                    deviceModel.firstInputModel.connectTo(net.connectedIOs);
                }
            }
        }
        return retVal;
    }

}
