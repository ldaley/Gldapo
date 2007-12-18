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

/**
 * The settings {@link Gldapo#getSettings() instance} holds the global default settings for Gldapo
 */
class GldapoSettings {
	
	/**
	 * The key that the setting should be under in the config map
	 * 
	 * @see #newInstance(Map)
	 */
	static String CONFIG_KEY = "settings"
	
	/**
	 * The page size is used for search operations where no page size is specified.
	 * Paging refers to collecting large result sets in <em>chunks</em>.
	 * The default setting is {@value}
	 */
	Integer pageSize = 500
	
	/**
	 * Create a new settings object from a config map.
	 * <p>
	 * The map should have an entry under the key {@value #CONFIG_KEY} which is a map containing entries 
	 * for each of the following attributes:
	 * <ul>
	 * 	<li>{@link #pageSize}</li>
	 * </ul>
	 * <p>
	 * Example ...
	 * <pre>
	 * [
	 * 	settings: [
	 * 		pageSize: 40
	 * 	]
	 * ]
	 * </pre>
	 * 
	 * @param config the config map
	 * @return the new instance from the config
	 */
	static newInstance(Map config) {
		def settings = new GldapoSettings()
		
		if (config.containsKey(CONFIG_KEY)) {
			def settingsConfig = config[CONFIG_KEY]
			if ((settingsConfig instanceof Map) == false) throw GldapoInvalidConfigException("$CONFIG_KEY is not a Map")
			settings.pageSize = settingsConfig.pageSize
		}
		
		return settings
	}
}