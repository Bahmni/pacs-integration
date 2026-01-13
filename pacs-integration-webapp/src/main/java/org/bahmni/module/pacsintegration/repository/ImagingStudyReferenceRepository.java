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
