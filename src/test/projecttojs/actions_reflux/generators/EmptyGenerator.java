package test.projecttojs.actions_reflux.generators;

import test.projecttojs.actions_reflux.ClassDefinition;

public class EmptyGenerator extends DefaultSingleGenerator implements Generator {
    public EmptyGenerator(ClassDefinition definition){
        super(definition);
    }

    @Override
    public void generateFullText() {

    }
}
