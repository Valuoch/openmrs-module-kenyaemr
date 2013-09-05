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

package org.openmrs.module.kenyaemr.fragment.controller;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Concept;
import org.openmrs.Patient;
import org.openmrs.Relationship;
import org.openmrs.Visit;
import org.openmrs.api.context.Context;
import org.openmrs.api.context.ContextAuthenticationException;
import org.openmrs.calculation.result.CalculationResult;
import org.openmrs.module.kenyaemr.regimen.RegimenManager;
import org.openmrs.module.kenyaemr.EmrConstants;
import org.openmrs.module.kenyaemr.util.EmrUiUtils;
import org.openmrs.module.kenyaemr.calculation.CalculationUtils;
import org.openmrs.module.kenyaemr.api.KenyaEmrService;
import org.openmrs.module.kenyaemr.calculation.library.hiv.art.InitialArtStartDateCalculation;
import org.openmrs.module.kenyaemr.regimen.RegimenChange;
import org.openmrs.module.kenyaemr.regimen.RegimenChangeHistory;
import org.openmrs.module.kenyaui.KenyaUiUtils;
import org.openmrs.module.kenyaui.annotation.PublicAction;
import org.openmrs.ui.framework.SimpleObject;
import org.openmrs.ui.framework.UiUtils;
import org.openmrs.ui.framework.annotation.SpringBean;
import org.openmrs.ui.framework.fragment.action.SuccessResult;
import org.openmrs.ui.framework.session.Session;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Fragment actions generally useful for KenyaEMR
 */
public class EmrUtilsFragmentController {

	protected static final Log log = LogFactory.getLog(EmrUtilsFragmentController.class);

	/**
	 * Checks if current user session is authenticated
	 * @return simple object {authenticated: true/false}
	 */
	@PublicAction
	public SimpleObject isAuthenticated() {
		return SimpleObject.create("authenticated", Context.isAuthenticated());
	}

	/**
	 * Attempt to authenticate current user session with the given credentials
	 * @param username the username
	 * @param password the password
	 * @return simple object {authenticated: true/false}
	 */
	@PublicAction
	public SimpleObject authenticate(@RequestParam(value = "username", required = false) String username,
									 @RequestParam(value = "password", required = false) String password) {
		try {
			Context.authenticate(username, password);
		} catch (ContextAuthenticationException ex) {
			// do nothing
		}
		return isAuthenticated();
	}

	/**
	 * Gets the next HIV patient number from the generator
	 * @param comment the optional comment
	 * @return simple object { value: identifier value }
	 */
	public SimpleObject nextHivUniquePatientNumber(@RequestParam(required = false, value = "comment") String comment) {
		if (comment == null) {
			comment = "KenyaEMR UI";
		}

		String id = Context.getService(KenyaEmrService.class).getNextHivUniquePatientNumber(comment);
		return SimpleObject.create("value", id);
	}

	/**
	 * Voids the given relationship
	 * @param relationship the relationship
	 * @param reason the reason for voiding
	 * @return the simplified visit
	 */
	public SuccessResult voidRelationship(@RequestParam("relationshipId") Relationship relationship, @RequestParam("reason") String reason) {
		Context.getPersonService().voidRelationship(relationship, reason);
		return new SuccessResult("Relationship voided");
	}

	/**
	 * Voids the given visit
	 * @param visit the visit
	 * @param reason the reason for voiding
	 * @return the simplified visit
	 */
	public SuccessResult voidVisit(@RequestParam("visitId") Visit visit, @RequestParam("reason") String reason) {
		Context.getVisitService().voidVisit(visit, reason);
		return new SuccessResult("Visit voided");
	}

	/**
	 * Gets the duration since patient started ART
	 * @param patient the patient
	 * @param now the current time reference
	 * @return the regimen and duration
	 */
	public SimpleObject currentArvRegimen(@RequestParam("patientId") Patient patient, @RequestParam("now") Date now, @SpringBean RegimenManager regimenManager, @SpringBean EmrUiUtils kenyaEmrUi, @SpringBean KenyaUiUtils kenyaUi, UiUtils ui) {
		Concept arvs = regimenManager.getMasterSetConcept("ARV");
		RegimenChangeHistory history = RegimenChangeHistory.forPatient(patient, arvs);
		RegimenChange current = history.getLastChangeBeforeDate(now);

		return SimpleObject.create(
				"regimen", current != null ? kenyaEmrUi.formatRegimenShort(current.getStarted(), ui) : null,
				"duration", current != null ? kenyaUi.formatInterval(current.getDate(), now) : null
		);
	}

	/**
	 * Gets the duration since patient started ART
	 * @param patient the patient
	 * @param now the current time reference
	 * @return the duration interval
	 */
	public SimpleObject durationSinceStartArt(@RequestParam("patientId") Patient patient, @RequestParam("now") Date now, @SpringBean KenyaUiUtils kenyaUi) {
		CalculationResult result = CalculationUtils.evaluateForPatient(InitialArtStartDateCalculation.class, null, patient);
		Date artStartDate = result != null ? (Date) result.getValue() : null;

		return SimpleObject.create("duration", artStartDate != null ? kenyaUi.formatInterval(artStartDate, now) : null);
	}

	/**
	 * Gets the recently viewed patient list
	 * @return the simple patients
	 */
	public SimpleObject[] recentlyViewed(UiUtils ui, Session session) {
		String attrName = EmrConstants.APP_CHART + ".recentlyViewedPatients";

		List<Integer> recent = session.getAttribute(attrName, List.class);
		List<Patient> pats = new ArrayList<Patient>();
		if (recent != null) {
			for (Integer ptId : recent) {
				pats.add(Context.getPatientService().getPatient(ptId));
			}
		}

		return ui.simplifyCollection(pats);
	}
}