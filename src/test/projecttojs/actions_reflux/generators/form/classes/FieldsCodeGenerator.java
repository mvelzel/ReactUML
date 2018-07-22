package test.projecttojs.actions_reflux.generators.form.classes;

import com.vp.plugin.model.*;
import test.projecttojs.actions_reflux.ClassDefinition;
import test.projecttojs.actions_reflux.Helpers;
import test.projecttojs.actions_reflux.generators.DefaultSingleGenerator;
import test.projecttojs.actions_reflux.generators.Generator;

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
        ITaggedValue sequenceTag = Helpers.getFromElementList(Arrays.asList(attribute.getTaggedValues().toTaggedValueArray()),
                ITaggedValue::getName,
                n -> n.equals("sequence"));
        String sequence = sequenceTag != null ? sequenceTag.getValueAsString() : "";

        ITaggedValue widthTag = Helpers.getFromElementList(Arrays.asList(attribute.getTaggedValues().toTaggedValueArray()),
                ITaggedValue::getName,
                n -> n.equals("width"));
        int width = widthTag != null ? Integer.parseInt(widthTag.getValueAsString()) : 12;

        ITaggedValue offsetTag = Helpers.getFromElementList(Arrays.asList(attribute.getTaggedValues().toTaggedValueArray()),
                ITaggedValue::getName,
                n -> n.equals("offset"));
        int offset = offsetTag != null ? Integer.parseInt(offsetTag.getValueAsString()) : 0;

        ITaggedValue getFocusTag = Helpers.getFromElementList(Arrays.asList(attribute.getTaggedValues().toTaggedValueArray()),
                ITaggedValue::getName,
                n -> n.equals("getFocus"));
        String getFocus = getFocusTag != null ? getFocusTag.getValueAsString() : "false";

        ClassDefinition attributeType = new ClassDefinition(attribute.getTypeAsElement().getId(), true);
        IAttribute defaultFieldType = attributeType.getAttributes() != null ?
                Helpers.getFromElementList(attributeType.getAttributes(), IAttribute::getName, n -> n.equals("defaultFieldType")) :
                null;

//        if (defaultFieldType != null && defaultFieldType.getInitialValue() != null) {
            return "            " + attribute.getName() + ": new class extends FormFieldList(" + ((IClass) attribute.getTypeAsModel()).getAttributeByName("type").getInitialValue() + ") { constructor(){ super(); this.sequence = '" + sequence + "'; this.attribute = new " + this.getDefinition().getName() + "().attributes." + attribute.getName() + "; this.width = '" + width + "'; this.offset = '" + offset + "'; this.getFocus = " + getFocus + ";} }()";
//        }
//        else {
//            Helpers.error("Attribute type has no defaultFieldType");
//            return null;
//        }
    }

    private String fieldFromAssociation(IAssociation association){
        IAssociationEnd thisEnd = Helpers.getAssociationEnd(association, this.getDefinition(), false);
        IAssociationEnd thatEnd = Helpers.getAssociationEnd(association, this.getDefinition(), true);

        ITaggedValue sequenceTag = Helpers.getFromElementList(Arrays.asList(thisEnd.getTaggedValues().toTaggedValueArray()),
                ITaggedValue::getName,
                n -> n.equals("sequence"));
        String sequence = sequenceTag != null ? sequenceTag.getValueAsString() : "";

        ITaggedValue widthTag = Helpers.getFromElementList(Arrays.asList(thisEnd.getTaggedValues().toTaggedValueArray()),
                ITaggedValue::getName,
                n -> n.equals("width"));
        int width = widthTag != null ? Integer.parseInt(widthTag.getValueAsString()) : 12;

        ITaggedValue offsetTag = Helpers.getFromElementList(Arrays.asList(thisEnd.getTaggedValues().toTaggedValueArray()),
                ITaggedValue::getName,
                n -> n.equals("offset"));
        int offset = offsetTag != null ? Integer.parseInt(offsetTag.getValueAsString()) : 0;

        ITaggedValue getFocusTag = Helpers.getFromElementList(Arrays.asList(thisEnd.getTaggedValues().toTaggedValueArray()),
                ITaggedValue::getName,
                n -> n.equals("getFocus"));
        String getFocus = getFocusTag != null ? getFocusTag.getValueAsString() : "false";

        ITaggedValue optionsTag = Helpers.getFromElementList(Arrays.asList(thatEnd.getTaggedValues().toTaggedValueArray()),
                ITaggedValue::getName,
                n -> n.equals("options"));
        String options = "";
        if (optionsTag != null) {
            ITaggedValue filterTag = Helpers.getFromElementList(Arrays.asList(thatEnd.getTaggedValues().toTaggedValueArray()),
                    ITaggedValue::getName,
                    n -> n.equals("filter"));
            options = filterTag != null ?
                    "_.filter(" + this.findOptions(optionsTag.getValueAsString()) + ", function(option) { return " + filterTag.getValueAsString() + ";})" :
                    this.findOptions(optionsTag.getValueAsString());
        }

        String fieldType = "SelectAssociation";
		if(thatEnd.getMultiplicity().endsWith("*"))
			fieldType = "MultiSelectAssociation";

	    return "            " + thisEnd.getName() + ": new class extends FormFieldList('" + fieldType + "') { constructor(){ super(); this.sequence = '" + sequence + "'; this.attribute = " + this.getDefinition().getName() + "().associations." + thisEnd.getName() + "; this.width = '" + width + "'; this.offset = '" + offset + "'; this.getFocus = " + getFocus + "; this.options = " + options + ";} }()";
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
