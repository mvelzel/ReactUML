package test.projecttojs.actions.generators;

import test.projecttojs.actions.ClassDefinition;

public class EmptyGenerator extends DefaultSingleGenerator implements Generator {
    public EmptyGenerator(ClassDefinition definition){
        super(definition);
    }

    @Override
    public void generateFullText() {

    }
}
