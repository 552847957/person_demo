package com.wondersgroup.healthcloud.jpa.enums;

import org.apache.commons.lang3.StringUtils;

/**
 * 云头条类型
 * Created by xianglinhai on 2016/12/9.
 */
public enum VisibleEnum {

     //1 可见 0 不可见
    VISIBLE("1","可见"),
    NOT_VISIBLE("0","不可见");


    private String id;
    private String name;

    VisibleEnum(String id, String name) {
        this.id = id;
        this.name = name;
    }

   public static VisibleEnum getEnumById(String id){
       if(StringUtils.isBlank(id)){
           return null;
       }

       for(VisibleEnum cenum: VisibleEnum.values()){
           if(id.equals(cenum.id)){
               return cenum;
           }
       }

       return null;
   }

   public String getId(){
       return this.id;
   }

    public String getName(){
        return this.name;
    }

}
