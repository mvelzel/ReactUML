package test.projecttojs.actions.generators.action;

import test.projecttojs.actions.ClassDefinition;
import test.projecttojs.actions.generators.DefaultSingleGenerator;
import test.projecttojs.actions.generators.Generator;
import test.projecttojs.actions.generators.action.operations.*;

public class ActionGenerator extends DefaultSingleGenerator implements Generator {
    public ActionGenerator(ClassDefinition definition){
        super(definition);
    }

    @Override
    public void generateFullText() {
        ClearItemGenerator clearItemGenerator = new ClearItemGenerator(this.getDefinition());
        CreateGenerator createGenerator = new CreateGenerator(this.getDefinition());
        DeleteGenerator deleteGenerator = new DeleteGenerator(this.getDefinition());
        InitGenerator initGenerator = new InitGenerator(this.getDefinition());
        ItemLoadedGenerator itemLoadedGenerator = new ItemLoadedGenerator(this.getDefinition());
        LoadItemGenerator loadItemGenerator = new LoadItemGenerator(this.getDefinition());
        UpdateGenerator updateGenerator = new UpdateGenerator(this.getDefinition());
        OtherOperationsGenerator otherOperationsGenerator = new OtherOperationsGenerator(this.getDefinition());
        ExtraExportsGenerator extraExportsGenerator = new ExtraExportsGenerator(this.getDefinition());

        clearItemGenerator.generateFullText();
        createGenerator.generateFullText();
        deleteGenerator.generateFullText();
        initGenerator.generateFullText();
        itemLoadedGenerator.generateFullText();
        loadItemGenerator.generateFullText();
        updateGenerator.generateFullText();
        otherOperationsGenerator.generateFullText();
        extraExportsGenerator.generateFullText();

        String clearItemCode = clearItemGenerator.getFullText();
        String createCode = createGenerator.getFullText();
        String deleteCode = deleteGenerator.getFullText();
        String initCode = initGenerator.getFullText();
        String itemLoadedCode = itemLoadedGenerator.getFullText();
        String loadItemCode = loadItemGenerator.getFullText();
        String updateCode = updateGenerator.getFullText();
        String otherOperationsCode = otherOperationsGenerator.getFullText();
        String extraExportsCode = extraExportsGenerator.getFullText();

        this.appendFullText("import " + this.getDefinition().getName() + " from '../domain-entity/" + this.getDefinition().getName() + "';\n" +
                "import store from '../store';\n" +
                "\n" +
                initCode +
                "\n" +
                loadItemCode +
                "\n" +
                itemLoadedCode +
                "\n" +
                createCode +
                "\n" +
                clearItemCode +
                "\n" +
                updateCode +
                "\n" +
                deleteCode +
                "\n" +
                otherOperationsCode +
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
                extraExportsCode +
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
