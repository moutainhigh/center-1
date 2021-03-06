package com.cmall.dborm.txmapper;

import com.cmall.dborm.txmodel.NcFreetryoutApply;
import com.cmall.dborm.txmodel.NcFreetryoutApplyExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface NcFreetryoutApplyMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table nc_freetryout_apply
     *
     * @mbggenerated
     */
    int countByExample(NcFreetryoutApplyExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table nc_freetryout_apply
     *
     * @mbggenerated
     */
    int deleteByExample(NcFreetryoutApplyExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table nc_freetryout_apply
     *
     * @mbggenerated
     */
    int deleteByPrimaryKey(Integer zid);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table nc_freetryout_apply
     *
     * @mbggenerated
     */
    int insert(NcFreetryoutApply record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table nc_freetryout_apply
     *
     * @mbggenerated
     */
    int insertSelective(NcFreetryoutApply record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table nc_freetryout_apply
     *
     * @mbggenerated
     */
    List<NcFreetryoutApply> selectByExample(NcFreetryoutApplyExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table nc_freetryout_apply
     *
     * @mbggenerated
     */
    NcFreetryoutApply selectByPrimaryKey(Integer zid);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table nc_freetryout_apply
     *
     * @mbggenerated
     */
    int updateByExampleSelective(@Param("record") NcFreetryoutApply record, @Param("example") NcFreetryoutApplyExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table nc_freetryout_apply
     *
     * @mbggenerated
     */
    int updateByExample(@Param("record") NcFreetryoutApply record, @Param("example") NcFreetryoutApplyExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table nc_freetryout_apply
     *
     * @mbggenerated
     */
    int updateByPrimaryKeySelective(NcFreetryoutApply record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table nc_freetryout_apply
     *
     * @mbggenerated
     */
    int updateByPrimaryKey(NcFreetryoutApply record);
}