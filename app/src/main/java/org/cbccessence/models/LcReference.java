package org.cbccessence.models;

import android.support.annotation.Nullable;

/**
 * Created by aangjnr on 14/02/2017.
 */

public class LcReference {

    Integer id;
    Integer team_id;
    Integer modded_by;
    String ref_name;
    String short_name;
    String file_url;
    String file_size;
    String created_at;
    String updated_at;


    public LcReference(){

    }

    public LcReference (Integer id, Integer teamId, Integer moddedBy, String ref_name,  String shortName, String fileUrl, String file_size, String dateCreated, String dateUpdated){
        this.id = id;
        this.team_id = teamId;
        this.modded_by = moddedBy;
        this.ref_name = ref_name;
        this.short_name = shortName;
        this.file_url = fileUrl;
        this.created_at = dateCreated;
        this.updated_at = dateUpdated;
        this.file_size = file_size;

    }



    public void setReferenceId(Integer id){
        this.id = id;

    }

    public Integer getReferenceId(){
        return id;
    }

    public void setTeamId(Integer tid){
        this.team_id = tid;

    }

    public Integer getTeamId(){
        return team_id;
    }


    public void setModdedBy(Integer mid){
        this.modded_by = mid;

    }

    public Integer getModdedBy(){
        return modded_by;
    }

    public void setReferenceName(String ref_name){
        this.ref_name = ref_name;

    }

    public String getReferenceName(){
        return ref_name;
    }

    public void setShortName(String short_name){
        this.short_name = short_name;

    }
    public String getShortName(){
        return short_name;
    }

    public void setfileUrl(String url){
        this.file_url = url;

    }

    public String getFileUrl(){
        return file_url;
    }

    public void setfileSize(String size){
        this.file_size = size;

    }

    public String getFileSize(){
        return file_size;
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

