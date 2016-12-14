package com.wondersgroup.healthcloud.services.bbs;


/**
 * </p>
 * Created by ys on 2016-12-09.
 */
public interface BadWordsService {

    String getBadWords();

    void setBadWords(String badWords);

    String dealBadWords(String text);

    Boolean isDealBadWords();
}
