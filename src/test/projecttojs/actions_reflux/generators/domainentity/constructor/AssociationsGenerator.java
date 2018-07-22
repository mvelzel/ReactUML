package test.projecttojs.actions_reflux.generators.domainentity.constructor;

import com.vp.plugin.model.IAssociation;
import com.vp.plugin.model.IAssociationEnd;
import test.projecttojs.actions_reflux.ClassDefinition;
import test.projecttojs.actions_reflux.Helpers;
import test.projecttojs.actions_reflux.generators.DefaultSingleGenerator;
import test.projecttojs.actions_reflux.generators.Generator;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class AssociationsGenerator extends DefaultSingleGenerator implements Generator {
    public AssociationsGenerator(ClassDefinition definition) {
        super(definition);
    }

    @Override
    public void generateFullText() {
        List<String> associationCodes = new ArrayList<>();
        for (IAssociation association : this.getDefinition().getAssociations()) {
            IAssociationEnd thisEnd = Helpers.getAssociationEnd(association, this.getDefinition(), false);
            IAssociationEnd thatEnd = Helpers.getAssociationEnd(association, this.getDefinition(), true);

            String thisCardinality = thisEnd.getMultiplicity().endsWith("*") ? "multiple" : "single";
            String thatCardinality = thatEnd.getMultiplicity().endsWith("*") ? "multiple" : "single";

            associationCodes.add("            " + thisEnd.getName() + ": new class extends Association {\n" +
                    "                constructor(){\n" +
                    "                    super();\n" +
                    "                    this.name = '" + association.getName() + "';\n" +
                    "                    this.thisName = '" + thisEnd.getName() + "';\n" +
                    "                    this.thisIsComposite = " + Boolean.toString(thisEnd.getAggregationKind().equals("Composited")) + ";\n" +
                    "                    this.thisCardinality = '" + thisCardinality + "';\n" +
                    "                    this.thisIsMandatory = " + Boolean.toString(thisEnd.getMultiplicity().startsWith("1")) + ";\n" +
                    "                    this.thisIsNavigable = " + Boolean.toString(thisEnd.getNavigable() == 0) + ";\n" +
                    "                    this.thatName = '" + thatEnd.getName() + "';\n" +
                    "                    this.thisEntity = require('./" + new ClassDefinition(thisEnd.getModelElement().getId(), false).getName() + "');\n" +
                    "                    this.thatEntity = require('./" + new ClassDefinition(thatEnd.getModelElement().getId(), false).getName() + "');\n" +
                    "                    this.thatIsComposite = " + Boolean.toString(thatEnd.getAggregationKind().equals("Composited")) + ";\n" +
                    "                    this.thatCardinality = '" + thatCardinality + "';\n" +
                    "                    this.thatIsMandatory = " + Boolean.toString(thatEnd.getMultiplicity().startsWith("1")) + ";\n" +
                    "                    this.thatIsNavigable = " + Boolean.toString(thatEnd.getNavigable() == 0) + ";\n" +
                    "                }\n" +
                    "            }()");
        }
        this.appendFullText(associationCodes.stream().collect(Collectors.joining(",\n")) + "\n");
    }
}
