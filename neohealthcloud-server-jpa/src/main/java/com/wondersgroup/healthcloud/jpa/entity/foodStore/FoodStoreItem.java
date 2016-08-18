package com.wondersgroup.healthcloud.jpa.entity.foodStore;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by longshasha on 16/8/10.
 */

@Data
@Entity
@Table(name = "app_food_store_item")
public class FoodStoreItem {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private int id;//

    @ManyToOne
    @JoinColumn(name = "category_id", updatable = false, insertable = false)
    private FoodStoreCategory category;

    @Column(name = "category_id")
    private int categoryId;// 食物分类的id

    private int rank;

    @Column(name = "food_name")
    private String foodName;// 食物名
    private String icon;// 来源

    @Column(name = "use_suggest")
    private String useSuggest;//使用建议(eq:适宜吃,少量吃)

    private String gi;//血糖影响GI

    private String heat;//热量325.0不带单位(eq:325.0kcal/100克)
    private String fat;//血糖影响GI
    private String protein;// 蛋白质(每100克)
    private String carbohydrates;// 碳水化合物(每100克)

    private String nutrition; // 营养价值
    private String recommend;// 抗糖贴士
    private String benefit; // 对并发症的益处

    @Column(name = "is_show")
    private Integer isShow;// 是否有效(1:有效,0:无效)
    private String source;// 上线时间

    @Column(name = "update_time")
    private Date updateTime;// 更新时间

    @Column(name = "create_time")
    private Date createTime;

    public static Map<String, String> foodlevelConf = new HashMap<>();
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
}
