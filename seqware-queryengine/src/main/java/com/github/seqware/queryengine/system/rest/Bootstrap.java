/*
 * Copyright (C) 2012 SeqWare
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.github.seqware.queryengine.system.rest;

import com.wordnik.swagger.jaxrs.JaxrsApiReader;
import javax.servlet.http.HttpServlet;

/**
 * Swagger hack to remove format string from their json file locations.
 * Seen in their tutorial at https://github.com/wordnik/swagger-core/wiki/java-jax-rs
 * @author dyuen
 */
public class Bootstrap extends HttpServlet {
  static {
	  JaxrsApiReader.setFormatString("");
  }
}
