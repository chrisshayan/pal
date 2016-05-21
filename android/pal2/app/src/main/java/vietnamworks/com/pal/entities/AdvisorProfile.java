package vietnamworks.com.pal.entities;

import java.util.HashMap;

/**
 * Created by duynk on 11/5/15.
 */
public class AdvisorProfile extends BaseEntity {
    String avatar;
    String display_name;
    String email;
    int rate5;
    int rate4;
    int rate3;
    int rate2;
    int rate1;

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getDisplay_name() {
        return display_name;
    }

    public void setDisplay_name(String display_name) {
        this.display_name = display_name;
    }

    public int getRate5() {
        return rate5;
    }

    public void setRate5(int rate5) {
        this.rate5 = rate5;
    }

    public int getRate4() {
        return rate4;
    }

    public void setRate4(int rate4) {
        this.rate4 = rate4;
    }

    public int getRate3() {
        return rate3;
    }

    public void setRate3(int rate3) {
        this.rate3 = rate3;
    }

    public int getRate2() {
        return rate2;
    }

    public void setRate2(int rate2) {
        this.rate2 = rate2;
    }

    public int getRate1() {
        return rate1;
    }

    public void setRate1(int rate1) {
        this.rate1 = rate1;
    }

    public AdvisorProfile importData(HashMap<String, Object> obj) {
        super.importData(obj);
        //core
        avatar = safeGetString(obj, "avatar");
        display_name = safeGetString(obj, "display_name");
        email = safeGetString(obj, "email");
        rate5 = safeGetInt(obj, "rate5", 0);
        rate4 = safeGetInt(obj, "rate4", 0);
        rate3 = safeGetInt(obj, "rate3", 0);
        rate2 = safeGetInt(obj, "rate2", 0);
        rate1 = safeGetInt(obj, "rate1", 0);
        return this;
    }

    public int totalRating() {
        return rate1 + rate2 + rate3 + rate4 + rate5;
    }

    public float avgRate() {
        float total = Math.max(totalRating(), 1)*1.0f;
        int rate = Math.round(((rate1 + rate2*2 + rate3*3 + rate4*4 + rate5*5)*10)/total);
        return rate/10.0f;
    }
}
