package com.company;

import com.company.Structures.ItemToShop;
import com.company.Structures.MagazineForTransport;
import com.company.Structures.OrderFromShop;

import java.util.Random;
import java.util.concurrent.BlockingQueue;

/**
 * Created by rafalrybak on 06.06.15.
 */
public class Transport implements Runnable {
    protected BlockingQueue<MagazineForTransport> mMagazineForTransportQueue;
    protected BlockingQueue<OrderFromShop> mOrderFromShopQueue;
    protected BlockingQueue<ItemToShop> mItemToShopQueue;
    protected int mTransportNumber;
    protected int mShopNumber;
    protected int mCars;
    protected MagazineForTransport mMagazineForTransport;
    protected OrderFromShop mOrderFromShop;
    protected Random rand = new Random();
    protected Config conf = new Config();

    public Transport(BlockingQueue<MagazineForTransport> magazineForTransportsQueue, BlockingQueue<OrderFromShop>
            orderFromShopsQueue, BlockingQueue<ItemToShop> itemToShopsQueue, int transportNumber) {
        mMagazineForTransportQueue = magazineForTransportsQueue;
        mOrderFromShopQueue = orderFromShopsQueue;
        mItemToShopQueue = itemToShopsQueue;
        mTransportNumber = transportNumber;
    }

    @Override
    public void run() {
        System.out.println("Transport firm nr: "+ mTransportNumber + " Started");
        while (true) {
            try {
                mOrderFromShop = mOrderFromShopQueue.take();
                mShopNumber = mOrderFromShop.mShopNumber;
                while (true) {
                    mMagazineForTransport = mMagazineForTransportQueue.take();
                    if (mMagazineForTransport.mOpC == mOrderFromShop.mOpC) {
                        mCars++;
                        System.out.println("CAR FROM COMP NR "+ mTransportNumber+"Is coing to Shop nr: "+ mShopNumber);
                        Thread.sleep(rand.nextInt(conf.MAX_COMPANIES) * 1000);
                        mItemToShopQueue.put(new ItemToShop(mMagazineForTransport.mOpA, mMagazineForTransport.mOpB,
                                mMagazineForTransport.mOpC, mMagazineForTransport.mResolved, mShopNumber));
                        System.out.println("CAR FROM COMP NR "+ mTransportNumber+"Done");
                        break;
                    } else {
                        mMagazineForTransportQueue.put(mMagazineForTransport);
                    }
                }

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
