package com.wondersgroup.healthcloud.jpa.enums;

/**
 * 云头条类型
 * Created by xianglinhai on 2016/12/9.
 */
public enum CloudTopLineEnum {

     //帖子、资讯、自定义H5
    TIE_ZI(1,"帖子"),
    ZI_XUN(2,"资讯"),
    H5(3,"自定义H5");

    private Integer id;
    private String name;

    CloudTopLineEnum(Integer id,String name) {
        this.id = id;
        this.name = name;
    }

   public static CloudTopLineEnum getNameById(Integer id){
       if(null == id){
           return null;
       }
       for(CloudTopLineEnum cenum: CloudTopLineEnum.values()){
           if(cenum.id.intValue() == id.intValue()){
               return cenum;
           }
       }

       return null;
   }

}
