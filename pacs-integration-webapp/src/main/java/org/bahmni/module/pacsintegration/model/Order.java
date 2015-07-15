package org.bahmni.module.pacsintegration.model;


import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "test_order")
public class Order extends BaseModel {

    @Id
    @Column(name = "id", unique = true)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name="order_type_id", nullable=false)
    private OrderType orderType;

    @Column(name = "order_uuid", unique = true, nullable = false)
    private String orderUuid;

    @Column(name = "test_name", nullable = false)
    private String testName;

    @Column(name = "test_uuid", unique = true, nullable = false)
    private String testUuid;

    @Column(name = "result")
    private String result;

    @Column(name = "creator")
    private String creator;

    @Column(name = "date_created")
    private Date dateCreated;

    @Column(name = "modifier")
    private String modifier;

    @Column(name = "date_modified")
    private Date dateModified;

    @Column(name = "order_number")
    private String orderNumber;

    public Order(int id, OrderType orderType, String orderUuid, String testName, String testUuid, String result, String orderNumber) {
        this.id = id;
        this.orderType = orderType;
        this.orderUuid = orderUuid;
        this.testName = testName;
        this.testUuid = testUuid;
        this.result = result;
        this.orderNumber = orderNumber;
    }

    public Order() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }

    public OrderType getOrderType() {
        return orderType;
    }

    public void setOrderType(OrderType orderType) {
        this.orderType = orderType;
    }

    public String getOrderUuid() {
        return orderUuid;
    }

    public void setOrderUuid(String orderUuid) {
        this.orderUuid = orderUuid;
    }

    public String getTestName() {
        return testName;
    }

    public void setTestName(String testName) {
        this.testName = testName;
    }

    public String getTestUuid() {
        return testUuid;
    }

    public void setTestUuid(String testUuid) {
        this.testUuid = testUuid;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public Date getDateModified() {
        return dateModified;
    }

    public void setDateModified(Date dateModified) {
        this.dateModified = dateModified;
    }

    public String getModifier() {
        return modifier;
    }

    public void setModifier(String modifier) {
        this.modifier = modifier;
    }

    public Date getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(Date dateCreated) {
        this.dateCreated = dateCreated;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }
}
