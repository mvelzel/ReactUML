package test.projecttojs.actions.generators.action;

import test.projecttojs.actions.generators.DefaultMultiGenerator;
import test.projecttojs.actions.generators.MultiGenerator;

public class ActionListEndGenerator extends DefaultMultiGenerator implements MultiGenerator {
    @Override
    public synchronized void generateFullText() {
        this.appendFullText("    " + this.getDefinition().getName() + ",\n");
    }

    @Override
    public void generateEndText() {

    }

    @Override
    public void generateBeginText() {
        this.appendFullText("\nexport {\n");
    }
}
