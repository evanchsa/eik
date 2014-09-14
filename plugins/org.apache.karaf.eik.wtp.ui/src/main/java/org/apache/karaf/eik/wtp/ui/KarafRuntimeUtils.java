/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.karaf.eik.wtp.ui;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.FontMetrics;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

public final class KarafRuntimeUtils {

    private static FontMetrics fontMetrics;

    protected static void initializeDialogUnits(Control testControl) {
        // Compute and store a font metric
        GC gc = new GC(testControl);
        gc.setFont(JFaceResources.getDialogFont());
        fontMetrics = gc.getFontMetrics();
        gc.dispose();
    }

    /**
     * Returns a width hint for a button control.
     */
    protected static int getButtonWidthHint(Button button) {
        int widthHint = Dialog.convertHorizontalDLUsToPixels(fontMetrics, IDialogConstants.BUTTON_WIDTH);
        return Math.max(widthHint, button.computeSize(SWT.DEFAULT, SWT.DEFAULT, true).x);
    }

    /**
     * Create a new button with the standard size.
     *
     * @param comp the component to add the button to
     * @param label the button label
     * @return a button
     */
    public static Button createButton(Composite comp, String label) {
        Button b = new Button(comp, SWT.PUSH);
        b.setText(label);
        if (fontMetrics == null)
            initializeDialogUnits(comp);
        GridData data = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
        data.widthHint = getButtonWidthHint(b);
        b.setLayoutData(data);
        return b;
    }

}
