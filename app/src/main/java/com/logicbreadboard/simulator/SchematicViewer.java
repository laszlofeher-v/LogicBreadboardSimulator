package com.logicbreadboard.simulator;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.AsyncTask;
import android.os.Environment;
import androidx.core.view.GestureDetectorCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;

/**
 * Created on 12/18/14.
 */
public class SchematicViewer extends EditorViewer {
    private static final int RASTER_SIZE = 5;
    static Paint paint = new Paint();

    static {
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(Color.BLACK);
        paint.setStrokeWidth(20);
    }

    SimulationTask simulationTask;
    private float savedScaleFactor = 1;
    private float savedXOffset;
    private float savedYOfseet;
    private boolean firstScopeShow = true;
    private float savedScopeScaleFactor;
    private float savedScopeXOffset;
    private float savedScopeYOfseet;
    private int maxSimTime;
    private TextView timeView;

    public SchematicViewer(Context context) {
        super(context);
    }

    public SchematicViewer(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SchematicViewer(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void add(Viewer v) {
        model.selections.unselect();
        if (!cursor.enabled) {
            CursorPosOnCanvas.x = Math.round(xOrigo - xOffset);
            CursorPosOnCanvas.y = Math.round(yOrigo - yOffset);
            cursor.set(CursorPosOnCanvas.x, CursorPosOnCanvas.y);
        } else {
            centerCursor();
        }
        model.add(v);
//        Log.i("add", "x=" + v.getLeft() + " y=" + v.getBottom());
        model.makeSnapshot();
        model.forcedSelect(v);
        invalidate();
    }

    synchronized void startSimulation() {
        simulationTask = new SimulationTask();
        simulationTask.execute(model);
    }

    public void gotoWeb() {
        MenuStateMachine.gotoWeb();
    }

    public View getCanvas() {
        View v = findViewById(R.id.schematic_canvas);
        return v;
    }

    @Override
    public void init(final MainActivity activity, EditorModel editorModel) {
        super.init(activity, editorModel);
        timeView = (TextView) activity.findViewById(R.id.simulationTime);
        model = editorModel;
        cursor = new Cursor(canvasWidth / 2, canvasHeight / 2);
//        ContextMenuIcons.help = false;
    }

    @Override
    public void scopeButtonHandler() {
        if (!model.enableScope) {
            model.enableScope = true;
            savedScaleFactor = scaleFactor;
            savedXOffset = xOffset;
            savedYOfseet = yOffset;
            if (firstScopeShow) {
                firstScopeShow = false;
                xOffset = 0;
                yOffset = 0;
                scaleFactor = 1;
            } else {
                xOffset = savedScopeXOffset;
                yOffset = savedScopeYOfseet;
                scaleFactor = savedScopeScaleFactor;
            }
        }
        invalidate();
    }

    public void runButtonHandler() {
        if (!model.simulationIsRunning) {
            cursor.enabled = false;
            moveSelectionEnabled = false;
//            commandButtonsHandler.buttons[1].setImageResource(R.drawable
//                    .ic_stop_black_24dp);
            model.selections.unselect();
            model.faultViewers.clear();
            maxSimTime = model.getMaxSimTime();
//                            model.faultViewerLinkedList.clear();
//                            ShortCircuitFault.faultViewerQueue.clear();
            model.simulationIsRunning = true;
            DeviceModel.destroyRegistrationList();
            Event.clear();
//            commandButtonsHandler.timeView.setText(Long.toString(Event.simulationTime, 10));
            // todo put compressNetNumbers to load
            model.compressNetNumbers();
            scaleDetector = new
                    ScaleGestureDetector(activity, new ScaleListener());
            gestureListener = new GestureDetectorCompat(activity, new
                    SimulationGestureListener());
            model.destroyLogicalConnections();
            model.makeLogicalConnections();
            model.reset(); // Sets undefined and puts first events to the queue.
            invalidate();
            startSimulation();
        }
    }

    public void runFromScopeButtonHandler() {
        cursor.enabled = false;
        moveSelectionEnabled = false;
//            commandButtonsHandler.buttons[1].setImageResource(R.drawable
//                    .ic_stop_black_24dp);
        model.selections.unselect();
        model.faultViewers.clear();
        maxSimTime = model.getMaxSimTime();
//                            model.faultViewerLinkedList.clear();
//                            ShortCircuitFault.faultViewerQueue.clear();
        model.simulationIsRunning = true;
        DeviceModel.destroyRegistrationList();
        Event.clear();
//            commandButtonsHandler.timeView.setText(Long.toString(Event.simulationTime, 10));
        // todo put compressNetNumbers to load
        model.compressNetNumbers();
        scaleDetector = new
                ScaleGestureDetector(activity, new ScaleListener());
        gestureListener = new GestureDetectorCompat(activity, new
                SimulationGestureListener());
        model.destroyLogicalConnections();
        model.makeLogicalConnections();
        model.reset(); // Sets undefined and puts first events to the queue.
        invalidate();
        startSimulation();
    }

    public void stopSimulation() {
        if (model != null) {
            if (model.simulationIsRunning && simulationTask != null) {
                simulationTask.cancel(true);
            }
//            commandButtonsHandler.buttons[1].setImageResource(R.drawable.ic_play_arrow_black_24dp);
            model.simulationIsRunning = false;
            gestureListener = new GestureDetectorCompat(activity, new
                    EditorGestureListener());
            invalidate();
        }
    }

    public void restoreAfterScope() {
//        commandButtonsHandler.buttons[0].setVisibility(View.VISIBLE);
//        commandButtonsHandler.buttons[4].setVisibility(View.GONE);
//        commandButtonsHandler.buttons[3].setVisibility(View.VISIBLE);
//        commandButtonsHandler.buttons[5].setVisibility(View.GONE);
//        commandButtonsHandler.buttons[2].setVisibility(View.VISIBLE);
//        commandButtonsHandler.buttons[6].setVisibility(View.GONE);
        model.enableScope = false;
        savedScopeScaleFactor = scaleFactor;
        savedScopeXOffset = xOffset;
        savedScopeYOfseet = yOffset;
        scaleFactor = savedScaleFactor;
        xOffset = savedXOffset;
        yOffset = savedYOfseet;
        invalidate();
    }

    @Override
    public void setScopeTimeZoom(float z) {
        //todo preserve start display time
        float zz = scopeTimeZoom;
        zz *= z;
        if ((zz >= 1f / 100f) && (zz <= 100f)) {
            scopeTimeZoom = zz;
        }
    }

    public void save(String projectFileName) {
        String TAG = "schematic".toString();
//        Log.i(TAG, "copy test files " + projectFileName);
        try {
            Log.i(TAG, projectFileName);
            FilenameViewer filenameViewer = new FilenameViewer();
            model.add(filenameViewer);
            ZoomPosViewer zoomPos = new ZoomPosViewer();
            zoomPos.zoom = scaleFactor;
            zoomPos.xOffset = xOffset;
            zoomPos.yOffset = yOffset;
            model.add(zoomPos);
            NativeSaveLoad.saveSchematic(projectFileName, model);
            model.remove(zoomPos);
            model.remove(filenameViewer);
        } catch (Exception ex) {
//            System.out.println("?????");
            ex.printStackTrace();
        }
    }

    @Override
    public void restore(String projectFileName) {
        try {
            File oldFile = new File(projectFileName);
            if (!oldFile.exists()) {
                return;
            }
            model = new EditorModel();
            NativeSaveLoad.loadSchematicMain(projectFileName, this.model, this);
            model.checkConnections();
            invalidate();
            model.makeSnapshot();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void pushButton(MotionEvent ev, int offset) {
        setCursorPosOnCanvas(ev);
        if (model.pushButton(CursorPosOnCanvas.x, CursorPosOnCanvas.y)) {
            invalidate();
//            System.out.println("queue size=" + Event.queue.size());
//            System.out.flush();
            if (Event.queue.size() == 0) {
//                System.out.println("New task started");
//                System.out.flush();
                //           Event.simulationTime++;
                Event.loadButtonEvents();
                startSimulation();
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        int size = ev.getPointerCount();
        int maskedAction = ev.getActionMasked();
        invalidate();
        if (size > 1) {
            if (scaleDetector.onTouchEvent(ev) == true) {
//                System.out.println("not passed  ");
                return true;
            } else if (size == 3) {
                model.selections.rotateSelected(4);
            }
        } else {
            if (maskedAction == MotionEvent.ACTION_DOWN ||
                    maskedAction == MotionEvent.ACTION_POINTER_DOWN) {
                setTouchPosOnCanvas(ev);
                fastSelectionDisabled = false;
            }

            if (gestureListener.onTouchEvent(ev) == true) {
//                System.out.println("not passed  ");
                return true;
            }
        }
//        System.out.println("+++++++++++passed  " + (cursor.left - 1) + "," + (cursor.right + 1) + "   ");
//        System.out.println(cursor.top - 1 + "," + cursor.bottom + 1);
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
                if (!model.simulationIsRunning) {
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
//                System.out.println("ACTION_UP");
//                if (moved) {
//                    model.reconnect();
//                }
//                moved = false;
                    moveCursorEnabled = false;
                    clearMovingSelection();
                    moveXOffset = 0;
                    moveYOffset = 0;
                    cursor.normalCursor();
                    ((MainActivity) activity).setContextMenuVisibility();
                    invalidate();
                } else {
//                System.out.println("ACTION_UP");
                    setCursorPosOnCanvas(ev);
                    if (model.releaseButton(CursorPosOnCanvas.x, CursorPosOnCanvas.y)) {
                        invalidate();
                        if (Event.queue.size() == 0) {
                            Event.loadButtonEvents();
                            startSimulation();
                        }
                    }
                }
                break;
            default:
        }
        return true;
    }

    private class SimulationTask extends AsyncTask<EditorModel, Integer, String> {
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
            Event event = Event.queue.poll();
            if (event == null) {
                DeviceModel.handleRegistratedModels();
                event = Event.queue.poll();
            }
            while (event != null && Event.simulationTime < maxSimTime) {
//                    System.out.println("?????:" + event.level + " " + event
//                            .time + " " + Event.simulationTime);
                event.deviceOutputModel.eventHandler(event);
                if (Event.displayEvent) {
                    publishProgress(Event.simulationTime);
                    Event.displayEvent = false;
                }
                event = Event.queue.peek();
                if (event == null || (event.time > Event.simulationTime)) {
                    DeviceModel.handleRegistratedModels();
                }
                event = Event.queue.poll();
            }
            Event.simulationTime++;
            publishProgress(Event.simulationTime);
            return "this string is passed to onPostExecute";
        }

        // This is called from background thread but runs in UI
        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            //todo display simulation time
            //todo run stop step
            if (values[0] < maxSimTime) {
                timeView.setText(Long.toString(values[0], 10));
            } else {
                timeView.setText("End:" + Long.toString(values[0], 10));
            }
//            Iterator<FaultViewer> iterator = ShortCircuitFault.faultViewerQueue.iterator();
//            while ((iterator.hasNext())) {
//                FaultViewer fv = iterator.next();
//                model.append(fv);
//            }
            model.setFaultPos();
            model.displayWireLevel();
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
//            Log.i("onPostExecute", "queue size=" + Event.changedNetsAndLevels.size());
            invalidate();
        }
    }

    private class SimulationGestureListener
            extends GestureDetector.SimpleOnGestureListener {

        protected void setOffsetsToScale() {
            int x0 = Math.round((0 - xOrigo) / scaleFactor + xOrigo - xOffset);
            int y0 = Math.round((0 - yOrigo) / scaleFactor + yOrigo - yOffset);
            int xMax = Math.round((canvasWidth - xOrigo) / scaleFactor + xOrigo - xOffset);
            int yMax = Math.round((canvasHeight - yOrigo) / scaleFactor + yOrigo - yOffset);
            if (!(xMax >= 10)) {
                xOffset = (canvasWidth - xOrigo) / scaleFactor + xOrigo - 10;
            }
            if (!(x0 <= EditorModel.MAX_X - 10)) {
                xOffset = (-xOrigo) / scaleFactor + xOrigo - (EditorModel.MAX_X - 10);
            }
            if (!(yMax >= 10)) {
                yOffset = (canvasHeight - yOrigo) / scaleFactor + yOrigo - 10;
            }
            if (!(y0 <= EditorModel.MAX_Y - 10)) {
                yOffset = (-yOrigo) / scaleFactor + yOrigo - (EditorModel.MAX_Y - 10);
            }
        }

        @Override
        public boolean onDown(MotionEvent ev) {
//            setCursorPosOnCanvas(ev);
//            System.out.println("onDown");
            setTouchPosOnCanvas(ev);
            invalidate();
            pushButton(ev, 0);
//            Viewer v = model.selections.findInSelectedList(model, CursorPosOnCanvas.x,
//                    CursorPosOnCanvas.y);
//            if (v != null) {
//                moveSelectionEnabled = true;
//            } else {
//                moveSelectionEnabled = false;
//            }
            boolean res = true;
//            System.out.println("onDown");
            return res;
        }

        @Override
        public void onShowPress(MotionEvent motionEvent) {
            Log.i("SCH", "onShowPress");
        }

        public boolean onSingleTapConfirmed(MotionEvent ev) {
//            System.out.println("onSingleTapConfirmed");
//            pushButton(ev, 0);
            return true;
        }

        @Override
        public boolean onSingleTapUp(MotionEvent ev) {
//            System.out.println("onSingleTapUp");
            cursor.touch = false;
            invalidate();
            if (model.releaseButton(CursorPosOnCanvas.x, CursorPosOnCanvas.y)) {
                invalidate();
                if (Event.queue.size() == 0) {
                    Event.loadButtonEvents();
                    startSimulation();
                }
            }
            ((MainActivity) activity).setContextMenuVisibility();
            return true;
        }

        @Override
        public void onLongPress(MotionEvent ev) {
            Log.i("SCH", "long press");
//            xDown = ev.getX();
//            yDown = ev.getY();
//            moveCursorEnabled = true;
//            cursor.redCursor(model.router);
        }

        public boolean onDoubleTapEvent(MotionEvent ev) {
//            System.out.println("+++++++++++onDoubleTapEvent");
            return true;
        }

//        private void moveSelected(float dx, float dy) {
//            if (moveSelectionEnabled) {
//                model.selections.move(dx, dy);
////                if (lastSelection != null) {
//                invalidate();
//            }
//        }

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
                    model.selections.move(SchematicViewer.this, fromX, fromY, ix, iy);
                    if (!model.selections.lockedSelection || model.selections
                            .lockedSelectionDisabled) { //try reconnect
                        model.selections.reconnect(model);
                    }
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
//            System.out.println("onFling");
            return true;
        }
    }

    private class ScaleListener
            extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            float nf = detector.
                    getScaleFactor();
            scaleFactor *= nf;
            scaleFactor = (float) Math.max(MIN_ZOOM, Math.min(scaleFactor, MAX_ZOOM));
            Android511workaround();
            setOffsetsToScale();
            invalidate();
            return true;
        }
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        if (model == null) {
            return;
        }
        if (model.enableScope) {
            dispatchScopeDraw(canvas);
        } else {
            DIPViewer.rasterSize = RASTER_SIZE;
            xOrigo = canvasWidth / 2;
            yOrigo = canvasHeight / 2;
            canvas.translate(xOffset, yOffset);
            canvas.scale(scaleFactor, scaleFactor, xOrigo - xOffset, yOrigo - yOffset);
            canvas.getClipBounds(clipBounds);
            Viewer.Context.borderPaint.setStrokeWidth(2 / scaleFactor);
//            Viewer.Context.borderPaint.setStyle(Paint.Style.FILL);
//            canvas.clipRect(EditorModel.MIN_X, EditorModel.MIN_Y, EditorModel
//                    .MAX_X, EditorModel.MAX_Y, Region.Op.DIFFERENCE);
//            canvas.drawARGB(255, 0, 50, 0);
//            //restore full canvas clip for any subsequent operations
//            canvas.clipRect(new Rect(0, 0, canvas.getWidth(), canvas.getHeight())
//                    , Region.Op.REPLACE);
//            canvas.drawRect(EditorModel.MIN_X, EditorModel.MIN_Y, EditorModel
//                    .MAX_X, EditorModel.MAX_Y, Viewer.Context.borderPaint);
            canvas.drawLine(EditorModel.MIN_X, EditorModel.MIN_Y, EditorModel.MIN_X, EditorModel.MAX_Y, Viewer.Context.borderPaint);
            canvas.drawLine(EditorModel.MIN_X, EditorModel.MIN_Y, EditorModel.MAX_X, EditorModel.MIN_Y, Viewer.Context.borderPaint);
            canvas.drawLine(EditorModel.MAX_X, EditorModel.MAX_Y, EditorModel.MIN_X, EditorModel.MAX_Y, Viewer.Context.borderPaint);
            canvas.drawLine(EditorModel.MAX_X, EditorModel.MAX_Y, EditorModel.MAX_X, EditorModel.MIN_Y, Viewer.Context.borderPaint);
//            ContextMenuIcons.left = -xOffset + (xOrigo) - (xOrigo) / scaleFactor;
//            ContextMenuIcons.top = -yOffset + (yOrigo) - (yOrigo) / scaleFactor;
            if (model != null) {
                model.drawAll(canvas, canvasWidth, canvasHeight, scaleFactor, xOffset, yOffset);
                cursor.draw(canvas, scaleFactor);
                ((MainActivity) activity).setContextMenuVisibility();
            }
        }
    }

}
