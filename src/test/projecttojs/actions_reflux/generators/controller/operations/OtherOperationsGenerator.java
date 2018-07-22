package test.projecttojs.actions_reflux.generators.controller.operations;

import com.vp.plugin.model.IOperation;
import com.vp.plugin.model.IParameter;
import test.projecttojs.actions_reflux.ClassDefinition;
import test.projecttojs.actions_reflux.Helpers;
import test.projecttojs.actions_reflux.generators.DefaultSingleGenerator;
import test.projecttojs.actions_reflux.generators.Generator;

import java.util.Arrays;
import java.util.stream.Collectors;

public class OtherOperationsGenerator extends DefaultSingleGenerator implements Generator {
    public OtherOperationsGenerator(ClassDefinition definition){
        super(definition);
    }

    @Override
    public void generateFullText() {
        for (IOperation operation : this.getDefinition().getOperations()) {
            if (operation.getScope().equals("classifier") && operation.getName().contains("Controller")) {
                String parameters = Arrays.stream(operation.toParameterArray()).map(IParameter::getName).collect(Collectors.joining(", "));

                this.appendFullText("var " + operation.getName() + " = function(" + parameters + ") {\n" +
                        Helpers.getOperationCode(operation, "    ") + "\n" +
                        "};\n");
            }
        }
    }
}
