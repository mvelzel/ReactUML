package test.projecttojs.actions_reflux.generators.classes;

import test.projecttojs.actions_reflux.ClassDefinition;
import test.projecttojs.actions_reflux.generators.DefaultSingleGenerator;
import test.projecttojs.actions_reflux.generators.Generator;

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
