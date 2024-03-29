package test.projecttojs.actions_old;

import java.awt.Component;
import java.util.*;

import com.vp.plugin.ViewManager;
import com.vp.plugin.model.*;

public class GenerateStore extends Helpers {
	public static String generateStore(String modelClass, IProject project, ViewManager viewManager, IClass definitionClass, Component parentFrame, String namespace){
		String code = "";
		classDefinition definition = GetFullClassDefinition(definitionClass.getId(), project, viewManager, parentFrame, true, namespace);
		Boolean firstIter = true;
		
		code +=
	        "// " + definition.getName() + " ItemStore\n" +
	        "// \n" +
	        "// Generated by IC STRATEGY\n" +
	        "//\n" +
	        "// WARNING: Do not change this code; it will be overwritten by the next generation run!\n" +
	        "//          Change the code only in the Visual Paradigm Project.\n\n" +
	        "import {ItemStore} from '../../../js/3-domain/stores/meta/ItemStore';\n" +
	        "var CollectionStore = require('../../../js/3-domain/stores/meta/CollectionStore');\n" +
	        "var DomainAPI = require('../domain-entity/DomainAPI');\n" +
	        "import {" + definition.getName() + "} from '../domain-entity/" + definition.getName() + "';\n" +
	        "import * as _ from 'lodash';\n" +
	        "\n" +
	        "module.exports = function(spec, my) {\n" +
	        "   var that;\n" +
	        "   my = my || {};\n" +
	        "   spec = spec || {};\n" +
	        "\n" +
	        "   that = ItemStore(_.extend({\n" +
	        "       Entity: " + definition.getName() + "\n" +
	        "   }, spec));\n";
		
		String operationsCode = "";
		List<IOperation> classifiers = new ArrayList<>();
		for(IOperation operation : definition.operations){
			if(operation.getScope().equals("classifier"))
				classifiers.add(operation);
		}
		for(IOperation operation : classifiers){
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
		        "   that." + operation.getName() + " = function(" + parameterCode + ") {\n" +
		        "       " + unEscapedJsonString(_code, "       ") + "\n" +
		        "   };\n";
		}
		code += operationsCode;
		
		code +=
		    "   that.connectAPI([\n" +
		    "       [DomainAPI." + definition.getName() + "Connect, 'onConnect'],\n" +
	        "       [DomainAPI." + definition.getName() + "Disconnect, 'onDisconnect'],\n" +
	        "       [DomainAPI." + definition.getName() + "CreateGenerator, 'onCreate'],\n" +
	        "       [DomainAPI." + definition.getName() + "Update, 'onUpdate'],\n" +
	        "       [DomainAPI." + definition.getName() + "Delete, 'onDelete'],\n" +
	        "       [DomainAPI." + definition.getName() + "Get, 'onGet'],\n" +
	        "       [DomainAPI." + definition.getName() + "LoadItemGenerator, 'onLoadItem'],\n" +
	        "       [DomainAPI." + definition.getName() + "Refresh, 'onRefresh']";
		
		if(!operationsCode.isEmpty()){
			code += ",\n";
			for(IOperation operation : classifiers){
				if(!firstIter)
					code += ",\n";
				code +=
		            "       [DomainAPI." + definition.getName() + operation.getName() + ", '" + operation.getName() + "']";
				firstIter = false;
			}
			code +=  "\n";
		}
		else
			code +=  "\n";
		
		code +=
		    "   ]);\n" +
		    "\n" +
		    "   return that;\n" +
	        "};\n" +
	        "\n";
		
		
		return code;
	}
}
