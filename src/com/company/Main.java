package com.company;


import com.company.Structures.ItemToShop;
import com.company.Structures.MagazineForShop;
import com.company.Structures.MagazineForTransport;
import com.company.Structures.OrderFromShop;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

public class Main {
    public static void main(String[] args) {
        Config conf = new Config();
        LinkedBlockingQueue<MagazineForShop> myMagazineForShop = new LinkedBlockingQueue();
        LinkedBlockingQueue<MagazineForTransport> myMagazineForTransport = new LinkedBlockingQueue();
        LinkedBlockingQueue<OrderFromShop> myOrderFromShop = new LinkedBlockingQueue();
        LinkedBlockingQueue<ItemToShop> myItemToShop = new LinkedBlockingQueue();

        Executor execComp = Executors.newCachedThreadPool();
        Executor execTran = Executors.newCachedThreadPool();
        Executor execShop = Executors.newCachedThreadPool();
        Executor execCust = Executors.newCachedThreadPool();

        for(int i = 0; i < conf.MAX_COMPANIES; i++){
            Company cp = new Company(myMagazineForTransport,i);
            execComp.execute(cp);
        }

        for(int i = 0; i < conf.MAX_TRANSPORT_COMP; i++){
            Transport tp = new Transport(myMagazineForTransport,myOrderFromShop,myItemToShop, i);
            execTran.execute(tp);
        }

        for(int i = 0; i < conf.MAX_STORES; i++){
            Shop sh = new Shop(i, myMagazineForShop, myOrderFromShop, myItemToShop);
            execShop.execute(sh);
        }

        for(int i = 0; i < conf.MAX_CUSTOMERS; i++){
            Customer cu = new Customer(myMagazineForShop, i);
            execCust.execute(cu);
        }

    }
}
