package org.cbccessence.models;

import android.support.annotation.Nullable;

/**
 * Created by aangjnr on 12/02/2017.
 */

public class Topic {
    Integer id;
    Integer team_id;
    Integer section_id;
    Integer sub_section_id;
    Integer modded_by;
    String topic_name;
    String short_name;
    String description;
    String upload_status;
    String file_url;
    String created_at;
    String updated_at;
    String section_name;
    String sub_section_name;


    public Topic(){

    }

    public Topic (Integer id, Integer teamId, Integer sectionId, Integer subSectionId, Integer moddedBy, String topicName, String shortName, String desctiption,
                  @Nullable String uploadStatus, @Nullable String fileUrl, String dateCreated, String dateUpdated, String sectionName, String subSectionName){
        this.id = id;
        this.team_id = teamId;
        this.section_id = sectionId;
        this.sub_section_id = subSectionId;
        this.modded_by = moddedBy;

        this.topic_name = topicName;
        this.short_name = shortName;
        this.description = desctiption;
        this.upload_status = uploadStatus;
        this.file_url = fileUrl;
        this.created_at = dateCreated;
        this.updated_at = dateUpdated;
        this.section_name = sectionName;
        this.sub_section_name = subSectionName;

    }



    public void setTopicId(Integer id){
        this.id = id;

    }

    public Integer getTopicId(){
        return id;
    }

    public void setTeamId(Integer tid){
        this.team_id = tid;

    }

    public Integer getTeamId(){
        return team_id;
    }

    public void setSectionId(Integer sid){
        this.section_id = sid;

    }
    public Integer getSectionId(){
        return section_id;
    }

    public void setSubSectionId(Integer ssid){
        this.sub_section_id = ssid;

    }
    public Integer getSubSectionId(){
        return sub_section_id;
    }
    public void setModdedBy(Integer mid){
        this.modded_by = mid;

    }

    public Integer getModdedBy(){
        return modded_by;
    }

    public void setTopicName(String topic_name){
        this.topic_name = topic_name;

    }

    public String getTopicName(){
        return topic_name;
    }

    public void setShortName(String short_name){
        this.short_name = short_name;

    }
    public String getShortName(){
        return short_name;
    }

    public void setDescription(String desc){
        this.description = desc;

    }

    public String getDescription(){
        return description;
    }
    public void setfileUrl(String url){
        this.file_url = url;

    }

    public String getFileUrl(){
        return file_url;
    }

    public void setSectionName(String secName){
        this.section_name = secName;

    }

    public String getSectionName(){
        return section_name;
    }

    public void setSubSectionName(String subSecName){
        this.sub_section_name = subSecName;

    }

    public String getSubSectionName(){
        return sub_section_name;
    }

    public void setDateCreated(String dateCreated){
        this.created_at = dateCreated;

    }

    public String getDateCreated(){
        return created_at;
    }


    public void setDateUpdated(String dateUpdated){
        this.updated_at = dateUpdated;

    }

    public String getDateUpdated(){
        return updated_at;
    }

    public void setUploadStatus(String upload){
        this.upload_status = upload;

    }

    public String getUploadStatus(){
        return upload_status;
    }

}
