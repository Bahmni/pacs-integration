package org.bahmni.pacsintegration.atomfeed.jobs;

public interface FeedJob {
    void process() throws InterruptedException;
}
