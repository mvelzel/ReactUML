package test.projecttojs.actions_reflux;

import com.vp.plugin.model.*;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

public final class Helpers {
    /**
     * Logs text to the Visual Paradigm message block
     *
     * @param text The text to log
     */
    public static void log(String text) {
        ProjectData.getViewManager().showMessage(text);
    }

    /**
     * Gives an error message with the given text
     *
     * @param text The text of the error message
     */
    public static void error(String text) {
        log("ERROR: " + text);
    }

    /**
     * Gets the namespace of the project
     *
     * @param project The project to get the namespace from
     * @return The project namespace as string
     */
    public static String getNamespace(IProject project) {
        return project.getName().split(" ")[1].replace("(", "").replace(")", "");
    }

    /**
     * Checks if a string exists in an iterator
     *
     * @param iterator The iterator to check in
     * @param string   The string to be found
     * @return True if string exists in iterator, false if not
     */
    public static Boolean stringExistsInIterator(Iterator<?> iterator, String string) {
        Boolean exists = false;
        while (iterator.hasNext()) {
            if (iterator.next().equals(string))
                exists = true;
        }
        return exists;
    }

    /**
     * Gets an element from a list based on an attribute and a filter
     *
     * @param elements The list of elements to get the element from
     * @param getAttribute The function that gets the attribute to check
     * @param filter The filter that will be applied to that attribute
     * @param <K> The type of the elements in the list
     * @param <T> The type of the attribute to filter by in the elements in the list
     * @return The element that we wanted to find from the list
     */
    public static <K, T> K getFromElementList(List<K> elements, Function<K, T> getAttribute, Predicate<T> filter) {
        for (K element : elements) {
            if (filter.test(getAttribute.apply(element))) {
                return element;
            }
        }
        return null;
    }

    /**
     * Gets a list of elements from a list based on an attribute and a filter
     *
     * @param elements The list of elements to filter
     * @param getAttribute The function that gets the attribute to filter by
     * @param filter The filter that will be applied to that attribute
     * @param <K> The type of the elements in the list
     * @param <T> The type of the attribute to filter by in the elements in the list
     * @return The elements that we wanted to find from the list
     */
    public static <K, T> List<K> filterElementList(List<K> elements, Function<K, T> getAttribute, Predicate<T> filter) {
        List<K> returns = new ArrayList<>();
        for (K element : elements) {
            if (filter.test(getAttribute.apply(element))) {
                returns.add(element);
            }
        }
        return returns;
    }

    /**
     * Extracts the code from an <code>IOperation</code>
     *
     * @param operation   The operation to extract the code from
     * @param indentation The extra indentation of the operation code
     * @return The code of the <code>IOperation</code>, null if the <code>IOperation</code> has no code
     */
    public static String getOperationCode(IOperation operation, String indentation) {
        try {
            String code = operation.getJavaDetail().getImplModel().getCode();
            code = code.replaceAll("(?m)^", indentation);
            return code;
        }
        catch(NullPointerException e){
            return null;
        }
    }

    /**
     * Converts a CamelCase string to a dashed string (camel-case)
     *
     * @param camel The CamelCase string to be converted
     * @return The dashed version of the input
     */
    public static String camelToDash(String camel) {
        String dash = camel.replaceAll("(.)(\\p{Upper})", "$1-$2").toLowerCase();
        return dash;
    }

    /**
     * Returns an <code>IAssociationEnd</code> tied a given <code>IAssociation</code> and fixes VP association type errors at the same time
     *
     * @param association The <code>IAssociation</code> from which to get an <code>IAssociationEnd</code>
     * @param model       The model to which the <code>IAssociation</code> is connected
     * @param opposite    Whether you want the <code>IAssociationEnd</code> tied to the given model or the opposite one
     * @return Return the <code>IAssociationEnd</code>
     */
    public synchronized static IAssociationEnd getAssociationEnd(IAssociation association, ClassDefinition model, boolean opposite) {
        IAssociationEnd from = (IAssociationEnd) association.getFromEnd();
        IAssociationEnd to = (IAssociationEnd) association.getToEnd();

        IModelElement fromElement = from.getModelElement();
        IModelElement toElement = to.getModelElement();

        if (!fromElement.equals(from.getTypeAsElement())) {
            error("VP TYPE ERROR, FIXING...");
            from.setType(fromElement);
        }
        if (!toElement.equals(to.getTypeAsElement())) {
            error("VP TYPE ERROR, FIXING...");
            to.setType(toElement);
        }

        if (!opposite) {
            return fromElement.getName().equals(model.getName().split("_")[1]) ? from : to;
        } else {
            return !fromElement.getName().equals(model.getName().split("_")[1]) ? from : to;
        }
    }

    /**
     * Capitalizes the first letter of a string
     * @param input The string that needs its first letter capitalized
     * @return The input string with its first letter capitalized
     */
    public static String capitalizeFirstLetter(String input){
        String output = input.substring(0, 1).toUpperCase() + input.substring(1);
        return output;
    }

    //TODO find a neater way to do this
    public static String findPropValue(String propName, ClassDefinition definition) {
        if (!propName.equals("")) {
            String suffix = "";
            if (propName.substring(0, 1).equals("[") && propName.substring(propName.length() - 1, propName.length()).equals("]")) {
                propName = propName.substring(1, propName.length() - 1);
                if (propName.indexOf(".") > -1) {
                    String[] propNameParts = propName.split("\\.");
                    propName = propNameParts[0];
                    suffix = ".get('" + propNameParts[1] + "')";
                }
            }
            String name = propName;
            IAttribute attribute = getFromElementList(definition.getAttributes(), IAttribute::getName, s -> s.equals(name));

            if (attribute != null) {
                String propString = "";
                if (attribute.getVisibility().equals("public"))
                    propString = "self.props." + attribute.getName() + suffix;
                else
                    propString = "self.state." + attribute.getName() + suffix;
                return propString;
            } else {
                IOperation operation = getFromElementList(definition.getOperations(), IOperation::getName, s -> s.equals(name));
                if (operation != null) {
                    return "self." + operation.getName();
                } else {
                    return propName;
                }
            }
        }
        return null;
    }

    //TODO find a neater way to do this
    public static String findPropBase(String propName, ClassDefinition definition) {
        if (!propName.equals("")) {
            String suffix = "";
            if (propName.substring(0, 1).equals("[") && propName.substring(propName.length() - 1, propName.length()).equals("]")) {
                propName = propName.substring(1, propName.length() - 1);
                if (propName.indexOf(".") > -1) {
                    String[] propNameParts = propName.split("\\.");
                    propName = propNameParts[0];
                }
            }
            IAttribute attribute = null;
            for (IAttribute _attribute : definition.getAttributes()) {
                if (_attribute.getName().equals(propName)) {
                    attribute = _attribute;
                    break;
                }
            }

            if (attribute != null) {
                String propString = "";
                if (attribute.getVisibility().equals("public"))
                    propString = "self.props." + attribute.getName();
                else
                    propString = "self.state." + attribute.getName();
                return propString;
            }
        }
        return propName;
    }
}

