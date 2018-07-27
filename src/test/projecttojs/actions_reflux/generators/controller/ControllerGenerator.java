package test.projecttojs.actions_reflux.generators.controller;

import com.vp.plugin.model.IOperation;
import test.projecttojs.actions_reflux.ClassDefinition;
import test.projecttojs.actions_reflux.generators.DefaultSingleGenerator;
import test.projecttojs.actions_reflux.generators.Generator;
import test.projecttojs.actions_reflux.generators.controller.operations.CreateOperationGenerator;
import test.projecttojs.actions_reflux.generators.controller.operations.OtherOperationsGenerator;
import test.projecttojs.actions_reflux.generators.controller.operations.UpdateOperationGenerator;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ControllerGenerator extends DefaultSingleGenerator implements Generator {
    public ControllerGenerator(ClassDefinition definition) {
        super(definition);
    }

    @Override
    public void generateFullText() {
        CreateOperationGenerator createOperationGenerator = new CreateOperationGenerator(this.getDefinition());
        createOperationGenerator.generateFullText();

        UpdateOperationGenerator updateOperationGenerator = new UpdateOperationGenerator(this.getDefinition());
        updateOperationGenerator.generateFullText();

        OtherOperationsGenerator otherOperationsGenerator = new OtherOperationsGenerator(this.getDefinition());
        otherOperationsGenerator.generateFullText();

        String createOperation = createOperationGenerator.getFullText();
        String updateOperation = updateOperationGenerator.getFullText();
        String otherOperations = otherOperationsGenerator.getFullText();
        List<String> exports = new ArrayList<>();

        for(IOperation operation : this.getDefinition().getOperations()){
            if (operation.getScope().equals("controller") && operation.getName().contains("Controller")) {
                exports.add("    " + operation.getName().replace("Controller", "") + ": " + operation.getName());
            }
        }

        String exportsCode = exports.size() > 0 ? ",\n" + exports.stream().collect(Collectors.joining("\n")) + "\n" : "";

        this.appendFullText("// " + this.getDefinition().getName() + " Controllers\n" +
                "// \n" +
                "// Generated by IC STRATEGY\n" +
                "//\n" +
                "// WARNING: Do not change this code; it will be overwritten by the next generation run!\n" +
                "//          Change the code only in the Visual Paradigm Project.\n\n" +
                "import * as Form from '../form/" + this.getDefinition().getName() + "';\n" +
                "import {" + this.getDefinition().getName() + "} from '../domain-entity/" + this.getDefinition().getName() + "';\n" +
                "var DomainAPI = require('../domain-entity/DomainAPI');\n" +
                "\n" +
                createOperation +
                updateOperation +
                otherOperations +
                "module.exports = {\n" +
                "    CreateGenerator: CreateController,\n" +
                "    Update: UpdateController" +
                exportsCode +
                "}\n");
    }

    @Override
    public String getFolder() {
        return "controller";
    }
}
