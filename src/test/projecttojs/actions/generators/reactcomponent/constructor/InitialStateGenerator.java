package test.projecttojs.actions.generators.reactcomponent.constructor;

import com.vp.plugin.model.IAttribute;
import com.vp.plugin.model.IOperation;
import test.projecttojs.actions.ClassDefinition;
import test.projecttojs.actions.generators.Generator;
import test.projecttojs.actions.Helpers;
import test.projecttojs.actions.generators.DefaultSingleGenerator;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class InitialStateGenerator extends DefaultSingleGenerator implements Generator {
    public InitialStateGenerator(ClassDefinition definition){
        super(definition);
    }

    @Override
    public void generateFullText() {
        IOperation getInitialState = Helpers.getFromElementList(this.getDefinition().getOperations(), IOperation::getName, s -> s.equals("getInitialState"));
        if (getInitialState != null) {
            this.appendFullText("        this.state = function() {\n" +
                    Helpers.getOperationCode(getInitialState, "            ") +
                    "        };");
        } else {
            List<IAttribute> stateAttributes = Helpers.filterElementList(this.getDefinition().getAttributes(), IAttribute::getVisibility, s -> !s.equals("public"));
            if (stateAttributes.size() > 0) {
                String initialStateCode = "";
                boolean firstIter = true;
                for (IAttribute stateAttribute : stateAttributes) {
                    if (!firstIter)
                        initialStateCode += ",\n";
                    String defaultValue;
                    if (stateAttribute.getInitialValue() != null && !stateAttribute.getInitialValue().equals("$attribute.getInitialValue().getName()"))
                        defaultValue = stateAttribute.getInitialValue();
                    else
                        defaultValue = null;
                    initialStateCode += "            " + stateAttribute.getName() + ": " + defaultValue;
                    firstIter = false;
                }
                this.appendFullText("        this.state = {\n" +
                        initialStateCode + "\n" +
                        "        };\n");
            }
        }
    }
}
