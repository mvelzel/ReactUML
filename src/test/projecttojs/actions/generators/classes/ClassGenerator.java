package test.projecttojs.actions.generators.classes;

import test.projecttojs.actions.ClassDefinition;
import test.projecttojs.actions.generators.DefaultSingleGenerator;
import test.projecttojs.actions.generators.Generator;
import test.projecttojs.actions.generators.classes.constructor.ConstructorGenerator;
import test.projecttojs.actions.generators.classes.operations.OperationsGenerator;
import test.projecttojs.actions.generators.classes.operations.OtherOperationsGenerator;

public class ClassGenerator extends DefaultSingleGenerator implements Generator {
    public ClassGenerator(ClassDefinition definition) {
        super(definition);
    }

    @Override
    public void generateFullText() {
        ImportsGenerator importsGenerator = new ImportsGenerator(this.getDefinition());
        importsGenerator.generateFullText();
        String importsCode = importsGenerator.getFullText();

        ConstructorGenerator constructorGenerator = new ConstructorGenerator(this.getDefinition());
        constructorGenerator.generateFullText();
        String constructorCode = constructorGenerator.getFullText();

        OperationsGenerator operationsGenerator = new OperationsGenerator(this.getDefinition());
        operationsGenerator.generateFullText();
        String operationsCode = operationsGenerator.getFullText();

        ExportsGenerator exportsGenerator = new ExportsGenerator(this.getDefinition());
        exportsGenerator.generateFullText();
        String exportsCode = exportsGenerator.getFullText();

        OtherOperationsGenerator otherOperationsGenerator = new OtherOperationsGenerator(this.getDefinition());
        otherOperationsGenerator.generateFullText();
        String otherOperationsCode = otherOperationsGenerator.getFullText();

        String generalization = this.getDefinition().getGeneralizationClass() != null ? "extends " + this.getDefinition().getGeneralizationClass().getName() + " " : "";

        this.appendFullText("// " + this.getDefinition().getName() + " Class\n" +
                "// \n" +
                "// Generated by IC STRATEGY\n\n" +
                "//\n" +
                "// WARNING: Do not change this code; it will be overwritten by the next generation run!\n" +
                "//          Change the code only in the Visual Paradigm Project.\n\n" +
                importsCode +
                "\n" +
                "class " + this.getDefinition().getName() + " " + generalization + "{\n" +
                constructorCode + "\n" +
                operationsCode +
                "}\n" +
                otherOperationsCode +
                exportsCode);
    }

    @Override
    public String getFolder() {
        return "class";
    }
}
