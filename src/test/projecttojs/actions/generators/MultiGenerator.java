package test.projecttojs.actions.generators;

import test.projecttojs.actions.ClassDefinition;

public interface MultiGenerator extends Generator {
    /**
     * Generates the end of the full text
     */
    void generateEndText();

    /**
     * Generates the beginning of the full text
     */
    void generateBeginText();

    /**
     * Sets the definition of the generator
     */
    void setDefinition(ClassDefinition definition);
}
