package test.projecttojs.actions.generators.reactcomponent.render;

import com.vp.plugin.model.IStereotype;
import com.vp.plugin.model.ITaggedValue;
import com.vp.plugin.model.ITaggedValueDefinition;
import test.projecttojs.actions.ClassDefinition;
import test.projecttojs.actions.generators.Generator;
import test.projecttojs.actions.Helpers;
import test.projecttojs.actions.generators.DefaultSingleGenerator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

public class TagGenerator extends DefaultSingleGenerator implements Generator {
    public TagGenerator(ClassDefinition definition) {
        super(definition);
    }

    @Override
    public void generateFullText() {
        List<ITaggedValue> filteredTags = Helpers.filterElementList(this.getDefinition().getTaggedValues(), ITaggedValue::getName, s -> !s.equals("classNames") && !s.equals("style"));
        filteredTags = Helpers.filterElementList(filteredTags, ITaggedValue::getTagDefinitionStereotype, s -> s == null);
        String properties = filteredTags.size() > 0 ? " " + filteredTags.stream().map(t -> t.getName() + "={" + t.getValueAsString() + "}").collect(Collectors.joining(" ")) : "";

        String openTag;
        String closeTag;
        if (this.getDefinition().getRealizationClass() == null) {
            String classNames = getClassNames();
            String style = getStyle();
            openTag = "<span className={\"" + Helpers.camelToDash(this.getDefinition().getName(false)) + " \" + " + classNames + "} style={self.props.style || " + style + "}" + properties + " >";
            closeTag = "</span>";
            if (Helpers.stringExistsInIterator(this.getDefinition().getStereotypes().iterator(), "page")) {
                openTag = "<div className={\"page " + Helpers.camelToDash(this.getDefinition().getName(false)) + " \" + " + classNames + "} style={self.props.style || " + style + "}" + properties + " >";
                closeTag = "</div>";
            }
            for (IStereotype stereotype : this.getDefinition().getOriginalClass().toStereotypeModelArray()) {
                if (stereotype.getTaggedValueDefinitions() != null) {
                    ITaggedValueDefinition tagTag = Helpers.getFromElementList(Arrays.asList(stereotype.getTaggedValueDefinitions().toTaggedValueDefinitionArray()), ITaggedValueDefinition::getName, n -> n.equals("tag"));
                    if (tagTag != null) {
                        String htmlTag = tagTag.getDefaultValue();
                        openTag = "<" + htmlTag + " className={\"" + Helpers.camelToDash(this.getDefinition().getName(false)) + " \" + " + classNames + "} style={self.props.style || " + style + "}" + properties + " >";
                        closeTag = "</" + htmlTag + ">";
                    }
                }
            }
        } else {
            openTag = "<" + new ClassDefinition(this.getDefinition().getRealizationClass().getFrom().getId(), true).getName() + " className=\"" + Helpers.camelToDash(this.getDefinition().getName()) + "\" ";
            if (this.getDefinition().getRealizationClass().getTaggedValues() != null)
                for (Iterator<ITaggedValue> iter = this.getDefinition().getRealizationClass().getTaggedValues().taggedValueIterator(); iter.hasNext(); ) {
                    ITaggedValue taggedValue = iter.next();
                    String value;
                    if (taggedValue.getType() == 4) {
                        value = taggedValue.getValueAsString().replace("\"[", "").replace("]\"", "").replace("\"{", "").replace("}\"", "");
                        openTag += " " + taggedValue.getName() + "={" + value + "}";
                    } else
                        openTag += " " + taggedValue.getName() + "={" + Helpers.findPropValue(taggedValue.getValueAsString(), this.getDefinition()) + "}";
                }
            openTag += ">";
            closeTag = "</" + new ClassDefinition(this.getDefinition().getRealizationClass().getFrom().getId(), true).getName() + ">";
        }

        this.appendFullText(openTag + "\n" + closeTag);
    }

    private String getClassNames() {
        ITaggedValue classNamesTag = Helpers.getFromElementList(this.getDefinition().getTaggedValues(), ITaggedValue::getName, s -> s.equals("classNames"));
        String classNames = "\"\"";
        if (classNamesTag != null) {
            //TODO findPropValue
            classNames = classNamesTag.getValueAsString();
        }
        return classNames;
    }

    private String getStyle() {
        String style = "{}";
        ITaggedValue styleTag = Helpers.getFromElementList(this.getDefinition().getTaggedValues(), ITaggedValue::getName, s -> s.equals("style"));
        if (styleTag != null) {
            //TODO extra ease
            style = "{" + styleTag.getValueAsString() + "}";
        }
        return style;
    }
}
