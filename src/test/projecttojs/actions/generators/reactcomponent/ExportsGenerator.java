package test.projecttojs.actions.generators.reactcomponent;

import com.vp.plugin.model.IAttribute;
import com.vp.plugin.model.IStereotype;
import com.vp.plugin.model.ITaggedValueDefinition;
import test.projecttojs.actions.ClassDefinition;
import test.projecttojs.actions.Helpers;
import test.projecttojs.actions.generators.DefaultSingleGenerator;
import test.projecttojs.actions.generators.Generator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ExportsGenerator extends DefaultSingleGenerator implements Generator {
    public ExportsGenerator(ClassDefinition definition){
        super(definition);
    }

    @Override
    public void generateFullText() {
        String exportsCode = "(" + this.getDefinition().getName() + ")";

        if (Helpers.stringExistsInIterator(this.getDefinition().getStereotypes().iterator(), "history") || Helpers.stringExistsInIterator(this.getDefinition().getStereotypes().iterator(), "match")) {
            exportsCode = "(withRouter" + exportsCode;
            exportsCode += ")";
        }

        List<IAttribute> connections = Helpers.filterElementList(this.getDefinition().getAttributes(),
                c -> Arrays.asList(c.toStereotypeArray()),
                ss -> ss.contains("connect") || ss.contains("connectRoute") || ss.contains("load") || ss.contains("formconnect"));

        if (connections.size() > 0) {
            exportsCode = "(connect(mapStateToProps)" + exportsCode;
            exportsCode += ")";
        }

        List<String> exportsAdded = new ArrayList<>();
        for (IStereotype stereotype : this.getDefinition().getOriginalClass().toStereotypeModelArray()) {
            if (stereotype.getTaggedValueDefinitions() != null) {
                ITaggedValueDefinition exportTag = Helpers.getFromElementList(Arrays.asList(stereotype.getTaggedValueDefinitions().toTaggedValueDefinitionArray()), ITaggedValueDefinition::getName, n -> n.equals("export"));
                if (exportTag != null && !exportsAdded.contains(exportTag.getDefaultValue())) {
                    exportsCode = "(" + exportTag.getDefaultValue() + exportsCode;
                    exportsCode += ")";
                    exportsAdded.add(exportTag.getDefaultValue());
                }
            }
        }

        this.appendFullText("export default " + exportsCode + ";");
    }
}
