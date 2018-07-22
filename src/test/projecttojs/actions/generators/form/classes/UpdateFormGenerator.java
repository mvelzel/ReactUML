package test.projecttojs.actions.generators.form.classes;

import com.vp.plugin.model.ITaggedValue;
import test.projecttojs.actions.ClassDefinition;
import test.projecttojs.actions.Helpers;
import test.projecttojs.actions.generators.DefaultSingleGenerator;
import test.projecttojs.actions.generators.Generator;

public class UpdateFormGenerator extends DefaultSingleGenerator implements Generator {
    public UpdateFormGenerator(ClassDefinition definition){
        super(definition);
    }

    @Override
    public void generateFullText() {
        ITaggedValue labelTag = Helpers.getFromElementList(this.getDefinition().getTaggedValues(), ITaggedValue::getName, s -> s.equals("label"));
        String label = labelTag != null ? labelTag.getValueAsString() : this.getDefinition().getName();

        FieldsCodeGenerator fieldsCodeGenerator = new FieldsCodeGenerator(this.getDefinition());
        fieldsCodeGenerator.generateFullText();
        String fieldsCode = fieldsCodeGenerator.getFullText();

        this.appendFullText("class UpdateForm extends Form {\n" +
                "    constructor(){\n" +
                "        super('" + this.getDefinition().getName() + "CreateForm', 'Update " + label + "', {\n" +
                fieldsCode + "\n" +
                "        });\n" +
                "    }\n" +
                "}\n");
    }
}
