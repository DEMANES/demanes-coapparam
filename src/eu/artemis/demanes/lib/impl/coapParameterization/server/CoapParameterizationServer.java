/**
 * File CoapParameterizationServer.java
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
package eu.artemis.demanes.lib.impl.coapParameterization.server;

import java.net.URISyntaxException;
import java.nio.ByteBuffer;

import eu.artemis.demanes.datatypes.ANES_URN;
import eu.artemis.demanes.exceptions.ParameterizationException;
import eu.artemis.demanes.lib.ParameterizationProxy;
import eu.artemis.demanes.lib.impl.communication.CommUtils;
import eu.artemis.demanes.lib.impl.communication.PayloadParsingException;
import eu.artemis.demanes.lib.services.RESTService;
import eu.artemis.demanes.lib.services.ServiceException;
import eu.artemis.demanes.lib.services.URNTranslator;
import eu.artemis.demanes.parameterization.Parameterizable;

/**
 * CoapParameterizationServer
 *
 * @author leeuwencjv
 * @version 0.1
 * @since 20 okt. 2014
 *
 */
public class CoapParameterizationServer implements RESTService {

	private static final String parNamePrefix = "urn:dmns:";

	private final ParameterizationProxy proxy;

	private final URNTranslator translator;

	/**
	 * @param myBroker
	 * @param mySerializer
	 */
	public CoapParameterizationServer(Parameterizable p, URNTranslator ut) {
		this.proxy = new CoapParameterizationProxy(p);
		this.translator = ut;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see eu.artemis.demanes.lib.services.RESTService#get(java.nio.ByteBuffer)
	 */
	@Override
	public ByteBuffer get(ByteBuffer input) throws ServiceException {
		try {
			byte[] msg = input.array();
			ANES_URN paramURN;

			if (msg.length == 1 && this.translator != null
					&& this.translator.byteToURN(msg[0]) != null) {
				paramURN = this.translator.byteToURN(msg[0]);
			} else {
				String parName = new String(CommUtils.readBytes(input));

				if (!parName.startsWith(parNamePrefix))
					parName = parNamePrefix + parName;

				paramURN = new ANES_URN(parName);
			}
			
			return ByteBuffer.wrap(this.proxy.getParameter(paramURN));
		} catch (ParameterizationException e) {		
			throw new ServiceException(e);
		} catch (URISyntaxException e) {
			throw new ServiceException("Invalid parameter URN", e);
		} catch (PayloadParsingException e) {
			throw new ServiceException("Error parsing COAP payload", e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see eu.artemis.demanes.lib.services.RESTService#put(java.nio.ByteBuffer)
	 */
	@Override
	public ByteBuffer put(ByteBuffer input) throws ServiceException {
		return this.post(input);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * eu.artemis.demanes.lib.services.RESTService#post(java.nio.ByteBuffer)
	 */
	@Override
	public ByteBuffer post(ByteBuffer input) throws ServiceException {
		try {
			byte[] msg = input.array();
			ANES_URN paramURN;

			if (msg.length == 5 && this.translator != null
					&& this.translator.byteToURN(msg[0]) != null) {
				paramURN = this.translator.byteToURN(msg[0]);
			} else {
				String parName = new String(CommUtils.readBytes(input));

				if (!parName.startsWith(parNamePrefix))
					parName = parNamePrefix + parName;

				paramURN = new ANES_URN(parName);
			}

			byte[] parValue = CommUtils.readBytes(input);
			this.proxy.setParameter(paramURN, parValue);

			return null;
		} catch (ParameterizationException e) {
			throw new ServiceException("Unable to get parameter", e);
		} catch (URISyntaxException e) {
			throw new ServiceException("Invalid parameter URN", e);
		} catch (PayloadParsingException e) {
			throw new ServiceException("Exception parsing COAP payload", e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * eu.artemis.demanes.lib.services.RESTService#delete(java.nio.ByteBuffer)
	 */
	@Override
	public ByteBuffer delete(ByteBuffer input) throws ServiceException {
		throw new ServiceException("Unsupported action: delete");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see eu.artemis.demanes.lib.services.RESTService#identifier()
	 */
	@Override
	public ANES_URN identifier() {
		return new ANES_URN("dmns", "par");
	}
}
