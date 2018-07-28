package test.projecttojs.actions.generators.classes.operations;

import com.vp.plugin.model.IOperation;
import test.projecttojs.actions.ClassDefinition;
import test.projecttojs.actions.Helpers;
import test.projecttojs.actions.generators.DefaultSingleGenerator;
import test.projecttojs.actions.generators.Generator;

import java.util.Arrays;
import java.util.stream.Collectors;

public class OtherOperationsGenerator extends DefaultSingleGenerator implements Generator {
    public OtherOperationsGenerator(ClassDefinition definition) {
        super(definition);
    }

    @Override
    public void generateFullText() {
        for (IOperation operation : this.getDefinition().getOperations()) {
            String name = operation.getName();
            if (!operation.getVisibility().equals("package")) {
                if (!name.equals("render") && !name.equals("componentDidMount") && !name.equals("componentWillReceiveProps") && !name.equals("componentWillUnmount")) {
                    String parameterCode = Arrays.stream(operation.toParameterArray())
                            .map(p -> Helpers.stringExistsInIterator(operation.stereotypeIterator(), "controller")
                                    ? p.getName() + ": " + Helpers.findPropValue(p.getDefaultValueAsString(), this.getDefinition())
                                    : p.getName())
                            .collect(Collectors.joining(", "));
                    String operationCode = Helpers.getOperationCode(operation, "        ");

                    if (name.equals("componentWillMount") || name.equals("shouldComponentUpdate") || name.equals("componentWillUpdate") || name.equals("componentDidUpdate")) {
                        this.appendFullText("    " + name + "(" + parameterCode + ") {\n" +
                                operationCode +
                                "\n    };\n");
                    } else if (Helpers.stringExistsInIterator(operation.stereotypeIterator(), "controller")) {
                        String methodName = operation.getName().split("_")[1];
                        String entityName = this.getDefinition().getName().split("_")[0] + "_" + operation.getName().split("_")[0];

                        this.appendFullText("    " + name.replace("_", "") + " = () => {\n");
                        if (operationCode != null) {
                            this.appendFullText(operationCode);
                        } else {
                            this.appendFullText("        var self = this;\n" +
                                    "        " + entityName + "Controller." + methodName + "({" + parameterCode + "});");
                        }
                        this.appendFullText("\n    };\n");
                    } else {
                        this.appendFullText("    " + name + " = (" + parameterCode + ") => {\n" +
                                operationCode +
                                "\n    };\n");
                    }
                }
            }
        }
    }
}
