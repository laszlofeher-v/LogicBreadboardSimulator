package com.logicbreadboard.simulator;

import android.util.Log;

import com.google.android.gms.analytics.HitBuilders;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;

/**
 * Created on 5/28/16.
 */
public class _DIPModel extends DIPModel {
//    public DeviceIOViewer firstIoViewer;
//    EditorModel editorModel;
//    LinkedList<DeviceModel> deviceModels;
//    VerilogSymTable symTable;
//    String fileName;
//    boolean hdl;

    _DIPModel() {
        super();
        fileName = new String("");
        editorModel = new EditorModel();
    }

    @Override
    void loadContent() {
        if (fileName.length() == 0) {
            return;
        }
        String loadableFilename = fileName;
//        Log.i("DIP:", loadableFilename);
//        File oldFile = new File(loadableFilename);
        InputStream inputStream = null;
        try {
            inputStream = MainActivity.assetManager.open(loadableFilename);

        } catch (IOException ex) {
            ex.printStackTrace();
            return;
        }
        if (loadableFilename.substring((loadableFilename.lastIndexOf(".") + 1), loadableFilename.length()).equals("v")) {
            MainActivity.mTracker.send(new HitBuilders.EventBuilder()
                    .setCategory("Action")
                    .setAction("Vrlg")
                    .build());
            hdl = true;
            VerilogParser parser = new VerilogParser();
            VerilogSetImplemented.set(parser);
            VerilogSetImplemented.toastDisplayed = false;
            VerilogScanner scanner = new VerilogScanner();
            VerilogToken firstToken = scanner.scan(loadableFilename);
            VerilogRider rider = new VerilogRider();
            rider.token = firstToken;
            rider.token = firstToken;
            rider.node = parser.descriptionProduct;
            rider.indent = 1;
            rider.token.path = new HashSet<>();
            Log.i("Verilog", "Start");
            boolean result = rider.node.next(rider);
            if (!result) {
                Log.i("Verilog", "Error found:" + rider.token.sym);
                return;
            }
            VerilogRider.Link link = null;
            editorModel.deleteAll();
            while (rider.link != null) {
                VerilogRider.Link tmp = rider.link.back;
                rider.link.back = link;
                link = rider.link;
                rider.link = tmp;
            }

            VerilogCode firstCode = new VerilogCode();
            VerilogCode code = firstCode;
            VerilogContext context = new VerilogContext();
            context.symTable = new VerilogSymTable();
            context.token = firstToken;
            context.editorModel = editorModel;
            VerilogRider.Link prev = link;
            String lastProduct = link.node.getIdent();
//        context.link.node.code.gen(context);
            while (link != null) {
                context.link = link;
//            System.out.println(link.node.getIdent() + " " + link.token.sym + " " + link.token.ident);
                if (link.node.getIdent() != null && link.node.getIdent().length() > 0) {
                    lastProduct = link.node.getIdent();
                }
                if (link.node.code != null) {
                    if (!link.node.code.gen(context)) {
                        Log.i("Verilog", "Not yet:" + link.node.toString() + " " + link.node.getIdent() + " " + link.node.getTerminal() + " " + lastProduct);
                    } else {
                        if (link.node.getIdent() != null && link.node.getIdent().length() > 0) {
                            Log.i("Verilog", link.node.getIdent() + " " + link.token.sym + " " + link.token.ident);
                        }
                    }
                } else {
                    Log.i("Verilog", "Missing code.");
                }
//            link.node.codeSkipTerminal(context);
                link = link.back;
            }
            deviceModels = context.deviceModels;
            symTable = context.symTable;
//            while (link != null) {
//                Log.i("Verilog", link.node.getIdent());
//                if (link.node.code != null) {
//                    if (!link.node.code.gen(context)) {
//                        Log.i("Verilog", "Not yet:" + link.node.getIdent() + " " + link.node.getTerminal()+" "+link.node.toString());
//                    }
//                }
//                link.node.codeSkipTerminal(context);
//                prev = link;
//                link = link.back;
//            }

        } else {
            MainActivity.mTracker.send(new HitBuilders.EventBuilder()
                    .setCategory("Action")
                    .setAction("Nvrlg")
                    .build());
            hdl = false;
            try {
                editorModel.deleteAll();
                if (!NativeSaveLoad.loadSub(loadableFilename, inputStream, editorModel)) {
                    editorModel.deleteAll();
                    return;
                }
                editorModel.checkConnections();
//            Log.i("DIP:", "done");
//            invalidate();
//            model.makeSnapshot();
            } catch (Exception ex) {
                ex.printStackTrace();
                return;
            }
            return;
        }
    }

}
