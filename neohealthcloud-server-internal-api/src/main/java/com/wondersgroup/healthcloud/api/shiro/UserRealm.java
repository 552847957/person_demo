package com.wondersgroup.healthcloud.api.shiro;

import com.google.common.collect.Maps;
import com.wondersgroup.healthcloud.api.helper.PropertiesUtil;
import com.wondersgroup.healthcloud.api.helper.UserHelper;
import com.wondersgroup.healthcloud.jpa.entity.permission.User;
import com.wondersgroup.healthcloud.jpa.repository.permission.UserRepository;
import com.wondersgroup.healthcloud.services.permission.PermissionService;
import org.apache.shiro.authc.*;
import org.apache.shiro.authc.credential.HashedCredentialsMatcher;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2015/11/23.
 */
@Component("userRealm")
public class UserRealm extends AuthorizingRealm {
    @Autowired
    private UserRepository userRepo;

    @Autowired
    private PermissionService permissionService;

    @Autowired
    private UserHelper userHelper;

    @Autowired
    private PropertiesUtil propertiesUtil;

    private static Map<String,Integer> retryMap = Maps.newHashMap();
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        User user = userHelper.getCurrentUser();
        SimpleAuthorizationInfo authorizationInfo = new SimpleAuthorizationInfo();

        List<Map<String,Object>> roleList = permissionService.getRoleEnnameByUser(user.getUserId(), user.getLoginname().equals(propertiesUtil.getAccount()));
        for(Map<String,Object> map :roleList) {
            if(StringUtils.isEmpty(map.get("enname"))) continue;
            authorizationInfo.addRole(String.valueOf(map.get("enname")));
        }

        List<Map<String,Object>> menuList = permissionService.getMenuPermissionByUser(user.getUserId(),user.getLoginname().equals(propertiesUtil.getAccount()));
        for(Map<String,Object> map : menuList){
            if(StringUtils.isEmpty(map.get("permission"))) continue;
            authorizationInfo.addStringPermission(map.get("permission").toString());
        }
        return authorizationInfo;
    }

    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
        String username = (String)token.getPrincipal();
        User user = userRepo.findByLoginName(username);
        if(user == null) {
            throw new UnknownAccountException();
        }
        if(user.getLocked().equals("1")){
            throw new DisabledAccountException();
        }
        SimpleAuthenticationInfo authenticationInfo = new SimpleAuthenticationInfo(
                user.getLoginname(),
                user.getPassword(),
                getName()  //realm name
        );


        return authenticationInfo;
    }

    @PostConstruct
    public void initCredentialsMatcher() {
        /*HashedCredentialsMatcher matcher = new HashedCredentialsMatcher();
        matcher.setHashIterations(1);
        matcher.setStoredCredentialsHexEncoded(true);
        matcher.setHashAlgorithmName("md5");*/
        HashedCredentialsMatcher matcher = new RetryLimitHashedCredentialsMatcher();
        setCredentialsMatcher(matcher);
    }

    public class RetryLimitHashedCredentialsMatcher extends HashedCredentialsMatcher {
        public RetryLimitHashedCredentialsMatcher(){
            this.setHashIterations(1);
            this.setStoredCredentialsHexEncoded(true);
            this.setHashAlgorithmName("md5");
        }

        @Override
        public boolean doCredentialsMatch(AuthenticationToken token, AuthenticationInfo info) {
            String username = (String)token.getPrincipal();
            System.err.println(retryMap);
            if(!retryMap.containsKey(username)) {
                retryMap.put(username,0);
            }
            int retryCount = retryMap.get(username);
            boolean matches = super.doCredentialsMatch(token, info);
            if(matches) {
                //clear retry count
                retryMap.remove(username);
            }else{
                retryCount++;
                retryMap.put(username,retryCount);
                if(retryCount >= 3) {
                    throw new ExcessiveAttemptsException();
                }
            }
            return matches;
        }
    }

}
