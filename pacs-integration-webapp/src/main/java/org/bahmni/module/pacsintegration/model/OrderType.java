package org.bahmni.module.pacsintegration.model;

import javax.persistence.*;


@Entity
@Table(name = "order_type")
public class OrderType extends BaseModel{

    @Id
    @Column(name = "id", unique = true)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "name", nullable = false)
    private String name;

    @ManyToOne
    private Modality modality;

    public OrderType(int id, String name, Modality modality) {
        this.id = id;
        this.name = name;
        this.modality = modality;
    }

    public OrderType() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Modality getModality() {
        return modality;
    }

    public void setModality(Modality modality) {
        this.modality = modality;
    }
}
