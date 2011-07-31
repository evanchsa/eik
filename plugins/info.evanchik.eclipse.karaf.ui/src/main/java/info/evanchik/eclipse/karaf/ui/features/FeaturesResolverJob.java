package info.evanchik.eclipse.karaf.ui.features;

import info.evanchik.eclipse.karaf.core.configuration.FeaturesSection;
import info.evanchik.eclipse.karaf.core.features.FeaturesRepository;
import info.evanchik.eclipse.karaf.core.features.XmlFeaturesRepository;
import info.evanchik.eclipse.karaf.ui.KarafUIPluginActivator;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;

/**
 * A {@link Job} that loads an Apache Karaf features configuration file
 * {@code org.apache.karaf.features.cfg} and examines all of the referenced
 * Features Repositories. It then attempts to resolve each of the repositories
 * in order to produce a {@link List} of {@link FeaturesRepository}
 *
 * @author Stephen Evanchik (evanchsa@gmail.com)
 *
 */
public final class FeaturesResolverJob extends Job {

    private final List<FeaturesRepository> featuresRepositories =
        Collections.synchronizedList(new ArrayList<FeaturesRepository>());

    private final FeaturesSection featuresSection;

    public FeaturesResolverJob(final String name, final FeaturesSection featuresSection) {
        super("Resolving Features for " + name);

        this.featuresSection = featuresSection;
    }

    /**
     * Getter for the {@link List} of {@link FeaturesRepository} objects. This
     * {@code List} is read-only and is a synchronized list via
     * {@link Collections#synchronizedList(List)}.
     *
     * @return the {@link List} of {@link FeaturesRepository} objects.
     */
    public List<FeaturesRepository> getFeaturesRepositories() {
        return Collections.unmodifiableList(featuresRepositories);
    }

    @Override
    protected IStatus run(final IProgressMonitor monitor) {
        featuresSection.load();
        featuresRepositories.clear();

        return resolveFeatures(monitor);
    }

    /**
     * Helper method that resolves Karaf Features.
     *
     * @param monitor
     *            the {@link IProgressMonitor} instance
     * @return the {@link Status#OK_STATUS} if the Features are successfully
     *         resolved
     */
    private IStatus resolveFeatures(final IProgressMonitor monitor) {
        monitor.beginTask("Loading Karaf Features", featuresSection.getRepositoryList().size());
        try {

            for (final String repository : featuresSection.getRepositoryList()) {

                if (monitor.isCanceled()) {
                    return Status.CANCEL_STATUS;
                }

                try {
                    final InputStream stream = new URL(repository).openConnection().getInputStream();

                    final String repositoryName;
                    if (repository.startsWith(FeaturesLabelProvider.MVN_URL_PREFIX)) {
                        final String[] repositoryComponents = repository.split("/"); //$NON-NLS-1$
                        repositoryName = repositoryComponents[1] + "-" + repositoryComponents[2]; //$NON-NLS-1$
                    } else {
                        repositoryName = repository;
                    }

                    final FeaturesRepository newRepo = new XmlFeaturesRepository(repositoryName, stream);
                    featuresRepositories.add(newRepo);

                    monitor.worked(1);

                } catch (final MalformedURLException e) {
                    if (monitor.isCanceled()) {
                        return Status.CANCEL_STATUS;
                    } else {
                        return new Status(IStatus.ERROR, KarafUIPluginActivator.PLUGIN_ID, "Unable determine location for Features repository: " + repository, e);
                    }
                } catch (final IOException e) {
                    if (monitor.isCanceled()) {
                        return Status.CANCEL_STATUS;
                    } else {
                        return new Status(IStatus.ERROR, KarafUIPluginActivator.PLUGIN_ID, "Unable load Features repository: " + repository, e);
                    }
                }
            }

            return Status.OK_STATUS;
        } finally {
            monitor.done();
        }
    }
}