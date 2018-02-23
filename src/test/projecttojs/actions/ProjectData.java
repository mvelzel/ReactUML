package test.projecttojs.actions;

import com.vp.plugin.ApplicationManager;
import com.vp.plugin.ViewManager;
import com.vp.plugin.model.IClass;
import com.vp.plugin.model.IProject;

public final class ProjectData {
    /**
     * Returns Visual Paradigms <code>ViewManager</code>
     */
    public static ViewManager getViewManager(){
        return ApplicationManager.instance().getViewManager();
    }

    /**
     * Returns the active Visual Paradigm <code>IProject</code>
     */
    public static IProject getMainProject(){
        return ApplicationManager.instance().getProjectManager().getProject();
    }

    /**
     * Gets the <code>IClass</code> by its ID looking at first the main project and then referenced projects
     * @param classId The ID of the <code>IClass</code>
     * @return Returns the <code>IClass</code> with the specified ID
     */
    public static IClass classFromID(String classId){
        IProject project = getMainProject();
        if(project.getModelElementById(classId) != null){
			return (IClass) project.getModelElementById(classId);
		}
		else{
			IProject[] linkedProjects = project.getLinkedProjects();
			for(int i = 0; i < linkedProjects.length; i++){
				IProject _project = linkedProjects[i];
				if(_project.getModelElementById(classId) != null){
					return (IClass) _project.getModelElementById(classId);
				}
			}
		}
		return null;
    }
}
