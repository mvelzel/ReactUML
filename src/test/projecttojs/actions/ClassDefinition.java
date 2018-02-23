package test.projecttojs.actions;

import com.vp.plugin.model.*;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ClassDefinition {
    private String name;
    private List<IOperation> operations;
    private List<IAttribute> attributes;
    private List<String> stereotypes;
    private List<ITaggedValue> taggedValues;
    private List<IAssociation> associations;
    private IRealization realizationClass;
    private IGeneralization generalizationClass;
    private IProject parentProject;

    /**
     * Constructs a <code>ClassDefinition</code> based on its id
     *
     * @param classId           The id of the Visual Paradigm class
     * @param addGeneralization Determines whether you want to include generalization properties or not
     */
    public ClassDefinition(String classId, boolean addGeneralization) {
        IClass modelClass = ProjectData.classFromID(classId);
        if (modelClass == null) {
            Helpers.error("Tried to create ClassDefinition from nonexistent classId");
            return;
        }

        String namespace = Helpers.getNamespace(modelClass.getProject());

        List<String> operationNames = new ArrayList<>();
        List<String> tagNames = new ArrayList<>();
        List<String> fromNames = new ArrayList<>();
        List<String> toNames = new ArrayList<>();
        List<String> attrNames = new ArrayList<>();

        this.name = namespace + "_" + modelClass.getName();
        this.parentProject = modelClass.getProject();
        this.operations = new ArrayList<IOperation>();
        this.attributes = new ArrayList<IAttribute>();
        this.associations = new ArrayList<IAssociation>();
        this.stereotypes = new ArrayList<String>();
        this.taggedValues = new ArrayList<ITaggedValue>();

        for (Iterator<IOperation> iter = modelClass.operationIterator(); iter.hasNext(); ) {
            IOperation operation = iter.next();
            operationNames.add(operation.getName());
            this.operations.add(operation);
        }
        for (Iterator<String> iter = modelClass.stereotypeIterator(); iter.hasNext(); ) {
            String stereotype = iter.next();
            this.stereotypes.add(stereotype);
        }
        if (modelClass.getTaggedValues() != null) {
            for (Iterator<ITaggedValue> iter = modelClass.getTaggedValues().taggedValueIterator(); iter.hasNext(); ) {
                ITaggedValue tag = iter.next();
                tagNames.add(tag.getName());
                this.taggedValues.add(tag);
            }
        }
        for (Iterator<IAttribute> iter = modelClass.attributeIterator(); iter.hasNext(); ) {
            IAttribute attr = iter.next();
            attrNames.add(attr.getName());
            this.attributes.add(attr);
        }
        for (Iterator<IAssociationEnd> iter = modelClass.fromRelationshipEndIterator(); iter.hasNext(); ) {
            IAssociationEnd associationEnd = iter.next();
            IAssociation association = (IAssociation) associationEnd.getEndRelationship();
            fromNames.add(association.getName());
            this.associations.add(association);
        }
        for (Iterator<IAssociationEnd> iter = modelClass.toRelationshipEndIterator(); iter.hasNext(); ) {
            IAssociationEnd associationEnd = iter.next();
            IAssociation association = (IAssociation) associationEnd.getEndRelationship();
            toNames.add(association.getName());
            this.associations.add(association);
        }
        for (Iterator<IRelationship> iter = modelClass.toRelationshipIterator(); iter.hasNext(); ) {
            IRelationship relationship = iter.next();
            if (relationship.getModelType().equals("Realization"))
                this.realizationClass = (IRealization) relationship;
        }
        for (Iterator<IRelationship> iter = modelClass.toRelationshipIterator(); iter.hasNext(); ) {
            IRelationship relationship = iter.next();
            if (relationship.getModelType().equals("Generalization")) {
                if (addGeneralization) {
                    IGeneralization generalization = (IGeneralization) relationship;
                    ClassDefinition generalizationDefinition = new ClassDefinition(generalization.getFrom().getId(), true);
                    for (IOperation operation : generalizationDefinition.operations) {
                        if (!Helpers.stringExistsInIterator(operationNames.iterator(), operation.getName()))
                            this.operations.add(operation);
                    }
                    for (String stereotype : generalizationDefinition.stereotypes) {
                        if (!Helpers.stringExistsInIterator(this.stereotypes.iterator(), stereotype))
                            this.stereotypes.add(stereotype);
                    }
                    for (ITaggedValue tag : generalizationDefinition.taggedValues) {
                        if (!Helpers.stringExistsInIterator(tagNames.iterator(), tag.getName()))
                            this.taggedValues.add(tag);
                    }
                    for (IAttribute attr : generalizationDefinition.attributes) {
                        if (!Helpers.stringExistsInIterator(attrNames.iterator(), attr.getName()))
                            this.attributes.add(attr);
                    }
                    for (IAssociation association : generalizationDefinition.associations) {
                        if (!Helpers.stringExistsInIterator(fromNames.iterator(), association.getName()))
                            this.associations.add(association);
                    }
                    if (generalizationDefinition.realizationClass != null)
                        this.realizationClass = generalizationDefinition.realizationClass;
                }
                this.generalizationClass = (IGeneralization) relationship;
            }
        }
    }

    /**
     * Returns the name of the class
     */
    public synchronized String getName() {
        return name;
    }

    /**
     * Returns the name of the class with or without its namespace
     *
     * @param namespace If true includes the namespace in the name
     * @return The name of the <code>ClassDefinition</code> with or without its namespace
     */
    public synchronized String getName(boolean namespace) {
        if (!namespace)
            return name.replace(Helpers.getNamespace(parentProject) + "_", "");
        else
            return name;
    }

    /**
     * Returns the operations of the class
     */
    public synchronized List<IOperation> getOperations() {
        return new ArrayList<>(operations);
    }

    /**
     * Returns the stereotypes of the class
     */
    public synchronized List<String> getStereotypes() {
        return new ArrayList<>(stereotypes);
    }

    /**
     * Returns the tagged values of the class
     */
    public synchronized List<ITaggedValue> getTaggedValues() {
        return new ArrayList<>(taggedValues);
    }

    /**
     * Returns the associations of the class
     */
    public synchronized List<IAssociation> getAssociations() {
        return new ArrayList<>(associations);
    }

    /**
     * Returns the realization of the class
     */
    public synchronized IRealization getRealizationClass() {
        return realizationClass;
    }

    /**
     * Returns the attributes of the class
     */
    public synchronized List<IAttribute> getAttributes() {
        return new ArrayList<>(attributes);
    }

    /**
     * Returns the generalization of the class
     */
    public synchronized IGeneralization getGeneralizationClass() {
        return generalizationClass;
    }

    @Override
    public synchronized String toString() {
        return "ClassDefinition{" +
                "name='" + name + '\'' +
                ", operations=" + operations +
                ", attributes=" + attributes +
                ", stereotypes=" + stereotypes +
                ", taggedValues=" + taggedValues +
                ", associations=" + associations +
                ", realizationClass=" + realizationClass +
                '}';
    }

}
