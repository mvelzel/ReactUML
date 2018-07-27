package test.projecttojs.actions.generators.controller.operations;


import test.projecttojs.actions.ClassDefinition;
import test.projecttojs.actions.generators.DefaultSingleGenerator;
import test.projecttojs.actions.generators.Generator;

public class UpdateOperationGenerator extends DefaultSingleGenerator implements Generator {
    public UpdateOperationGenerator(ClassDefinition definition) {
        super(definition);
    }

    @Override
    public void generateFullText() {
        this.appendFullText("let UpdateController = function(contextObject) {\n" +
                "    function openUpdateForm(loadedEntity, user) {\n" +
                "        var form = new Form.Update(contextObject).open(loadedEntity.getValues());\n" +
                "        $.when(form).done(function(formData) {\n" +
                "            if (formData) {\n" +
                "                switch(formData.action) {\n" +
                "                    case 'save':\n" +
                "                        loadedEntity.setValues(formData.values);\n" +
                "                        ActionList." + this.getDefinition().getName() + ".Update(loadedEntity, formData.values);\n" +
                "                        break;\n" +
                "                    case 'delete':\n" +
                "                        ActionList." + this.getDefinition().getName() + ".Delete(loadedEntity);\n" +
                "                        break;\n" +
                "                }\n" +
                "            }\n" +
                "        });\n" +
                "    }\n" +
                "    var entity = contextObject.entity || contextObject;\n" +
                "    if (entity.loadStatus === 'proxy') {\n" +
                "        entity.loadInstance(" + this.getDefinition().getName() + ", entity.id, function(loadedEntity) {\n" +
                "            openUpdateForm(loadedEntity, contextObject.user);\n" +
                "        }, contextObject.user);\n" +
                "    } else {\n" +
                "        openUpdateForm(entity, contextObject.user);\n" +
                "    }\n" +
                "};\n");
    }
}
