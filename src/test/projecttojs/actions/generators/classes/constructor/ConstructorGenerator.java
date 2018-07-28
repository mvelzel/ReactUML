package test.projecttojs.actions.generators.classes.constructor;

import com.vp.plugin.model.IAttribute;
import test.projecttojs.actions.ClassDefinition;
import test.projecttojs.actions.generators.DefaultSingleGenerator;
import test.projecttojs.actions.generators.Generator;

import java.util.ArrayList;
import java.util.List;

public class ConstructorGenerator extends DefaultSingleGenerator implements Generator {
    public ConstructorGenerator(ClassDefinition definition){
        super(definition);
    }

    @Override
    public void generateFullText() {
        String attributesCode = "";
        String parameterCode = "";
        boolean firstIter = true;
        for (IAttribute attribute : this.getDefinition().getAttributes()) {
            if (!firstIter)
                parameterCode += ", ";
            parameterCode += attribute.getName() + " = " + attribute.getInitialValue();
            attributesCode += "        this." + attribute.getName() + " = " + attribute.getName() + ";\n";
            firstIter = false;
        }

        this.appendFullText("    constructor(" + parameterCode + ") {\n" +
                "        super();\n" +
                attributesCode +
                "    };");
    }
}
