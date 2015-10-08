package vietnamworks.com.pal.components;

/**
 * Created by duynk on 10/6/15.
 */
public interface CustomCardStackViewDelegate {
    public void onLaunched(CustomCardStackView obj);
    public void onChangedActiveItem(int front_index, int back_index, CustomCardStackView obj);
    public void onBeforeChangedActiveItem(int front_index, int back_index, CustomCardStackView obj);
    public int getTotalRecords();
    public void onBeforeSelectItem(int index,CustomCardStackView ccsv);
    public void onSelectItem(int index,CustomCardStackView ccsv);
}
