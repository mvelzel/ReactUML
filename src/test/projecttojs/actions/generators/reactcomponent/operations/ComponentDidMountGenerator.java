package test.projecttojs.actions.generators.reactcomponent.operations;

import com.vp.plugin.model.IAttribute;
import com.vp.plugin.model.IOperation;
import com.vp.plugin.model.ITaggedValue;
import test.projecttojs.actions.ClassDefinition;
import test.projecttojs.actions.Helpers;
import test.projecttojs.actions.generators.Generator;
import test.projecttojs.actions.generators.DefaultSingleGenerator;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ComponentDidMountGenerator extends DefaultSingleGenerator implements Generator {
    public ComponentDidMountGenerator(ClassDefinition definition){
        super(definition);
    }

    @Override
    public void generateFullText() {
        IOperation operation = Helpers.getFromElementList(this.getDefinition().getOperations(), IOperation::getName, s -> s.equals("componentDidMount"));
        List<IAttribute> connections = Helpers.filterElementList(this.getDefinition().getAttributes(),
                c -> Arrays.asList(c.toStereotypeArray()),
                ss -> /*ss.contains("connect") || ss.contains("connectRoute") || */ss.contains("load"));

        String errorActionCode = Helpers.stringExistsInIterator(this.getDefinition().getStereotypes().iterator(), "errors") ? "        ErrorActions.connect(this);\n" : "";
        boolean errorImplement = false;
        if(connections.size() > 0){
            this.appendFullText("    componentDidMount() {\n");
            for(IAttribute connection : connections){
                if (connection.getTypeAsModel() == null) {
                    Helpers.error("The type of " + connection.getTypeAsString() + " in attribute " + connection.getName() + " of " + this.getDefinition().getName() + " is not a model!");
                    continue;
                }
                if (connection.getTaggedValues() == null || connection.getTaggedValues().toTaggedValueArray().length == 0) {
                    continue;
                }
                ClassDefinition type = new ClassDefinition(connection.getTypeAsModel().getId(), false);
                ITaggedValue idTag = Helpers.getFromElementList(Arrays.asList(connection.getTaggedValues().toTaggedValueArray()), ITaggedValue::getName, n -> n.equals("id"));
                this.appendFullText("        ActionList." + type.getName() + ".LoadItem(" + idTag.getValueAsString() + ");\n");
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
