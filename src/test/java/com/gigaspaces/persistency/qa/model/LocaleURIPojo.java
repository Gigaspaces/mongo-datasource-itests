package com.gigaspaces.persistency.qa.model;

import com.gigaspaces.annotation.pojo.SpaceId;
import com.gigaspaces.annotation.pojo.SpaceProperty;
import com.gigaspaces.annotation.pojo.SpaceRouting;
import com.gigaspaces.metadata.SpaceDocumentSupport;

import java.net.URI;
import java.util.Locale;

/**
 * User: boris
 * Date: 04/03/14
 */
public class LocaleURIPojo implements Comparable<LocaleURIPojo> {
    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1L;

    private Locale locale;

    private URI uri;

    /** The handle. */
    private String handle;


    public LocaleURIPojo(){}

    /**
     * Gets the handle.
     *
     * @return the handle
     */

    @SpaceId
    @SpaceRouting
    public String getHandle() {
        return handle;
    }

    /**
     * Sets the handle.
     *
     * @param handle the new handle
     */
    public void setHandle(String handle) {
        this.handle = handle;
    }


    @Override
    public int compareTo(LocaleURIPojo o) {
        return handle.compareTo(o.getHandle());
    }

    @SpaceProperty(documentSupport = SpaceDocumentSupport.COPY)
    public Locale getLocale() {
        return locale;
    }

    public void setLocale(Locale locale) {
        this.locale = locale;
    }

    public URI getUri() {
        return uri;
    }

    public void setUri(URI uri) {
        this.uri = uri;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        LocaleURIPojo that = (LocaleURIPojo) o;

        if (!handle.equals(that.handle)) return false;
        if (!locale.equals(that.locale)) return false;
        if (!uri.equals(that.uri)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = locale.hashCode();
        result = 31 * result + uri.hashCode();
        result = 31 * result + handle.hashCode();
        return result;
    }
}
