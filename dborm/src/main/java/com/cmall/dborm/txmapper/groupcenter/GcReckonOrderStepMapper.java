package com.cmall.dborm.txmapper.groupcenter;

import com.cmall.dborm.txmodel.groupcenter.GcReckonOrderStep;
import com.cmall.dborm.txmodel.groupcenter.GcReckonOrderStepExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface GcReckonOrderStepMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table gc_reckon_order_step
     *
     * @mbggenerated
     */
    int countByExample(GcReckonOrderStepExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table gc_reckon_order_step
     *
     * @mbggenerated
     */
    int deleteByExample(GcReckonOrderStepExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table gc_reckon_order_step
     *
     * @mbggenerated
     */
    int deleteByPrimaryKey(Integer zid);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table gc_reckon_order_step
     *
     * @mbggenerated
     */
    int insert(GcReckonOrderStep record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table gc_reckon_order_step
     *
     * @mbggenerated
     */
    int insertSelective(GcReckonOrderStep record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table gc_reckon_order_step
     *
     * @mbggenerated
     */
    List<GcReckonOrderStep> selectByExampleWithBLOBs(GcReckonOrderStepExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table gc_reckon_order_step
     *
     * @mbggenerated
     */
    List<GcReckonOrderStep> selectByExample(GcReckonOrderStepExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table gc_reckon_order_step
     *
     * @mbggenerated
     */
    GcReckonOrderStep selectByPrimaryKey(Integer zid);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table gc_reckon_order_step
     *
     * @mbggenerated
     */
    int updateByExampleSelective(@Param("record") GcReckonOrderStep record, @Param("example") GcReckonOrderStepExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table gc_reckon_order_step
     *
     * @mbggenerated
     */
    int updateByExampleWithBLOBs(@Param("record") GcReckonOrderStep record, @Param("example") GcReckonOrderStepExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table gc_reckon_order_step
     *
     * @mbggenerated
     */
    int updateByExample(@Param("record") GcReckonOrderStep record, @Param("example") GcReckonOrderStepExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table gc_reckon_order_step
     *
     * @mbggenerated
     */
    int updateByPrimaryKeySelective(GcReckonOrderStep record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table gc_reckon_order_step
     *
     * @mbggenerated
     */
    int updateByPrimaryKeyWithBLOBs(GcReckonOrderStep record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table gc_reckon_order_step
     *
     * @mbggenerated
     */
    int updateByPrimaryKey(GcReckonOrderStep record);
}