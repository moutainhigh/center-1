package com.cmall.dborm.txmapper;

import com.cmall.dborm.txmodel.PcProductflow;
import com.cmall.dborm.txmodel.PcProductflowExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface PcProductflowMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table pc_productflow
     *
     * @mbggenerated
     */
    int countByExample(PcProductflowExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table pc_productflow
     *
     * @mbggenerated
     */
    int deleteByExample(PcProductflowExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table pc_productflow
     *
     * @mbggenerated
     */
    int deleteByPrimaryKey(Integer zid);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table pc_productflow
     *
     * @mbggenerated
     */
    int insert(PcProductflow record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table pc_productflow
     *
     * @mbggenerated
     */
    int insertSelective(PcProductflow record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table pc_productflow
     *
     * @mbggenerated
     */
    List<PcProductflow> selectByExampleWithBLOBs(PcProductflowExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table pc_productflow
     *
     * @mbggenerated
     */
    List<PcProductflow> selectByExample(PcProductflowExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table pc_productflow
     *
     * @mbggenerated
     */
    PcProductflow selectByPrimaryKey(Integer zid);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table pc_productflow
     *
     * @mbggenerated
     */
    int updateByExampleSelective(@Param("record") PcProductflow record, @Param("example") PcProductflowExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table pc_productflow
     *
     * @mbggenerated
     */
    int updateByExampleWithBLOBs(@Param("record") PcProductflow record, @Param("example") PcProductflowExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table pc_productflow
     *
     * @mbggenerated
     */
    int updateByExample(@Param("record") PcProductflow record, @Param("example") PcProductflowExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table pc_productflow
     *
     * @mbggenerated
     */
    int updateByPrimaryKeySelective(PcProductflow record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table pc_productflow
     *
     * @mbggenerated
     */
    int updateByPrimaryKeyWithBLOBs(PcProductflow record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table pc_productflow
     *
     * @mbggenerated
     */
    int updateByPrimaryKey(PcProductflow record);
}