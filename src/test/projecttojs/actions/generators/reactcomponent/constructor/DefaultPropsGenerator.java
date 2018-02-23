package test.projecttojs.actions.generators.reactcomponent.constructor;

import com.vp.plugin.model.IAttribute;
import com.vp.plugin.model.ITaggedValue;
import test.projecttojs.actions.ClassDefinition;
import test.projecttojs.actions.generators.Generator;
import test.projecttojs.actions.generators.DefaultSingleGenerator;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class DefaultPropsGenerator extends DefaultSingleGenerator implements Generator {
    public DefaultPropsGenerator(ClassDefinition definition){
        super(definition);
    }

    @Override
    public void generateFullText() {
        List<IAttribute> propAttributes = new ArrayList<>();
        for (Iterator<IAttribute> iter = this.getDefinition().getAttributes().iterator(); iter.hasNext(); ) {
            IAttribute attribute = iter.next();
            if (attribute.getVisibility().equals("public"))
                propAttributes.add(attribute);
        }
        if (this.getDefinition().getAttributes().size() > 0) {
            ITaggedValue labelTag = null;
            if (this.getDefinition().getTaggedValues().size() > 0) {
                for (int i = 0; i < this.getDefinition().getTaggedValues().size(); i++) {
                    ITaggedValue tag = this.getDefinition().getTaggedValues().get(i);
                    if (tag.getName().equals("label")) {
                        labelTag = tag;
                        break;
                    }
                }
            }

            String label = "";
            try {
                if (labelTag != null) {
                    if (labelTag.getValueAsString() != null) {
                        label = labelTag.getValueAsString();
                        this.appendFullText("        label: " + label);
                    }
                }
            } catch (Exception e) {
            }
            boolean firstIter = true;
            for (IAttribute propAttribute : propAttributes) {
                if (!firstIter || !label.isEmpty())
                    this.appendFullText(",\n");
                String defaultValue;
                if (propAttribute.getInitialValue() != null && !propAttribute.getInitialValue().equals("$attribute.getInitialValue().getName()"))
                    defaultValue = propAttribute.getInitialValue();
                else
                    defaultValue = null;
                this.appendFullText("        " + propAttribute.getName() + ": " + defaultValue);
                firstIter = false;
            }
        }
    }
}
