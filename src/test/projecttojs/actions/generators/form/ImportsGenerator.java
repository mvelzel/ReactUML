package test.projecttojs.actions.generators.form;

import test.projecttojs.actions.ClassDefinition;
import test.projecttojs.actions.generators.DefaultSingleGenerator;
import test.projecttojs.actions.generators.Generator;

public class ImportsGenerator extends DefaultSingleGenerator implements Generator {
    public ImportsGenerator(ClassDefinition definition){
        super(definition);
    }

    @Override
    public void generateFullText() {
        this.appendFullText("import Form from '../../../js/view/form/form';\n" +
                "import FormField from '../../../js/view/form/formField';\n" +
                "import " + this.getDefinition().getName() + " from '../domain-entity/" + this.getDefinition().getName() + "';\n");
    }
}
