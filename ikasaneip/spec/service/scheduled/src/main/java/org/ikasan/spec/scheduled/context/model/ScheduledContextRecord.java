package org.ikasan.spec.scheduled.context.model;

public interface ScheduledContextRecord {

    String getId();

    String getContextName();

    void setContextName(String contextName);

    ContextTemplate getContext();

    void setContext(ContextTemplate context);

    long getTimestamp();

    void setTimestamp(long timestamp);
}
