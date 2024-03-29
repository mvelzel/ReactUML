package test.projecttojs.actions.generators.reducer;

import test.projecttojs.actions.generators.DefaultMultiGenerator;
import test.projecttojs.actions.generators.MultiGenerator;

public class CombineReducerGenerator extends DefaultMultiGenerator implements MultiGenerator {
    @Override
    public synchronized void generateFullText() {
        this.appendFullText("import " + this.getDefinition().getName() + " from './" + this.getDefinition().getName() + "';\n");
    }

    @Override
    public void generateEndText() {
        this.appendFullText("});\n\n" +
                "export default allReducers;\n");
    }

    @Override
    public void generateBeginText() {
        this.appendFullText("// Combine Reducers\n//\n" +
                "// Generated by IC STRATEGY\n" +
                "//\n" +
                "// WARNING: Do not change this code; it will be overwritten with the next generation run!\n" +
                "//          Change the code only in Visual Paradigm.\n//\n" +
                "import { combineReducers } from 'redux';\n" +
                "import Form from '../../../js/view/form/formReducer';\n");
    }

    @Override
    public String getFolder() {
        return "reducer";
    }

    @Override
    public String getName() {
        return "index";
    }
}
