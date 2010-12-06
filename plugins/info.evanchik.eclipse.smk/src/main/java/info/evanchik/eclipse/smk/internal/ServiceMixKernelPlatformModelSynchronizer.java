package info.evanchik.eclipse.smk.internal;

import info.evanchik.eclipse.karaf.core.KarafPlatformModel;
import info.evanchik.eclipse.karaf.core.model.GenericKarafPlatformModelSynchronizer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ServiceMixKernelPlatformModelSynchronizer extends GenericKarafPlatformModelSynchronizer {

    private static final List<String> fileKeys;

    static {
        final List<String> tempFileKeys = new ArrayList<String>();

        tempFileKeys.add("org.apache.servicemix.features.cfg"); //$NON-NLS-1$
        tempFileKeys.add("org.apache.servicemix.shell.cfg"); //$NON-NLS-1$

        fileKeys = Collections.unmodifiableList(tempFileKeys);
    }

    public ServiceMixKernelPlatformModelSynchronizer(KarafPlatformModel platformModel) {
        super(platformModel);
    }

    @Override
    protected List<String> getFileKeys() {
        final List<String> mergedFileKeys = new ArrayList<String>();
        mergedFileKeys.addAll(super.getFileKeys());
        mergedFileKeys.addAll(fileKeys);
        return Collections.unmodifiableList(mergedFileKeys);
    }
}
