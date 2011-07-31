package info.evanchik.eclipse.karaf.ui.features;

import info.evanchik.eclipse.karaf.core.features.Bundle;
import info.evanchik.eclipse.karaf.core.features.Feature;
import info.evanchik.eclipse.karaf.core.features.Features;
import info.evanchik.eclipse.karaf.core.features.FeaturesRepository;

import java.util.List;

import org.apache.commons.collections.ListUtils;
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
        if (parentElement == featuresRepositories) {
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
        if (inputElement == featuresRepositories && inputElement != null) {
            return featuresRepositories.toArray();
        } else if (inputElement instanceof FeaturesRepository) {
            final FeaturesRepository featuresRepo = (FeaturesRepository) inputElement;
            return new Object[] { featuresRepo.getFeatures() };
        } else if (inputElement instanceof Features) {
            final Features features = (Features) inputElement;
            return features.getFeatures().toArray();
        } else if (inputElement instanceof Feature) {
            final Feature feature = (Feature) inputElement;
            return ListUtils.union(feature.getFeatures(), feature.getBundles()).toArray();
        } else {
            return new Object[0];
        }
    }

    @Override
    public Object getParent(final Object element) {
        if (element instanceof FeaturesRepository) {
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
        if (element == featuresRepositories && element != null) {
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
            featuresRepositories = (List<FeaturesRepository>) newInput;
        }
    }
}