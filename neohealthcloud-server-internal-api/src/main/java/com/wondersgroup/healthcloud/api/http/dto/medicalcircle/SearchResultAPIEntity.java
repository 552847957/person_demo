package com.wondersgroup.healthcloud.api.http.dto.medicalcircle;

import java.util.List;

/**
 * Created by Yoda on 2015/9/4.
 */
public class SearchResultAPIEntity {
    private List<DocSearchResultAPIEntity> doc_list;
    private List<NoteCaseSearchResultAPIEntity> notecase_list;
    private List<DynamicSearchResultAPIEntity> dynamic_list;
    private Boolean doc_more;
    private Boolean notecase_more;
    private Boolean dynamic_more;

    public List<DocSearchResultAPIEntity> getDoc_list() {
        return doc_list;
    }

    public void setDoc_list(List<DocSearchResultAPIEntity> doc_list) {
        this.doc_list = doc_list;
    }

    public List<NoteCaseSearchResultAPIEntity> getNotecase_list() {
        return notecase_list;
    }

    public void setNotecase_list(List<NoteCaseSearchResultAPIEntity> notecase_list) {
        this.notecase_list = notecase_list;
    }

    public List<DynamicSearchResultAPIEntity> getDynamic_list() {
        return dynamic_list;
    }

    public void setDynamic_list(List<DynamicSearchResultAPIEntity> dynamic_list) {
        this.dynamic_list = dynamic_list;
    }

    public Boolean getDoc_more() {
        return doc_more;
    }

    public void setDoc_more(Boolean doc_more) {
        this.doc_more = doc_more;
    }

    public Boolean getNotecase_more() {
        return notecase_more;
    }

    public void setNotecase_more(Boolean notecase_more) {
        this.notecase_more = notecase_more;
    }

    public Boolean getDynamic_more() {
        return dynamic_more;
    }

    public void setDynamic_more(Boolean dynamic_more) {
        this.dynamic_more = dynamic_more;
    }
}
