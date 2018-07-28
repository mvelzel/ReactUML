package test.projecttojs.actions.generators.classes;

import com.vp.plugin.model.IStereotype;
import com.vp.plugin.model.ITaggedValueDefinition;
import test.projecttojs.actions.ClassDefinition;
import test.projecttojs.actions.Helpers;
import test.projecttojs.actions.generators.DefaultSingleGenerator;
import test.projecttojs.actions.generators.Generator;

import java.util.Arrays;

public class ImportsGenerator extends DefaultSingleGenerator implements Generator {
    public ImportsGenerator(ClassDefinition definition){
        super(definition);
    }

    @Override
    public void generateFullText() {
        for (IStereotype stereotype : this.getDefinition().getOriginalClass().toStereotypeModelArray()) {
            if (stereotype.getTaggedValueDefinitions() != null) {
                ITaggedValueDefinition importTag = Helpers.getFromElementList(Arrays.asList(stereotype.getTaggedValueDefinitions().toTaggedValueDefinitionArray()), ITaggedValueDefinition::getName, n -> n.equals("import"));
                if (importTag != null) {
                    this.appendFullText(importTag.getDefaultValue());
                }
            }
       }
    }
}
