package org.glotaran.core.main.project;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.IOException;
import java.util.Properties;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import org.glotaran.core.messages.CoreErrorMessages;
import org.glotaran.core.messages.CoreWarningMessages;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.ProjectInformation;
import org.netbeans.spi.project.ActionProvider;
import org.netbeans.spi.project.ProjectState;
import org.netbeans.spi.project.ui.LogicalViewProvider;
import org.openide.filesystems.FileObject;
import org.openide.util.ImageUtilities;
import org.openide.util.Lookup;
import org.openide.util.lookup.AbstractLookup;
import org.openide.util.lookup.InstanceContent;

public class TGProject implements Project {

    /** The name of the folder containing datasets files. */
    public static final String DATASETS_DIR = "datasets";
    /** The name of the folder containing preprocessing files. */
    public static final String PREPROCESSING_DIR = "preprocessing";
    /** The name of the folder containing models files. */
    public static final String MODELS_DIR = "models";
    /** The name of the folder containing fitting options files. */
    public static final String OPTIONS_DIR = "options";
    /** The name of the folder containing output files. */
    public static final String RESULTS_DIR = "results";
    /** The name of the folder containing dataset files. */
    public static final String CACHE_DIR = "cache";
    /** The name of the folder containing analysis schema files. */
    public static final String SCHEMA_DIR = "schema";
    /** The name of the folder containing analysis schema files. */
    public static final String SIMMODELS_DIR = "simmodels";
    private final FileObject projectDir;
    private final ProjectState state;
    private final LogicalViewProvider logicalView = new TGLogicalView(this);
    private final InstanceContent lookUpContent = new InstanceContent();
    private final Lookup lookUp = new AbstractLookup(lookUpContent);

    public TGProject(FileObject root, ProjectState state) {

        this.projectDir = root;
        this.state = state;
        lookUpContent.add(this);
        lookUpContent.add(state);
        lookUpContent.add(new ActionProviderImpl());
        lookUpContent.add(loadProperties());
        lookUpContent.add(new Info());
        lookUpContent.add(logicalView);

    }

    @Override
    public FileObject getProjectDirectory() {
        return projectDir;
    }

    @Override
    public Lookup getLookup() {
        return lookUp;
    }

    public FileObject getDatasetsFolder(boolean create) {
        FileObject result = projectDir.getFileObject(DATASETS_DIR);

        if (result == null && create) {
            try {
                result = projectDir.createFolder(DATASETS_DIR);
            } catch (IOException ioe) {
                CoreErrorMessages.createFolderException(DATASETS_DIR);
            }
        }
        return result;
    }

    public FileObject getPreprocessingFolder(boolean create) {
        FileObject result = projectDir.getFileObject(PREPROCESSING_DIR);

        if (result == null && create) {
            try {
                result = projectDir.createFolder(PREPROCESSING_DIR);
            } catch (IOException ioe) {
                CoreErrorMessages.createFolderException(PREPROCESSING_DIR);
            }
        }
        return result;
    }

    public FileObject getSchemaFolder(boolean create) {
        FileObject result = projectDir.getFileObject(SCHEMA_DIR);

        if (result == null && create) {
            try {
                result = projectDir.createFolder(SCHEMA_DIR);
            } catch (IOException ioe) {
                CoreErrorMessages.createFolderException(SCHEMA_DIR);
            }
        }
        return result;
    }

    public FileObject getModelsFolder(boolean create) {
        FileObject result = projectDir.getFileObject(MODELS_DIR);

        if (result == null && create) {
            try {
                result = projectDir.createFolder(MODELS_DIR);
            } catch (IOException ioe) {
                CoreErrorMessages.createFolderException(MODELS_DIR);
            }
        }
        return result;
    }

    public FileObject getOptionsFolder(boolean create) {
        FileObject result = projectDir.getFileObject(OPTIONS_DIR);

        if (result == null && create) {
            try {
                result = projectDir.createFolder(OPTIONS_DIR);
            } catch (IOException ioe) {
                CoreErrorMessages.createFolderException(OPTIONS_DIR);
            }
        }
        return result;
    }

    public FileObject getResultsFolder(boolean create) {
        FileObject result = projectDir.getFileObject(RESULTS_DIR);

        if (result == null && create) {
            try {
                result = projectDir.createFolder(RESULTS_DIR);
            } catch (IOException ioe) {
                CoreErrorMessages.createFolderException(RESULTS_DIR);
            }
        }
        return result;
    }

    public FileObject getSimModelFolder(boolean create) {
        FileObject result = projectDir.getFileObject(SIMMODELS_DIR);

        if (result == null && create) {
            try {
                result = projectDir.createFolder(SIMMODELS_DIR);
            } catch (IOException ioe) {
                CoreErrorMessages.createFolderException(SIMMODELS_DIR);
            }
        }
        return result;
    }

    public FileObject getCacheFolder(boolean create) {
        FileObject result = projectDir.getFileObject(CACHE_DIR);

        if (result == null && create) {
            try {
                result = projectDir.createFolder(CACHE_DIR);
            } catch (IOException ioe) {
                CoreErrorMessages.createFolderException(CACHE_DIR);
            }
        }
        return result;
    }

    private Properties loadProperties() {
        FileObject fob = projectDir.getFileObject(TGProjectFactory.PROJECT_DIR
                + "/" + TGProjectFactory.PROJECT_PROPFILE);
        Properties properties = new NotifyProperties(state);
        if (fob != null) {
            try {
                properties.load(fob.getInputStream());
            } catch (Exception e) {
                CoreWarningMessages.propFileWarning();
            }
        }
        return properties;
    }

    ////////////////////////////////////////////////////////////////////////////
    // PRIVATE CLASSES /////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////
    private static class NotifyProperties extends Properties {

        private final ProjectState state;

        NotifyProperties(ProjectState state) {
            this.state = state;
        }

        @Override
        public Object put(Object key, Object val) {
            Object result = super.put(key, val);
            if (((result == null) != (val == null)) || (result != null
                    && val != null && !val.equals(result))) {
                state.markModified();
            }
            return result;
        }
    }

    private final class ActionProviderImpl implements ActionProvider {

        @Override
        public String[] getSupportedActions() {
            return new String[0];
        }

        @Override
        public void invokeAction(String string, Lookup lookup) throws IllegalArgumentException {
            //do nothing
        }

        @Override
        public boolean isActionEnabled(String string, Lookup lookup) throws IllegalArgumentException {
            return false;
        }
    }

    /* Implementation of project system's ProjectInformation class */
    private final class Info implements ProjectInformation {

        private final String ICON_PATH = "org/glotaran/core/main/resources/Project-icon.png";
        private final ImageIcon ICON = new ImageIcon(ImageUtilities.loadImage(ICON_PATH, true));
        private final PropertyChangeSupport support = new PropertyChangeSupport(this);

        @Override
        public String getName() {
            return projectDir.getName();
        }

        @Override
        public String getDisplayName() {
            return getProjectDirectory().getName();
        }

        public ImageIcon getImageIcon() {
            return ICON;
        }

        @Override
        public Icon getIcon() {
            return ICON;
        }

        @Override
        public Project getProject() {
            return TGProject.this;
        }

        @Override
        public void addPropertyChangeListener(PropertyChangeListener listener) {
            support.addPropertyChangeListener(listener);
        }

        @Override
        public void removePropertyChangeListener(PropertyChangeListener listener) {
            support.removePropertyChangeListener(listener);
        }
    }
}
