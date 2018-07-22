package test.projecttojs.actions.generators.domainentity.constructor;

import com.vp.plugin.model.IAttribute;
import com.vp.plugin.model.ITaggedValue;
import test.projecttojs.actions.ClassDefinition;
import test.projecttojs.actions.Helpers;
import test.projecttojs.actions.generators.Generator;
import test.projecttojs.actions.generators.DefaultSingleGenerator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class AttributesGenerator extends DefaultSingleGenerator implements Generator {
    public AttributesGenerator(ClassDefinition definition){
        super(definition);
    }

    @Override
    public void generateFullText() {
        List<String> attributeCodes = new ArrayList<>();
        for (IAttribute attribute : this.getDefinition().getAttributes()){
            String indent = "                        ";

            ITaggedValue labelTag = attribute.getTaggedValues() != null ? Helpers.getFromElementList(Arrays.asList(attribute.getTaggedValues().toTaggedValueArray()), ITaggedValue::getName, s -> s.equals("label")) : null;
            String label = labelTag != null ? labelTag.getValueAsString() : attribute.getName();

            String initialValue = attribute.getInitialValue();
            String defaultValue = initialValue != null && !initialValue.equals("$attribute.getInitialValue().getName()") ? initialValue : "null";

            ITaggedValue optionsTag = attribute.getTaggedValues() != null ? Helpers.getFromElementList(Arrays.asList(attribute.getTaggedValues().toTaggedValueArray()), ITaggedValue::getName, s -> s.equals("options")) : null;
            String options = optionsTag != null ? optionsTag.getValueAsString() : "null";

            ClassDefinition attributeClass = new ClassDefinition(attribute.getTypeAsModel().getId(), false);

            String chartCode = Arrays.stream(attribute.toStereotypeArray())
                    .filter(s -> s.contains("chart-"))
                    .map(s -> "                        " + s.split("-")[1] + ": true")
                    .collect(Collectors.joining(",\n"));

            attributeCodes.add("            " + attribute.getName() + ": new class extends " + attributeClass.getName() + " {\n" +
                    "                constructor() {\n" +
                    "                    super();\n" +
                    "                    this.name = '" + attribute.getName() + "';\n" +
                    "                    this.label = '" + label + "';\n" +
                    "                    this.description = '" + attribute.getDescription() + "';\n" +
                    "                    this.defaultValue = " + defaultValue + ";\n" +
                    "                    this.options = " + options + ";\n" +
                    "                    this.chartParameters = {\n" +
                    chartCode +
                    "\n                    };\n" +
                    "                    this.visibility = '" + Helpers.capitalizeFirstLetter(attribute.getVisibility()) + "';\n" +
                    "                }\n" +
                    "            }()");
        }
        if (Helpers.stringExistsInIterator(this.getDefinition().getStereotypes().iterator(), "timestamp")){
            attributeCodes.add("            timestampCreate: new class extends Attribute.DateTimeAttribute {\n" +
					"                constructor(){\n" +
			        "                    super();\n" +
			        "                    this.name = 'timestampCreate';\n" +
			        "                    this.label = 'Tijdstip van ontstaan';\n" +
			        "                    this.defaultValue = '';\n" +
			        "                    this.visibility = 'Private';\n" +
			        "                }\n" +
			        "            }()");
            attributeCodes.add("            timestampLastUpdate: new class extends Attribute.DateTimeAttribute {\n" +
					"                constructor(){\n" +
			        "                    super();\n" +
			        "                    this.name = 'timestampLastUpdate';\n" +
			        "                    this.label = 'Tijdstip van laatste wijziging';\n" +
			        "                    this.defaultValue = '';\n" +
			        "                    this.visibility = 'Private';\n" +
			        "                }\n" +
			        "            }()");
        }
        this.appendFullText(attributeCodes.stream().collect(Collectors.joining(",\n")) + "\n");
    }
}
