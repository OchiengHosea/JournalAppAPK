package com.hosea.ochieng.journalapp;

import com.google.firebase.database.IgnoreExtraProperties;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@IgnoreExtraProperties
public class Journal implements Serializable{
    private String journalID;
    private String subject, userId;
    private Date entryDate, dueDate;
    private String description;
    private Boolean completed;

    public Journal() {
    }

    public Journal(String userId, String subject, Date entryDate, Date dueDate, String description, Boolean completed) {
        this.userId = userId;
        this.subject = subject;
        this.entryDate = entryDate;
        this.dueDate = dueDate;
        this.description = description;
        this.completed = completed;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public Date getEntryDate() {
        return entryDate;
    }

    public void setEntryDate(Date entryDate) {
        this.entryDate = entryDate;
    }

    public Date getDueDate() {
        return dueDate;
    }

    public void setDueDate(Date dueDate) {
        this.dueDate = dueDate;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Boolean getCompleted() {
        return completed;
    }

    public void setCompleted(Boolean completed) {
        this.completed = completed;
    }

    public String getJournalID() {
        return journalID;
    }

    public void setJournalID(String journalID) {
        this.journalID = journalID;
    }

    public Map<String, Object> toMap(){
        HashMap<String, Object> result = new HashMap<>();
        result.put("userId", userId);
        result.put("subject", subject);
        result.put("entryDate", entryDate);
        result.put("dueDate", dueDate);
        result.put("description", description);
        result.put("completed", completed);
        return result;
    }
}
