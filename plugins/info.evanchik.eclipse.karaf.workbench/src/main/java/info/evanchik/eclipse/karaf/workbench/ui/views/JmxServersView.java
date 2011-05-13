/**
 * Copyright (c) 2009 Stephen Evanchik
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *  Stephen Evanchik - initial implementation
 */
package info.evanchik.eclipse.karaf.workbench.ui.views;

import info.evanchik.eclipse.karaf.workbench.KarafWorkbenchActivator;
import info.evanchik.eclipse.karaf.workbench.jmx.IJMXServiceListener;
import info.evanchik.eclipse.karaf.workbench.jmx.IJMXServiceManager;
import info.evanchik.eclipse.karaf.workbench.jmx.IJMXTransportRegistry;
import info.evanchik.eclipse.karaf.workbench.jmx.JMXServiceDescriptor;

import java.util.List;

import javax.management.remote.JMXConnector;

import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.part.ViewPart;

/**
 * @author Stephen Evanchik (evanchsa@gmail.com)
 *
 */
public class JmxServersView extends ViewPart {

    /**
	 *
	 * @author Stephen Evanchik (evanchsa@gmail.com)
	 *
	 */
	private final class JMXServiceDescriptorContentProvider
		implements IStructuredContentProvider, IJMXServiceListener
	{
		private ListViewer concreteViewer;

		@Override
        public void dispose() {
			// What to do?
		}

		@Override
        public Object[] getElements(final Object element) {
		    if (element == jmxServiceManager) {
	            final List<JMXServiceDescriptor> jmxServiceDescriptors = jmxServiceManager.getJMXServices();

	            return jmxServiceDescriptors.toArray();
		    } else {
		        return null;
		    }
		}

		@Override
        public void inputChanged(final Viewer viewer, final Object oldInput, final Object newInput) {
			if(viewer instanceof ListViewer) {
				concreteViewer = (ListViewer) viewer;
			}
		}

		@Override
        public void jmxServiceAdded(final JMXServiceDescriptor jmxService) {
			concreteViewer.getControl().getDisplay().syncExec(new Runnable() {
				@Override
                public void run() {
					concreteViewer.add(jmxService);
				}

			});
		}

		@Override
        public void jmxServiceRemoved(final JMXServiceDescriptor jmxService) {
			concreteViewer.getControl().getDisplay().syncExec(new Runnable() {
				@Override
                public void run() {
					concreteViewer.remove(jmxService);
				}
			});
		}
	}

	/**
	 *
	 * @author Stephen Evanchik (evanchsa@gmail.com)
	 *
	 */
	private final class JMXServiceDescriptorLabelProvider extends LabelProvider {

		@Override
		public Image getImage(final Object element) {
		    if (element == jmxServiceManager) {
		        return KarafWorkbenchActivator.getDefault().getImageRegistry().get(KarafWorkbenchActivator.LOGO_16X16_IMG);
		    } else {
		        return null;
		    }
		}

		@Override
		public String getText(final Object element) {
		    if (element instanceof JMXServiceDescriptor) {
    			final JMXServiceDescriptor jmxServiceDescriptor = (JMXServiceDescriptor) element;
    			return jmxServiceDescriptor.getName();
		    } else {
		        return null;
		    }
		}
	}

	public static final String VIEW_ID = "info.evanchik.eclipse.karaf.workbench.jmx.serversView";

	private IJMXServiceManager jmxServiceManager;

	private IJMXTransportRegistry jmxTransportRegistry;

	private ListViewer viewer;

	public JmxServersView() {
	    super();

	    jmxServiceManager = KarafWorkbenchActivator.getDefault().getJMXServiceManager();
	    jmxTransportRegistry = KarafWorkbenchActivator.getDefault().getJMXTransportRegistry();
    }

	@Override
	public void createPartControl(final Composite parent) {
		parent.setLayout(new FillLayout());

		final JMXServiceDescriptorContentProvider contentProvider =
			new JMXServiceDescriptorContentProvider();

		viewer = new ListViewer(parent, SWT.SINGLE);
		viewer.setContentProvider(contentProvider);
		viewer.setLabelProvider(new JMXServiceDescriptorLabelProvider());
		viewer.setInput(jmxServiceManager);

		jmxServiceManager.addJMXServiceListener(contentProvider);

		viewer.addDoubleClickListener(new IDoubleClickListener() {

			@Override
            public void doubleClick(final DoubleClickEvent event) {
				final IStructuredSelection selection =
					(IStructuredSelection) event.getSelection();

				final JMXServiceDescriptor connection =
					(JMXServiceDescriptor) selection.getFirstElement();

				final JMXConnector connector =
					jmxTransportRegistry.getJMXConnector(connection);

				// TODO: Update the data provided by this connector
			}
		});

		initContextMenu();

		jmxTransportRegistry.loadTransportExtensions();
	}

	@Override
	public void setFocus() {
		viewer.getControl().setFocus();
	}

	public void setJmxServiceManager(final IJMXServiceManager jmxServiceManager) {
        this.jmxServiceManager = jmxServiceManager;
    }

	public void setJmxTransportRegistry(final IJMXTransportRegistry jmxTransportRegistry) {
        this.jmxTransportRegistry = jmxTransportRegistry;
    }

	protected void initContextMenu() {
        final MenuManager menuMgr = new MenuManager("#PopupMenu"); //$NON-NLS-1$
        menuMgr.setRemoveAllWhenShown(true);
        menuMgr.addMenuListener(new IMenuListener() {
            @Override
            public void menuAboutToShow(final IMenuManager manager) {
                menuMgr.add(new Separator());
                menuMgr.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
            }
        });

        final Menu menu = menuMgr.createContextMenu(viewer.getControl());
        viewer.getControl().setMenu(menu);
        getSite().registerContextMenu(menuMgr, viewer);
    }
}
