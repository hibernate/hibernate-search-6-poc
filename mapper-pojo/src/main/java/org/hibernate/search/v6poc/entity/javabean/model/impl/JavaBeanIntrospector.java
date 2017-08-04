/*
 * Hibernate Search, full-text search for your domain model
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.search.v6poc.entity.javabean.model.impl;

import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;

import org.hibernate.search.v6poc.entity.pojo.model.spi.PojoIntrospector;
import org.hibernate.search.v6poc.entity.pojo.model.spi.ReadableProperty;
import org.hibernate.search.v6poc.util.SearchException;

/**
 * A very simple introspector roughly following Java Beans conventions.
 * <p>
 * As per JavaBeans conventions, only public getters are supported, and field access is not.
 *
 * @author Yoann Rodiere
 */
public class JavaBeanIntrospector implements PojoIntrospector {

	private static final JavaBeanIntrospector INSTANCE = new JavaBeanIntrospector();

	public static JavaBeanIntrospector get() {
		return INSTANCE;
	}

	private JavaBeanIntrospector() {
		// Private constructor, use get() instead
	}

	@Override
	public ReadableProperty findReadableProperty(Class<?> holderType, String name) {
		// TODO also handle primitive property types?
		return doFind( holderType, name, null );
	}

	@Override
	public ReadableProperty findReadableProperty(Class<?> holderType, String name, Class<?> propertyType) {
		return doFind( holderType, name, propertyType );
	}

	private ReadableProperty doFind(Class<?> holderType, String name, Class<?> expectedPropertyType) {
		// TODO also handle inherited methods
		// TODO make sure this works with private methods
		try {
			String normalizedName = Introspector.decapitalize( name );
			PropertyDescriptor propertyDescriptor = getPropertyDescriptor( holderType, normalizedName );
			Class<?> actualPropertyType = propertyDescriptor.getPropertyType();
			if ( expectedPropertyType != null && !expectedPropertyType.isAssignableFrom( actualPropertyType ) ) {
				throw new ClassCastException( actualPropertyType + " cannot be cast to " + expectedPropertyType );
			}
			return new JavaBeanReadableProperty( normalizedName, propertyDescriptor.getReadMethod() );
		}
		catch (IntrospectionException | IllegalAccessException | RuntimeException e) {
			throw new SearchException( "Exception while retrieving property reference for '"
					+ name + "' of type '" + expectedPropertyType + "' on '" + holderType + "'", e );
		}
	}

	private PropertyDescriptor getPropertyDescriptor(Class<?> holderType, String propertyName) throws IntrospectionException {
		PropertyDescriptor[] propertyDescriptors = Introspector.getBeanInfo( holderType ).getPropertyDescriptors();
		for ( PropertyDescriptor descriptor : propertyDescriptors ) {
			if ( propertyName.equals( descriptor.getName() ) ) {
				return descriptor;
			}
		}
		throw new SearchException( "JavaBean property '" + propertyName + "' not found in '" + holderType + "'" );
	}

}
