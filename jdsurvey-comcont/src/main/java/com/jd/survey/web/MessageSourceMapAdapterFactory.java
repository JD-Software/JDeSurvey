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

import java.util.AbstractMap;
import java.util.Map;
import java.util.Set;


import org.springframework.beans.factory.FactoryBean;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceAware;
import org.springframework.context.NoSuchMessageException;
import org.springframework.context.i18n.LocaleContextHolder;


public class MessageSourceMapAdapterFactory implements FactoryBean<Map<String,String>>, MessageSourceAware {


	private MessageSource messageSource = null;
	
	public Map<String,String> getObject() throws Exception {
		return createMap();
	}


	public Class<?> getObjectType() {
		return Map.class;
	}


	public boolean isSingleton() {
		return true;
	}
	
    protected Map<String, String> createMap() {
        if (messageSource == null) {
            return null;
        }
        return new AbstractMap<String, String>() {
            @Override
            public String get(Object key) {
                if (key instanceof String) {
                    String resourceKey = (String) key;
                    String resource;
                    try {
                        resource = messageSource.getMessage(resourceKey, null, LocaleContextHolder.getLocale());
                    } catch (NoSuchMessageException mre) {
                        return resourceKey;
                    }
                    return (resource == null) ? resourceKey : resource;
                } else {
                    return null;
                }
            }


            @Override
            public Set<Map.Entry<String, String>> entrySet() {
            	throw new NoSuchMethodError("unimplemented method : should not be called");
            }
        };
    }


	public void setMessageSource(MessageSource messageSource) {
		this.messageSource = messageSource; 
	}
}