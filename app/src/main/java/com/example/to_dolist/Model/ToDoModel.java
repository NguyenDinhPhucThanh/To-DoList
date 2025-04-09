package com.example.to_dolist.Model;

public class ToDoModel {
    private String task;
    private int id, status;

    // Default constructor (Fixes the error)
    public ToDoModel() {
    }

    public ToDoModel(int id, String task, int status) {
        this.id = id;
        this.task = task;
        this.status = status;
    }

    public String getTask() {
        return task;
    }

    public void setTask(String task) {
        this.task = task;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
