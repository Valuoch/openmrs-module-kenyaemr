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

package org.openmrs.module.kenyaemr.metadata;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.module.kenyacore.metadata.AbstractMetadataProvider;
import org.openmrs.module.kenyacore.metadata.Requires;
import org.openmrs.module.kenyacore.metadata.installer.CoreMetadataInstaller;
import org.openmrs.module.kenyaemr.Dictionary;
import org.openmrs.module.kenyaemr.Metadata;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * MCH metadata provider
 */
@Component
@Requires("kenyaemr.common.metadata")
public class MchMetadataProvider extends AbstractMetadataProvider {

	protected static final Log log = LogFactory.getLog(MchMetadataProvider.class);

	@Autowired
	private CoreMetadataInstaller installer;

	@Override
	public void install() {
		log.info("Installing MCH metadata");

		installer.program("MCH - Child Services", "Treatment for children", Dictionary.MATERNAL_AND_CHILD_HEALTH_PROGRAM, Metadata.Program.MCHCS);
		installer.program("MCH - Mother Services", "Treatment for mothers", Dictionary.MATERNAL_AND_CHILD_HEALTH_PROGRAM, Metadata.Program.MCHMS);

		installer.encounterType("MCH Child Enrollment", "Enrollment of child onto MCH program", Metadata.EncounterType.MCHCS_ENROLLMENT);
		installer.encounterType("MCH Child Consultation", "Collection of child data during MCH visit", Metadata.EncounterType.MCHCS_CONSULTATION);
		//installer.encounterType("MCH Child HEI Exit", "Reasons that child is exited from HEI", Metadata.EncounterType.MCHCS_HEI_EXIT);
		installer.encounterType("MCH Child Immunization", "Record of child immunizations", Metadata.EncounterType.MCHCS_IMMUNIZATION);
		installer.encounterType("MCH Child Discontinuation", "Discontinuation of child from MCH program", Metadata.EncounterType.MCHCS_DISCONTINUATION);

		installer.encounterType("MCH Mother Enrollment", "Enrollment of mother onto MCH program", Metadata.EncounterType.MCHMS_ENROLLMENT);
		installer.encounterType("MCH Mother Consultation", "Collection of mother data during MCH visit", Metadata.EncounterType.MCHMS_CONSULTATION);
		installer.encounterType("MCH Mother Discontinuation", "Discontinuation of mother from MCH program", Metadata.EncounterType.MCHMS_DISCONTINUATION);
	}
}