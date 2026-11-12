package com.logicbreadboard.simulator;

/**
 * Created on 12/5/15.
 */
public class VerilogAddOutputCode extends VerilogCode {
    public boolean gen(VerilogContext context) {
        boolean retVal = true;
        DeviceModel deviceModel = context.deviceModels.getLast();
        if (deviceModel != null) {
            deviceModel.firstOutputModel = new DeviceOutputModel(deviceModel);
            VerilogSymTable.Net net = context.symTable.getNet(context.token.ident);
            if (net != null) {
                if (net.connectedIOs == null) {
                    net.connectedIOs = deviceModel.firstOutputModel;
                } else {
                    deviceModel.firstOutputModel.connectTo(net.connectedIOs);
                }
            }
        }
        return retVal;
    }

}
