import com.sun.org.apache.regexp.internal.RE;
import com.sun.xml.internal.bind.v2.util.QNameMap;

import java.text.DecimalFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Created by disinuo on 17/4/7.
 */
public class Main {

    public static void main(String[] args) throws InterruptedException {

        Resource r1=new Resource();
        Resource r2=new Resource();
        Resource r3=new Resource();


        Thread t1 = new Thread(new SyncThreadC(r1, r2), "t1");
        Thread t2 = new Thread(new SyncThreadC(r2, r3), "t2");
        Thread t3 = new Thread(new SyncThreadC(r3, r1), "t3");

        t1.start();
        Thread.sleep(5000);
        t2.start();
        Thread.sleep(5000);
        t3.start();
    }
}

class SyncThreadC implements Runnable{
    private Resource r1;
    private Resource r2;


    public SyncThreadC(Resource o1, Resource o2){
        this.r1=o1;
        this.r2=o2;
    }
    @Override
    public void run() {
        String name = Thread.currentThread().getName();
        System.out.println(name + " acquiring lock on "+r1);

        r1.getResource(name);
        work();
        System.out.println(name + " acquiring lock on "+r2);


        Resource r=r2.getResource(name);
        System.out.println("----"+name+" got Resource r2= "+r2);

        if(r!=null){
            work();
            r2.unlock(name);
        }
        r1.unlock(name);
        System.out.println(name + " finished execution.");
    }
    private void work() {
        try {
            System.out.println("working...");
            Thread.sleep(30000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
class Resource{
    private Lock lock=new ReentrantLock();
    public Resource getResource(String threadName){
        boolean ifGetLock;
        try{
           ifGetLock=lock.tryLock(30000,TimeUnit.MILLISECONDS);
            if(ifGetLock){
                System.out.println(threadName + " acquired lock on "+this);
                return this;
            }else {
                return null;
            }
        }catch (InterruptedException e){
            e.printStackTrace();
        }finally {
            return null;
        }

    }
    public void unlock(String threadName){
        lock.unlock();
        System.out.println(threadName + " released lock on "+this);

    }
}
