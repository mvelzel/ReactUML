package test.projecttojs.actions;

import java.util.ArrayList;
import java.util.List;

public class ThreadManager implements Observer {
    private int threadMax;
    private List<NotifyingThread> threads = new ArrayList<>();
    private List<Runnable> queued = new ArrayList<>();
    private boolean joining = false;

    public ThreadManager(int threadMax) {
        this.threadMax = threadMax;
    }

    public synchronized void addThread(Runnable r) {
        if (r == null) {
            throw new IllegalArgumentException("Runnable r is null");
        }
        if (!this.joining) {
            NotifyingThread t = new NotifyingThread(r);
            if (threads.size() >= threadMax) {
                queued.add(r);
            } else {
                t.addObserver(this);
                threads.add(t);
                t.start();
            }
        }
    }

    public synchronized void moveQueue() {
        if (queued.size() > 0 && !this.joining) {
            if (queued.get(0) != null) {
                this.addThread(queued.get(0));
            }
            queued.remove(0);
        }
    }

    public void join() {
        this.joining = true;
        /*
        while (threads.size() > 0) {
            NotifyingThread t = threads.get(0);
            threads.remove(t);
            if (t != null) {
                try {
                    t.boolJoin();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            this.moveQueue();
        }
        */
        for (NotifyingThread thread : threads) {
            if (thread.isAlive()) {
                try {
                    thread.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        for (int i = 0; i < queued.size(); i++) {
            Runnable t = this.queued.get(i);
            NotifyingThread th = new NotifyingThread(t);
            th.run();
        }
        Helpers.log("" + queued.size());
        this.joining = false;
    }

    public synchronized void update(Object caller, Object info) {
        if (caller instanceof NotifyingThread && !this.joining) {
            threads.remove(caller);
            this.moveQueue();
        }
    }
}
