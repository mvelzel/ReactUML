package test.projecttojs.actions_reflux.generators.domainentity;

import com.vp.plugin.model.IGeneralization;
import test.projecttojs.actions_reflux.ClassDefinition;
import test.projecttojs.actions_reflux.Helpers;
import test.projecttojs.actions_reflux.generators.DefaultSingleGenerator;
import test.projecttojs.actions_reflux.generators.Generator;

public class ImportsGenerator extends DefaultSingleGenerator implements Generator {
    public ImportsGenerator(ClassDefinition definition) {
        super(definition);
    }

    @Override
    public void generateFullText() {
        if (Helpers.stringExistsInIterator(this.getDefinition().getStereotypes().iterator(), "errors"))
            this.appendFullText("var ErrorActions = require('../../../js/3-domain/actions/ErrorActions');\n");

        this.appendFullText("import * as Attribute from '../../../js/3-domain/meta/Attribute';\n" +
                "import { Association } from '../../../js/3-domain/meta/Association';\n" +
                "import * as _ from 'lodash';\n");

        IGeneralization generalization = this.getDefinition().getGeneralizationClass();
        this.appendFullText(generalization != null
                ? "import { " + new ClassDefinition(generalization.getFrom().getId(), false).getName() + " } from './" + new ClassDefinition(generalization.getFrom().getId(), false).getName() + "';\n"
                : "import { Entity } from '../../../js/3-domain/meta/Entity';\n");

        if (Helpers.stringExistsInIterator(this.getDefinition().getStereotypes().iterator(), "firebase"))
            this.appendFullText("import * as firebase from 'firebase';\n" +
                    "import { FirebaseManager } from '../../../js/4-infrastructure/databaseManagers/FirebaseManager';\n");
    }
}
