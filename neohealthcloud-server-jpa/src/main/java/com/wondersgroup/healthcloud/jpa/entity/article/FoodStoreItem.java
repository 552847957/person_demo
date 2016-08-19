package com.wondersgroup.healthcloud.jpa.entity.article;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 食物库
 * @author ys
 *
 */
public class FoodStoreItem implements Serializable{

    private int id;// 文章ID
    private int category_id;// 食物分类的id
    private int rank;
    private String food_name;// 食物名
    private String icon;// 来源

    private String use_suggest;//使用建议(eq:适宜吃,少量吃)
    private String gi;//血糖影响GI

    private String heat;//热量325.0不带单位(eq:325.0kcal/100克)
    private String fat;//血糖影响GI
    private String protein;// 蛋白质(每100克)
    private String carbohydrates;// 碳水化合物(每100克)

    private String nutrition; // 营养价值
    private String recommend;// 抗糖贴士
    private String benefit; // 对并发症的益处

    private Integer is_show;// 是否有效(1:有效,0:无效)
    private String source;// 上线时间
    private Date update_time;// 更新时间
    private Date create_time;// 关键字

    public static Map<String, String> foodlevelConf = new HashMap<String, String>();
    static {
        foodlevelConf.put("适宜吃", "1");
        foodlevelConf.put("少量吃", "2");
        foodlevelConf.put("谨慎吃", "3");
    }

    /**
     * 获取转换后的使用建议
     * @param use_suggest
     * @return
     */
    public static String getFoodUseSuggest(String use_suggest){
        String use_suggest_rt = use_suggest;
        Map<String, String> use_suggest_conf = FoodStoreItem.foodlevelConf;
        for (Map.Entry<String, String> entry : use_suggest_conf.entrySet()) {
            if (use_suggest.equals(entry.getValue())){
                use_suggest_rt = entry.getKey();
            }
        }
        return use_suggest_rt;
    }

    /**
     * 获取 血糖影响升糖水平,[高,中,低]
     * @param bloodsugar_gi
     * @return
     */
    public static String getGiLevel(String bloodsugar_gi){
        String giLevel = "-";
        int gi_int=0;
        try {
            gi_int = Integer.parseInt(bloodsugar_gi);
            if (gi_int > 70){
                giLevel = "高";
            }else if (gi_int<55){
                giLevel = "低";
            }else {
                giLevel = "中";
            }
        } catch (NumberFormatException e) {
            try {
                Double gi_double = Double.parseDouble(bloodsugar_gi);
                gi_int = gi_double.intValue();
                if (gi_int > 70){
                    giLevel = "高";
                }else if (gi_int<55){
                    giLevel = "低";
                }else {
                    giLevel = "中";
                }
            }catch (Exception e2){

            }
        }
        return giLevel;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCategory_id() {
        return category_id;
    }

    public void setCategory_id(int category_id) {
        this.category_id = category_id;
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

    public int getRank() {
        return rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }

    public String getUse_suggest() {
        return use_suggest;
    }

    public void setUse_suggest(String use_suggest) {
        this.use_suggest = use_suggest;
    }

    public String getGi() {
        return gi;
    }

    public void setGi(String gi) {
        this.gi = gi;
    }

    public String getHeat() {
        return heat;
    }

    public void setHeat(String heat) {
        this.heat = heat;
    }

    public String getFat() {
        return fat;
    }

    public void setFat(String fat) {
        this.fat = fat;
    }

    public String getProtein() {
        return protein;
    }

    public void setProtein(String protein) {
        this.protein = protein;
    }

    public String getCarbohydrates() {
        return carbohydrates;
    }

    public void setCarbohydrates(String carbohydrates) {
        this.carbohydrates = carbohydrates;
    }

    public String getNutrition() {
        return nutrition;
    }

    public void setNutrition(String nutrition) {
        this.nutrition = nutrition;
    }

    public String getRecommend() {
        return recommend;
    }

    public void setRecommend(String recommend) {
        this.recommend = recommend;
    }

    public String getBenefit() {
        return benefit;
    }

    public void setBenefit(String benefit) {
        this.benefit = benefit;
    }

    public Integer getIs_show() {
        return is_show;
    }

    public void setIs_show(Integer is_show) {
        this.is_show = is_show;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public Date getUpdate_time() {
        return update_time;
    }

    public void setUpdate_time(Date update_time) {
        this.update_time = update_time;
    }

    public Date getCreate_time() {
        return create_time;
    }

    public void setCreate_time(Date create_time) {
        this.create_time = create_time;
    }
}
