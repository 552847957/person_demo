package com.wondersgroup.healthcloud.services.localspread;

import com.wondersgroup.healthcloud.jpa.entity.spread.Evangelist;

import java.util.List;

/**
 * Created by zhaozhenxing on 2017/1/9.
 */
public interface LocalSpreadService {
    List<Evangelist> list(Evangelist evangelist);
    Evangelist saveAndUpdate(Evangelist evangelist);
}
