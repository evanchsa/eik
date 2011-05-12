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
package info.evanchik.eclipse.karaf.workbench.jmx.views;

import info.evanchik.eclipse.karaf.workbench.KarafWorkbenchActivator;
import info.evanchik.eclipse.karaf.workbench.jmx.IJMXServiceListener;
import info.evanchik.eclipse.karaf.workbench.jmx.IJMXServiceManager;
import info.evanchik.eclipse.karaf.workbench.jmx.JMXServiceDescriptor;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.management.MBeanServer;
import javax.management.MBeanServerFactory;
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
	private static final class JMXServiceDescriptorContentProvider
		implements IStructuredContentProvider, IJMXServiceListener
	{
		protected ListViewer concreteViewer;

		public JMXServiceDescriptorContentProvider() {
			// This space intentionally left blank
		}

		@Override
        public void dispose() {
			// What to do?
		}

		@Override
        public Object[] getElements(final Object element) {
			final IJMXServiceManager jmxServiceManager = KarafWorkbenchActivator.getDefault().getJMXServiceManager();
			final List<JMXServiceDescriptor> jmxServiceDescriptors = jmxServiceManager.getJMXServices();

			return jmxServiceDescriptors.toArray();
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
	private static final class JMXServiceDescriptorLabelProvider extends LabelProvider {

		public JMXServiceDescriptorLabelProvider() {
			super();
		}

		@Override
		public Image getImage(final Object element) {
			return KarafWorkbenchActivator.getDefault().getImageRegistry().get(KarafWorkbenchActivator.LOGO_16X16_IMG);
		}

		@Override
		public String getText(final Object element) {
			final JMXServiceDescriptor jmxServiceDescriptor = (JMXServiceDescriptor) element;
			return jmxServiceDescriptor.getName();
		}
	}

	public static final String VIEW_ID = "info.evanchik.eclipse.karaf.workbench.jmx.serversView";

	private ListViewer viewer;

	@Override
	public void createPartControl(final Composite parent) {
		parent.setLayout(new FillLayout());

		final JMXServiceDescriptorContentProvider contentProvider =
			new JMXServiceDescriptorContentProvider();

		viewer = new ListViewer(parent, SWT.SINGLE);
		viewer.setContentProvider(contentProvider);
		viewer.setLabelProvider(new JMXServiceDescriptorLabelProvider());
		viewer.setInput(KarafWorkbenchActivator.getDefault().getJMXServiceManager());

		KarafWorkbenchActivator.getDefault().getJMXServiceManager().addJMXServiceListener(contentProvider);

		viewer.addDoubleClickListener(new IDoubleClickListener() {

			@Override
            public void doubleClick(final DoubleClickEvent event) {
				final IStructuredSelection selection =
					(IStructuredSelection)event.getSelection();

				final JMXServiceDescriptor connection =
					(JMXServiceDescriptor)selection.getFirstElement();

				final JMXConnector connector =
					KarafWorkbenchActivator.getDefault()
						.getJMXTransportRegistry().getJMXConnector(connection);

				// TODO: Update the data provided by this connector
			}
		});

		addLocalMBeanServer();
		initContextMenu();

		KarafWorkbenchActivator.getDefault().getJMXTransportRegistry().loadTransportExtensions();
	}

	@Override
	public void setFocus() {
		viewer.getControl().setFocus();
	}

    private void addLocalMBeanServer() {
		final MBeanServer mbs = findLocalMBeanServer();
		if(mbs == null) {
			return;
		}

		final JMXServiceDescriptor localJmxService =
			JMXServiceDescriptor.getLocalJMXServiceDescriptor(null, null);
		KarafWorkbenchActivator.getDefault().getJMXServiceManager().addJMXService(localJmxService);
	}

    private MBeanServer findLocalMBeanServer() {
		final ArrayList<MBeanServer> mbeanServers = MBeanServerFactory.findMBeanServer(null);
		final Iterator<MBeanServer> iter = mbeanServers.iterator();
		while (iter.hasNext()) {
			final MBeanServer mbeanServer = iter.next();
			if (mbeanServer.getDefaultDomain().equals(JMXServiceDescriptor.DEFAULT_DOMAIN)) {
				return mbeanServer;
			}
		}
		return null;
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
