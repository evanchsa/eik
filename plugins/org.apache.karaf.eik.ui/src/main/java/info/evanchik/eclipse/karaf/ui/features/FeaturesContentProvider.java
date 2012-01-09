package info.evanchik.eclipse.karaf.ui.features;

import org.apache.karaf.eik.core.features.Bundle;
import org.apache.karaf.eik.core.features.Feature;
import org.apache.karaf.eik.core.features.Features;
import org.apache.karaf.eik.core.features.FeaturesRepository;
import info.evanchik.eclipse.karaf.ui.IKarafProject;
import info.evanchik.eclipse.karaf.ui.model.AbstractContentModel;
import info.evanchik.eclipse.karaf.ui.model.ContentModel;
import info.evanchik.eclipse.karaf.ui.model.FeatureRepositoryContentModel;
import info.evanchik.eclipse.karaf.ui.project.KarafProject;

import java.util.List;

import org.apache.commons.collections.ListUtils;
import org.eclipse.core.resources.IProject;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

/**
 * A {@link ITreeContentProvider} suitable for displaying Apache Karaf Features
 * Repositories.
 *
 * @see FeaturesResolverJob
 * @see FeaturesRepository
 * @see Features
 * @see Feature
 * @see Bundle
 *
 * @author Stephen Evanchik (evanchsa@gmail.com)
 */
public final class FeaturesContentProvider implements ITreeContentProvider {

    private List<FeaturesRepository> featuresRepositories;

    @Override
    public void dispose() {
    }

    @Override
    public Object[] getChildren(final Object parentElement) {
        if (parentElement instanceof IProject && KarafProject.isKarafProject((IProject) parentElement)) {
            final IProject project = (IProject) parentElement;
            final IKarafProject karafProject = (IKarafProject) project.getAdapter(IKarafProject.class);

            return new Object[] { new FeatureRepositoryContentModel(karafProject) };
        } else if (parentElement instanceof ContentModel) {
            final ContentModel contentModel = (ContentModel) parentElement;
            return contentModel.getElements();
        } else if (parentElement == featuresRepositories && parentElement != null) {
            return featuresRepositories.toArray();
        } else if (parentElement instanceof FeaturesRepository) {
            final FeaturesRepository featuresRepository = (FeaturesRepository) parentElement;
            return featuresRepository.getFeatures().getFeatures().toArray();
        } else if (parentElement instanceof Features) {
            final Features features = (Features) parentElement;
            return features.getFeatures().toArray();
        } else if (parentElement instanceof Feature) {
            final Feature feature = (Feature) parentElement;
            return ListUtils.union(feature.getFeatures(), feature.getBundles()).toArray();
        } else {
            return new Object[0];
        }
    }

    @Override
    public Object[] getElements(final Object inputElement) {
        return getChildren(inputElement);
    }

    @Override
    public Object getParent(final Object element) {
        if (element instanceof AbstractContentModel) {
            return ((AbstractContentModel) element).getParent();
        } else if (element instanceof FeaturesRepository) {
            return featuresRepositories;
        } else if (element instanceof Features) {
            final Features features = (Features) element;
            return features.getParent();
        } else if (element instanceof Feature) {
            return ((Feature)element).getParent();
        } else {
            return null;
        }
    }

    @Override
    public boolean hasChildren(final Object element) {
        if (element instanceof AbstractContentModel) {
            return ((AbstractContentModel) element).getElements().length > 0;
        } else if (element == featuresRepositories && element != null) {
            return featuresRepositories.size() > 0;
        } else if (element instanceof FeaturesRepository) {
            final FeaturesRepository featuresRepository = (FeaturesRepository) element;
            return featuresRepository.getFeatures().getFeatures().size() > 0;
        } else if (element instanceof Features) {
            final Features features = (Features) element;
            return features.getFeatures().size() > 0;
        } else if (element instanceof Feature) {
            final Feature feature = (Feature) element;
            return feature.getBundles().size() > 0 || feature.getFeatures().size() > 0;
        } else {
            return false;
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public void inputChanged(final Viewer viewer, final Object oldInput, final Object newInput) {
        if (newInput != null) {
            if (newInput instanceof List) {
                featuresRepositories = (List<FeaturesRepository>) newInput;
            }
        }
    }
}