package com.logicbreadboard.simulator;

/**
 * Created on 2/3/16.
 */
public abstract class ViewerFactory {
    public static short META_VIEWER_GROUP = -1;
    public static short ZOOMPOS_VIEWER_SYM = -1;
    public static short FILENAME_VIEWER_SYM = -2;

    static public ViewerFactory factory;
    static public SchematicViewerFactory schematicViewerFactory;
    static public BBViewerFactory bbViewerFactory;

    abstract public Viewer newViewer(int group, int position);

    abstract public int getGroupBox();

    abstract int getGroupDIPSw();

    public abstract Viewer newViewer(byte selectedGroup,String name);
}
