package com.wondersgroup.healthcloud.common.utils;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by ys on 16/8/26.
 *
 */
public class ImagesUtils {

    /**
     * 批量图片大小转换 根据手机屏幕
     */
    public static List<String> coverSize(List<String> imgs, Integer screenWidth) {
        if (null == imgs || imgs.size() == 0){
            return null;
        }
        List<String> coverImgs = new ArrayList<>();
        for (String img : imgs){
            coverImgs.add(coverSize(img, screenWidth));
        }
        return coverImgs;
    }

    /**
     * 批量图片大小转换 根据手机屏幕
     */
    public static String[] coverSize(String[] imgs, Integer screenWidth) {
        if (null == imgs || imgs.length == 0){
            return null;
        }
        String[] coverImg = new String[imgs.length];
        int i=0;
        for (String img : imgs){
            coverImg[i] = coverSize(img, screenWidth);
            i++;
        }
        return coverImg;
    }

    /**
     * 图片大小转换 根据手机屏幕
     */
    public static String coverSize(String img, Integer screenWidth) {
        if (StringUtils.isEmpty(img)){
            return img;
        }
        try {
            Matcher m = Pattern.compile("/w/([\\d]+)/h/([\\d]+)").matcher(img);
            if (m.find()){
                int originalW = Integer.valueOf(m.group(1));
                int originalH = Integer.valueOf(m.group(2));
                if (originalW <= screenWidth){
                    return img;
                }
                int coverH = originalH * screenWidth / originalW;
                return img.replace("/w/"+originalW+"/h/"+originalH, "/w/"+screenWidth+"/h/"+coverH);
            }
        }catch (Exception e){

        }
        return img;
    }

    public static void main(String[] args) {
        String img = ImagesUtils.coverSize("http://img.wdjky.com/1482485777693?imageView2/1/w/257/h/255/3", 100);
        System.out.println(img);
    }
}
