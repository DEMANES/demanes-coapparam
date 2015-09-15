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

package eu.artemis.demanes.test.lib.impl.TestCOAPParameterizer;

import java.nio.ByteBuffer;

import aQute.bnd.annotation.component.Activate;
import aQute.bnd.annotation.component.Component;
import aQute.bnd.annotation.component.Deactivate;
import aQute.bnd.annotation.component.Reference;
import eu.artemis.demanes.datatypes.ANES_URN;
import eu.artemis.demanes.exceptions.ParameterizationException;
import eu.artemis.demanes.parameterization.Parameterizable;

@Component(immediate = true)
public class TestCOAPParameterizer {

	private Parameterizable coapclient;

	private Thread writerThread;

	@Activate
	public void init() {
		writerThread = new Thread(new AnoMessageWriter());
		writerThread.start();
	}

	@Deactivate
	public void stop() {
		writerThread.interrupt();
		this.coapclient = null;
	}

	@Reference(target = "(proxy=COAP)")
	public void setCOAPhandler(Parameterizable client) {
		System.out.println("Setting the parameterization coap client");
		this.coapclient = client;
	}

	/**
	 * AnoMessageWriter
	 *
	 * @author leeuwencjv
	 * @version 0.2
	 * @since 1 jul. 2014
	 *
	 */
	public class AnoMessageWriter implements Runnable {

		private int [] round_times = {3000, 5000, 8000, 10000, 12000, 15000};
		
		private int id = 0;

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.lang.Runnable#run()
		 */
		@Override
		public void run() {
			try {
				while (true) {
					try {
						Thread.sleep(1 * 1000);
						ANES_URN paramUrn = new ANES_URN("dmns", "tround");
						byte [] ans = (byte[]) coapclient.getParameter(paramUrn);

						ByteBuffer bb = ByteBuffer.wrap(ans);
						System.out.println("Receive back from the GNode that " + paramUrn + " is now " + bb.getInt() );
						Thread.sleep(3 * 1000);
						
						paramUrn = new ANES_URN("dmns", "fradiowakeup");
						ans = (byte[]) coapclient.getParameter(paramUrn);

						bb = ByteBuffer.wrap(ans);
						System.out.println("Receive back from the GNode that " + paramUrn + " is now " + bb.getInt() );
						
						/*int newval = round_times[id++ % 5];
						coapclient.setParameter(paramUrn, newval);
						System.out.println("I think I have set the " + paramUrn + " to " + newval);*/
						
					} catch (ParameterizationException e) {
						System.err.println("Error during parameterization: "
								+ e.getMessage());
						e.printStackTrace();
					} 
					//Thread.sleep(1000);
				}
			} catch (InterruptedException e) {
				//Do nothing
			}
		}
	}

}
