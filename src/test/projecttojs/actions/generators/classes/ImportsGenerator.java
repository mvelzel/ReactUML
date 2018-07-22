package test.projecttojs.actions.generators.classes;

import test.projecttojs.actions.ClassDefinition;
import test.projecttojs.actions.generators.DefaultSingleGenerator;
import test.projecttojs.actions.generators.Generator;

public class ImportsGenerator extends DefaultSingleGenerator implements Generator {
    public ImportsGenerator(ClassDefinition definition){
        super(definition);
    }

    @Override
    public void generateFullText() {
        if (this.getDefinition().getStereotypes().contains("FormField")) {
            this.appendFullText("import FormField from '../../../js/1-presentation/services/meta/FormField';\n");
        }
    }
}
