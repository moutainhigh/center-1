package com.cmall.dborm.txmapper.groupcenter;

import com.cmall.dborm.txmodel.groupcenter.GcWithdrawLog;
import com.cmall.dborm.txmodel.groupcenter.GcWithdrawLogExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface GcWithdrawLogMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table gc_withdraw_log
     *
     * @mbggenerated
     */
    int countByExample(GcWithdrawLogExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table gc_withdraw_log
     *
     * @mbggenerated
     */
    int deleteByExample(GcWithdrawLogExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table gc_withdraw_log
     *
     * @mbggenerated
     */
    int deleteByPrimaryKey(Integer zid);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table gc_withdraw_log
     *
     * @mbggenerated
     */
    int insert(GcWithdrawLog record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table gc_withdraw_log
     *
     * @mbggenerated
     */
    int insertSelective(GcWithdrawLog record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table gc_withdraw_log
     *
     * @mbggenerated
     */
    List<GcWithdrawLog> selectByExampleWithBLOBs(GcWithdrawLogExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table gc_withdraw_log
     *
     * @mbggenerated
     */
    List<GcWithdrawLog> selectByExample(GcWithdrawLogExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table gc_withdraw_log
     *
     * @mbggenerated
     */
    GcWithdrawLog selectByPrimaryKey(Integer zid);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table gc_withdraw_log
     *
     * @mbggenerated
     */
    int updateByExampleSelective(@Param("record") GcWithdrawLog record, @Param("example") GcWithdrawLogExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table gc_withdraw_log
     *
     * @mbggenerated
     */
    int updateByExampleWithBLOBs(@Param("record") GcWithdrawLog record, @Param("example") GcWithdrawLogExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table gc_withdraw_log
     *
     * @mbggenerated
     */
    int updateByExample(@Param("record") GcWithdrawLog record, @Param("example") GcWithdrawLogExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table gc_withdraw_log
     *
     * @mbggenerated
     */
    int updateByPrimaryKeySelective(GcWithdrawLog record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table gc_withdraw_log
     *
     * @mbggenerated
     */
    int updateByPrimaryKeyWithBLOBs(GcWithdrawLog record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table gc_withdraw_log
     *
     * @mbggenerated
     */
    int updateByPrimaryKey(GcWithdrawLog record);
}