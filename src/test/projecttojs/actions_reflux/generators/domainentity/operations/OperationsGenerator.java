package test.projecttojs.actions_reflux.generators.domainentity.operations;

import com.vp.plugin.model.IOperation;
import com.vp.plugin.model.IParameter;
import test.projecttojs.actions_reflux.ClassDefinition;
import test.projecttojs.actions_reflux.Helpers;
import test.projecttojs.actions_reflux.generators.DefaultSingleGenerator;
import test.projecttojs.actions_reflux.generators.Generator;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class OperationsGenerator extends DefaultSingleGenerator implements Generator {
    public OperationsGenerator(ClassDefinition definition){
        super(definition);
    }

    @Override
    public void generateFullText() {
        List<IOperation> instanced = this.getDefinition().getOperations().stream().filter(o -> o.getScope().equals("instance")).collect(Collectors.toList());

        for (IOperation operation : instanced){
            String parameterCode = Arrays.stream(operation.toParameterArray()).map(IParameter::getName).collect(Collectors.joining(", "));

            this.appendFullText("    " + operation.getName() + "(" + parameterCode + ") {\n" +
                    Helpers.getOperationCode(operation, "        ") + "\n" +
                    "    };\n");
        }
    }
}
