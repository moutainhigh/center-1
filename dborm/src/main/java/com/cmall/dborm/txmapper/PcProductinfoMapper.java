package com.cmall.dborm.txmapper;

import com.cmall.dborm.txmodel.PcProductinfo;
import com.cmall.dborm.txmodel.PcProductinfoExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface PcProductinfoMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table pc_productinfo
     *
     * @mbggenerated
     */
    int countByExample(PcProductinfoExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table pc_productinfo
     *
     * @mbggenerated
     */
    int deleteByExample(PcProductinfoExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table pc_productinfo
     *
     * @mbggenerated
     */
    int deleteByPrimaryKey(Integer zid);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table pc_productinfo
     *
     * @mbggenerated
     */
    int insert(PcProductinfo record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table pc_productinfo
     *
     * @mbggenerated
     */
    int insertSelective(PcProductinfo record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table pc_productinfo
     *
     * @mbggenerated
     */
    List<PcProductinfo> selectByExampleWithBLOBs(PcProductinfoExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table pc_productinfo
     *
     * @mbggenerated
     */
    List<PcProductinfo> selectByExample(PcProductinfoExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table pc_productinfo
     *
     * @mbggenerated
     */
    PcProductinfo selectByPrimaryKey(Integer zid);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table pc_productinfo
     *
     * @mbggenerated
     */
    int updateByExampleSelective(@Param("record") PcProductinfo record, @Param("example") PcProductinfoExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table pc_productinfo
     *
     * @mbggenerated
     */
    int updateByExampleWithBLOBs(@Param("record") PcProductinfo record, @Param("example") PcProductinfoExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table pc_productinfo
     *
     * @mbggenerated
     */
    int updateByExample(@Param("record") PcProductinfo record, @Param("example") PcProductinfoExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table pc_productinfo
     *
     * @mbggenerated
     */
    int updateByPrimaryKeySelective(PcProductinfo record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table pc_productinfo
     *
     * @mbggenerated
     */
    int updateByPrimaryKeyWithBLOBs(PcProductinfo record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table pc_productinfo
     *
     * @mbggenerated
     */
    int updateByPrimaryKey(PcProductinfo record);
}