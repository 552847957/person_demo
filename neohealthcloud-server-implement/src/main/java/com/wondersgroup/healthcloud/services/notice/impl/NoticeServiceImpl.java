package com.wondersgroup.healthcloud.services.notice.impl;

import com.wondersgroup.healthcloud.common.utils.IdGen;
import com.wondersgroup.healthcloud.jpa.entity.notice.Notice;
import com.wondersgroup.healthcloud.jpa.repository.notice.NoticeRepository;
import com.wondersgroup.healthcloud.services.notice.NoticeService;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.sql.DataSource;
import java.util.Date;
import java.util.List;

/**
 * Created by zhaozhenxing on 2016/8/18.
 */
@Service(value = "noticeService")
public class NoticeServiceImpl implements NoticeService {

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(NoticeServiceImpl.class);

    @Autowired
    private DataSource dataSource;

    private JdbcTemplate jt;

    @Autowired
    private NoticeRepository noticeRepository;

    @Override
    public Notice findNoticeByid(String id) {
        return noticeRepository.findNoticeByid(id);
    }

    @Override
    public List<Notice> findAllNoticeByArea(String mainArea, String specArea) {
        try {
            StringBuffer sql = new StringBuffer();
            sql.append("SELECT * FROM app_tb_neonotice WHERE del_flag = '0' ");
            if (!StringUtils.isEmpty(mainArea)) {
                sql.append(" AND main_area = '").append(mainArea).append("'");
            }
            if (!StringUtils.isEmpty(specArea)) {
                sql.append(" AND spec_area = '").append(specArea).append("'");
            }
            sql.append(" ORDER BY update_time DESC");
            List<Notice> notices = getJt().query(sql.toString(), new Object[]{}, new BeanPropertyRowMapper<Notice>(Notice.class));

            if (notices != null && notices.size() > 0) {
                return notices;
            }
        } catch (Exception ex) {
            logger.error("NoticeServiceImpl.findAllNoticeByArea\t-->\t" + ex.getLocalizedMessage());
        }
        return null;
    }

    @Override
    public Notice findNoticeByAreaForApp(String mainArea, String specArea) {
        try {
            StringBuffer sql = new StringBuffer();
            sql.append("SELECT * FROM app_tb_neonotice WHERE del_flag = '0' ");
            if (!StringUtils.isEmpty(mainArea)) {
                sql.append(" AND main_area = '").append(mainArea).append("'");
            }
            if (!StringUtils.isEmpty(specArea)) {
                sql.append(" AND spec_area = '").append(specArea).append("'");
            }
            sql.append(" ORDER BY update_time DESC");
            List<Notice> notices = getJt().query(sql.toString(), new Object[]{}, new BeanPropertyRowMapper<Notice>(Notice.class));

            if (notices != null && notices.size() > 0) {
                return notices.get(0);
            }
        } catch (Exception ex) {
            logger.error("NoticeServiceImpl.findAllNoticeByArea\t-->\t" + ex.getLocalizedMessage());
        }
        return null;
    }

    @Override
    public Notice saveNotice(Notice notice) {
        if (notice.getId() == null) {
            notice.setId(IdGen.uuid());
            notice.setCreateTime(new Date());
            notice.setUpdateTime(new Date());
        }
        return noticeRepository.save(notice);
    }

    /**
     * 获取jdbc template
     *
     * @return
     */
    private JdbcTemplate getJt() {
        if (jt == null) {
            jt = new JdbcTemplate(dataSource);
        }
        return jt;
    }
}
