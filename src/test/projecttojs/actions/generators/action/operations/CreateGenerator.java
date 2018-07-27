package test.projecttojs.actions.generators.action.operations;

import com.vp.plugin.model.IOperation;
import test.projecttojs.actions.ClassDefinition;
import test.projecttojs.actions.Helpers;
import test.projecttojs.actions.generators.DefaultSingleGenerator;
import test.projecttojs.actions.generators.Generator;

public class CreateGenerator extends DefaultSingleGenerator implements Generator {
    public CreateGenerator(ClassDefinition definition){
        super(definition);
    }

    @Override
    public void generateFullText() {
        IOperation initOperation = Helpers.getFromElementList(this.getDefinition().getOperations(), o -> o.getName(), n -> n.equals("Create"));
        boolean customOperation = initOperation != null && initOperation.getScope().equals("classifier");
        if (customOperation) {
            this.appendFullText("function Create(values, callback) {\n");
            this.appendFullText(Helpers.getOperationCode(initOperation, "    "));
            this.appendFullText("\n}\n");
        } else {
            this.appendFullText("function Create(values, callback) {\n" +
                "    var Entity = new " + this.getDefinition().getName() + "();\n" +
                "    var item = Entity._new(values, null, callback);\n" +
                "    trigger(item);\n" +
                "}\n");
        }
    }
}
