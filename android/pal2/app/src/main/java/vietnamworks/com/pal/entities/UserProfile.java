package vietnamworks.com.pal.entities;

import java.util.HashMap;

import vietnamworks.com.pal.services.FirebaseService;

/**
 * Created by duynk on 12/1/15.
 */
public class UserProfile extends BaseEntity {
    String avatar;
    String displayName;
    String email;

    String firstName;
    String lastName;
    String city;
    String address;
    String school;
    String jobTitle;

    int exp;
    String level;
    int levelCompletion;
    String levelName;

    int score1 = 0;
    int score2 = 0;
    int score3 = 0;
    int score4 = 0;
    int score5 = 0;
    public float getScore() {
        int total = score1 + score2 + score3 + score4 + score5;
        float score = 0;
        if (total > 0) {
            score = Math.round(((score1 + score2 * 2 + score3 * 3 + score4 * 4 + score5 * 5) * 10.0f) / total) / 10f;
        }
        return score;
    }

    int totalPosts;

    int totalSessions;

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getSchool() {
        return school;
    }

    public void setSchool(String school) {
        this.school = school;
    }

    public int getExp() {
        return exp;
    }

    public void setExp(int exp) {
        this.exp = exp;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public int getLevelCompletion() {
        return levelCompletion;
    }

    public void setLevelCompletion(int levelCompletion) {
        this.levelCompletion = levelCompletion;
    }

    public String getLevelName() {
        return levelName;
    }

    public void setLevelName(String levelName) {
        this.levelName = levelName;
    }

    public int getTotalPosts() {
        return totalPosts;
    }

    public void setTotalPosts(int totalPosts) {
        this.totalPosts = totalPosts;
    }

    public int getScore1() {
        return score1;
    }

    public void setScore1(int score1) {
        this.score1 = score1;
    }

    public int getScore2() {
        return score2;
    }

    public void setScore2(int score2) {
        this.score2 = score2;
    }

    public int getScore3() {
        return score3;
    }

    public void setScore3(int score3) {
        this.score3 = score3;
    }

    public int getScore4() {
        return score4;
    }

    public void setScore4(int score4) {
        this.score4 = score4;
    }

    public int getScore5() {
        return score5;
    }

    public void setScore5(int score5) {
        this.score5 = score5;
    }

    public String getJobTitle() {
        return jobTitle;
    }

    public void setJobTitle(String jobTitle) {
        this.jobTitle = jobTitle;
    }

    public int getTotalSessions() {
        return totalSessions;
    }

    public void setTotalSessions(int totalSessions) {
        this.totalSessions = totalSessions;
    }

    @Override
    public UserProfile importData(HashMap<String, Object> obj) {
        super.importData(obj);

        score1 = safeGetInt(obj, "score_1", 0);
        score2 = safeGetInt(obj, "score_2", 0);
        score3 = safeGetInt(obj, "score_3", 0);
        score4 = safeGetInt(obj, "score_4", 0);
        score5 = safeGetInt(obj, "score_5", 0);

        avatar = safeGetString(obj, "avatar", "");
        displayName = safeGetString(obj, "display_name", "");
        email = safeGetString(obj, "email", "");
        firstName = safeGetString(obj, "first_name", "");
        lastName = safeGetString(obj, "last_name", "");
        city = safeGetString(obj, "city");
        address = safeGetString(obj, "address", "");
        school = safeGetString(obj, "school");
        jobTitle = safeGetString(obj, "job_title", "");

        exp = safeGetInt(obj, "exp", 0);
        level = safeGetString(obj, "level");
        levelCompletion = safeGetInt(obj, "level_completion", 0);
        levelName = safeGetString(obj, "level_name", "Beginner");

        totalPosts = safeGetInt(obj, "total_posts", 0);

        totalSessions = safeGetInt(obj, "total_sessions", 0);

        return this;
    }

    public static UserProfile getCurrentUserProfile() {
        return new UserProfile().importData(FirebaseService.getPublicProfile());
    }

}
