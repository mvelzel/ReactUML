package test.projecttojs.actions_reflux;

import test.projecttojs.actions_reflux.generators.Generator;

import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.prefs.Preferences;

public class FileWriter {
    private String mainDir;

    /**
     * Writes to a file within a folder within the <code>mainDir</code>
     *
     * @param generator The generator of which the contents should be written into the file
     * @return Returns false if fail, true if success
     */
    public boolean writeGenerator(Generator generator) {
        String folder = generator.getFolder();
        try {
            if (!Files.exists(Paths.get(this.getMainDir() + "/" + folder)))
                Files.createDirectory(Paths.get(this.getMainDir() + "/" + folder));
            PrintWriter writer = new PrintWriter((this.getMainDir() + "/" + folder).replace("\\", "/") + "/" + generator.getName() + ".js");
            writer.println(generator.getFullText());
            writer.close();
            return true;
        } catch (Exception e) {
            Helpers.log(e.getMessage());
            return false;
        }
    }

    /**
     * Returns the main directory where the <code>FileWriter</code> writes its files
     */
    public String getMainDir() {
        return this.mainDir;
    }

    /**
     * Sets the main directory where the <code>FileWriter</code> writes its files
     *
     * @param dir The string to which the main directory
     */
    public void setMainDir(String dir) {
        this.mainDir = dir;
    }

    /**
     * Shows a VP dialog to set the main directory
     *
     * @return Returns false if fail, true if success
     */
    public boolean mainDirDialog() {
        String fileDirectory;
        MainGUI setFileDirectoryDialog = new MainGUI();

        ProjectData.getViewManager().showDialog(setFileDirectoryDialog);
        fileDirectory = setFileDirectoryDialog._inputField1.getText();

        if (!fileDirectory.isEmpty()) {
            Preferences prefs = Preferences.userRoot();
            prefs.put("FileDir", fileDirectory);

            String pName = ProjectData.getMainProject().getName().split(" ")[0];
            fileDirectory = fileDirectory + "/" + pName + "/src/generated/js";

            Helpers.log(fileDirectory);

            this.setMainDir(fileDirectory);
            return true;
        } else {
            return false;
        }
    }
}
