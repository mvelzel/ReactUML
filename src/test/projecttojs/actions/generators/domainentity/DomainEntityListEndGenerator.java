package test.projecttojs.actions.generators.domainentity;

import test.projecttojs.actions.generators.MultiGenerator;
import test.projecttojs.actions.generators.DefaultMultiGenerator;

public class DomainEntityListEndGenerator extends DefaultMultiGenerator implements MultiGenerator {
    @Override
    public synchronized void generateFullText() {
        this.appendFullText("        case '" + this.getDefinition().getName() + "': return " + this.getDefinition().getName() + ";\n");
    }

    @Override
    public void generateEndText() {

    }

    @Override
    public void generateBeginText() {
        this.appendFullText("module.exports = function(entityType) {\n" +
                "    switch (entityType) {\n");
    }
}
