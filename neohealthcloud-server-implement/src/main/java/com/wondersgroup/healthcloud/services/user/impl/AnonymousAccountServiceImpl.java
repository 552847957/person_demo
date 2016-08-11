package com.wondersgroup.healthcloud.services.user.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.wondersgroup.healthcloud.jpa.entity.user.AnonymousAccount;
import com.wondersgroup.healthcloud.jpa.repository.user.AnonymousAccountRepository;
import com.wondersgroup.healthcloud.services.user.AnonymousAccountService;
import com.wondersgroup.healthcloud.services.user.exception.ErrorChangeMobileException;

@Service
public class AnonymousAccountServiceImpl implements AnonymousAccountService{
    
    @Autowired
    private AnonymousAccountRepository anonymousAccountRepository;
    
    public AnonymousAccount getAnonymousAccount(String userId, Boolean nullable) {
        AnonymousAccount account = anonymousAccountRepository.findOne(userId);
        if (account != null || nullable) {
            return account;
        } else {
            throw new ErrorChangeMobileException(1001, "不存在的用户");
        }
    }
}
