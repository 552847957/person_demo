package com.wondersgroup.healthcloud.jpa.repository.imagetext;

import com.wondersgroup.healthcloud.jpa.entity.imagetext.GImageText;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * Created by zhaozhenxing on 2016/8/28.
 */
public interface GImageTextRepository extends JpaRepository<GImageText, String> {

    @Query("select a.version from GImageText a where a.mainArea = ?1 and (a.specArea is null or a.specArea = ?2) and a.gadcode = ?3")
    List<String> findGImageTextVersions(String mainArea, String specArea, Integer gadcode);

    @Query("select a from GImageText a where a.mainArea = ?1 and (a.specArea is null or a.specArea = ?2) and a.gadcode = ?3")
    List<GImageText> findGImageTextList(String mainArea, String specArea, Integer gadcode);

    @Query("select a from GImageText a where a.mainArea = ?1 and (a.specArea is null or a.specArea = ?2) and a.gadcode = ?3 and a.version = ?4")
    GImageText findGImageTextForApp(String mainArea, String specArea, Integer gadcode, String version);
}
