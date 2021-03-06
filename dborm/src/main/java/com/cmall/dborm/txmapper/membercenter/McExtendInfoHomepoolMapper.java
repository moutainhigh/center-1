package com.cmall.dborm.txmapper.membercenter;

import com.cmall.dborm.txmodel.membercenter.McExtendInfoHomepool;
import com.cmall.dborm.txmodel.membercenter.McExtendInfoHomepoolExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface McExtendInfoHomepoolMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table mc_extend_info_homepool
     *
     * @mbggenerated
     */
    int countByExample(McExtendInfoHomepoolExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table mc_extend_info_homepool
     *
     * @mbggenerated
     */
    int deleteByExample(McExtendInfoHomepoolExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table mc_extend_info_homepool
     *
     * @mbggenerated
     */
    int deleteByPrimaryKey(Integer zid);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table mc_extend_info_homepool
     *
     * @mbggenerated
     */
    int insert(McExtendInfoHomepool record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table mc_extend_info_homepool
     *
     * @mbggenerated
     */
    int insertSelective(McExtendInfoHomepool record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table mc_extend_info_homepool
     *
     * @mbggenerated
     */
    List<McExtendInfoHomepool> selectByExample(McExtendInfoHomepoolExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table mc_extend_info_homepool
     *
     * @mbggenerated
     */
    McExtendInfoHomepool selectByPrimaryKey(Integer zid);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table mc_extend_info_homepool
     *
     * @mbggenerated
     */
    int updateByExampleSelective(@Param("record") McExtendInfoHomepool record, @Param("example") McExtendInfoHomepoolExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table mc_extend_info_homepool
     *
     * @mbggenerated
     */
    int updateByExample(@Param("record") McExtendInfoHomepool record, @Param("example") McExtendInfoHomepoolExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table mc_extend_info_homepool
     *
     * @mbggenerated
     */
    int updateByPrimaryKeySelective(McExtendInfoHomepool record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table mc_extend_info_homepool
     *
     * @mbggenerated
     */
    int updateByPrimaryKey(McExtendInfoHomepool record);
}