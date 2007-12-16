/* 
 * Copyright 2007 Luke Daley
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
package gldapo
import gldapo.exception.GldapoInitializationException

class GldapoTest extends GroovyTestCase 
{
	void testInitialiseDefaultConf() 
	{
		Gldapo.initialize("dev")
		assertEquals(2, Gldapo.instance.directories.size())
		assertEquals(50, Gldapo.instance.directories["t1"].searchControls.countLimit) // Tests env collapse
	}
	
	void testNullUrlExplodes()
	{
		shouldFail {
			Gldapo.initialize(new File("2853kgmpv0").toURL())
		}
	}
}