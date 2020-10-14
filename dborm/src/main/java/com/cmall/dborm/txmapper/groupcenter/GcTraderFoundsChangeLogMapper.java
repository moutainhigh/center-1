package com.cmall.dborm.txmapper.groupcenter;

import com.cmall.dborm.txmodel.groupcenter.GcTraderFoundsChangeLog;
import com.cmall.dborm.txmodel.groupcenter.GcTraderFoundsChangeLogExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface GcTraderFoundsChangeLogMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table gc_trader_founds_change_log
     *
     * @mbggenerated
     */
    int countByExample(GcTraderFoundsChangeLogExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table gc_trader_founds_change_log
     *
     * @mbggenerated
     */
    int deleteByExample(GcTraderFoundsChangeLogExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table gc_trader_founds_change_log
     *
     * @mbggenerated
     */
    int deleteByPrimaryKey(Integer zid);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table gc_trader_founds_change_log
     *
     * @mbggenerated
     */
    int insert(GcTraderFoundsChangeLog record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table gc_trader_founds_change_log
     *
     * @mbggenerated
     */
    int insertSelective(GcTraderFoundsChangeLog record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table gc_trader_founds_change_log
     *
     * @mbggenerated
     */
    List<GcTraderFoundsChangeLog> selectByExample(GcTraderFoundsChangeLogExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table gc_trader_founds_change_log
     *
     * @mbggenerated
     */
    GcTraderFoundsChangeLog selectByPrimaryKey(Integer zid);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table gc_trader_founds_change_log
     *
     * @mbggenerated
     */
    int updateByExampleSelective(@Param("record") GcTraderFoundsChangeLog record, @Param("example") GcTraderFoundsChangeLogExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table gc_trader_founds_change_log
     *
     * @mbggenerated
     */
    int updateByExample(@Param("record") GcTraderFoundsChangeLog record, @Param("example") GcTraderFoundsChangeLogExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table gc_trader_founds_change_log
     *
     * @mbggenerated
     */
    int updateByPrimaryKeySelective(GcTraderFoundsChangeLog record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table gc_trader_founds_change_log
     *
     * @mbggenerated
     */
    int updateByPrimaryKey(GcTraderFoundsChangeLog record);
}