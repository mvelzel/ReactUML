package test.projecttojs.actions.generators.reactcomponent;

import com.vp.plugin.model.*;
import test.projecttojs.actions.ClassDefinition;
import test.projecttojs.actions.generators.Generator;
import test.projecttojs.actions.Helpers;
import test.projecttojs.actions.generators.DefaultSingleGenerator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ImportsGenerator extends DefaultSingleGenerator implements Generator {
    public ImportsGenerator(ClassDefinition definition){
        super(definition);
    }

    @Override
    public void generateFullText() {
        if (Helpers.stringExistsInIterator(this.getDefinition().getStereotypes().iterator(), "errors"))
            this.appendFullText("var ErrorActions = require('../../../js/3-domain/actions/ErrorActions');\n");

        this.appendFullText("import React from 'react';\n" +
                "import * as _ from 'lodash';\n");

        if (this.getDefinition().getRealizationClass() != null) {
            ClassDefinition def = new ClassDefinition(this.getDefinition().getRealizationClass().getFrom().getId(), false);
            this.appendFullText("import " + def.getName() + " from './" + def.getName() + "';\n");
        }

        if (Helpers.stringExistsInIterator(this.getDefinition().getStereotypes().iterator(), "history") || Helpers.stringExistsInIterator(this.getDefinition().getStereotypes().iterator(), "match"))
            this.appendFullText("import { withRouter } from 'react-router-dom';\n");

        if (Helpers.stringExistsInIterator(this.getDefinition().getStereotypes().iterator(), "firebase"))
            this.appendFullText("import * as firebase from 'firebase';\n" +
                    "import { FirebaseManager } from '../../../js/4-infrastructure/databaseManagers/FirebaseManager';\n");


        List<IAttribute> connections = Helpers.filterElementList(this.getDefinition().getAttributes(),
                c -> Arrays.asList(c.toStereotypeArray()),
                ss -> ss.contains("connect") || ss.contains("connectRoute") || ss.contains("load") || ss.contains("formconnect"));

        if (connections.size() > 0) {
            this.appendFullText("import { connect } from 'react-redux';\n");
        }
        this.appendFullText("import * as ActionList from '../action';\n");

        boolean routeFound = false;
        List<String> iterated = new ArrayList<>();
        for (IAssociation association : this.getDefinition().getAssociations()) {
            ClassDefinition def = new ClassDefinition(Helpers.getAssociationEnd(association, this.getDefinition(), true).getModelElement().getId(), false);
            if (Helpers.getAssociationEnd(association, this.getDefinition(), false).getAggregationKind().equals("Composited") && !Helpers.stringExistsInIterator(iterated.iterator(), def.getName())) {
                iterated.add(def.getName());
                if(Helpers.stringExistsInIterator(association.stereotypeIterator(), "Route")) {
                    if (!routeFound) {
                        this.appendFullText("import { Switch } from 'react-router-dom';\n");
                        routeFound = true;
                    }
                } else {
                    this.appendFullText("import " + def.getName() + " from './" + def.getName() + "';\n");
                }
            }
        }

        List<String> entityIterated = new ArrayList<>();
        for (IOperation operation : this.getDefinition().getOperations()) {
            if (Helpers.stringExistsInIterator(operation.stereotypeIterator(), "controller")) {
                String entityName = this.getDefinition().getName().split("_")[0] + "_" + operation.getName().split("_")[0];
                if (!entityIterated.contains(entityName)) {
                    this.appendFullText("import * as " + entityName + "Controller from '../controller/" + entityName + "';\n");
                    entityIterated.add(entityName);
                }
            }
        }

        for (IStereotype stereotype : this.getDefinition().getOriginalClass().toStereotypeModelArray()) {
            if (stereotype.getTaggedValueDefinitions() != null) {
                ITaggedValueDefinition importTag = Helpers.getFromElementList(Arrays.asList(stereotype.getTaggedValueDefinitions().toTaggedValueDefinitionArray()), ITaggedValueDefinition::getName, n -> n.equals("import"));
                if (importTag != null) {
                    this.appendFullText(importTag.getDefaultValue());
                }
            }
       }
    }
}
