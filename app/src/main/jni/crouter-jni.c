#include <string.h>
#include <jni.h>
#include <android/log.h>

#define  LOG_TAG    "crouter"
#define  ALOG(...)  __android_log_print(ANDROID_LOG_INFO,LOG_TAG,__VA_ARGS__)
//#define  ALOG(...)

void myMethod2() {
    ALOG("This message comes from C at line %d.", __LINE__);
}

#define XSIZE 4096
#define YSIZE 4096
#define QSIZE 8192
#define MAX_CONNECTION_DISTANCE  8
#define TARGET_PIXEL  ((signed char)0xf0)
#define ALL_DISABLED  ((signed char)0x00)
#define VERTICAL_ALLOWED   ((signed char)0x1)
#define HORIZONTAL_ALLOWED  ((signed char)0x2)
#define DOWN_ALLOWED  ((signed char)0x4)
#define UP_ALLOWED ((signed char)0x8)
#define DEFAULT_RASTER_PIXEL 0x7fffffff
static signed char allEnabled;

static int raster[XSIZE][YSIZE];
static signed char mask[XSIZE][YSIZE];
static signed char inQueue[XSIZE][YSIZE];
static int queueX[QSIZE];
static int queueY[QSIZE];
static int rasterWidth;
static int rasterHeigth;
static int rasterXOffset;
static int rasterYOffset;
static int route45Enabled;
static int queueIn;
static int queueOut;
static int minDistance;
static int bestTargetX;
static int bestTargetY;
static int getQueueCount;
static int xSource;
static int ySource;
enum Direction {
    ZERO, UP, UP_RIGHT, RIGHT, DOWN_RIGHT, DOWN, DOWN_LEFT, LEFT, ERROR, UP_LEFT
};

JNIEXPORT

void JNICALL
Java_com_logicbreadboard_simulator_Router_initCRouter(JNIEnv *env, jobject instance,
                                                               jfloat minX,
                                                               jfloat minY, jfloat maxX,
                                                               jfloat maxY,
                                                               jboolean diagonalEnabled) {
//        rasterWidth = (int) (maxX - minX) + 2 * MARGIN;
//        rasterHeigth = (int) (maxY - minY) + 2 * MARGIN;
//        rasterXOffset = -(int) minX + MARGIN;
//        rasterYOffset = -(int) minY + MARGIN;
    rasterWidth = (short) (maxX - minX);
    rasterHeigth = (short) (maxY - minY);
    rasterXOffset = (short) (-minX);
    rasterYOffset = (short) (-minY);
    route45Enabled = diagonalEnabled;
    allEnabled = VERTICAL_ALLOWED | HORIZONTAL_ALLOWED;
    if (route45Enabled) {
        allEnabled |= DOWN_ALLOWED | UP_ALLOWED;
    }
    for (int x = 0; x < rasterWidth - 1; x++) {
        for (int y = 0; y < rasterHeigth - 1; y++) {
            inQueue[x][y] = 0;
            raster[x][y] = DEFAULT_RASTER_PIXEL;
            mask[x][y] = allEnabled;
        }
    }
    queueIn = 0;
    queueOut = 0;
}

static void addQueue(int x, int y, int distance, signed char direction /*boolean onWire*/) {
    if (x < 0 || y < 0 || x >= rasterWidth || y >= rasterHeigth) {
        //           System.out.println("Out " + x + " " + y + " " + distance);
        return;
    }
    int foundDistance = raster[x][y];
    signed char pixelMask = mask[x][y];
    if (pixelMask == ALL_DISABLED) {
//            System.out.println("(pixelMask & ALL_DISABLED) != 0 at:  " + x + " " + y);
        return;

    } else if (pixelMask == allEnabled && foundDistance > distance) {
//            System.out.println("foundDistance " + x + " " + y + " " + foundDistance + " " + distance);
        raster[x][y] = distance;
    } else if ((direction & pixelMask) != 0) {
        int crossPenalty;
        crossPenalty = 10;
        if (foundDistance > distance + crossPenalty) {
            raster[x][y] = (distance + crossPenalty);
        } else {
//                System.out.println("foundDistance > distance + crossPenalty at:  " + x + " " + y);
            return;
        }
    } else if (pixelMask == TARGET_PIXEL) {
        if (foundDistance > distance) {
            if (minDistance > distance) {
//                    System.out.println("FOUND TARGET " + distance + " < " + minDistance);
                minDistance = distance;
                bestTargetX = x;
                bestTargetY = y;
                ALOG("in wave best set x= %d  y=%d", x, y);
            }
            raster[x][y] = distance;
        }
//            return;
    } else {
//            if (pixelMask != 0) {
//                System.out.println("pixelMask " + pixelMask + " direction " + direction);
//                System.out.flush();
//            }
        if (pixelMask == allEnabled) {
            int crossPenalty;
            crossPenalty = 10;
            if (foundDistance > distance + crossPenalty) {
                raster[x][y] = (distance + crossPenalty);
            } else {
//                    System.out.println("2. foundDistance > distance + crossPenalty at:  " + x + " " +
//                            y);
                return;
            }
        } else {
            return;
        }
    }
    if (inQueue[x][y]) {
        //           System.out.println("INQUEUE " + x + " " + y);
        return;
    }
    inQueue[x][y] = 1;
    queueX[queueIn] = x;
    queueY[queueIn] = y;
    queueIn = (queueIn + 1) % QSIZE;
    if (queueIn == queueOut) {
        return;
    }
}

static void setMask(int x, int y, int value) {
    if (x > 0 && x < XSIZE &&
        y > 0 && y < YSIZE) {
        mask[x][y] = value;
    }
}

static int getQueue() {
    if (queueIn == queueOut) {
//            System.out.println("getQueue end " + count);
        return 0;
    }
    getQueueCount++;
    xSource = queueX[queueOut];
    ySource = queueY[queueOut];
    inQueue[xSource][ySource] = 0;
//        System.out.println("INQUEUE " + xSource + " " + ySource + " " + queueOut + " " + queueIn);
    queueOut = (queueOut + 1) % QSIZE;
    return 1;
}


static int wave() {
//        private void wave(int x, int y, int xEnd, int yEnd) {
    int distance = 0;
    minDistance = DEFAULT_RASTER_PIXEL;
//        System.out.println("CALLED");
    ALOG("target at 74 28 %x", mask[74][28]);
    ALOG("target at 28 74 %x", mask[28][74]);
    for (int x = 0; x < rasterWidth - 1; x++) {
        for (int y = 0; y < rasterHeigth - 1; y++) {
            if (mask[x][y] == TARGET_PIXEL) {
                ALOG("target at x= %d  y=%d", x, y);
            }
        }
    }
    getQueueCount = 0;
    while (getQueue()) {
        distance = raster[xSource][ySource];
        if (distance == 0) {
            ALOG("in wave x= %d  y=%d", xSource, ySource);
            ALOG("in wave distance= %d  mask=%d", distance, mask[xSource][ySource]);
//                System.out.println("distance == 0 at:  " + xSource + " " + ySource + " :  "
//                        + mask[xSource][ySource]);
            if (mask[xSource][ySource] == TARGET_PIXEL) {
//                    System.out.println("TARGET_PIXEL at:  " + xSource + " " + ySource);
                ALOG("in wave break distance= %d.", distance);
                break;
            }
        }
        //           boolean onWire = mask[xSource][ySource] == WIRE_PIXEL;
        int newDistance = (distance + 10);
        if (newDistance <= minDistance) {
            addQueue((short) (xSource - 1), ySource, newDistance, HORIZONTAL_ALLOWED);
            addQueue((short) (xSource + 1), ySource, newDistance, HORIZONTAL_ALLOWED);
            addQueue((short) (xSource), (short) (ySource - 1), newDistance,
                     VERTICAL_ALLOWED);
            addQueue((short) (xSource), (short) (ySource + 1), newDistance,
                     VERTICAL_ALLOWED);
            if (route45Enabled) {
                newDistance = (distance + 14);
                //                   System.out.println(xSource + ", " + ySource);
                if (xSource > 0 && ySource > 0 && (mask[xSource][ySource - 1] != ALL_DISABLED)
                    && mask[xSource - 1][ySource] != ALL_DISABLED) {
                    addQueue((short) (xSource - 1), (short) (ySource - 1), newDistance,
                             UP_ALLOWED);
                }
                if (
                        xSource < rasterWidth - 1 && ySource < rasterHeigth - 1 &&
                        (mask[xSource][ySource + 1] != ALL_DISABLED) && (mask[xSource +
                                                                              1][ySource] !=
                                                                         ALL_DISABLED)) {
                    addQueue((short) (xSource + 1), (short) (ySource + 1), newDistance,
                             UP_ALLOWED);
                }
                if (
                        xSource > 0 && ySource < rasterHeigth - 1 && (mask[xSource][ySource + 1]
                                                                      != ALL_DISABLED) &&
                        (mask[xSource - 1][ySource] != ALL_DISABLED)
                        ) {
                    addQueue((short) (xSource - 1), (short) (ySource + 1), newDistance,
                             DOWN_ALLOWED);
                }
                if (
                        ySource > 0 && xSource < rasterWidth - 1 && (mask[xSource][ySource - 1] !=
                                                                     ALL_DISABLED) &&
                        (mask[xSource + 1][ySource] != ALL_DISABLED)) {
                    addQueue((short) (xSource + 1), (short) (ySource - 1), newDistance,
                             DOWN_ALLOWED);
                }
            }
        } else {
//                System.out.println("Drop " + newDistance + " > " + minDistance);
        }
    }
    return distance;
}

static int xFromFindDirection;
static int yFromFindDirection;

//static enum Direction findDirection(short x, short y, enum Direction prevDirection) {
JNIEXPORT jint JNICALL
Java_com_logicbreadboard_simulator_Router_findCDirection(JNIEnv *env, jobject instance,
                                                         jint prevDirection) {

    int distance = 0;
    int newDistance = 0;
    int leftDirectionDistance = DEFAULT_RASTER_PIXEL;
    int rightDirectionDistance = DEFAULT_RASTER_PIXEL;
    int upDirectionDistance = DEFAULT_RASTER_PIXEL;
    int downDirectionDistance = DEFAULT_RASTER_PIXEL;
    int upLeftDirectionDistance = DEFAULT_RASTER_PIXEL;
    int upRightDirectionDistance = DEFAULT_RASTER_PIXEL;
    int downLeftDirectionDistance = DEFAULT_RASTER_PIXEL;
    int downRightDirectionDistance = DEFAULT_RASTER_PIXEL;
    enum Direction dir = ZERO;
    ALOG("in findCDirection prevDirection= %d", prevDirection);
    short x = xFromFindDirection;
    short y = yFromFindDirection;
    short newX = x;
    short newY = y;
    distance = raster[x][y];
    signed char maskPixel = mask[x][y];
    ALOG("1. x=%d y=%d dir= %d  distance=%d", x, y, dir, distance);
    ALOG("                       maskPixel= %x ", maskPixel);
//        maskPixel = (byte) (maskPixel & ~TARGET_PIXEL);
    if (((maskPixel & (HORIZONTAL_ALLOWED | TARGET_PIXEL)) != 0)) {
        if (x > 0) {
            newX = (short) (x - 1);
            newDistance = raster[newX][newY];
            leftDirectionDistance = newDistance;
            if (newDistance >= 0 && newDistance < distance) {
                distance = newDistance;
                xFromFindDirection = newX;
                yFromFindDirection = newY;
                dir = LEFT;
            }
        }
        newX = x;
        newY = y;
        if (x < rasterWidth - 1) {
            newX = (short) (x + 1);
            newDistance = raster[newX][newY];
            rightDirectionDistance = newDistance;
            if (newDistance >= 0 && newDistance < distance) {
                distance = newDistance;
                xFromFindDirection = newX;
                yFromFindDirection = newY;
                dir = RIGHT;
            }
        }
    }
    if (((maskPixel & (VERTICAL_ALLOWED | TARGET_PIXEL)) != 0)) {
        newX = x;
        newY = y;
        if (y > 0) {
            newY = (short) (y - 1);
            newDistance = raster[newX][newY];
            upDirectionDistance = newDistance;
            if (newDistance >= 0 && newDistance < distance) {
                distance = newDistance;
                xFromFindDirection = newX;
                yFromFindDirection = newY;
                dir = UP;
            }
        }
        newX = x;
        newY = y;
        if (y < rasterHeigth - 1) {
            newY = (short) (y + 1);
            newDistance = raster[newX][newY];
            downDirectionDistance = newDistance;
            if (newDistance >= 0 && newDistance < distance) {
                ALOG(" newDistance= %d  distance=%d", newDistance, distance);
                distance = newDistance;
                xFromFindDirection = newX;
                yFromFindDirection = newY;
                dir = DOWN;
            }
        }
    }
    newX = x;
    newY = y;
    if (route45Enabled) {
        if (((maskPixel & (UP_ALLOWED | TARGET_PIXEL)) != 0) && x > 0) {
            if (y > 0 && x > 0) {
                if (y > 0 && x > 0 && (mask[x][y - 1] != ALL_DISABLED) && (mask[x -
                                                                                1][y] !=
                                                                           ALL_DISABLED)) {
                    newX = (short) (x - 1);
                    newY = (short) (y - 1);
                    newDistance = raster[newX][newY];
                    upLeftDirectionDistance = newDistance;
                    if (newDistance >= 0 && newDistance < distance) {
                        distance = newDistance;
                        xFromFindDirection = newX;
                        yFromFindDirection = newY;
                        dir = UP_LEFT;
                    }
                }
            }
        }
        newX = x;
        newY = y;
        if (((maskPixel & (DOWN_ALLOWED | TARGET_PIXEL)) != 0) && x > 0) {
            if (y < rasterHeigth - 1 && x > 0 && (mask[x][y + 1] != ALL_DISABLED) &&
                (mask[x - 1][y] != ALL_DISABLED)) {
                newX = (short) (x - 1);
                newY = (short) (y + 1);
                newDistance = raster[newX][newY];
                downLeftDirectionDistance = newDistance;
                if (newDistance >= 0 && newDistance < distance) {
                    distance = newDistance;
                    xFromFindDirection = newX;
                    yFromFindDirection = newY;
                    dir = DOWN_LEFT;
                }
            }
        }
        newX = x;
        newY = y;
        if (((maskPixel & (DOWN_ALLOWED | TARGET_PIXEL)) != 0) && x > 0) {
            if (y > 0 && x < rasterWidth - 1 && (mask[x][y - 1] != ALL_DISABLED) &&
                (mask[x + 1][y] != ALL_DISABLED)) {
                newX = (short) (x + 1);
                newY = (short) (y - 1);
                newDistance = raster[newX][newY];
                upRightDirectionDistance = newDistance;
                if (newDistance >= 0 && newDistance < distance) {
                    distance = newDistance;
                    xFromFindDirection = newX;
                    yFromFindDirection = newY;
                    dir = UP_RIGHT;
                }
            }
        }
        newX = x;
        newY = y;
        if (((maskPixel & (UP_ALLOWED | TARGET_PIXEL)) != 0) && x > 0) {
            if (y < rasterHeigth - 1 && x < rasterWidth - 1 && (mask[x][y + 1] !=
                                                                ALL_DISABLED) &&
                (mask[x + 1][y] != ALL_DISABLED)) {
                newX = (short) (x + 1);
                newY = (short) (y + 1);
                newDistance = raster[newX][newY];
                downRightDirectionDistance = newDistance;
                if (newDistance >= 0 && newDistance < distance) {
                    distance = newDistance;
                    xFromFindDirection = newX;
                    yFromFindDirection = newY;
                    dir = DOWN_RIGHT;
                }
            }
        }
    }
//        System.out.println("distance " + distance);
//        System.out.flush();
    ALOG("2. dir= %d  distance=%d", dir, distance);

    if (dir == ZERO && distance != 0) {
//            System.out.println("?????????????????????????????????????????????????????????? " + distance);
//            System.out.flush();
        dir = ERROR;
    }
    ALOG("3. dir= %d  distance=%d", dir, distance);
    if (dir != prevDirection && distance != 0) {
        switch (prevDirection) {
            case LEFT:
                if (leftDirectionDistance == distance) {
//                        System.out.println("dir=" + dir + " prevDirection=" + prevDirection + " " +
//                                "distance=" + distance + " leftDirectionDistance=" + leftDirectionDistance);
                    dir = prevDirection;
                    xFromFindDirection = (short) (x - 1);
                    yFromFindDirection = y;
                }
                break;
            case RIGHT:
                if (rightDirectionDistance == distance) {
//                        System.out.println("dir=" + dir + " prevDirection=" + prevDirection + " " +
//                                "distance=" + distance + " rightDirectionDistance=" + rightDirectionDistance);
                    dir = prevDirection;
                    xFromFindDirection = (short) (x + 1);
                    yFromFindDirection = y;
                }
                break;
            case UP:
                if (upDirectionDistance == distance) {
//                        System.out.println("dir=" + dir + " prevDirection=" + prevDirection + " " +
//                                "distance=" + distance + " upDirectionDistance=" + upDirectionDistance);
                    dir = prevDirection;
                    xFromFindDirection = x;
                    yFromFindDirection = (short) (y - 1);
                }
                break;
            case DOWN:
                if (downDirectionDistance == distance) {
//                        System.out.println("dir=" + dir + " prevDirection=" + prevDirection + " " +
//                                "distance=" + distance + " downDirectionDistance=" + downDirectionDistance);
                    dir = prevDirection;
                    xFromFindDirection = x;
                    yFromFindDirection = (short) (y + 1);
                }
                break;
            case UP_LEFT:
                if (upLeftDirectionDistance == distance) {
//                        System.out.println("dir=" + dir + " prevDirection=" + prevDirection + " " +
//                                "distance=" + distance + " upLeftDirectionDistance=" + upLeftDirectionDistance);
                    xFromFindDirection = (short) (x - 1);
                    yFromFindDirection = (short) (y - 1);
                    dir = prevDirection;
                }
                break;
            case DOWN_LEFT:
                if (downLeftDirectionDistance == distance) {
//                        System.out.println("dir=" + dir + " prevDirection=" + prevDirection + " " +
//                                "distance=" + distance + " downLeftDirectionDistance=" + downLeftDirectionDistance);
                    dir = prevDirection;
                    xFromFindDirection = (short) (x - 1);
                    yFromFindDirection = (short) (y + 1);
                }
                break;
            case UP_RIGHT:
                if (upRightDirectionDistance == distance) {
//                        System.out.println("dir=" + dir + " prevDirection=" + prevDirection + " " +
//                                "distance=" + distance + " upRightDirectionDistance=" + upRightDirectionDistance);
                    xFromFindDirection = (short) (x + 1);
                    yFromFindDirection = (short) (y - 1);
                    dir = prevDirection;
                }
                break;
            case DOWN_RIGHT:
                if (downRightDirectionDistance == distance) {
//                        System.out.println("dir=" + dir + " prevDirection=" + prevDirection + " " +
//                                "distance=" + distance + " downRightDirectionDistance=" + downRightDirectionDistance);
                    dir = prevDirection;
                    xFromFindDirection = (short) (x + 1);
                    yFromFindDirection = (short) (y + 1);
                }
                break;
            case ZERO: // start back. try to follow the pin direction
                if (maskPixel == TARGET_PIXEL) {
                    switch (dir) {
                        case UP:
                            if (mask[x][y + 1] != ALL_DISABLED) { // possible wrong
                                // direction
                                if (leftDirectionDistance == upDirectionDistance) {
                                    xFromFindDirection = (short) (x - 1);
                                    yFromFindDirection = y;
                                    dir = LEFT;
                                } else if (rightDirectionDistance == upDirectionDistance) {
                                    xFromFindDirection = (short) (x + 1);
                                    yFromFindDirection = y;
                                    dir = RIGHT;
                                }
                            }
                            break;
                        case DOWN:
                            if (mask[x][y - 1] != ALL_DISABLED) { // possible wrong
                                // direction
                                if (leftDirectionDistance == downDirectionDistance) {
                                    xFromFindDirection = (short) (x - 1);
                                    yFromFindDirection = y;
                                    dir = LEFT;
                                } else if (rightDirectionDistance == downDirectionDistance) {
                                    xFromFindDirection = (short) (x + 1);
                                    yFromFindDirection = y;
                                    dir = RIGHT;
                                }
                            }
                            break;
                        case LEFT:
                            if (mask[x + 1][y] != ALL_DISABLED) { // possible wrong
                                // direction
                                if (leftDirectionDistance == upDirectionDistance) {
                                    xFromFindDirection = x;
                                    yFromFindDirection = (short) (y - 1);
                                    dir = UP;
                                } else if (leftDirectionDistance == downDirectionDistance) {
                                    xFromFindDirection = x;
                                    yFromFindDirection = (short) (y + 1);
                                    dir = DOWN;
                                }
                            }
                            break;
                        case RIGHT:
                            if (mask[x - 1][y] != ALL_DISABLED) { // possible wrong
                                // direction
                                if (rightDirectionDistance == upDirectionDistance) {
                                    xFromFindDirection = x;
                                    yFromFindDirection = (short) (y - 1);
                                    dir = UP;
                                } else if (rightDirectionDistance == downDirectionDistance) {
                                    xFromFindDirection = x;
                                    yFromFindDirection = (short) (y + 1);
                                    dir = DOWN;
                                }
                            }
                            break;
                        default:
                            break;
                    }
                }
                break;
//            default:
//                return dir;
        }
    }
    ALOG("4. dir= %d  distance=%d", dir, distance);
    ALOG(" xFromFindDirection= %d  yFromFindDirection=%d", xFromFindDirection, yFromFindDirection);
    return dir;
}

static int lastDistance;

JNIEXPORT jint JNICALL
Java_com_logicbreadboard_simulator_Router_findCRoute(JNIEnv *env, jobject instance) {


    //todo check if endpoints are really selected, not cleared
//        float x0 = selStartX + rasterXOffset;
//        float x1 = selTargetX + rasterXOffset;
//        float y0 = selStartY + rasterYOffset;
//        float y1 = selTargetY + rasterYOffset;
//        int xStart = Math.round(x0 / PIXEL_RASTER_RATIO);
//        int yStart = Math.round(y0 / PIXEL_RASTER_RATIO);
//        int xEnd = Math.round(x1 / PIXEL_RASTER_RATIO);
//        int yEnd = Math.round(y1 / PIXEL_RASTER_RATIO);
    lastDistance = wave();
    ALOG("distance= %d.", lastDistance);

//        wave(xStart, yStart, xEnd, yEnd);
//        System.out.println("Orig " + selTargetX + " " + selTargetY);
    if (getQueueCount == 0) {
//            System.out.println("return null 1 ");
        ALOG("getQueueCount == 0 lastDistance= %d.", lastDistance);
        return 0xffffffff;
    } else if (lastDistance == 0) { // zero queue
        ALOG("lastDistance == 0 lastDistance= %d.", lastDistance);
        short selTargetX = (short) (xSource - rasterXOffset);
        short selTargetY = (short) (ySource - rasterYOffset);

        return (selTargetY << 16) | (selTargetX & 0xffff);
    }
    if (mask[bestTargetX][bestTargetY] == TARGET_PIXEL) {
        ALOG("mask[bestTargetX][bestTargetY] == TARGET_PIXEL lastDistance= %d.", lastDistance);
        short selTargetX = (short) (bestTargetX - rasterXOffset);
        short selTargetY = (short) (bestTargetY - rasterYOffset);
        return (selTargetY << 16) | (selTargetX & 0xffff);
    } else {
//            System.out.println("return null 2 ");
        ALOG("else lastDistance= %d.", lastDistance);
        return 0xffffffff;
    }
}

JNIEXPORT void JNICALL
Java_com_logicbreadboard_simulator_Router_addCPointMask(JNIEnv *env, jobject instance, jint x0,
                                                        jint y0, jint r) {
    mask[x0][y0] = ALL_DISABLED;

    setMask(x0, y0, ALL_DISABLED);
    setMask(x0 + 1, y0, ALL_DISABLED);
    setMask(x0 - 1, y0, ALL_DISABLED);
    setMask(x0, y0 - 1, ALL_DISABLED);
    setMask(x0, y0 + 1, ALL_DISABLED);
    setMask(x0 + 1, y0 + 1, ALL_DISABLED);
    setMask(x0 + 1, y0 - 1, ALL_DISABLED);
    setMask(x0 - 1, y0 - 1, ALL_DISABLED);
    setMask(x0 - 1, y0 + 1, ALL_DISABLED);
}

void Java_com_logicbreadboard_simulator_Router_setCMask__IIB(JNIEnv *env,
                                                             jobject thiz, jint x, jint y,
                                                             jbyte value) {
    if (x > 0 && x < XSIZE &&
        y > 0 && y < YSIZE) {
        mask[x][y] = value;
    }
}

void Java_com_logicbreadboard_simulator_Router_andCMask__IIB(JNIEnv *env,
                                                             jobject thiz, jint x, jint y,
                                                             jbyte value) {
    if (x > 0 && x < XSIZE &&
        y > 0 && y < YSIZE) {
        mask[x][y] &= value;
    }
}

jstring
Java_com_logicbreadboard_simulator_MainActivity_stringFromJNI(JNIEnv *env,
                                                              jobject thiz, jint x, jint y) {
#if defined(__arm__)
#if defined(__ARM_ARCH_7A__)
#if defined(__ARM_NEON__)
#if defined(__ARM_PCS_VFP)
#define ABI "armeabi-v7a/NEON (hard-float)"
#else
#define ABI "armeabi-v7a/NEON"
#endif
#else
#if defined(__ARM_PCS_VFP)
#define ABI "armeabi-v7a (hard-float)"
#else
#define ABI "armeabi-v7a"
#endif
#endif
#else
#define ABI "armeabi"
#endif
#elif defined(__i386__)
#define ABI "x86"
#elif defined(__x86_64__)
#define ABI "x86_64"
#elif defined(__mips64)  /* mips64el-* toolchain defines __mips__ too */
#define ABI "mips64"
#elif defined(__mips__)
#define ABI "mips"
#elif defined(__aarch64__)
#define ABI "arm64-v8a"
#else
#define ABI "unknown"
#endif
    return (*env)->NewStringUTF(env, "Hello from JNI !  Compiled with ABI " ABI ".");
}

JNIEXPORT void JNICALL
Java_com_logicbreadboard_simulator_Router_addCQueue(JNIEnv *env, jobject instance,
                                                             jshort x,
                                                             jshort y, jshort distance,
                                                             jbyte direction) {

    addQueue(x, y, distance, direction);

}

JNIEXPORT jint JNICALL
Java_com_logicbreadboard_simulator_Router_initCDirection(JNIEnv *env, jobject instance,
                                                                  jshort x,
                                                                  jshort y) {

    xFromFindDirection = x;
    yFromFindDirection = y;
    return 0;
}

JNIEXPORT jshort JNICALL
Java_com_logicbreadboard_simulator_Router_getCxFromFindDirection(JNIEnv *env, jobject instance) {

    return xFromFindDirection;

}

JNIEXPORT jshort JNICALL
Java_com_logicbreadboard_simulator_Router_getCyFromFindDirection(JNIEnv *env, jobject instance) {

    return yFromFindDirection;

}

JNIEXPORT jint JNICALL
Java_com_logicbreadboard_simulator_Router_getCLastDistance(JNIEnv *env, jobject instance) {

    return lastDistance;

}