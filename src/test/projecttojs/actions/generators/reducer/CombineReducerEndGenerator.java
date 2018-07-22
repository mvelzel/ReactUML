package test.projecttojs.actions.generators.reducer;

import test.projecttojs.actions.generators.DefaultMultiGenerator;
import test.projecttojs.actions.generators.MultiGenerator;

public class CombineReducerEndGenerator extends DefaultMultiGenerator implements MultiGenerator {
    @Override
    public synchronized void generateFullText() {
        this.appendFullText("    " + this.getDefinition().getName() + ": " + this.getDefinition().getName() + ",\n");
    }

    @Override
    public void generateEndText() {

    }

    @Override
    public void generateBeginText() {
        this.appendFullText("\nconst allReducers = combineReducers({\n");
    }
}
