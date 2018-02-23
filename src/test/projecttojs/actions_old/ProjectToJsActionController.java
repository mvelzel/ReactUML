package test.projecttojs.actions_old;

import java.awt.Component;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.*;
import java.util.prefs.Preferences;

import org.apache.commons.io.*;

import com.vp.plugin.*;
import com.vp.plugin.action.*;
import com.vp.plugin.diagram.*;
import com.vp.plugin.model.*;

public class ProjectToJsActionController extends Helpers implements VPActionController {
	public Boolean initApp;
	public Boolean domainAPI;
	public Boolean entityList;
	public String entityListEndCode;
	public void performAction(VPAction action){
		ViewManager viewManager = ApplicationManager.instance().getViewManager();
		Component parentFrame = viewManager.getRootFrame();
		initApp = true;
		domainAPI = true;
		entityList = true;
		String fileDirectory;
		MainGUI setFileDirectoryDialog = new MainGUI();
		viewManager.showDialog(setFileDirectoryDialog);
		
		
		
		IProject project = ApplicationManager.instance().getProjectManager().getProject();
		String pName = project.getName().split(" ")[0];
		fileDirectory = setFileDirectoryDialog._inputField1.getText();
		Preferences prefs = Preferences.userRoot();
		prefs.put("FileDir", fileDirectory);
		fileDirectory = fileDirectory + "/" + pName + "/src/generated/js";
		try{
			Files.createDirectories(Paths.get(fileDirectory));
		}
		catch(Exception e){
			viewManager.showMessage(e.toString());
		}
		IProject[] linkedProjects = project.getLinkedProjects();
		Iterator<?> modelIterator = project.allLevelModelElementIterator();
		List<String> iteratedArray = new ArrayList<>();
		
		Iterator<?> diagramIterator = project.diagramIterator();
		
		try {
			FileUtils.cleanDirectory(new File(fileDirectory.replace("\\", "/")));
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		try{
		if(!fileDirectory.isEmpty() && setFileDirectoryDialog.activateGenerator){
			if(linkedProjects == null){
				while(modelIterator.hasNext()){
					IModelElement modelElement = (IModelElement) modelIterator.next();
					String namespace = "";
					if(modelElement.getProject().getName().contains(" "))
						namespace = modelElement.getProject().getName().split(" ")[1].replace("(", "").replace(")", "");
					else{
						viewManager.showMessageDialog(parentFrame, "Project doesn't have namespace");
						return;
					}
					if(modelElement.getModelType() == "Class" && !stringExistsInIterator(iteratedArray.iterator(), namespace + "_" + modelElement.getName())){
						IClass modelClass = (IClass) modelElement;
						generateApp(fileDirectory, modelClass, project, viewManager, parentFrame, namespace);
						if(modelElement.getName() != null)
							iteratedArray.add(namespace + "_" + modelElement.getName());
					}
				}
			}
			else{
				while(diagramIterator.hasNext()){
					IDiagramUIModel diagram = (IDiagramUIModel) diagramIterator.next();
					Iterator<?> elementIter = diagram.diagramElementIterator();
					while(elementIter.hasNext()){
						IDiagramElement element = (IDiagramElement) elementIter.next();
						IModelElement modelElement = element.getModelElement();
						String namespace = modelElement.getProject().getName().split(" ")[1].replace("(", "").replace(")", "");
						if(modelElement.getModelType() == "Class" && !stringExistsInIterator(iteratedArray.iterator(), namespace + "_" + modelElement.getName())){
							viewManager.showMessage(namespace + "_" + modelElement.getName());
							IClass modelClass = (IClass) modelElement;
							generateApp(fileDirectory, modelClass, project, viewManager, parentFrame, namespace);
							for(Iterator<?> iter = modelClass.fromRelationshipEndIterator(); iter.hasNext();){
								IAssociationEnd associationEnd = (IAssociationEnd) iter.next();
								IAssociation association = (IAssociation) associationEnd.getEndRelationship();
								IAssociationEnd thatEnd = (IAssociationEnd) association.getToEnd();
								IClass associationClass = (IClass) thatEnd.getTypeAsModel();
								namespace = associationClass.getProject().getName().split(" ")[1].replace("(", "").replace(")", "");
								if(!stringExistsInIterator(iteratedArray.iterator(), namespace + "_" + associationClass.getName())){
									generateApp(fileDirectory, associationClass, project, viewManager, parentFrame, namespace);
									if(associationClass.getName() != null)
										iteratedArray.add(namespace + "_" + associationClass.getName());
								}
							}
							for(Iterator<?> iter = modelClass.toRelationshipEndIterator(); iter.hasNext();){
								IAssociationEnd associationEnd = (IAssociationEnd) iter.next();
								IAssociation association = (IAssociation) associationEnd.getEndRelationship();
								IAssociationEnd thatEnd = (IAssociationEnd) association.getFromEnd();
								IClass associationClass = (IClass) thatEnd.getTypeAsModel();
								namespace = associationClass.getProject().getName().split(" ")[1].replace("(", "").replace(")", "");
								if(!stringExistsInIterator(iteratedArray.iterator(), namespace + "_" + associationClass.getName())){
									generateApp(fileDirectory, associationClass, project, viewManager, parentFrame, namespace);
									if(associationClass.getName() != null)
										iteratedArray.add(namespace + "_" + associationClass.getName());
								}
							}
							if(modelElement.getName() != null)
								iteratedArray.add(namespace + "_" + modelElement.getName());
						}
					}
				}
			}
			endCode(fileDirectory + "/domain-entity", "\n});\n\nmodule.exports = API;\n", "DomainAPI.js");
			endCode(fileDirectory + "/domain-entity", entityListEndCode + "   }\n};\n", "DomainEntitiesList.js");
			viewManager.showMessageDialog(parentFrame, "Done writing to " + fileDirectory);
		}
		}
		catch(Exception e){
			viewManager.showMessageDialog(parentFrame, "Error " + e.toString() + " on line " + e.getStackTrace()[0].getLineNumber());
		}
	}
	
	public void update(VPAction action) {
		
	}
	
	private void generateApp(String fileDirectory, IClass modelClass, IProject project, ViewManager viewManager, Component parentFrame, String namespace){
		if(stringExistsInIterator(modelClass.stereotypeIterator(), "ReactComponent")){
			try{
			List<IAssociation> fromAssociations = new ArrayList<>();
			List<IAssociation> toAssociations = new ArrayList<>();
			for(Iterator<?> iter = modelClass.fromRelationshipEndIterator(); iter.hasNext();){
				IAssociationEnd associationEnd = (IAssociationEnd) iter.next();
				IAssociation association = (IAssociation) associationEnd.getEndRelationship();
				fromAssociations.add(association);
			}
			for(Iterator<?> iter = modelClass.toRelationshipEndIterator(); iter.hasNext();){
				IAssociationEnd associationEnd = (IAssociationEnd) iter.next();
				IAssociation association = (IAssociation) associationEnd.getEndRelationship();
				toAssociations.add(association);
			}
			List<IAssociation> inboundRoutes = new ArrayList<>();
			for(IAssociation association : fromAssociations){
				IAssociationEnd thatEnd = (IAssociationEnd) association.getToEnd();
				if(thatEnd.getAggregationKind().equals("Composited") && thatEnd.getTypeAsString().equals(modelClass.getName()) && stringExistsInIterator(association.stereotypeIterator(), "Route"))
					inboundRoutes.add(association);
			}
			for(IAssociation association : toAssociations){
				IAssociationEnd thatEnd = (IAssociationEnd) association.getFromEnd();
				if(thatEnd.getAggregationKind().equals("Composited") && thatEnd.getTypeAsString().equals(modelClass.getName()) && stringExistsInIterator(association.stereotypeIterator(), "Route"))
					inboundRoutes.add(association);
			}
			List<IAssociation> outboundRoutes = new ArrayList<>();
			for(IAssociation association : fromAssociations){
				IAssociationEnd thisEnd = (IAssociationEnd) association.getFromEnd();
				IClass endClass = (IClass) thisEnd.getTypeAsElement();
				if(thisEnd.getAggregationKind().equals("Composited") && thisEnd.getTypeAsString().equals(modelClass.getName()) && stringExistsInIterator(association.stereotypeIterator(), "Route") && stringExistsInIterator(endClass.stereotypeIterator(), "RootRoute"))
					outboundRoutes.add(association);
			}
			for(IAssociation association : toAssociations){
				IAssociationEnd thisEnd = (IAssociationEnd) association.getToEnd();
				IClass endClass = (IClass) thisEnd.getTypeAsElement();
				if(thisEnd.getAggregationKind().equals("Composited") && thisEnd.getTypeAsString().equals(modelClass.getName()) && stringExistsInIterator(association.stereotypeIterator(), "Route") && stringExistsInIterator(endClass.stereotypeIterator(), "RootRoute"))
					outboundRoutes.add(association);
			}
			
			if(inboundRoutes.size() == 0 && outboundRoutes.size() > 0){
				try{
					PrintWriter writer;
					writer = new PrintWriter(fileDirectory.replace("\\", "/") + "/App.js", "UTF-8");
					writer.println(GenerateApp.generateApp(modelClass.getName(), modelClass, viewManager, project, parentFrame, namespace));
					writer.close();
				}
				catch(Exception e){
					viewManager.showMessageDialog(parentFrame, "Error " + e.toString() + " on line " + e.getStackTrace()[0].getLineNumber() + " in " + modelClass.getName());
				}
			}
			}
			catch(Exception e){
				viewManager.showMessageDialog(parentFrame, "Error " + e.toString() + " on line " + e.getStackTrace()[0].getLineNumber() + " in " + modelClass.getName());
			}
		}
		generateDomainEntities(fileDirectory, modelClass, project, viewManager, parentFrame, namespace);
	}
	
	private void generateDomainEntities(String fileDirectory, IClass modelClass, IProject project, ViewManager viewManager, Component parentFrame, String namespace){
		if(stringExistsInIterator(modelClass.stereotypeIterator(), "DomainEntity")){
			try{
				PrintWriter writer;
				try{
					Files.createDirectory(Paths.get(fileDirectory + "/domain-entity"));
				}
				catch(Exception e){}
				writer = new PrintWriter((fileDirectory + "/domain-entity").replace("\\", "/") + "/" + namespace + "_" + modelClass.getName() + ".js", "UTF-8");
				writer.println(GenerateDomainEntities.generateDomainEntity(modelClass.getName(), modelClass, viewManager, project, parentFrame, namespace));
				writer.close();
			}
			catch(Exception e){
				viewManager.showMessageDialog(parentFrame, "Error " + e.toString() + " on line " + e.getStackTrace()[0].getLineNumber());
			}
		}
		generateDomainAPI(fileDirectory, modelClass, project, viewManager, parentFrame, namespace);
	}
	
	private void generateDomainAPI(String fileDirectory, IClass modelClass, IProject project, ViewManager viewManager, Component parentFrame, String namespace){
		if(stringExistsInIterator(modelClass.stereotypeIterator(), "DomainEntity")){
			if(domainAPI){
				try{
					try{
						Files.createDirectory(Paths.get(fileDirectory + "/domain-entity"));
					}
					catch(Exception e){}
					String startCode = 
						"// DomainAPI\n//\n" +
			            "// Generated by IC STRATEGY\n" +
		                "//\n" +
		                "// WARNING: Do not change this code; it will be overwritten with the next generation run!\n" +
		                "//          Change the code only in Visual Paradigm.\n//\n" +
		                "var Reflux = require('reflux');\n\n" +
		                "var API = Reflux.createActions({";
					PrintWriter writer;
					writer = new PrintWriter((fileDirectory + "/domain-entity").replace("\\", "/") + "/DomainAPI.js", "UTF-8");
					writer.println(startCode);
					writer.close();
					domainAPI = false;
				}
				catch(Exception e){
					viewManager.showMessageDialog(parentFrame, "Error " + e.toString() + " on line " + e.getStackTrace()[0].getLineNumber());
				}
			}
			try{
				String code = GenerateDomainAPI.generateDomainAPI(modelClass.getName(), modelClass, viewManager, project, parentFrame, namespace);
				Files.write(Paths.get((fileDirectory + "/domain-entity").replace("\\", "/") + "/DomainAPI.js"), code.getBytes(), StandardOpenOption.APPEND);
			}
			catch(Exception e){
				viewManager.showMessageDialog(parentFrame, "Error " + e.toString() + " on line " + e.getStackTrace()[0].getLineNumber());
			}
		}
		generateDomainEntitiesList(fileDirectory, modelClass, project, viewManager, parentFrame, namespace);
	}
	
	private void generateDomainEntitiesList(String fileDirectory, IClass modelClass, IProject project, ViewManager viewManager, Component parentFrame, String namespace){
		if(stringExistsInIterator(modelClass.stereotypeIterator(), "DomainEntity")){
			if(entityList){
				try{
					try{
						Files.createDirectory(Paths.get(fileDirectory + "/domain-entity"));
					}
					catch(Exception e){}
					String startCode = 
						"// Domain Entities List\n//\n" +
			            "// Generated by IC STRATEGY\n" +
		                "//\n" +
		                "// WARNING: Do not change this code; it will be overwritten with the next generation run!\n" +
		                "//          Change the code only in Visual Paradigm.\n//\n";
					entityListEndCode =
						"module.exports = function(entityType) {\n" +
			            "   switch (entityType) {\n";
					PrintWriter writer;
					writer = new PrintWriter((fileDirectory + "/domain-entity").replace("\\", "/") + "/DomainEntitiesList.js", "UTF-8");
					writer.println(startCode);
					writer.close();
					entityList = false;
				}
				catch(Exception e){
					viewManager.showMessageDialog(parentFrame, "Error " + e.toString() + " on line " + e.getStackTrace()[0].getLineNumber());
				}
			}
			try{
				entityListEndCode += "       case '" + namespace + "_" + modelClass.getName() + "': return " + namespace + "_" + modelClass.getName() + ";\n";
				String code = "import {" + namespace + "_" + modelClass.getName() + "} from '../domain-entity/" + namespace + "_" + modelClass.getName() + "';\n";
				Files.write(Paths.get((fileDirectory + "/domain-entity").replace("\\", "/") + "/DomainEntitiesList.js"), code.getBytes(), StandardOpenOption.APPEND);
			}
			catch(Exception e){
				viewManager.showMessageDialog(parentFrame, "Error " + e.toString() + " on line " + e.getStackTrace()[0].getLineNumber());
			}
		}
		generateClasses(fileDirectory, modelClass, project, viewManager, parentFrame, namespace);
	}
	
	private void generateClasses(String fileDirectory, IClass modelClass, IProject project, ViewManager viewManager, Component parentFrame, String namespace){
		if(stringExistsInIterator(modelClass.stereotypeIterator(), "Class")){
			try{
				PrintWriter writer;
				try{
					Files.createDirectory(Paths.get(fileDirectory + "/class"));
				}
				catch(Exception e){}
				writer = new PrintWriter((fileDirectory + "/class").replace("\\", "/") + "/" + namespace + "_" + modelClass.getName() + ".js", "UTF-8");
				writer.println(GenerateClass.generateClass(modelClass.getName(), project, viewManager, modelClass, parentFrame, namespace));
				writer.close();
			}
			catch(Exception e){
				viewManager.showMessageDialog(parentFrame, "Error " + e.toString() + " on line " + e.getStackTrace()[0].getLineNumber());
			}
		}
		generateInitApp(fileDirectory, modelClass, project, viewManager, parentFrame, namespace);
	}
	
	private void generateInitApp(String fileDirectory, IClass modelClass, IProject project, ViewManager viewManager, Component parentFrame, String namespace){
		if(stringExistsInIterator(modelClass.stereotypeIterator(), "DomainEntity") && !modelClass.isAbstract()){
			if(initApp){
				try{
					Files.createDirectory(Paths.get(fileDirectory + "/domain-entity"));
				}
				catch(Exception e){}
				try{
					String startCode = 
			            "// initApp\n//\n" +
			            "// Generated by IC STRATEGY\n" +
		                "//\n" +
		                "// WARNING: Do not change this code; it will be overwritten with the next generation run!\n" +
		                "//          Change the code only in the Visual Paradigm Project.\n//\n" +
		                "require('../../js/1-presentation/services/stores/FormStore');\n" +
		                "require('../../js/3-domain/stores/ErrorStore');";
					PrintWriter writer;
					writer = new PrintWriter(fileDirectory.replace("\\", "/") + "/initApp.js", "UTF-8");
					writer.println(startCode);
					writer.close();
					initApp = false;
				}
				catch(Exception e){
					viewManager.showMessageDialog(parentFrame, "Error " + e.toString() + " on line " + e.getStackTrace()[0].getLineNumber());
				}
			}
			try{
				String code = "require('./store/" + namespace + "_" + modelClass.getName() + "')().init();\n";
				Files.write(Paths.get(fileDirectory.replace("\\", "/") + "/initApp.js"), code.getBytes(), StandardOpenOption.APPEND);
			}
			catch(Exception e){
				viewManager.showMessageDialog(parentFrame, "Error " + e.toString() + " on line " + e.getStackTrace()[0].getLineNumber());
			}
		}
		generateStores(fileDirectory, modelClass, project, viewManager, parentFrame, namespace);
	}
	
	private void generateStores(String fileDirectory, IClass modelClass, IProject project, ViewManager viewManager, Component parentFrame, String namespace){
		if(stringExistsInIterator(modelClass.stereotypeIterator(), "DomainEntity") && !modelClass.isAbstract()){
			try{
				PrintWriter writer;
				try{
					Files.createDirectory(Paths.get(fileDirectory + "/store"));
				}
				catch(Exception e){}
				writer = new PrintWriter((fileDirectory + "/store").replace("\\", "/") + "/" + namespace + "_" + modelClass.getName() + ".js", "UTF-8");
				writer.println(GenerateStore.generateStore(modelClass.getName(), project, viewManager, modelClass, parentFrame, namespace));
				writer.close();
			}
			catch(Exception e){
				viewManager.showMessageDialog(parentFrame, "Error " + e.toString() + " on line " + e.getStackTrace()[0].getLineNumber());
			}
		}
		generateForms(fileDirectory, modelClass, project, viewManager, parentFrame, namespace);
	}
	
	private void generateForms(String fileDirectory, IClass modelClass, IProject project, ViewManager viewManager, Component parentFrame, String namespace){
		if(stringExistsInIterator(modelClass.stereotypeIterator(), "DomainEntity")){
			try{
				PrintWriter writer;
				try{
					Files.createDirectory(Paths.get(fileDirectory + "/form"));
				}
				catch(Exception e){}
				writer = new PrintWriter((fileDirectory + "/form").replace("\\", "/") + "/" + namespace + "_" + modelClass.getName() + ".js", "UTF-8");
				writer.println(GenerateForm.generateForm(modelClass.getName(), project, viewManager, modelClass, parentFrame, namespace));
				writer.close();
			}
			catch(Exception e){
				viewManager.showMessageDialog(parentFrame, "Error " + e.toString() + " on line " + e.getStackTrace()[0].getLineNumber() + " in model " + modelClass.getName());
			}
		}
		generateControllers(fileDirectory, modelClass, project, viewManager, parentFrame, namespace);
	}
	
	private void generateControllers(String fileDirectory, IClass modelClass, IProject project, ViewManager viewManager, Component parentFrame, String namespace){
		if(stringExistsInIterator(modelClass.stereotypeIterator(), "DomainEntity")){
			try{
				PrintWriter writer;
				try{
					Files.createDirectory(Paths.get(fileDirectory + "/controller"));
				}
				catch(Exception e){}
				writer = new PrintWriter((fileDirectory + "/controller").replace("\\", "/") + "/" + namespace + "_" + modelClass.getName() + ".js", "UTF-8");
				writer.println(GenerateControllers.generateController(modelClass.getName(), modelClass, viewManager, project, parentFrame, namespace));
				writer.close();
			}
			catch(Exception e){
				viewManager.showMessageDialog(parentFrame, "Error " + e.toString() + " on line " + e.getStackTrace()[0].getLineNumber());
			}
		}
		generateReactComponents(fileDirectory, modelClass, project, viewManager, parentFrame, namespace);
	}
	
	private void generateReactComponents(String fileDirectory, IClass modelClass, IProject project, ViewManager viewManager, Component parentFrame, String namespace){
		if(stringExistsInIterator(modelClass.stereotypeIterator(), "ReactComponent") && !modelClass.isAbstract()){
			try{
				PrintWriter writer;
				try{
					Files.createDirectory(Paths.get(fileDirectory + "/react-component"));
				}
				catch(Exception e){}
				writer = new PrintWriter((fileDirectory + "/react-component").replace("\\", "/") + "/" + namespace + "_" + modelClass.getName() + ".js", "UTF-8");
				writer.println(GenerateReactComponents.generateReactComponent(modelClass.getName(), project, viewManager, modelClass, parentFrame, namespace));
				writer.close();
			}
			catch(Exception e){
				viewManager.showMessageDialog(parentFrame, "Error " + e.toString() + " on line " + e.getStackTrace()[0].getLineNumber());
			}
		}
	}
	
	private void endCode(String fileDirectory, String endCode, String fileName){
		try{
			Files.write(Paths.get(fileDirectory.replace("\\", "/") + "/" + fileName), endCode.getBytes(), StandardOpenOption.APPEND);
		}
		catch(Exception e){}
	}
	
	private void consoleLog(String log, ViewManager viewManager){
		viewManager.showMessage(log);
	}
}
