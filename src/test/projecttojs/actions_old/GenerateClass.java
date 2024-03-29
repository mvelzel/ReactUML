package test.projecttojs.actions_old;

import java.awt.Component;
import java.util.*;

import com.vp.plugin.ViewManager;
import com.vp.plugin.model.*;

public class GenerateClass extends Helpers {
	public static String generateClass(String modelClass, IProject project, ViewManager viewManager, IClass definitionClass, Component parentFrame, String namespace){
		String code = "";
		classDefinition definition = GetFullClassDefinition(definitionClass.getId(), project, viewManager, parentFrame, false, namespace);
		Boolean firstIter = true;
		
		String requireCode = "";
		String inheritsFrom = "";
		IGeneralization generalization = null;
		for(Iterator<?> iter = definitionClass.toRelationshipIterator(); iter.hasNext();){
			IRelationship relationship = (IRelationship) iter.next();
			if(relationship.getModelType().equals("Generalization"))
				generalization = (IGeneralization) relationship;
		}
		if(generalization != null)
			inheritsFrom = GetFullClassDefinition(generalization.getFrom().getId(), project, viewManager, parentFrame, false, namespace).name;
		
		if(!inheritsFrom.isEmpty())
			requireCode = "var " + inheritsFrom + " = require('./" + inheritsFrom + "');\n";
		
		String attributesCode = "";
		for(IAttribute attribute : definition.attributes){
			if(!firstIter)
				attributesCode += ",\n";
			String defaultValue = "''";
			if(attribute.getInitialValue() != null)
				if(!attribute.getInitialValue().equals("$attribute.getInitialValue().getName()"))
					defaultValue = attribute.getInitialValue();
			attributesCode +=
				"        " + attribute.getName() + ": " + defaultValue;
			firstIter = false;
		}
		if(definition.fromAssociations.size() > 0){
			if(definition.attributes.size() > 0)
				attributesCode += ",\n";
			firstIter = true;
			for(IAssociation association : definition.fromAssociations){
				IAssociationEnd thisEnd = (IAssociationEnd) association.getFromEnd();
				IAssociationEnd thatEnd = (IAssociationEnd) association.getToEnd();
				if(thatEnd.getNavigable() == 0){
					if(!firstIter)
						attributesCode += ",\n";
					String defaultValue = "null";
					if(thatEnd.getMultiplicity().endsWith("*"))
						defaultValue = "[]";
					attributesCode +=
						"        " + thisEnd.getName() + ": " + defaultValue;
					firstIter = false;
				}
			}
		}
		if(definition.toAssociations.size() > 0){
			if(definition.attributes.size() > 0 || definition.fromAssociations.size() > 0)
				attributesCode += ",\n";
			firstIter = true;
			for(IAssociation association : definition.toAssociations){
				IAssociationEnd thisEnd = (IAssociationEnd) association.getToEnd();
				IAssociationEnd thatEnd = (IAssociationEnd) association.getFromEnd();
				if(thatEnd.getNavigable() == 0){
					if(!firstIter)
						attributesCode += ",\n";
					String defaultValue = "null";
					if(thatEnd.getMultiplicity().endsWith("*"))
						defaultValue = "[]";
					attributesCode +=
						"        " + thisEnd.getName() + ": " + defaultValue;
					firstIter = false;
				}
			}
		}
		attributesCode += "\n";
		
		String operationsCode = "";
		List<IOperation> instanced = new ArrayList<>();
		for(IOperation operation : definition.operations){
			if(operation.getScope().equals("instance"))
				instanced.add(operation);
		}
		for(IOperation operation : instanced){
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
			operationsCode +=
	            "    that." + operation.getName() + " = function(" + parameterCode + ") {\n" +
	            "        " + unEscapedJsonString(_code, "        ") + "\n" +
	            "    };\n";
		}
		
		code +=
	        "// " + definition.getName() + " Class\n" +
	        "// \n" +
	        "// Generated by IC STRATEGY\n\n" +
	        "//\n" +
	        "// WARNING: Do not change this code; it will be overwritten by the next generation run!\n" +
	        "//          Change the code only in the Visual Paradigm Project.\n\n" +
	        //"import * as _ from 'lodash';\n" +
	        requireCode +
	        "\n" +
	        "module.exports = function(spec, my) {\n" +
	        "    var that;\n" +
	        "    my = my || {};\n" +
	        "    spec = spec || {};\n" +
	        "\n";
		
		if (!inheritsFrom.isEmpty())
	        code += "    that = " + inheritsFrom + "();\n";
	    else 
	        code += "    that = {};\n";
		
		code +=
	        "    that = _.extend(that, {\n" +
	                attributesCode +
	        "    });\n" +
	                operationsCode +
	        "\n" +
	        "    that = _.extend(that, spec);\n" +
	        "    return that;\n" +
	        "};";
		
		
		return code;
	}
}
