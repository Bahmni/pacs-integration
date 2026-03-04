/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at https://www.bahmni.org/license/mplv2hd.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

package org.bahmni.module.pacsintegration.repository;

import org.bahmni.module.pacsintegration.model.ImagingStudyReference;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ImagingStudyReferenceRepository extends JpaRepository<ImagingStudyReference, Integer> {
    
    ImagingStudyReference findByStudyInstanceUid(String studyInstanceUid);
    
    List<ImagingStudyReference> findByOrderId(Integer orderId);
}
