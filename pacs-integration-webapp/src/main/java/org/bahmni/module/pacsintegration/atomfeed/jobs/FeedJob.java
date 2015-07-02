package org.bahmni.module.pacsintegration.atomfeed.jobs;

public interface FeedJob {
    void process() throws InterruptedException;
}
