/**
 * File CoapParameterizationClient.java
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
package eu.artemis.demanes.lib.impl.coapParameterization.client;

import java.util.Set;
import java.util.UUID;

import org.apache.log4j.Logger;

import aQute.bnd.annotation.component.Activate;
import aQute.bnd.annotation.component.Component;
import aQute.bnd.annotation.component.Reference;
import eu.artemis.demanes.datatypes.ANES_URN;
import eu.artemis.demanes.exceptions.ParameterizationException;
import eu.artemis.demanes.exceptions.ParameterizationValueTypeException;
import eu.artemis.demanes.lib.ParameterizationProxy;
import eu.artemis.demanes.lib.impl.communication.CommUtils;
import eu.artemis.demanes.lib.impl.communication.PayloadSerializationException;
import eu.artemis.demanes.lib.services.ServiceProvider;
import eu.artemis.demanes.lib.services.URNTranslator;
import eu.artemis.demanes.logging.LogConstants;
import eu.artemis.demanes.logging.LogEntry;
import eu.artemis.demanes.parameterization.Parameterizable;

/**
 * CoapParameterizationClient
 *
 * @author leeuwencjv
 * @version 0.1
 * @since 20 okt. 2014
 *
 */
@Component(properties = "proxy=COAP")
public class CoapParameterizationClient implements Parameterizable {

	private final Logger logger = Logger.getLogger("dmns:log");

	private static final ANES_URN parService = new ANES_URN("dmns", "par");

	private static final ANES_URN listService = new ANES_URN("dmns", "par:list");

	private ServiceProvider provider;

	private ParameterizationProxy proxy;

	private URNTranslator translator;

	@Reference(target = "(messageType=COAP)", optional = false)
	public void setServiceProvider(ServiceProvider sp) {
		logger.debug(new LogEntry(this.getClass().getName(),
				LogConstants.LOG_LEVEL_DEBUG, "Reference",
				"Setting REST Service provider " + sp));

		this.provider = sp;
	}

	@Activate
	public void start() {
		logger.debug(new LogEntry(this.getClass().getName(),
				LogConstants.LOG_LEVEL_DEBUG, "LifeCycle", "Activating module"));

		this.proxy = new CoapParameterizationClientProxy(
				provider.resolve(parService), provider.resolve(listService),
				translator);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * eu.artemis.demanes.parameterization.Parameterizable#getParameter(eu.artemis
	 * .demanes.datatypes.ANES_URN)
	 */
	@Override
	public Object getParameter(ANES_URN urn) throws ParameterizationException {
		UUID uid = UUID.randomUUID();
		logger.trace(new LogEntry(this.getClass().getName(),
				LogConstants.LOG_LEVEL_TRACE, "Param", "Obtaining parameter "
						+ urn + " via COAP (" + uid + ")"));

		Object response = this.proxy.getParameter(urn);

		logger.trace(new LogEntry(this.getClass().getName(),
				LogConstants.LOG_LEVEL_TRACE, "Param", "Parameter " + urn
						+ " obtained (" + uid + ")"));
		return response;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see eu.artemis.demanes.parameterization.Parameterizable#listParameters()
	 */
	@Override
	public Set<ANES_URN> listParameters() throws ParameterizationException {
		throw new RuntimeException("NYI");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * eu.artemis.demanes.parameterization.Parameterizable#setParameter(eu.artemis
	 * .demanes.datatypes.ANES_URN, java.lang.Object)
	 */
	@Override
	public void setParameter(ANES_URN urn, Object value)
			throws ParameterizationException {
		UUID uid = UUID.randomUUID();
		logger.trace(new LogEntry(this.getClass().getName(),
				LogConstants.LOG_LEVEL_TRACE, "Param", "Setting parameter "
						+ urn + " via COAP (" + uid + ")"));

		try {
			if (translator != null && translator.URNToByte(urn) != null)
				this.proxy.setParameter(urn, CommUtils.serialize4(value));
			else
				this.proxy.setParameter(urn, CommUtils.serialize(value));
		} catch (PayloadSerializationException e) {
			throw new ParameterizationValueTypeException(e);
		}

		logger.trace(new LogEntry(this.getClass().getName(),
				LogConstants.LOG_LEVEL_TRACE, "Param", "Parameter " + urn
						+ " set (" + uid + ")"));
	}

	@Reference(optional = true)
	public void setTranslator(URNTranslator ut) {
		logger.debug(new LogEntry(this.getClass().getName(),
				LogConstants.LOG_LEVEL_DEBUG, "Reference",
				"Setting URNTranslator " + ut));

		this.translator = ut;
	}

}
