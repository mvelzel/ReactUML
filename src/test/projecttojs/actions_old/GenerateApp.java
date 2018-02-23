package test.projecttojs.actions_old;

import java.awt.Component;
import java.util.*;

import org.apache.commons.lang3.tuple.*;

import com.vp.plugin.ViewManager;
import com.vp.plugin.model.*;

public class GenerateApp extends Helpers {
    public static String generateApp(String className, IClass definitionClass, ViewManager viewManager, IProject project, Component parentFrame, String namespace) {
        String code = "";
        classDefinition definition = GetFullClassDefinition(definitionClass.getId(), project, viewManager, parentFrame, true, namespace);

        List<IAssociation> fromOutboundRoutes = new ArrayList<>();
        List<IAssociation> toOutboundRoutes = new ArrayList<>();
        for (IAssociation association : definition.fromAssociations) {
            IAssociationEnd AssociationEnd = (IAssociationEnd) association.getFromEnd();
            if (AssociationEnd.getAggregationKind().equals("Composited") && stringExistsInIterator(association.stereotypeIterator(), "Route"))
                fromOutboundRoutes.add(association);
        }
        for (IAssociation association : definition.toAssociations) {
            IAssociationEnd AssociationEnd = (IAssociationEnd) association.getToEnd();
            if (AssociationEnd.getAggregationKind().equals("Composited") && stringExistsInIterator(association.stereotypeIterator(), "Route"))
                toOutboundRoutes.add(association);
        }

        code +=
                "// App\n" +
                        "// \n" +
                        "// Generated by IC STRATEGY\n" +
                        "//\n" +
                        "// WARNING: Do not change this code; it will be overwritten by the next generation run!\n" +
                        "//          Change the code only in Visual Paradigm.\n\n" +
                        //"import * as _ from 'lodash';\n" +
                        //"var React = require('react');\n" +
                        "var initApp = require('./initApp');\n" +
                        "import React from 'react';\n" +
                        "import ReactDOM from 'react-dom';\n" +
                        "import { BrowserRouter as Router, Route, Link, Switch } from 'react-router-dom';\n" +
                        "import " + definition.getName() + " from './react-component/" + definition.getName() + "';\n";

        for (IAssociation route : fromOutboundRoutes) {
            IAssociationEnd thatEnd = (IAssociationEnd) route.getToEnd();
            code +=
                    "import " + GetFullClassDefinition(thatEnd.getTypeAsModel().getId(), project, viewManager, parentFrame, false, namespace).name + " from './react-component/" + GetFullClassDefinition(thatEnd.getTypeAsModel().getId(), project, viewManager, parentFrame, false, namespace).name + "';\n";
        }
        for (IAssociation route : toOutboundRoutes) {
            IAssociationEnd thatEnd = (IAssociationEnd) route.getFromEnd();
            code +=
                    "import " + GetFullClassDefinition(thatEnd.getTypeAsModel().getId(), project, viewManager, parentFrame, false, namespace).name + " from './react-component/" + GetFullClassDefinition(thatEnd.getTypeAsModel().getId(), project, viewManager, parentFrame, false, namespace).name + "';\n";
        }
        code +=
                "\n" +
                        "$(window).load(function() {\n" +
                        "   ReactDOM.render(\n" +
                        "       <Router>\n" +
                        "           <" + definition.getName() + ">\n";
        IAssociation fromIndexRoute = null;
        IAssociation toIndexRoute = null;
        for (IAssociation route : fromOutboundRoutes) {
            if (stringExistsInIterator(route.stereotypeIterator(), "DefaultRoute")) {
                fromIndexRoute = route;
                break;
            }
        }
        for (IAssociation route : toOutboundRoutes) {
            if (stringExistsInIterator(route.stereotypeIterator(), "DefaultRoute")) {
                toIndexRoute = route;
                break;
            }
        }
        if (fromIndexRoute != null) {
            IAssociationEnd thatEnd = (IAssociationEnd) fromIndexRoute.getToEnd();
            code += "               <Route path='/' exact render={(props) => (<" + GetFullClassDefinition(thatEnd.getTypeAsModel().getId(), project, viewManager, parentFrame, false, namespace).name + " {...props} />)} />\n";
        }
        if (toIndexRoute != null) {
            IAssociationEnd thatEnd = (IAssociationEnd) fromIndexRoute.getFromEnd();
            code += "               <Route path='/' exact render={(props) => (<" + GetFullClassDefinition(thatEnd.getTypeAsModel().getId(), project, viewManager, parentFrame, false, namespace).name + " {...props} />)} />\n";
        }

        List<Pair<String, String>> routes = new ArrayList<>();
        for (IAssociation route : fromOutboundRoutes) {
            IAssociationEnd thatEnd = (IAssociationEnd) route.getToEnd();
            ITaggedValue routeValues = null;
            if (route.getTaggedValues() != null) {
                for (Iterator<?> iter = route.getTaggedValues().taggedValueIterator(); iter.hasNext(); ) {
                    ITaggedValue taggedValue = (ITaggedValue) iter.next();
                    if (taggedValue.getName().equals("Routes")) {
                        routeValues = taggedValue;
                        break;
                    }
                }
            }
            if (routeValues != null) {
                List<String> routeStrings = Arrays.asList(routeValues.getValueAsString().replace("[", "").replace("]", "").replaceAll("/\'/g", "").split(","));
                for (String routeString : routeStrings) {
                    routeString = routeString.replaceAll("/^\\s+|\\s+$/g", "");
                    Pair<String, String> pair = new ImmutablePair<String, String>(routeString, GetFullClassDefinition(thatEnd.getTypeAsModel().getId(), project, viewManager, parentFrame, false, namespace).name);
                    routes.add(pair);
                }
            }
        }

        for (Pair<String, String> pair : routes) {
            code += "               <Route path='/" + pair.getKey() + "' render={(props) => (<" + pair.getValue() + " {...props} />)} />\n";
        }

        code +=
                "           </" + definition.getName() + ">\n" +
                "       </Router>,\n" +
                "       document.getElementById('app-container')\n" +
                "   );\n" +
                "});\n";

        return code;
    }
}
