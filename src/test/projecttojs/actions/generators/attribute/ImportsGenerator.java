package test.projecttojs.actions.generators.attribute;

import test.projecttojs.actions.ClassDefinition;
import test.projecttojs.actions.generators.DefaultSingleGenerator;
import test.projecttojs.actions.generators.Generator;

public class ImportsGenerator extends DefaultSingleGenerator implements Generator {
    public ImportsGenerator(ClassDefinition definition){
        super(definition);
    }

    @Override
    public void generateFullText() {
        this.appendFullText("import Attribute from '../../../js/model/attribute';\n");
    }
}
