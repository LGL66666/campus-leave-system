package com.campus.leave.model;

import java.io.Serializable;

public class ClassInfo implements Serializable {
    private int id;
    private int collegeId;
    private String collegeName;
    private String name;
    private int headTeacherId;
    private String headTeacherName;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCollegeId() {
        return collegeId;
    }

    public void setCollegeId(int collegeId) {
        this.collegeId = collegeId;
    }

    public String getCollegeName() {
        return collegeName;
    }

    public void setCollegeName(String collegeName) {
        this.collegeName = collegeName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getHeadTeacherId() {
        return headTeacherId;
    }

    public void setHeadTeacherId(int headTeacherId) {
        this.headTeacherId = headTeacherId;
    }

    public String getHeadTeacherName() {
        return headTeacherName;
    }

    public void setHeadTeacherName(String headTeacherName) {
        this.headTeacherName = headTeacherName;
    }
}
