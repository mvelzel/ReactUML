package test.projecttojs.actions_reflux.generators.store;

import com.vp.plugin.model.IOperation;
import com.vp.plugin.model.IParameter;
import test.projecttojs.actions_reflux.ClassDefinition;
import test.projecttojs.actions_reflux.Helpers;
import test.projecttojs.actions_reflux.generators.DefaultSingleGenerator;
import test.projecttojs.actions_reflux.generators.Generator;

import java.util.ArrayList;
import java.util.List;

public class StoreGenerator extends DefaultSingleGenerator implements Generator {
    public StoreGenerator(ClassDefinition definition) {
        super(definition);
    }

    @Override
    public void generateFullText() {
        String importsCode = "var CollectionStore = require('../../../js/3-domain/stores/meta/CollectionStore');\n" +
                "var DomainAPI = require('../domain-entity/DomainAPI');\n" +
                "import { ItemStore } from '../../../js/3-domain/stores/meta/ItemStore';\n" +
                "import { " + this.getDefinition().getName() + " } from '../domain-entity/" + this.getDefinition().getName() + "';\n" +
                "import * as _ from 'lodash';\n";

        String operationsCode = "";
        List<IOperation> classifiers = Helpers.filterElementList(this.getDefinition().getOperations(), IOperation::getScope, s -> s.equals("classifier"));
        for (IOperation operation : classifiers) {
            List<String> parameters = new ArrayList<>();
            for (IParameter parameter : operation.toParameterArray()) {
                parameters.add(parameter.getName());
            }
            String parameterCode = String.join(", ", parameters);
            String operationCode = Helpers.getOperationCode(operation, "        ");

            operationsCode += "    that." + operation.getName() + " = function(" + parameterCode + ") {\n" +
                    operationCode + "\n" +
                    "    };\n";
        }

        String APICode = "        [DomainAPI." + this.getDefinition().getName() + "Connect, 'onConnect'],\n" +
                "        [DomainAPI." + this.getDefinition().getName() + "Disconnect, 'onDisconnect'],\n" +
                "        [DomainAPI." + this.getDefinition().getName() + "CreateGenerator, 'onCreate'],\n" +
                "        [DomainAPI." + this.getDefinition().getName() + "Update, 'onUpdate'],\n" +
                "        [DomainAPI." + this.getDefinition().getName() + "Delete, 'onDelete'],\n" +
                "        [DomainAPI." + this.getDefinition().getName() + "Get, 'onGet'],\n" +
                "        [DomainAPI." + this.getDefinition().getName() + "LoadItemGenerator, 'onLoadItem'],\n" +
                "        [DomainAPI." + this.getDefinition().getName() + "Refresh, 'onRefresh']";
        if (operationsCode != "") {
            APICode += ",\n";
            List<String> APIs = new ArrayList<>();
            for (IOperation operation : classifiers) {
                APIs.add("        [DomainAPI." + this.getDefinition().getName() + operation.getName() + ", '" + operation.getName() + "']");
            }
            APICode += String.join(",\n", APIs);
        }
        APICode += "\n";

        this.appendFullText("// " + this.getDefinition().getName() + " ItemStore\n" +
                "// \n" +
                "// Generated by IC STRATEGY\n" +
                "//\n" +
                "// WARNING: Do not change this code; it will be overwritten by the next generation run!\n" +
                "//          Change the code only in the Visual Paradigm Project.\n\n" +
                importsCode + "\n" +
                "module.exports = function(spec, my) {\n" +
                "    var that;\n" +
                "    my = my || {};\n" +
                "    spec = spec || {};\n" +
                "\n" +
                "    that = ItemStore(_.extend({\n" +
                "        Entity: " + this.getDefinition().getName() + "\n" +
                "    }, spec));\n" +
                operationsCode +
                "    that.connectAPI([\n" +
                APICode +
                "    ]);\n\n" +
                "    return that;\n" +
                "};\n");
    }

    public String getFolder() {
        return "store";
    }
}
