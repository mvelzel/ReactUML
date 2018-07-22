package test.projecttojs.actions_reflux;

import com.vp.plugin.action.VPAction;
import com.vp.plugin.action.VPActionController;
import com.vp.plugin.model.*;
import test.projecttojs.actions_reflux.generators.Generator;
import test.projecttojs.actions_reflux.generators.MultiGenerator;
import test.projecttojs.actions_reflux.generators.classes.ClassGenerator;
import test.projecttojs.actions_reflux.generators.controller.ControllerGenerator;
import test.projecttojs.actions_reflux.generators.domainentity.DomainEntityAPIGenerator;
import test.projecttojs.actions_reflux.generators.domainentity.DomainEntityGenerator;
import test.projecttojs.actions_reflux.generators.domainentity.DomainEntityListEndGenerator;
import test.projecttojs.actions_reflux.generators.domainentity.DomainEntityListGenerator;
import test.projecttojs.actions_reflux.generators.form.FormGenerator;
import test.projecttojs.actions_reflux.generators.form.FormListEndGenerator;
import test.projecttojs.actions_reflux.generators.form.FormListGenerator;
import test.projecttojs.actions_reflux.generators.formfield.FormFieldGenerator;
import test.projecttojs.actions_reflux.generators.formfield.FormFieldListEndGenerator;
import test.projecttojs.actions_reflux.generators.formfield.FormFieldListGenerator;
import test.projecttojs.actions_reflux.generators.reactcomponent.AppGenerator;
import test.projecttojs.actions_reflux.generators.reactcomponent.ReactComponentGenerator;
import test.projecttojs.actions_reflux.generators.store.InitAppGenerator;
import test.projecttojs.actions_reflux.generators.store.StoreGenerator;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ProjectToJsActionController implements VPActionController {
    public static final int MAX_THREADS = 15;

    public void performAction(VPAction action) {
        IPrintStream ps = new IPrintStream();
        System.setOut(ps);
        System.setErr(ps);
        long startTime = System.currentTimeMillis();
        FileWriter writer = new FileWriter();
        writer.mainDirDialog();

        IProject project = ProjectData.getMainProject();
        Iterator<IModelElement> modelIterator = project.allLevelModelElementIterator();

        DomainEntityAPIGenerator domainAPI = new DomainEntityAPIGenerator();

        DomainEntityListGenerator entityList = new DomainEntityListGenerator();
        DomainEntityListEndGenerator entityListEnd = new DomainEntityListEndGenerator();

        FormListGenerator formList = new FormListGenerator();
        FormListEndGenerator formListEnd = new FormListEndGenerator();

        FormFieldListGenerator formFieldList = new FormFieldListGenerator();
        FormFieldListEndGenerator formFieldListEnd = new FormFieldListEndGenerator();

        InitAppGenerator initApp = new InitAppGenerator();

        ThreadManager generatorThreads = new ThreadManager(MAX_THREADS);

        List<Generator> generators = new ArrayList<>();
        while (modelIterator.hasNext()) {
            IModelElement modelElement = modelIterator.next();
            if (modelElement.getModelType().equals("Class")) {
                IClass modelClass = (IClass) modelElement;
                if (Helpers.stringExistsInIterator(modelClass.stereotypeIterator(), "ReactComponent")) {
                    ClassDefinition modelDefinition = new ClassDefinition(modelClass.getId(), true);
                    ReactComponentGenerator generator = new ReactComponentGenerator(modelDefinition);
                    generatorThreads.addThread(generator);

                    List<IAssociation> inBoundRoutes = new ArrayList<>();
                    List<IAssociation> outBoundRoutes = new ArrayList<>();

                    for (IAssociation association : modelDefinition.getAssociations()) {
                        IAssociationEnd thisEnd =  Helpers.getAssociationEnd(association, modelDefinition, false);
                        IAssociationEnd thatEnd =  Helpers.getAssociationEnd(association, modelDefinition, true);

                        if (thatEnd.getAggregationKind().equals("Composited") && Helpers.stringExistsInIterator(association.stereotypeIterator(), "Route")) {
                            inBoundRoutes.add(association);
                        } else if (thisEnd.getAggregationKind().equals("Composited") && Helpers.stringExistsInIterator(association.stereotypeIterator(), "Route") && Helpers.stringExistsInIterator(modelClass.stereotypeIterator(), "RootRoute")) {
                            outBoundRoutes.add(association);
                        }
                    }

                    if (inBoundRoutes.size() == 0 && outBoundRoutes.size() > 0) {
                        AppGenerator appGenerator = new AppGenerator(modelDefinition);
                        generatorThreads.addThread(appGenerator);

                        generators.add(appGenerator);
                    }

                    generators.add(generator);
                } else if (Helpers.stringExistsInIterator(modelClass.stereotypeIterator(), "DomainEntity")) {
                    ClassDefinition modelDefinition = new ClassDefinition(modelClass.getId(), false);
                    ClassDefinition modelDefinitionGen = new ClassDefinition(modelClass.getId(), true);
                    DomainEntityGenerator domainGenerator = new DomainEntityGenerator(modelDefinition);
                    generatorThreads.addThread(domainGenerator);

                    ControllerGenerator controllerGenerator = new ControllerGenerator(modelDefinitionGen);
                    generatorThreads.addThread(controllerGenerator);

                    FormGenerator formGenerator = new FormGenerator(modelDefinitionGen);
                    generatorThreads.addThread(formGenerator);

                    StoreGenerator storeGenerator = new StoreGenerator(modelDefinitionGen);
                    generatorThreads.addThread(storeGenerator);

                    domainAPI.setDefinition(modelDefinitionGen);
                    domainAPI.generateFullText();

                    entityList.setDefinition(modelDefinition);
                    entityList.generateFullText();
                    entityListEnd.setDefinition(modelDefinition);
                    entityListEnd.generateFullText();

                    formList.setDefinition(modelDefinition);
                    formList.generateFullText();
                    formListEnd.setDefinition(modelDefinition);
                    formListEnd.generateFullText();

                    initApp.setDefinition(modelDefinition);
                    initApp.generateFullText();

                    generators.add(domainGenerator);
                    generators.add(controllerGenerator);
                    generators.add(formGenerator);
                    generators.add(storeGenerator);
                } else if (Helpers.stringExistsInIterator(modelClass.stereotypeIterator(), "Class")) {
                    ClassDefinition modelDefinition = new ClassDefinition(modelClass.getId(), false);

                    ClassGenerator classGenerator = new ClassGenerator(modelDefinition);
                    generatorThreads.addThread(classGenerator);
                    generators.add(classGenerator);
                } else if (Helpers.stringExistsInIterator(modelClass.stereotypeIterator(), "FormField")) {
                    ClassDefinition modelDefinition = new ClassDefinition(modelClass.getId(), false);

                    FormFieldGenerator formFieldGenerator = new FormFieldGenerator(modelDefinition);
                    generatorThreads.addThread(formFieldGenerator);
                    generators.add(formFieldGenerator);

                    formFieldList.setDefinition(modelDefinition);
                    formFieldList.generateFullText();
                    formFieldListEnd.setDefinition(modelDefinition);
                    formFieldListEnd.generateFullText();
                }
            }
        }

        entityList.appendFullText(entityListEnd.getFullText());
        formList.appendFullText(formListEnd.getFullText());
        formFieldList.appendFullText(formFieldListEnd.getFullText());
        generators.add(entityList);
        generators.add(formList);
        generators.add(domainAPI);
        generators.add(initApp);
        generators.add(formFieldList);

        generatorThreads.join();

        ThreadManager writerThreads = new ThreadManager(MAX_THREADS);

        generators.forEach(g -> {
            if (g instanceof MultiGenerator)
                ((MultiGenerator) g).generateEndText();
            writerThreads.addThread(new RunnableWriter(writer, g));
        });

        writerThreads.join();

        long endTime = System.currentTimeMillis();
        long elapsed = endTime - startTime;
        Helpers.log("Finished generating code, time elapsed: " + elapsed);
    }

    public void update(VPAction action) {

    }
}
