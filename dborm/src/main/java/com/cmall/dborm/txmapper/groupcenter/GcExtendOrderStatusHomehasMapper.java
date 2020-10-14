package com.cmall.dborm.txmapper.groupcenter;

import com.cmall.dborm.txmodel.groupcenter.GcExtendOrderStatusHomehas;
import com.cmall.dborm.txmodel.groupcenter.GcExtendOrderStatusHomehasExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface GcExtendOrderStatusHomehasMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table gc_extend_order_status_homehas
     *
     * @mbggenerated
     */
    int countByExample(GcExtendOrderStatusHomehasExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table gc_extend_order_status_homehas
     *
     * @mbggenerated
     */
    int deleteByExample(GcExtendOrderStatusHomehasExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table gc_extend_order_status_homehas
     *
     * @mbggenerated
     */
    int deleteByPrimaryKey(Integer zid);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table gc_extend_order_status_homehas
     *
     * @mbggenerated
     */
    int insert(GcExtendOrderStatusHomehas record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table gc_extend_order_status_homehas
     *
     * @mbggenerated
     */
    int insertSelective(GcExtendOrderStatusHomehas record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table gc_extend_order_status_homehas
     *
     * @mbggenerated
     */
    List<GcExtendOrderStatusHomehas> selectByExample(GcExtendOrderStatusHomehasExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table gc_extend_order_status_homehas
     *
     * @mbggenerated
     */
    GcExtendOrderStatusHomehas selectByPrimaryKey(Integer zid);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table gc_extend_order_status_homehas
     *
     * @mbggenerated
     */
    int updateByExampleSelective(@Param("record") GcExtendOrderStatusHomehas record, @Param("example") GcExtendOrderStatusHomehasExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table gc_extend_order_status_homehas
     *
     * @mbggenerated
     */
    int updateByExample(@Param("record") GcExtendOrderStatusHomehas record, @Param("example") GcExtendOrderStatusHomehasExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table gc_extend_order_status_homehas
     *
     * @mbggenerated
     */
    int updateByPrimaryKeySelective(GcExtendOrderStatusHomehas record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table gc_extend_order_status_homehas
     *
     * @mbggenerated
     */
    int updateByPrimaryKey(GcExtendOrderStatusHomehas record);
}