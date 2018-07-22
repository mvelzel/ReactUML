package test.projecttojs.actions_reflux.generators.formfield;

import test.projecttojs.actions_reflux.generators.DefaultMultiGenerator;
import test.projecttojs.actions_reflux.generators.MultiGenerator;

public class FormFieldListGenerator extends DefaultMultiGenerator implements MultiGenerator {
    @Override
    public synchronized void generateFullText() {
        this.appendFullText("import " + this.getDefinition().getName() + " from '../form-field/" + this.getDefinition().getName() + "';\n");
    }

    @Override
    public void generateEndText() {
        this.appendFullText("    }\n" +
                "};");
    }

    @Override
    public void generateBeginText() {
        this.appendFullText("// Form Field List\n//\n" +
                "// Generated by IC STRATEGY\n" +
                "//\n" +
                "// WARNING: Do not change this code; it will be overwritten with the next generation run!\n" +
                "//          Change the code only in Visual Paradigm.\n//\n");
    }

    @Override
    public String getFolder() {
        return "form-field";
    }

    @Override
    public String getName() {
        return "FormFieldList";
    }
}
