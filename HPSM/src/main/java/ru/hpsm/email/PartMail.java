package ru.hpsm.email;

import java.time.LocalDateTime;

public class PartMail {
    private String body;
    private String title;
    private String numberTask;
    private LocalDateTime localDateTime;
    private boolean isProcessing = false;
    private boolean isNew = true;
    private boolean isCompleted = false;


    public String getNumberTask() {
        return numberTask;
    }

    public void setNumberTask(String numberTask) {
        this.numberTask = numberTask;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public LocalDateTime getLocalDateTime() {
        return localDateTime;
    }

    public void setLocalDateTime(LocalDateTime localDateTime) {
        this.localDateTime = localDateTime;
    }

    @Override
    public String toString() {
        return "PartMail{" +
                "body='" + body + '\'' +
                ", title='" + title + '\'' +
                ", numberTask='" + numberTask + '\'' +
                ", localDateTime=" + localDateTime +
                '}';
    }


    public boolean isProcessing() {
        return isProcessing;
    }

    public void setProcessing(boolean processing) {
        isProcessing = processing;
    }

    public boolean isNew() {
        return isNew;
    }

    public void setNew(boolean aNew) {
        isNew = aNew;
    }

    public boolean isCompleted() {
        return isCompleted;
    }

    public void setCompleted(boolean completed) {
        isCompleted = completed;
    }
}
