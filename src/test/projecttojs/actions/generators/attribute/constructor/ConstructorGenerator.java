package test.projecttojs.actions.generators.attribute.constructor;

import com.vp.plugin.model.IAttribute;
import test.projecttojs.actions.ClassDefinition;
import test.projecttojs.actions.generators.DefaultSingleGenerator;
import test.projecttojs.actions.generators.Generator;

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

        this.appendFullText("    constructor(name = '', label = '', description = '', defaultValue = '', mandatory = false, visibility = 'protected', help = '', options = {}) {\n" +
                "        super(name, label, description, defaultValue, mandatory, visibility, help, options);\n" +
                attributesCode +
                "    };");
    }
}
