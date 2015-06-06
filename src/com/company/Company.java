package com.company;

import com.company.Structures.MagazineForTransport;
import com.company.Structures.containerToDo;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by rafalrybak on 06.06.15.
 */
class Company implements Runnable {
    protected int mOpA, mOpB, mOpC, mResolved, mWorkerA, mWorkerB, mWorkerC, mWrokerD,
            mAddingCounter, mMultiplyCounter, mCompanyNumber;
    public static Config mConf = new Config();
    public static List<Worker> mThreadSet = new LinkedList<>();
    protected static Random mRand = new Random();
    protected BlockingQueue<MagazineForTransport> mMagazineForTransport;

    Company(BlockingQueue<MagazineForTransport> magazineForTransport, int i) {
        mCompanyNumber = i;
        mMagazineForTransport = magazineForTransport;
    }

        /*
        Boss class, using concurent and blockig mTaskQueue to manage the multitasking;
        */

    class Boss implements Runnable {
        BlockingQueue<containerToDo> mTaskQueue;
        int i = 0;

        Boss(BlockingQueue<containerToDo> queue) {
            this.mTaskQueue = queue;
        }

        public void run() {
            try {
                while (true) {
                    while (mTaskQueue.size() == mConf.MAX) {
                        Thread.sleep(1000);
                    }
                    mOpA = mRand.nextInt(9);
                    mOpB = mRand.nextInt(9);
                    mOpC = mRand.nextInt(3);
                    i++;
                    mTaskQueue.put(new containerToDo(mOpA, mOpB, mOpC, 0));
                    System.out.println("Company "+ mCompanyNumber + " Procedure " + i + " : " + mOpA + " " + mOpB + " " + mOpC + " List size now:" +
                            mTaskQueue.size());
                    Thread.sleep(mRand.nextInt(9) * mConf.BOSS_SLEEP);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();

            }
        }
    }

    /*
    Worker class, using concurent and blockig mTaskQueue to manage the multitasking;
    */
    class Worker implements Runnable {
        protected BlockingQueue<containerToDo> mTaskQueue;
        protected BlockingQueue<MagazineForTransport> mMagazienQueue;
        protected AddingMachine mAddingMachine;
        protected int mCounting = 0;
        protected boolean mState;
        protected Random mRand = new Random();
        protected int mIteration;
        boolean mButtonOne, mButtonTwo;
        protected int mWorekerNumber;

        Worker(BlockingQueue<containerToDo> queue, BlockingQueue<MagazineForTransport> magazineForTransportQueue, int i) {
            mTaskQueue = queue;
            mMagazienQueue = magazineForTransportQueue;
            mWorekerNumber = i;
        }

        private void adding(containerToDo cont, int i) throws InterruptedException {
            if (mAddingCounter <= mConf.MAX_ADDING_MACH) {
                mAddingMachine = new AddingMachine();
                mAddingMachine.addRecord(cont);
                System.out.println("Worker" + i +" in Company "+ mCompanyNumber + " take: " + mWorkerA + " + " + mWorkerB + " and going to adding machine ");
                Thread.sleep(mConf.ADDING_TIME);
                if (mAddingMachine.mSuccess) {
                    containerToDo cont2 = mAddingMachine.mSentItem;
                    System.out.println("Company "+ mCompanyNumber +" mSuccess " + cont2.mOpA + " + " + cont2.mOpB + " = " + cont2.mOpD);
                    mMagazienQueue.put(new MagazineForTransport(cont2.mOpA, cont2.mOpB, cont2.mOpC, cont2.mOpD, mCompanyNumber));
                    mAddingCounter--;
                } else {
                    System.err.println("Company "+ mCompanyNumber +" OMG I FAILED!!!");
                    if (mAddingCounter <= mConf.MAX_ADDING_MACH) {
                        adding(cont, i);
                    }
                }
            } else {
                Thread.sleep(mConf.ADDING_TIME);
                System.out.println("Company " + mCompanyNumber + "All machines are in use irght now! Trying one more time");
                adding(cont, i);
            }
        }

        private synchronized void multiply(containerToDo cont, int i) throws InterruptedException {
            if (mMultiplyCounter <= mConf.MAX_MULTIPLYING_MACH) {
                mIteration = mRand.nextInt(mThreadSet.size());
                Worker wrker = mThreadSet.get(mIteration);

                if (wrker == this) {
                    multiply(cont, i);
                } else {
                    if (wrker.mState) {
                        System.out.println("Company "+ mCompanyNumber +" Trying with anotherone");
                        multiply(cont, i);
                    } else {
                        mButtonOne = true;
                        mButtonTwo = true;
                        System.out.println("Worker " + i +"in Company nr: "+ mCompanyNumber + "  take: " + mWorkerA + "  " + mWorkerB + " = " + cont.mOpD +
                                " and going to multiplying machine ");
                        MultiplyingMachine mM = new MultiplyingMachine(mButtonOne, mButtonTwo);
                        mM.addRecord(cont);
                        if (mM.mSuccess) {
                            containerToDo cont2 = mM.mSentItem;
                            System.out.println("Company "+ mCompanyNumber +" mSuccess " + cont2.mOpA + " * " + cont2.mOpB + " = " + cont2.mOpD);

                            mMagazienQueue.put(new MagazineForTransport(cont2.mOpA, cont2.mOpB, cont2.mOpC, cont2.mOpD, mCompanyNumber));
                            mMultiplyCounter--;
                        } else {
                            System.err.println("Company "+ mCompanyNumber + " OMG LOL FAILD");
                            if (mMultiplyCounter <= mConf.MAX_MULTIPLYING_MACH) {
                                multiply(cont, i);
                            }
                        }
                    }
                }

            } else {
                Thread.sleep(mConf.ADDING_TIME);
                System.out.println("Company "+ mCompanyNumber +" All machines are in use irght now! Trying one more time");
                multiply(cont, i);
            }
        }

        public void run() {

            while (true) {
                if (mMagazienQueue.size() == mConf.STORE_MAX) {
                    return;
                } else {
                    try {
                        Thread.sleep(mConf.WORKER_SLEEP);
                        containerToDo cont = mTaskQueue.take();
                        mWorkerA = cont.mOpA;
                        mWorkerB = cont.mOpB;
                        mWorkerC = cont.mOpC;
                        mWrokerD = cont.mOpD;


                        switch (mWorkerC) {
                            case 0:
                                mState = true;
                                adding(cont, mWorekerNumber);
                                mState = false;
                                break;
                            case 1:
                                mState = true;
                                mResolved = mWorkerA - mWorkerB;
                                System.out.println("Company "+ mCompanyNumber +" Worker   " + mWorekerNumber + " take: " + mWorkerA + " - " + mWorkerB + " = " +
                                        mResolved);
                                mMagazienQueue.put(new MagazineForTransport(mWorkerA, mWorkerB, mWorkerC, mResolved, mCompanyNumber));
                                mState = false;
                                break;
                            case 2:
                                mState = true;
                                multiply(cont, mWorekerNumber);
                                mState = false;
                                break;
                        }

                        mCounting++;

                        if (mCounting == 10) {
                            Thread.sleep(mConf.WORKER_FREE_TIME);
                            mCounting = 0;
                        }


                    } catch (InterruptedException ex) {
                        ex.printStackTrace();
                    }
                }

                System.out.println("Company "+ mCompanyNumber +" Resolved current size: " + mTaskQueue.size());
            }
        }
    }
    /*
    Consumer class, using concurent and blockig mTaskQueue to manage the multitasking;
    */

    class MultiplyingMachine {

        public containerToDo mCurrItem, mSentItem;
        public boolean mSuccess = false;
        private int a, b, c, d;
        protected boolean mA, mB;
        protected boolean mVal;

        public MultiplyingMachine(boolean workerOne, boolean workerTwo) {
            workerOne = mA;
            workerTwo = mB;
        }

        public void addRecord(containerToDo cont) throws InterruptedException {
            mCurrItem = cont;
            make();
        }

        public void make() throws InterruptedException {

            if (mVal = new Random().nextInt(10) != 0) {

                mAddingCounter++;
                a = mCurrItem.mOpA;
                b = mCurrItem.mOpB;
                c = mCurrItem.mOpC;
                d = mCurrItem.mOpD;
                Thread.sleep(mConf.MULTYPLYING_TIME);

                d = a * b;

                mSentItem = new containerToDo(a, b, c, d);
                mSuccess = true;
            } else {
                mSuccess = false;
            }


        }


    }

    class AddingMachine {
        public containerToDo mCurrItem, mSentItem;
        public boolean mSuccess = false;
        private int a, b, c, d;
        boolean mVal;

        public void addRecord(containerToDo cont) {
            mCurrItem = cont;
            make();
        }

        public void make() {
            if (mVal = new Random().nextInt(10) != 0) {
                mAddingCounter++;
                a = mCurrItem.mOpA;
                b = mCurrItem.mOpB;
                c = mCurrItem.mOpC;
                d = mCurrItem.mOpD;

                d = a + b;

                mSentItem = new containerToDo(a, b, c, d);
                mSuccess = true;
            } else {
                mSuccess = false;
            }
        }


    }


    @Override
    public void run() {
        BlockingQueue<containerToDo> myToDo = new LinkedBlockingQueue();
        Executor execW = Executors.newCachedThreadPool();
        System.err.println("Company number " + mCompanyNumber + "Started!");
        new Thread(new Boss(myToDo)).start();

        for (int i = 0; i < mConf.MAX_WORKERS; i++) {
            Worker wk = new Worker(myToDo, mMagazineForTransport, i);
            execW.execute(wk);
            mThreadSet.add(i, wk);
            try {
                Thread.sleep(mConf.WORKERS_CHANGE);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}