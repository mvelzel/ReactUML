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
    	String label = labelTag != null ? labelTag.getValueAsString() : this.getDefinition().getName();

        this.appendFullText("class SelectForm extends Form {\n" +
	        "    constructor(){\n" +
	    	"        super();\n" +
	        "        this.name = '" + this.getDefinition().getName() + "SelectForm';\n" +
		    "        this.title = '" + label + " selecteren';\n" +
		    "        this.fields = {\n" +
	        "            id: new class extends FormField.Select { constructor(){ super(); this.name = 'id'; this.sequence = '01'; this.attribute = Attribute.SelectionAttribute({ name: 'id', options: spec.options || [], label: '', defaultFieldType: FormField.Select, help: 'Maak een selectie...' }); this.width = '12'; this.offset = '0'; this.getFocus = true;} }()\n" +
	        "        };\n" +
	        "        delete this.buttons.remove;\n" +
	        "    }\n" +
	        "}\n");
    }
}
