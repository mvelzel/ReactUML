package test.projecttojs.actions_old;

import java.util.*;

import com.vp.plugin.model.*;

public class AttributeClass {
	public static class Attribute {
		public String name = "";
		public String label = "";
		public List<IParameter> chartParameters = new ArrayList<>();
		public Object defaultValue = "";
		public Boolean mandatory = false;
		public String visibility = "protected";
		public Boolean isGroupInput = false;
		public Object value = "";
		public Object persistentValue = "";
		public Boolean hasChanged(){
			return !this.value.equals(this.persistentValue);
		}
		public String help = "";
		public FormFieldClass.FormField defaultFieldType = null;
		public void set(Object value, Boolean setPersistent){
			this.value = value;
			if(setPersistent)
				this.persistentValue = this.value;
		}
		public Boolean hasUserInput(){
			return false;
		}
		public Object get(){
			if(this.value instanceof Boolean)
				if(!(Boolean) this.value)
					return this.value;
				else
					if(this.value != null)
						return this.defaultValue;
					else
						return this.value;
			else
				if(this.value != null)
					return this.defaultValue;
				else
					return this.value;
		}
		public void reset(){
			this.value = "";
		}
		public void resetPersistentValue(){
			this.persistentValue = "";
		}
	}
	
	public static Attribute getAttribute(String attributeType){
		if(attributeType.equals("TextAttribute")){
			Attribute attribute = new Attribute();
			attribute.defaultFieldType = FormFieldClass.getFormField("Text");
			return attribute;
		}
		else if(attributeType.equals("LongTextAttribute")){
			Attribute attribute = new Attribute();
			attribute.defaultFieldType = FormFieldClass.getFormField("LongText");
			return attribute;
		}
		else if(attributeType.equals("HtmlAttribute")){
			Attribute attribute = new Attribute();
			attribute.defaultFieldType = FormFieldClass.getFormField("Editor");
			return attribute;
		}
		else if(attributeType.equals("DateTimeAttribute")){
			Attribute attribute = new Attribute();
			attribute.defaultFieldType = FormFieldClass.getFormField("Text");
			return attribute;
		}
		else if(attributeType.equals("EmailAttribute")){
			Attribute attribute = new Attribute();
			attribute.defaultFieldType = FormFieldClass.getFormField("Email");
			return attribute;
		}
		else if(attributeType.equals("NumberAttribute")){
			Attribute attribute = new Attribute();
			attribute.defaultFieldType = FormFieldClass.getFormField("Number");
			return attribute;
		}
		else if(attributeType.equals("GroupInputNumberAttribute")){
			Attribute attribute = new Attribute();
			attribute.defaultFieldType = FormFieldClass.getFormField("GroupInputRange");
			return attribute;
		}
		else if(attributeType.equals("PercentageAttribute")){
			Attribute attribute = new Attribute();
			attribute.defaultFieldType = FormFieldClass.getFormField("Number");
			return attribute;
		}
		else if(attributeType.equals("GroupInputPercentageAttribute")){
			Attribute attribute = new Attribute();
			return attribute;
		}
		else if(attributeType.equals("SelectionAttribute")){
			Attribute attribute = new Attribute();
			attribute.defaultFieldType = FormFieldClass.getFormField("Select");
			return attribute;
		}
		else if(attributeType.equals("MultipleSelectionAttribute")){
			Attribute attribute = new Attribute();
			attribute.defaultFieldType = FormFieldClass.getFormField("MultipleSelect");
			return attribute;
		}
		else if(attributeType.equals("ArrayAttribute")){
			Attribute attribute = new Attribute();
			attribute.defaultFieldType = FormFieldClass.getFormField("MultipleSelect");
			return attribute;
		}
		else if(attributeType.equals("BooleanObjectAttribute")){
			Attribute attribute = new Attribute();
			attribute.defaultFieldType = FormFieldClass.getFormField("CheckList");
			return attribute;
		}
		else if(attributeType.equals("BooleanAttribute")){
			Attribute attribute = new Attribute();
			attribute.defaultFieldType = FormFieldClass.getFormField("CheckBox");
			return attribute;
		}
		else if(attributeType.equals("ImageAttribute")){
			Attribute attribute = new Attribute();
			attribute.defaultFieldType = FormFieldClass.getFormField("Image");
			return attribute;
		}
		else if(attributeType.equals("ScriptAttribute")){
			Attribute attribute = new Attribute();
			attribute.defaultFieldType = FormFieldClass.getFormField("AceEditor");
			return attribute;
		}
		else {
			return new Attribute();
		}
	}
}
