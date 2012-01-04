/**
 * Copyright (c) 2011 Stephen Evanchik
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *  Stephen Evanchik - initial implementation
 */
package info.evanchik.eclipse.karaf.core.features;


import org.apache.commons.collections.Predicate;

/**
 * A {@link Predicate} implementation that evaluates to {@code true} if the
 * supplied input is a {@link Feature}
 *
 * @author Stephen Evanchik (evanchsa@gmail.com)
 *
 */
final class FeatureOnlyPredicate implements Predicate {

    @Override
    public boolean evaluate(final Object element) {
        return element instanceof Feature;
    }
}