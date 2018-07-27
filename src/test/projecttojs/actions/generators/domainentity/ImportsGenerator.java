package test.projecttojs.actions.generators.domainentity;

import com.vp.plugin.model.IAttribute;
import com.vp.plugin.model.IGeneralization;
import test.projecttojs.actions.ClassDefinition;
import test.projecttojs.actions.Helpers;
import test.projecttojs.actions.generators.Generator;
import test.projecttojs.actions.generators.DefaultSingleGenerator;

import java.util.ArrayList;
import java.util.List;

public class ImportsGenerator extends DefaultSingleGenerator implements Generator {
    public ImportsGenerator(ClassDefinition definition) {
        super(definition);
    }

    @Override
    public void generateFullText() {
        if (Helpers.stringExistsInIterator(this.getDefinition().getStereotypes().iterator(), "errors"))
            this.appendFullText("var ErrorActions = require('../../../js/3-domain/actions/ErrorActions');\n");

        this.appendFullText("import { Association } from '../../../js/3-domain/meta/Association';\n" +
                "import * as _ from 'lodash';\n");

        IGeneralization generalization = this.getDefinition().getGeneralizationClass();
        this.appendFullText(generalization != null
                ? "import " + new ClassDefinition(generalization.getFrom().getId(), false).getName() + " from './" + new ClassDefinition(generalization.getFrom().getId(), false).getName() + "';\n"
                : "import { Entity } from '../../../js/3-domain/meta/Entity';\n");

        if (Helpers.stringExistsInIterator(this.getDefinition().getStereotypes().iterator(), "firebase"))
            this.appendFullText("import * as firebase from 'firebase';\n" +
                    "import { FirebaseManager } from '../../../js/4-infrastructure/databaseManagers/FirebaseManager';\n");

        List<String> addedAttributes = new ArrayList<>();
        for (IAttribute attribute : this.getDefinition().getAttributes()) {
            ClassDefinition attributeClass = new ClassDefinition(attribute.getTypeAsModel().getId(), false);
            if (!addedAttributes.contains(attributeClass.getName())) {
                this.appendFullText("import " + attributeClass.getName() + " from '../attribute/" + attributeClass.getName() + "';\n");
                addedAttributes.add(attributeClass.getName());
            }
        }
    }
}
