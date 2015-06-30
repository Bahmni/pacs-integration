package org.bahmni.pacsintegration.model;


import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "order")
public class Order extends BaseModel{

    @Id
    @Column(name = "id", unique = true)
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Integer id;

    @ManyToOne
    private OrderType orderType;

    @Column(name = "order_uuid", unique = true, nullable = false)
    private String orderUuid;

    @Column(name = "test_name", nullable = false)
    private String testName;

    @Column(name = "test_uuid", unique = true, nullable = false)
    private String testUuid;

    @Column(name = "result")
    private String result;

    public Order(int id, OrderType orderType, String orderUuid, String testName, String testUuid, String result) {
        this.id = id;
        this.orderType = orderType;
        this.orderUuid = orderUuid;
        this.testName = testName;
        this.testUuid = testUuid;
        this.result = result;
    }

    public Order() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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
}
