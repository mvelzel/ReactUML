package test.projecttojs.actions.generators.reactcomponent.operations;

import com.vp.plugin.model.IAttribute;
import com.vp.plugin.model.IOperation;
import test.projecttojs.actions.ClassDefinition;
import test.projecttojs.actions.Helpers;
import test.projecttojs.actions.generators.Generator;
import test.projecttojs.actions.generators.DefaultSingleGenerator;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ComponentWillUnmountGenerator extends DefaultSingleGenerator implements Generator {
    public ComponentWillUnmountGenerator(ClassDefinition definition){
        super(definition);
    }

    @Override
    public void generateFullText() {
        IOperation operation = Helpers.getFromElementList(this.getDefinition().getOperations(), IOperation::getName, s -> s.equals("componentWillUnmount"));
        List<IAttribute> connections = Helpers.filterElementList(this.getDefinition().getAttributes(),
                c -> Arrays.asList(c.toStereotypeArray()),
                ss -> ss.contains("connect") || ss.contains("connectRoute") || ss.contains("load"));

        String errorActionCode = Helpers.stringExistsInIterator(this.getDefinition().getStereotypes().iterator(), "errors") ? "        ErrorActions.connect(this);\n" : "";
        boolean errorImplement = false;

        if(operation != null){
            this.appendFullText("    componentWillUnmount() {\n");
            for(IAttribute connection : connections){
                this.appendFullText("        DomainAPI." + new ClassDefinition(connection.getTypeAsModel().getId(), false).getName() + "Disconnect(this);\n");
            }
            this.appendFullText(errorActionCode);
            errorImplement = true;
            this.appendFullText("    };\n");
        }
        if(!errorImplement && !errorActionCode.isEmpty()){
            this.appendFullText("    componentWillUnmount() {\n" +
                    errorActionCode +
                    "    };\n");
        }
    }
}
