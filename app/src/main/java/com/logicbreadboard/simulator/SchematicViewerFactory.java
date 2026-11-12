package com.logicbreadboard.simulator;

import java.util.Arrays;

/**
 * Created on 6/11/15.
 */
public class SchematicViewerFactory extends ViewerFactory {

    enum DeviceGroup {
        Wires, Gates, Flip_Flops, Connections, Sources, Displays, Mux, Demux, Transistors, Annotation,
        Box
//        , Shift_registers, Counters,
//        Blocks, Series_7400, Series_4000
        ;

        public static String[] names() {
            String valuesStr = Arrays.toString(DeviceGroup.values());
            valuesStr = valuesStr.replaceFirst("Wires,", "");
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
        return DeviceGroup.Box.ordinal();
    }

    @Override
    public int getGroupDIPSw() {
        return 0;
    }

    public enum BBWires {
        FreeWire;

        public static String[] names() {
            String valuesStr = Arrays.toString(Transistors.values());
            return valuesStr.substring(1, valuesStr.length() - 1).replace("_", " ").split(",");
        }
    }

    enum Transistors {
        NMOS, PMOS,;

        public static String[] names() {
            String valuesStr = Arrays.toString(Transistors.values());
            return valuesStr.substring(1, valuesStr.length() - 1).replace("_", " ").split(",");
        }

        @Override
        public String toString() {
            String s = super.toString();
            s = s.replaceAll("_", " ");
            return s;
        }
    }

    enum Mux {
        Mux_2_to_1, Mux_4_to_1, Mux_8_to_1,;

        public static String[] names() {
            String valuesStr = Arrays.toString(Mux.values());
            return valuesStr.substring(1, valuesStr.length() - 1).replace("_", " ").split(",");
        }

        @Override
        public String toString() {
            String s = super.toString();
            s = s.replaceAll("_", " ");
            return s;
        }
    }

    enum Demux {
        Demux_1_to_2, Demux_1_to_4, Demux_1_to_8,;

        public static String[] names() {
            String valuesStr = Arrays.toString(Demux.values());
            return valuesStr.substring(1, valuesStr.length() - 1).replace("_", " ").split(",");
        }

        @Override
        public String toString() {
            String s = super.toString();
            s = s.replaceAll("_", " ");
            return s;
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

//    enum BreadBoardDevice {
//        WIRE, PUSH_BUTTON, TOGGLE_BUTTON, LED, RESISTOR, PROBE, _7400, _7402;
//
//        public static String[] names() {
//            String valuesStr = Arrays.toString(BreadBoardDevice.values());
//            return valuesStr.substring(1, valuesStr.length() - 1).replace("_", " ").split(",");
//        }
//
//        @Override
//        public String toString() {
//            String s = super.toString();
//            s = s.replaceAll("_", " ");
//            return s;
//        }
//    }

    enum Gates {
        Buffer, Inverter, And, Nand, Or, Nor, Exor, Exnor, Buffer_TS_H, Buffer_TS_L, Inverter_TS_H,
        Inverter_TS_L // end of & logics
        ;

        public static String[] names() {
            String valuesStr = Arrays.toString(Gates.values());
            return valuesStr.substring(1, valuesStr.length() - 1).replace("_", " ").split(",");
        }

        @Override
        public String toString() {
            String s = super.toString();
            s = s.replaceAll("_", " ");
            return s;
        }
    }

    enum Flip_Flops {
        D_Flip_Flop, D_Latch, JK_Flip_Flop, Monostable;

        public static String[] names() {
            String valuesStr = Arrays.toString(Flip_Flops.values());
            return valuesStr.substring(1, valuesStr.length() - 1).replace("_", " ").split(",");
        }

        @Override
        public String toString() {
            String s = super.toString();
            s = s.replaceAll("_", " ");
            return s;
        }
    }

    enum Connections {
        Label, Bus_wire, Port, PowerConnector;

        public static String[] names() {
            String valuesStr = Arrays.toString(Connections.values());
            return valuesStr.substring(1, valuesStr.length() - 1).replace("_", " ").split(",");
        }

        @Override
        public String toString() {
            String s = super.toString();
            s = s.replaceAll("_", " ");
            return s;
        }
    }

    enum Wires {
        Wire, RestrictedWire;

        public static String[] names() {
            String valuesStr = Arrays.toString(Wires.values());
            return valuesStr.substring(1, valuesStr.length() - 1).replace("_", " ").split(",");
        }

        @Override
        public String toString() {
            String s = super.toString();
            s = s.replaceAll("_", " ");
            return s;
        }
    }

    enum Annotations {
        Annotation;

        public static String[] names() {
            String valuesStr = Arrays.toString(Annotations.values());
            return valuesStr.substring(1, valuesStr.length() - 1).replace("_", " ").split(",");
        }

        @Override
        public String toString() {
            String s = super.toString();
            s = s.replaceAll("_", " ");
            return s;
        }
    }

    enum Sources {
        Toggle_Button_L, Toggle_Button_H, Level_High, Level_Low, Push_Button_L, Push_Button_H,
        Clock_HL, Clock_LH
//        ,Generator, Clock
        ;

        public static String[] names() {
            String valuesStr = Arrays.toString(Sources.values());
            return valuesStr.substring(1, valuesStr.length() - 1).replace("_", " ").split(",");
        }

        @Override
        public String toString() {
            String s = super.toString();
            s = s.replaceAll("_", " ");
            return s;
        }
    }

    enum Displays {
        LED, Probe, Decimal, Hexadecimal
//        , HL
//        , Decimal, HexaDecinal, Scope
        ;

        public static String[] names() {
            String valuesStr = Arrays.toString(Displays.values());
            return valuesStr.substring(1, valuesStr.length() - 1).replace("_", " ").split(",");
        }

        @Override
        public String toString() {
            String s = super.toString();
            s = s.replaceAll("_", " ");
            return s;
        }
    }

    private Viewer newAnsiGate(int position) {
        Viewer v = null;
        if (position < Gates.values().length) {
            switch (Gates.values()[position]) {
                case Buffer:
                    v = new BufferAnsiViewer();
                    break;
                case Inverter:
                    v = new InverterAnsiViewer();
                    break;
                case And:
                    v = new AndAnsiViewer();
                    break;
                case Nand:
                    v = new NandAnsiViewer();
                    break;
                case Or:
                    v = new OrAnsiViewer();
                    break;
                case Nor:
                    v = new NorAnsiViewer();
                    break;
                case Exor:
                    v = new ExorAnsiViewer();
                    break;
                case Exnor:
                    v = new ExnorAnsiViewer();
                    break;
                case Buffer_TS_H:
                    v = new BufferTSHAnsiViewer();
                    break;
                case Buffer_TS_L:
                    v = new BufferTSLAnsiViewer();
                    break;
                case Inverter_TS_H:
                    v = new InverterTSHAnsiViewer();
                    break;
                case Inverter_TS_L:
                    v = new InverterTSLAnsiViewer();
                    break;
                default:

            }
        }
        return v;
    }

    private Viewer newDisplays(int position) {
        Viewer v = null;
        if (position < Displays.values().length) {
            switch (Displays.values()[position]) {
                case LED:
                    v = new LEDViewer();
                    break;
                case Probe:
                    v = new ProbeViewer();
                    break;
                case Decimal:
                    v = new DecimalDisplayViewer();
                    break;
                case Hexadecimal:
                    v = new HexadecimalDisplayViewer();
                    break;
//            case HL:
//                break;
//            case Decimal:
//                break;
//            case HexaDecinal:
//                break;
//            case Scope:
//                break;
                default:

            }
        }
        return v;
    }

    static private Viewer newSources(int position) {
        Viewer v = null;
        if (position < Sources.values().length) {
            switch (Sources.values()[position]) {
                case Toggle_Button_L:
                    v = new LogicalToggleButtonViewerL();
                    break;
                case Toggle_Button_H:
                    v = new LogicalToggleButtonViewerH();
                    break;
                case Push_Button_L:
                    v = new PushButtonViewerL();
                    break;
                case Push_Button_H:
                    v = new PushButtonViewerH();
                    break;
                case Level_High:
                    v = new HighViewer();
                    break;
                case Level_Low:
                    v = new LowViewer();
                    break;
                case Clock_HL:
                    v = new ClockHLViewer();
                    break;
                case Clock_LH:
                    v = new ClockLHViewer();
                    break;
//            case Generator:
//                break;
//            case Clock:
//                break;
                default:
            }
        }
        return v;
    }

    private Viewer newFlipFlops(int position) {
        Viewer v = null;
        if (position < Flip_Flops.values().length) {
            switch (Flip_Flops.values()[position]) {
                case D_Flip_Flop:
                    v = new DFlipFlopViewer();
                    break;
                case D_Latch:
                    v = new DLatchViewer();
                    break;
                case JK_Flip_Flop:
                    v = new JKFlipFlopViewer();
                    break;
                case Monostable:
                    v = new MonoStableViewer();
                    break;
                default:

            }
        }
        return v;
    }

    private Viewer newWires(int position) {
        Viewer v = null;
        if (position < Wires.values().length) {
            switch (Wires.values()[position]) {
                case Wire:
                    v = new WireViewer();
                    break;
                case RestrictedWire:
                    v = new RestrictedWireViewer();
                    break;
                default:
            }
        }
        return v;
    }

    private Viewer newComments(int position) {
        Viewer v = null;
        if (position < Annotations.values().length) {
            switch (Annotations.values()[position]) {
                case Annotation:
                    v = new AnnotationViewer();
                    break;
                default:
            }
        }
        return v;
    }

    private Viewer newConnections(int position) {
        Viewer v = null;
        if (position < Connections.values().length) {
            switch (Connections.values()[position]) {
                case Bus_wire:
                    v = new BusViewer();
                    break;
                case Label:
                    v = new LabelViewer();
                    break;
                case Port:
                    v = new PortViewer();
                    break;
                case PowerConnector:
                    v = new PowerConnectorViewer();
                    break;
                default:
            }
        }
        return v;
    }

    private Viewer newMux(int position) {
        Viewer v = null;
        if (position < Mux.values().length) {
            switch (Mux.values()[position]) {
                case Mux_2_to_1:
                    v = new Mux2To1Viewer();
                    break;
                case Mux_4_to_1:
                    v = new Mux4To1Viewer();
                    break;
                case Mux_8_to_1:
                    v = new Mux8To1Viewer();
                    break;
                default:
            }
        }
        return v;
    }

    private Viewer newDemux(int position) {
        Viewer v = null;
        if (position < Demux.values().length) {
            switch (Demux.values()[position]) {
                case Demux_1_to_2:
                    v = new Demux1To2Viewer();
                    break;
                case Demux_1_to_4:
                    v = new Demux1To4Viewer();
                    break;
                case Demux_1_to_8:
                    v = new Demux1To8Viewer();
                    break;
                default:
            }
        }
        return v;
    }

    static Viewer newTransistors(int position) {
        Viewer v = null;
        if (position < Transistors.values().length) {
            switch (Transistors.values()[position]) {
                case NMOS:
                    v = new NMOSViewer();
                    break;
                case PMOS:
                    v = new PMOSViewer();
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
                default:
            }
        }
        return v;
    }

    public Viewer newViewer(byte selectedGroup, String name) {
        Viewer v = null;
        v = new _74xxxViewer(name);
        return v;
    }

    public Viewer newViewer(int group, int position) {
        Viewer v = null;
        if (group >= 0 && group < DeviceGroup.values().length) {
            switch (DeviceGroup.values()[group]) {
                case Gates:
                    v = newAnsiGate(position);
                    break;
                case Flip_Flops:
                    v = newFlipFlops(position);
                    break;
                case Wires:
                    v = newWires(position);
                    break;
                case Connections:
                    v = newConnections(position);
                    break;
                case Displays:
                    v = newDisplays(position);
                    break;
                case Sources:
                    v = newSources(position);
                    break;
                case Mux:
                    v = newMux(position);
                    break;
                case Demux:
                    v = newDemux(position);
                    break;
                case Transistors:
                    v = newTransistors(position);
                    break;
                case Annotation:
                    v = newComments(position);
                    break;
                case Box:
                    v = newBox(position);
                    break;
                default:
            }
        }
        return v;
    }

}
