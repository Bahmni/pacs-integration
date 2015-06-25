package org.bahmni.pacsintegration.model;

import org.quartz.CronExpression;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

@Entity
@Table(name = "quartz_scheduler")
public class QuartzScheduler {

    @Id
    @Column(name = "id", unique = true)
    @SequenceGenerator(name = "order_seq", initialValue = 1)
    private Integer id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "enabled")
    private Boolean enabled;
    private String cronStatement;

    public QuartzScheduler() {
    }

    public QuartzScheduler(Integer id, String name, Boolean enabled, String cronStatement) {
        this.id = id;
        this.name = name;
        this.enabled = enabled;
        this.cronStatement = cronStatement;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public String getCronStatement() {
        return cronStatement;
    }

    public void setCronStatement(String cronStatement) {
        this.cronStatement = cronStatement;
    }
}
