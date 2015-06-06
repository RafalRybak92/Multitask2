package com.company;

import com.company.Structures.ItemToShop;
import com.company.Structures.MagazineForShop;
import com.company.Structures.OrderFromShop;

import java.util.Random;
import java.util.concurrent.BlockingQueue;

/**
 * Created by rafalrybak on 06.06.15.
 * Shop
 */
public class Shop implements Runnable {
    protected int mShopNumber;
    protected Config mConfig = new Config();
    protected ItemToShop mItemToShop;
    protected BlockingQueue<MagazineForShop> mShopQueue;
    protected BlockingQueue<OrderFromShop> mOrderFromShopQueue;
    protected BlockingQueue<ItemToShop> mItemToShopQueue;
    protected Random rand = new Random();

    Shop(int i, BlockingQueue<MagazineForShop> shopQueue, BlockingQueue<OrderFromShop> orderFromShopQueue,
         BlockingQueue<ItemToShop> itemToShop) {
        mShopNumber = i;
        mShopQueue = shopQueue;
        mOrderFromShopQueue = orderFromShopQueue;
        mItemToShopQueue = itemToShop;
    }

    @Override
    public void run() {
        try {
            System.out.println("Shop nr: " + mShopNumber+ " Started");
            while (true) {
                if (mShopQueue.size() == 0) {
                    for(int i =0; i < mConfig.STORE_MAX; i++) {
                        int opC = rand.nextInt(3);
                        mOrderFromShopQueue.put(new OrderFromShop(opC, mShopNumber));
                    }
                }

                if(!mItemToShopQueue.isEmpty()){
                    mItemToShop = mItemToShopQueue.take();
                    if(mItemToShop.mShopDelivery == mShopNumber){
                        mShopQueue.put(new MagazineForShop(mItemToShop.mOpA, mItemToShop.mOpB, mItemToShop.mOpC,
                                mItemToShop.mResolved, mShopNumber));
                    }else{
                        mItemToShopQueue.put(mItemToShop);
                    }
                }
            }
        }catch(InterruptedException e){
            e.printStackTrace();
        }
    }
}
