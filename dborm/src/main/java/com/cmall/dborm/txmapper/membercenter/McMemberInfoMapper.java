package com.cmall.dborm.txmapper.membercenter;

import com.cmall.dborm.txmodel.membercenter.McMemberInfo;
import com.cmall.dborm.txmodel.membercenter.McMemberInfoExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface McMemberInfoMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table mc_member_info
     *
     * @mbggenerated
     */
    int countByExample(McMemberInfoExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table mc_member_info
     *
     * @mbggenerated
     */
    int deleteByExample(McMemberInfoExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table mc_member_info
     *
     * @mbggenerated
     */
    int deleteByPrimaryKey(Integer zid);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table mc_member_info
     *
     * @mbggenerated
     */
    int insert(McMemberInfo record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table mc_member_info
     *
     * @mbggenerated
     */
    int insertSelective(McMemberInfo record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table mc_member_info
     *
     * @mbggenerated
     */
    List<McMemberInfo> selectByExample(McMemberInfoExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table mc_member_info
     *
     * @mbggenerated
     */
    McMemberInfo selectByPrimaryKey(Integer zid);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table mc_member_info
     *
     * @mbggenerated
     */
    int updateByExampleSelective(@Param("record") McMemberInfo record, @Param("example") McMemberInfoExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table mc_member_info
     *
     * @mbggenerated
     */
    int updateByExample(@Param("record") McMemberInfo record, @Param("example") McMemberInfoExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table mc_member_info
     *
     * @mbggenerated
     */
    int updateByPrimaryKeySelective(McMemberInfo record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table mc_member_info
     *
     * @mbggenerated
     */
    int updateByPrimaryKey(McMemberInfo record);
}