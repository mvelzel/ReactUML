package test.projecttojs.actions_reflux.generators.reactcomponent.operations;

import com.vp.plugin.model.IAttribute;
import com.vp.plugin.model.IOperation;
import test.projecttojs.actions_reflux.ClassDefinition;
import test.projecttojs.actions_reflux.Helpers;
import test.projecttojs.actions_reflux.generators.DefaultSingleGenerator;
import test.projecttojs.actions_reflux.generators.Generator;

import java.util.Arrays;
import java.util.List;

public class ComponentDidMountGenerator extends DefaultSingleGenerator implements Generator {
    public ComponentDidMountGenerator(ClassDefinition definition){
        super(definition);
    }

    @Override
    public void generateFullText() {
        IOperation operation = Helpers.getFromElementList(this.getDefinition().getOperations(), IOperation::getName, s -> s.equals("componentDidMount"));
        List<IAttribute> connections = Helpers.filterElementList(this.getDefinition().getAttributes(),
                c -> Arrays.asList(c.toStereotypeArray()),
                ss -> ss.contains("connect") || ss.contains("connectRoute") || ss.contains("load"));

        String errorActionCode = Helpers.stringExistsInIterator(this.getDefinition().getStereotypes().iterator(), "errors") ? "        ErrorActions.connect(this);\n" : "";
        boolean errorImplement = false;
        if(connections.size() > 0){
            this.appendFullText("    componentDidMount() {\n");
            for(IAttribute connection : connections){
                if (connection.getTypeAsModel() == null) {
                    Helpers.error("The type of " + connection.getTypeAsString() + " in attribute " + connection.getName() + " of " + this.getDefinition().getName() + " is not a model!");
                    continue;
                }
                ClassDefinition type = new ClassDefinition(connection.getTypeAsModel().getId(), false);
                this.appendFullText("        DomainAPI." + type.getName() + "Connect(this, '" + connection.getName() + "');\n");
                if(Helpers.stringExistsInIterator(connection.stereotypeIterator(), "connectRoute")){
                    this.appendFullText("        DomainAPI." + type.getName() + "LoadItemGenerator(this.props.params.id);\n");
                }
                if(Helpers.stringExistsInIterator(connection.stereotypeIterator(), "load")){
                    this.appendFullText("        if(this.props." + connection.getName() + ".id && this.props." + connection.getName() + ".loadStatus === \"proxy\") { DomainAPI." + type.getName() + "LoadItemGenerator(this.props." + connection.getName() + ".id); }\n");
                }
            }
            this.appendFullText(errorActionCode);
            errorImplement = true;
            if(operation == null)
                this.appendFullText("    };\n");
        }
        if(operation != null){
            if(connections.size() == 0)
                this.appendFullText("    componentDidMount() {\n");
            this.appendFullText(Helpers.getOperationCode(operation, "        "));
            if(!errorImplement){
                this.appendFullText(errorActionCode);
                errorImplement = true;
            }
            this.appendFullText("\n    };\n");
        }
        if(!errorImplement && !errorActionCode.isEmpty()){
            this.appendFullText("    componentDidMount() {\n" +
                    errorActionCode +
                    "    };\n");
        }
    }
}
