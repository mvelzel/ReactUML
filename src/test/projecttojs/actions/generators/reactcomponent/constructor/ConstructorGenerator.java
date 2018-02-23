package test.projecttojs.actions.generators.reactcomponent.constructor;

import com.vp.plugin.model.IOperation;
import test.projecttojs.actions.ClassDefinition;
import test.projecttojs.actions.generators.Generator;
import test.projecttojs.actions.Helpers;
import test.projecttojs.actions.generators.DefaultSingleGenerator;

public class ConstructorGenerator extends DefaultSingleGenerator implements Generator {
    public ConstructorGenerator(ClassDefinition definition){
        super(definition);
    }

    @Override
    public void generateFullText(){
        DefaultPropsGenerator props = new DefaultPropsGenerator(this.getDefinition());
        props.generateFullText();
        String propsCode = props.getFullText();

        InitialStateGenerator state = new InitialStateGenerator(this.getDefinition());
        state.generateFullText();
        String stateCode = state.getFullText();

        IOperation componentWillMount = Helpers.getFromElementList(this.getDefinition().getOperations(), IOperation::getName, s -> s.equals("componentWillMount"));
        String mountCode = "";
        if(componentWillMount != null){
            mountCode += Helpers.getOperationCode(componentWillMount, "        ") + "\n";
        }

        this.appendFullText("    constructor(props) {\n" +
                "        super(props);\n" +
                stateCode +
                mountCode +
                "    };\n" +
                "    static defaultProps = {\n" +
                propsCode + "\n" +
                "    };\n");
    }
}
