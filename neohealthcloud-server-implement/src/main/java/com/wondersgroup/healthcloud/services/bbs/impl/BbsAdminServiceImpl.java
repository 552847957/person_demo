package com.wondersgroup.healthcloud.services.bbs.impl;

import com.wondersgroup.healthcloud.services.bbs.BbsAdminService;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Created by ys on 2016/08/11.
 *
 * @author ys
 */
@Service("bbsAdminService")
public class BbsAdminServiceImpl implements BbsAdminService {


    @Override
    public void cancelBBSAdmin(String mobile) {

    }

    @Override
    public List<String> getAssociationUidsByAdminId(String admin_bindUid) {
        return null;
    }
}
