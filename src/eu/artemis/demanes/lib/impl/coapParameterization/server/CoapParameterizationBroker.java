/**
 * File ParameterizationCOAPServer.java
 * Created on 6 mei 2014 by oliveirafilhojad
 * 
 * This file was created for DEMANES project.
 * 
 * Copyright 2014 TNO
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package eu.artemis.demanes.lib.impl.coapParameterization.server;

import java.nio.ByteBuffer;
import java.util.UUID;

import org.apache.log4j.Logger;

import aQute.bnd.annotation.component.Activate;
import aQute.bnd.annotation.component.Component;
import aQute.bnd.annotation.component.Reference;
import eu.artemis.demanes.datatypes.ANES_URN;
import eu.artemis.demanes.impl.parameterization.ParameterizationBroker;
import eu.artemis.demanes.lib.ParameterizableRegistry;
import eu.artemis.demanes.lib.services.RESTService;
import eu.artemis.demanes.lib.services.ServiceException;
import eu.artemis.demanes.lib.services.URNTranslator;
import eu.artemis.demanes.logging.LogConstants;
import eu.artemis.demanes.logging.LogEntry;
import eu.artemis.demanes.parameterization.Parameterizable;

/**
 * ParameterizationCOAPServer
 * 
 * @author oliveirafilhojad
 * @version 0.1
 * @since 6 mei 2014
 * 
 */
@Component(immediate = true, properties = "broker.type=COAP")
public class CoapParameterizationBroker implements ParameterizableRegistry,
		RESTService {

	private final Logger logger = Logger.getLogger("dmns:log");

	private ParameterizableRegistry myBroker;

	private RESTService myServer;

	private URNTranslator translator;

	@Activate
	public void start() {
		logger.debug(new LogEntry(this.getClass().getName(),
				LogConstants.LOG_LEVEL_DEBUG, "LifeCycle", "Activating Module"));

		ParameterizationBroker broker = new ParameterizationBroker();
		this.myBroker = broker;
		this.myServer = new CoapParameterizationServer(broker, translator);
	}

	@Reference(optional = true)
	public void setTranslator(URNTranslator ut) {
		logger.debug(new LogEntry(this.getClass().getName(),
				LogConstants.LOG_LEVEL_DEBUG, "Reference",
				"Setting URNTranslator " + ut));

		this.translator = ut;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void register(Parameterizable p) {
		this.myBroker.register(p);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void unregister(Parameterizable p) {
		this.myBroker.unregister(p);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see eu.artemis.demanes.lib.services.RESTService#get(java.nio.ByteBuffer)
	 */
	@Override
	public ByteBuffer get(ByteBuffer input) throws ServiceException {
		UUID uid = UUID.randomUUID();
		logger.trace(new LogEntry(this.getClass().getName(),
				LogConstants.LOG_LEVEL_TRACE, "Param", "Parameter GET request via COAP (" + uid + ")"));
		
		ByteBuffer response = this.myServer.get(input);
		
		logger.trace(new LogEntry(this.getClass().getName(),
				LogConstants.LOG_LEVEL_TRACE, "Param", "Returning parameter via COAP (" + uid + ")"));
		return response;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see eu.artemis.demanes.lib.services.RESTService#put(java.nio.ByteBuffer)
	 */
	@Override
	public ByteBuffer put(ByteBuffer input) throws ServiceException {
		UUID uid = UUID.randomUUID();
		logger.trace(new LogEntry(this.getClass().getName(),
				LogConstants.LOG_LEVEL_TRACE, "Param", "Parameter PUT request via COAP (" + uid + ")"));
		
		ByteBuffer response = this.myServer.put(input);
		
		logger.trace(new LogEntry(this.getClass().getName(),
				LogConstants.LOG_LEVEL_TRACE, "Param", "Returning response via COAP (" + uid + ")"));
		return response;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * eu.artemis.demanes.lib.services.RESTService#post(java.nio.ByteBuffer)
	 */
	@Override
	public ByteBuffer post(ByteBuffer input) throws ServiceException {
		UUID uid = UUID.randomUUID();
		logger.trace(new LogEntry(this.getClass().getName(),
				LogConstants.LOG_LEVEL_TRACE, "Param", "Parameter POST request via COAP (" + uid + ")"));
		
		ByteBuffer response = this.myServer.post(input);
		
		logger.trace(new LogEntry(this.getClass().getName(),
				LogConstants.LOG_LEVEL_TRACE, "Param", "Returning response via COAP (" + uid + ")"));
		return response;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * eu.artemis.demanes.lib.services.RESTService#delete(java.nio.ByteBuffer)
	 */
	@Override
	public ByteBuffer delete(ByteBuffer input) throws ServiceException {
		UUID uid = UUID.randomUUID();
		logger.trace(new LogEntry(this.getClass().getName(),
				LogConstants.LOG_LEVEL_TRACE, "Param", "Parameter DELETE request via COAP (" + uid + ")"));
		
		ByteBuffer response = this.myServer.delete(input);
		
		logger.trace(new LogEntry(this.getClass().getName(),
				LogConstants.LOG_LEVEL_TRACE, "Param", "Returning response via COAP (" + uid + ")"));
		return response;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see eu.artemis.demanes.lib.services.RESTService#identifier()
	 */
	@Override
	public ANES_URN identifier() {
		return this.myServer.identifier();
	}

}
