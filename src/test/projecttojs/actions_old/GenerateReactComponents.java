package test.projecttojs.actions_old;

import java.awt.Component;
import java.util.*;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.json.*;

import com.vp.plugin.ViewManager;
import com.vp.plugin.model.*;


public class GenerateReactComponents extends Helpers {


    public static String generateReactComponent(String modelClass, IProject project, ViewManager viewManager, IClass definitionClass, Component parentFrame, String namespace) {
        try {
            String code = "";
            Boolean firstIter = true;

            classDefinition definition = GetFullClassDefinition(definitionClass.getId(), project, viewManager, parentFrame, true, namespace);


            List<IAttribute> propAttributes = new ArrayList<>();
            for (Iterator<?> iter = definition.attributes.iterator(); iter.hasNext(); ) {
                IAttribute attribute = (IAttribute) iter.next();
                if (attribute.getVisibility().equals("public"))
                    propAttributes.add(attribute);
            }
            String defaultPropsCode = "";
            if (definition.attributes.size() > 0) {

                ITaggedValue labelTag = null;
                if (definition.taggedValues.size() > 0) {
                    for (int i = 0; i < definition.taggedValues.size(); i++) {
                        ITaggedValue tag = definition.taggedValues.get(i);
                        if (tag.getName().equals("label")) {
                            labelTag = tag;
                            break;
                        }
                    }
                }

                String label = "";
                try {
                    if (labelTag != null) {
                        if (labelTag.getValueAsString() != null) {
                            label = labelTag.getValueAsString();
                            defaultPropsCode += "           label: " + label;
                        }
                    }
                } catch (Exception e) {
                }

                for (IAttribute propAttribute : propAttributes) {
                    if (propAttribute.getInitialValue() != null) {
                        if (!firstIter || !label.isEmpty())
                            defaultPropsCode += ",\n";
                        String defaultValue = "";
                        if (!propAttribute.getInitialValue().equals("$attribute.getInitialValue().getName()"))
                            defaultValue = propAttribute.getInitialValue();
                        else
                            defaultValue = null;
                        defaultPropsCode += "           " + propAttribute.getName() + ": " + defaultValue;
                    }
                    firstIter = false;
                }
            }

            IOperation getInitialState = null;
            for (Iterator<?> iter = definition.operations.iterator(); iter.hasNext(); ) {
                IOperation operation = (IOperation) iter.next();
                if (operation.getName().equals("getInitialState")) {
                    getInitialState = operation;
                    break;
                }
            }
            String initialStateCode = "";
            if (getInitialState != null) {
                initialStateCode += "       this.state = function() {\n";
                initialStateCode += unEscapedJsonString(getInitialState.getJavaDetail().getImplModel().getCode(), "       ") + "\n";
                initialStateCode += "       };\n";
            } else {
                List<IAttribute> stateAttributes = new ArrayList<>();
                for (Iterator<?> iter = definition.attributes.iterator(); iter.hasNext(); ) {
                    IAttribute attribute = (IAttribute) iter.next();
                    if (!attribute.getVisibility().equals("public"))
                        stateAttributes.add(attribute);
                }
                if (stateAttributes.size() > 0) {
                    firstIter = true;
                    for (IAttribute stateAttribute : stateAttributes) {
                        if (stateAttribute.getInitialValue() != null && !stateAttribute.getInitialValue().isEmpty()) {
                            if (!firstIter)
                                initialStateCode += ",\n";
                            String defaultValue = "";
                            if (!stateAttribute.getInitialValue().equals("$attribute.getInitialValue().getName()"))
                                defaultValue = stateAttribute.getInitialValue();
                            else
                                defaultValue = null;
                            initialStateCode += "           " + stateAttribute.getName() + ": " + defaultValue;
                        }
                        firstIter = false;
                    }
                    initialStateCode =
                            "       this.state = {\n"
                                    + initialStateCode
                                    + "\n       };\n";
                }
            }
            String willMountCode = "";
            IOperation componentWillMount = null;
            for (Iterator<?> iter = definition.operations.iterator(); iter.hasNext(); ) {
                IOperation operation = (IOperation) iter.next();
                if (operation.getName().equals("componentWillMount")) {
                    componentWillMount = operation;
                    break;
                }
            }


            if (componentWillMount != null) {
                willMountCode += "       " + unEscapedJsonString(componentWillMount.getJavaDetail().getImplModel().getCode(), "       ") + "\n";
            }


            String constructorCode =
                    "   constructor(props) {\n"
                            + "       super(props);\n"
                            + initialStateCode
                            + willMountCode
                            + "   };\n"
                            + "   static defaultProps = {\n"
                            + defaultPropsCode
                            + "\n   };\n";

            String allOperationsCode = "";
            String componentBindCode = "";
            List<IOperation> tempOperations = new ArrayList<>();
            for (Iterator<?> iter = definition.operations.iterator(); iter.hasNext(); ) {
                IOperation operation = (IOperation) iter.next();
                JSONArray defaultOperations = new JSONArray("[\"getDefaultProps\",\"getInitialState\",\"render\",\"componentWillMount\",\"componentDidMount\",\"componentWillReceiveProps\",\"shouldComponentUpdate\",\"componentWillUpdate\",\"componentDidUpdate\",\"componentWillUnmount\"]");
                if (!stringExistsInArray(defaultOperations, operation.getName()))
                    tempOperations.add(operation);
            }

            for (IOperation operation : tempOperations) {
                String operationCode = "";
                Boolean isDebouncedOperation = stringExistsInIterator(operation.stereotypeIterator(), "debounced");
                String debouncePrefix = "";
                String debounceSuffix = "";
                if (isDebouncedOperation) {
                    debouncePrefix += "_.debounce(";
                    debounceSuffix = ",300)";
                }
                Boolean isControllerOperation = stringExistsInIterator(operation.stereotypeIterator(), "controller");
                if (isControllerOperation) {
                    String entityName = namespace + "_" + operation.getName().split("_")[0];
                    String methodName = operation.getName().split("_")[1];
                    String parameterCode = "";

                    for (Iterator<?> iter = operation.parameterIterator(); iter.hasNext(); ) {
                        IParameter parameter = (IParameter) iter.next();
                        parameterCode += parameter.getName() + ": " + findPropValue(parameter.getDefaultValueAsString(), false, definition);
                        if (iter.hasNext())
                            parameterCode += ", ";
                    }
                    operationCode +=
                            "   " + operation.getName().replace("_", "") + " = () => {\n";
                    if (operation.getJavaDetail() != null)
                        if (operation.getJavaDetail().getImplModel() != null)
                            operationCode +=
                                    "       " + unEscapedJsonString(operation.getJavaDetail().getImplModel().getCode(), "       ") + "\n";
                        else
                            operationCode +=
                                    "       var Controller = require('../controller/" + entityName + "');\n" +
                                            "       var self = this;\n" +
                                            "       Controller." + methodName + "({" + parameterCode + "});\n";
                    else
                        operationCode +=
                                "       var Controller = require('../controller/" + entityName + "');\n" +
                                        "       var self = this;\n" +
                                        "       Controller." + methodName + "({" + parameterCode + "});\n";

                } else {
                    String parameterCode = "";
                    for (Iterator<?> iter = operation.parameterIterator(); iter.hasNext(); ) {
                        IParameter parameter = (IParameter) iter.next();
                        parameterCode += parameter.getName();
                        if (iter.hasNext())
                            parameterCode += ", ";
                    }
                    if (operation.getJavaDetail() != null) {
                        if (operation.getJavaDetail().getImplModel() != null) {
                            operationCode +=
                                    "   " + operation.getName().replace("_", "") + " = (" + parameterCode + ") => {\n" +
                                            "       " + unEscapedJsonString(operation.getJavaDetail().getImplModel().getCode(), "       ") + "\n";
                        } else
                            operationCode +=
                                    "   " + operation.getName().replace("_", "") + " = (" + parameterCode + ") => {\n" +
                                            "       " + unEscapedJsonString("", "       ") + "\n";
                    } else
                        operationCode +=
                                "   " + operation.getName().replace("_", "") + " = (" + parameterCode + ") => {\n" +
                                        "       " + unEscapedJsonString("", "       ") + "\n";
                }
                operationCode +=
                        "   };\n";
                allOperationsCode += operationCode;
            }

            String renderCode = "";


            List<IAssociation> compositeFromAssociations = new ArrayList<>();
            List<IAssociation> compositeToAssociations = new ArrayList<>();
            for (IAssociation association : definition.fromAssociations) {
                if (((IAssociationEnd) association.getFromEnd()).getAggregationKind().equals("Composited") && !stringExistsInIterator(association.stereotypeIterator(), "Route"))
                    compositeFromAssociations.add(association);
            }
            for (IAssociation association : definition.toAssociations) {
                if (((IAssociationEnd) association.getToEnd()).getAggregationKind().equals("Composited") && !stringExistsInIterator(association.stereotypeIterator(), "Route"))
                    compositeToAssociations.add(association);
            }
            String requireCode = "";
            List<String> iteratedNames = new ArrayList<>();
            for (IAssociation composite : compositeFromAssociations) {
                IAssociationEnd association = (IAssociationEnd) composite.getToEnd();
                if (!stringExistsInIterator(iteratedNames.iterator(), GetFullClassDefinition(association.getTypeAsModel().getId(), project, viewManager, parentFrame, false, namespace).name)) {
                    requireCode += "import " + GetFullClassDefinition(association.getTypeAsModel().getId(), project, viewManager, parentFrame, false, namespace).name + " from './" + GetFullClassDefinition(association.getTypeAsModel().getId(), project, viewManager, parentFrame, false, namespace).name + "';\n";
                    iteratedNames.add(GetFullClassDefinition(association.getTypeAsModel().getId(), project, viewManager, parentFrame, false, namespace).name);
                }
            }
            for (IAssociation composite : compositeToAssociations) {
                IAssociationEnd association = (IAssociationEnd) composite.getFromEnd();
                if (!stringExistsInIterator(iteratedNames.iterator(), GetFullClassDefinition(association.getTypeAsModel().getId(), project, viewManager, parentFrame, false, namespace).name)) {
                    requireCode += "import " + GetFullClassDefinition(association.getTypeAsModel().getId(), project, viewManager, parentFrame, false, namespace).name + " from './" + GetFullClassDefinition(association.getTypeAsModel().getId(), project, viewManager, parentFrame, false, namespace).name + "';\n";
                    iteratedNames.add(GetFullClassDefinition(association.getTypeAsModel().getId(), project, viewManager, parentFrame, false, namespace).name);
                }
            }

            IOperation render = null;
            for (Iterator<?> iter = definition.operations.iterator(); iter.hasNext(); ) {
                IOperation operation = (IOperation) iter.next();
                if (operation.getName().equals("render")) {
                    render = operation;
                    break;
                }
            }

            if (render != null) {
                if (render.getJavaDetail().getImplModel() != null)
                    renderCode += "       " + unEscapedJsonString(render.getJavaDetail().getImplModel().getCode(), "       ") + "\n";
            } else {
                renderCode += "       var self = this;\n";
                ITaggedValue classNamesTag = null;
                if (definition.taggedValues.size() > 0) {
                    for (int i = 0; i < definition.taggedValues.size(); i++) {
                        ITaggedValue tag = definition.taggedValues.get(i);
                        if (tag.getName().equals("classNames")) {
                            classNamesTag = tag;
                            break;
                        }
                    }
                }

                String classNames = "\"\"";
                if (classNamesTag != null)
                    classNames = findPropValue(classNamesTag.getValueAsString(), false, definition);

                ITaggedValue styleTag = null;
                if (definition.taggedValues.size() > 0) {
                    for (int i = 0; i < definition.taggedValues.size(); i++) {
                        ITaggedValue tag = definition.taggedValues.get(i);
                        if (tag.getName().equals("style")) {
                            styleTag = tag;
                            break;
                        }
                    }
                }

                String style = "{}";
                if (styleTag != null) {
                    if (styleTag.getValueAsString() != null) {
                        if ((styleTag.getValueAsString().charAt(0) == '{' && styleTag.getValueAsString().charAt(styleTag.getValueAsString().length() - 1) == '}') || styleTag.getValueAsString().indexOf("self") > -1)
                            style = styleTag.getValueAsString();
                        else
                            style = "{" + styleTag.getValueAsString() + "}";
                    }
                }
                String properties = "";
                List<ITaggedValue> filteredTags = new ArrayList<>();
                if (definition.taggedValues.size() > 0) {
                    for (int i = 0; i < definition.taggedValues.size(); i++) {
                        ITaggedValue tag = definition.taggedValues.get(i);
                        if (!tag.getName().equals("classNames") && !tag.getName().equals("style"))
                            filteredTags.add(tag);
                    }
                }

                for (ITaggedValue tag : filteredTags) {
                    if (tag.getValueAsString() != null)
                        properties += " " + tag.getName() + "={" + tag.getValueAsString() + "} ";
                }

                String openTag = "<span className={\"" + toDash(definitionClass.getName()) + " \" + " + classNames + "} style={self.props.style ||" + style + "}" + properties + " >";
                String closeTag = "</span>";
                if (stringExistsInIterator(definition.stereotypes.iterator(), "div")) {
                    openTag = "<div className={\"" + toDash(definitionClass.getName()) + " \" + " + classNames + "} style={self.props.style ||" + style + "}" + properties + " >";
                    closeTag = "</div>";
                }
                if (stringExistsInIterator(definition.stereotypes.iterator(), "ul")) {
                    openTag = "<ul className={\"" + toDash(definitionClass.getName()) + " \" + " + classNames + "} style={self.props.style ||" + style + "}" + properties + " >";
                    closeTag = "</ul>";
                }
                if (stringExistsInIterator(definition.stereotypes.iterator(), "li")) {
                    openTag = "<li className={\"" + toDash(definitionClass.getName()) + " \" + " + classNames + "} style={self.props.style ||" + style + "}" + properties + " >";
                    closeTag = "</li>";
                }
                if (stringExistsInIterator(definition.stereotypes.iterator(), "nav")) {
                    openTag = "<nav className={\"" + toDash(definitionClass.getName()) + " \" + " + classNames + "} style={self.props.style ||" + style + "}" + properties + " >";
                    closeTag = "</nav>";
                }
                if (stringExistsInIterator(definition.stereotypes.iterator(), "tile-container")) {
                    openTag = "<div className={\"tile-container  " + toDash(definitionClass.getName()) + " \"" + classNames + "} style={self.props.style ||" + style + "}" + properties + " >";
                    closeTag = "</div>";
                }
                if (stringExistsInIterator(definition.stereotypes.iterator(), "page")) {
                    openTag = "<div className={\"page " + toDash(definitionClass.getName()) + " \" + " + classNames + "} style={self.props.style ||" + style + "}" + properties + " >";
                    closeTag = "</div>";
                }
                for (String stereotype : definition.stereotypes) {
                    if (stereotype.startsWith("tag-")) {
                        String htmlTag = stereotype.split("-")[1];
                        openTag = "<" + htmlTag + " className={\"" + toDash(definitionClass.getName()) + " \" + " + classNames + "} style={self.props.style ||" + style + "}" + properties + " >";
                        closeTag = "</" + htmlTag + ">";
                    }
                }

                JSONArray preConditions = new JSONArray();
                List<IAttribute> preConditionTemp = new ArrayList<>();
                for (Iterator<?> iter = definition.attributes.iterator(); iter.hasNext(); ) {
                    IAttribute attribute = (IAttribute) iter.next();
                    if (attribute.getMultiplicity().startsWith("1"))
                        preConditionTemp.add(attribute);
                }
                for (IAttribute mandatoryAttribute : preConditionTemp) {
                    if (!mandatoryAttribute.getMultiplicity().endsWith("*"))
                        preConditions.put(findPropValue(mandatoryAttribute.getName(), true, definition));
                    if (mandatoryAttribute.getMultiplicity().endsWith("*"))
                        preConditions.put(findPropValue(mandatoryAttribute.getName(), true, definition) + ".length > 0");
                }

                String preCondition = joinStringArray(preConditions, " && ");
                if (preCondition.equals(""))
                    preCondition = "true";

                if (definition.realizationClass != null) {
                    openTag = "<" + GetFullClassDefinition(definition.realizationClass.getFrom().getId(), project, viewManager, parentFrame, true, namespace).name + " className=\"" + toDash(definition.getName()) + "\" ";
                    if (definition.realizationClass.getTaggedValues() != null)
                        for (Iterator<?> iter = definition.realizationClass.getTaggedValues().taggedValueIterator(); iter.hasNext(); ) {
                            ITaggedValue taggedValue = (ITaggedValue) iter.next();
                            String value = "";
                            if (taggedValue.getType() == 4) {
                                value = taggedValue.getValueAsString().toString().replace("\"[", "").replace("]\"", "").replace("\"{", "").replace("}\"", "");
                                openTag += " " + taggedValue.getName() + "={" + value + "}";
                            } else
                                openTag += " " + taggedValue.getName() + "={" + findPropValue(taggedValue.getValueAsString(), false, definition) + "}";
                        }
                    openTag += ">";
                    closeTag = "</" + GetFullClassDefinition(definition.realizationClass.getFrom().getId(), project, viewManager, parentFrame, true, namespace).name + ">";
                }

                renderCode +=
                        "       if (" + preCondition + "){ \n";
                renderCode +=
                        "           return(\n" +
                                "               " + openTag + "\n";
                Boolean routeFound = false;
                // LINE 362 SORTBY
                List<Pair<Boolean, IAssociation>> subComponents = new ArrayList<>();
                for (IAssociation association : definition.fromAssociations) {
                    if (((IAssociationEnd) association.getFromEnd()).getAggregationKind().equals("Composited"))
                        subComponents.add(new ImmutablePair<Boolean, IAssociation>(true, association));
                }
                for (IAssociation association : definition.toAssociations) {
                    if (((IAssociationEnd) association.getToEnd()).getAggregationKind().equals("Composited"))
                        subComponents.add(new ImmutablePair<Boolean, IAssociation>(false, association));
                }

                // NOW SORT THE SUBCOMPONENTS ARRAY LIKE IN LINE 362
                // subComponents = sortJSONArray(subComponents, "thisName");
                List<Pair<Boolean, IAssociation>> sortedSubComponents = new ArrayList<>();
                List<String> sortedNames = new ArrayList<>();
                for (Pair<Boolean, IAssociation> _subComponent : subComponents) {
//				viewManager.showMessage(definition.getName() + " has things " + subComponent.toPropertiesString());
                    IAssociationEnd associationEnd;
                    IAssociation subComponent = _subComponent.getRight();
                    if (_subComponent.getLeft())
                        associationEnd = (IAssociationEnd) subComponent.getFromEnd();
                    else
                        associationEnd = (IAssociationEnd) subComponent.getToEnd();
                    if (associationEnd.getName() == null)
                        sortedNames.add("a");
                    else if (associationEnd.getName().isEmpty())
                        sortedNames.add("a");
                    else
                        sortedNames.add(associationEnd.getName());
                }
//			viewManager.showMessage(definition.getName() + " has order " + String.join(", ", sortedNames));
                Collections.sort(sortedNames);
//			viewManager.showMessage(definition.getName() + " has order " + String.join(", ", sortedNames));

                for (String sortedName : sortedNames) {
                    for (int _i = 0; _i < subComponents.size(); _i++) {
                        IAssociation subComponent = subComponents.get(_i).getRight();
                        Boolean isFrom = subComponents.get(_i).getLeft();
                        IAssociationEnd associationEnd;
                        if (isFrom)
                            associationEnd = (IAssociationEnd) subComponent.getFromEnd();
                        else
                            associationEnd = (IAssociationEnd) subComponent.getToEnd();
                        if (associationEnd.getName() == null) {
                            sortedSubComponents.add(new ImmutablePair<Boolean, IAssociation>(isFrom, subComponent));
                            subComponents.remove(_i);
                            break;
                        } else if (associationEnd.getName().isEmpty()) {
                            sortedSubComponents.add(new ImmutablePair<Boolean, IAssociation>(isFrom, subComponent));
                            subComponents.remove(_i);
                            break;
                        } else {
                            if (associationEnd.getName().equals(sortedName)) {
                                sortedSubComponents.add(new ImmutablePair<Boolean, IAssociation>(isFrom, subComponent));
                                subComponents.remove(_i);
                                break;
                            }
                        }
                    }
                }

                subComponents = sortedSubComponents;

                for (Pair<Boolean, IAssociation> _subComponent : subComponents) {
                    IAssociation subComponent = _subComponent.getRight();
                    Boolean isFrom = _subComponent.getLeft();
                    if (stringExistsInIterator(subComponent.stereotypeIterator(), "Route")) {
                        if (!routeFound) {
                            routeFound = true;
                            requireCode += "import { Switch } from 'react-router-dom';\n";
                            renderCode +=
                                    "                   <Switch>\n" +
                                            "                       { React.Children.map(self.props.children, function(child){\n" +
                                            "                           return React.cloneElement(child, {render: function(props){ return child.props.render(self.state); }});\n" +
                                            "                       })}\n" +
                                            "                   </Switch>\n";
                        }
                    } else {
                        String indent = "                  ";
                        String conditionStart = "";
                        String conditionEnd = "";
                        List<ITaggedValue> conditions = new ArrayList<>();
                        if (subComponent.getTaggedValues() != null) {
                            for (int i = 0; i < subComponent.getTaggedValues().toTaggedValueArray().length; i++) {
                                ITaggedValue taggedValue = subComponent.getTaggedValues().toTaggedValueArray()[i];
                                if (taggedValue.getName().equals("condition") || taggedValue.getName().equals("condition:false"))
                                    conditions.add(taggedValue);
                            }
                        }
                        if (conditions.size() > 0) {
                            conditionStart = indent + "{(";
                            firstIter = true;
                            for (ITaggedValue condition : conditions) {
                                if (!firstIter)
                                    conditionStart += " && ";
                                if (condition.getName().equals("condition:false"))
                                    conditionStart += "!";
                                conditionStart += findPropValue(condition.getValueAsString(), false, definition);
                                firstIter = false;
                            }
                            conditionStart += ") ? ";
                            conditionEnd = " : <span />}";
                            indent = "";
                        }
                        renderCode += conditionStart;
                        IAssociationEnd subComponentToEnd;

                        if (isFrom)
                            subComponentToEnd = (IAssociationEnd) subComponent.getToEnd();
                        else
                            subComponentToEnd = (IAssociationEnd) subComponent.getFromEnd();

                        JSONObject subComponentProps = new JSONObject();
                        classDefinition subComponentClass = GetFullClassDefinition(subComponentToEnd.getTypeAsModel().getId(), project, viewManager, parentFrame, true, namespace);
                        List<IAttribute> tempPropAttributes = new ArrayList<>();
                        for (Iterator<?> iter = subComponentClass.attributes.iterator(); iter.hasNext(); ) {
                            IAttribute attr = (IAttribute) iter.next();

                            if (attr.getVisibility().equals("public"))
                                tempPropAttributes.add(attr);
                        }

                        for (IAttribute propAttribute : tempPropAttributes) {
                            String propValue = findPropValue(propAttribute.getName(), true, definition);
                            if (propValue != null && !propValue.isEmpty())
                                subComponentProps.put(propAttribute.getName(), propValue);
                        }

                        List<ITaggedValue> tempPropValues = new ArrayList<>();
                        if (subComponent.getTaggedValues() != null) {
                            for (int _i = 0; _i < subComponent.getTaggedValues().toTaggedValueArray().length; _i++) {
                                ITaggedValue taggedValue = subComponent.getTaggedValues().toTaggedValueArray()[_i];
                                if (!taggedValue.getName().equals("condition") && !taggedValue.getName().equals("condition:false") && !taggedValue.getName().equals("sort") && !taggedValue.getName().equals("filter") && taggedValue.getName().indexOf("-iterator") == -1)
                                    tempPropValues.add(taggedValue);
                            }
                        }
                        for (ITaggedValue propValue : tempPropValues) {
                            if (propValue.getType() == 4) {
                                String value;
                                value = propValue.getValueAsString().replace("\"[", "").replace("]\"", "").replace("\"{", "").replace("}\"", "");
                                subComponentProps.put(propValue.getName(), value);
                            } else
                                subComponentProps.put(propValue.getName(), findPropValue(propValue.getValueAsString(), false, definition));
                        }

                        ITaggedValue iteratorProp = null;
                        if (subComponent.getTaggedValues() != null) {
                            for (int _i = 0; _i < subComponent.getTaggedValues().toTaggedValueArray().length; _i++) {
                                ITaggedValue tag = subComponent.getTaggedValues().toTaggedValueArray()[_i];
                                if (tag.getName().indexOf("-iterator") > -1) {
                                    iteratorProp = tag;
                                    break;
                                }
                            }
                        }


                        String propsCode = "";
                        Iterator<?> subComponentPropsKeys = subComponentProps.keys();
                        while (subComponentPropsKeys.hasNext()) {
                            String propName = (String) subComponentPropsKeys.next();
                            if (iteratorProp == null || !(propName + "-iterator").equals(iteratorProp.getName()))
                                propsCode += propName + "={" + subComponentProps.getString(propName).replace("_.", "���").replace("_", "").replace("���", "_.") + "} ";
                        }

                        if (!subComponentToEnd.getMultiplicity().endsWith("*"))
                            renderCode += indent + "<" + GetFullClassDefinition(subComponentToEnd.getTypeAsModel().getId(), project, viewManager, parentFrame, false, namespace).name + " " + propsCode + "/>";
                        if (subComponentToEnd.getMultiplicity().endsWith("*")) {
                            if (conditionStart.equals(""))
                                conditionStart = "{";
                            renderCode += conditionStart;
                            if (conditionEnd.equals(""))
                                conditionEnd = "}";
                            if (iteratorProp != null) {
                                String prop = findPropValue(iteratorProp.getValueAsString(), false, definition);
                                String propBase = findPropBase(iteratorProp.getValueAsString(), false, definition);
                                ITaggedValue filter = null;
                                if (subComponent.getTaggedValues() != null) {
                                    for (int _i = 0; _i < subComponent.getTaggedValues().toTaggedValueArray().length; _i++) {
                                        ITaggedValue tag = subComponent.getTaggedValues().toTaggedValueArray()[_i];
                                        if (tag.getName().equals("filter")) {
                                            filter = tag;
                                            break;
                                        }
                                    }
                                }

                                if (filter != null)
                                    prop = "_.filter(" + prop + ", function(item) { return " + filter.getValueAsString() + ";})";

                                ITaggedValue sortBy = null;
                                if (subComponent.getTaggedValues() != null) {
                                    for (int _i = 0; _i < subComponent.getTaggedValues().toTaggedValueArray().length; _i++) {
                                        ITaggedValue tag = subComponent.getTaggedValues().toTaggedValueArray()[_i];
                                        if (tag.getName().equals("sort")) {
                                            sortBy = tag;
                                            break;
                                        }
                                    }
                                }

                                if (sortBy != null)
                                    prop = "_.sortBy(" + prop + ", function(item) { return " + sortBy.getValueAsString() + ";})";

                                String key = "";
                                String tempFindSubComponentProps = "";
                                try {
                                    Iterator<?> keys = subComponentProps.keys();
                                    while (keys.hasNext()) {
                                        String propName = (String) keys.next();
                                        if (propName.equals("key")) {
                                            tempFindSubComponentProps = propName;
                                            break;
                                        }
                                    }
                                } catch (Exception e) {
                                }

                                if (tempFindSubComponentProps.equals(""))
                                    key = " key={item.id || index} ";

                                String propBaseCondition = "";
                                if (propBase != null && !propBase.equals(""))
                                    propBaseCondition = propBase + " && ";
                                renderCode +=
                                        "                  " + propBaseCondition + prop + " ? " + prop + ".map(function(item, index) {\n" +
                                                "                      return(<" + GetFullClassDefinition(subComponentToEnd.getTypeAsModel().getId(), project, viewManager, parentFrame, false, namespace).name + " " + iteratorProp.getName().replace("-iterator", "") + "={item} index={index} " + key + propsCode + "/>);\n" +
                                                "                  }) : ''";
                            }
                        }
                        renderCode += conditionEnd;
                        renderCode += "\n";
                    }
                }

                renderCode += "               " + closeTag + "\n" +
                        "           );\n" +
                        "       } else {\n" +
                        "           return (<span />);\n" +
                        "       }\n";
            }


            allOperationsCode +=
                    "   componentDidMount() {\n";
            List<IAttribute> connections = new ArrayList<>();
            for (Iterator<?> iter = definition.attributes.iterator(); iter.hasNext(); ) {
                IAttribute attribute = (IAttribute) iter.next();
                if (stringExistsInIterator(attribute.stereotypeIterator(), "connect") || stringExistsInIterator(attribute.stereotypeIterator(), "connectRoute") || stringExistsInIterator(attribute.stereotypeIterator(), "load"))
                    connections.add(attribute);
            }
            for (IAttribute connection : connections) {
                String suffix = "";
                allOperationsCode +=
                        "       DomainAPI." + GetFullClassDefinition(connection.getTypeAsModel().getId(), project, viewManager, parentFrame, false, namespace).name + suffix + "Connect(this, '" + connection.getName() + "');\n";
                if (stringExistsInIterator(connection.stereotypeIterator(), "connectRoute"))
                    allOperationsCode +=
                            "       DomainAPI." + GetFullClassDefinition(connection.getTypeAsModel().getId(), project, viewManager, parentFrame, false, namespace).name + suffix + "LoadItem(this.props.params.id);\n";
                if (stringExistsInIterator(connection.stereotypeIterator(), "load"))
                    allOperationsCode +=
                            "       if (this.props." + connection.getName() + ".id && this.props." + connection.getName() + ".loadStatus === 'proxy') { DomainAPI." + GetFullClassDefinition(connection.getTypeAsModel().getId(), project, viewManager, parentFrame, false, namespace).name + suffix + "LoadItem(this.props." + connection.getName() + ".id); }\n";
            }
            if (stringExistsInIterator(definition.stereotypes.iterator(), "errors"))
                allOperationsCode +=
                        "       ErrorActions.connect(this);\n";

            IOperation componentDidMount = null;
            for (Iterator<?> iter = definition.operations.iterator(); iter.hasNext(); ) {
                IOperation operation = (IOperation) iter.next();
                if (operation.getName().equals("componentDidMount")) {
                    componentDidMount = operation;
                    break;
                }
            }
            if (componentDidMount != null)
                allOperationsCode += "       " + unEscapedJsonString(componentDidMount.getJavaDetail().getImplModel().getCode(), "       ") + "\n";

            if (stringExistsInIterator(definition.stereotypes.iterator(), "resize"))
                allOperationsCode +=
                        "\n" +
                                "// Generated code - Start\n" +
                                "       $(window).bind('resized', function() { if (self.resize) { self.resize();} });\n" +
                                "       var $section = $(ReactDOM.findDOMNode(self || this)).closest('.section');\n" +
                                "       if ($section) {\n" +
                                "           $section.bind('toFullScreen', function() {\n" +
                                "               if (self.resize) { self.resize(); }\n" +
                                "           });\n" +
                                "           $section.bind('toNormalScreen', function() {\n" +
                                "               if (self.resize) { self.resize(); }\n" +
                                "           });\n" +
                                "           if (self.resize) { self.resize(); }\n" +
                                "// Generated code - End\n" +
                                "       }\n";
            allOperationsCode +=
                    "   };\n";

            IOperation componentWillReceiveProps = null;
            for (Iterator<?> iter = definition.operations.iterator(); iter.hasNext(); ) {
                IOperation operation = (IOperation) iter.next();
                if (operation.getName().equals("componentWillReceiveProps")) {
                    componentWillReceiveProps = operation;
                    break;
                }
            }
            if (componentWillReceiveProps != null) {
                allOperationsCode +=
                        "   componentWillReceiveProps(nextProps) {\n";
                allOperationsCode += "       " + unEscapedJsonString(componentWillReceiveProps.getJavaDetail().getImplModel().getCode(), "       ") + "\n";
                allOperationsCode +=
                        "   };\n";
            } else {
                List<IAttribute> loadAttributes = new ArrayList<>();
                for (Iterator<?> iter = definition.attributes.iterator(); iter.hasNext(); ) {
                    IAttribute attribute = (IAttribute) iter.next();
                    if (attribute.getVisibility().equals("public") && stringExistsInIterator(attribute.stereotypeIterator(), "load"))
                        loadAttributes.add(attribute);
                }
                if (loadAttributes.size() > 0) {
                    allOperationsCode +=
                            "   componentWillReceiveProps() {\n";
                    for (IAttribute loadAttribute : loadAttributes) {
                        String suffix = "";
                        if (!capitalizeFirstLetter(loadAttribute.getName()).equals(loadAttribute.getType()))
                            suffix = capitalizeFirstLetter(loadAttribute.getName());
                        allOperationsCode +=
                                "       if (this.props." + loadAttribute.getName() + ".id && this.props." + loadAttribute.getName() + ".loadStatus === 'proxy') {\n" +
                                        "           DomainAPI." + GetFullClassDefinition(loadAttribute.getTypeAsModel().getId(), project, viewManager, parentFrame, false, namespace).name + suffix + "LoadItem(this.props." + loadAttribute.getName() + ".id);\n" +
                                        "       }\n";
                    }
                    allOperationsCode +=
                            "   };\n";
                }
            }

            IOperation shouldComponentUpdate = null;
            for (Iterator<?> iter = definition.operations.iterator(); iter.hasNext(); ) {
                IOperation operation = (IOperation) iter.next();
                if (operation.getName().equals("shouldComponentUpdate")) {
                    shouldComponentUpdate = operation;
                    break;
                }
            }
            if (shouldComponentUpdate != null) {
                allOperationsCode +=
                        "   shouldComponentUpdate() {\n";
                allOperationsCode += "       " + unEscapedJsonString(shouldComponentUpdate.getJavaDetail().getImplModel().getCode(), "       ") + "\n";
                allOperationsCode +=
                        "   };\n";
            }

            IOperation componentWillUpdate = null;
            for (Iterator<?> iter = definition.operations.iterator(); iter.hasNext(); ) {
                IOperation operation = (IOperation) iter.next();
                if (operation.getName().equals("componentWillUpdate")) {
                    componentWillUpdate = operation;
                    break;
                }
            }
            if (componentWillUpdate != null) {
                allOperationsCode +=
                        "   componentWillUpdate() {\n";
                allOperationsCode += "       " + unEscapedJsonString(componentWillUpdate.getJavaDetail().getImplModel().getCode(), "       ") + "\n";
                allOperationsCode +=
                        "   };\n";
            }

            IOperation componentDidUpdate = null;
            for (Iterator<?> iter = definition.operations.iterator(); iter.hasNext(); ) {
                IOperation operation = (IOperation) iter.next();
                if (operation.getName().equals("componentDidUpdate")) {
                    componentDidUpdate = operation;
                    break;
                }
            }
            if (componentDidUpdate != null) {
                allOperationsCode +=
                        "   componentDidUpdate() {\n";
                allOperationsCode += "       " + unEscapedJsonString(componentDidUpdate.getJavaDetail().getImplModel().getCode(), "       ") + "\n";
                allOperationsCode +=
                        "   };\n";
            }

            allOperationsCode +=
                    "   componentWillUnmount() {\n";

            for (IAttribute connection : connections) {
                String suffix = "";
                allOperationsCode +=
                        "       DomainAPI." + GetFullClassDefinition(connection.getTypeAsModel().getId(), project, viewManager, parentFrame, false, namespace).name + suffix + "Disconnect(this);\n";
            }
            if (stringExistsInIterator(definition.stereotypes.iterator(), "errors"))
                allOperationsCode +=
                        "       ErrorActions.disconnect(this);\n";

            IOperation componentWillUnmount = null;
            for (Iterator<?> iter = definition.operations.iterator(); iter.hasNext(); ) {
                IOperation operation = (IOperation) iter.next();
                if (operation.getName().equals("componentWillUnmount")) {
                    componentWillUnmount = operation;
                    break;
                }
            }
            if (componentWillUnmount != null)
                allOperationsCode += "       " + unEscapedJsonString(componentWillUnmount.getJavaDetail().getImplModel().getCode(), "       ") + "\n";

            allOperationsCode +=
                    "   };\n";

            code +=
                    "// " + definition.getName() + " React Component\n" +
                            "//\n" +
                            "// Generated by IC STRATEGY\n" +
                            "//\n" +
                            "// WARNING: Do not change this code; it will be overwritten by the next generation run!\n" +
                            "//          Change the code only in the Visual Paradigm Project.\n\n" +
                            //"var React = require('react');\n" +
                            //"import * as _ from 'lodash';\n" +
                            "import React from 'react';\n" +
                            "import * as _ from 'lodash';\n" +
                            "var DomainAPI = require('../domain-entity/DomainAPI');\n";

            if (stringExistsInIterator(definition.stereotypes.iterator(), "formstore"))
                code +=
                        "var FormActions = require('../../../js/1-presentation/services/actions_old/FormActions');\n";
            if (stringExistsInIterator(definition.stereotypes.iterator(), "errors"))
                code +=
                        "var ErrorActions = require('../../../js/3-domain/actions_old/ErrorActions');\n";


            if (definition.realizationClass != null) {
                code += "import " + GetFullClassDefinition(definition.realizationClass.getFrom().getId(), project, viewManager, parentFrame, true, namespace).name + " from './" + GetFullClassDefinition(definition.realizationClass.getFrom().getId(), project, viewManager, parentFrame, true, namespace).name + "';\n";
            }

            if (stringExistsInIterator(definition.stereotypes.iterator(), "history") || stringExistsInIterator(definition.stereotypes.iterator(), "match"))
                code += "import { withRouter } from 'react-router-dom';\n";

            if (stringExistsInIterator(definition.stereotypes.iterator(), "firebase"))
                code +=
                        "import * as firebase from 'firebase';\n" +
                                "import { FirebaseManager } from '../../../js/4-infrastructure/databaseManagers/FirebaseManager';\n";

            if (stringExistsInIterator(definition.stereotypes.iterator(), "form"))
                code +=
                        "import {Form} from '../../../js/1-presentation/services/meta/Form';\n" +
                                "import * as FormField from '../../../js/1-presentation/services/meta/FormField';\n";

            code += requireCode;
            code +=
                    "\n" +
                            "class " + definition.getName() + " extends React.Component {\n"
                            + constructorCode
                            + "   render() {\n"
                            + renderCode
                            + "   };\n"
                            + allOperationsCode
                            + "}\n\n";

            if (stringExistsInIterator(definition.stereotypes.iterator(), "history") || stringExistsInIterator(definition.stereotypes.iterator(), "match"))
                code += "export default withRouter(" + definition.getName() + ");";
            else
                code += "export default " + definition.getName() + ";";

            return code;
        } catch (Exception e) {
            String returnString = "";
            for (int i = 0; i < e.getStackTrace().length; i++) {
                returnString += "Error " + e.toString() + " on line " + e.getStackTrace()[i].getLineNumber() + "\n";
            }
            return returnString;
        }
    }


}

