package test.projecttojs.actions.generators.action.operations;

import com.vp.plugin.model.IOperation;
import test.projecttojs.actions.ClassDefinition;
import test.projecttojs.actions.Helpers;
import test.projecttojs.actions.generators.DefaultSingleGenerator;
import test.projecttojs.actions.generators.Generator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class OtherOperationsGenerator extends DefaultSingleGenerator implements Generator {
    public OtherOperationsGenerator(ClassDefinition definition) {
        super(definition);
    }

    @Override
    public void generateFullText() {
        List<String> operationNames = new ArrayList<>();
        operationNames.add("ClearItem");
        operationNames.add("Create");
        operationNames.add("Delete");
        operationNames.add("Init");
        operationNames.add("ItemLoaded");
        operationNames.add("LoadItem");
        operationNames.add("Update");
        List<IOperation> filteredOperations = Helpers.filterElementList(this.getDefinition().getOperations(), o -> o.getName(), n -> !operationNames.contains(n));
        List<IOperation> classifiers = Helpers.filterElementList(filteredOperations, o -> o.getScope(), c -> c.equals("classifier"));
        for (IOperation operation : classifiers) {
            String parameterCode = Arrays.stream(operation.toParameterArray())
                    .map(p -> p.getName())
                    .collect(Collectors.joining(", "));
            this.appendFullText("function " + operation.getName() + "(" + parameterCode + ") {\n");
            this.appendFullText(Helpers.getOperationCode(operation, "    "));
            this.appendFullText("\n}\n\n");
        }
    }
}
