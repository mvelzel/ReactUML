package test.projecttojs.actions.generators.domainentity.constructor;

import test.projecttojs.actions.ClassDefinition;
import test.projecttojs.actions.Helpers;
import test.projecttojs.actions.generators.Generator;
import test.projecttojs.actions.generators.DefaultSingleGenerator;

public class ConstructorGenerator extends DefaultSingleGenerator implements Generator {
    public ConstructorGenerator(ClassDefinition definition) {
        super(definition);
    }

    @Override
    public void generateFullText() {
        AttributesGenerator attributes = new AttributesGenerator(this.getDefinition());
        AssociationsGenerator associations = new AssociationsGenerator(this.getDefinition());

        attributes.generateFullText();
        associations.generateFullText();

        String attributesCode = attributes.getFullText();
        String associationsCode = associations.getFullText();

        this.appendFullText("    constructor() {\n" +
                "        super();\n" +
                "        this.type = '" + this.getDefinition().getName() + "';\n" +
                "        this.controller = require('../controller/" + this.getDefinition().getName() + "');\n" +
                "        this.isPersistent = " + Boolean.toString(Helpers.stringExistsInIterator(this.getDefinition().getStereotypes().iterator(), "persistent")) + ";\n" +
                "        this.hasUrl = " + Boolean.toString(Helpers.stringExistsInIterator(this.getDefinition().getStereotypes().iterator(), "hasUrl")) + ";\n" +
                "        this.attributes = _.extend(this.attributes, {\n" +
                attributesCode +
                "        });\n" +
                "        this.associations = _.extend(this.associations, {\n" +
                associationsCode +
                "        });\n" +
                "    };");
    }
}
