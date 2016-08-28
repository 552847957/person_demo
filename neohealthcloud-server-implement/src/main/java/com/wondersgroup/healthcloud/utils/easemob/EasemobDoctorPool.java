package com.wondersgroup.healthcloud.utils.easemob;

import com.wondersgroup.healthcloud.common.utils.IdGen;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * ░░░░░▄█▌▀▄▓▓▄▄▄▄▀▀▀▄▓▓▓▓▓▌█
 * ░░░▄█▀▀▄▓█▓▓▓▓▓▓▓▓▓▓▓▓▀░▓▌█
 * ░░█▀▄▓▓▓███▓▓▓███▓▓▓▄░░▄▓▐█▌
 * ░█▌▓▓▓▀▀▓▓▓▓███▓▓▓▓▓▓▓▄▀▓▓▐█
 * ▐█▐██▐░▄▓▓▓▓▓▀▄░▀▓▓▓▓▓▓▓▓▓▌█▌
 * █▌███▓▓▓▓▓▓▓▓▐░░▄▓▓███▓▓▓▄▀▐█
 * █▐█▓▀░░▀▓▓▓▓▓▓▓▓▓██████▓▓▓▓▐█
 * ▌▓▄▌▀░▀░▐▀█▄▓▓██████████▓▓▓▌█▌
 * ▌▓▓▓▄▄▀▀▓▓▓▀▓▓▓▓▓▓▓▓█▓█▓█▓▓▌█▌
 * █▐▓▓▓▓▓▓▄▄▄▓▓▓▓▓▓█▓█▓█▓█▓▓▓▐█
 *
 * Created by zhangzhixiu on 8/2/16.
 */
@Component
public class EasemobDoctorPool {

  @Autowired
  private EasemobAccountUtil util;

  public EasemobAccount fetchOne() {
    String id = "d" + IdGen.uuid();//easemob doctor account id has 33 length and start with character 'd'
    String pwd = IdGen.uuid();
    Boolean result = util.register(id, pwd, id);
    if (result) {
      return new EasemobAccount(id, pwd);
    } else {
      return null;
    }
  }

  public EasemobAccount fetchOneUser() {
    String id = IdGen.uuid();
    String pwd = IdGen.uuid();
    Boolean result = util.register(id, pwd, id);
    if (result) {
      return new EasemobAccount(id, pwd);
    } else {
      return null;
    }
  }
}
