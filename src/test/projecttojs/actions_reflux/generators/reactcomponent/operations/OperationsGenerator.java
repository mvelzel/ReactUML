package test.projecttojs.actions_reflux.generators.reactcomponent.operations;

import test.projecttojs.actions_reflux.ClassDefinition;
import test.projecttojs.actions_reflux.generators.DefaultSingleGenerator;
import test.projecttojs.actions_reflux.generators.Generator;

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
