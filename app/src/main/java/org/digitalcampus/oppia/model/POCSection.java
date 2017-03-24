package org.digitalcampus.oppia.model;

import android.support.annotation.Nullable;

/**
 * Created by aangjnr on 13/02/2017.
 */

public class POCSection {


    Integer id;
    Integer team_id;
    Integer modded_by;
    String image_url;
    String created_at;
    String updated_at;
    String section_name;



    public POCSection(){

    }





    public POCSection (Integer id, Integer teamId, Integer moddedBy, String sectionName, @Nullable String imageUrl, String dateCreated, String dateUpdated){

        this.id = id;
        this.team_id = teamId;
        this.modded_by = moddedBy;
        this.image_url = imageUrl;
        this.created_at = dateCreated;
        this.updated_at = dateUpdated;
        this.section_name = sectionName;



    }

    public void setSectionId(Integer id){
        this.id = id;

    }

    public Integer getSectionId(){
        return id;
    }

    public void setTeamId(Integer tid){
        this.team_id = tid;

    }

    public Integer getTeamId(){
        return team_id;
    }


    public Integer getModdedBy(){
        return modded_by;
    }



    public void setImageUrl(String url){
        this.image_url = url;

    }

    public String getImageUrl(){
        return image_url;
    }

    public void setSectionName(String secName){
        this.section_name = secName;
    }

    public String getSectionName(){
        return section_name;
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




}
