package com.wondersgroup.healthcloud.api.http.dto.foodStore;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.wondersgroup.healthcloud.jpa.entity.foodStore.FoodStoreItem;

/**
 * Created by yanshuai on 15/6/26.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FoodStoreItemListAPIEntity {

    private String id;
    private String food_name;// 作者
    private String icon;// 来源
    private String kcal;//热量单位(eq:390.0kcal/100克)
    private String use_suggest;//使用建议(eq:适宜吃,少量吃)
    private String url;

    public FoodStoreItemListAPIEntity() {
    }

    public FoodStoreItemListAPIEntity(FoodStoreItem foodStoreItem) {
        this.id = String.valueOf(foodStoreItem.getId());
        this.food_name = foodStoreItem.getFoodName();
        this.icon = foodStoreItem.getIcon();
        this.kcal = foodStoreItem.getHeat() + "kcal/100克";
        this.use_suggest = FoodStoreItem.getFoodUseSuggest(foodStoreItem.getUseSuggest());
//        this.url = AppUrlH5Utils.buildFoodStoreView(foodStoreItem.getId());
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFood_name() {
        return food_name;
    }

    public void setFood_name(String food_name) {
        this.food_name = food_name;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getKcal() {
        return kcal;
    }

    public void setKcal(String kcal) {
        this.kcal = kcal;
    }

    public String getUse_suggest() {
        return use_suggest;
    }

    public void setUse_suggest(String use_suggest) {
        this.use_suggest = use_suggest;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
