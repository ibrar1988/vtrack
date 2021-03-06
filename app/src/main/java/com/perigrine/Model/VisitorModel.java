package com.perigrine.Model;

import java.security.PrivateKey;

/**
 * Created by deepika on 14/4/16.
 */
public class VisitorModel {

    private String name = "";
    private String company = "";
    private String phno;
    private String email;
    private String title;
    private String homePage;
    private String address;
    private String whomToMeet;
    private String visitorId;
    private boolean isImageTaken = false;
    private String imagePath = "";
    private String jdata="";
    private String deptID;
    private String deptName;
    private String empEmail;
    private String empPhno;
    private String empRole;
    private String hostName;
    private String departmentName_viewVisitor;
    private String purpose;
    // Added by Ibrar


    public String getDepartmentName_viewVisitor() {
        return departmentName_viewVisitor;
    }

    public void setDepartmentName_viewVisitor(String departmentName_viewVisitor) {
        this.departmentName_viewVisitor = departmentName_viewVisitor;
    }

    public String getPurpose() {
        return purpose;
    }

    public void setPurpose(String purpose) {
        this.purpose = purpose;
    }

    public String getHostName() {
        return hostName;
    }

    public void setHostName(String hostName) {
        this.hostName = hostName;
    }

    private String designation;
    private String signInTime;
    private String signOutTime;
    private String cardStatus;
    private String idCardImage;
    private String badgeId;
    private int visitingId;

    public int getVisitingId() {
        return visitingId;
    }

    public void setVisitingId(int visitingId) {
        this.visitingId = visitingId;
    }

    public String getDesignation(){
        return designation;
    }

    public String getSignInTime(){
        return signInTime;
    }

    public String getSignOutTime(){
        return signOutTime;
    }

    public String getCardStatus(){
        return cardStatus;
    }

    public String getIdCardImage(){
        return idCardImage;
    }

    public String getBadgeId() {
        return badgeId;
    }
    // End here

    public String getEmpRole() {
        return empRole;
    }

    public void setEmpRole(String empRole) {
        this.empRole = empRole;
    }

    public String getEmpEmail() {
        return empEmail;
    }

    public void setEmpEmail(String empEmail) {
        this.empEmail = empEmail;
    }

    public String getEmpPhno() {
        return empPhno;
    }

    public void setEmpPhno(String empPhno) {
        this.empPhno = empPhno;
    }

    // Ibrar added

    public VisitorModel(String name, String email, String company, String designation,
                        String signInTime, String signOutTime, String cardStatus, String idCardImage,
                        String badgeId, int visitingId) {
        this.name = name;
        this.email = email;
        this.company = company;
        this.designation = designation;
        this.signInTime = signInTime;
        this.signOutTime = signOutTime;
        this.cardStatus = cardStatus;
        this.idCardImage = idCardImage;
        this.badgeId = badgeId;
        this.visitingId = visitingId;
    }

    public VisitorModel(String name, String company) {
        this.name = name;
        this.company = company;
    }

    public VisitorModel() {
    }

    public VisitorModel(String name) {
        this.name = name;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public boolean isImageTaken() {
        return isImageTaken;
    }

    public void setIsImageTaken(boolean isImageTaken) {
        this.isImageTaken = isImageTaken;
    }

    public String getVisitorId() {
        return visitorId;
    }

    public void setVisitorId(String visitorId) {
        this.visitorId = visitorId;
    }

    public String getJdata() {
        return jdata;
    }

    public void setJdata(String jdata) {
        this.jdata = jdata;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getPhno() {
        return phno;
    }

    public void setPhno(String phno) {
        this.phno = phno;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getHomePage() {
        return homePage;
    }

    public void setHomePage(String homePage) {
        this.homePage = homePage;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getWhomToMeet() {
        return whomToMeet;
    }

    public void setWhomToMeet(String whomToMeet) {
        this.whomToMeet = whomToMeet;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public String getDeptID() {
        return deptID;
    }

    public void setDeptID(String deptID) {
        this.deptID = deptID;
    }

    public String getDeptName() {
        return deptName;
    }

    public void setDeptName(String deptName) {
        this.deptName = deptName;
    }
}
