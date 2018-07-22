package test.projecttojs.actions_reflux.generators;

import test.projecttojs.actions_reflux.ClassDefinition;

public abstract class DefaultMultiGenerator implements MultiGenerator {
    protected String fullText = "";
    private ClassDefinition definition;

    public DefaultMultiGenerator() {
        this.generateBeginText();
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

    @Override
    public void setDefinition(ClassDefinition definition) {
        this.definition = definition;
    }
}
