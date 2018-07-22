package test.projecttojs.actions_reflux;

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

    public void addThread(Runnable r) {
        if (r == null) {
            throw new IllegalArgumentException("Runnable r is null");
        }
        NotifyingThread t = new NotifyingThread(r);
        if (threads.size() >= threadMax) {
            queued.add(r);
        } else {
            t.addObserver(this);
            threads.add(t);
            t.start();
        }
    }

    public synchronized void moveQueue() {
        if (queued.size() > 0) {
            if (queued.get(0) != null) {
                this.addThread(queued.get(0));
            }
            queued.remove(0);
        }
    }

    public void join() {
        this.joining = true;
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
        this.joining = false;
    }

    public synchronized void update(Object caller, Object info) {
        if (caller instanceof NotifyingThread && !this.joining) {
            threads.remove(caller);
            this.moveQueue();
        }
    }
}
