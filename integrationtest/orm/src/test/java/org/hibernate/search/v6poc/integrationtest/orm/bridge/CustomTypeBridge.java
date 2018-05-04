/*
 * Hibernate Search, full-text search for your domain model
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.search.v6poc.integrationtest.orm.bridge;

import java.time.LocalDate;

import org.hibernate.search.v6poc.backend.document.DocumentElement;
import org.hibernate.search.v6poc.backend.document.IndexFieldAccessor;
import org.hibernate.search.v6poc.backend.document.IndexObjectFieldAccessor;
import org.hibernate.search.v6poc.backend.document.model.dsl.IndexSchemaElement;
import org.hibernate.search.v6poc.backend.document.model.dsl.IndexSchemaObjectField;
import org.hibernate.search.v6poc.engine.spi.BuildContext;
import org.hibernate.search.v6poc.entity.model.SearchModel;
import org.hibernate.search.v6poc.entity.pojo.bridge.TypeBridge;
import org.hibernate.search.v6poc.entity.pojo.bridge.mapping.AnnotationBridgeBuilder;
import org.hibernate.search.v6poc.entity.pojo.model.PojoElement;
import org.hibernate.search.v6poc.entity.pojo.model.PojoModelElementAccessor;
import org.hibernate.search.v6poc.entity.pojo.model.PojoModelType;
import org.hibernate.search.v6poc.integrationtest.orm.bridge.annotation.CustomTypeBridgeAnnotation;
import org.hibernate.search.v6poc.util.impl.integrationtest.common.rule.StaticCounters;

public final class CustomTypeBridge implements TypeBridge {

	public static final StaticCounters.Key INSTANCE_COUNTER_KEY = StaticCounters.createKey();
	public static final StaticCounters.Key CLOSE_COUNTER_KEY = StaticCounters.createKey();

	private static final String TEXT_PROPERTY_NAME = "text";
	private static final String LOCAL_DATE_PROPERTY_NAME = "localDate";
	private static final String TEXT_FIELD_NAME = "text";
	private static final String LOCAL_DATE_FIELD_NAME = "date";

	public static final class Builder implements AnnotationBridgeBuilder<TypeBridge, CustomTypeBridgeAnnotation> {

		private String objectName;

		@Override
		public void initialize(CustomTypeBridgeAnnotation annotation) {
			objectName( annotation.objectName() );
		}

		public Builder objectName(String value) {
			this.objectName = value;
			return this;
		}

		@Override
		public TypeBridge build(BuildContext buildContext) {
			return new CustomTypeBridge( objectName );
		}
	}

	private final String objectName;

	private PojoModelElementAccessor<String> textPropertyAccessor;
	private PojoModelElementAccessor<LocalDate> localDatePropertyAccessor;
	private IndexObjectFieldAccessor objectFieldAccessor;
	private IndexFieldAccessor<String> textFieldAccessor;
	private IndexFieldAccessor<LocalDate> localDateFieldAccessor;

	private CustomTypeBridge(String objectName) {
		StaticCounters.get().increment( INSTANCE_COUNTER_KEY );
		this.objectName = objectName;
	}

	@Override
	public void bind(IndexSchemaElement indexSchemaElement, PojoModelType bridgedPojoModelType,
			SearchModel searchModel) {
		textPropertyAccessor = bridgedPojoModelType.property( TEXT_PROPERTY_NAME ).createAccessor( String.class );
		localDatePropertyAccessor = bridgedPojoModelType.property( LOCAL_DATE_PROPERTY_NAME ).createAccessor( LocalDate.class );

		IndexSchemaObjectField objectField = indexSchemaElement.objectField( objectName );
		objectFieldAccessor = objectField.createAccessor();
		textFieldAccessor = objectField.field( TEXT_FIELD_NAME ).asString().createAccessor();
		localDateFieldAccessor = objectField.field( LOCAL_DATE_FIELD_NAME ).asLocalDate().createAccessor();
	}

	@Override
	public void write(DocumentElement target, PojoElement source) {
		String textSourceValue = textPropertyAccessor.read( source );
		LocalDate localDateSourceValue = localDatePropertyAccessor.read( source );
		if ( textSourceValue != null || localDateSourceValue != null ) {
			DocumentElement object = objectFieldAccessor.add( target );
			textFieldAccessor.write( object, textSourceValue );
			localDateFieldAccessor.write( object, localDateSourceValue );
		}
	}

	@Override
	public void close() {
		StaticCounters.get().increment( CLOSE_COUNTER_KEY );
	}
}
