package test.projecttojs.actions.generators.reactcomponent.operations;

import test.projecttojs.actions.ClassDefinition;
import test.projecttojs.actions.generators.Generator;
import test.projecttojs.actions.generators.DefaultSingleGenerator;

public class OperationsGenerator extends DefaultSingleGenerator implements Generator {
    public OperationsGenerator(ClassDefinition definition){
        super(definition);
    }

    @Override
    public void generateFullText() {
        ComponentDidMountGenerator didMountGenerator = new ComponentDidMountGenerator(this.getDefinition());
        didMountGenerator.generateFullText();
        String didMountCode = didMountGenerator.getFullText();

        ComponentWillReceivePropsGenerator willReceivePropsGenerator = new ComponentWillReceivePropsGenerator(this.getDefinition());
        willReceivePropsGenerator.generateFullText();
        String willReceivePropsCode = willReceivePropsGenerator.getFullText();

        ComponentWillUnmountGenerator willUnmountGenerator = new ComponentWillUnmountGenerator(this.getDefinition());
        willUnmountGenerator.generateFullText();
        String willUnmountCode = willUnmountGenerator.getFullText();

        OtherOperationsGenerator otherGenerator = new OtherOperationsGenerator(this.getDefinition());
        otherGenerator.generateFullText();
        String otherCode = otherGenerator.getFullText();

        this.appendFullText(didMountCode +
                willReceivePropsCode +
                willUnmountCode +
                otherCode);
    }
}
