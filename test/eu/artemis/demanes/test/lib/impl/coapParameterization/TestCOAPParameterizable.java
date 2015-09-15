/**
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

package eu.artemis.demanes.test.lib.impl.coapParameterization;

import java.nio.ByteBuffer;
import java.util.Random;

import org.apache.log4j.Logger;

import aQute.bnd.annotation.component.Activate;
import aQute.bnd.annotation.component.Component;
import eu.artemis.demanes.impl.annotations.DEM_GetParameter;
import eu.artemis.demanes.impl.annotations.DEM_SetParameter;
import eu.artemis.demanes.impl.annotations.DEM_TaskProperties;
import eu.artemis.demanes.lib.SelfRegister;
import eu.artemis.demanes.logging.LogConstants;
import eu.artemis.demanes.logging.LogEntry;

@Component(immediate = true)
@DEM_TaskProperties(brokerType = "COAP")
public class TestCOAPParameterizable implements SelfRegister {

	private final Logger logger = Logger.getLogger("dmns:log");

	@Activate
	public void start() {
		logger.debug(new LogEntry(this.getClass().getName(),
				LogConstants.LOG_LEVEL_DEBUG, "LifeCycle",
				"Activating module"));
	}
	
	@DEM_GetParameter(urn = "urn:dmns:frequency")
	public String getNames() {
		logger.info(new LogEntry(this.getClass().getName(),
				LogConstants.LOG_LEVEL_INFO, "Param", "Getting names"));

		return "Get was successful";
	}

	@DEM_GetParameter(urn = "urn:dmns:randomval")
	public Integer getSamplingFrequency() {
		Random rnd = new Random();
		int nextval = rnd.nextInt(4) * 2 + 1;

		logger.info(new LogEntry(this.getClass().getName(),
				LogConstants.LOG_LEVEL_INFO, "Param",
				"Getting random value returns with " + nextval));

		return nextval;
	}

	@DEM_SetParameter(urn = "urn:dmns:frequency")
	public void setNames(ByteBuffer value) {

		logger.info(new LogEntry(this.getClass().getName(),
				LogConstants.LOG_LEVEL_INFO, "Param",
				"Set was successful. Content: "
						+ new String(((ByteBuffer) value).array())));
	}

}
