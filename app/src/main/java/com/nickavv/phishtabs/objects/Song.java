package com.nickavv.phishtabs.objects;

import java.io.Serializable;

/**
 * Created by Nick on 11/9/2015.
 */
public class Song implements Serializable {

    private long id;

    private String title;

    private String fileName;

    private boolean isCoverOnly;

    private boolean isLiveOnly;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public boolean isCoverOnly() {
        return isCoverOnly;
    }

    public void setIsCoverOnly(boolean isCoverOnly) {
        this.isCoverOnly = isCoverOnly;
    }

    public boolean isLiveOnly() {
        return isLiveOnly;
    }

    public void setIsLiveOnly(boolean isLiveOnly) {
        this.isLiveOnly = isLiveOnly;
    }
}
