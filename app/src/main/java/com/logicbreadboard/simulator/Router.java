package com.logicbreadboard.simulator;

/**
 * Created by laci on 5/24/15.
 */

import android.util.Log;

public class Router {
    public native void setCMask(int x, int y, byte value);

    public native void andCMask(int x, int y, byte value);

    public native void initCRouter(float minX, float minY, float maxX, float maxY, boolean diagonalEnabled);

    public native void addCPointMask(int x0, int y0, int r);

    public native int findCRoute();

    public native void addCQueue(short x, short y, short distance, byte direction /*boolean onWire*/);

    public native int initCDirection(short x, short y);

    public native int findCDirection(int prevDirection);

    public native short getCxFromFindDirection();

    public native short getCyFromFindDirection();

    public native int getCLastDistance();

    enum Direction {
        NULL, UP, UP_RIGHT, RIGHT, DOWN_RIGHT, DOWN, DOWN_LEFT, LEFT, ERROR, UP_LEFT
    }

    public static final float MAX_CONNECTION_DISTANCE = 8;
    static final byte TARGET_PIXEL = (byte) 0xf0;
    static final byte ALL_DISABLED = 0x00;
    static final byte VERTICAL_ALLOWED = 0x1;
    static final byte HORIZONTAL_ALLOWED = 0x2;
    static final byte DOWN_ALLOWED = 0x4;
    static final byte UP_ALLOWED = (byte) 0x8;
    static final short DEFAULT_RASTER_PIXEL = Short.MAX_VALUE;
    static byte allEnabled =
            VERTICAL_ALLOWED | VERTICAL_ALLOWED |
                    DOWN_ALLOWED | UP_ALLOWED;
    //    static short[][] raster;
//    static byte[][] mask;
//    static boolean[][] inQueue;
    private final RasterRect rasterRect;
    short rasterWidth;
    short rasterHeigth;
    short rasterXOffset;
    short rasterYOffset;
    //    short[] queueX;
//    int queueIn;
//    int queueOut;
//    short[] queueY;
    short xSource;
    short ySource;
    short bestTargetX;
    short bestTargetY;
    int count;
    int startNet;
    int targetNet;
    private boolean route45Enabled;
    private short minDistance;
    private short xFromFindDirection;
    private short yFromFindDirection;

    public Router() {
        //Todo try, background
        rasterRect = new RasterRect();
    }

//    void addQueue(short x, short y, short distance, byte direction /*boolean onWire*/) {
//        if (x < 0 || y < 0 || x >= rasterWidth || y >= rasterHeigth) {
//            //           System.out.println("Out " + x + " " + y + " " + distance);
//            return;
//        }
//        short foundDistance = raster[x][y];
//        byte pixelMask = mask[x][y];
//        if ((pixelMask == ALL_DISABLED)) {
////            System.out.println("(pixelMask & ALL_DISABLED) != 0 at:  " + x + " " + y);
//            return;
//
//        } else if (pixelMask == allEnabled && foundDistance > distance) {
////            System.out.println("foundDistance " + x + " " + y + " " + foundDistance + " " + distance);
//            raster[x][y] = distance;
//        } else if ((direction & pixelMask) != 0) {
//            final short crossPenalty;
//            crossPenalty = 10;
//            if (foundDistance > distance + crossPenalty) {
//                raster[x][y] = (short) (distance + crossPenalty);
//            } else {
////                System.out.println("foundDistance > distance + crossPenalty at:  " + x + " " + y);
//                return;
//            }
//        } else if (pixelMask == TARGET_PIXEL) {
//            if (foundDistance > distance) {
//                if (minDistance > distance) {
////                    System.out.println("FOUND TARGET " + distance + " < " + minDistance);
//                    minDistance = distance;
//                    bestTargetX = x;
//                    bestTargetY = y;
//                    System.out.println("Best set " + bestTargetX + " " + bestTargetY);
//                }
//                raster[x][y] = distance;
//            }
////            return;
//        } else {
////            if (pixelMask != 0) {
////                System.out.println("pixelMask " + pixelMask + " direction " + direction);
////                System.out.flush();
////            }
//            if (pixelMask == allEnabled) {
//                final int crossPenalty;
//                crossPenalty = 10;
//                if (foundDistance > distance + crossPenalty) {
//                    raster[x][y] = (short) (distance + crossPenalty);
//                } else {
////                    System.out.println("2. foundDistance > distance + crossPenalty at:  " + x + " " +
////                            y);
//                    return;
//                }
//            } else {
//                return;
//            }
//        }
//        if (inQueue[x][y]) {
//            //           System.out.println("INQUEUE " + x + " " + y);
//            return;
//        }
//        inQueue[x][y] = true;
//        queueX[queueIn] = x;
//        queueY[queueIn] = y;
//        queueIn = (queueIn + 1) % queueX.length;
//        if (queueIn == queueOut) {
////            System.out.println("LEE ERROR 1");
//            return;
//        }
//    }

//    private boolean getQueue() {
//        if (queueIn == queueOut) {
////            System.out.println("getQueue end " + count);
//            return false;
//        }
//        count++;
//        xSource = queueX[queueOut];
//        ySource = queueY[queueOut];
//        inQueue[xSource][ySource] = false;
////        System.out.println("INQUEUE " + xSource + " " + ySource + " " + queueOut + " " + queueIn);
//        queueOut = (queueOut + 1) % queueX.length;
//        return true;
//    }

//    private short wave() {
////        private void wave(int x, int y, int xEnd, int yEnd) {
//        short distance = 0;
//        minDistance = DEFAULT_RASTER_PIXEL;
////        System.out.println("CALLED");
//        count = 0;
//        while (getQueue()) {
//            distance = raster[xSource][ySource];
//            if (distance == 0) {
//                System.out.println("distance == 0 at:  " + xSource + " " + ySource + " :  "
//                        + mask[xSource][ySource]);
//                if (mask[xSource][ySource] == TARGET_PIXEL) {
//                    System.out.println("TARGET_PIXEL at:  " + xSource + " " + ySource);
//                    break;
//                }
//            }
//            //           boolean onWire = mask[xSource][ySource] == WIRE_PIXEL;
//            short newDistance = (short) (distance + 10);
//            if (newDistance <= minDistance) {
//                addQueue((short) (xSource - 1), ySource, newDistance, HORIZONTAL_ALLOWED);
//                addQueue((short) (xSource + 1), ySource, newDistance, HORIZONTAL_ALLOWED);
//                addQueue((short) (xSource), (short) (ySource - 1), newDistance,
//                        VERTICAL_ALLOWED);
//                addQueue((short) (xSource), (short) (ySource + 1), newDistance,
//                        VERTICAL_ALLOWED);
//                if (route45Enabled) {
//                    newDistance = (short) (distance + 14);
//                    //                   System.out.println(xSource + ", " + ySource);
//                    if (xSource > 0 && ySource > 0 && (mask[xSource][ySource - 1] != ALL_DISABLED)
//                            && mask[xSource - 1][ySource] != ALL_DISABLED) {
//                        addQueue((short) (xSource - 1), (short) (ySource - 1), newDistance,
//                                UP_ALLOWED);
//                    }
//                    if (xSource < rasterWidth - 1 && ySource < rasterHeigth - 1 &&
//                            (mask[xSource][ySource + 1] != ALL_DISABLED) && (mask[xSource +
//                            1][ySource] != ALL_DISABLED)) {
//                        addQueue((short) (xSource + 1), (short) (ySource + 1), newDistance,
//                                UP_ALLOWED);
//                    }
//                    if (xSource > 0 && ySource < rasterHeigth - 1 && (mask[xSource][ySource + 1]
//                            != ALL_DISABLED) && (mask[xSource - 1][ySource] != ALL_DISABLED)
//                            ) {
//                        addQueue((short) (xSource - 1), (short) (ySource + 1), newDistance,
//                                DOWN_ALLOWED);
//                    }
//                    if (ySource > 0 && xSource < rasterWidth - 1 && (mask[xSource][ySource - 1] !=
//                            ALL_DISABLED) && (mask[xSource + 1][ySource] != ALL_DISABLED)) {
//                        addQueue((short) (xSource + 1), (short) (ySource - 1), newDistance,
//                                DOWN_ALLOWED);
//                    }
//                }
//            } else {
////                System.out.println("Drop " + newDistance + " > " + minDistance);
//            }
//        }
//        return distance;
//    }

    //    public void findRoute(Viewer.Selection selStart, Viewer.Selection selTarget) {
    public WirePoint findRoute(int[] distance) {
        //todo check if endpoints are really selected, not cleared
//        float x0 = selStartX + rasterXOffset;
//        float x1 = selTargetX + rasterXOffset;
//        float y0 = selStartY + rasterYOffset;
//        float y1 = selTargetY + rasterYOffset;
//        int xStart = Math.round(x0 / PIXEL_RASTER_RATIO);
//        int yStart = Math.round(y0 / PIXEL_RASTER_RATIO);
//        int xEnd = Math.round(x1 / PIXEL_RASTER_RATIO);
//        int yEnd = Math.round(y1 / PIXEL_RASTER_RATIO);
        int wireYX = findCRoute();
        distance[0] = getCLastDistance();
//        Log.i("CROUTE", "wireX=" + (wireYX & 0xffff) + " wireY=" + (wireYX >> 16));
//        distance[0] = wave();
//        System.out.println("Orig " + selTargetX + " " + selTargetY);
        if (wireYX == 0xffffffff) {
//            System.out.println("return null 1 ");
            return null;
        } else if (distance[0] == 0) { // zero queue
            xSource = (short) (wireYX & 0xffff);
            ySource = (short) ((wireYX >> 16) & 0xffff);
            short selTargetX = (short) (xSource - rasterXOffset);
            short selTargetY = (short) (ySource - rasterYOffset);
            WirePoint point = new WirePoint(selTargetX, selTargetY);
//            System.out.println("zero queue " + selTargetX + " " + selTargetY);
            return point;
        }
        bestTargetX = (short) (wireYX & 0xffff);
        bestTargetY = (short) ((wireYX >> 16) & 0xffff);
//        if (mask[bestTargetX][bestTargetY] == TARGET_PIXEL) {
//            System.out.println("Best " + bestTargetX + " " + bestTargetY);
        short selTargetX = (short) (bestTargetX - rasterXOffset);
        short selTargetY = (short) (bestTargetY - rasterYOffset);
        WirePoint point = new WirePoint(selTargetX, selTargetY);
        //       System.out.println("Best " + selTargetX + " " + selTargetY);
        return point;
//        } else {
////            System.out.println("return null 2 ");
//            return null;
//        }
    }

    //    private Direction findDirection(short x, short y, Direction prevDirection) {
//        short distance = 0;
//        short newDistance = 0;
//        short leftDirectionDistance = Short.MAX_VALUE;
//        short rightDirectionDistance = Short.MAX_VALUE;
//        short upDirectionDistance = Short.MAX_VALUE;
//        short downDirectionDistance = Short.MAX_VALUE;
//        short upLeftDirectionDistance = Short.MAX_VALUE;
//        short upRightDirectionDistance = Short.MAX_VALUE;
//        short downLeftDirectionDistance = Short.MAX_VALUE;
//        short downRightDirectionDistance = Short.MAX_VALUE;
//        Direction dir = Direction.NULL;
//        short newX = x;
//        short newY = y;
//        distance = raster[x][y];
//        byte maskPixel = mask[x][y];
////        maskPixel = (byte) (maskPixel & ~TARGET_PIXEL);
//        if (((maskPixel & (HORIZONTAL_ALLOWED | TARGET_PIXEL)) != 0)) {
//            if (x > 0) {
//                newX = (short) (x - 1);
//                leftDirectionDistance = newDistance = raster[newX][newY];
//                if (newDistance >= 0 && newDistance < distance) {
//                    distance = newDistance;
//                    xFromFindDirection = newX;
//                    yFromFindDirection = newY;
//                    dir = Direction.LEFT;
//                }
//            }
//            newX = x;
//            newY = y;
//            if (x < rasterWidth - 1) {
//                newX = (short) (x + 1);
//                rightDirectionDistance = newDistance = raster[newX][newY];
//                if (newDistance >= 0 && newDistance < distance) {
//                    distance = newDistance;
//                    xFromFindDirection = newX;
//                    yFromFindDirection = newY;
//                    dir = Direction.RIGHT;
//                }
//            }
//        }
//        if (((maskPixel & (VERTICAL_ALLOWED | TARGET_PIXEL)) != 0)) {
//            newX = x;
//            newY = y;
//            if (y > 0) {
//                newY = (short) (y - 1);
//                upDirectionDistance = newDistance = raster[newX][newY];
//                if (newDistance >= 0 && newDistance < distance) {
//                    distance = newDistance;
//                    xFromFindDirection = newX;
//                    yFromFindDirection = newY;
//                    dir = Direction.UP;
//                }
//            }
//            newX = x;
//            newY = y;
//            if (y < rasterHeigth - 1) {
//                newY = (short) (y + 1);
//                downDirectionDistance = newDistance = raster[newX][newY];
//                if (newDistance >= 0 && newDistance < distance) {
//                    distance = newDistance;
//                    xFromFindDirection = newX;
//                    yFromFindDirection = newY;
//                    dir = Direction.DOWN;
//                }
//            }
//        }
//        newX = x;
//        newY = y;
//        if (route45Enabled) {
//            if (((maskPixel & (UP_ALLOWED | TARGET_PIXEL)) != 0) && x > 0) {
//                if (y > 0 && x > 0) {
//                    if (y > 0 && x > 0 && (mask[x][y - 1] != ALL_DISABLED) && (mask[x -
//                            1][y] != ALL_DISABLED)) {
//                        newX = (short) (x - 1);
//                        newY = (short) (y - 1);
//                        upLeftDirectionDistance = newDistance = raster[newX][newY];
//                        if (newDistance >= 0 && newDistance < distance) {
//                            distance = newDistance;
//                            xFromFindDirection = newX;
//                            yFromFindDirection = newY;
//                            dir = Direction.UP_LEFT;
//                        }
//                    }
//                }
//            }
//            newX = x;
//            newY = y;
//            if (((maskPixel & (DOWN_ALLOWED | TARGET_PIXEL)) != 0) && x > 0) {
//                if (y < rasterHeigth - 1 && x > 0 && (mask[x][y + 1] != ALL_DISABLED) &&
//                        (mask[x - 1][y] != ALL_DISABLED)) {
//                    newX = (short) (x - 1);
//                    newY = (short) (y + 1);
//                    downLeftDirectionDistance = newDistance = raster[newX][newY];
//                    if (newDistance >= 0 && newDistance < distance) {
//                        distance = newDistance;
//                        xFromFindDirection = newX;
//                        yFromFindDirection = newY;
//                        dir = Direction.DOWN_LEFT;
//                    }
//                }
//            }
//            newX = x;
//            newY = y;
//            if (((maskPixel & (DOWN_ALLOWED | TARGET_PIXEL)) != 0) && x > 0) {
//                if (y > 0 && x < rasterWidth - 1 && (mask[x][y - 1] != ALL_DISABLED) &&
//                        (mask[x + 1][y] != ALL_DISABLED)) {
//                    newX = (short) (x + 1);
//                    newY = (short) (y - 1);
//                    upRightDirectionDistance = newDistance = raster[newX][newY];
//                    if (newDistance >= 0 && newDistance < distance) {
//                        distance = newDistance;
//                        xFromFindDirection = newX;
//                        yFromFindDirection = newY;
//                        dir = Direction.UP_RIGHT;
//                    }
//                }
//            }
//            newX = x;
//            newY = y;
//            if (((maskPixel & (UP_ALLOWED | TARGET_PIXEL)) != 0) && x > 0) {
//                if (y < rasterHeigth - 1 && x < rasterWidth - 1 && (mask[x][y + 1] !=
//                        ALL_DISABLED) && (mask[x + 1][y] != ALL_DISABLED)) {
//                    newX = (short) (x + 1);
//                    newY = (short) (y + 1);
//                    downRightDirectionDistance = newDistance = raster[newX][newY];
//                    if (newDistance >= 0 && newDistance < distance) {
//                        distance = newDistance;
//                        xFromFindDirection = newX;
//                        yFromFindDirection = newY;
//                        dir = Direction.DOWN_RIGHT;
//                    }
//                }
//            }
//        }
////        System.out.println("distance " + distance);
////        System.out.flush();
//        if (dir == Direction.NULL && distance != 0) {
////            System.out.println("?????????????????????????????????????????????????????????? " + distance);
////            System.out.flush();
//            dir = Direction.ERROR;
//        }
//        if (dir != prevDirection && distance != 0) {
//            switch (prevDirection) {
//                case LEFT:
//                    if (leftDirectionDistance == distance) {
////                        System.out.println("dir=" + dir + " prevDirection=" + prevDirection + " " +
////                                "distance=" + distance + " leftDirectionDistance=" + leftDirectionDistance);
//                        dir = prevDirection;
//                        xFromFindDirection = (short) (x - 1);
//                        yFromFindDirection = y;
//                    }
//                    break;
//                case RIGHT:
//                    if (rightDirectionDistance == distance) {
////                        System.out.println("dir=" + dir + " prevDirection=" + prevDirection + " " +
////                                "distance=" + distance + " rightDirectionDistance=" + rightDirectionDistance);
//                        dir = prevDirection;
//                        xFromFindDirection = (short) (x + 1);
//                        yFromFindDirection = y;
//                    }
//                    break;
//                case UP:
//                    if (upDirectionDistance == distance) {
////                        System.out.println("dir=" + dir + " prevDirection=" + prevDirection + " " +
////                                "distance=" + distance + " upDirectionDistance=" + upDirectionDistance);
//                        dir = prevDirection;
//                        xFromFindDirection = x;
//                        yFromFindDirection = (short) (y - 1);
//                    }
//                    break;
//                case DOWN:
//                    if (downDirectionDistance == distance) {
////                        System.out.println("dir=" + dir + " prevDirection=" + prevDirection + " " +
////                                "distance=" + distance + " downDirectionDistance=" + downDirectionDistance);
//                        dir = prevDirection;
//                        xFromFindDirection = x;
//                        yFromFindDirection = (short) (y + 1);
//                    }
//                    break;
//                case UP_LEFT:
//                    if (upLeftDirectionDistance == distance) {
////                        System.out.println("dir=" + dir + " prevDirection=" + prevDirection + " " +
////                                "distance=" + distance + " upLeftDirectionDistance=" + upLeftDirectionDistance);
//                        xFromFindDirection = (short) (x - 1);
//                        yFromFindDirection = (short) (y - 1);
//                        dir = prevDirection;
//                    }
//                    break;
//                case DOWN_LEFT:
//                    if (downLeftDirectionDistance == distance) {
////                        System.out.println("dir=" + dir + " prevDirection=" + prevDirection + " " +
////                                "distance=" + distance + " downLeftDirectionDistance=" + downLeftDirectionDistance);
//                        dir = prevDirection;
//                        xFromFindDirection = (short) (x - 1);
//                        yFromFindDirection = (short) (y + 1);
//                    }
//                    break;
//                case UP_RIGHT:
//                    if (upRightDirectionDistance == distance) {
////                        System.out.println("dir=" + dir + " prevDirection=" + prevDirection + " " +
////                                "distance=" + distance + " upRightDirectionDistance=" + upRightDirectionDistance);
//                        xFromFindDirection = (short) (x + 1);
//                        yFromFindDirection = (short) (y - 1);
//                        dir = prevDirection;
//                    }
//                    break;
//                case DOWN_RIGHT:
//                    if (downRightDirectionDistance == distance) {
////                        System.out.println("dir=" + dir + " prevDirection=" + prevDirection + " " +
////                                "distance=" + distance + " downRightDirectionDistance=" + downRightDirectionDistance);
//                        dir = prevDirection;
//                        xFromFindDirection = (short) (x + 1);
//                        yFromFindDirection = (short) (y + 1);
//                    }
//                    break;
//                case NULL: // start back. try to follow the pin direction
//                    if (maskPixel == TARGET_PIXEL) {
//                        switch (dir) {
//                            case UP:
//                                if (mask[x][y + 1] != ALL_DISABLED) { // possible wrong
//                                    // direction
//                                    if (leftDirectionDistance == upDirectionDistance) {
//                                        xFromFindDirection = (short) (x - 1);
//                                        yFromFindDirection = y;
//                                        dir = Direction.LEFT;
//                                    } else if (rightDirectionDistance == upDirectionDistance) {
//                                        xFromFindDirection = (short) (x + 1);
//                                        yFromFindDirection = y;
//                                        dir = Direction.RIGHT;
//                                    }
//                                }
//                                break;
//                            case DOWN:
//                                if (mask[x][y - 1] != ALL_DISABLED) { // possible wrong
//                                    // direction
//                                    if (leftDirectionDistance == downDirectionDistance) {
//                                        xFromFindDirection = (short) (x - 1);
//                                        yFromFindDirection = y;
//                                        dir = Direction.LEFT;
//                                    } else if (rightDirectionDistance == downDirectionDistance) {
//                                        xFromFindDirection = (short) (x + 1);
//                                        yFromFindDirection = y;
//                                        dir = Direction.RIGHT;
//                                    }
//                                }
//                                break;
//                            case LEFT:
//                                if (mask[x + 1][y] != ALL_DISABLED) { // possible wrong
//                                    // direction
//                                    if (leftDirectionDistance == upDirectionDistance) {
//                                        xFromFindDirection = x;
//                                        yFromFindDirection = (short) (y - 1);
//                                        dir = Direction.UP;
//                                    } else if (leftDirectionDistance == downDirectionDistance) {
//                                        xFromFindDirection = x;
//                                        yFromFindDirection = (short) (y + 1);
//                                        dir = Direction.DOWN;
//                                    }
//                                }
//                                break;
//                            case RIGHT:
//                                if (mask[x - 1][y] != ALL_DISABLED) { // possible wrong
//                                    // direction
//                                    if (rightDirectionDistance == upDirectionDistance) {
//                                        xFromFindDirection = x;
//                                        yFromFindDirection = (short) (y - 1);
//                                        dir = Direction.UP;
//                                    } else if (rightDirectionDistance == downDirectionDistance) {
//                                        xFromFindDirection = x;
//                                        yFromFindDirection = (short) (y + 1);
//                                        dir = Direction.DOWN;
//                                    }
//                                }
//                                break;
//                            default:
//                        }
//                    }
//                    break;
//                default:
//            }
//        }
//        return dir;
//    }
//

    public int getNextPoint(WireViewer wire) {
        short x = (short) Math.round(wire.endWirePoint.x);
        short y = (short) Math.round(wire.endWirePoint.y);
        x = (short) (x + rasterXOffset);
        y = (short) (y + rasterYOffset);
        if (x < 0 || y < 0 || x >= rasterWidth || y >= rasterHeigth) {
//            System.out.println("LEE ERROR 2");
            return -1;
        }
        xFromFindDirection = x;
        yFromFindDirection = y;
        initCDirection(xFromFindDirection, yFromFindDirection);
//        System.out.println(" 0. " + " " + xFromFindDirection + "  " + yFromFindDirection);
        int dirIndex = findCDirection(Direction.NULL.ordinal());
        xFromFindDirection = getCxFromFindDirection();
        yFromFindDirection = getCyFromFindDirection();
//        System.out.println(" 1. " + dirIndex + " " + xFromFindDirection + "  " + yFromFindDirection);
        Direction direction = Direction.values()[dirIndex];
//        Direction direction = findDirection(xFromFindDirection, yFromFindDirection, Direction.NULL);
//        System.out.println("1. directions:" + direction + " " + cDirection);
        if (direction == Direction.ERROR) {
            return -1;
        }
        if (dirIndex == 0) {
            return 0;
        }
        WirePoint point = new WirePoint(xFromFindDirection, yFromFindDirection);
//        System.out.println(xFromFindDirection + "  " + yFromFindDirection);
//        System.out.println(direction + " " + xFromFindDirection + "  " + yFromFindDirection);
        while (true) {
            dirIndex = findCDirection(direction.ordinal());
            Direction nextDirection = Direction.values()[dirIndex];
            xFromFindDirection = getCxFromFindDirection();
            yFromFindDirection = getCyFromFindDirection();
//            System.out.println(direction + " " + xFromFindDirection + "  " + yFromFindDirection);
//            Direction nextDirection = findDirection(xFromFindDirection, yFromFindDirection, direction);
//            System.out.println("2, directions:" + nextDirection + " " + cDirection);
//            Log.i("getNextPoint", "direction=" + direction);
//            Log.i("getNextPoint", "dirIndex=" + dirIndex);
//            Log.i("getNextPoint", "point.x=" + point.x);
//            Log.i("getNextPoint", "point.y=" + point.y);
            if (nextDirection != direction) {
                Log.i("getNextPoint", "break");
                break;
            }
            if (nextDirection == Direction.ERROR) {
                Log.i("getNextPoint", "nextDirection == Direction.ERROR");
                return -1;
            }
            point.x = getCxFromFindDirection();
            point.y = getCyFromFindDirection();
//            point.x = xFromFindDirection;
//            point.y = yFromFindDirection;
        }
//        System.out.println(xFromFindDirection + "  " + yFromFindDirection);
//        System.out.println("next point: " + point.x + " " + point.y);
        point.x -= rasterXOffset;
        point.y -= rasterYOffset;
        wire.addPoint(point);
        return 1;
    }

    public void addRectMask(int x0, int y0, int x1, int y1) {
        x0 = x0 + rasterXOffset;
        x1 = x1 + rasterXOffset;
        y0 = y0 + rasterYOffset;
        y1 = y1 + rasterYOffset;
        rasterizeRect(x0, y0, x1, y1, rasterRect);
        for (int x = rasterRect.xLow; x < rasterRect.xHigh; x++) {
            for (int y = rasterRect.yLow; y < rasterRect.yHigh; y++) {
//                mask[x][y] = ALL_DISABLED;
                setCMask(x, y, ALL_DISABLED);
            }
        }
    }

    protected void addRawPointMask(int x0, int y0, int r) {
        int size = 2 * r;
        addRawRectMask(x0 - r, y0 - r, x0 + size, y0 + size);
    }

    protected void addRawRectMask(int x0, int y0, int x1, int y1) {
        rasterizeRect(x0, y0, x1, y1, rasterRect);
        for (int x = rasterRect.xLow; x < rasterRect.xHigh; x++) {
            for (int y = rasterRect.yLow; y < rasterRect.yHigh; y++) {
//                mask[x][y] = ALL_DISABLED;
                setCMask(x, y, ALL_DISABLED);
            }
        }
    }

    public void addPointMask(int x0, int y0, int r) {
        setMask(x0, y0, ALL_DISABLED);
        setMask(x0 + 1, y0, ALL_DISABLED);
        setMask(x0 - 1, y0, ALL_DISABLED);
        setMask(x0, y0 - 1, ALL_DISABLED);
        setMask(x0, y0 + 1, ALL_DISABLED);
        setMask(x0 + 1, y0 + 1, ALL_DISABLED);
        setMask(x0 + 1, y0 - 1, ALL_DISABLED);
        setMask(x0 - 1, y0 - 1, ALL_DISABLED);
        setMask(x0 - 1, y0 + 1, ALL_DISABLED);
        addCPointMask(x0, y0, r);
    }

    public void setMask(int x0, int y0, byte value) {
        if (x0 > EditorModel.MIN_X && x0 < EditorModel.MAX_X &&
                y0 > EditorModel.MIN_Y && y0 < EditorModel.MAX_Y) {
//            mask[x0][y0] = value;
            setCMask(x0, y0, value);
        }
    }

    public void andMask(int x0, int y0, byte value) {
        if (x0 > EditorModel.MIN_X && x0 < EditorModel.MAX_X &&
                y0 > EditorModel.MIN_Y && y0 < EditorModel.MAX_Y) {
//            mask[x0][y0] &= value;
            andCMask(x0, y0, value);
        }
    }

    public void addLineMask(int x0, int y0, int x1, int y1) {
        int xx0 = x0 + rasterXOffset;
        int xx1 = x1 + rasterXOffset;
        int yy0 = y0 + rasterYOffset;
        int yy1 = y1 + rasterYOffset;
//        int xx0 = x0;
//        int xx1 = x1;
//        int yy0 = y0;
//        int yy1 = y1;
        if (xx0 == xx1) {
            int d;
            if (yy0 < yy1) {
                d = 1;
            } else {
                d = -1;
            }
            yy0 = yy0 - 2 * d;
            yy1 = yy1 + 2 * d;
            int y;
            for (y = yy0; y != yy1; y = y + d) {
                andMask(xx0, y, (byte) (HORIZONTAL_ALLOWED));
                andMask(xx0 - 1, y, (byte) (HORIZONTAL_ALLOWED));
                andMask(xx0 - 2, y, (byte) (HORIZONTAL_ALLOWED));
                andMask(xx0 + 1, y, (byte) (HORIZONTAL_ALLOWED));
                andMask(xx0 + 2, y, (byte) (HORIZONTAL_ALLOWED));
            }
            andMask(xx0, y, (byte) (HORIZONTAL_ALLOWED));
            andMask(xx0 - 1, y, (byte) (HORIZONTAL_ALLOWED));
            andMask(xx0 - 2, y, (byte) (HORIZONTAL_ALLOWED));
            andMask(xx0 + 1, y, (byte) (HORIZONTAL_ALLOWED));
            andMask(xx0 + 2, y, (byte) (HORIZONTAL_ALLOWED));
        } else if (yy0 == yy1) {
            int d;
            if (xx0 < xx1) {
                d = 1;
            } else {
                d = -1;
            }
            xx0 = xx0 - 2 * d;
            xx1 = xx1 + 2 * d;
            int x;
            for (x = xx0; x != xx1; x = x + d) {
                andMask(x, yy0, (byte) (VERTICAL_ALLOWED));
                andMask(x, yy0 - 1, (byte) (VERTICAL_ALLOWED));
                andMask(x, yy0 - 2, (byte) (VERTICAL_ALLOWED));
                andMask(x, yy0 + 1, (byte) (VERTICAL_ALLOWED));
                andMask(x, yy0 + 2, (byte) (VERTICAL_ALLOWED));
            }
            andMask(x, yy0, (byte) (VERTICAL_ALLOWED));
            andMask(x, yy0 - 1, (byte) (VERTICAL_ALLOWED));
            andMask(x, yy0 - 2, (byte) (VERTICAL_ALLOWED));
            andMask(x, yy0 + 1, (byte) (VERTICAL_ALLOWED));
            andMask(x, yy0 + 2, (byte) (VERTICAL_ALLOWED));
        } else {
            int dx;
            if (xx0 < xx1) {
                dx = 1;
            } else {
                dx = -1;
            }
            int dy;
            if (yy0 < yy1) {
                dy = 1;
            } else {
                dy = -1;
            }
            byte allowedDirection;
            if (dx != dy) {
                allowedDirection = UP_ALLOWED;
            } else {
                allowedDirection = DOWN_ALLOWED;
            }
            int x;
            for (x = xx0; x != xx1; x = x + dx) {
                andMask(x, yy0, (byte) (allowedDirection));
                andMask(x - dx, yy0 + dy, (byte) (allowedDirection));
                andMask(x + dx, yy0 - dy, (byte) (allowedDirection));
                andMask(x - dx, yy0, (byte) (allowedDirection));
                andMask(x + dx, yy0, (byte) (allowedDirection));

                andMask(x - 2 * dx, yy0 + 2 * dy, (byte) (allowedDirection));
                andMask(x + 2 * dx, yy0 - 2 * dy, (byte) (allowedDirection));

                andMask(x - dx, yy0 + 2 * dy, (byte) (allowedDirection));
                andMask(x + dx, yy0 - 2 * dy, (byte) (allowedDirection));

                andMask(x - 2 * dx, yy0 + dy, (byte) (allowedDirection));
                andMask(x + 2 * dx, yy0 - dy, (byte) (allowedDirection));

                yy0 = yy0 + dy;
            }
            andMask(x, yy0, (byte) (allowedDirection));
            andMask(x - dx, yy0 + dy, (byte) (allowedDirection));
            andMask(x + dx, yy0 - dy, (byte) (allowedDirection));
            andMask(x - dx, yy0, (byte) (allowedDirection));
            andMask(x + dx, yy0, (byte) (allowedDirection));

            andMask(x - 2 * dx, yy0 + 2 * dy, (byte) (allowedDirection));
            andMask(x + 2 * dx, yy0 - 2 * dy, (byte) (allowedDirection));

            andMask(x - dx, yy0 + 2 * dy, (byte) (allowedDirection));
            andMask(x + dx, yy0 - 2 * dy, (byte) (allowedDirection));

            andMask(x - 2 * dx, yy0 + dy, (byte) (allowedDirection));
            andMask(x + 2 * dx, yy0 - dy, (byte) (allowedDirection));
        }
    }

    public void addLineTarget(int x0, int y0, int x1, int y1) {
        int xx0 = x0 + rasterXOffset;
        int xx1 = x1 + rasterXOffset;
        int yy0 = y0 + rasterYOffset;
        int yy1 = y1 + rasterYOffset;
//        int xx0 = Math.round(x0 / PIXEL_RASTER_RATIO);
//        int xx1 = Math.round(x1 / PIXEL_RASTER_RATIO);
//        int yy0 = Math.round(y0 / PIXEL_RASTER_RATIO);
//        int yy1 = Math.round(y1 / PIXEL_RASTER_RATIO);
        if (xx0 == xx1) {
            int d;
            if (yy0 < yy1) {
                d = 1;
            } else {
                d = -1;
            }
            int y;
            for (y = yy0; y != yy1; y = y + d) {
                setMask(xx0, y, TARGET_PIXEL);
                setCMask(xx0, y, TARGET_PIXEL);
            }
            setMask(xx0, y, TARGET_PIXEL);
            setCMask(xx0, y, TARGET_PIXEL);
        } else if (yy0 == yy1) {
            int d;
            if (xx0 < xx1) {
                d = 1;
            } else {
                d = -1;
            }
            int x;
            for (x = xx0; x != xx1; x = x + d) {
                setMask(x, yy0, TARGET_PIXEL);
                setCMask(x, yy0, TARGET_PIXEL);
            }
            setMask(x, yy0, TARGET_PIXEL);
            setCMask(x, yy0, TARGET_PIXEL);
        } else {
            int dx;
            if (xx0 < xx1) {
                dx = 1;
            } else {
                dx = -1;
            }
            int dy;
            if (yy0 < yy1) {
                dy = 1;
            } else {
                dy = -1;
            }
            int x;
            for (x = xx0; x != xx1; x = x + dx) {
                setMask(x, yy0, TARGET_PIXEL);
                setCMask(x, yy0, TARGET_PIXEL);
                yy0 = yy0 + dy;
            }
            setMask(x, yy0, TARGET_PIXEL);
            setCMask(x, yy0, TARGET_PIXEL);
        }
    }

//    public boolean getSetMask(int x0, int y0) {
//        if (x0 > EditorModel.MIN_X && x0 < EditorModel.MAX_X &&
//                y0 > EditorModel.MIN_Y && y0 < EditorModel.MAX_Y) {
//            if (mask[x0][y0] == TARGET_PIXEL) {
//                return true;
//            }
//            mask[x0][y0] = TARGET_PIXEL;
//            setCMask(x0, y0, TARGET_PIXEL);
//        }
//        return false;
//    }

//    public boolean addPath(int x0, int y0, int x1, int y1) {
//        int xx0 = x0 + rasterXOffset;
//        int xx1 = x1 + rasterXOffset;
//        int yy0 = y0 + rasterYOffset;
//        int yy1 = y1 + rasterYOffset;
////        int xx0 = Math.round(x0 / PIXEL_RASTER_RATIO);
////        int xx1 = Math.round(x1 / PIXEL_RASTER_RATIO);
////        int yy0 = Math.round(y0 / PIXEL_RASTER_RATIO);
////        int yy1 = Math.round(y1 / PIXEL_RASTER_RATIO);
//        if (xx0 == xx1) {
//            int d;
//            if (yy0 < yy1) {
//                d = 1;
//            } else {
//                d = -1;
//            }
//            for (int y = yy0; y != yy1; y = y + d) {
//                if (getSetMask(xx0, y)) {
//                    return true;
//                }
//            }
//        } else if (yy0 == yy1) {
//            int d;
//            if (xx0 < xx1) {
//                d = 1;
//            } else {
//                d = -1;
//            }
//            for (int x = xx0; x != xx1; x = x + d) {
//                if (getSetMask(x, yy0)) {
//                    return true;
//                }
//            }
//        } else {
//            int dx;
//            if (xx0 < xx1) {
//                dx = 1;
//            } else {
//                dx = -1;
//            }
//            int dy;
//            if (yy0 < yy1) {
//                dy = 1;
//            } else {
//                dy = -1;
//            }
//            for (int x = xx0; x != xx1; x = x + dx) {
//                if (getSetMask(x, yy0)) {
//                    return true;
//                }
//                yy0 = yy0 + dy;
//            }
//        }
//        return false;
//    }

    public void addLineStart(short x0, short y0, short x1, short y1) {
        short xx0 = (short) (x0 + rasterXOffset);
        short xx1 = (short) (x1 + rasterXOffset);
        short yy0 = (short) (y0 + rasterYOffset);
        short yy1 = (short) (y1 + rasterYOffset);
//        int xx0 = Math.round(x0 / PIXEL_RASTER_RATIO);
//        int xx1 = Math.round(x1 / PIXEL_RASTER_RATIO);
//        int yy0 = Math.round(y0 / PIXEL_RASTER_RATIO);
//        int yy1 = Math.round(y1 / PIXEL_RASTER_RATIO);
//        System.out.println("startPoint point: " + x0 + "," + y0 + " " + x1 + "," + y1);
        if (xx0 == xx1) {
            short d;
            if (yy0 < yy1) {
                d = 1;
            } else {
                d = -1;
            }
            short y;
            for (y = yy0; y != yy1; y = (short) (y + d)) { // check if crossing
//                if (mask[xx0][y] == TARGET_PIXEL) {
//                    System.out.println("cross point: " + xx0 + " " + y);
////                    return new WirePoint(xx0 - rasterXOffset, y - rasterYOffset);
//                }
//                addQueue(xx0, y, (short) 0, VERTICAL_ALLOWED);
                addCQueue(xx0, y, (short) 0, VERTICAL_ALLOWED);
            }
//            addQueue(xx0, y, (short) 0, VERTICAL_ALLOWED);
            addCQueue(xx0, y, (short) 0, VERTICAL_ALLOWED);
        } else if (yy0 == yy1) {
            int d;
            if (xx0 < xx1) {
                d = 1;
            } else {
                d = -1;
            }
            short x;
            for (x = xx0; x != xx1; x = (short) (x + d)) {
//                if (mask[x][yy0] == TARGET_PIXEL) {
//                    System.out.println("cross point: " + x + " " + yy0);
////                    return new WirePoint(x - rasterXOffset, yy0 - rasterYOffset);
//                }
//                addQueue(x, yy0, (short) 0, HORIZONTAL_ALLOWED);
                addCQueue(x, yy0, (short) 0, HORIZONTAL_ALLOWED);
            }
//            addQueue(x, yy0, (short) 0, HORIZONTAL_ALLOWED);
            addCQueue(x, yy0, (short) 0, HORIZONTAL_ALLOWED);
        } else {
            int dx = 0;
            if (xx0 < xx1) {
                dx = 1;
            } else if (xx0 > xx1) {
                dx = -1;
            }
            int dy = 0;
            if (yy0 < yy1) {
                dy = 1;
            } else if (yy0 > yy1) {
                dy = -1;
            }
            short x;
            for (x = xx0; x != xx1; x = (short) (x + dx)) {
//                if (mask[x][yy0] == TARGET_PIXEL) {
//                    System.out.println("cross point: " + x + " " + yy0);
////                    return new WirePoint(x - rasterXOffset, yy0 - rasterYOffset);
//                }
//                addQueue(x, yy0, (short) 0, allEnabled);
                addCQueue(x, yy0, (short) 0, allEnabled);
                yy0 = (short) (yy0 + dy);
            }
//            addQueue(x, yy0, (short) 0, allEnabled);
            addCQueue(x, yy0, (short) 0, allEnabled);
        }
        //       return null;
    }

    protected void rasterizeRect(int x0, int y0, int x1, int y1, RasterRect r) {
//        r.xLow = Math.max(Math.round((Math.min(x0, x1) / PIXEL_RASTER_RATIO)), 0);
//        r.xHigh = Math.min(Math.round(Math.max(x0, x1) / PIXEL_RASTER_RATIO), rasterWidth - 1);
//        r.yLow = Math.max(Math.round(Math.min(y0, y1) / PIXEL_RASTER_RATIO), 0);
//        r.yHigh = Math.min(Math.round(Math.max(y0, y1) / PIXEL_RASTER_RATIO), rasterHeigth - 1);
        r.xLow = Math.max(Math.min(x0, x1), 0);
        r.xHigh = Math.min(Math.max(x0, x1), rasterWidth - 1);
        r.yLow = Math.max(Math.min(y0, y1), 0);
        r.yHigh = Math.min(Math.max(y0, y1), rasterHeigth - 1);
    }

    public void init(float minX, float minY, float maxX, float maxY) {
//        rasterWidth = (int) (maxX - minX) + 2 * MARGIN;
//        rasterHeigth = (int) (maxY - minY) + 2 * MARGIN;
//        rasterXOffset = -(int) minX + MARGIN;
//        rasterYOffset = -(int) minY + MARGIN;
        initCRouter(minX, minY, maxX, maxY, Settings.enabled45);
        rasterWidth = (short) (maxX - minX);
        rasterHeigth = (short) (maxY - minY);
        rasterXOffset = (short) (-minX);
        rasterYOffset = (short) (-minY);
        this.route45Enabled = Settings.enabled45;
        allEnabled =
                VERTICAL_ALLOWED | HORIZONTAL_ALLOWED;
        if (route45Enabled) {
            allEnabled |= DOWN_ALLOWED | UP_ALLOWED;
        }

//        System.out.println("rw=" + rasterWidth + " rh=" + rasterHeigth + " rxo=" + rasterXOffset + " ryo=" + rasterYOffset);
//        int queueSize = rasterWidth * rasterHeigth;
//        queueX = new short[queueSize];
//        queueY = new short[queueSize];
//        if (inQueue == null) {
//            inQueue = new boolean[rasterWidth][rasterHeigth];
//        }
//        for (int x = 0; x < rasterWidth - 1; x++) {
//            Arrays.fill(inQueue[x], false);
//        }
//        if (raster == null) {
//            raster = new short[rasterWidth][rasterHeigth];
//        }
//        mask = new byte[rasterWidth][rasterHeigth];
//        for (int x = 0; x < rasterWidth - 1; x++) {
//            Arrays.fill(raster[x], DEFAULT_RASTER_PIXEL);
//            Arrays.fill(mask[x], allEnabled);
//        }
//        queueIn = 0;
//        queueOut = 0;
    }

    public String setStartTarget(Selections selection) {
        int numberOfRoutableSelections = selection.getNumberOfRoutableSelections();
//        if (numberOfRoutableSelections > 2) {
//            return new String("Too many selections");
//        } else
        if (numberOfRoutableSelections == 0) {
            return new String("Not enough selection"); // Never called, context menu does not
            // appear
        } else if (numberOfRoutableSelections == 1) {
            return new String("Not enough selection");
//           rerouteNet();
        } else if (numberOfRoutableSelections >= 2) {
            Viewer vStart = selection.getFirstRoutableSelection();
            if (vStart != null) {
                startNet = vStart.getNetNumber();
            } else {
                return new String("Not enough selection");
            }
            Viewer vEnd = selection.getLastRoutableSelection();
            if (vEnd != null) {
                targetNet = vEnd.getNetNumber();
            } else {
                return new String("Not enough selection");
            }
            if (vEnd == vStart) {
                return new String("Not enough selection");
            }
            if (startNet == targetNet) {
                return new String("Already boardConnection");
            } else {
                return null;
            }
        }
        return null;
    }

    class RasterRect {
        int xLow;
        int xHigh;
        int yLow;
        int yHigh;
    }
}