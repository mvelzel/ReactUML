package test.projecttojs.actions.generators.reactcomponent.render;

import com.vp.plugin.model.IAttribute;
import test.projecttojs.actions.ClassDefinition;
import test.projecttojs.actions.generators.Generator;
import test.projecttojs.actions.Helpers;
import test.projecttojs.actions.generators.DefaultSingleGenerator;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

public class PreConditionGenerator extends DefaultSingleGenerator implements Generator {
    public PreConditionGenerator(ClassDefinition definition){
        super(definition);
    }

    @Override
    public void generateFullText() {
        List<String> preConditions = new ArrayList<String>();

        for (Iterator<IAttribute> iter = this.getDefinition().getAttributes().iterator(); iter.hasNext(); ) {
            IAttribute attribute = iter.next();
            if (attribute.getMultiplicity().startsWith("1")) {
                if (attribute.getMultiplicity().endsWith("*"))
                    preConditions.add(Helpers.findPropValue(attribute.getName(), this.getDefinition()) + ".length > 0");
                else
                    preConditions.add(Helpers.findPropValue(attribute.getName(), this.getDefinition()));
            }
        }

        this.appendFullText(preConditions.stream().collect(Collectors.joining(" && ")));
    }
}
