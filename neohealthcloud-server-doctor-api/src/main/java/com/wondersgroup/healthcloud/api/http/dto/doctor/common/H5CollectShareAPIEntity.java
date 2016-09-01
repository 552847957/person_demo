package com.wondersgroup.healthcloud.api.http.dto.doctor.common;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class H5CollectShareAPIEntity {

    private Boolean is_collect;
    private Boolean can_collect;
    private ShareH5APIEntity share;

    public Boolean getIs_collect() {
        return is_collect;
    }

    public void setIs_collect(Boolean is_collect) {
        this.is_collect = is_collect;
    }

    public ShareH5APIEntity getShare() {
        return share;
    }

    public void setShare(ShareH5APIEntity share) {
        this.share = share;
    }

    public Boolean getCan_collect() {
        return can_collect;
    }

    public void setCan_collect(Boolean can_collect) {
        this.can_collect = can_collect;
    }
}
