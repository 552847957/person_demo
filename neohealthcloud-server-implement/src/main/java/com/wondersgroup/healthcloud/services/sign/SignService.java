package com.wondersgroup.healthcloud.services.sign;

import java.util.List;

/**
 * Created by ZZX on 2017/6/9.
 */
public interface SignService {
    List userLists(String name, int pageNo, int pageSize);
}
