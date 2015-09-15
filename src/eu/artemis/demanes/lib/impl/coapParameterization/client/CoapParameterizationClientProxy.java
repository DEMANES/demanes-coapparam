/**
 * File CoapParameterizationClientProxy.java
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

import java.net.URISyntaxException;
import java.nio.ByteBuffer;

import eu.artemis.demanes.datatypes.ANES_URN;
import eu.artemis.demanes.exceptions.ParameterizationException;
import eu.artemis.demanes.exceptions.ParameterizationLinkException;
import eu.artemis.demanes.lib.ParameterizationProxy;
import eu.artemis.demanes.lib.impl.communication.PayloadParsingException;
import eu.artemis.demanes.lib.impl.communication.CommUtils;
import eu.artemis.demanes.lib.services.RESTService;
import eu.artemis.demanes.lib.services.ServiceException;
import eu.artemis.demanes.lib.services.URNTranslator;

/**
 * CoapParameterizationClientProxy
 * 
 * @author leeuwencjv
 * @version 0.1
 * @since 30 jun. 2014
 * 
 */
public class CoapParameterizationClientProxy implements ParameterizationProxy {

	private final RESTService listService;

	private final RESTService parameterizationService;

	private final URNTranslator translator;

	/**
	 * @param resolve
	 * @param resolve2
	 */
	public CoapParameterizationClientProxy(RESTService par, RESTService list,
			URNTranslator ut) {
		this.parameterizationService = par;
		this.listService = list;
		this.translator = ut;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * eu.artemis.demanes.lib.ParameterizationProxy#getParameter(eu.artemis.
	 * demanes.datatypes.ANES_URN)
	 */
	@Override
	public byte[] getParameter(ANES_URN urn) throws ParameterizationException {
		ByteBuffer args;
		if (this.translator != null && this.translator.URNToByte(urn) != null) {
			args = ByteBuffer.allocate(1);
			args.put(this.translator.URNToByte(urn));
		} else {
			String paramName = urn.toString();
			args = ByteBuffer.allocate(paramName.length() + 1);
			args.put((byte) paramName.length());
			args.put(paramName.getBytes());
		}

		args.flip();

		try {
			ByteBuffer response = parameterizationService.get(args);

			if (response == null)
				throw new ParameterizationLinkException();

			response.mark();
			byte firstByte = response.get();			
			if (this.translator != null && this.translator.byteToURN(firstByte) != null) {
				ANES_URN returnURN = translator.byteToURN(response.get());
				assert(returnURN == urn);
				
				return CommUtils.readBytes(response, 4);
			} else {
				response.reset();
				ANES_URN returnURN = new ANES_URN(CommUtils.ReadStringFromByteBuffer(response));
				assert(returnURN == urn);
				
				return CommUtils.readBytes(response);
			}
			
		} catch (ServiceException e) {
			throw new ParameterizationLinkException(e);
		} catch (PayloadParsingException e) {
			throw new ParameterizationLinkException("Exception parsing COAP payload", e);
		} catch (URISyntaxException e) {
			throw new ParameterizationLinkException("Invalid Parameter URN", e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see eu.artemis.demanes.lib.ParameterizationProxy#listParameters()
	 */
	@Override
	public byte[] listParameters() throws ParameterizationException {
		try {
			ByteBuffer response = listService.get(null);

			byte[] buf = new byte[response.remaining()];
			response.get(buf, 0, response.remaining());

			return buf;
		} catch (ServiceException e) {
			throw new ParameterizationLinkException(e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * eu.artemis.demanes.lib.ParameterizationProxy#setParameter(eu.artemis.
	 * demanes.datatypes.ANES_URN, byte[])
	 */
	@Override
	public void setParameter(ANES_URN urn, byte[] value)
			throws ParameterizationException {

		ByteBuffer args;
		if (this.translator != null && this.translator.URNToByte(urn) != null) {
			args = ByteBuffer.allocate(5);
			args.put(this.translator.URNToByte(urn));
			args.put(value);
		} else {
			String paramName = urn.toString();
			args = ByteBuffer.allocate(paramName.length() + value.length
					+ 2);

			args.put((byte) paramName.length());
			args.put(paramName.getBytes());
			args.put((byte) value.length);
			args.put(value);
		}

		args.flip();

		try {
			parameterizationService.put(args);
		} catch (ServiceException e) {
			throw new ParameterizationLinkException(e);
		}
	}
}
