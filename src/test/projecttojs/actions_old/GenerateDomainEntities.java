package test.projecttojs.actions_old;

import java.awt.Component;
import java.util.*;

import org.apache.commons.lang3.tuple.*;
import com.vp.plugin.ViewManager;
import com.vp.plugin.model.*;

public class GenerateDomainEntities extends Helpers {
	public static String generateDomainEntity(String className, IClass definitionClass, ViewManager viewManager, IProject project, Component parentFrame, String namespace){
		String code = "";
		Boolean iterFirst = true;
		classDefinition definition = GetFullClassDefinition(definitionClass.getId(), project, viewManager, parentFrame, false, namespace);
		String requireCode = "";
		IGeneralization generalization = null;
		String inheritsFrom = "Entity";
		for(Iterator<?> iter = definitionClass.toRelationshipIterator(); iter.hasNext();){
			IRelationship relationship = (IRelationship) iter.next();
			if(relationship.getModelType().equals("Generalization"))
				generalization = (IGeneralization) relationship;
		}
		if(generalization == null)
			requireCode = "import {Entity} from '../../../js/3-domain/meta/Entity';\n";
		else{
			requireCode = "import {" + GetFullClassDefinition(generalization.getFrom().getId(), project, viewManager, parentFrame, false, namespace).name + "} from './" + GetFullClassDefinition(generalization.getFrom().getId(), project, viewManager, parentFrame, false, namespace).name + "';\n";
			inheritsFrom = GetFullClassDefinition(generalization.getFrom().getId(), project, viewManager, parentFrame, false, namespace).name;
		}
		
		if(stringExistsInIterator(definition.stereotypes.iterator(), "firebase"))
			requireCode += "import * as firebase from 'firebase';\n" +
					"import {FirebaseManager} from '../../../js/4-infrastructure/databaseManagers/FirebaseManager';\n";
		if(stringExistsInIterator(definition.stereotypes.iterator(), "errors"))
			requireCode += "var ErrorActions = require('../../../js/3-domain/actions_old/ErrorActions');\n";
		requireCode += "import * as _ from 'lodash';\n";
		
		String attributesCode = "";
		iterFirst = true;
		for(Iterator<?> iter = definition.attributes.iterator(); iter.hasNext();){
			if(!iterFirst)
				attributesCode += ",\n";
			IAttribute attribute = (IAttribute) iter.next();
			
			ITaggedValue labelTag = null;
			if(attribute.getTaggedValues() != null){
				for(Iterator<?> _iter = attribute.getTaggedValues().taggedValueIterator(); _iter.hasNext();){
					ITaggedValue tag = (ITaggedValue) _iter.next();
					if(tag.getName().equals("label")){
						labelTag = tag;
						break;
					}
				}
			}
		
			String label = attribute.getName();
			if(labelTag != null)
				label = labelTag.getValueAsString();
			
			attributesCode +=
		        "           " + attribute.getName() + ": new class extends Attribute." + attribute.getTypeAsString() + " {\n" +
				"               constructor(){\n" +
		        "                   super();\n" +
		        "                   this.name = '" + attribute.getName() + "';\n" +
		        "                   this.label = '" + label + "';\n" +
	            "                   this.description = '" + attribute.getDescription() + "';\n";
			
			String defaultValue = "";
			String initialValue = "";
			if(attribute.getInitialValue() != null){
				initialValue = attribute.getInitialValue();
			}

			if(!initialValue.equals("$attribute.getInitialValue().getName()"))
				defaultValue = initialValue;
			if(!defaultValue.isEmpty())
				attributesCode +=
                	"                   this.defaultValue = " + defaultValue + ";\n";
			
			ITaggedValue optionsTag = null;
			if(attribute.getTaggedValues() != null){
				for(Iterator<?> _iter = attribute.getTaggedValues().taggedValueIterator(); _iter.hasNext();){
					ITaggedValue tag = (ITaggedValue) _iter.next();
					if(tag.getName().equals("options")){
						optionsTag = tag;
						break;
					}
				}
			}

			String options = "";
			if(optionsTag != null){
				options = optionsTag.getValueAsString();
				attributesCode +=
		            "                   this.options = " + options + ";\n";
			}
			attributesCode +=
		        "                   this.chartParameters = {\n";
			
			List<String> chartParameters = new ArrayList<>();
			for(Iterator<?> _iter = attribute.stereotypeIterator(); _iter.hasNext();){
				String stereotype = (String) _iter.next();
				if(stereotype.indexOf("chart") > -1)
					chartParameters.add(stereotype);
			}
			Boolean _iterFirst = true;
			for(String chartParameter : chartParameters){

				if(!_iterFirst)
					attributesCode += ",\n";
				attributesCode +=
		            "                      " + chartParameter.split("-")[1] + ": true";
				_iterFirst = false;
			}
			
			attributesCode +=
		            "\n                   };\n";
		    attributesCode +=
		        "                   this.visibility = '" + capitalizeFirstLetter(attribute.getVisibility()) + "';\n" +
		        "               }\n" +
	            "           }()";
		    iterFirst = false;
		}
		if(stringExistsInIterator(definition.stereotypes.iterator(), "timestamp")){
			if(attributesCode != null && !attributesCode.isEmpty())
				attributesCode += ",\n" +
			        "           timestampCreate: new class extends Attribute.DateTimeAttribute {\n" +
					"               constructor(){\n" +
			        "                   super();\n" +
			        "          	        this.name = 'timestampCreate';\n" +
			        "                   this.label = 'Tijdstip van ontstaan';\n" +
			        "                   this.defaultValue = '';\n" +
			        "                   this.visibility = 'Private';\n" +
			        "               }\n" +
			        "           }(),\n" +
			        "           timestampLastUpdate: new class extends Attribute.DateTimeAttribute {\n" +
					"               constructor(){\n" +
			        "                   super();\n" +
			        "                   this.name = 'timestampLastUpdate';\n" +
			        "                   this.label = 'Tijdstip van laatste wijziging';\n" +
			        "                   this.defaultValue = '';\n" +
			        "                   this.visibility = 'Private';\n" +
			        "               }\n" +
			        "           }(),\n";
		}
		attributesCode += "\n";
		
		String associationsCode = "";
		List<Pair<IAssociation, Boolean>> associations = new ArrayList<>();
		for(Iterator<?> iter = definition.fromAssociations.iterator(); iter.hasNext();){
			IAssociation association = (IAssociation) iter.next();
			Pair<IAssociation, Boolean> pair = new ImmutablePair<IAssociation, Boolean>(association, true);
			associations.add(pair);
		}
		for(Iterator<?> iter = definition.toAssociations.iterator(); iter.hasNext();){
			IAssociation association = (IAssociation) iter.next();
			Pair<IAssociation, Boolean> pair = new ImmutablePair<IAssociation, Boolean>(association, false);
			associations.add(pair);
		}
		for(Iterator<?> iter = associations.iterator(); iter.hasNext();){
			Pair<IAssociation, Boolean> pair = (Pair<IAssociation, Boolean>) iter.next();
			IAssociation association = pair.getLeft();
			Boolean isFrom = pair.getRight();
			IAssociationEnd toEnd = null;
			IAssociationEnd fromEnd = null;
			if(isFrom){
				fromEnd = (IAssociationEnd) association.getFromEnd();
				toEnd = (IAssociationEnd) association.getToEnd();
			}
			else{
				fromEnd = (IAssociationEnd) association.getToEnd();
				toEnd = (IAssociationEnd) association.getFromEnd();
			}
			String thisCardinality = "single";
			String thatCardinality = "single";
			if(fromEnd.getMultiplicity().endsWith("*"))
				thisCardinality = "multiple";
			if(toEnd.getMultiplicity().endsWith("*"))
				thatCardinality = "multiple";
			associationsCode +=
		        "           " + fromEnd.getName() + ": new class extends Association {\n" +
		        "               constructor(){\n" +
		        "                   super();\n" +
		        "                   this.name = '" + association.getName() + "';\n" +
		        "                   this.thisName = '" + fromEnd.getName() + "';\n" +
		        "                   this.thisIsComposite = " + Boolean.toString(fromEnd.getAggregationKind().equals("Composited")) + ";\n" +
		        "                   this.thisCardinality = '" + thisCardinality + "';\n" +
		        "                   this.thisIsMandatory = " + Boolean.toString(fromEnd.getMultiplicity().startsWith("1")) + ";\n" +
		        "                   this.thisIsNavigable = " + Boolean.toString(fromEnd.getNavigable() == 0) + ";\n" +
		        "                   this.thatName = '" + toEnd.getName() + "';\n" +
		        "                   this.thisEntity = require('./" + GetFullClassDefinition(fromEnd.getTypeAsModel().getId(), project, viewManager, parentFrame, false, namespace).name  + "');\n" +
		        "                   this.thatEntity = require('./" + GetFullClassDefinition(toEnd.getTypeAsModel().getId(), project, viewManager, parentFrame, false, namespace).name + "');\n" +
		        "                   this.thatIsComposite = " + Boolean.toString(toEnd.getAggregationKind().equals("Composited")) + ";\n" +
		        "                   this.thatCardinality = '" + thatCardinality + "';\n" +
		        "                   this.thatIsMandatory = " + Boolean.toString(toEnd.getMultiplicity().startsWith("1")) + ";\n" +
	            "                   this.thatIsNavigable = " + Boolean.toString(toEnd.getNavigable() == 0) + ";\n" +
	            "               }\n" +
	            "           }()";
			if(iter.hasNext())
				associationsCode += ",\n";
			else
				associationsCode += "\n";
		}
		
		String operationsCode = "";
		List<IOperation> operations = new ArrayList<>();
		for(IOperation operation : definition.operations){
			if(operation.getScope().equals("instance"))
				operations.add(operation);
		}
		for(IOperation operation : operations){
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
		        "   " + operation.getName() + "(" + parameterCode + ") {\n" +
		        "       " + unEscapedJsonString(_code, "       ") + "\n" +
		        "   };\n";
		}
		code +=
		    "// " + definition.getName() + " Entity\n" +
		    "// \n" +
		    "// Generated by IC STRATEGY on " + new Date().toString() + "\n\n" +
		    "//\n" +
		    "// WARNING: Do not change this code; it will be overwritten by the next generation run!\n" +
		    "//          Change the code only in the Visual Paradigm Project.\n\n" +
	        //"import * as _ from 'lodash';\n" +
		    "import * as Attribute from '../../../js/3-domain/meta/Attribute';\n" +
	        "import {Association} from '../../../js/3-domain/meta/Association';\n" +
	        requireCode +
	        "\n" +
	        "export class " + definition.getName() + " extends " + inheritsFrom + " {\n" +
	        "   constructor() {\n" +
	        "       super();\n" +
	        "       this.type = '" + definition.getName() + "';\n" +
	        "       this.controller = require('../controller/" + definition.getName() + "');\n" +
	        "       this.isPersistent = " + Boolean.toString(stringExistsInIterator(definition.stereotypes.iterator(), "persistent")) + ";\n" +
	        "       this.hasUrl = " + Boolean.toString(stringExistsInIterator(definition.stereotypes.iterator(), "hasUrl")) + ";\n" +
	        "       this.attributes = _.extend(this.attributes, {\n" + 
	        			attributesCode +
	        "       });\n" +
	        "       this.associations = _.extend(this.associations, {\n" + 
						associationsCode +
			"       });\n";
			try{
				code += 
					"       this.parentClass = require('./" + GetFullClassDefinition(generalization.getFrom().getId(), project, viewManager, parentFrame, false, namespace).name + "');\n";
			}
			catch(Exception e){}
	        code += "   };\n";
		
		code +=
	        operationsCode +
	        "\n" +
	        "};";
		
		return code;
	}
}
