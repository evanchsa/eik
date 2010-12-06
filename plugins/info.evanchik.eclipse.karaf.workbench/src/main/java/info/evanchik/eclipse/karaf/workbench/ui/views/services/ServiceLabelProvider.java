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
package info.evanchik.eclipse.karaf.workbench.ui.views.services;

import info.evanchik.eclipse.karaf.workbench.KarafWorkbenchActivator;
import info.evanchik.eclipse.karaf.workbench.provider.BundleItem;
import info.evanchik.eclipse.karaf.workbench.provider.RuntimeDataProvider;
import info.evanchik.eclipse.karaf.workbench.provider.ServiceItem;
import info.evanchik.eclipse.karaf.workbench.ui.views.PropertyEntry;
import info.evanchik.eclipse.karaf.workbench.ui.views.bundle.BundleTableLabelProvider;

import java.lang.reflect.Method;
import java.util.Arrays;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;

/**
 *
 * @author Neil Bartlett
 * @author Stephen Evanchik (evanchsa@gmail.com)
 *
 */
public class ServiceLabelProvider extends BundleTableLabelProvider {

    private static final String LABEL_NULL = "<null>";
    private static final String LABEL_ERROR = "<error>";

    private final Image imgObject;

    public ServiceLabelProvider() {
        super();

        final ImageDescriptor desc = KarafWorkbenchActivator.imageDescriptorFromPlugin(KarafWorkbenchActivator.PLUGIN_ID, "/icons/obj16/generic_element.gif");
        imgObject = desc.createImage();
    }

    @Override
    public Image getColumnImage(Object element, int columnIndex) {
        Image image = super.getColumnImage(element, columnIndex);

        if (image != null) {
            return image;
        }

        if (element instanceof ServiceItem && columnIndex == 0) {
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
        } else if (element instanceof ServiceItem) {
            final ServiceItem service = (ServiceItem) element;

            if (columnIndex == 0) {
                final String[] interfaces = service.getServiceInterfaces();
                Arrays.sort(interfaces);
                label = arrayToString(interfaces);
            } else {
                final BundleItem bundle = (BundleItem) service.getAdapter(BundleItem.class);
                if (bundle != null) {
                    label = bundle.getSymbolicName();
                } else {
                    label = LABEL_ERROR;
                }
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

    // TODO: This is just join
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
