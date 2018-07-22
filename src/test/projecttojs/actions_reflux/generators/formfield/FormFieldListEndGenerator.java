package test.projecttojs.actions_reflux.generators.formfield;

import com.vp.plugin.model.IAttribute;
import test.projecttojs.actions_reflux.Helpers;
import test.projecttojs.actions_reflux.generators.DefaultMultiGenerator;
import test.projecttojs.actions_reflux.generators.MultiGenerator;

public class FormFieldListEndGenerator extends DefaultMultiGenerator implements MultiGenerator {
    @Override
    public synchronized void generateFullText() {
        this.appendFullText("        case " + Helpers.getFromElementList(this.getDefinition().getAttributes(), IAttribute::getName, n -> n.equals("type")).getInitialValue() + ": return " + this.getDefinition().getName() + ";\n");
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
