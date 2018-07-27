package test.projecttojs.actions.generators.reducer;

import test.projecttojs.actions.ClassDefinition;
import test.projecttojs.actions.generators.DefaultSingleGenerator;
import test.projecttojs.actions.generators.Generator;

public class StoreGenerator extends DefaultSingleGenerator implements Generator {
    public StoreGenerator(ClassDefinition definition){
        super(definition);
    }

    @Override
    public void generateFullText() {
        this.appendFullText("import allReducers from './reducer';\n" +
                "import { initStore, getStore } from '../../js/controller/store';\n" +
                "\n" +
                "initStore(allReducers);\n" +
                "var store = getStore();\n" +
                "\n" +
                "export default store;\n");
    }

    @Override
    public String getName() {
        return "store";
    }

    @Override
    public String getFolder() {
        return "";
    }
}
