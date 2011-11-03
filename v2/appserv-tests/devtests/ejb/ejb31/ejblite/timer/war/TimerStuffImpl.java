package com.sun.s1asdev.ejb31.ejblite.timer;

import java.lang.reflect.*;
import javax.naming.*;
import java.rmi.RemoteException;
import java.io.Serializable;
import java.util.Date;
import java.util.Collection;
import java.util.Iterator;
import javax.ejb.*;

public class TimerStuffImpl implements TimerStuff {
    
    private EJBContext context_;
    protected boolean isBMT = false;
    
    public TimerStuffImpl() {}

    protected void setContext(EJBContext context) {
        context_ = context;
    }

    // XXX - TODO
    protected boolean isTxUnspecified() { return false; }
    
    public Timer createTimer(long duration, String info) throws Exception {
        TimerService ts = context_.getTimerService();
        Timer t = ts.createSingleActionTimer(duration, new TimerConfig(info, false));
        return t;
    }

    public Timer createTimer(long duration) throws Exception {
        return createTimer(duration, "createTimer<long>" + duration);
    }

    public Timer createTimer(long initialDuration, long interval) 
        throws Exception {
        return createTimer(initialDuration, interval, 
                           "createTimer<long, long>" + initialDuration +
                           "," + interval);
    }

    public Timer createTimer(long initialDuration, long interval, 
                                   String info) 
        throws Exception {
        if( isBMT ) {
            context_.getUserTransaction().begin();
        }
        TimerService ts = context_.getTimerService();
        Timer t = ts.createIntervalTimer(initialDuration, interval, 
                    new TimerConfig(new AppInfo(info), false));
                                 
        if( isBMT ) {
            context_.getUserTransaction().commit();
        }
        return t;
    }

    public Timer createTimer(Date expirationTime) 
        throws Exception {
        TimerService ts = context_.getTimerService();
        if( isBMT ) {
            context_.getUserTransaction().begin();
        }
        // NOTE : in tx_unspecified case, technically there is a race
        // condition if the timer expires before method is returned.
        // This should happen very rarely since container puts the brakes
        // on a bit by adding a few seconds if, upon creation, the expiration
        // time has already passed.
        Timer t = ts.createSingleActionTimer(expirationTime, 
                new TimerConfig("createTimer<Date>" + expirationTime, false));
        if( isBMT ) {
            context_.getUserTransaction().commit();
        }
        return t;
    }

    public Timer createTimer(Date expirationTime, long interval) 
        throws Exception {

        if( isBMT ) {
            context_.getUserTransaction().begin();
        }
        TimerService ts = context_.getTimerService();
        Timer t = ts.createIntervalTimer(expirationTime, interval, 
                new TimerConfig("createTimer<Date, long>" + expirationTime +
                                 "," + interval, false));
        if( isBMT ) {
            context_.getUserTransaction().commit();
        }
        return t;
    }

    public void createTimerAndRollback(long duration) throws Exception {
        if( isTxUnspecified() ) {
            return;
        } else if( isBMT ) {
            context_.getUserTransaction().begin();
        }
        TimerService ts = context_.getTimerService();
        Timer t = ts.createSingleActionTimer(duration, 
                new TimerConfig("createTimerAndRollback" + duration, false));
        if( isBMT ) {
            context_.getUserTransaction().rollback();
        } else {
            context_.setRollbackOnly();
        }
    }

    public void createTimerAndCancel(long duration) throws Exception {
        if( isBMT ) {
            context_.getUserTransaction().begin();
        }
        TimerService ts = context_.getTimerService();
        Timer t = ts.createSingleActionTimer(duration, 
                new TimerConfig("createTimerAndCancel" + duration, false));
        t.cancel();
        if( isBMT ) {
            context_.getUserTransaction().commit();
        }
    }

    public void createTimerAndCancelAndCancel(long duration) throws Exception {
        if( isTxUnspecified() ) {
            return; 
        } else if( isBMT ) {
            context_.getUserTransaction().begin();
        }
        TimerService ts = context_.getTimerService();
        Timer t = ts.createSingleActionTimer(duration, 
                new TimerConfig("createTimerAndCancelAndCancel"+ duration, false));
        t.cancel();
        t.cancel();
        if( isBMT ) {
            context_.getUserTransaction().commit();
        }
    }

    public void createTimerAndCancelAndRollback(long duration) 
        throws Exception {
        if( isTxUnspecified() ) {
            return;
        } else if( isBMT ) {
            context_.getUserTransaction().begin();
        }
        TimerService ts = context_.getTimerService();
        Timer t = ts.createSingleActionTimer(duration, 
                new TimerConfig("createTimerAndCancelAndRollback" + duration, false));
        t.cancel();
        if( isBMT ) {
            context_.getUserTransaction().rollback();
        } else {
            context_.setRollbackOnly();
        }
    }

    public void cancelTimerNoError(Timer timer) throws Exception {
        cancelTimer(timer, false);
    }

    public void cancelTimer(Timer timer) throws Exception {
        cancelTimer(timer, true);
    }

    private void cancelTimer(Timer timer, boolean throwError) throws Exception {
        if( isBMT ) {
            context_.getUserTransaction().begin();
        }
        try {
            timer.cancel();
        } catch(Exception e) {
            if( throwError ) {
                throw new RemoteException("", e);
            }
        } finally {
            if( isBMT ) {
                context_.getUserTransaction().commit();
            }
        }
    }

    public void cancelTimerAndCancel(Timer timer) throws Exception {
        if( isBMT ) {
            context_.getUserTransaction().begin();
        }
        timer.cancel();
        timer.cancel();
        if( isBMT ) {
            context_.getUserTransaction().commit();
        }
    }
    
    public void cancelTimerAndRollback(Timer timer) 
        throws Exception {
        if( isTxUnspecified() ) {
            return;
        } else if( isBMT ) {
            context_.getUserTransaction().begin();
        }
        timer.cancel();
        if( isBMT ) {
            context_.getUserTransaction().rollback();
        } else {
            context_.setRollbackOnly();
        }
    }

    public void cancelTimerAndCancelAndRollback(Timer timer) 
        throws Exception {
        if( isTxUnspecified() ) {
            return;
        } else if( isBMT ) {
            context_.getUserTransaction().begin();
        }
        timer.cancel();
        timer.cancel();
        if( isBMT ) {
            context_.getUserTransaction().rollback();
        } else {
            context_.setRollbackOnly();
        }
    }

    private void printTimers(Collection timers) {
        System.out.println("printTimers:" + timers.size());
        int i = 0;
        for(Iterator iter = timers.iterator(); iter.hasNext();) {
            Timer t = (Timer) iter.next();
            System.out.println("timer element " + i);
            System.out.println("info = " + t.getInfo());
            System.out.println("next timeout = " + t.getNextTimeout());
            System.out.println("time remaining = " + t.getTimeRemaining());
            i++;
        }
    }

    public void getTimersTest() throws Exception {
        if( isBMT ) {
            context_.getUserTransaction().begin();
        }
        TimerService ts = context_.getTimerService();
        Collection timers1= ts.getTimers();
        printTimers(timers1);
        Timer t = ts.createSingleActionTimer(1000000, new TimerConfig("getTimersTest", false));
        Collection timers2 = ts.getTimers();
        printTimers(timers2);
        t.cancel();
        Collection timers3 = ts.getTimers();
        printTimers(timers3);
        if( isBMT ) {
            context_.getUserTransaction().commit();
        }
        if( (timers1.size() == timers3.size()) &&
            (timers2.size() == (timers1.size() + 1)) ) {
            // success
        } else {
            throw new RemoteException("getTimers failure");
        }                                   
    }

    public Timer getTimeRemainingTest1(int numIterations) throws Exception {
        if( isBMT ) {
            context_.getUserTransaction().begin();
        }
        TimerService ts = context_.getTimerService();
        Timer t = ts.createIntervalTimer(1, 1, 
                new TimerConfig("getTimeRemainingTest1", false));
        System.out.println("Remaining times for " + t.getInfo());
        for(int i = 0; i < numIterations; i++) {
            long timeRemaining = t.getTimeRemaining();
            System.out.println("Time remaining = " + timeRemaining);
            try { Thread.sleep(100); } catch(Exception e) {};
        }
        if( isBMT ) {
            context_.getUserTransaction().commit();
        }
        return t;
    }

    public void getTimeRemainingTest2(int numIterations, Timer t) throws Exception {

        System.out.println("Remaining times for " + t.getInfo());
        for(int i = 0; i < numIterations; i++) {
            long timeRemaining = t.getTimeRemaining();
            System.out.println("Time remaining = " + timeRemaining);
            try { Thread.sleep(100); } catch(Exception e) {};
        }
    }

    public Timer getNextTimeoutTest1(int numIterations) throws Exception {
        if( isBMT ) {
            context_.getUserTransaction().begin();
        }
        TimerService ts = context_.getTimerService();
        Timer t = ts.createIntervalTimer(1, 1, 
                new TimerConfig("getNextTimeoutTest1", false));
        System.out.println("Remaining times for " + t.getInfo());
        for(int i = 0; i < numIterations; i++) {
            Date nextTimeout = t.getNextTimeout();
            System.out.println("Next timeout = " + nextTimeout);
            try { Thread.sleep(100); } catch(Exception e) {};
        }
        if( isBMT ) {
            context_.getUserTransaction().commit();
        }
        return t;
    }

    public void getNextTimeoutTest2(int numIterations, Timer t) throws Exception {

        System.out.println("Remaining times for " + t.getInfo());
        for(int i = 0; i < numIterations; i++) {
            Date nextTimeout = t.getNextTimeout();
            System.out.println("Next timeout = " + nextTimeout);
            try { Thread.sleep(100); } catch(Exception e) {};
        }
    }

    // Make sure there are no active timers.  
    public void assertNoTimers() throws Exception {
        TimerService ts = context_.getTimerService();
        Collection timers= ts.getTimers();
        if(!timers.isEmpty()) {
            throw new RemoteException(timers.size() + " timers still exist");
        }
    }

    public void distributedTimerTestSetup(String id, String owner) {

        try {
                      
            Date now = new Date();
            String timerId = "dtttimer_" + now + id;
            Method testCreateMethod = com.sun.ejb.containers.TimerBean.class.getMethod("testCreate", new Class[] { String.class, EJBContext.class, String.class,
                                                                                                                   Date.class, Long.TYPE, Serializable.class });
            testCreateMethod.invoke(null, new Object[] {
                timerId, context_, owner, 
                    new Date( now.getTime() + 10000000), new Long(0),
                    "otherservertimer1" });              
        } catch(Exception e) {
            e.printStackTrace();
            throw new EJBException(e);
        }                         
    }

    public void distributedTimerTestCleanup() {
        System.out.println("In distributedTimerTestCleanup");
        try {
            TimerService ts = context_.getTimerService();
            Collection timers = ts.getTimers();
            for(Iterator iter = timers.iterator(); iter.hasNext();) {
                Timer t = (Timer) iter.next();
                System.out.println("Cancelling timer " + t + " , " + 
                                   "next timeout=" + 
                                   t.getNextTimeout() + " , " + 
                                   "info=" + t.getInfo());
                t.cancel();
            }
        } catch(Exception e) {
            e.printStackTrace();
            throw new EJBException(e);
        }         
        

    }

    // NOTE : distributedTimerTestSetup() must have been called by client
    // *before* this method.
    public void distributedTimerGetTimers() throws Exception {
        if( isBMT ) {
            context_.getUserTransaction().begin();
        }

        TimerService ts = context_.getTimerService();

        Collection timers1 = ts.getTimers();
        System.out.println("Only other timers initial");
        printTimers(timers1);
        
        Timer t = ts.createSingleActionTimer(1000000, 
                new TimerConfig("DistributedTimerGetTimers", false));
        Collection timers2 = ts.getTimers();
        System.out.println("Other timers plus one new timer");
        printTimers(timers2);

        t.cancel();
        timerAfterCancelTest(t);

        System.out.println("Only other timers ... again");
        Collection timers3 = ts.getTimers();

        printTimers(timers3);

        Timer otherTimer = (Timer) timers3.iterator().next();
/** ????
        Timer otherTimer = otherTimer.getHandle();
        Timer otherTimerFromHandle = otherTimerHandle.getTimer();
        
        if( !otherTimer.equals(otherTimerFromHandle) ) {
            throw new EJBException("timers not equal");
        }        
??? **/

        otherTimer.cancel();
        timerAfterCancelTest(otherTimer);

        Collection timers4 = ts.getTimers();
        System.out.println("(Only other timers) - 1");
        printTimers(timers4);

        if( isBMT ) {
            context_.getUserTransaction().rollback();
        }
        if( (timers1.size() > 0) &&
            (timers1.size() == timers3.size()) &&
            (timers2.size() == (timers1.size() + 1)) &&
            (timers4.size() == (timers1.size() - 1)) ) {
            // success
        } else {
            throw new EJBException("getTimers failure :\n" + 
               "timers1.size = " + timers1.size() + 
               "timers2.size = " + timers2.size() +
               "timers3.size = " + timers3.size() + 
               "timers4.size = " + timers4.size());
        }          
        
    }

    public void migrateTimersFrom(String from) {

        try {
                      
            Method testMigrateMethod = com.sun.ejb.containers.TimerBean.class.getMethod("testMigrate", new Class[] { String.class });
            testMigrateMethod.invoke(null, new Object[] { from });
        } catch(Exception e) {
            e.printStackTrace();
            throw new EJBException(e);
        }                         
    }

    public void assertTimerNotActive(Timer timer) 
        throws RemoteException {
        try {
            timer.getTimeRemaining();
            throw 
                new Exception("assertTimerNotActive called with active timer");
        } catch(NoSuchObjectLocalException nsole) {
            // caught expected exception
        } catch(Throwable t) {
            throw new RemoteException("caught wrong exception", t);
        }
    }

    public Serializable getInfoNoError(Timer timer) 
        throws Exception {
        return getInfo(timer, false);
        
    }

    public Serializable getInfo(Timer timer) throws Exception {
        return getInfo(timer, true);
    }

    private void timerAfterCancelTest(Timer t) {

        try {
            t.cancel();
            throw new EJBException("timer " + t + " should have thrown " +
                " an exception since it was accessed after having been " +
                                   "cancelled");
        } catch(NoSuchObjectLocalException fe) {
            System.out.println("Successfully got exception after accessing " +
                               "cancelled timer " + t);
        }

        try {
            t.getInfo();
            throw new EJBException("timer " + t + " should have thrown " +
                " an exception since it was accessed after having been " +
                                   "cancelled");
        } catch(NoSuchObjectLocalException fe) {
            System.out.println("Successfully got exception after accessing " +
                               "cancelled timer " + t);
        }

        try {
            t.getNextTimeout();
            throw new EJBException("timer " + t + " should have thrown " +
                " an exception since it was accessed after having been " +
                                   "cancelled");
        } catch(NoSuchObjectLocalException fe) {
            System.out.println("Successfully got exception after accessing " +
                               "cancelled timer " + t);
        }

        try {
            t.getTimeRemaining();
            throw new EJBException("timer " + t + " should have thrown " +
                " an exception since it was accessed after having been " +
                                   "cancelled");
        } catch(NoSuchObjectLocalException fe) {
            System.out.println("Successfully got exception after accessing " +
                               "cancelled timer " + t);
        }

    }

    private Serializable getInfo(Timer timer, boolean throwError) throws Exception {
        try {
            return timer.getInfo();
        } catch(Exception e) {
            if( throwError ) {
                throw e;
            }
        }
        return null;
    }

    protected void getTimerService(String method, boolean allowed) {
        try {
            TimerService ts = context_.getTimerService();
            if( !allowed ) {
                throw new EJBException("Error : getTimerService should have " +
                                       "failed in " + method);
            }
        } catch(IllegalStateException e) {
            if( allowed ) {
                throw new EJBException("Error : getTimerService should have " +
                                       "succeeded in " + method);
            }
        }
    }

    protected void doTimerStuff(String method, boolean allowed) {
        boolean txStarted = false;
        try {
            if( isTxUnspecified() ) {
                return;
            }
            TimerService ts = context_.getTimerService();
            if( isBMT ) {
                context_.getUserTransaction().begin();
                txStarted = true;
            }
            Timer t = ts.createIntervalTimer(1, 1, 
                    new TimerConfig("doTimerStuff_" + method, false));
            t.cancel();
            if( !allowed ) {
                throw new EJBException("Error : doTimerStuff should have " +
                                       "failed in " + method);
            }
        } catch(IllegalStateException ise) {
            if( allowed ) {
                throw new EJBException("Error : doTimerStuff should have " +
                                       "succeeded in " + method);
            }
        } catch(EJBException ejbe) {
            throw ejbe;
        } catch(Exception e) {
            e.printStackTrace();
            throw new EJBException("Caught unexpected exception in " +
                                   " doTimerStuff " + method);
        } finally {
            if( txStarted ) {
                try {
                    context_.getUserTransaction().commit();
                } catch(Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    protected void handleTimeout(Timer t) throws Exception {
        Serializable info = t.getInfo();

        if (t.isPersistent()) {
            throw new EJBException("Error : Timer: " + info + " is persistent");
        }
        String infoString = info.toString();
        if( infoString.startsWith("cancelTimer") ) {
            try {
                Method m = this.getClass().getMethod
                    (infoString, new Class[] {Timer.class});
                System.out.println("Invoking " + infoString + " in handleTimeout");
                m.invoke(this, new Object[] { t });
            } catch(InvocationTargetException ite) {
                throw new Exception(ite.getCause());
            } catch(Exception e) {
                throw e;
            }
        } else if( infoString.startsWith("RuntimeException") ) {
            System.out.println("Causing runtime exception from Timeout");
            throw new RuntimeException("force ejbtimeout delivery");
        } else if( infoString.startsWith("setRollbackOnly") ) {
            if( !isBMT ) {
                System.out.println("setRollbackOnly from Timeout");
                context_.setRollbackOnly();
            }
        } else {
            try {
                t.getHandle();
                throw new EJBException("Error : Timer.getHandle should have " +
                                       "failed for non-persistent timer: " + info);
            } catch(IllegalStateException e) { }
            getTimerService("Timeout", true);
            doTimerStuff("Timeout", true);
        } 
    }

}
