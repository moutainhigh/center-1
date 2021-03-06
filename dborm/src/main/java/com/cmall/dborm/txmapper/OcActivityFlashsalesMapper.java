package com.cmall.dborm.txmapper;

import com.cmall.dborm.txmodel.OcActivityFlashsales;
import com.cmall.dborm.txmodel.OcActivityFlashsalesExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface OcActivityFlashsalesMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table oc_activity_flashsales
     *
     * @mbggenerated
     */
    int countByExample(OcActivityFlashsalesExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table oc_activity_flashsales
     *
     * @mbggenerated
     */
    int deleteByExample(OcActivityFlashsalesExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table oc_activity_flashsales
     *
     * @mbggenerated
     */
    int deleteByPrimaryKey(Integer zid);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table oc_activity_flashsales
     *
     * @mbggenerated
     */
    int insert(OcActivityFlashsales record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table oc_activity_flashsales
     *
     * @mbggenerated
     */
    int insertSelective(OcActivityFlashsales record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table oc_activity_flashsales
     *
     * @mbggenerated
     */
    List<OcActivityFlashsales> selectByExample(OcActivityFlashsalesExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table oc_activity_flashsales
     *
     * @mbggenerated
     */
    OcActivityFlashsales selectByPrimaryKey(Integer zid);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table oc_activity_flashsales
     *
     * @mbggenerated
     */
    int updateByExampleSelective(@Param("record") OcActivityFlashsales record, @Param("example") OcActivityFlashsalesExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table oc_activity_flashsales
     *
     * @mbggenerated
     */
    int updateByExample(@Param("record") OcActivityFlashsales record, @Param("example") OcActivityFlashsalesExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table oc_activity_flashsales
     *
     * @mbggenerated
     */
    int updateByPrimaryKeySelective(OcActivityFlashsales record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table oc_activity_flashsales
     *
     * @mbggenerated
     */
    int updateByPrimaryKey(OcActivityFlashsales record);
}