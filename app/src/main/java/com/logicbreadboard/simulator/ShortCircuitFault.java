package com.logicbreadboard.simulator;

import java.util.Iterator;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Created by laci on 8/6/15.
 */
public class ShortCircuitFault {
    static private Queue<DeviceIOModel> candidateList;
    //   static BlockingQueue<FaultViewer> faultViewerQueue;

    static {
        candidateList = new ConcurrentLinkedQueue<DeviceIOModel>();
//        faultViewerQueue = new LinkedBlockingQueue<FaultViewer>();
    }

    static void clear() {
        candidateList.clear();
//        faultViewerQueue.clear();
    }

    static public void addCandidate(DeviceIOModel io) {
        if (!candidateList.contains(io)) {
            candidateList.add(io);
        }
    }

    static public void removeCandidate(DeviceIOModel io) {
        if (candidateList.contains(io)) {
            candidateList.remove(io);
        }
    }

    static public void checkCandidates() {
       Iterator<DeviceIOModel> iterator = candidateList.iterator();
        while (iterator.hasNext()) {
            DeviceIOModel io = iterator.next();
            if (io.impedance != State.Impedance.high) {
                DeviceIOModel c = io.connected;
                while (c != io) {
                    if (c.impedance != State.Impedance.high &&
                            ((c.level != io.level) || (c.level == State.Level.undefined))) {
                        if (c.bidirectionalFault()) {
                            removeCandidate(c);
                        } else {
                            FaultViewer.newFaultViewer(io, FaultViewer.FaultType.SHORT_CIRCUIT);
                        }
                    } else {
                        removeCandidate(c);
                    }
                    c = c.connected;
                }
            }
        }

        candidateList.clear();
    }
}
