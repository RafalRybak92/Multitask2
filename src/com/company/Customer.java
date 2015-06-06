package com.company;

import com.company.Structures.MagazineForShop;

import java.util.concurrent.BlockingQueue;

/**
 * Created by rafalrybak on 06.06.15.
 */
public class Customer implements Runnable {
    protected BlockingQueue<MagazineForShop> mMagazineForShopQueue;
    protected MagazineForShop mMagazineForShop;
    protected int mCustomerNumber;
    protected int mConsumerR;
    private Config mConf = new Config();


    Customer(BlockingQueue<MagazineForShop> magazineQueue, int i) {
        mCustomerNumber = i;
        mMagazineForShopQueue = magazineQueue;
    }

    public void run() {

        while (true) {
            try {
                Thread.sleep(mConf.CONSUMER_SLEEP);
                if(!mMagazineForShopQueue.isEmpty()) {
                    mMagazineForShop = mMagazineForShopQueue.take();
                    mConsumerR = mMagazineForShop.mResolved;
                    System.err.println("Consumer " + mCustomerNumber + " Take: " + mConsumerR + " From Shop nr: " +
                            mMagazineForShop.mShopNumber);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }
    }
}
