package test.projecttojs.actions.generators.reactcomponent.render;

import com.vp.plugin.model.IAssociation;
import com.vp.plugin.model.IAssociationEnd;
import com.vp.plugin.model.IAttribute;
import com.vp.plugin.model.ITaggedValue;
import test.projecttojs.actions.ClassDefinition;
import test.projecttojs.actions.generators.Generator;
import test.projecttojs.actions.Helpers;
import test.projecttojs.actions.generators.DefaultSingleGenerator;

import java.util.*;
import java.util.stream.Collectors;

public class ChildrenGenerator extends DefaultSingleGenerator implements Generator {
    public ChildrenGenerator(ClassDefinition definition) {
        super(definition);
    }

    @Override
    public void generateFullText() {
        List<IAssociation> sorted = this.getDefinition().getAssociations().size() > 1 ? sortAssociations(this.getDefinition().getAssociations()) : this.getDefinition().getAssociations();
        boolean routeFound = false;

        for (IAssociation association : sorted) {
            IAssociationEnd thisEnd = Helpers.getAssociationEnd(association, this.getDefinition(), false);
            IAssociationEnd thatEnd = Helpers.getAssociationEnd(association, this.getDefinition(), true);

            if (thisEnd.getAggregationKind().equals("Composited")) {
                if (Helpers.stringExistsInIterator(association.stereotypeIterator(), "Route")) {
                    if (!routeFound) {
                        routeFound = true;
                        this.appendFullText("                    <Switch>\n" +
                                "                        { React.Children.map(self.props.children, function(child){\n" +
                                "                            return React.cloneElement(child, {render: function(props){ return child.props.render(self.state); }});\n" +
                                "                        })}\n" +
                                "                    </Switch>\n");
                    }
                } else {
                    String indent = "                    ";
                    List<ITaggedValue> taggedValues = association.getTaggedValues() != null ? Arrays.asList(association.getTaggedValues().toTaggedValueArray()) : new ArrayList<>();

                    List<ITaggedValue> conditions = Helpers.filterElementList(taggedValues, ITaggedValue::getName, s -> s.equals("condition") || s.equals("condition:false"));
                    ITaggedValue iterator = Helpers.getFromElementList(taggedValues, ITaggedValue::getName, s -> s.contains("-iterator"));

                    String childCode = generateChild(association);

                    String conditionsCode = "";
                    String conditionStart = "";
                    String conditionMid = "";
                    String conditionEnd = "";
                    if (conditions.size() > 0) {
                        conditionsCode = conditions.stream()
                                .map(t -> (t.getName().endsWith(":false") ? "!" : "") + (t.getType() != 4 ? Helpers.findPropValue(t.getValueAsString(), this.getDefinition()) : t.getValueAsString()))
                                .collect(Collectors.joining(" && "));
                        conditionStart = "{ (";
                        conditionMid = ") ? ";
                        conditionEnd = " : <span /> }";
                    }
                    if (thatEnd.getMultiplicity().endsWith("*") && iterator != null) {
                        conditionStart = "{ (";
                        conditionsCode = conditionsCode != "" ? conditionsCode : "true";
                        String prop = Helpers.findPropValue(iterator.getValueAsString(), this.getDefinition());
                        String propOrig = prop;
                        String propBase = Helpers.findPropBase(iterator.getValueAsString(), this.getDefinition());

                        ITaggedValue filter = Helpers.getFromElementList(taggedValues, ITaggedValue::getName, s -> s.equals("filter"));
                        if (filter != null)
                            prop = "_.filter(" + prop + ", function(item) { return " + filter.getValueAsString() + ";})";

                        ITaggedValue sortBy = Helpers.getFromElementList(taggedValues, ITaggedValue::getName, s -> s.equals("sort"));
                        if (sortBy != null)
                            prop = "_.sortBy(" + prop + ", function(item) { return " + sortBy.getValueAsString() + ";})";

                        String propBaseCondition = propBase != null && !propBase.isEmpty() ? propBase + " && " : "";
                        String propBaseConditionOrig = propOrig != null && !propOrig.isEmpty() ? propOrig + " && " : "";

                        this.appendFullText(indent + conditionStart + "(" + conditionsCode + ") && (" + propBaseCondition + propBaseConditionOrig + prop + ")) ? " + prop + ".map(function(item, index) {\n" +
                                indent + "    return (" + childCode + ");\n" +
                                indent + "}) : '' }\n");
                    } else {
                        String fullConditionStart = conditionStart + conditionsCode + conditionMid;
                        this.appendFullText(indent + fullConditionStart + childCode + conditionEnd + "\n");
                    }
                }
            }
        }
    }

    /**
     * Sorts a list of <code>IAssociation</code> type models by name
     *
     * @param start The unsorted list
     * @return The sorted list
     */
    private List<IAssociation> sortAssociations(List<IAssociation> start) {
        ChildrenGenerator self = this;
        Collections.sort(start, new Comparator<IAssociation>() {
            @Override
            public int compare(IAssociation obj1, IAssociation obj2) {
                IAssociationEnd thisEnd1 = Helpers.getAssociationEnd(obj1, self.getDefinition(), false);
                IAssociationEnd thisEnd2 = Helpers.getAssociationEnd(obj2, self.getDefinition(), false);

                String n1 = thisEnd1.getName() == null || thisEnd1.getName().isEmpty() ? "a" : thisEnd1.getName();
                String n2 = thisEnd2.getName() == null || thisEnd2.getName().isEmpty() ? "a" : thisEnd2.getName();

                return n1.compareTo(n2);
            }
        });
        return start;
    }

    /**
     * Generates an HTML tag for the sub component at the other end of an association
     *
     * @param association The association to which the sub component is tied
     * @return An HTML tag string of the sub component
     */
    private String generateChild(IAssociation association) {
        List<ITaggedValue> taggedValues = association.getTaggedValues() != null ? Arrays.asList(association.getTaggedValues().toTaggedValueArray()) : new ArrayList<>();
        Map<String, String> props = new HashMap<>();

        ClassDefinition subComponent = new ClassDefinition(Helpers.getAssociationEnd(association, this.getDefinition(), true).getModelElement().getId(), false);
        for (IAttribute attribute : subComponent.getAttributes()) {
            IAttribute ownAttribute = Helpers.getFromElementList(this.getDefinition().getAttributes(), IAttribute::getName, s -> s.equals(attribute.getName()));
            if (ownAttribute != null) {
                props.put(attribute.getName(), Helpers.findPropValue(ownAttribute.getName(), this.getDefinition()));
            }
        }

        for (ITaggedValue taggedValue : taggedValues) {
            if (!taggedValue.getName().equals("condition") && !taggedValue.getName().equals("condition:false") && !taggedValue.getName().contains("-iterator") && !taggedValue.getName().equals("sort") && !taggedValue.getName().equals("filter")) {
                if (taggedValue.getType() != 4) {
                    props.put(taggedValue.getName(), Helpers.findPropValue(taggedValue.getValueAsString(), this.getDefinition()));
                } else {
                    props.put(taggedValue.getName(), taggedValue.getValueAsString());
                }
            } else if (taggedValue.getName().contains("-iterator")) {
                IAssociationEnd thatEnd = Helpers.getAssociationEnd(association, this.getDefinition(), true);
                if (thatEnd.getMultiplicity().endsWith("*")) {
                    String propName = taggedValue.getName().split("-")[0];
                    props.put(propName, "item");
                    props.put("index", "index");
                    if (!Helpers.stringExistsInIterator(props.keySet().iterator(), "key"))
                        props.put("key", "item.id || index");
                }
            }
        }

        String propsCode = "";
        for (Map.Entry<String, String> entry : props.entrySet()) {
            propsCode += entry.getKey() + "={" + entry.getValue() + "} ";
        }

        return "<" + subComponent.getName() + " " + propsCode + "/>";
    }
}
