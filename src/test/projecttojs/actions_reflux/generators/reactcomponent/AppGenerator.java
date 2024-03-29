package test.projecttojs.actions_reflux.generators.reactcomponent;

import com.vp.plugin.model.IAssociation;
import com.vp.plugin.model.IAssociationEnd;
import com.vp.plugin.model.ITaggedValue;
import test.projecttojs.actions_reflux.ClassDefinition;
import test.projecttojs.actions_reflux.Helpers;
import test.projecttojs.actions_reflux.generators.DefaultSingleGenerator;
import test.projecttojs.actions_reflux.generators.Generator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AppGenerator extends DefaultSingleGenerator implements Generator {
    public AppGenerator(ClassDefinition definition) {
        super(definition);
    }

    @Override
    public void generateFullText() {
        List<IAssociation> outBoundRoutes = new ArrayList<>();

        for (IAssociation association : this.getDefinition().getAssociations()) {
            IAssociationEnd thisEnd = Helpers.getAssociationEnd(association, this.getDefinition(), false);
            if (thisEnd.getAggregationKind().equals("Composited") && Helpers.stringExistsInIterator(association.stereotypeIterator(), "Route")) {
                outBoundRoutes.add(association);
            }
        }

        String importsCode = "";
        importsCode += "var initApp = require('./initApp');\n" +
                "import React from 'react';\n" +
                "import ReactDOM from 'react-dom';\n" +
                "import { BrowserRouter as Router, Route, Link, Switch } from 'react-router-dom';\n" +
                "import " + this.getDefinition().getName() + " from './react-component/" + this.getDefinition().getName() + "';\n";
        for (IAssociation association : outBoundRoutes) {
            IAssociationEnd thatEnd = Helpers.getAssociationEnd(association, this.getDefinition(), true);
            ClassDefinition endClassDefinition = new ClassDefinition(thatEnd.getTypeAsModel().getId(), false);
            importsCode += "import " + endClassDefinition.getName() + " from './react-component/" + endClassDefinition.getName() + "';\n";
        }

        String routeCode = "";
        IAssociation indexRoute = Helpers.getFromElementList(this.getDefinition().getAssociations(), IAssociation::stereotypeIterator, i -> Helpers.stringExistsInIterator(i, "DefaultRoute"));
        if (indexRoute != null) {
            IAssociationEnd thatEnd = Helpers.getAssociationEnd(indexRoute, this.getDefinition(), true);
            ClassDefinition endDefinition = new ClassDefinition(thatEnd.getTypeAsModel().getId(), false);
            routeCode += "                <Route path='/' exact render={(props) => (<" + endDefinition.getName() + " {...props} />)} />\n";
        }

        for (IAssociation association : outBoundRoutes) {
            IAssociationEnd thatEnd = Helpers.getAssociationEnd(association, this.getDefinition(), true);
            ITaggedValue routeValues = null;
            if (association.getTaggedValues() != null) {
                routeValues = Helpers.getFromElementList(Arrays.asList(association.getTaggedValues().toTaggedValueArray()), ITaggedValue::getName, n -> n.equals("Routes"));
            }

            if (routeValues != null) {
                String[] routes = routeValues.getValueAsString().replace("[", "").replace("]", "").split(",");
                for (String route : routes) {
                    route = route.trim();
                    ClassDefinition endDefinition = new ClassDefinition(thatEnd.getTypeAsModel().getId(), false);
                    routeCode += "                <Route path='/" + route + "' render={(props) => (<" + endDefinition.getName() + " {...props} />)} />\n";
                }
            }
        }

        this.appendFullText("// App\n" +
                "// \n" +
                "// Generated by IC STRATEGY\n" +
                "//\n" +
                "// WARNING: Do not change this code; it will be overwritten by the next generation run!\n" +
                "//          Change the code only in Visual Paradigm.\n\n" +
                importsCode + "\n" +
                "$(window).on('load', function() {\n" +
                "    ReactDOM.render(\n" +
                "        <Router>\n" +
                "            <" + this.getDefinition().getName() + ">\n" +
                routeCode +
                "            </" + this.getDefinition().getName() + ">\n" +
                "        </Router>,\n" +
                "        document.getElementById('app-container')\n" +
                "    );\n" +
                "});\n");
    }

    public String getFolder() {
        return "";
    }

    public String getName() {
        return "App";
    }
}
