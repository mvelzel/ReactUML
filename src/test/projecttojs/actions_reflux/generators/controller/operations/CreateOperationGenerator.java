package test.projecttojs.actions_reflux.generators.controller.operations;

import com.vp.plugin.model.IOperation;
import test.projecttojs.actions_reflux.ClassDefinition;
import test.projecttojs.actions_reflux.Helpers;
import test.projecttojs.actions_reflux.generators.DefaultSingleGenerator;
import test.projecttojs.actions_reflux.generators.Generator;

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
            String formData = this.getDefinition().getAssociations().stream()
                    .map(a -> Helpers.getAssociationEnd(a, this.getDefinition(), true))
                    .map(e -> "                    if (contextObject['" + e.getName() + "']) {\n" +
                            "                        formData.values['" + e.getName() + "'] = contextObject['" + e.getName() + "'];\n" +
                            "                    }").collect(Collectors.joining("\n"));

            this.appendFullText("var CreateController = function(contextObject) {\n" +
                    "    var form = new Form.CreateGenerator(contextObject).open();\n" +
                    "    $.when(form).done(function(formData) {\n" +
                    "        if (formData) {\n" +
                    "            switch(formData.action) {\n" +
                    "                case 'save':\n" +
                    "                    formData.values = formData.values || {};\n" +
                    formData +
                    "                    \nDomainAPI." + this.getDefinition().getName() + "CreateGenerator(formData.values, contextObject.user);\n" +
                    "                    break;\n" +
                    "            }\n" +
                    "        }\n" +
                    "    });\n" +
                    "};\n");
        }
    }
}
