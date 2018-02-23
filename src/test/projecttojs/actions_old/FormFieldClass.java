package test.projecttojs.actions_old;

//import java.util.*;

import com.vp.plugin.model.*;

public class FormFieldClass {
	public static class FormField {
		public int sequence = 0;
		public String type = "FormField";
		public IAttribute attribute = null;
		public Object value = "";
		public int width = 12;
		public int offset = 0;
		public Boolean newLine = false;
		public Boolean getFocus = false;
		public Boolean isInput = true;
		public void set(Object value){
			this.value = value;
		}
		public Object get(){
			return this.value;
		}
	}
	
	public static FormField getFormField(String formFieldType){
		if(formFieldType.equals("Text")){
			FormField formField = new FormField();
			formField.type = "Text";
			return formField;
		}
		else if(formFieldType.equals("LongText")){
			FormField formField = new FormField();
			formField.type = "LongText";
			return formField;
		}
		else if(formFieldType.equals("Editor")){
			FormField formField = new FormField();
			formField.type = "Editor";
			return formField;
		}
		else if(formFieldType.equals("CheckBox")){
			FormField formField = new FormField();
			formField.type = "CheckBox";
			return formField;
		}
		else if(formFieldType.equals("CheckList")){
			FormField formField = new FormField();
			formField.type = "CheckList";
			return formField;
		}
		else if(formFieldType.equals("Number")){
			FormField formField = new FormField();
			formField.type = "Number";
			return formField;
		}
		else if(formFieldType.equals("Email")){
			FormField formField = new FormField();
			formField.type = "Email";
			return formField;
		}
		else if(formFieldType.equals("Image")){
			FormField formField = new FormField();
			formField.type = "Image";
			return formField;
		}
		else if(formFieldType.equals("Select")){
			FormField formField = new FormField();
			formField.type = "Select";
			return formField;
		}	
		else if(formFieldType.equals("MultipleSelect")){
			FormField formField = new FormField();
			formField.type = "MultipleSelect";
			return formField;
		}
		else if(formFieldType.equals("SelectAssociation")){
			FormField formField = new FormField();
			formField.type = "SelectAssociation";
			return formField;
		}
		else if(formFieldType.equals("MultipleSelectAssociation")){
			FormField formField = new FormField();
			formField.type = "MultipleSelectAssociation";
			return formField;
		}
		else if(formFieldType.equals("TokenField")){
			FormField formField = new FormField();
			formField.type = "TokenField";
			return formField;
		}
		else if(formFieldType.equals("GroupInputRange")){
			FormField formField = new FormField();
			formField.type = "GroupInputRange";
			return formField;
		}
		else if(formFieldType.equals("AceEditor")){
			FormField formField = new FormField();
			formField.type = "AceEditor";
			return formField;
		}
		else{
			return new FormField();
		}
	}
}
