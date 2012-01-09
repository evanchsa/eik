package info.evanchik.eclipse.karaf.ui.model;

import org.apache.karaf.eik.core.features.FeaturesRepository;
import org.apache.karaf.eik.core.features.XmlFeaturesRepository;
import info.evanchik.eclipse.karaf.ui.IKarafProject;
import info.evanchik.eclipse.karaf.ui.KarafUIPluginActivator;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.swt.graphics.Image;

/**
 *
 * @author Stephen Evanchik (evanchsa@gmail.com)
 *
 */
public final class FeatureRepositoryContentModel extends AbstractContentModel {

    private final Set<FeaturesRepository> featuresRepository = new HashSet<FeaturesRepository>();

    /**
     *
     * @param project
     */
    public FeatureRepositoryContentModel(final IKarafProject project) {
        super(project);
    }

    @Override
    public Object[] getElements() {
        FileInputStream fin = null;
        try {
            final IFolder featuresFolder = project.getFolder("features");
            if (!featuresFolder.exists()) {
                return new Object[0];
            }

            final IResource[] resources = featuresFolder.members();

            for (final IResource featureFileResource : resources) {
                if (featureFileResource.getFullPath().getFileExtension().equals("xml")) {
                    fin = new FileInputStream(featureFileResource.getRawLocation().toFile());
                    featuresRepository.add(new XmlFeaturesRepository(featureFileResource.getName(), fin));
                    fin.close();
                } else {
                    // TODO: What to do here?
                }
            }
        } catch (final IOException e) {
            e.printStackTrace();
        } catch (final CoreException e) {
            e.printStackTrace();
        } finally {
            if (fin != null) {
                try {
                    fin.close();
                } catch (final IOException e) {
                    // This is intentionally left blank
                }
            }
        }

        return featuresRepository.toArray();
    }

    @Override
    public Image getImage() {
        return KarafUIPluginActivator.getDefault().getImageRegistry().get(KarafUIPluginActivator.FEATURE_OBJ_IBM);
    }

    @Override
    public String toString() {
        return "Feature Repositories";
    }
}