package org.digitalcampus.oppia.model;

/**
 * Created by aangjnr on 08/02/2017.
 */

public class Projects {


    private Integer id;
    private Integer owner_id;
    private Integer team_id;
    private String name;
    private String dateUpdated;
    private String dateCreated;

    public Projects(){

    }

    public Integer getProjectId() {
        return id;
    }
    public void setProjectId(Integer id) {
        this.id = id;
    }

    public Integer getTeamId() {
        return team_id;
    }
    public void setTeamId(Integer team_id) {
        this.team_id = team_id;
    }

    public Integer getProjectOwnerId() {
        return owner_id;
    }
    public void setProjectOwnerId(Integer owner_id) {
        this.owner_id = owner_id;
    }
    public String getProjectName() {
        return name;
    }
    public void setProjectName(String name) {
        this.name = name;
    }

    public String getDateUpdated() {
        return dateUpdated;
    }
    public void setDateUpdated(String lastUpdated) {
        this.dateUpdated = lastUpdated;
    }

    public String getDateCreated() {
        return dateCreated;
    }
    public void setgetDateCreated(String dateCreated) {
        this.dateCreated = dateCreated;
    }
}
