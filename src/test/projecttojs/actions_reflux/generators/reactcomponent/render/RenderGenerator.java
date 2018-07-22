package test.projecttojs.actions_reflux.generators.reactcomponent.render;

import com.vp.plugin.model.IOperation;
import test.projecttojs.actions_reflux.ClassDefinition;
import test.projecttojs.actions_reflux.Helpers;
import test.projecttojs.actions_reflux.generators.DefaultSingleGenerator;
import test.projecttojs.actions_reflux.generators.Generator;

public class RenderGenerator extends DefaultSingleGenerator implements Generator {
    public RenderGenerator(ClassDefinition definition){
        super(definition);
    }

    @Override
    public void generateFullText() {
        IOperation render = Helpers.getFromElementList(this.getDefinition().getOperations(), IOperation::getName, s -> s.equals("render"));
        if(render != null){
            this.appendFullText(Helpers.getOperationCode(render, "        "));
        }
        else{
            TagGenerator tag = new TagGenerator(this.getDefinition());
            tag.generateFullText();
            String openTag = tag.getFullText().split("\n")[0];
            String closeTag = tag.getFullText().split("\n")[1];

            PreConditionGenerator precon = new PreConditionGenerator(this.getDefinition());
            precon.generateFullText();
            String preConditionCode = !precon.getFullText().isEmpty() ? precon.getFullText() : "true";

            ChildrenGenerator children = new ChildrenGenerator(this.getDefinition());
            children.generateFullText();
            String childrenCode = children.getFullText();

            this.appendFullText("        var self = this;\n" +
                    "        if (" + preConditionCode + ") {\n" +
                    "            return(\n" +
                    "                " + openTag + "\n" +
                    childrenCode +
                    "                " + closeTag + "\n" +
                    "            );\n" +
                    "        }\n" +
                    "        else {\n" +
                    "            return <span />;\n" +
                    "        };");
        }
    }
}
