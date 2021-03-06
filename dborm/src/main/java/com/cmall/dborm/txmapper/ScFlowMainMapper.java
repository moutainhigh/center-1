package com.cmall.dborm.txmapper;

import com.cmall.dborm.txmodel.ScFlowMain;
import com.cmall.dborm.txmodel.ScFlowMainExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface ScFlowMainMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table sc_flow_main
     *
     * @mbggenerated
     */
    int countByExample(ScFlowMainExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table sc_flow_main
     *
     * @mbggenerated
     */
    int deleteByExample(ScFlowMainExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table sc_flow_main
     *
     * @mbggenerated
     */
    int deleteByPrimaryKey(Integer zid);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table sc_flow_main
     *
     * @mbggenerated
     */
    int insert(ScFlowMain record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table sc_flow_main
     *
     * @mbggenerated
     */
    int insertSelective(ScFlowMain record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table sc_flow_main
     *
     * @mbggenerated
     */
    List<ScFlowMain> selectByExample(ScFlowMainExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table sc_flow_main
     *
     * @mbggenerated
     */
    ScFlowMain selectByPrimaryKey(Integer zid);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table sc_flow_main
     *
     * @mbggenerated
     */
    int updateByExampleSelective(@Param("record") ScFlowMain record, @Param("example") ScFlowMainExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table sc_flow_main
     *
     * @mbggenerated
     */
    int updateByExample(@Param("record") ScFlowMain record, @Param("example") ScFlowMainExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table sc_flow_main
     *
     * @mbggenerated
     */
    int updateByPrimaryKeySelective(ScFlowMain record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table sc_flow_main
     *
     * @mbggenerated
     */
    int updateByPrimaryKey(ScFlowMain record);
}