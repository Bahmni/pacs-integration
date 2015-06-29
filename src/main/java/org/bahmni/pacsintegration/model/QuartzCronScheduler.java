package org.bahmni.pacsintegration.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

@Entity
@Table(name = "quartz_cron_scheduler")
public class QuartzCronScheduler {

    @Id
    @Column(name = "id", unique = true)
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Integer id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "enabled")
    private Boolean enabled;

    @Column(name = "cron_statement")
    private String cronStatement;

    @Column(name = "start_delay")
    private Integer startDelay;

    public QuartzCronScheduler() {
    }

    public QuartzCronScheduler(Integer id, String name, Boolean enabled, String cronStatement, Integer startDelay) {
        this.id = id;
        this.name = name;
        this.enabled = enabled;
        this.cronStatement = cronStatement;
        this.startDelay = startDelay;
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

    public Integer getStartDelay() {
        return startDelay;
    }

    public void setStartDelay(Integer startDelay) {
        this.startDelay = startDelay;
    }
}
