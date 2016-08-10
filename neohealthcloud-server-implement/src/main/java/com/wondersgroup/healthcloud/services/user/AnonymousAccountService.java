package com.wondersgroup.healthcloud.services.user;

import com.wondersgroup.healthcloud.jpa.entity.user.AnonymousAccount;

public interface AnonymousAccountService {
    public AnonymousAccount getAnonymousAccount(String userId, Boolean nullable);

}
