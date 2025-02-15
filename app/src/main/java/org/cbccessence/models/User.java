/* 
 * This file is part of OppiaMobile - http://oppia-mobile.org/
 * 
 * OppiaMobile is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * OppiaMobile is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with OppiaMobile. If not, see <http://www.gnu.org/licenses/>.
 */

package org.cbccessence.models;

public class User {

	private String username;
	private String email;
	private String password;
	private String passwordAgain;
	private String firstname;
	private String lastname;
	private String api_key;
	private boolean scoringEnabled = true;
	private int points = 0;
	private int badges = 0;
	private boolean passwordRight = true;
	private String userrole;
	private String district;
	private String subdistrict;
	private String facility;
	private String zone;
	private String staffid;
	private String teamId;
	private String moduleId;

	public User (){}



	
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	
	public String getUserrole() {
		return userrole;
	}
	public void setUserrole(String userrole) {
		this.userrole = userrole;
	}
	
	public String getUserDistrict() {
		return district;
	}
	public void setUserDistrict(String district) {
		this.district = district;
	}
	
	public String getUserSubsistrict() {
		return subdistrict;
	}
	public void setUserSubsistrict(String subdistrict) {
		this.subdistrict = subdistrict;
	}
	
	public String getUserFacility() {
		return facility;
	}
	public void setUserFacility(String facility) {
		this.facility = facility;
	}
	
	public String getUserZone() {
		return zone;
	}
	public void setUserZone(String zone) {
		this.zone = zone;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getStaffId() {
		return staffid;
	}
	public void setStaffId(String staffid) {
		this.staffid = staffid;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getPasswordAgain() {
		return passwordAgain;
	}
	public void setPasswordAgain(String passwordAgain) {
		this.passwordAgain = passwordAgain;
	}
	public String getFirstname() {
		return firstname;
	}
	public void setFirstname(String firstname) {
		this.firstname = firstname;
	}
	public String getLastname() {
		return lastname;
	}
	public void setLastname(String lastname) {
		this.lastname = lastname;
	}
	public String getApi_key() {
		return api_key;
	}
	public void setApi_key(String api_key) {
		this.api_key = api_key;
	}
	public String getDisplayName() {
		return firstname + " " + lastname;
	}

	public int getPoints() {
		return points;
	}
	public void setPoints(int points) {
		this.points = points;
	}
	public int getBadges() {
		return badges;
	}
	public void setBadges(int badges) {
		this.badges = badges;
	}
	public boolean isScoringEnabled() {
		return scoringEnabled;
	}
	public void setScoringEnabled(boolean scoringEnabled) {
		this.scoringEnabled = scoringEnabled;
	}
	
	public void setPasswordRight(boolean v) {
		this.passwordRight = v;
	}
	public boolean isPasswordRight() {
		return this.passwordRight;
	}

	public String getTeamId() {
		return teamId;
	}
	public void setTeamId(String teamId) {
		this.teamId = teamId;
	}


	public String getModuleId() {
		return moduleId;
	}
	public void setModuleId(String moduleId) {
		this.moduleId = moduleId;
	}

}
