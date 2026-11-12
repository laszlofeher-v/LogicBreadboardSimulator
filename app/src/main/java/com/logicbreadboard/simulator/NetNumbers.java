package com.logicbreadboard.simulator;

/**
 * Created by laci on 7/23/15.
 */
public class NetNumbers {
    public static final int NOT_CONNECTED = 0 ;
    static int maxNum;
//    static TreeSet<Integer> usedNetNumbers;

    static {
        reset();
    }

    static int get() {
        int n;
        n = maxNum++;
//        usedNetNumbers.addCandidate(n);
//        System.out.println("maxNum=" + maxNum + "  get netNumber=" + n);
//        System.out.flush();
        return n;
    }

    static void reset() {
        maxNum = 1;
    }

//    static void save(DataOutputStream outputStream) throws java.io.IOException {
////        int freeCount = freeNetNumbers.size();
//        outputStream.writeInt(maxNum);
////        outputStream.writeInt(0);
////        for (int i = 0; i < freeCount; i++) {
////            int n = freeNetNumbers.first();
////            outputStream.writeInt(n);
////            freeNetNumbers.removeInput(n);
////        }
//    }

//    static void load(DataInputStream inputStream) throws java.io.IOException {
////        freeNetNumbers = new TreeSet<Integer>();
//        maxNum = inputStream.readInt();
////        int freeCount = inputStream.readInt();
////        for (int i = 0; i < freeCount; i++) {
////            inputStream.readInt();
////        }
//    }

}
