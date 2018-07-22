package test.projecttojs.actions;

import com.vp.plugin.action.VPAction;
import com.vp.plugin.action.VPActionController;
import com.vp.plugin.model.*;
import test.projecttojs.actions.generators.Generator;
import test.projecttojs.actions.generators.MultiGenerator;
import test.projecttojs.actions.generators.action.ActionInitEndGenerator;
import test.projecttojs.actions.generators.action.ActionInitGenerator;
import test.projecttojs.actions.generators.action.ActionListEndGenerator;
import test.projecttojs.actions.generators.action.ActionListGenerator;
import test.projecttojs.actions.generators.classes.ClassGenerator;
import test.projecttojs.actions.generators.domainentity.DomainEntityGenerator;
import test.projecttojs.actions.generators.domainentity.DomainEntityListEndGenerator;
import test.projecttojs.actions.generators.domainentity.DomainEntityListGenerator;
import test.projecttojs.actions.generators.reactcomponent.AppGenerator;
import test.projecttojs.actions.generators.reactcomponent.ReactComponentGenerator;
import test.projecttojs.actions.generators.reducer.CombineReducerEndGenerator;
import test.projecttojs.actions.generators.reducer.CombineReducerGenerator;
import test.projecttojs.actions.generators.reducer.ReducerGenerator;

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

        DomainEntityListGenerator entityList = new DomainEntityListGenerator();
        DomainEntityListEndGenerator entityListEnd = new DomainEntityListEndGenerator();

        ActionListGenerator actionList = new ActionListGenerator();
        ActionListEndGenerator actionListEnd = new ActionListEndGenerator();

        ActionInitGenerator actionInit = new ActionInitGenerator();
        ActionInitEndGenerator actionInitEnd = new ActionInitEndGenerator();

        CombineReducerGenerator combineReducer = new CombineReducerGenerator();
        CombineReducerEndGenerator combineReducerEnd = new CombineReducerEndGenerator();

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
                    //ClassDefinition modelDefinitionGen = new ClassDefinition(modelClass.getId(), true);
                    DomainEntityGenerator domainGenerator = new DomainEntityGenerator(modelDefinition);
                    generatorThreads.addThread(domainGenerator);

                    ReducerGenerator reducerGenerator = new ReducerGenerator(modelDefinition);
                    generatorThreads.addThread(reducerGenerator);

                    entityList.setDefinition(modelDefinition);
                    entityList.generateFullText();
                    entityListEnd.setDefinition(modelDefinition);
                    entityListEnd.generateFullText();

                    combineReducer.setDefinition(modelDefinition);
                    combineReducer.generateFullText();
                    combineReducerEnd.setDefinition(modelDefinition);
                    combineReducerEnd.generateFullText();

                    actionList.setDefinition(modelDefinition);
                    actionList.generateFullText();
                    actionListEnd.setDefinition(modelDefinition);
                    actionListEnd.generateFullText();

                    actionInit.setDefinition(modelDefinition);
                    actionInit.generateFullText();
                    actionInitEnd.setDefinition(modelDefinition);
                    actionInitEnd.generateFullText();

                    generators.add(domainGenerator);
                    generators.add(reducerGenerator);
                } else if (Helpers.stringExistsInIterator(modelClass.stereotypeIterator(), "Class")) {
                    ClassDefinition modelDefinition = new ClassDefinition(modelClass.getId(), false);

                    ClassGenerator classGenerator = new ClassGenerator(modelDefinition);
                    generatorThreads.addThread(classGenerator);
                    generators.add(classGenerator);
                }
            }
        }

        entityList.appendFullText(entityListEnd.getFullText());
        combineReducer.appendFullText(combineReducerEnd.getFullText().substring(0, combineReducerEnd.getFullText().length() - 2) + "\n");
        actionList.appendFullText(actionListEnd.getFullText().substring(0, actionListEnd.getFullText().length() - 2) + "\n");
        actionInit.appendFullText(actionInitEnd.getFullText());
        generators.add(actionInit);
        generators.add(actionList);
        generators.add(entityList);
        generators.add(combineReducer);

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
