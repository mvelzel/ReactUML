package test.projecttojs.actions.generators.action;

import test.projecttojs.actions.ClassDefinition;
import test.projecttojs.actions.generators.DefaultSingleGenerator;
import test.projecttojs.actions.generators.Generator;

public class ActionGenerator extends DefaultSingleGenerator implements Generator {
    public ActionGenerator(ClassDefinition definition){
        super(definition);
    }

    @Override
    public void generateFullText() {
        this.appendFullText("import { " + this.getDefinition().getName() + " } from '../domain-entity/" + this.getDefinition().getName() + "';\n" +
                "import store from '../store';\n" +
                "\n" +
                "function Init() {}\n" +
                "\n" +
                "function LoadItem(id) {\n" +
                "    var Entity = new " + this.getDefinition().getName() + ";\n" +
                "    Entity.syncInstance(" + this.getDefinition().getName() + ", id, function(item) {\n" +
                "        ItemLoaded(item);\n" +
                "    });\n" +
                "}\n" +
                "\n" +
                "function ItemLoaded(item) {\n" +
                "    trigger(item);\n" +
                "}\n" +
                "\n" +
                "function Create(values, callback) {\n" +
                "    var Entity = new " + this.getDefinition().getName() + "();\n" +
                "    var item = Entity._new(values, null, callback);\n" +
                "    trigger(item);\n" +
                "}\n" +
                "\n" +
                "function ClearItem() {\n" +
                "    trigger(null);\n" +
                "}\n" +
                "\n" +
                "function Update(item, values, callback) {\n" +
                "    item.update(values, null, callback);\n" +
                "    trigger(item);\n" +
                "}\n" +
                "\n" +
                "function Delete(item, callback) {\n" +
                "    item.delete(null, callback);\n" +
                "    ClearItem();\n" +
                "}\n" +
                "\n" +
                "function trigger(item) {\n" +
                "    store.dispatch(function(i) {\n" +
                "        return {\n" +
                "            type: '" + this.getDefinition().getName().toUpperCase() + "_TRIGGER',\n" +
                "            payload: i\n" +
                "        }\n" +
                "    }(item));\n" +
                "}\n" +
                "\n" +
                "export {\n" +
                "    LoadItem,\n" +
                "    ItemLoaded,\n" +
                "    Create,\n" +
                "    ClearItem,\n" +
                "    Update,\n" +
                "    Delete,\n" +
                "    Init\n" +
                "}");
    }

    @Override
    public String getFolder() {
        return "action";
    }
}
