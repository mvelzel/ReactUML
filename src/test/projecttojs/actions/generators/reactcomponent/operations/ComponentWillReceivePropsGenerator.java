package test.projecttojs.actions.generators.reactcomponent.operations;

import com.vp.plugin.model.IAttribute;
import com.vp.plugin.model.IOperation;
import com.vp.plugin.model.IParameter;
import test.projecttojs.actions.ClassDefinition;
import test.projecttojs.actions.Helpers;
import test.projecttojs.actions.generators.Generator;
import test.projecttojs.actions.generators.DefaultSingleGenerator;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ComponentWillReceivePropsGenerator extends DefaultSingleGenerator implements Generator {
    public ComponentWillReceivePropsGenerator(ClassDefinition definition){
        super(definition);
    }

    @Override
    public void generateFullText() {
        IOperation operation = Helpers.getFromElementList(this.getDefinition().getOperations(), IOperation::getName, s -> s.equals("componentWillReceiveProps"));
        if(operation != null){
            String parameters = operation.parameterCount() > 0 ? Arrays.stream(operation.toParameterArray()).map(IParameter::getName).collect(Collectors.joining(", ")) : "nextProps";
            this.appendFullText("    componentWillReceiveProps(" + parameters + ") {\n" +
                    Helpers.getOperationCode(operation, "        ") +
                    "    };\n");
        }
        else{
            List<IAttribute> loadAttributes = this.getDefinition().getAttributes().stream().filter(a -> Helpers.stringExistsInIterator(a.stereotypeIterator(), "load")).collect(Collectors.toList());
            if(loadAttributes.size() > 0) {
                this.appendFullText("    componentWillReceiveProps() {\n");
                for (IAttribute attribute : loadAttributes) {
                    this.appendFullText("        if(this.props. + " + attribute.getName() + " && this.props." + attribute.getName() + ".id && this.props." + attribute.getName() + ".loadStatus === 'proxy') {\n" +
                            "            ActionList." + new ClassDefinition(attribute.getTypeAsModel().getId(), false).getName() + ".LoadItem(this.props." + attribute.getName() + ".id);\n" +
                            "        }\n");
                }
                this.appendFullText("    };\n");
            }
        }
    }
}
