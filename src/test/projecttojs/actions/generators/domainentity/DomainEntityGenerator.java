package test.projecttojs.actions.generators.domainentity;

import com.vp.plugin.model.IGeneralization;
import test.projecttojs.actions.ClassDefinition;
import test.projecttojs.actions.generators.Generator;
import test.projecttojs.actions.generators.DefaultSingleGenerator;
import test.projecttojs.actions.generators.domainentity.constructor.ConstructorGenerator;
import test.projecttojs.actions.generators.domainentity.operations.OperationsGenerator;

import java.util.Date;

public class DomainEntityGenerator extends DefaultSingleGenerator implements Generator {
    public DomainEntityGenerator(ClassDefinition definition) {
        super(definition);
    }

    @Override
    public void generateFullText() {
        ImportsGenerator imports = new ImportsGenerator(this.getDefinition());
        ConstructorGenerator constructor = new ConstructorGenerator(this.getDefinition());
        OperationsGenerator operations = new OperationsGenerator(this.getDefinition());

        imports.generateFullText();
        constructor.generateFullText();
        operations.generateFullText();

        String importsCode = imports.getFullText();
        String constructorCode = constructor.getFullText();
        String operationsCode = operations.getFullText();

        IGeneralization generalization = this.getDefinition().getGeneralizationClass();
        String inheritsFrom = generalization != null ? new ClassDefinition(generalization.getFrom().getId(), false).getName() : "Entity";
        this.appendFullText("// " + this.getDefinition().getName() + " Entity\n" +
                "// \n" +
                "// Generated by IC STRATEGY on " + new Date().toString() + "\n\n" +
                "//\n" +
                "// WARNING: Do not change this code; it will be overwritten by the next generation run!\n" +
                "//          Change the code only in the Visual Paradigm Project.\n\n" +
                importsCode +
                "\n" +
                "class " + this.getDefinition().getName() + " extends " + inheritsFrom + " {\n" +
                constructorCode + "\n" +
                operationsCode +
                "};\n\n" +
                "export { " + this.getDefinition().getName() + " };");
    }

    public String getFolder(){
        return "domain-entity";
    }
}
