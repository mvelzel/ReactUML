package test.projecttojs.actions.generators.action.operations;

import com.vp.plugin.model.IOperation;
import test.projecttojs.actions.ClassDefinition;
import test.projecttojs.actions.Helpers;
import test.projecttojs.actions.generators.DefaultSingleGenerator;
import test.projecttojs.actions.generators.Generator;

public class LoadItemGenerator extends DefaultSingleGenerator implements Generator {
    public LoadItemGenerator(ClassDefinition definition){
        super(definition);
    }

    @Override
    public void generateFullText() {
        IOperation initOperation = Helpers.getFromElementList(this.getDefinition().getOperations(), o -> o.getName(), n -> n.equals("LoadItem"));
        boolean customOperation = initOperation != null && initOperation.getScope().equals("classifier");
        if (customOperation) {
            this.appendFullText("function LoadItem(id) {\n");
            this.appendFullText(Helpers.getOperationCode(initOperation, "    "));
            this.appendFullText("\n}\n");
        } else {
            this.appendFullText("function LoadItem(id) {\n" +
                "    var Entity = new " + this.getDefinition().getName() + ";\n" +
                "    Entity.syncInstance(" + this.getDefinition().getName() + ", id, function(item) {\n" +
                "        ItemLoaded(item);\n" +
                "    });\n" +
                "}\n");
        }
    }
}
