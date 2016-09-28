package com.wondersgroup.healthcloud.api.http.controllers.family;

import java.util.Date;

import com.wondersgroup.healthcloud.common.http.dto.JsonResponseEntity;
import com.wondersgroup.healthcloud.common.http.support.misc.JsonKeyReader;
import com.wondersgroup.healthcloud.common.http.support.version.VersionRange;
import com.wondersgroup.healthcloud.helper.family.FamilyMemberRelation;
import com.wondersgroup.healthcloud.jpa.entity.user.AnonymousAccount;
import com.wondersgroup.healthcloud.jpa.entity.user.RegisterInfo;
import com.wondersgroup.healthcloud.jpa.entity.user.member.FamilyMember;
import com.wondersgroup.healthcloud.jpa.repository.user.AnonymousAccountRepository;
import com.wondersgroup.healthcloud.jpa.repository.user.member.FamilyMemberRepository;
import com.wondersgroup.healthcloud.services.user.AnonymousAccountService;
import com.wondersgroup.healthcloud.services.user.UserAccountService;
import com.wondersgroup.healthcloud.services.user.UserService;
import com.wondersgroup.healthcloud.services.user.exception.ErrorChildVerificationException;
import com.wondersgroup.healthcloud.services.user.exception.ErrorIdcardException;
import com.wondersgroup.healthcloud.utils.IdcardUtils;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by longshasha on 16/9/18.
 */
@RestController
@RequestMapping("/api/child")
public class ChildController {

    @Autowired
    private UserAccountService userAccountService;

    @Autowired
    private UserService userService;

    @Autowired
    private AnonymousAccountService anonymousAccountService;
    
    @Autowired
    private AnonymousAccountRepository anonymousAccountRepository;
    
    @Autowired
    private FamilyMemberRepository memberRepository;
    
    /**
     * 提交实名认证信息
     *
     * @return
     */
    @VersionRange
    @PostMapping(path = "/verification/submit")
    public JsonResponseEntity<String> verificationSubmit(@RequestBody String request) {
        JsonKeyReader reader = new JsonKeyReader(request);
        String id = reader.readString("uid", false);//监护人Id
        String childId = reader.readString("childId", false);//儿童匿名Id
        String name = reader.readString("name", false);//儿童的真实姓名
        String idCard = reader.readString("idcard", false);//儿童的身份证号
        String idCardFile = reader.readString("idCardFile", false);//户口本(儿童身份信息页照片)
        String birthCertFile = reader.readString("birthCertFile", false);//出生证明(照片)
        JsonResponseEntity<String> body = new JsonResponseEntity<>();
        name = name.trim();//去除空字符串
        idCard = idCard.trim();
        idCard = StringUtils.upperCase(idCard);
        Boolean isIdCard = IdcardUtils.validateCard(idCard);
        if(!isIdCard){
            throw new ErrorIdcardException();
        }
        if(!IdcardUtils.containsChinese(name)){
            throw new ErrorChildVerificationException("姓名必须是中文");
        }
        int age = IdcardUtils.getAgeByIdCard(idCard);
        if(age>=18){
            throw new ErrorChildVerificationException("年龄大于等于18岁的不能使用儿童实名认证");
        }
        RegisterInfo registerInfo = userService.getOneNotNull(id);
        if(!registerInfo.verified()){
            throw new ErrorChildVerificationException("您还未实名认证,请先去市民云实名认证");
        }else if(!"1".equals(registerInfo.getIdentifytype())){
            throw new ErrorChildVerificationException("您未通过市民云实名认证");
        }
        if(StringUtils.isBlank(registerInfo.getRegmobilephone())){
            throw new ErrorChildVerificationException("您未绑定手机号,请先绑定手机号");
        }
        
        //重新提交改变身份证和名字的情况进行更新
        AnonymousAccount account = anonymousAccountService.getAnonymousAccount(childId, false);
        if(!account.getIsChild()){
            throw new ErrorChildVerificationException("您提交的实名认证信息不是儿童账号");
        }
        if(account != null && (!idCard.equals(account.getIdcard()) || !name.equals(account.getName()))){
            account.setIdcard(idCard);
            account.setName(name);
            account.setUpdateDate(new Date());
            anonymousAccountRepository.saveAndFlush(account);
            
            FamilyMember menber = memberRepository.findRelationWithOrder(id, childId);
            if(menber != null){
                String gender = IdcardUtils.getGenderByIdCard(idCard);
                menber.setRelation("1".equals(gender) ? "4" : "5");
                menber.setRelationName("1".equals(gender) ? "儿子" : "女儿");
                menber.setMemo(FamilyMemberRelation.isOther(menber.getRelation()) ? menber.getRelationName() : null);
                menber.setUpdateDate(new Date());
                memberRepository.saveAndFlush(menber);
            }
        }
        userAccountService.childVerificationSubmit(id, childId,name, idCard, idCardFile, birthCertFile);
        body.setMsg("提交成功");
        return body;
    }


}
