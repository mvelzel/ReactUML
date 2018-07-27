package test.projecttojs.actions.generators.controller.operations;

import com.vp.plugin.model.IOperation;
import test.projecttojs.actions.ClassDefinition;
import test.projecttojs.actions.Helpers;
import test.projecttojs.actions.generators.DefaultSingleGenerator;
import test.projecttojs.actions.generators.Generator;

import java.util.stream.Collectors;

public class CreateOperationGenerator extends DefaultSingleGenerator implements Generator {
    public CreateOperationGenerator(ClassDefinition definition) {
        super(definition);
    }

    @Override
    public void generateFullText() {
        IOperation create = Helpers.getFromElementList(this.getDefinition().getOperations(), IOperation::getName, s -> s.equals("CreateController"));
        create = create != null && create.getScope().equals("classifier") ? create : null;

        if (create == null) {
            String contexts = this.getDefinition().getAssociations().stream()
                    .map(a -> Helpers.getAssociationEnd(a, this.getDefinition(), false))
                    .map(e -> "'" + e.getName() + "'").collect(Collectors.joining(", "));

            this.appendFullText("let CreateController = function(contextObject) {\n" +
                    "    var form = new Form.Create().open();\n" +
                    "    $.when(form).done(function(formData) {\n" +
                    "        if (formData) {\n" +
                    "            switch(formData.action) {\n" +
                    "                case 'save':\n" +
                    "                    formData.values = formData.values || {};\n" +
                    "                    let contexts = [" + contexts + "];\n" +
                    "                    for (var i in contexts) {\n" +
                    "                        let c = contexts[i];\n" +
                    "                        if (contextObject[c]) {\n" +
                    "                            formData.values[c] = contextObject[c];\n" +
                    "                        }\n" +
                    "                    }\n" +
                    "                    ActionList." + this.getDefinition().getName() + ".Create(formData.values);\n" +
                    "                    break;\n" +
                    "            }\n" +
                    "        }\n" +
                    "    });\n" +
                    "};\n");
        }
    }
}
