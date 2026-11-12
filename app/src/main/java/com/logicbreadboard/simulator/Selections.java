package com.logicbreadboard.simulator;

import android.app.Activity;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.Log;

import java.util.LinkedList;
import java.util.ListIterator;

/**
 * Created by laci on 7/4/15.
 */
public class Selections {
//TODO make class static. DeviceIoViewer selected change synchronize with selectionList and routableSelectionList

    public LinkedList<Viewer> routableSelectionList;
    //    private LinkedList<Viewer> viewerWithSelectedIOsLinkedList;
    boolean movingSelection = false;
    boolean lockedSelection = true;
    boolean lockedSelectionDisabled = false;
    boolean fastSelection = false;
    private LinkedList<Viewer> selectionList;
    private LinkedList<Viewer> cloneList;
    private String selectedLabelText;
    private int contextMenuMask;

    Selections() {
        selectionList = new LinkedList<Viewer>();
        routableSelectionList = new LinkedList<Viewer>();
        cloneList = new LinkedList<Viewer>();
//        viewerWithSelectedIOsLinkedList = new LinkedList<Viewer>();
    }

    Viewer getFirstRoutableSelection() {
        if (routableSelectionList.size() > 0) {
            return routableSelectionList.getFirst();
        } else {
            return null;
        }
    }

    Viewer getLastRoutableSelection() {
        if (routableSelectionList.size() > 0) {
            return routableSelectionList.getLast();
        } else {
            return null;
        }
    }

    //TODO Align selected matrix,  addCandidate/removeInput inverter circle to io

    public Viewer findInSelectedList(EditorModel model, float pointerX,
                                     float pointerY) {
        LinkedList<Viewer> foundList = model.broadcastSearchMsg(selectionList, pointerX, pointerY);
        if (foundList.size() > 0) {
            return foundList.getLast();
        } else {
            return null;
        }
    }

    public void unselect() {
        ListIterator<Viewer> listIterator = selectionList.listIterator();
        while (listIterator.hasNext()) {
            Viewer v = listIterator.next();
            v.unselect(this);
        }
        lockedSelection = true;
        selectionList.clear();
        listIterator = routableSelectionList.listIterator();
        while (listIterator.hasNext()) {
            Viewer v = listIterator.next();
            v.setUnSelected();
        }
        routableSelectionList.clear();
    }

    public void unselect(Viewer v) {
        if (selectionList.contains(v)) {
            v.unselect(this);
            selectionList.remove(v);
            if (v.isRoutable()) {
                unSelectRoutable(v);
            }
        }
    }

    public void rotateSelected(int n) {
        ListIterator<Viewer> listIterator = selectionList.listIterator();
        while (listIterator.hasNext()) {
            Viewer v = listIterator.next();
            v.rotate(n);
        }
    }

    public void rotateSelectedWithoutCheck(int n) {
        ListIterator<Viewer> listIterator = selectionList.listIterator();
        while (listIterator.hasNext()) {
            Viewer v = listIterator.next();
            v.rotateWithoutCheck(n);
        }
    }

    public Viewer copySingle(EditorModel model, int x, int y, Viewer v) {
        Viewer cloned;
        cloned = ViewerFactory.factory.newViewer(v.getGroup(), v.getSymbol());
        cloned.clone(x, y, v);
//        cloned.initPosition(x, y, v.angleIndex);
//        cloned.setNumberOfIos(v.numberOfIos);
        model.add(cloned);
//        cloned.reconnect(model);
        cloneList.add(cloned);
        return cloned;
    }

    public void addInput(EditorModel model, EditorViewer editorViewer) {
        selectionList.getLast().plusIO(editorViewer);
        model.selections.reconnect(model);
        //       unselect();
    }

    public void addSwPort(EditorModel model, EditorViewer editorViewer) {
        selectionList.getLast().plusIO(editorViewer);
    }

    public void removeSwPort(EditorModel model, EditorViewer editorViewer) {
        selectionList.getLast().minusIO(editorViewer);
    }

    public void changeInv(EditorModel model) {
        Viewer v = selectionList.getLast();
        v.changeInv(this);
//        v.unselect(this);
//        unselect();
    }

    public void copyMultiple(EditorModel model, Cursor cursor) {
        ListIterator<Viewer> listIterator = selectionList.listIterator();
        Viewer v = selectionList.getFirst();
        Viewer clone = selectionList.getFirst();
        int xBase = v.getBaseX();
        int yBase = v.getBaseY();
        while (listIterator.hasNext()) {
            v = listIterator.next();
            clone = copySingle(model, cursor.getX() + v.getBaseX() - xBase, cursor.getY() + v.getBaseY() - yBase, v);
        }
        cursor.set(clone.getBaseX(), clone.getBaseY());
//        clone.cursorStep(cursor);
    }

    public boolean copy(EditorModel model, Cursor cursor) {
        if (cursor.getX() == selectionList.getFirst().getBaseX() &&
                cursor.getY() == selectionList.getFirst().getBaseY()) {
            return false;
        }
        if (selectionList.size() > 1) {
            copyMultiple(model, cursor);
//        } else if (selectionList.getFirst().hasSelectedIO()) {
//            selectionList.getFirst().copyIO();
        } else {
            copySingle(model, cursor.getX(), cursor.getY(), selectionList.getFirst()).cursorStep
                    (cursor);
        }
        unselect();
        ListIterator<Viewer> listIterator = cloneList.listIterator();
        while (listIterator.hasNext()) {
            Viewer clone = listIterator.next();
            clone.reconnectFrom(model);
            model.forcedSelect(clone);
        }
        cloneList.clear();
        return true;
    }

    public void deleteSelected(EditorModel model, EditorViewer editorViewer) {
        if (selectionList.size() == 0) {
            return;
        }
        Viewer v = selectionList.getLast();
        if (v.removeSelectedIO(this)) {
            model.selections.reconnect(model);
        } else {
//            model.deletedViewers.clear();
            ListIterator<Viewer> listIterator = selectionList.listIterator();
            while (listIterator.hasNext()) {
                if (EditorModel.faultViewers.contains(v)) {
                    EditorModel.faultViewers.remove(((FaultViewer) v).faultyIo);
                }
                v.delete(editorViewer);
                v = listIterator.next();
                //todo check if boardConnection
                model.remove(v);
//                model.deletedViewers.add(v);
                v.cutNet(model);
            }
        }
        unselect();
    }

    //    public void syncSelection(DeviceIOViewer ioViewer) {
//        if (ioViewer.selected) {
//            routableSelectionList.add(ioViewer);
//        } else if (routableSelectionList.contains(ioViewer)) {
//            routableSelectionList.removeInput(ioViewer);
//        }
//    }
    public void selectRoutable(Viewer v) {
        if (!routableSelectionList.contains(v)) {
            routableSelectionList.add(v);
            v.setSelected();
        }
    }

    public void unSelectRoutable(Viewer v) {
        if (routableSelectionList.contains(v)) {
            routableSelectionList.remove(v);
            v.setUnSelected();
        }
    }

    public boolean isSelected(Viewer v) {
        return selectionList.contains(v);
    }

    public boolean selectIO(int x, int y, EditorModel model) {
        boolean retVal = false;
        DeviceIOViewer ioViewer = null;
        if (selectionList.size() > 0) {
            Viewer v = selectionList.getLast();
            ioViewer = v.findIO(x, y);
            if (ioViewer != null) {
                retVal = true;
                if (routableSelectionList.contains(ioViewer)) {
                    unSelectRoutable(ioViewer);
                } else {
                    selectRoutable(ioViewer);
                }
//                ioViewer.selected = !ioViewer.selected;
//                syncSelection(ioViewer);
            }
        }
        if (routableSelectionList.size() >= 2) {
            model.routing(0, 0);
            model.makeSnapshot();
        }
        return retVal;
    }

    public void invertSelection(Viewer v) {
        if (selectionList.contains(v)) {
            v.unselect(this);
            selectionList.remove(v);
            if (v.isRoutable()) {
                unSelectRoutable(v);
            }
        } else {
            selectionList.add(v);
            if (v.isRoutable()) {
                selectRoutable(v);
            }
        }
    }

    public void select(Viewer v) {
        if (!selectionList.contains(v)) {
            selectionList.add(v);
            if (v.isRoutable()) {
                selectRoutable(v);
            }
        }
    }

    private void drawLastSelected(Canvas canvas, int canvasWidth, int canvasHeight,
                                  float scaleFactor) {
        Viewer v = selectionList.getLast();
        if (v.hasSelectedIO(this)) {
            Viewer.currentPaint = Viewer.Context.cannotDeletePaint;
        } else {
            Viewer.currentPaint = Viewer.Context.selectedPaint;
        }
        Paint savedbbWirePaint = Viewer.Context.bbWirePaint;
        Viewer.Context.bbWirePaint = Viewer.Context.bbSelectedWirePaint;
        float selectionZoom = 1;
        if (Settings.auto_zoom) {
            selectionZoom = Math.max(1, v.getSelectionZoom(canvasHeight, canvasWidth,
                    scaleFactor));
        }
//        Log.i("selzoom=", selectionZoom + " zoom=" + scaleFactor);
//        float selectionZoom = Math.min(canvasWidth / (v.right - v.left),
//                canvasHeight / (v.bottom - v.top)) / (4 * scaleFactor);
        if (!fastSelection && !movingSelection && (selectionZoom > (scaleFactor / 4))) {
            v.drawEnlarged(canvas, selectionZoom);
        } else {
            v.drawEnlarged(canvas, 1);
        }
        Viewer.currentPaint = Viewer.Context.normalPaint;
        Viewer.Context.bbWirePaint = savedbbWirePaint;
    }

    private int drawMultipleSelected(Canvas canvas, int contextMenuMask) {
        Viewer.currentPaint = Viewer.Context.multiSelectedPaint;
        Paint savedbbWirePaint = Viewer.Context.bbWirePaint;
        Viewer.Context.bbWirePaint = Viewer.Context.bbSelectedWirePaint;
        Viewer.currentTextPaint = Viewer.Context.selectedTextPaint;
        ListIterator<Viewer> listIterator = selectionList.listIterator();
        while (listIterator.hasNext()) {
            Viewer v = listIterator.next();
            v.draw(canvas);
            contextMenuMask &= v.getContextMenuMask();
        }
        Viewer.currentPaint = Viewer.Context.normalPaint;
        Viewer.currentTextPaint = Viewer.Context.textPaint;
        Viewer.Context.bbWirePaint = savedbbWirePaint;
        return contextMenuMask;
    }

    public void draw(Canvas canvas, int canvasWidth, int canvasHeight, float scaleFactor,
                     EditorModel model) {
        boolean selectionListIsNotEmpty = true;
        int numberOfSelectedItems = selectionList.size();
        if (numberOfSelectedItems == 0) {
            selectionListIsNotEmpty = false;
            numberOfSelectedItems = routableSelectionList.size();
        }
        int contextMenuMask = 0;
        if (numberOfSelectedItems > 0) {
            if (selectionListIsNotEmpty) {
                contextMenuMask = 0xffffffff;
            } else {
                contextMenuMask = 0;
            }
            contextMenuMask = drawMultipleSelected(canvas, contextMenuMask);
            if (selectionListIsNotEmpty) {
                drawLastSelected(canvas, canvasWidth, canvasHeight, scaleFactor);
            }
            ListIterator<Viewer> listIterator = routableSelectionList.listIterator();
            while (listIterator.hasNext()) {
                Viewer v = listIterator.next();
                contextMenuMask &= v.getContextMenuMask();
            }
//            if (routableSelectionList.size() > 2 || routableSelectionList.size() == 0) {
            if (routableSelectionList.size() == 0 || (
                    routableSelectionList.size() == 1 && getFirstRoutableSelection() instanceof DeviceIOViewer
            )) {
                contextMenuMask &= ~ContextMenuIcons.ROUTE;
            } else {
                contextMenuMask |= ContextMenuIcons.ROUTE;
                contextMenuMask |= ContextMenuIcons.UNSELECT;
            }
            if (numberOfSelectedItems == 1) {
                contextMenuMask &=
                        ~(ContextMenuIcons.ALIGN_HORIZONTAL | ContextMenuIcons.ALIGN_VERTICAL);
            }
            if (lockedSelection) {
                contextMenuMask &= ~ContextMenuIcons.LOCK;
            } else {
                contextMenuMask &= ~ContextMenuIcons.UNLOCK;
            }
            lockedSelectionDisabled = (((ContextMenuIcons.LOCK | ContextMenuIcons.UNLOCK) &
                    contextMenuMask) == 0);
        }
        if (!model.simulationIsRunning) {
            if (model.snapshotIndex > 1) {
                contextMenuMask |= ContextMenuIcons.UNDO;
            }
            if (model.snapshotIndex < EditorModel.MAX_NUMBER_OF_SNAPSHOTS) {
                if (model.snapshots[model.snapshotIndex] != null) {
                    contextMenuMask |= ContextMenuIcons.REDO;
                }
            }
            if (contextMenuMask != 0) {
//                ContextMenuIcons.drawContextMenu(canvas, contextMenuMask, canvasHeight, canvasWidth,
//                        scaleFactor);
            }
        }
    }

    public int getContextMenuMask(EditorModel model) {
        contextMenuMask = 0;
        int numberOfSelectedItems = selectionList.size();
        if (numberOfSelectedItems > 0) {
            contextMenuMask = 0xffffffff;
            ListIterator<Viewer> listIterator = selectionList.listIterator();
            while (listIterator.hasNext()) {
                Viewer v = listIterator.next();
                contextMenuMask &= v.getContextMenuMask();
            }
//            if (routableSelectionList.size() > 2 || routableSelectionList.size() == 0) {
            if (routableSelectionList.size() == 0 || (
                    routableSelectionList.size() == 1 && getFirstRoutableSelection() instanceof DeviceIOViewer
            )) {
                contextMenuMask &= ~ContextMenuIcons.ROUTE;
            } else {
                contextMenuMask |= ContextMenuIcons.ROUTE;
                contextMenuMask |= ContextMenuIcons.UNSELECT;
            }
            if (numberOfSelectedItems == 1) {
                contextMenuMask &=
                        ~(ContextMenuIcons.ALIGN_HORIZONTAL | ContextMenuIcons.ALIGN_VERTICAL);
            }
            if (lockedSelection) {
                contextMenuMask &= ~ContextMenuIcons.LOCK;
            } else {
                contextMenuMask &= ~ContextMenuIcons.UNLOCK;
            }
            lockedSelectionDisabled = (((ContextMenuIcons.LOCK | ContextMenuIcons.UNLOCK) &
                    contextMenuMask) == 0);
        }
        if (!model.simulationIsRunning && !model.enableScope) {
            if (model.snapshotIndex > 1) {
                contextMenuMask |= ContextMenuIcons.UNDO;
//                Log.i("CONTEXTMENU","UNDO");
            }
            if (model.snapshotIndex < EditorModel.MAX_NUMBER_OF_SNAPSHOTS) {
                if (model.snapshots[model.snapshotIndex] != null) {
                    contextMenuMask |= ContextMenuIcons.REDO;
//                    Log.i("CONTEXTMENU","REDO");
                }
            }
        } else {
            contextMenuMask = 0;
        }
        return contextMenuMask;
    }

    public void move(EditorViewer editorViewer, float fromX, float fromY, int xOffset, int yOffset) {
        if (!selectionList.isEmpty()) {
            movingSelection = true;
            boolean moveInsideOk = true;
            ListIterator<Viewer> listIterator = selectionList.listIterator();
            while (listIterator.hasNext()) {
                Viewer v = listIterator.next();
                moveInsideOk = v.moveCheck(xOffset, yOffset);
                if (!moveInsideOk) {
                    break;
                }
            }
            if (moveInsideOk) {
                if (selectionList.size() == 1) {
                    Viewer w = selectionList.getFirst();
                    if (!w.moveSegmentOrPoint(editorViewer, fromX, fromY, xOffset, yOffset)) {
                        moveEntireObjects(editorViewer, xOffset, yOffset, Viewer.CHECK_INSIDE);
                    }
                } else {
                    moveEntireObjects(editorViewer, xOffset, yOffset, Viewer.CHECK_INSIDE);
                }
            }
        }
    }

    public void moveWithoutCheck(EditorViewer editorViewer, float fromX, float fromY, int xOffset, int yOffset) {
        if (!selectionList.isEmpty()) {
            movingSelection = true;
            if (selectionList.size() == 1) {
                Viewer w = selectionList.getFirst();
                if (!w.moveSegmentOrPoint(editorViewer, fromX, fromY, xOffset, yOffset)) {
                    moveEntireObjects(editorViewer, xOffset, yOffset, Viewer.DO_NOT_CHECK_INSIDE);
                }
            } else {
                moveEntireObjects(editorViewer, xOffset, yOffset, Viewer.DO_NOT_CHECK_INSIDE);
            }
        }
    }

    public void moveEntireObjects(EditorViewer editorViewer, int xOffset, int yOffset, boolean check) {
        ListIterator<Viewer> listIterator;
        listIterator = selectionList.listIterator();
        while (listIterator.hasNext()) {
            Viewer v = listIterator.next();
            if (!lockedSelection || lockedSelectionDisabled) {
                v.disconnectFrom(editorViewer.model);
            }
            v.move(editorViewer, xOffset, yOffset, check);
        }
    }

    public boolean isEmpty() {
        return selectionList.size() == 0;
    }

    public int getNumberOfRoutableSelections() {
        return routableSelectionList.size();
    }

    public void reconnect(EditorModel em) {
        ListIterator<Viewer> listIterator = selectionList.listIterator();
        while (listIterator.hasNext()) {
            Viewer v = listIterator.next();
            v.reconnectFrom(em);
        }
    }

//    public void reconnectUndeleted(EditorModel em) {
//        ListIterator<Viewer> listIterator = em.deletedViewers.listIterator();
//        while (listIterator.hasNext()) {
//            Viewer v = listIterator.next();
//            v.reconnectFrom(em);
//        }
//        em.deletedViewers.clear();
//    }

    public boolean isRoutableSelected(Viewer v) {
        return routableSelectionList.contains(v);
    }

    public void setDelay(Activity activity) {
        selectionList.getLast().showDelayPopup(activity);
    }

    public String rerouteAllSelected(EditorModel editorModel) {
//        LinkedList<Integer> netlistToReroute = new LinkedList<Integer>();
        ListIterator<Viewer> listIterator = selectionList.listIterator();
//        while (listIterator.hasNext()) {
//            Viewer v = listIterator.next();
//            v.prepareReroute(netlistToReroute);
//        }
//        ListIterator<Integer> netListIterator = netlistToReroute.listIterator();
//        while (netListIterator.hasNext()) {
//            Integer i = netListIterator.next();
//            editorModel.removeWires(i.intValue());
//        }
        listIterator = selectionList.listIterator();
        while (listIterator.hasNext()) {
            Viewer v = listIterator.next();
            String res = v.reroute(editorModel);
            if (res != null) {
                return res;
            }
        }
        return null;
    }

    public String getSelectedLabelText() {
        return selectionList.getLast().getLabel();
    }

    public void setSelectedLabelText(String selectedLabelText) {
        ListIterator<Viewer> listIterator = selectionList.listIterator();
        while (listIterator.hasNext()) {
            Viewer v = listIterator.next();
            v.setLabel(selectedLabelText);
        }
    }

    public void removeFirstRoutable() {
        if (routableSelectionList.size() > 0) {
            Viewer v = routableSelectionList.getFirst();
            routableSelectionList.remove(v);
            selectionList.remove(v);
            v.setUnSelected();
        }
    }

    public String alignHorizontal(EditorViewer editorViewer) {
        int toBottom = ((DeviceViewer) selectionList.getFirst()).bottom;
        int x = ((DeviceViewer) selectionList.getFirst()).left;
        int dx = ((DeviceViewer) selectionList.get(1)).left - x;
        int nextXPos = x;
        ListIterator<Viewer> listIterator = selectionList.listIterator();
        while (listIterator.hasNext()) {
            Viewer v = listIterator.next();
            if (nextXPos != x) {
                if (v.alignHorizontal(editorViewer, nextXPos, toBottom)) {
                    if (nextXPos != x) {
                        String ret = v.reroute(editorViewer.model);
                        if (ret != null) {
                            return ret;
                        }
                    } else {
                        return "Could not move";
                    }
                }
            }
            nextXPos += dx;
        }
        return null;
    }

    public String alignVertical(EditorViewer editorViewer) {
        int toLeft = ((DeviceViewer) selectionList.getFirst()).left;
        int y = ((DeviceViewer) selectionList.getFirst()).bottom;
        int dy = ((DeviceViewer) selectionList.get(1)).bottom - y;
        int nextYPos = y;
        ListIterator<Viewer> listIterator = selectionList.listIterator();
        while (listIterator.hasNext()) {
            Viewer v = listIterator.next();
            if (nextYPos != y) {
                if (v.alignHorizontal(editorViewer, toLeft, nextYPos)) {
                    String ret = v.reroute(editorViewer.model);
                    ;
                    if (ret != null) {
                        return ret;
                    }
                } else {
                    return "Could not move";
                }
            }
            nextYPos += dy;
        }
        return null;
    }

    public void endMove(EditorModel model) {
        movingSelection = false;
        if (selectionList.size() == 1 && selectionList.getFirst().isWire()) {
            Viewer w = selectionList.getFirst();
            w.endSegmentMove(model);
        }
        ListIterator<Viewer> listIterator = selectionList.listIterator();
        while (listIterator.hasNext()) {
            Viewer v = listIterator.next();
            v.endMove();
        }
    }

    public Viewer getLastSelected() {
        return selectionList.getLast();
    }

    public void setHolesFree(BreadBoardViewer breadBoardViewer) {
        ListIterator<Viewer> listIterator = selectionList.listIterator();
        while (listIterator.hasNext()) {
            Log.i("setHolesFree", "setHolesFree");
            Viewer v = listIterator.next();
            if (!v.isFloating()) {
                v.setHolesFree(breadBoardViewer);
                v.setFloating();
            }
        }
    }

    public void setHolesUsed(BreadBoardViewer breadBoardViewer) {
        ListIterator<Viewer> listIterator = selectionList.listIterator();
        while (listIterator.hasNext()) {
            Viewer v = listIterator.next();
            v.setHolesUsed(breadBoardViewer);
        }
    }

    public int getNumberOfSelections() {
        return selectionList.size();
    }

    public void setLToH() {
        ListIterator<Viewer> listIterator = selectionList.listIterator();
        while (listIterator.hasNext()) {
            Viewer v = listIterator.next();
            v.setLToH();
        }
    }

    public void setHToL() {
        ListIterator<Viewer> listIterator = selectionList.listIterator();
        while (listIterator.hasNext()) {
            Viewer v = listIterator.next();
            v.setHToL();
        }
    }

    public void setDisabled() {
        ListIterator<Viewer> listIterator = selectionList.listIterator();
        while (listIterator.hasNext()) {
            Viewer v = listIterator.next();
            v.setDisabled();
        }
    }

    public boolean isMovable() {
        ListIterator<Viewer> listIterator = selectionList.listIterator();
        while (listIterator.hasNext()) {
            Viewer v = listIterator.next();
            if (!v.isMovable()) {
                return false;
            }
        }
        return true;
    }

    public void setAllTime(int[] d) {
        ListIterator<Viewer> listIterator = selectionList.listIterator();
        while (listIterator.hasNext()) {
            Viewer v = listIterator.next();
            if (v instanceof DeviceViewer) {
                if (((DeviceViewer) v).model.delays.length == d.length) {
                    System.arraycopy(d, 0, ((DeviceViewer) v).model.delays, 0, d.length);
                }
            }
        }
    }
}