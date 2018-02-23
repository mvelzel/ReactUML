package test.projecttojs.actions.generators.form.classes;

import com.vp.plugin.model.ITaggedValue;
import test.projecttojs.actions.ClassDefinition;
import test.projecttojs.actions.Helpers;
import test.projecttojs.actions.generators.DefaultSingleGenerator;
import test.projecttojs.actions.generators.Generator;

public class CreateFormGenerator extends DefaultSingleGenerator implements Generator {
    public CreateFormGenerator(ClassDefinition definition) {
        super(definition);
    }

    @Override
    public void generateFullText() {
        ITaggedValue labelTag = Helpers.getFromElementList(this.getDefinition().getTaggedValues(), ITaggedValue::getName, s -> s.equals("label"));
        String label = labelTag != null ? labelTag.getValueAsString() : this.getDefinition().getName();

        FieldsCodeGenerator fieldsCodeGenerator = new FieldsCodeGenerator(this.getDefinition());
        fieldsCodeGenerator.generateFullText();
        String fieldsCode = fieldsCodeGenerator.getFullText();

        this.appendFullText("class CreateForm extends Form {\n" +
                "    constructor(){\n" +
                "        super();\n" +
                "        this.name = '" + this.getDefinition().getName() + "CreateForm';\n" +
                "        this.title = '" + label + " toevoegen';\n" +
                "        this.fields = {\n" +
                fieldsCode + "\n" +
                "        };\n" +
                "        delete this.buttons.remove;\n" +
                "    }\n" +
                "}\n");
    }
}
