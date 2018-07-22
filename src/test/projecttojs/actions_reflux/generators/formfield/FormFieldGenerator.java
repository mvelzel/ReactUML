package test.projecttojs.actions_reflux.generators.formfield;

import test.projecttojs.actions_reflux.ClassDefinition;
import test.projecttojs.actions_reflux.generators.Generator;
import test.projecttojs.actions_reflux.generators.classes.ClassGenerator;

public class FormFieldGenerator extends ClassGenerator implements Generator {
    public FormFieldGenerator(ClassDefinition definition){
        super(definition);
    }

    @Override
    public String getFolder() {
        return "form-field";
    }
}
