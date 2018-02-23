package test.projecttojs.actions;

import java.util.ArrayList;
import java.util.List;

public class NotifyingThread extends Thread {
    private Runnable toRun;
    private List<Observer> observers;
    private boolean isJoining = false;

    public NotifyingThread(Runnable toRun) {
        this.toRun = toRun;
        observers = new ArrayList<>();
    }

    public void addObserver(Observer o) {
        observers.add(o);
    }

    public void removeObserver(Observer o) {
        observers.remove(o);
    }

    public void run() {
        toRun.run();
        if (!isJoining) {
            this.notifyObservers();
        }
    }

    public void boolJoin() throws InterruptedException {
        this.isJoining = true;
        super.join();
    }

    private void notifyObservers() {
        for (Observer o : this.observers) {
            o.update(this, null);
        }
    }
}
