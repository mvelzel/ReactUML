package test.projecttojs.actions_old;

import java.awt.Component;
import java.util.*;

import com.vp.plugin.ViewManager;
import com.vp.plugin.model.*;

public class GenerateControllers extends Helpers {
	public static String generateController(String className, IClass definitionClass, ViewManager viewManager, IProject project, Component parentFrame, String namespace){
		String code = "";
		classDefinition definition = GetFullClassDefinition(definitionClass.getId(), project, viewManager, parentFrame, true, namespace);
		
		List<String> exportsCode = new ArrayList<>();
		code +=
		    "// " + definition.getName() + " Controllers\n" +
		    "// \n" +
	        "// Generated by IC STRATEGY\n" +
	        "//\n" +
	        "// WARNING: Do not change this code; it will be overwritten by the next generation run!\n" +
	        "//          Change the code only in the Visual Paradigm Project.\n\n" +
	        "import * as Form from '../form/" + definition.getName() + "';\n" +
	        "import {" + definition.getName() + "} from '../domain-entity/" + definition.getName() + "';\n" +
	        "var DomainAPI = require('../domain-entity/DomainAPI');\n" +
	        "\n";
		
		Boolean hasClassifierCreateController = false;
		for(IOperation operation : definition.operations){
			if(operation.getScope().equals("classifier") && operation.getName().equals("CreateController"))
				hasClassifierCreateController = true;
		}
		if(!hasClassifierCreateController){
			exportsCode.add("   CreateGenerator: CreateController");
			code +=
		        "var CreateController = function(contextObject) {\n" +
		        "   var form = new Form.CreateGenerator(contextObject).open();\n" +
		        "   $.when(form).done(function(formData) {\n" +
	            "       if (formData) {\n" +
	            "           switch(formData.action) {\n" +
	            "               case 'save':\n" +
	            "                   formData.values = formData.values || {};\n";
			
			for(IAssociation association : definition.fromAssociations){
				IAssociationEnd fromEnd = (IAssociationEnd) association.getFromEnd();
				code +=
		            "                   if (contextObject['" + fromEnd.getName() + "']) {\n" +
		            "                       formData.values['" + fromEnd.getName() + "'] = contextObject['" + fromEnd.getName() + "'];\n" +
		            "                   }\n";
			}
			for(IAssociation association : definition.toAssociations){
				IAssociationEnd fromEnd = (IAssociationEnd) association.getToEnd();
				code +=
		            "                   if (contextObject['" + fromEnd.getName() + "']) {\n" +
		            "                       formData.values['" + fromEnd.getName() + "'] = contextObject['" + fromEnd.getName() + "'];\n" +
		            "                   }\n";
			}
			code +=
		        "                   DomainAPI." + definition.getName() + "CreateGenerator(formData.values, contextObject.user);\n" +
		        "                   break;\n" +
		        "           }\n" +
	            "       }\n" +
	            "   });\n" +
	            "};\n";
		}
		Boolean hasClassifierUpdateController = false;
		for(IOperation operation : definition.operations){
			if(operation.getScope().equals("classifier") && operation.getName().equals("UpdateController"))
				hasClassifierUpdateController = true;
		}
		if(!hasClassifierUpdateController){
			exportsCode.add("   Update: UpdateController");
			code +=
		        "var UpdateController = function(contextObject) {\n" +
		        "   function openUpdateForm(loadedEntity, user) {\n" +
		        "       var form = new Form.Update(contextObject).open(loadedEntity.getValues());\n" +
	            "       $.when(form).done(function(formData) {\n" +
	            "           if (formData) {\n" +
	            "               switch(formData.action) {\n" +
	            "                   case 'save':\n" +
	            "                       loadedEntity.setValues(formData.values);\n" +
	            "                       DomainAPI." + definition.getName() + "Update(loadedEntity, formData.values, user);\n" +
	            "                       break;\n" +
	            "                   case 'delete':\n" +
	            "                       DomainAPI." + definition.getName() + "Delete(loadedEntity, user);\n" +
	            "                       break;\n" +
	            "               }\n" +
	            "           }\n" +
	            "       });\n" +
	            "   }\n" +
	            "   var entity = contextObject.entity || contextObject;\n" +
	            "   if (entity.loadStatus === 'proxy') {\n" +
	            "       entity.loadInstance(" + definition.getName() + ", entity.id, function(loadedEntity) {\n" +
	            "           openUpdateForm(loadedEntity, contextObject.user);\n" +
	            "       }, contextObject.user);\n" +
	            "   } else {\n" +
	            "       openUpdateForm(entity, contextObject.user);\n" +
	            "   }\n" +
	            "};\n";
		}

		for(IOperation operation : definition.operations){
			if(operation.getScope().equals("classifier") && operation.getName().indexOf("Controller") > -1){
				exportsCode.add("   " + operation.getName().replace("Controller", "") + ": " + operation.getName());
				String parameterCode = "";
				for(Iterator<?> iter = operation.parameterIterator(); iter.hasNext();){
					IParameter parameter = (IParameter) iter.next();
					parameterCode += parameter.getName();
					if(iter.hasNext())
						parameterCode += ", ";
				}
				String _code = "";
				try{
					_code = operation.getJavaDetail().getImplModel().getCode();
				}
				catch(Exception e){}
				code +=
			        "var " + operation.getName() + " = function(" + parameterCode + ") {\n" +
			        "   " + unEscapedJsonString(_code, "   ") + "\n" +
			        "};\n";
			}
		}
		
		code +=
		    "module.exports = {\n" + String.join(",\n", exportsCode) +
		    "\n};\n";
		
		
		return code;
	}
}
