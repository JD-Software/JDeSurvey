  /*Copyright (C) 2014  JD Software, Inc.

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License as
    published by the Free Software Foundation, either version 3 of the
    License, or (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Affero General Public License for more details.

    You should have received a copy of the GNU Affero General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
  */
package com.jd.survey.web;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;


import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanExpressionContext;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.format.AnnotationFormatterFactory;
import org.springframework.format.Parser;
import org.springframework.format.Printer;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;
import org.springframework.format.datetime.joda.JodaTimeContextHolder;
import org.springframework.format.datetime.joda.JodaTimeFormatterRegistrar;
import org.springframework.util.StringUtils;

public class DateTimeFormatAnnotationFormatterFactory implements
AnnotationFormatterFactory<DateTimeFormat>, ApplicationContextAware {


	private final Set<Class<?>> fieldTypes;
	private ConfigurableListableBeanFactory beanFactory = null;


	public DateTimeFormatAnnotationFormatterFactory() {
		this.fieldTypes = createFieldTypes();
	}


	/**
	 * Create the set of field types that may be annotated with @DateTimeFormat.
	 * Note: the 3 ReadablePartial concrete types are registered explicitly
	 * since addFormatterForFieldType rules exist for each of these types (if we
	 * did not do this, the default byType rules for LocalDate, LocalTime, and
	 * LocalDateTime would take precedence over the annotation rule, which is
	 * not what we want)
	 * 
	 * @see JodaTimeFormatterRegistrar#registerFormatters(org.springframework.format.FormatterRegistry)
	 */
	private Set<Class<?>> createFieldTypes() {
		Set<Class<?>> rawFieldTypes = new HashSet<Class<?>>(7);
		rawFieldTypes.add(Date.class);
		rawFieldTypes.add(Calendar.class);
		rawFieldTypes.add(Long.class);
		return Collections.unmodifiableSet(rawFieldTypes);
	}


	public Set<Class<?>> getFieldTypes() {
		return fieldTypes;
	}


	public Printer<Date> getPrinter(DateTimeFormat annotation,
			Class<?> fieldType) {
		if (Date.class.isAssignableFrom(fieldType)) {
			return new DatePrinter(annotation);
		} else {
			return null;
		}
		// return configureFormatterFrom(annotation, fieldType);
	}


	public Parser<Date> getParser(DateTimeFormat annotation, Class<?> fieldType) {
		if (Date.class.isAssignableFrom(fieldType)) {
			return new DatePrinter(annotation);
		} else {
			return null;
		}
	}


	public final class DatePrinter implements Printer<Date>, Parser<Date> {


		private DateTimeFormat dateTimeFormat;


		/**
		 * Create a new ReadableInstantPrinter.
		 * 
		 * @param formatter
		 *            the Joda DateTimeFormatter instance
		 */
		public DatePrinter(DateTimeFormat dateTimeFormat) {
			this.dateTimeFormat = dateTimeFormat;
		}


		private String evaluateExpression(String expression) {
			Object value = beanFactory.getBeanExpressionResolver().evaluate(
					expression, new BeanExpressionContext(beanFactory, null));
			return value != null ? value.toString() : null;
		}


		private DateTimeFormatter getJodaFormatter() {
			return configureDateTimeFormatterFrom(this.dateTimeFormat);
		}


		public String print(Date date, Locale locale) {
			return JodaTimeContextHolder.getFormatter(getJodaFormatter(),
					locale).print(new DateTime(date));
		}


		public Date parse(String text, Locale locale) throws ParseException {
			return JodaTimeContextHolder
					.getFormatter(getJodaFormatter(), locale)
					.parseDateTime(text).toDate();
		}


		private DateTimeFormatter configureDateTimeFormatterFrom(
				DateTimeFormat annotation) {
			if (StringUtils.hasLength(annotation.pattern())) {
				return forPattern(evaluateExpression(annotation.pattern()));
			} else if (annotation.iso() != ISO.NONE) {
				return forIso(annotation.iso());
			} else {
				return forStyle(evaluateExpression(annotation.style()));
			}
		}


		private DateTimeFormatter forPattern(String pattern) {
			return org.joda.time.format.DateTimeFormat.forPattern(pattern);
		}


		private DateTimeFormatter forStyle(String style) {
			return org.joda.time.format.DateTimeFormat.forStyle(style);
		}


		private DateTimeFormatter forIso(ISO iso) {
			if (iso == ISO.DATE) {
				return org.joda.time.format.ISODateTimeFormat.date();
			} else if (iso == ISO.TIME) {
				return org.joda.time.format.ISODateTimeFormat.time();
			} else {
				return org.joda.time.format.ISODateTimeFormat.dateTime();
			}
		}
	}


	public void setApplicationContext(ApplicationContext applicationContext)
			throws BeansException {
		beanFactory = ((ConfigurableApplicationContext) applicationContext)
				.getBeanFactory();
	}
}