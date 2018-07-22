package test.projecttojs.actions.generators.form.classes;

import com.vp.plugin.model.*;
import test.projecttojs.actions.ClassDefinition;
import test.projecttojs.actions.Helpers;
import test.projecttojs.actions.generators.DefaultSingleGenerator;
import test.projecttojs.actions.generators.Generator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FieldsCodeGenerator extends DefaultSingleGenerator implements Generator {
    public FieldsCodeGenerator(ClassDefinition definition){
        super(definition);
    }

    @Override
    public void generateFullText() {
        List<String> fieldCode = new ArrayList<>();
        List<IAttribute> fieldCodeAttributes = new ArrayList<>();
        List<IAssociation> fieldCodeAssociations = new ArrayList<>();

        for (IAttribute a : this.getDefinition().getAttributes()) {
            for (String s : a.toStereotypeArray()) {
                if (s.equals("c") || s.equals("cu")) {
                    fieldCodeAttributes.add(a);
                }
            }
        }

        for (IAssociation a : this.getDefinition().getAssociations()) {
            IAssociationEnd thisEnd = Helpers.getAssociationEnd(a, this.getDefinition(), false);
            for (String s : thisEnd.toStereotypeArray()) {
                if (s.equals("c") || s.equals("cu")) {
                    fieldCodeAssociations.add(a);
                }
            }
        }

        for (IAttribute attribute : fieldCodeAttributes) {
            fieldCode.add(fieldFromAttribute(attribute));
        }

        for (IAssociation association : fieldCodeAssociations) {
            fieldCode.add(fieldFromAssociation(association));
        }

        this.appendFullText(String.join(",\n", fieldCode));
    }

    private String fieldFromAttribute(IAttribute attribute){
        ITaggedValue sequenceTag = Helpers.getFromElementList(Arrays.asList(attribute.getTaggedValues().toTaggedValueArray()), ITaggedValue::getName, n -> n.equals("sequence"));
        String sequence = sequenceTag != null ? sequenceTag.getValueAsString() : "0";

        return "            " + attribute.getName() + ": new FormField('" + attribute.getName() + "', " + sequence + ", Entity.attributes. " + attribute.getName() + ".defaultFieldType, Entity.attributes." + attribute.getName() + ")";
    }

    private String fieldFromAssociation(IAssociation association){
        return "";
    }

    private String findOptions(String optionsTag){
		if(optionsTag != null){
			String suffix = "";
			if(optionsTag.substring(0, 1).equals("[") && optionsTag.substring(optionsTag.length() - 1, optionsTag.length()).equals("]")){
				optionsTag = optionsTag.substring(1, optionsTag.length() - 1);
				if(optionsTag.contains(".")){
					try{
						List<String> optionsTagParts = Arrays.asList(optionsTag.split("\\."));
						optionsTag = optionsTagParts.get(0);
						suffix = ".get('" + optionsTagParts.get(1) + "')";
					}
					catch(Exception e){
						for(int i = 0; i < e.getStackTrace().length; i++){
							Helpers.error("OptionsTag error occurred in line " + e.getStackTrace()[i].getLineNumber() + " in tag " + optionsTag);
						}
					}
				}
			}
			return "spec." + optionsTag + suffix;
		}
		return "";
	}
}
