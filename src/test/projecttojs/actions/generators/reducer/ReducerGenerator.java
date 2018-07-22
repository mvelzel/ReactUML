package test.projecttojs.actions.generators.reducer;

import test.projecttojs.actions.ClassDefinition;
import test.projecttojs.actions.generators.DefaultSingleGenerator;
import test.projecttojs.actions.generators.Generator;

public class ReducerGenerator extends DefaultSingleGenerator implements Generator {
    public ReducerGenerator(ClassDefinition definition){
        super(definition);
    }

    @Override
    public void generateFullText() {
        this.appendFullText("export default function(state = null, action) {\n" +
                "    switch (action.type) {\n" +
                "        case '" + this.getDefinition().getName().toUpperCase() + "_TRIGGER':\n" +
                "            return action.payload;\n" +
                "        default:\n" +
                "            return state;\n" +
                "    }\n" +
                "}\n");
    }

    @Override
    public String getFolder() {
        return "reducer";
    }
}
