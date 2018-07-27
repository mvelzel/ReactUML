package test.projecttojs.actions.generators.reactcomponent.operations;

import com.vp.plugin.model.IAttribute;
import com.vp.plugin.model.IOperation;
import test.projecttojs.actions.ClassDefinition;
import test.projecttojs.actions.Helpers;
import test.projecttojs.actions.generators.DefaultSingleGenerator;
import test.projecttojs.actions.generators.Generator;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ExtraOperationsGenerator extends DefaultSingleGenerator implements Generator {
    public ExtraOperationsGenerator(ClassDefinition definition) {
        super(definition);
    }

    @Override
    public void generateFullText() {
                List<IAttribute> connections = Helpers.filterElementList(this.getDefinition().getAttributes(),
                c -> Arrays.asList(c.toStereotypeArray()),
                ss -> ss.contains("connect") || ss.contains("connectRoute") || ss.contains("load") || ss.contains("formconnect"));
        String mapStateToPropsCode = "";

        if (connections.size() > 0) {
            mapStateToPropsCode += "function mapStateToProps(state) {\n" +
                    "    return {";
            boolean firstIter = true;
            for (IAttribute connection : connections) {
                if (Arrays.asList(connection.toStereotypeArray()).contains("formconnect")) {
                    if (!firstIter) {
                        mapStateToPropsCode += ",";
                    }
                    mapStateToPropsCode += "\n        form: state.Form";
                    firstIter = false;
                    continue;
                }
                ClassDefinition type = new ClassDefinition(connection.getTypeAsModel().getId(), false);

                if (!firstIter) {
                    mapStateToPropsCode += ",";
                }
                mapStateToPropsCode += "\n        " + connection.getName() + ": state." + type.getName();
                firstIter = false;
            }
            mapStateToPropsCode += "\n    }\n" +
                    "}\n";
        }
        this.appendFullText(mapStateToPropsCode);

        for (IOperation operation : this.getDefinition().getOperations()) {
            if (operation.getVisibility().equals("package")) {
                String name = operation.getName();
                String parameterCode = Arrays.stream(operation.toParameterArray())
                        .map(p -> Helpers.stringExistsInIterator(operation.stereotypeIterator(), "controller")
                                ? p.getName() + ": " + Helpers.findPropValue(p.getDefaultValueAsString(), this.getDefinition())
                                : p.getName())
                        .collect(Collectors.joining(", "));
                String operationCode = Helpers.getOperationCode(operation, "    ");

                this.appendFullText("function " + name + "(" + parameterCode + ") {\n" +
                        operationCode + "\n" +
                        "}\n");
            }
        }
        if (!this.getFullText().equals("")) {
            this.appendFullText("\n");
        }
    }
}
