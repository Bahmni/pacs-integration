package org.bahmni.module.pacsintegration.model;

import javax.persistence.*;

@Entity
@Table(name = "order_details")
public class OrderDetails {
    @Id
    @Column(name = "id", unique = true)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name="test_order_id", nullable=false)
    private Order order;

    @Column(name = "hl7_request", nullable = false)
    private String hl7Request;

    @Column(name = "hl7_response", nullable = false)
    private String hl7Response;

    public OrderDetails() {
    }

    public OrderDetails(Order order, String hl7Request, String hl7Response) {
        this.order = order;
        this.hl7Request = hl7Request;
        this.hl7Response = hl7Response;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public String getHl7Request() {
        return hl7Request;
    }

    public void setHl7Request(String hl7Request) {
        this.hl7Request = hl7Request;
    }

    public String getHl7Response() {
        return hl7Response;
    }

    public void setHl7Response(String hl7Response) {
        this.hl7Response = hl7Response;
    }
}
