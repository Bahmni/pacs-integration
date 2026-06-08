package org.bahmni.module.pacsintegration.model;

import javax.persistence.*;

@Entity
@Table(name = "imaging_study_reference")
public class ImagingStudyReference extends  BaseModel {
    @Id
    @Column(name = "id", unique = true)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "study_instance_uid", nullable = false)
    private String studyInstanceUid;

    @Column(name = "imaging_study_uuid", nullable = false)
    private String imagingStudyUuid;

    @ManyToOne
    @JoinColumn(name="test_order_id", nullable=false)
    private Order order;

    public ImagingStudyReference() {
    }

    public ImagingStudyReference(String studyInstanceUid, String imagingStudyUuid, Order order) {
        this.studyInstanceUid = studyInstanceUid;
        this.imagingStudyUuid = imagingStudyUuid;
        this.order = order;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getStudyInstanceUid() {
        return studyInstanceUid;
    }

    public void setStudyInstanceUid(String studyInstanceUid) {
        this.studyInstanceUid = studyInstanceUid;
    }

    public String getImagingStudyUuid() {
        return imagingStudyUuid;
    }

    public void setImagingStudyUuid(String imagingStudyUuid) {
        this.imagingStudyUuid = imagingStudyUuid;
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }
}
