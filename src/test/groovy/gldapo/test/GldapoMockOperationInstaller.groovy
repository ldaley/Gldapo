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
package gldapo.test
import gldapo.GldapoOperationRegistry
import gldapo.operation.GldapoOperation
import gldapo.Gldapo
/**
 * Used to install fake operations in tests
 */
class GldapoMockOperationInstaller
{
	static install(opName, opInstance)
	{
		Gldapo.instance.operations.install(opName, new Expando(newInstance: { -> opInstance }))
	}
	
	static installSearchWithResult(result)
	{
		install(GldapoOperationRegistry.SEARCH, [execute: { -> result }] as GldapoOperation)
	}
}