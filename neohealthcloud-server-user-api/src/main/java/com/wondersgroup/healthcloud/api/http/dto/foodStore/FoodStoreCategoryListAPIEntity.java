package com.wondersgroup.healthcloud.api.http.dto.foodStore;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.google.gson.Gson;
import com.wondersgroup.healthcloud.jpa.entity.foodStore.FoodStoreCategory;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

/**
 * Created by yanshuai on 15/6/26.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FoodStoreCategoryListAPIEntity {

    private String id;
    private String category_name;
    private String icon;
    private List<String> tags;

    public FoodStoreCategoryListAPIEntity() {
    }

    public FoodStoreCategoryListAPIEntity(FoodStoreCategory foodStoreCategory) {
        this.id = String.valueOf(foodStoreCategory.getId());
        this.category_name = foodStoreCategory.getCategoryName();
        this.icon = foodStoreCategory.getIcon();
        String tagJson = foodStoreCategory.getTags();
        if (StringUtils.isNotEmpty(tagJson)){
            Gson gson = new Gson();
            this.tags = gson.fromJson(tagJson, List.class);
        }
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCategory_name() {
        return category_name;
    }

    public void setCategory_name(String category_name) {
        this.category_name = category_name;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }
}
