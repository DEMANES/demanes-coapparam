/**
 * File CoapParameterizationServerProxy.java
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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Set;

import org.apache.log4j.Logger;

import eu.artemis.demanes.datatypes.ANES_URN;
import eu.artemis.demanes.exceptions.ParameterizationException;
import eu.artemis.demanes.exceptions.ParameterizationValueTypeException;
import eu.artemis.demanes.lib.ParameterizationProxy;
import eu.artemis.demanes.lib.impl.communication.CommUtils;
import eu.artemis.demanes.lib.impl.communication.PayloadSerializationException;
import eu.artemis.demanes.logging.LogConstants;
import eu.artemis.demanes.logging.LogEntry;
import eu.artemis.demanes.parameterization.Parameterizable;

/**
 * CoapParameterizationServerProxy
 *
 * @author leeuwencjv
 * @version 0.1
 * @since 20 okt. 2014
 *
 */
public class CoapParameterizationProxy implements ParameterizationProxy {

	private final Logger logger = Logger.getLogger("dmns:log");

	private final Parameterizable broker;

	/**
	 * @param p
	 * @param s
	 */
	public CoapParameterizationProxy(Parameterizable p) {
		this.broker = p;
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
		try {
			Object value = broker.getParameter(urn);
			return CommUtils.serialize(value);
		} catch (PayloadSerializationException e) {
			throw new ParameterizationValueTypeException(e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see eu.artemis.demanes.lib.ParameterizationProxy#listParameters()
	 */
	@Override
	public byte[] listParameters() throws ParameterizationException {
		Set<ANES_URN> urnList = broker.listParameters();

		try {
			// Put the value in a stream
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			for (ANES_URN urn : urnList) {
				bos.write((byte) urn.toString().length());
				bos.write(urn.toString().getBytes());
			}

			return bos.toByteArray();
		} catch (IOException e) {
			logger.error(new LogEntry(this.getClass().getName(),
					LogConstants.LOG_LEVEL_ERROR, "Param",
					"Unable to create URN list"));

			throw new ParameterizationValueTypeException(
					"Unable to create URN list");
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
		this.broker.setParameter(urn, value);
	}

}
