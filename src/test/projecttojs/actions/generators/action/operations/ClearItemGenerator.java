package test.projecttojs.actions.generators.action.operations;

import com.vp.plugin.model.IOperation;
import test.projecttojs.actions.ClassDefinition;
import test.projecttojs.actions.Helpers;
import test.projecttojs.actions.generators.DefaultSingleGenerator;
import test.projecttojs.actions.generators.Generator;

public class ClearItemGenerator extends DefaultSingleGenerator implements Generator {
    public ClearItemGenerator(ClassDefinition definition){
        super(definition);
    }

    @Override
    public void generateFullText() {
        IOperation initOperation = Helpers.getFromElementList(this.getDefinition().getOperations(), o -> o.getName(), n -> n.equals("ClearItem"));
        boolean customOperation = initOperation != null && initOperation.getScope().equals("classifier");
        if (customOperation) {
            this.appendFullText("function ClearItem() {\n");
            this.appendFullText(Helpers.getOperationCode(initOperation, "    "));
            this.appendFullText("\n}\n");
        } else {
            this.appendFullText("function ClearItem() {\n" +
                "    trigger(null);\n" +
                "}\n");
        }
    }
}
