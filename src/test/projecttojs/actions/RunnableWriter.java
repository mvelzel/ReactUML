package test.projecttojs.actions;

import test.projecttojs.actions.generators.Generator;

public class RunnableWriter implements Runnable {
    private Generator generator;
    private FileWriter writer;

    public RunnableWriter(FileWriter writer, Generator generator) {
        this.writer = writer;
        this.generator = generator;
    }

    public void run() {
        this.writer.writeGenerator(this.generator);
    }
}
