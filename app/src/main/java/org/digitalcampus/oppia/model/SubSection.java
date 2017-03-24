package org.digitalcampus.oppia.model;

import android.support.annotation.Nullable;

/**
 * Created by aangjnr on 13/02/2017.
 */

public class SubSection {

    Integer id;
    Integer team_id;
    Integer section_id;
    Integer modded_by;
    String name;
    String image_url;
    String created_at;
    String updated_at;
    String section_name;


    public SubSection(){


    }

    public SubSection (Integer id, Integer teamId, Integer sectionId, Integer moddedBy, String name, @Nullable String imageUrl, String dateCreated,
                       String dateUpdated, String sectionName){


        this.id = id;
        this.team_id = teamId;
        this.section_id = sectionId;
        this.modded_by = moddedBy;
        this.name = name;
        this.image_url = imageUrl;
        this.created_at = dateCreated;
        this.updated_at = dateUpdated;
        this.section_name = sectionName;

    }

    public void setId(Integer id){
        this.id = id;

    }

    public Integer getId(){
        return id;
    }


    public void setSectionId(Integer secId){
        this.section_id = secId;

    }

    public Integer getSectionId(){
        return section_id;
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


    public void setName(String name){
        this.name = name;
    }

    public String getName(){
        return name;
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
