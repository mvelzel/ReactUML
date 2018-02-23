package test.projecttojs.actions_old;

import java.awt.Component;

import com.vp.plugin.ViewManager;
import com.vp.plugin.model.*;

public class GenerateDomainAPI extends Helpers {
	public static String generateDomainAPI(String className, IClass definitionClass, ViewManager viewManager, IProject project, Component parentFrame, String namespace){
		String code = "";
		classDefinition definition = GetFullClassDefinition(definitionClass.getId(), project, viewManager, parentFrame, true, namespace);
		
		code +=
		    "//\n" +
		    "// " + definition.getName() + " Actions\n" +
	        "// \n";
		
		code +=
		    "    '" + definition.getName() + "Connect': { asyncResult: true },\n" +
		    "    '" + definition.getName() + "Disconnect': { asyncResult: true },\n" +
	        "    '" + definition.getName() + "Create': { asyncResult: true },\n" +
	        "    '" + definition.getName() + "Update': { asyncResult: true },\n" +
	        "    '" + definition.getName() + "Delete': { asyncResult: true },\n" +
	        "    '" + definition.getName() + "Get': { asyncResult: false },\n" +
	        "    '" + definition.getName() + "LoadItem': { asyncResult: true },\n" +
	        "    '" + definition.getName() + "Refresh': { asyncResult: true },\n";
		
		if(definition.operations.size() > 0){
			for(IOperation operation : definition.operations){
				if(operation.getScope().equals("classifier"))
					code +=
	                	"    '" + definition.getName() + operation.getName() + "': { asyncResult: true },\n";
			}
		}
		
		return code;
	}
}
