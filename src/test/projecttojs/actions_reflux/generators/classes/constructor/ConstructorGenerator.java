package test.projecttojs.actions_reflux.generators.classes.constructor;

import com.vp.plugin.model.IAttribute;
import test.projecttojs.actions_reflux.ClassDefinition;
import test.projecttojs.actions_reflux.generators.DefaultSingleGenerator;
import test.projecttojs.actions_reflux.generators.Generator;

public class ConstructorGenerator extends DefaultSingleGenerator implements Generator {
    public ConstructorGenerator(ClassDefinition definition){
        super(definition);
    }

    @Override
    public void generateFullText() {
        String attributesCode = "";
        for (IAttribute attribute : this.getDefinition().getAttributes()) {
            attributesCode += "        this." + attribute.getName() + " = " + attribute.getInitialValue() + ";\n";
        }

        this.appendFullText("    constructor() {\n" +
                "        super();\n" +
                attributesCode +
                "    };");
    }
}
