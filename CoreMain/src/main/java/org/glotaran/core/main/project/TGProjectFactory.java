package org.glotaran.core.main.project;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;
import org.glotaran.core.messages.CoreErrorMessages;
import org.netbeans.api.project.Project;
import org.netbeans.spi.project.ProjectFactory;
import org.netbeans.spi.project.ProjectState;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.util.lookup.ServiceProvider;

@ServiceProvider(service=ProjectFactory.class)
public class TGProjectFactory implements ProjectFactory {

    public static final String PROJECT_DIR = "tgproject";
    public static final String PROJECT_PROPFILE = "project.properties";

    @Override
    public boolean isProject(FileObject file) {
        FileObject folder = file.getFileObject(PROJECT_DIR);
        if (folder == null) {
            return false;
        }
        return folder.getFileObject(PROJECT_PROPFILE) != null;
    }

    @Override
    public Project loadProject(FileObject dir, ProjectState state) throws IOException {
        return isProject(dir) ? new TGProject(dir, state) : null;
    }

    @Override
    public void saveProject(Project project) throws IOException, ClassCastException {
        FileObject projectRoot = project.getProjectDirectory();
        if (projectRoot.getFileObject(PROJECT_DIR) == null) {
            CoreErrorMessages.projectFolderException();
        }
        //Force creation of folders if it was deleted
        ((TGProject) project).getModelsFolder(true);
        ((TGProject) project).getResultsFolder(true);
        ((TGProject) project).getDatasetsFolder(true);
        ((TGProject) project).getSimModelFolder(true);

        String propsPath = PROJECT_DIR + File.pathSeparator + PROJECT_PROPFILE;
        FileObject propertiesFile = projectRoot.getFileObject(propsPath);
        if (propertiesFile == null) {
            //Recreate the properties file if needed
            propertiesFile = projectRoot.createData(propsPath);
        }

        Properties properties = project.getLookup().lookup(Properties.class);

        File f = FileUtil.toFile(propertiesFile);
        properties.store(new FileOutputStream(f), "TG Project Properties");
    }
}
