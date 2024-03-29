package test.projecttojs.actions_reflux.generators.domainentity;

import test.projecttojs.actions_reflux.generators.DefaultMultiGenerator;
import test.projecttojs.actions_reflux.generators.MultiGenerator;

public class DomainEntityListGenerator extends DefaultMultiGenerator implements MultiGenerator {
    @Override
    public synchronized void generateFullText() {
        this.appendFullText("import { " + this.getDefinition().getName() + " } from '../domain-entity/" + this.getDefinition().getName() + "';\n");
    }

    @Override
    public void generateEndText() {
        this.appendFullText("    }\n" +
                "};");
    }

    @Override
    public void generateBeginText() {
        this.appendFullText("// Domain Entities List\n//\n" +
                "// Generated by IC STRATEGY\n" +
                "//\n" +
                "// WARNING: Do not change this code; it will be overwritten with the next generation run!\n" +
                "//          Change the code only in Visual Paradigm.\n//\n");
    }

    @Override
    public String getFolder() {
        return "domain-entity";
    }

    @Override
    public String getName() {
        return "DomainEntitiesList";
    }
}
