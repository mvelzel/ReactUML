package test.projecttojs.actions_reflux.generators;

import test.projecttojs.actions_reflux.ClassDefinition;

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
