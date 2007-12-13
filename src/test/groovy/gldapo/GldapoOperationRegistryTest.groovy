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
import gldapo.operation.*

class GldapoOperationRegistryTest extends GroovyTestCase 
{
	def ops
	
	void setUp()
	{
		ops = new GldapoOperationRegistry()
	}
	
	void testInstall() 
	{	
		def o = [execute: {}] as GldapoOperation
		def f = new Expando(newInstance: { -> o })
		ops.install("custom", f)
		assertSame(o, ops["custom"])
	}
	
	void testOptionPassing()
	{
		def options = [t: "t"]
		def passedInOptions
		def o = [execute: {}, setOptions: { passedInOptions = it }] as GldapoOptionSubjectableOperation
		def f = new Expando(newInstance: { -> o })
		ops.install("custom", f)
		def op = ops["custom", options]
		assertSame(options, passedInOptions)
	}
	
}