package test.projecttojs.actions_reflux.generators.reactcomponent;

import test.projecttojs.actions_reflux.ClassDefinition;
import test.projecttojs.actions_reflux.Helpers;
import test.projecttojs.actions_reflux.generators.DefaultSingleGenerator;
import test.projecttojs.actions_reflux.generators.Generator;
import test.projecttojs.actions_reflux.generators.reactcomponent.constructor.ConstructorGenerator;
import test.projecttojs.actions_reflux.generators.reactcomponent.operations.OperationsGenerator;
import test.projecttojs.actions_reflux.generators.reactcomponent.render.RenderGenerator;

public class ReactComponentGenerator extends DefaultSingleGenerator implements Generator {
    public ReactComponentGenerator(ClassDefinition definition){
        super(definition);
    }

    @Override
    public void generateFullText() {
        ImportsGenerator imports = new ImportsGenerator(this.getDefinition());
        imports.generateFullText();
        String importsCode = imports.getFullText();

        ConstructorGenerator constructor = new ConstructorGenerator(this.getDefinition());
        constructor.generateFullText();
        String constructorCode = constructor.getFullText();

        RenderGenerator render = new RenderGenerator(this.getDefinition());
        render.generateFullText();
        String renderCode = render.getFullText();

        OperationsGenerator operations = new OperationsGenerator(this.getDefinition());
        operations.generateFullText();
        String operationsCode = operations.getFullText();

        String exportsCode = "export default " + this.getDefinition().getName() + ";";
        if (Helpers.stringExistsInIterator(this.getDefinition().getStereotypes().iterator(), "history") || Helpers.stringExistsInIterator(this.getDefinition().getStereotypes().iterator(), "match"))
            exportsCode = "export default withRouter(" + this.getDefinition().getName() + ");";

        this.appendFullText("// " + this.getDefinition().getName() + " React Component\n" +
                "//\n" +
                "// Generated by IC STRATEGY\n" +
                "//\n" +
                "// WARNING: Do not change this code; it will be overwritten by the next generation run!\n" +
                "//          Change the code only in the Visual Paradigm Project.\n\n");
        this.appendFullText(importsCode);
        this.appendFullText("\n" +
                "class " + this.getDefinition().getName() + " extends React.Component {\n" +
                constructorCode +
                "    render() {\n" +
                renderCode + "\n" +
                "    };\n" +
                operationsCode +
                "};\n\n" +
                exportsCode);
    }

    public String getFolder() {
        return "react-component";
    }
}
