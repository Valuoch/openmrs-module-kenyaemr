/**
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */
package org.openmrs.module.kenyaemr.api;

import org.openmrs.Location;
import org.openmrs.api.OpenmrsService;
import org.springframework.transaction.annotation.Transactional;

/**
 * Business methods for the Kenya EMR application
 */
public interface KenyaEmrService extends OpenmrsService {
	
	/**
	 * @return whether or not all required settings in the application are configured.
	 * @should return false before default location has been set
	 * @should return true after everything is configured
	 */
	boolean isConfigured();
	
	/**
	 * Sets the default location for this server, i.e. the value that should be auto-set for new
	 * encounters, visits, etc.
	 * 
	 * @param location
	 */
	@Transactional
	void setDefaultLocation(Location location);

	/**
	 * Gets the default location for this server.
	 * 
	 * @return
	 * @should throw an exception if the default location has not been set
	 * @should get the default location when set
	 */
	@Transactional(readOnly=true)
	Location getDefaultLocation();
	
}
