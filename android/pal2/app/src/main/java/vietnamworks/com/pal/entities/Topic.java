package vietnamworks.com.pal.entities;

import java.util.HashMap;

/**
 * Created by duynk on 10/1/15.
 */
public class Topic extends BaseEntity {
    private String title;
    private int status;
    private int level;
    private String category;
    long views;
    long submits;

    public String getTitle() {return this.title;}
    public void setTitle(String title) {this.title = title;}

    public int getStatus() {return this.status;}
    public void setStatus(int status) {this.status = status;}

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public long getViews() {
        return views;
    }

    public void setViews(long views) {
        this.views = views;
    }

    public long getSubmits() {
        return submits;
    }

    public void setSubmits(long submits) {
        this.submits = submits;
    }

    @Override
    public Topic importData(HashMap<String, Object> obj) {
        super.importData(obj);

        setLevel(safeGetInt(obj, "level", 0));
        setCategory(safeGetString(obj, "category", ""));
        setStatus(safeGetInt(obj, "status", 0));
        setTitle(safeGetString(obj, "title", ""));
        setViews(safeGetLong(obj, "views", 0));
        setSubmits(safeGetLong(obj, "submits", 0));

        return this;
    }
}
