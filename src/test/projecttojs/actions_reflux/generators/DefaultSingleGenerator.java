package test.projecttojs.actions_reflux.generators;

import test.projecttojs.actions_reflux.ClassDefinition;

public abstract class DefaultSingleGenerator implements Generator {
    private String fullText = "";
    private ClassDefinition definition;

    public DefaultSingleGenerator(ClassDefinition definition){
        this.definition = definition;
    }

    @Override
    public String getFullText() {
        return this.fullText;
    }

    @Override
    public void appendFullText(String text) {
        this.fullText += text;
    }

    @Override
    public ClassDefinition getDefinition(){
        return this.definition;
    }
}
