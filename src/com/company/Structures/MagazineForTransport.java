package com.company.Structures;

/**
 * Created by rafalrybak on 06.06.15.
 */
        /*
        mOpA - parameter A
        mOpB - parameter B
        mOpC - Operator
        mResolved - Resolved

        This Class contains information about Store;
         */
public class MagazineForTransport {
    public int mOpA, mOpB, mOpC, mResolved, mFabricNumber;

    public MagazineForTransport(int opA, int opB, int opC, int resolved, int fabricNumber) {
        mOpA = opA;
        mOpB = opB;
        mOpC = opC;
        mResolved = resolved;
        mFabricNumber = fabricNumber;
    }
}
