package test.projecttojs.actions_reflux.generators.domainentity;

import test.projecttojs.actions_reflux.generators.DefaultMultiGenerator;
import test.projecttojs.actions_reflux.generators.MultiGenerator;

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
