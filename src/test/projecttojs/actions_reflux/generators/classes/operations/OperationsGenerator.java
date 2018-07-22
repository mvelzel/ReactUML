package test.projecttojs.actions_reflux.generators.classes.operations;

import com.vp.plugin.model.IOperation;
import com.vp.plugin.model.IParameter;
import test.projecttojs.actions_reflux.ClassDefinition;
import test.projecttojs.actions_reflux.Helpers;
import test.projecttojs.actions_reflux.generators.DefaultSingleGenerator;
import test.projecttojs.actions_reflux.generators.Generator;

import java.util.Arrays;
import java.util.stream.Collectors;

public class OperationsGenerator extends DefaultSingleGenerator implements Generator {
    public OperationsGenerator(ClassDefinition definition){
        super(definition);
    }

    @Override
    public void generateFullText() {
        for (IOperation operation : this.getDefinition().getOperations()) {
            String parameterCode = Arrays.stream(operation.toParameterArray()).map(IParameter::getName).collect(Collectors.joining(", "));
            this.appendFullText("    " + operation.getName() + "(" + parameterCode + ") {\n" +
                    Helpers.getOperationCode(operation, "        ") + "\n" +
                    "    };\n");
        }
    }
}
