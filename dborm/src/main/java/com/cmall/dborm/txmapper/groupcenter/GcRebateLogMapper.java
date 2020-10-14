package com.cmall.dborm.txmapper.groupcenter;

import com.cmall.dborm.txmodel.groupcenter.GcRebateLog;
import com.cmall.dborm.txmodel.groupcenter.GcRebateLogExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface GcRebateLogMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table gc_rebate_log
     *
     * @mbggenerated
     */
    int countByExample(GcRebateLogExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table gc_rebate_log
     *
     * @mbggenerated
     */
    int deleteByExample(GcRebateLogExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table gc_rebate_log
     *
     * @mbggenerated
     */
    int deleteByPrimaryKey(Integer zid);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table gc_rebate_log
     *
     * @mbggenerated
     */
    int insert(GcRebateLog record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table gc_rebate_log
     *
     * @mbggenerated
     */
    int insertSelective(GcRebateLog record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table gc_rebate_log
     *
     * @mbggenerated
     */
    List<GcRebateLog> selectByExampleWithBLOBs(GcRebateLogExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table gc_rebate_log
     *
     * @mbggenerated
     */
    List<GcRebateLog> selectByExample(GcRebateLogExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table gc_rebate_log
     *
     * @mbggenerated
     */
    GcRebateLog selectByPrimaryKey(Integer zid);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table gc_rebate_log
     *
     * @mbggenerated
     */
    int updateByExampleSelective(@Param("record") GcRebateLog record, @Param("example") GcRebateLogExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table gc_rebate_log
     *
     * @mbggenerated
     */
    int updateByExampleWithBLOBs(@Param("record") GcRebateLog record, @Param("example") GcRebateLogExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table gc_rebate_log
     *
     * @mbggenerated
     */
    int updateByExample(@Param("record") GcRebateLog record, @Param("example") GcRebateLogExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table gc_rebate_log
     *
     * @mbggenerated
     */
    int updateByPrimaryKeySelective(GcRebateLog record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table gc_rebate_log
     *
     * @mbggenerated
     */
    int updateByPrimaryKeyWithBLOBs(GcRebateLog record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table gc_rebate_log
     *
     * @mbggenerated
     */
    int updateByPrimaryKey(GcRebateLog record);
}