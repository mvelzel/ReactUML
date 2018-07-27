package test.projecttojs.actions.generators.form.classes;

import com.vp.plugin.model.ITaggedValue;
import test.projecttojs.actions.ClassDefinition;
import test.projecttojs.actions.Helpers;
import test.projecttojs.actions.generators.DefaultSingleGenerator;
import test.projecttojs.actions.generators.Generator;

public class SelectFormGenerator extends DefaultSingleGenerator implements Generator {
    public SelectFormGenerator(ClassDefinition definition){
        super(definition);
    }

    @Override
    public void generateFullText() {
    	ITaggedValue labelTag = Helpers.getFromElementList(this.getDefinition().getTaggedValues(), ITaggedValue::getName, n -> n.equals("label"));
    	String label = labelTag != null ? labelTag.getValueAsString() : this.getDefinition().getName().split("_")[1];

        this.appendFullText("class SelectForm extends Form {\n" +
	        "    constructor(){\n" +
	    	"        super('" + this.getDefinition().getName() + "SelectForm', 'Select " + label + "', {\n" +
	        "        });\n" +
	        "        delete this.buttons.remove;\n" +
	        "    }\n" +
	        "}\n");
    }
}
