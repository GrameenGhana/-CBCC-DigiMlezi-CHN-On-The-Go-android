/* 
 * This file is part of OppiaMobile - https://digital-campus.org/
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

package org.digitalcampus.oppia.model;




public class CbccUser {

	private long userId;
	private String username;
	private String email;
	private String password;
	private String passwordAgain;
	private String firstname;
	private String lastname;
	private String apiKey;
	private String jobTitle;
	private String organisation;
	private String phoneNo;
	private String passwordEncypted;
	private boolean scoringEnabled = true;
	private boolean badgingEnabled = true;
	private int points = 0;
	private int badges = 0;
	
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
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
	public String getApiKey() {
		return apiKey;
	}
	public void setApiKey(String apiKey) {
		this.apiKey = apiKey;
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
	
	public boolean isBadgingEnabled() {
		return badgingEnabled;
	}
	public void setBadgingEnabled(boolean badgingEnabled) {
		this.badgingEnabled = badgingEnabled;
	}
	
	public String getPasswordEncrypted() {
		if (this.passwordEncypted == null){
            //this.passwordEncypted = DigestUtils.sha256Hex(this.password);
        }
		return this.passwordEncypted;
	}
	
	public void setPasswordEncrypted(String pwEncrypted){
		this.passwordEncypted = pwEncrypted;
	}
	
	public long getUserId() {
		return userId;
	}
	public void setUserId(long userId) {
		this.userId = userId;
	}
	public String getJobTitle() {
		return jobTitle;
	}
	public void setJobTitle(String jobTitle) {
		this.jobTitle = jobTitle;
	}
	public String getOrganisation() {
		return organisation;
	}
	public void setOrganisation(String organisation) {
		this.organisation = organisation;
	}
	public String getPhoneNo() {
		return phoneNo;
	}
	public void setPhoneNo(String phoneNo) {
		this.phoneNo = phoneNo;
	}

}
