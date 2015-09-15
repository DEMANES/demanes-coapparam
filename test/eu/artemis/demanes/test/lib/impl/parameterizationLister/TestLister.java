/**
 * File TestLister.java
 * 
 * This file is part of the eu.artemis.demanes.lib.coapParameterization project.
 *
 * Copyright 2014 TNO
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package eu.artemis.demanes.test.lib.impl.parameterizationLister;

import aQute.bnd.annotation.component.Component;
import aQute.bnd.annotation.component.Reference;
import eu.artemis.demanes.datatypes.ANES_URN;
import eu.artemis.demanes.exceptions.ParameterizationException;
import eu.artemis.demanes.parameterization.Parameterizable;

/**
 * TestLister
 *
 * @author leeuwencjv
 * @version 0.1
 * @since 9 feb. 2015
 *
 */
@Component
public class TestLister {

	private Parameterizable coapclient;

	@Reference(target = "(proxy=COAP)")
	public void setCOAPhandler(Parameterizable client) {
		System.out.println("Setting the parameterization coap client");
		this.coapclient = client;
		
		try {
			System.out.println(client.getParameter(new ANES_URN("test","something")));
		} catch (ParameterizationException e) {
			e.printStackTrace();
		}
	}
	
}
