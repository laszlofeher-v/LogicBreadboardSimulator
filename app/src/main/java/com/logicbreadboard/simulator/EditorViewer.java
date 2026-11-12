package com.logicbreadboard.simulator;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.net.Uri;
import android.os.AsyncTask;
import androidx.core.view.GestureDetectorCompat;
import android.text.InputFilter;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;
import android.widget.ZoomControls;

/**
 * Created on 12/16/14.
 */
public class EditorViewer extends RelativeLayout {
    static final float MAX_ZOOM = 16.0f;
    static final float MIN_ZOOM = 0.1f;
    static final Rect clipBounds = new Rect();
    static float staticXOffset;
    static float staticYOffset;
    protected int SCROLL_MARGIN;
    //    static Paint paint = new Paint();
    static int densityDpi;
    //    static {
//        paint.setStyle(Paint.Style.STROKE);
//        paint.setColor(Color.BLACK);
//        paint.setStrokeWidth(20);
//    }
    protected ZoomControls zoom;
    protected float scaleFactor = 1f;
    public static float staticScaleFactor = 1f;
    public static int xOrigo = 0;
    public static int yOrigo = 0;
    protected float xOffset = -EditorModel.MAX_X / 2;
    protected float yOffset = -EditorModel.MAX_Y / 2;
    protected float xDown = 0;
    protected float yDown = 0;
    protected ScaleGestureDetector scaleDetector;
    //    protected boolean newCursorPosition = false;
    protected boolean moveCursorEnabled = false;
    protected boolean moveSelectionEnabled = false; // true if a selected device touched
    protected GestureDetectorCompat gestureListener;
    protected int canvasWidth;
    protected int canvasHeight;
    protected float scopeTimeZoom = 1;
    protected float moveXOffset = 0;
    protected float moveYOffset = 0;
    Cursor cursor;
    EditorModel model;
    float px;
    float py;
    MainActivity activity;
    protected boolean fastSelectionDisabled;

    public EditorViewer(Context activity) {
        super(activity);
    }

    public EditorViewer(Context activity, AttributeSet attrs) {
        super(activity, attrs);
    }

    public EditorViewer(Context activity, AttributeSet attrs, int defStyle) {
        super(activity, attrs, defStyle);
    }

    protected void setOffsetsToScale() {
        int x0 = Math.round((0 - xOrigo) / scaleFactor + xOrigo - xOffset);
        int y0 = Math.round((0 - yOrigo) / scaleFactor + yOrigo - yOffset);
        int xMax = Math.round((canvasWidth - xOrigo) / scaleFactor + xOrigo - xOffset);
        int yMax = Math.round((canvasHeight - yOrigo) / scaleFactor + yOrigo - yOffset);
//        int x0 = -Math.round(((0 - xOrigo) / scaleFactor + xOrigo -
//                xOffset) * scaleFactor);
//        int y0 = Math.round(((0 - yOrigo) / scaleFactor + yOrigo - yOffset) * scaleFactor);
//        int xMax = Math.round(x0 + EditorModel.MAX_X * scaleFactor);
//        int yMax = Math.round(y0 - EditorModel.MAX_Y * scaleFactor);
        final float H = 100 / scaleFactor;
        if (!(xMax >= H)) {
            xOffset = (canvasWidth - xOrigo) / scaleFactor + xOrigo - H;
        }
        if (!(x0 <= EditorModel.MAX_X - H)) {
            xOffset = (-xOrigo) / scaleFactor + xOrigo - (EditorModel.MAX_X - H);
        }
        if (!(yMax >= H)) {
            yOffset = (canvasHeight - yOrigo) / scaleFactor + yOrigo - H;
        }
        if (!(y0 <= EditorModel.MAX_Y - H)) {
            yOffset = (-yOrigo) / scaleFactor + yOrigo - (EditorModel.MAX_Y - H);
        }
    }

    public void init(final MainActivity activity, EditorModel editorModel) {
        this.activity = activity;
        scaleDetector = new
                ScaleGestureDetector(this.activity, new ScaleListener());
        gestureListener = new GestureDetectorCompat(this.activity, new EditorGestureListener());
        setZoomControl(activity);
        DisplayMetrics dm = getResources().getDisplayMetrics();
        densityDpi = dm.densityDpi;
    }

    public void scopeButtonHandler() {
    }

    public void setZoomControl(Activity activity) {
        zoom = (ZoomControls) activity.findViewById(R.id.zoomControl);
        if (Settings.hideZoomControl) {
            zoom.setVisibility(View.GONE);
        } else {
            zoom.setVisibility(View.VISIBLE);
        }
        zoom.setOnZoomInClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                scaleFactor *= 2;
                //              xOrigo = getWidth() / 2;
                //              yOrigo = getHeight() / 2;
                scaleFactor = (float) Math.max(MIN_ZOOM, Math.min(scaleFactor, MAX_ZOOM));
                Android511workaround();
                setOffsetsToScale();
                invalidate();
            }
        });
        zoom.setOnZoomOutClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                scaleFactor *= 0.5f;
                //               xOrigo = getWidth() / 2;
                //               yOrigo = getHeight() / 2;
                scaleFactor = (float) Math.max(MIN_ZOOM, Math.min(scaleFactor, MAX_ZOOM));
                Android511workaround();
                setOffsetsToScale();
                invalidate();
            }
        });
    }


    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        invalidate();
        int size = ev.getPointerCount();
        int maskedAction = ev.getActionMasked();
        if (size > 1) {
            if (scaleDetector.onTouchEvent(ev) == true) {
                return true;
            }
            ;
        } else {
            if (maskedAction == MotionEvent.ACTION_DOWN ||
                    maskedAction == MotionEvent.ACTION_POINTER_DOWN) {
                setTouchPosOnCanvas(ev);
                fastSelectionDisabled = false;
            }
            if (gestureListener.onTouchEvent(ev) == true) {
                return true;
            }
        }
        switch (maskedAction) {
            case MotionEvent.ACTION_MOVE:
                if (moveCursorEnabled) {
                    setCursorPosOnCanvas(ev);
                    setTouchPosOnCanvas(ev);
                    setCursor2(ev, Settings.CURSOR__OFFSET_10);
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_POINTER_UP:
                cursor.touch = false;
                invalidate();
                if (model.selections.movingSelection) {
                    if (model.selections.lockedSelection && !model.selections
                            .lockedSelectionDisabled) {
                        //try reroute
                        String ret = model.rerouteMoved();
                        if (ret != null) {
                            Toast.makeText(activity, ret, Toast.LENGTH_LONG).show();
                        }
                    }
                    model.makeSnapshot();
                    clearMovingSelection();
                }
                moveCursorEnabled = false;
                clearMovingSelection();
                moveXOffset = 0;
                moveYOffset = 0;
                cursor.normalCursor();
                ((MainActivity) activity).setContextMenuVisibility();
                invalidate();
                break;

            default:
        }
        return true;
    }

    public void clearMovingSelection() {
        if (model.selections.movingSelection) {
            model.selections.endMove(model);
            invalidate();
        }
    }

    protected void setTouchPosOnCanvas(MotionEvent ev) {
//        cursor.enabled = false;
        //       invalidate((int) cursor.left - 1, (int) cursor.top - 1, (int) cursor.right + 1, (int) cursor.bottom + 1);
        cursor.touch = true;
        px = ev.getX();
        py = ev.getY();
        Cursor.touchX = Math.round((px - xOrigo) / scaleFactor + xOrigo - xOffset);
        Cursor.touchY = Math.round((py - yOrigo) / scaleFactor + yOrigo - yOffset);
//        CursorPosOnCanvas.x = (px) / scaleFactor - xOffset;
//        CursorPosOnCanvas.y = (py) / scaleFactor - yOffset;
    }

    protected void setCursorPosOnCanvas(MotionEvent ev) {
//        cursor.enabled = false;
        //       invalidate((int) cursor.left - 1, (int) cursor.top - 1, (int) cursor.right + 1, (int) cursor.bottom + 1);
        px = ev.getX();
        py = ev.getY();
        CursorPosOnCanvas.x = Math.round((px - xOrigo) / scaleFactor + xOrigo - xOffset);
        CursorPosOnCanvas.y = Math.round((py - yOrigo) / scaleFactor + yOrigo - yOffset);
//        CursorPosOnCanvas.x = (px) / scaleFactor - xOffset;
//        CursorPosOnCanvas.y = (py) / scaleFactor - yOffset;
    }

    protected void setCursor2(MotionEvent ev, int offset) {
        CursorPosOnCanvas.y = Math.round(CursorPosOnCanvas.y - offset * densityDpi /
                (10 * scaleFactor));
        cursor.set(CursorPosOnCanvas.x, CursorPosOnCanvas.y);
        cursor.enabled = true;
        if (model.selections.fastSelection) {
            model.selectFast(CursorPosOnCanvas.x, CursorPosOnCanvas.y);
        }
        ((MainActivity) activity).setContextMenuVisibility();
        invalidate();
    }

    private boolean select(MotionEvent ev, int offset) {
        setCursorPosOnCanvas(ev);
        if (!model.select(CursorPosOnCanvas.x, CursorPosOnCanvas.y)) {
            setCursor2(ev, offset);
            ((MainActivity) activity).setContextMenuVisibility();
            invalidate();
            return false;
        } else {
            moveSelectionEnabled = true;
            ((MainActivity) activity).setContextMenuVisibility();
            invalidate();
            return true;
        }
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        canvasWidth = getWidth();
        canvasHeight = getHeight();
        SCROLL_MARGIN = Math.min(canvasHeight, canvasWidth);

//        canvasWidth = getWidth();
//        canvasHeight = getHeight();
//        if (model == null) {
//            return;
//        }
//        if (model.enableScope) {
//            canvas.scale(scaleFactor, scaleFactor, 0, 0);
//            canvas.translate(xOffset, yOffset);
//            float x = -xOffset;
//            float y = -yOffset;
//            canvas.save();
//            Rect clipBounds = canvas.getClipBounds();
//            final float textLength = Viewer.Context.scopeTextPaint.measureText("WWWWWW");
//            clipBounds.left = Math.round(x + textLength);
//            canvas.clipRect(clipBounds);
//            canvas.save();
//            canvas.translate(-xOffset, 0);
//            model.showScope(canvas, Math.round(textLength), Math.round(x), canvasWidth,
//                    scaleFactor, scopeTimeZoom, y);
//            canvas.restore();
//            canvas.restore();
//            model.showScopeChannelNames(canvas, x, y);
//        } else {
//            xOrigo = canvasWidth / 2;
//            yOrigo = canvasHeight / 2;
//            canvas.translate(xOffset, yOffset);
//            canvas.scale(scaleFactor, scaleFactor, xOrigo - xOffset, yOrigo - yOffset);
//            canvas.getClipBounds(clipBounds);
//            Viewer.Context.borderPaint.setStrokeWidth(2 / scaleFactor);
////            Viewer.Context.borderPaint.setStyle(Paint.Style.FILL);
////            canvas.clipRect(EditorModel.MIN_X, EditorModel.MIN_Y, EditorModel
////                    .MAX_X, EditorModel.MAX_Y, Region.Op.DIFFERENCE);
////            canvas.drawARGB(255, 0, 50, 0);
////            //restore full canvas clip for any subsequent operations
////            canvas.clipRect(new Rect(0, 0, canvas.getWidth(), canvas.getHeight())
////                    , Region.Op.REPLACE);
//            canvas.drawRect(EditorModel.MIN_X, EditorModel.MIN_Y, EditorModel
//                    .MAX_X, EditorModel.MAX_Y, Viewer.Context.borderPaint);
//            ContextMenuIcons.left = -xOffset + (xOrigo) - (xOrigo) / scaleFactor;
//            ContextMenuIcons.top = -yOffset + (yOrigo) - (yOrigo) / scaleFactor;
//            if (model != null) {
//                model.drawAll(canvas, canvasWidth, canvasHeight, scaleFactor, xOffset, yOffset);
//                cursor.draw(canvas, scaleFactor);
//            }
//        }
    }

    public boolean setHoleUsed(int x, int y) {
        return false;
    }

    public boolean setHoleUsed(int x, int y, DeviceIOViewer iov) {
        return false;
    }

    public boolean setHoleUsed(int x, int y, Viewer wire) {
        return false;
    }

    public void setHoleFree(int x, int y) {
    }

    public void tryReroute() {
        String ret = model.rerouteMoved();
        if (ret != null) {
            Toast.makeText(activity, ret, Toast.LENGTH_LONG).show();
        }
    }

    public void Android511workaround() {
//        Log.i("Android511workaround", "scaleFactor0=" + scaleFactor);
        if (model.enableScope) {
            if (scaleFactor > 3.9) { //workaround to avoid crash when scope displayed on nexus
                // 7 Android 5.1.1
                scaleFactor = 3.9f;
            }
        } else {
            if (scaleFactor > 7.9) { //workaround to avoid wrong cursor on
                // nexus
                // 7 Android 5.1.1
                scaleFactor = 7.9f;
            }
        }
    }

    public void setScopeTimeZoom(float z) {
    }

    public void runButtonHandler() {
    }

    protected void centerCursor() {
//        int x0 = -Math.round((-cursor.getX() + xOrigo) * scaleFactor - xOrigo - xOffset);
//        int y0 = -Math.round((-cursor.getY() + yOrigo) * scaleFactor - yOrigo - yOffset);
//        Log.i("centerCursor", "x0=" + x0);
//        Log.i("centerCursor", "y0=" + y0 + "\n");
        int x0 = cursor.getX();
        int y0 = cursor.getY();
        if (x0 < clipBounds.left || x0 > clipBounds.right) {
            xOffset = -(x0 - xOrigo);
        }
        if (y0 < clipBounds.top || y0 > clipBounds.bottom) {
            yOffset = -(y0 - yOrigo);
        }
    }

    public void restore(String fileName) {
    }

    public void save(String projectFileName) {
    }

    public void restoreAfterScope() {
    }

    public void runFromScopeButtonHandler() {
    }

    public void setTimeViewVisibility(int visibility) {
    }

    public void stopSimulation() {
    }

    public void add(Viewer device) {
    }

    public View getCanvas() {
        return null;
    }

    public void gotoWeb() {
    }

    public void undo() {
        model.undo(this);
    }

    public void redo() {
        model.redo(this);
    }

    public void rPlusLed() {
    }

    public void led() {
    }

    public void resistor() {
    }

    public void addInterfaces() {
    }

    public void lToH() {
    }

    public void hToL() {
    }

    public void disable() {
    }

    void removeInput(EditorViewer editorViewer) {
    }

    static class CursorPosOnCanvas {
        static int x;
        static int y;
    }

    private class LongCommandTask extends AsyncTask<EditorModel, Integer, String> {
        ProgressDialog pd;

        // Runs in UI before background thread is called
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Do something like display a progress bar
        }

        // This is run in a background thread
        @Override
        protected String doInBackground(EditorModel... params) {
            publishProgress();
            return " ";
        }

        // This is called from background thread but runs in UI
        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            invalidate();

            // Do things like update the progress bar
        }

        // This runs in UI when background thread finishes
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
//            pd.dismiss();
            // Do things like hide the progress bar or change a TextView
//            commandButtonsHandler.buttons[1].setImageResource(R.drawable
//                    .ic_play_arrow_black_24dp);
            invalidate();
        }
    }

    protected class EditorGestureListener
            extends GestureDetector.SimpleOnGestureListener {

        int delay = 1;

        @Override
        public boolean onDown(MotionEvent ev) {
            setTouchPosOnCanvas(ev);
            invalidate();
            setCursorPosOnCanvas(ev);
            Viewer v = model.selections.findInSelectedList(model, CursorPosOnCanvas.x,
                    CursorPosOnCanvas.y);
            if (v != null) {
                moveSelectionEnabled = true;
            } else {
                moveSelectionEnabled = false;
            }
            boolean res = true;
            return res;
        }

        @Override
        public void onShowPress(MotionEvent motionEvent) {
        }

        public boolean onSingleTapConfirmed(MotionEvent ev) {
            boolean processed = false;
            setCursorPosOnCanvas(ev);
            if (Settings.auto_zoom) {
                processed = model.selections.selectIO(CursorPosOnCanvas.x,
                        CursorPosOnCanvas.y, model);
            } else {
                processed = model.selectIO(CursorPosOnCanvas.x,
                        CursorPosOnCanvas.y);
            }
// TODO: 11/9/15
//            processed = model.selectIOGlobal(CursorPosOnCanvas.x,
//                    CursorPosOnCanvas.y);
            if (processed) {
                activity.setContextMenuVisibility();
                invalidate();
                return true;
            }
            select(ev, 0);
            invalidate();
            ((MainActivity) activity).setContextMenuVisibility();
            return true;
        }

        @Override
        public boolean onSingleTapUp(MotionEvent ev) {
            cursor.touch = false;
            invalidate();
            if (model.selections.movingSelection) {
                if (model.selections.lockedSelection && !model.selections
                        .lockedSelectionDisabled) { //try reroute
                    tryReroute();
                }
                clearMovingSelection();
                model.makeSnapshot();
                activity.setContextMenuVisibility();
                invalidate();
            }
            return true;
        }

        @Override
        public void onLongPress(MotionEvent ev) {
            fastSelectionDisabled = select(ev, Settings.CURSOR__OFFSET_10);
            if (!fastSelectionDisabled) { // fast selection mode can be started only on free area
                xDown = ev.getX();
                yDown = ev.getY();
                moveCursorEnabled = true;
                model.selections.fastSelection = true;
                cursor.redCursor(model.router);
            }
        }

        public boolean onDoubleTapEvent(MotionEvent ev) {
            return true;
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            if (model.enableScope) {
                setTouchPosOnCanvas(e2);
                scrollScope(distanceX, distanceY);
            } else if (moveSelectionEnabled) {
                setTouchPosOnCanvas(e2);
                moveXOffset -= distanceX / scaleFactor;
                moveYOffset -= distanceY / scaleFactor;
                int ix = (int) moveXOffset;
                float fx = moveXOffset - ix;
                if (ix != 0) {
                    moveXOffset = fx;
                }
                int iy = (int) moveYOffset;
                float fy = moveYOffset - iy;
                if (iy != 0) {
                    moveYOffset = fy;
                }
                if (ix != 0 || iy != 0) {
                    float fromX = e1.getX();
                    float fromY = e1.getY();
                    fromX = (fromX - xOrigo) / scaleFactor + xOrigo - xOffset;
                    fromY = (fromY - yOrigo) / scaleFactor + yOrigo - yOffset;
                    model.selections.move(EditorViewer.this, fromX, fromY, ix, iy);
                    if (!model.selections.lockedSelection || model.selections
                            .lockedSelectionDisabled) { //try reconnect
                        model.selections.reconnect(model);
                    }
                    activity.setContextMenuVisibility();
                    invalidate();
                }
            } else {
                float newXOffset = xOffset - distanceX / scaleFactor;
                float newYOffset = yOffset - distanceY / scaleFactor;
                int x0 = -Math.round(((0 - xOrigo) / scaleFactor + xOrigo - newXOffset) * scaleFactor);
                int y0 = Math.round(((0 - yOrigo) / scaleFactor + yOrigo - newYOffset) * scaleFactor);
                int xMax = Math.round(x0 + EditorModel.MAX_X * scaleFactor);
                int yMax = Math.round(y0 - EditorModel.MAX_Y * scaleFactor);
//                Log.i("onScroll", "x0=" + x0 + " xMax=" + xMax);
//                Log.i("onScroll", "y0=" + y0 + " yMax=" + yMax);
                if (x0 <= SCROLL_MARGIN && xMax >= canvasWidth - SCROLL_MARGIN) { // screen is inside the board
                    xOffset = newXOffset;
                } else if (x0 >= SCROLL_MARGIN && xMax <= canvasWidth - SCROLL_MARGIN) { // board is inside the screen
                    xOffset = newXOffset;
                } else if (x0 >= SCROLL_MARGIN && distanceX >= 0) {
                    xOffset = newXOffset;
                } else if (xMax <= canvasWidth - SCROLL_MARGIN && distanceX <= 0) {
                    xOffset = newXOffset;
                }
                if (y0 >= -SCROLL_MARGIN && yMax <= -canvasHeight + SCROLL_MARGIN) {
                    yOffset = newYOffset;
                } else if (y0 <= -SCROLL_MARGIN && yMax >= -canvasHeight + SCROLL_MARGIN) {
                    yOffset = newYOffset;
                } else if (y0 <= -SCROLL_MARGIN && distanceY >= 0) {
                    yOffset = newYOffset;
                } else if (yMax >= -canvasHeight + SCROLL_MARGIN && distanceY <= 0) {
                    yOffset = newYOffset;
                }
                invalidate();
            }
            return true;
        }

        @Override
        public boolean onFling(MotionEvent event1, MotionEvent event2,
                               float velocityX, float velocityY) {
            return false;
        }
    }

    void showNet() {
        model.selectFullNet();
    }

    void lockAction() {
        model.selections.lockedSelection = true;
        invalidate();
    }

    public void showOnlineHelp() {
//        DownloadExamples.findFile("Download", new File(Environment.getExternalStorageDirectory().getAbsolutePath()
//                + "/"));
//        DownloadExamples.findFile("downloads", new File(Environment.getExternalStorageDirectory().getAbsolutePath()
//                + "/"));
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse
                ("http://www.logicbreadboard.com/home/andlogics"));
        activity.startActivity(browserIntent);
    }

    void editLabel() {
        AlertDialog.Builder alert = new AlertDialog.Builder(activity);
        final EditText edittext = new EditText(activity);
        edittext.setText(model.selections.getSelectedLabelText());
        // TODO: 8/13/15 disable dictionary hint
        Viewer lastSelected = model.selections.getLastSelected();
        String message = lastSelected.getEditMessage();
        String title = lastSelected.getEditTitle();
        int maxLength = lastSelected.getMaxIdLength();
        alert.setMessage(message);
        alert.setTitle(title);
        edittext.setFilters(new InputFilter[]{new InputFilter.LengthFilter
                (maxLength)});
        alert.setView(edittext);
        alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
//                                //What ever you want to do with the value
//                                editable YouEditTextValue = edittext.getText();
//                                OR
//                                String YouEditTextValue = edittext.getText();
                model.selections.setSelectedLabelText(edittext.getText().toString());
                model.makeSnapshot();
                invalidate();
            }
        });
        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
            }
        });
        alert.show();
    }

    void includeFile() {
        String m_chosenDir = "";
        FileBrowser fileBrowser = new FileBrowser(activity, new FileBrowser.ChosenDirectoryListener() {
            @Override
            public boolean onChosenDir(String chosenDir, boolean overWrite) {
                Toast.makeText(activity, "Chosen FileOpenDialog File: " +
                        chosenDir, Toast.LENGTH_LONG).show();
                model.selections.setSelectedLabelText(chosenDir);
                model.makeSnapshot();
                invalidate();
//                                if (chosenDir.substring((chosenDir.lastIndexOf(".") + 1), chosenDir.length())
//                                        .equals("v")) {
//                                    VerilogParser verilogParser = new VerilogParser();
//                                    verilogParser.run(chosenDir);
//                                    model.selections.setSelectedLabelText(chosenDir);
//                                    model.makeSnapshot();
//                                    invalidate();
//                                } else if (chosenDir.substring((chosenDir.lastIndexOf(".") + 1),
//                                        chosenDir.length())
//                                        .equals("s")){
//                                    model.selections.setSelectedLabelText(chosenDir);
//                                    model.makeSnapshot();
//                                    invalidate();
//                                }
                return true;
            }
        }, null);
        fileBrowser.setNewFolderEnabled(false);
        fileBrowser.chooseDirectory(m_chosenDir);
        model.makeSnapshot();
    }

    void alignHorizontal() {
        String ret = model.selections.alignHorizontal(EditorViewer.this);
        if (ret != null) {
            Toast.makeText(activity, ret, Toast.LENGTH_LONG).show();
        }
        model.makeSnapshot();
    }

    void alignVertical() {
        String ret;
        ret = model.selections.alignVertical(EditorViewer.this);
        if (ret != null) {
            Toast.makeText(activity, ret, Toast.LENGTH_LONG).show();
        }
        model.makeSnapshot();
    }

    void setDelay() {
        model.selections.setDelay((Activity) getContext());
        invalidate();
        model.makeSnapshot();
    }

    void addInput(EditorViewer editorViewer) {
        model.selections.addInput(model, editorViewer);
        invalidate();
        model.makeSnapshot();
    }

    void unlockAction() {
        model.selections.lockedSelection = false;
        invalidate();
    }

    void changeInvert() {
        model.selections.changeInv(model);
        model.makeSnapshot();
    }

    void copyAction() {
        if (!cursor.enabled) {
            CursorPosOnCanvas.x = Math.round(xOrigo - xOffset);
            CursorPosOnCanvas.y = Math.round(yOrigo - yOffset);
            cursor.set(CursorPosOnCanvas.x, CursorPosOnCanvas.y);
        } else {
            centerCursor();
        }
        boolean retVal = model.selections.copy(model, cursor);
        if (!retVal) {
            Toast.makeText(getContext(), "Select destination first", Toast.LENGTH_LONG).show();
        } else {
            invalidate();
        }
        model.makeSnapshot();
    }

    void morphAction() {
        MenuStateMachine.gotoDeviceGroupSelection();
        View vv = findViewById(R.id.schematic_canvas);
        DeviceSelectionMenu.displayDeviceMenu(activity, vv);
        model.makeSnapshot();
    }

    public void rotateRight() {
        model.selections.rotateSelected(2);
        if (!model.selections.lockedSelection || model.selections
                .lockedSelectionDisabled) { //try reconnect
            model.selections.reconnect(model);
        } else {
            tryReroute();
        }
        model.makeSnapshot();
    }

    public void rotateLeft() {
        model.selections.rotateSelected(-2);
        if (!model.selections.lockedSelection || model.selections
                .lockedSelectionDisabled) { //try reconnect
            model.selections.reconnect(model);
        } else {
            tryReroute();
        }
        model.makeSnapshot();
    }

    public void fineRotateLeft() {
        model.selections.rotateSelected(-2);
        if (!model.selections.lockedSelection || model.selections
                .lockedSelectionDisabled) { //try reconnect
            model.selections.reconnect(model);
        } else {
            tryReroute();
        }
        model.makeSnapshot();
    }

    public void deleteAction() {
        model.selections.deleteSelected(model, this);
        model.checkConnections();
        model.makeSnapshot();
    }

    private class ScaleListener
            extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            float nf = detector.getScaleFactor();
            scaleFactor *= nf;
            scaleFactor = (float) Math.max(MIN_ZOOM, Math.min(scaleFactor, MAX_ZOOM));
            Android511workaround();
            setOffsetsToScale();
            invalidate();
            return true;
        }
    }

    public void propagateNetNumberAt(int x, int y, int netNumber) {
    }

    void dispatchScopeDraw(Canvas canvas) {
        canvas.scale(scaleFactor, scaleFactor, 0, 0);
        canvas.translate(xOffset, yOffset);
        float x = -xOffset;
        float y = -2 * yOffset;
//        Log.i("SCOPE", "y=" + y);
        // TODO: 5/7/16  limit here or in showScope or center from menu
        canvas.save();
        Rect clipBounds = canvas.getClipBounds();
        final float textLength = Viewer.Context.scopeTextPaint.measureText("WWWWWW");
        clipBounds.left = Math.round(x + textLength);
        clipBounds.top = Math.round(ProbeViewer.CHANNEL_HEIGHT - yOffset);
        canvas.clipRect(clipBounds);
        canvas.save();
        canvas.translate(-xOffset, 0);
        model.showScope(canvas, Math.round(textLength), Math.round(x), canvasWidth, scaleFactor, scopeTimeZoom, y);
        canvas.restore();
        canvas.restore();
        model.showScopeChannelNames(canvas, x, y);
        canvas.save();
        canvas.translate(-xOffset, 0);
        model.showScopeTime(canvas, Math.round(textLength), Math.round(x), canvasWidth, scaleFactor, scopeTimeZoom, y, Math.round(-yOffset));
        canvas.restore();
    }

    void scrollScope(float distanceX, float distanceY) {
        xOffset += distanceX / (scaleFactor * scopeTimeZoom);
        if (xOffset < 0) {
            xOffset = 0;
        } else if (xOffset > Event.simulationTime) {
            xOffset = Event.simulationTime;
        }
        float newYOffset = distanceY / scaleFactor;
        int y0 = -Math.round(((0 - yOrigo) / scaleFactor + yOrigo - newYOffset) * scaleFactor);
        int numberOfChannels = model.getNumberOfChannels();
        int yMax = Math.round(BBProbeViewer.CHANNEL_HEIGHT * numberOfChannels / scaleFactor);
//        Log.i("onScroll", "y0=" + y0 + " yMax=" + yMax + " newYOffset=" + newYOffset + " canvasHeight=" + canvasHeight);
        if (yOffset + newYOffset > yMax * scaleFactor) {
            yOffset = yMax * scaleFactor;
        } else {
            yOffset += newYOffset;
        }
        if (yOffset < 0) {
            yOffset = 0;
        }
        invalidate();
    }

}

