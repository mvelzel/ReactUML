package test.projecttojs.actions_reflux.generators.classes;

import test.projecttojs.actions_reflux.ClassDefinition;
import test.projecttojs.actions_reflux.generators.DefaultSingleGenerator;
import test.projecttojs.actions_reflux.generators.Generator;
import test.projecttojs.actions_reflux.generators.classes.constructor.ConstructorGenerator;
import test.projecttojs.actions_reflux.generators.classes.operations.OperationsGenerator;
import test.projecttojs.actions_reflux.generators.formfield.FormFieldGenerator;

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

        String generalization = this.getDefinition().getGeneralizationClass() != null ? "extends " + this.getDefinition().getGeneralizationClass().getName() + " " : "";
        generalization = this instanceof FormFieldGenerator && generalization.equals("") ? "extends FormField " : generalization;

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
                "}\n\n" +
                "export default " + this.getDefinition().getName() + ";");
    }

    @Override
    public String getFolder() {
        return "class";
    }
}