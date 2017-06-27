package org.cbccessence.cch.model;


/**
 * Created by aangjnr on 30/05/2017.
 */

public class User {

    String firstName;
    String lastName;
    String phoneNumber;
    String DOB;
    String gender;
    String nationalId;
    String clientType;
    String afyaChannel;
    String afyaStartWeek;
    String facilityId;
    String insured;
    String language;
    String location;
    String education;
    String relativePhoneNumber;
    String timestamp;
    int syncStatus;


    public User(String firstName, String lastName, String phoneNumber, String DOB, String gender, String nationalId, String clientType,
                String afyaChannel, String afyaStartWeek, String facilityId, String insured, String language, String location,
                String education, String relativePhoneNumber, int syncStatus){

         this.firstName = firstName;
         this.lastName = lastName;
         this.phoneNumber = phoneNumber;
         this.DOB = DOB;
         this.gender = gender;
         this.nationalId = nationalId;
         this.clientType = clientType;
         this.afyaChannel = afyaChannel;
         this.afyaStartWeek = afyaStartWeek;
         this.facilityId = facilityId;
         this.insured = insured;
         this.language = language;
         this.location = location;
         this.education = education;
         this.relativePhoneNumber = relativePhoneNumber;
         this.syncStatus = syncStatus;
    }




    public void setFirstName(String f_name){
        this.firstName = f_name;

    }

    public String getFirstName(){
        return firstName;
    }

    public void setLastName(String l_name){
        this.lastName = l_name;

    }

    public String getLastName(){
        return lastName;
    }

    public void setPhoneNumber(String phone){
        this.phoneNumber = phone;

    }

    public String getPhoneNumber(){
        return phoneNumber;
    }

    public void setDOB(String dob){
        this.DOB = dob;

    }

    public String getDOB(){
        return DOB;
    }

    public void setGender(String gender){
        this.gender = gender;

    }

    public String getGender(){
        return gender;
    }
    public void setNationalId(String nat_id){
        this.nationalId = nat_id;

    }

    public String getNationalId(){
        return nationalId;
    }

    public void setClientType(String client_type){
        this.clientType = client_type;

    }

    public String getClientType(){
        return clientType;
    }

    public void setAfyaChannel(String channel){
        this.afyaChannel = channel;

    }

    public String getAfyaChannel(){
        return afyaChannel;
    }


    public void setAfyaStartWeek(String start_week){
        this.afyaStartWeek = start_week;

    }

    public String getAfyaStartWeek(){
        return afyaStartWeek;
    }



    public void setFacilityId(String facility_id){
        this.facilityId = facility_id;

    }

    public String getFacilityId(){
        return facilityId;
    }

    public void setIsInsured(String insured){
        this.insured = insured;

    }

    public String getIsInsured(){
        return insured;
    }

    public void setLanguage(String lang){
        this.language = lang;

    }

    public String getLanguage(){
        return language;
    }

    public void setLocation(String loc){
        this.location = loc;

    }

    public String getLocation(){
        return location;
    }
    public void setEducation(String edu){
        this.education = edu;

    }

    public String getEducation(){
        return education;
    }

    public void setRelativePhoneNumber(String alt_phone){
        this.relativePhoneNumber = alt_phone;

    }

    public String getRelativePhoneNumber(){
        return relativePhoneNumber;
    }

    public void setTimestamp(String time){
        this.timestamp = time;

    }

    public String getTimestamp(){
        return timestamp;
    }

    public void setSyncStatus(int sync){
        this.syncStatus = sync;

    }

    public int getSyncStatus(){
        return syncStatus;
    }


}
