package com.coditramuntana.discography.shared.error;

public abstract class ResourceNotFoundException extends RuntimeException {

    private final String resourceType;
    private final Object resourceId;

    protected ResourceNotFoundException(String resourceType, Object resourceId) {
        super(String.format("%s with id %s not found", resourceType, resourceId));
        this.resourceType = resourceType;
        this.resourceId = resourceId;
    }

    public String getResourceType() {
        return resourceType;
    }

    public Object getResourceId() {
        return resourceId;
    }
}
