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
package com.jd.survey.util;

import java.lang.reflect.Type;

import org.hibernate.proxy.HibernateProxy;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import java.lang.reflect.Type;
public class HibernateProxySerializer implements JsonSerializer<HibernateProxy> {

    @Override
    public JsonElement serialize(HibernateProxy proxyObj, Type arg1, JsonSerializationContext arg2) {
        try {
            GsonBuilder gsonBuilder = new GsonBuilder();
            //below ensures deep deproxied serialization
            gsonBuilder.registerTypeHierarchyAdapter(HibernateProxy.class, new HibernateProxySerializer());
            Object deProxied = proxyObj.getHibernateLazyInitializer().getImplementation();
            return gsonBuilder.create().toJsonTree(deProxied);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

	
}