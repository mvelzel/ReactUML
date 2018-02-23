package test.projecttojs.actions_old;

import java.awt.Component;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import com.google.common.base.CaseFormat;
import com.vp.plugin.ViewManager;
import com.vp.plugin.model.*;
import com.vp.plugin.model.property.IModelProperty;

public class Helpers {
	public static class classDefinition{
		String name = "";
		List<IOperation> operations = new ArrayList<>();
		List<String> stereotypes = new ArrayList<>();
		List<ITaggedValue> taggedValues = new ArrayList<>();
		List<IAssociation> fromAssociations = new ArrayList<>();
		List<IAssociation> toAssociations = new ArrayList<>();
		List<IAttribute> attributes = new ArrayList<>();
		IRealization realizationClass = null;
		public String getName(){
			return this.name;
		}
	}
	
	public static Boolean stringExistsInArray(JSONArray stringArray, String string){
		Boolean exists = false;
		for(int i = 0; i < stringArray.length(); i++){
			if(stringArray.getString(i).equals(string))
				exists = true;
		}
		return exists;
	}
	
	public static Boolean stringExistsInIterator(Iterator<?> iterator, String string){
		Boolean exists = false;
		while(iterator.hasNext()){
			if(((String) iterator.next()).equals(string))
				exists = true;
		}
		return exists;
	}
	
	public static String joinStringArray(JSONArray stringArray, String divider){
		String joinedString = "";
		for(int i = 0; i < stringArray.length(); i++){
			joinedString += stringArray.getString(i);
			if(i < stringArray.length() - 1)
				joinedString += divider;
				
		}
		return joinedString;
	}
	
	public static String unEscapedJsonString(String s, String indent){
		String newString = s.replace("\\n", "\n" + indent).replace("\\r", "\r").replace("\\t", "\t");
		return newString;
	}
	
	public static String findPropBase(String propName, Boolean strict, classDefinition definition){
		if(!propName.equals("")){
			String suffix = "";
			if(propName.substring(0, 1).equals("[") && propName.substring(propName.length() - 1, propName.length()).equals("]")){
				propName = propName.substring(1, propName.length() - 1);
				if (propName.indexOf(".") > -1){
					String[] propNameParts = propName.split("\\.");
					propName = propNameParts[0];
				}
			}
			IAttribute attribute = null;
			for(IAttribute _attribute : definition.attributes){
				if(_attribute.getName().equals(propName)){
					attribute = _attribute;
					break;
				}
			}
			
			if(attribute != null){
				String propString = "";
				if(attribute.getVisibility().equals("public"))
					propString = "self.props." + attribute.getName();
				else
					propString = "self.state." + attribute.getName();
				return propString;
			}
		}
		if(strict)
			return null;
		else
			return propName;
	}
	
	public static String findPropValue(String propName, Boolean strict, classDefinition definition){
		if(!propName.equals("")){
			String suffix = "";
			if(propName.substring(0, 1).equals("[") && propName.substring(propName.length() - 1, propName.length()).equals("]")){
				propName = propName.substring(1, propName.length() - 1);
				if (propName.indexOf(".") > -1){
					String[] propNameParts = propName.split("\\.");
					propName = propNameParts[0];
					suffix = ".get('" + propNameParts[1] + "')";
				}
			}
			IAttribute attribute = null;
			for(IAttribute _attribute : definition.attributes){
				if(_attribute.getName().equals(propName)){
					attribute = _attribute;
					break;
				}
			}
			
			if(attribute != null){
				String propString = "";
				if(attribute.getVisibility().equals("public"))
					propString = "self.props." + attribute.getName() + suffix;
				else
					propString = "self.state." + attribute.getName() + suffix;
				return propString;
			}
			else{
				IOperation operation = null;
				for(IOperation _operation : definition.operations){
					if(_operation.getName().equals(propName)){
						operation = _operation;
						break;
					}
				}
				if(operation != null){
					return "self." + operation.getName();
				}
			}
		}
		if(strict)
			return null;
		else
			return propName;
	}

	// FIX REPLACE
	public static String toDash(String input){
		return CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_HYPHEN, input);
	}
	
	public static String capitalizeFirstLetter(String original) {
	    if (original == null || original.length() == 0) {
	        return original;
	    }
	    return original.substring(0, 1).toUpperCase() + original.substring(1);
	}
	
	public static JSONObject propertyStringToJSON(String propertyString, IModelElement originalModel){
        String[] properties = propertyString.split(";");
        String previousKey = "";
        JSONObject childObject = new JSONObject();
        JSONArray propertyNames = new JSONArray();
        IModelProperty[] propertyArray = originalModel.toModelPropertyArray();
        for(int i = 0; i < propertyArray.length; i++){
            IModelProperty property = propertyArray[i];
            propertyNames.put(property.getName());
        }   
         
        for(int i = 0; i < properties.length; i++){
            try{
                String[] splitArray = properties[i].split("=", 2);  
                 
                String key = splitArray[0];
                String value = splitArray[1];
                Boolean isNormal = false;
                for(int _i = 0; _i < propertyNames.length(); _i++){
                    if(key.equals(propertyNames.getString(_i)))
                        isNormal = true;
                }
                if(!isNormal){
                    try{
                        String previousValue = childObject.getString(previousKey);
                        String currentValue = value;
                        childObject.put(previousKey, previousValue + ";" + key + "=" + currentValue);
                    }
                    catch(Exception _e){
                        childObject.put("error", _e.toString() + "|||" + previousKey + "|||" + key);
                    }
                } 
                else {
                    childObject.put(key, value);
                    previousKey = key;
                }
                 
            }
            catch(Exception e){
                Boolean isNormal = false;
                String value = "";
                String key = properties[i];
                for(int _i = 0; _i < propertyNames.length(); _i++){
                    if(key.equals(propertyNames.getString(_i)))
                        isNormal = true;
                }
                if(!isNormal){
                    try{
                        String previousValue = childObject.getString(previousKey);
                        String currentValue = value;
                        childObject.put(previousKey, previousValue + ";" + key + currentValue);
                    }
                    catch(Exception _e){
                        childObject.put("error", _e.toString());
                    }
                } 
                else {
                    childObject.put(removeLastChar(properties[i]), "");
                    previousKey = key;
                }
                 
            }
        }
        
        return childObject;
    }
	
	public static String removeLastChar(String str) {
        return str.substring(0,str.length()-1);
    }
	
	public static classDefinition GetFullClassDefinition(String classId, IProject project, ViewManager viewManager, Component parentFrame, Boolean addGeneralization, String namespace){
		classDefinition definition = new classDefinition();
		
		List<String> operationNames = new ArrayList<>();
		List<String> tagNames = new ArrayList<>();
		List<String> fromNames = new ArrayList<>();
		List<String> toNames = new ArrayList<>();
		List<String> attrNames = new ArrayList<>();
		
		IClass modelClass = null;
		if(project.getModelElementById(classId) != null){
			modelClass = (IClass) project.getModelElementById(classId);
		}
		else{
			IProject[] linkedProjects = project.getLinkedProjects();
			for(int i = 0; i < linkedProjects.length; i++){
				IProject _project = linkedProjects[i];
				if(_project.getModelElementById(classId) != null){
					modelClass = (IClass) _project.getModelElementById(classId);
					break;
				}
			}
		}
		namespace = modelClass.getProject().getName().split(" ")[1].replace("(", "").replace(")", "");
		definition.name = namespace + "_" + modelClass.getName();
		for(Iterator<?> iter = modelClass.operationIterator(); iter.hasNext();){
			IOperation operation = (IOperation) iter.next();
			operationNames.add(operation.getName());
			definition.operations.add(operation);
		}
		for(Iterator<?> iter = modelClass.stereotypeIterator(); iter.hasNext();){
			String stereotype = (String) iter.next();
			definition.stereotypes.add(stereotype);
		}
		if(modelClass.getTaggedValues() != null){
			for(Iterator<?> iter = modelClass.getTaggedValues().taggedValueIterator(); iter.hasNext();){
				ITaggedValue tag = (ITaggedValue) iter.next();
				tagNames.add(tag.getName());
				definition.taggedValues.add(tag);
			}
		}
		for(Iterator<?> iter = modelClass.attributeIterator(); iter.hasNext();){
			IAttribute attr = (IAttribute) iter.next();
			attrNames.add(attr.getName());
			definition.attributes.add(attr);
		}
		for(Iterator<?> iter = modelClass.fromRelationshipEndIterator(); iter.hasNext();){
			IAssociationEnd associationEnd = (IAssociationEnd) iter.next();
			IAssociation association = (IAssociation) associationEnd.getEndRelationship();
			fromNames.add(association.getName());
			definition.fromAssociations.add(association);
		}
		for(Iterator<?> iter = modelClass.toRelationshipEndIterator(); iter.hasNext();){
			IAssociationEnd associationEnd = (IAssociationEnd) iter.next();
			IAssociation association = (IAssociation) associationEnd.getEndRelationship();
			toNames.add(association.getName());
			definition.toAssociations.add(association);
		}
		for(Iterator<?> iter = modelClass.toRelationshipIterator(); iter.hasNext();){
			IRelationship relationship = (IRelationship) iter.next();
			if(relationship.getModelType().equals("Realization"))
				definition.realizationClass = (IRealization) relationship;
		}
		for(Iterator<?> iter = modelClass.toRelationshipIterator(); iter.hasNext();){
			IRelationship relationship = (IRelationship) iter.next();
			if(relationship.getModelType().equals("Generalization") && addGeneralization){
				IGeneralization generalization = (IGeneralization) relationship; 
				classDefinition generalizationDefinition = GetFullClassDefinition(generalization.getFrom().getId(), project, viewManager, parentFrame, true, namespace);
				for(IOperation operation : generalizationDefinition.operations){
					if(!stringExistsInIterator(operationNames.iterator(), operation.getName()))
						definition.operations.add(operation);
				}
				for(String stereotype : generalizationDefinition.stereotypes){
					if(!stringExistsInIterator(definition.stereotypes.iterator(), stereotype))
						definition.stereotypes.add(stereotype);
				}
				for(ITaggedValue tag : generalizationDefinition.taggedValues){
					if(!stringExistsInIterator(tagNames.iterator(), tag.getName()))
						definition.taggedValues.add(tag);
				}
				for(IAttribute attr : generalizationDefinition.attributes){
					if(!stringExistsInIterator(attrNames.iterator(), attr.getName()))
						definition.attributes.add(attr);
				}
				for(IAssociation association : generalizationDefinition.fromAssociations){
					if(!stringExistsInIterator(fromNames.iterator(), association.getName()))
						definition.fromAssociations.add(association);
				}
				for(IAssociation association : generalizationDefinition.toAssociations){
					if(!stringExistsInIterator(toNames.iterator(), association.getName()))
						definition.toAssociations.add(association);
				}
				if(generalizationDefinition.realizationClass != null)
					definition.realizationClass = generalizationDefinition.realizationClass;
			}
		}
		return definition;
	}
}

