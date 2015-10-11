package vietnamworks.com.pal.components;

/**
 * Created by duynk on 10/6/15.
 */
public interface CustomCardStackViewDelegate {
    void onLaunched(CustomCardStackView obj);
    void onChangedActiveItem(int front_index, int mid_index, int back_index, CustomCardStackView obj);
    void onBeforeChangedActiveItem(int front_index, int mid_index, int back_index, CustomCardStackView obj);
    int getTotalRecords();
    void onBeforeSelectItem(int index,CustomCardStackView ccsv);
    void onSelectItem(int index,CustomCardStackView ccsv);
    void onDeselectItem(int index, CustomCardStackView ccsv);
}
