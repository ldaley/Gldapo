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
package gldapo.schema.attribute.typeconversion;
import java.math.BigInteger
import javax.naming.directory.Attribute

class GldapoTypeConversions 
{
	static public String convertToStringType(Attribute value)
	{
		return value.get().toString()
	}
	
	static public Integer convertToIntegerType(Attribute value)
	{
		return value.get().toInteger()
	}
	
	static public BigInteger convertToBigIntegerType(Attribute value)
	{
		return new BigInteger(value.get());
	}
	
	static public List convertToListType(Attribute value)
	{
		def list = []
		0.upto(value.size() - 1) {
			list << value.get(it)
		}
		return list
	}
}