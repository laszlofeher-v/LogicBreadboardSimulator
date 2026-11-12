package com.logicbreadboard.simulator;

import java.util.Arrays;

/**
 * Created on 2/4/16.
 */
public class BBViewerFactory extends ViewerFactory {

    enum BBDeviceGroup {
        BBWires,
        BBTwoTerminals,
        BBLabDevices,
        //        BBButtons, BBLeds, BBProbes,
        Box,
        Switches,
        _74XX,
//        _4XXX,
//        _MY_IC
//        _740X,
//        _741X,
//        _742X,
//        _743X,
//        _744X,
//        _745X,
//        _746X,
//        _747X,
//        _748X,
//        _749X,
//        _7410X,
//        _7411X
        ;

        public static String[] names() {
            String valuesStr = Arrays.toString(BBDeviceGroup.values());
            valuesStr = valuesStr.replaceFirst("BBWires,", "");
            valuesStr = valuesStr.replaceFirst("BBTwoTerminals,", "");
            valuesStr = valuesStr.replaceFirst("BBLabDevices,", "");
            return valuesStr.substring(1, valuesStr.length() - 1).replace("_", " ").split(",");
        }

        @Override
        public String toString() {
            String s = super.toString();
            s = s.replaceAll("_", " ");
            return s;
        }
    }

    @Override
    public int getGroupBox() {
        return BBDeviceGroup.Box.ordinal();
    }

    @Override
    public int getGroupDIPSw() {
        return BBDeviceGroup.Switches.ordinal();
    }

    public enum BBWires {
        FreeWire;

        public static String[] names() {
            String valuesStr = Arrays.toString(BBWires.values());
            return valuesStr.substring(1, valuesStr.length() - 1).replace("_", " ").split(",");
        }
    }

    public enum BBTwoTerminals {
        Resistor,
        Led,
        ResistorLed;

        public static String[] names() {
            String valuesStr = Arrays.toString(BBTwoTerminals.values());
            return valuesStr.substring(1, valuesStr.length() - 1).replace("_", " ").split(",");
        }
    }

    public enum BBLabDevices {
        BBClockViewer, BBProbeViewer;

        public static String[] names() {
            String valuesStr = Arrays.toString(BBTwoTerminals.values());
            return valuesStr.substring(1, valuesStr.length() - 1).replace("_", " ").split(",");
        }
    }

    enum Box {
        DIP_8, DIP_14, DIP_16, DIP_18, DIP_24, DIP_28, DIP_32, DIP_40, DIP_48, DIP_64;

        public static String[] names() {
            String valuesStr = Arrays.toString(Box.values());
            return valuesStr.substring(1, valuesStr.length() - 1).replace("_", " ").split(",");
        }

        @Override
        public String toString() {
            String s = super.toString();
            s = s.replaceAll("_", " ");
            return s;
        }
    }

    enum BB_Switch {
        // number of pins, number of switches
        Tactile1_1,
        //        Tactile2_2,
        DIP_SW
        ;

        public static String[] names() {
            String valuesStr = Arrays.toString(BB_Switch.values());
            return valuesStr.substring(1, valuesStr.length() - 1).replace("_", " ").split(",");
        }

        @Override
        public String toString() {
            String s = super.toString();
            s = s.replaceAll("_", " ");
            return s;
        }
    }

    enum BreadBoardDevice {
        WIRE, PUSH_BUTTON, TOGGLE_BUTTON, LED, RESISTOR, PROBE;

        public static String[] names() {
            String valuesStr = Arrays.toString(BreadBoardDevice.values());
            return valuesStr.substring(1, valuesStr.length() - 1).replace("_", " ").split(",");
        }

        @Override
        public String toString() {
            String s = super.toString();
            s = s.replaceAll("_", " ");
            return s;
        }
    }

    private Viewer newLabDevices(int position) {
        Viewer v = null;
        if (position < BBLabDevices.values().length) {
            switch (BBLabDevices.values()[position]) {
                case BBClockViewer:
                    v = new BBClockViewer();
                    break;
                case BBProbeViewer:
                    v = new BBProbeViewer();
                    break;
                default:
            }
        }
        return v;
    }

    private Viewer newBBWires(int position) {
        Viewer v = null;
        if (position < BBWires.values().length) {
            switch (BBWires.values()[position]) {
                case FreeWire:
                    v = new BBWireViewer();
                    break;
                default:
            }
        }
        return v;
    }

    private Viewer newBBTwoTerminals(int position) {
        Viewer v = null;
        if (position < BBTwoTerminals.values().length) {
            switch (BBTwoTerminals.values()[position]) {
                case Resistor:
                    v = new BBResistorViewer();
                    break;
                case Led:
                    v = new BBLedViewer();
                    break;
                case ResistorLed:
                    v = new BBResistorLedViewer();
                    break;
                default:
            }
        }
        return v;
    }

    private Viewer newDIPSwitch(int position) {
        Viewer v = null;
        if (position < BB_Switch.values().length) {
            switch (BB_Switch.values()[position]) {
                case Tactile1_1:
                    v = new BBTactile1_1SwViewer();
                    break;
//                case Tactile2_2:
//                    v = new BBTactile2_2SwViewer();
//                    break;
                case DIP_SW:
                    v = new BBDIPSwViewer();
                    break;
                default:
            }
        }
        return v;
    }

    private Viewer newBox(int position) {
        Viewer v = null;
        if (position < Box.values().length) {
            switch (Box.values()[position]) {
//                case Verilog:
//                    v = new VerilogBoxViewer();
//                    break;
//                case VHDL:
//                    break;
//                case SUB:
//                    v = new SubBoxViewer();
//                    break;
                case DIP_8:
                    v = new DIPViewer8();
                    break;
                case DIP_14:
                    v = new DIPViewer14();
                    break;
                case DIP_16:
                    v = new DIPViewer16();
                    break;
                case DIP_18:
                    v = new DIPViewer18();
                    break;
                case DIP_24:
                    v = new DIPViewer24();
                    break;
                case DIP_28:
                    v = new DIPViewer28();
                    break;
                case DIP_32:
                    v = new DIPViewer32();
                    break;
                case DIP_40:
                    v = new DIPViewer40();
                    break;
                case DIP_48:
                    v = new DIPViewer48();
                    break;
                case DIP_64:
                    v = new DIPViewer64();
                    break;
//                case _7400:
//                    v = new _74xxxViewer();
//                    break;
//                case _7401:
//                    v = new _7401Viewer();
//                    break;
//                case _7402:
//                    v = new _7402Viewer();
//                    break;
//                case _7404:
//                    v = new _7404Viewer();
//                    break;
//                case _7432:
//                    v = new _7432Viewer();
//                    break;
                default:
            }
        }
        return v;
    }

    public  Viewer newViewer(byte selectedGroup, String name) {
        Viewer v = null;
        v = new _74xxxViewer(name);
        return v;
    }

    public Viewer newViewer(int group, int position) {
        Viewer v = null;
        if (group >= 0 && group < BBDeviceGroup.values().length) {
            switch (BBDeviceGroup.values()[group]) {
                case BBWires:
                    v = newBBWires(position);
                    break;
                case Box:
                    v = newBox(position);
                    break;
                case Switches:
                    v = newDIPSwitch(position);
                    break;
//                case BBLeds:
//                    v = newBBLeds(position);
//                    break;
                case BBTwoTerminals:
                    v = newBBTwoTerminals(position);
                    break;
                case BBLabDevices:
                    v = newLabDevices(position);
                    break;
//                case BBProbes:
//                    v = newBBProbes(position);
//                    break;
                case _74XX:
                    v = new _74xxxViewer();
                    break;

//                _741X,
//                        _742X,
//                        _743X,
//                        _744X,
//                        _745X,
//                        _746X,
//                        _747X,
//                        _748X,
//                        _749X,
//                        _7410X,
//                        _7411X;
                default:
            }
        }
        return v;
    }

}


