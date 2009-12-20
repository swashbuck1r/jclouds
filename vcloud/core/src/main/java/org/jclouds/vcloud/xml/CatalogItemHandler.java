/**
 *
 * Copyright (C) 2009 Cloud Conscious, LLC. <info@cloudconscious.com>
 *
 * ====================================================================
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
 * ====================================================================
 */
package org.jclouds.vcloud.xml;

import java.util.SortedMap;

import org.jclouds.http.functions.ParseSax;
import org.jclouds.vcloud.domain.CatalogItem;
import org.jclouds.vcloud.domain.NamedResource;
import org.jclouds.vcloud.domain.internal.CatalogItemImpl;
import org.jclouds.vcloud.util.Utils;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import com.google.common.collect.Maps;

/**
 * @author Adrian Cole
 */
public class CatalogItemHandler extends ParseSax.HandlerWithResult<CatalogItem> {
   private StringBuilder currentText = new StringBuilder();

   private NamedResource catalog;
   private NamedResource entity;

   private String description;
   private String key;
   private SortedMap<String, String> properties = Maps.newTreeMap();

   public CatalogItem getResult() {
      return new CatalogItemImpl(catalog.getId(), catalog.getName(), catalog.getLocation(),
               description, entity, properties);
   }

   @Override
   public void startElement(String uri, String localName, String qName, Attributes attributes)
            throws SAXException {
      if (qName.equals("CatalogItem")) {
         catalog = Utils.newNamedResource(attributes);
      } else if (qName.equals("Entity")) {
         entity = Utils.newNamedResource(attributes);
      } else if (qName.equals("Property")) {
         key = attributes.getValue(attributes.getIndex("key"));
      }
   }

   public void endElement(String uri, String name, String qName) {
      if (qName.equals("Description")) {
         description = currentOrNull();
      } else if (qName.equals("Property")) {
         properties.put(key, currentOrNull());
         key = null;
      }
      currentText = new StringBuilder();
   }

   public void characters(char ch[], int start, int length) {
      currentText.append(ch, start, length);
   }

   protected String currentOrNull() {
      String returnVal = currentText.toString().trim();
      return returnVal.equals("") ? null : returnVal;
   }
}
