/*
 * Copyright (c) 2008 Neil Bartlett
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Neil Bartlett - initial implementation
 *     Stephen Evanchik - Updated to use data provider services
 */
package name.neilbartlett.eclipse.bundlemonitor.views.services;

import info.evanchik.eclipse.karaf.workbench.provider.OSGiServiceWrapper;
import info.evanchik.eclipse.karaf.workbench.provider.RuntimeDataProvider;

import java.lang.reflect.Method;
import java.util.Arrays;

import name.neilbartlett.eclipse.bundlemonitor.internal.Activator;
import name.neilbartlett.eclipse.bundlemonitor.views.bundle.BundleTableLabelProvider;
import name.neilbartlett.eclipse.bundlemonitor.views.shared.PropertyEntry;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.plugin.AbstractUIPlugin;

public class ServiceLabelProvider extends BundleTableLabelProvider {

    private static final String LABEL_NULL = "<null>";
    private static final String LABEL_ERROR = "<error>";

    private final Image imgObject;

    public ServiceLabelProvider() {
        super();

        final ImageDescriptor desc = AbstractUIPlugin.imageDescriptorFromPlugin(Activator.PLUGIN_ID, "/icons/generic_element.gif");
        imgObject = desc.createImage();
    }

    @Override
    public Image getColumnImage(Object element, int columnIndex) {
        Image image = super.getColumnImage(element, columnIndex);

        if (image != null) {
            return image;
        }

        if (element instanceof OSGiServiceWrapper && columnIndex == 0) {
            image = imgObject;
        }

        return image;
    }

    @Override
    public String getColumnText(Object element, int columnIndex) {
        String label;
        if (element instanceof RuntimeDataProvider) {
            if (columnIndex == 0) {
                label = ((RuntimeDataProvider) element).getName();
            } else {
                label = "";
            }
        } else if (element instanceof OSGiServiceWrapper) {
            final OSGiServiceWrapper service = (OSGiServiceWrapper) element;

            if (columnIndex == 0) {
                final String[] interfaces = service.getOSGiService().getServiceInterfaces();
                Arrays.sort(interfaces);
                label = arrayToString(interfaces);
            } else {
                label = service.getBundle().getSymbolicName();
            }
        } else if (element instanceof PropertyEntry) {
            final PropertyEntry prop = (PropertyEntry) element;

            if (columnIndex == 0) {
                label = prop.getKey();
            } else if (columnIndex == 1) {
                final Object value = prop.getValue();

                if (value == null) {
                    label = LABEL_NULL;
                } else if (value instanceof Object[]) {
                    label = arrayToString((Object[]) value);
                } else {
                    label = value.toString();
                }
            } else {
                label = null;
            }
        } else {
            label = LABEL_ERROR;
        }

        return label;
    }

    protected static String arrayToString(Object[] array) {
        final StringBuffer buffer = new StringBuffer();

        for (int i = 0; i < array.length; i++) {
            if (i > 0) {
                buffer.append(',');
            }

            buffer.append(array[i] == null ? LABEL_NULL : array[i].toString());
        }

        return buffer.toString();
    }

    @Override
    public void dispose() {
        super.dispose();

        try {
            final Method disposeMethod = imgObject.getClass().getMethod("dispose", new Class[0]);
            disposeMethod.invoke(imgObject, new Object[0]);
        } catch (Exception e) {
            // Ignore
        }
    }

}
