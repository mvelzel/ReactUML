package test.projecttojs.actions_reflux.generators.reactcomponent;

import com.vp.plugin.model.IAssociation;
import test.projecttojs.actions_reflux.ClassDefinition;
import test.projecttojs.actions_reflux.Helpers;
import test.projecttojs.actions_reflux.generators.DefaultSingleGenerator;
import test.projecttojs.actions_reflux.generators.Generator;

import java.util.ArrayList;
import java.util.List;

public class ImportsGenerator extends DefaultSingleGenerator implements Generator {
    public ImportsGenerator(ClassDefinition definition){
        super(definition);
    }

    @Override
    public void generateFullText() {
        if (Helpers.stringExistsInIterator(this.getDefinition().getStereotypes().iterator(), "formstore"))
            this.appendFullText("var FormActions = require('../../../js/1-presentation/services/actions/FormActions');\n");
        if (Helpers.stringExistsInIterator(this.getDefinition().getStereotypes().iterator(), "errors"))
            this.appendFullText("var ErrorActions = require('../../../js/3-domain/actions/ErrorActions');\n");

        this.appendFullText("var DomainAPI = require('../domain-entity/DomainAPI');\n" +
                "import React from 'react';\n" +
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

        if (Helpers.stringExistsInIterator(this.getDefinition().getStereotypes().iterator(), "form"))
            this.appendFullText("import { Form } from '../../../js/1-presentation/services/meta/Form';\n" +
                    "import * as FormField from '../../../js/1-presentation/services/meta/FormField';\n");

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
    }
}
