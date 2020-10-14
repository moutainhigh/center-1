package com.cmall.dborm.txmodel;

import java.util.ArrayList;
import java.util.List;

public class PcQualificationInfoExample {
    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database table pc_qualification_info
     *
     * @mbggenerated
     */
    protected String orderByClause;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database table pc_qualification_info
     *
     * @mbggenerated
     */
    protected boolean distinct;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database table pc_qualification_info
     *
     * @mbggenerated
     */
    protected List<Criteria> oredCriteria;

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table pc_qualification_info
     *
     * @mbggenerated
     */
    public PcQualificationInfoExample() {
        oredCriteria = new ArrayList<Criteria>();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table pc_qualification_info
     *
     * @mbggenerated
     */
    public void setOrderByClause(String orderByClause) {
        this.orderByClause = orderByClause;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table pc_qualification_info
     *
     * @mbggenerated
     */
    public String getOrderByClause() {
        return orderByClause;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table pc_qualification_info
     *
     * @mbggenerated
     */
    public void setDistinct(boolean distinct) {
        this.distinct = distinct;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table pc_qualification_info
     *
     * @mbggenerated
     */
    public boolean isDistinct() {
        return distinct;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table pc_qualification_info
     *
     * @mbggenerated
     */
    public List<Criteria> getOredCriteria() {
        return oredCriteria;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table pc_qualification_info
     *
     * @mbggenerated
     */
    public void or(Criteria criteria) {
        oredCriteria.add(criteria);
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table pc_qualification_info
     *
     * @mbggenerated
     */
    public Criteria or() {
        Criteria criteria = createCriteriaInternal();
        oredCriteria.add(criteria);
        return criteria;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table pc_qualification_info
     *
     * @mbggenerated
     */
    public Criteria createCriteria() {
        Criteria criteria = createCriteriaInternal();
        if (oredCriteria.size() == 0) {
            oredCriteria.add(criteria);
        }
        return criteria;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table pc_qualification_info
     *
     * @mbggenerated
     */
    protected Criteria createCriteriaInternal() {
        Criteria criteria = new Criteria();
        return criteria;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table pc_qualification_info
     *
     * @mbggenerated
     */
    public void clear() {
        oredCriteria.clear();
        orderByClause = null;
        distinct = false;
    }

    /**
     * This class was generated by MyBatis Generator.
     * This class corresponds to the database table pc_qualification_info
     *
     * @mbggenerated
     */
    protected abstract static class GeneratedCriteria {
        protected List<Criterion> criteria;

        protected GeneratedCriteria() {
            super();
            criteria = new ArrayList<Criterion>();
        }

        public boolean isValid() {
            return criteria.size() > 0;
        }

        public List<Criterion> getAllCriteria() {
            return criteria;
        }

        public List<Criterion> getCriteria() {
            return criteria;
        }

        protected void addCriterion(String condition) {
            if (condition == null) {
                throw new RuntimeException("Value for condition cannot be null");
            }
            criteria.add(new Criterion(condition));
        }

        protected void addCriterion(String condition, Object value, String property) {
            if (value == null) {
                throw new RuntimeException("Value for " + property + " cannot be null");
            }
            criteria.add(new Criterion(condition, value));
        }

        protected void addCriterion(String condition, Object value1, Object value2, String property) {
            if (value1 == null || value2 == null) {
                throw new RuntimeException("Between values for " + property + " cannot be null");
            }
            criteria.add(new Criterion(condition, value1, value2));
        }

        public Criteria andZidIsNull() {
            addCriterion("zid is null");
            return (Criteria) this;
        }

        public Criteria andZidIsNotNull() {
            addCriterion("zid is not null");
            return (Criteria) this;
        }

        public Criteria andZidEqualTo(Integer value) {
            addCriterion("zid =", value, "zid");
            return (Criteria) this;
        }

        public Criteria andZidNotEqualTo(Integer value) {
            addCriterion("zid <>", value, "zid");
            return (Criteria) this;
        }

        public Criteria andZidGreaterThan(Integer value) {
            addCriterion("zid >", value, "zid");
            return (Criteria) this;
        }

        public Criteria andZidGreaterThanOrEqualTo(Integer value) {
            addCriterion("zid >=", value, "zid");
            return (Criteria) this;
        }

        public Criteria andZidLessThan(Integer value) {
            addCriterion("zid <", value, "zid");
            return (Criteria) this;
        }

        public Criteria andZidLessThanOrEqualTo(Integer value) {
            addCriterion("zid <=", value, "zid");
            return (Criteria) this;
        }

        public Criteria andZidIn(List<Integer> values) {
            addCriterion("zid in", values, "zid");
            return (Criteria) this;
        }

        public Criteria andZidNotIn(List<Integer> values) {
            addCriterion("zid not in", values, "zid");
            return (Criteria) this;
        }

        public Criteria andZidBetween(Integer value1, Integer value2) {
            addCriterion("zid between", value1, value2, "zid");
            return (Criteria) this;
        }

        public Criteria andZidNotBetween(Integer value1, Integer value2) {
            addCriterion("zid not between", value1, value2, "zid");
            return (Criteria) this;
        }

        public Criteria andUidIsNull() {
            addCriterion("uid is null");
            return (Criteria) this;
        }

        public Criteria andUidIsNotNull() {
            addCriterion("uid is not null");
            return (Criteria) this;
        }

        public Criteria andUidEqualTo(String value) {
            addCriterion("uid =", value, "uid");
            return (Criteria) this;
        }

        public Criteria andUidNotEqualTo(String value) {
            addCriterion("uid <>", value, "uid");
            return (Criteria) this;
        }

        public Criteria andUidGreaterThan(String value) {
            addCriterion("uid >", value, "uid");
            return (Criteria) this;
        }

        public Criteria andUidGreaterThanOrEqualTo(String value) {
            addCriterion("uid >=", value, "uid");
            return (Criteria) this;
        }

        public Criteria andUidLessThan(String value) {
            addCriterion("uid <", value, "uid");
            return (Criteria) this;
        }

        public Criteria andUidLessThanOrEqualTo(String value) {
            addCriterion("uid <=", value, "uid");
            return (Criteria) this;
        }

        public Criteria andUidLike(String value) {
            addCriterion("uid like", value, "uid");
            return (Criteria) this;
        }

        public Criteria andUidNotLike(String value) {
            addCriterion("uid not like", value, "uid");
            return (Criteria) this;
        }

        public Criteria andUidIn(List<String> values) {
            addCriterion("uid in", values, "uid");
            return (Criteria) this;
        }

        public Criteria andUidNotIn(List<String> values) {
            addCriterion("uid not in", values, "uid");
            return (Criteria) this;
        }

        public Criteria andUidBetween(String value1, String value2) {
            addCriterion("uid between", value1, value2, "uid");
            return (Criteria) this;
        }

        public Criteria andUidNotBetween(String value1, String value2) {
            addCriterion("uid not between", value1, value2, "uid");
            return (Criteria) this;
        }

        public Criteria andSellerQualificationCodeIsNull() {
            addCriterion("seller_qualification_code is null");
            return (Criteria) this;
        }

        public Criteria andSellerQualificationCodeIsNotNull() {
            addCriterion("seller_qualification_code is not null");
            return (Criteria) this;
        }

        public Criteria andSellerQualificationCodeEqualTo(String value) {
            addCriterion("seller_qualification_code =", value, "sellerQualificationCode");
            return (Criteria) this;
        }

        public Criteria andSellerQualificationCodeNotEqualTo(String value) {
            addCriterion("seller_qualification_code <>", value, "sellerQualificationCode");
            return (Criteria) this;
        }

        public Criteria andSellerQualificationCodeGreaterThan(String value) {
            addCriterion("seller_qualification_code >", value, "sellerQualificationCode");
            return (Criteria) this;
        }

        public Criteria andSellerQualificationCodeGreaterThanOrEqualTo(String value) {
            addCriterion("seller_qualification_code >=", value, "sellerQualificationCode");
            return (Criteria) this;
        }

        public Criteria andSellerQualificationCodeLessThan(String value) {
            addCriterion("seller_qualification_code <", value, "sellerQualificationCode");
            return (Criteria) this;
        }

        public Criteria andSellerQualificationCodeLessThanOrEqualTo(String value) {
            addCriterion("seller_qualification_code <=", value, "sellerQualificationCode");
            return (Criteria) this;
        }

        public Criteria andSellerQualificationCodeLike(String value) {
            addCriterion("seller_qualification_code like", value, "sellerQualificationCode");
            return (Criteria) this;
        }

        public Criteria andSellerQualificationCodeNotLike(String value) {
            addCriterion("seller_qualification_code not like", value, "sellerQualificationCode");
            return (Criteria) this;
        }

        public Criteria andSellerQualificationCodeIn(List<String> values) {
            addCriterion("seller_qualification_code in", values, "sellerQualificationCode");
            return (Criteria) this;
        }

        public Criteria andSellerQualificationCodeNotIn(List<String> values) {
            addCriterion("seller_qualification_code not in", values, "sellerQualificationCode");
            return (Criteria) this;
        }

        public Criteria andSellerQualificationCodeBetween(String value1, String value2) {
            addCriterion("seller_qualification_code between", value1, value2, "sellerQualificationCode");
            return (Criteria) this;
        }

        public Criteria andSellerQualificationCodeNotBetween(String value1, String value2) {
            addCriterion("seller_qualification_code not between", value1, value2, "sellerQualificationCode");
            return (Criteria) this;
        }

        public Criteria andSmallSellerCodeIsNull() {
            addCriterion("small_seller_code is null");
            return (Criteria) this;
        }

        public Criteria andSmallSellerCodeIsNotNull() {
            addCriterion("small_seller_code is not null");
            return (Criteria) this;
        }

        public Criteria andSmallSellerCodeEqualTo(String value) {
            addCriterion("small_seller_code =", value, "smallSellerCode");
            return (Criteria) this;
        }

        public Criteria andSmallSellerCodeNotEqualTo(String value) {
            addCriterion("small_seller_code <>", value, "smallSellerCode");
            return (Criteria) this;
        }

        public Criteria andSmallSellerCodeGreaterThan(String value) {
            addCriterion("small_seller_code >", value, "smallSellerCode");
            return (Criteria) this;
        }

        public Criteria andSmallSellerCodeGreaterThanOrEqualTo(String value) {
            addCriterion("small_seller_code >=", value, "smallSellerCode");
            return (Criteria) this;
        }

        public Criteria andSmallSellerCodeLessThan(String value) {
            addCriterion("small_seller_code <", value, "smallSellerCode");
            return (Criteria) this;
        }

        public Criteria andSmallSellerCodeLessThanOrEqualTo(String value) {
            addCriterion("small_seller_code <=", value, "smallSellerCode");
            return (Criteria) this;
        }

        public Criteria andSmallSellerCodeLike(String value) {
            addCriterion("small_seller_code like", value, "smallSellerCode");
            return (Criteria) this;
        }

        public Criteria andSmallSellerCodeNotLike(String value) {
            addCriterion("small_seller_code not like", value, "smallSellerCode");
            return (Criteria) this;
        }

        public Criteria andSmallSellerCodeIn(List<String> values) {
            addCriterion("small_seller_code in", values, "smallSellerCode");
            return (Criteria) this;
        }

        public Criteria andSmallSellerCodeNotIn(List<String> values) {
            addCriterion("small_seller_code not in", values, "smallSellerCode");
            return (Criteria) this;
        }

        public Criteria andSmallSellerCodeBetween(String value1, String value2) {
            addCriterion("small_seller_code between", value1, value2, "smallSellerCode");
            return (Criteria) this;
        }

        public Criteria andSmallSellerCodeNotBetween(String value1, String value2) {
            addCriterion("small_seller_code not between", value1, value2, "smallSellerCode");
            return (Criteria) this;
        }

        public Criteria andQualificationNameIsNull() {
            addCriterion("qualification_name is null");
            return (Criteria) this;
        }

        public Criteria andQualificationNameIsNotNull() {
            addCriterion("qualification_name is not null");
            return (Criteria) this;
        }

        public Criteria andQualificationNameEqualTo(String value) {
            addCriterion("qualification_name =", value, "qualificationName");
            return (Criteria) this;
        }

        public Criteria andQualificationNameNotEqualTo(String value) {
            addCriterion("qualification_name <>", value, "qualificationName");
            return (Criteria) this;
        }

        public Criteria andQualificationNameGreaterThan(String value) {
            addCriterion("qualification_name >", value, "qualificationName");
            return (Criteria) this;
        }

        public Criteria andQualificationNameGreaterThanOrEqualTo(String value) {
            addCriterion("qualification_name >=", value, "qualificationName");
            return (Criteria) this;
        }

        public Criteria andQualificationNameLessThan(String value) {
            addCriterion("qualification_name <", value, "qualificationName");
            return (Criteria) this;
        }

        public Criteria andQualificationNameLessThanOrEqualTo(String value) {
            addCriterion("qualification_name <=", value, "qualificationName");
            return (Criteria) this;
        }

        public Criteria andQualificationNameLike(String value) {
            addCriterion("qualification_name like", value, "qualificationName");
            return (Criteria) this;
        }

        public Criteria andQualificationNameNotLike(String value) {
            addCriterion("qualification_name not like", value, "qualificationName");
            return (Criteria) this;
        }

        public Criteria andQualificationNameIn(List<String> values) {
            addCriterion("qualification_name in", values, "qualificationName");
            return (Criteria) this;
        }

        public Criteria andQualificationNameNotIn(List<String> values) {
            addCriterion("qualification_name not in", values, "qualificationName");
            return (Criteria) this;
        }

        public Criteria andQualificationNameBetween(String value1, String value2) {
            addCriterion("qualification_name between", value1, value2, "qualificationName");
            return (Criteria) this;
        }

        public Criteria andQualificationNameNotBetween(String value1, String value2) {
            addCriterion("qualification_name not between", value1, value2, "qualificationName");
            return (Criteria) this;
        }

        public Criteria andEndTimeIsNull() {
            addCriterion("end_time is null");
            return (Criteria) this;
        }

        public Criteria andEndTimeIsNotNull() {
            addCriterion("end_time is not null");
            return (Criteria) this;
        }

        public Criteria andEndTimeEqualTo(String value) {
            addCriterion("end_time =", value, "endTime");
            return (Criteria) this;
        }

        public Criteria andEndTimeNotEqualTo(String value) {
            addCriterion("end_time <>", value, "endTime");
            return (Criteria) this;
        }

        public Criteria andEndTimeGreaterThan(String value) {
            addCriterion("end_time >", value, "endTime");
            return (Criteria) this;
        }

        public Criteria andEndTimeGreaterThanOrEqualTo(String value) {
            addCriterion("end_time >=", value, "endTime");
            return (Criteria) this;
        }

        public Criteria andEndTimeLessThan(String value) {
            addCriterion("end_time <", value, "endTime");
            return (Criteria) this;
        }

        public Criteria andEndTimeLessThanOrEqualTo(String value) {
            addCriterion("end_time <=", value, "endTime");
            return (Criteria) this;
        }

        public Criteria andEndTimeLike(String value) {
            addCriterion("end_time like", value, "endTime");
            return (Criteria) this;
        }

        public Criteria andEndTimeNotLike(String value) {
            addCriterion("end_time not like", value, "endTime");
            return (Criteria) this;
        }

        public Criteria andEndTimeIn(List<String> values) {
            addCriterion("end_time in", values, "endTime");
            return (Criteria) this;
        }

        public Criteria andEndTimeNotIn(List<String> values) {
            addCriterion("end_time not in", values, "endTime");
            return (Criteria) this;
        }

        public Criteria andEndTimeBetween(String value1, String value2) {
            addCriterion("end_time between", value1, value2, "endTime");
            return (Criteria) this;
        }

        public Criteria andEndTimeNotBetween(String value1, String value2) {
            addCriterion("end_time not between", value1, value2, "endTime");
            return (Criteria) this;
        }
    }

    /**
     * This class was generated by MyBatis Generator.
     * This class corresponds to the database table pc_qualification_info
     *
     * @mbggenerated do_not_delete_during_merge
     */
    public static class Criteria extends GeneratedCriteria {

        protected Criteria() {
            super();
        }
    }

    /**
     * This class was generated by MyBatis Generator.
     * This class corresponds to the database table pc_qualification_info
     *
     * @mbggenerated
     */
    public static class Criterion {
        private String condition;

        private Object value;

        private Object secondValue;

        private boolean noValue;

        private boolean singleValue;

        private boolean betweenValue;

        private boolean listValue;

        private String typeHandler;

        public String getCondition() {
            return condition;
        }

        public Object getValue() {
            return value;
        }

        public Object getSecondValue() {
            return secondValue;
        }

        public boolean isNoValue() {
            return noValue;
        }

        public boolean isSingleValue() {
            return singleValue;
        }

        public boolean isBetweenValue() {
            return betweenValue;
        }

        public boolean isListValue() {
            return listValue;
        }

        public String getTypeHandler() {
            return typeHandler;
        }

        protected Criterion(String condition) {
            super();
            this.condition = condition;
            this.typeHandler = null;
            this.noValue = true;
        }

        protected Criterion(String condition, Object value, String typeHandler) {
            super();
            this.condition = condition;
            this.value = value;
            this.typeHandler = typeHandler;
            if (value instanceof List<?>) {
                this.listValue = true;
            } else {
                this.singleValue = true;
            }
        }

        protected Criterion(String condition, Object value) {
            this(condition, value, null);
        }

        protected Criterion(String condition, Object value, Object secondValue, String typeHandler) {
            super();
            this.condition = condition;
            this.value = value;
            this.secondValue = secondValue;
            this.typeHandler = typeHandler;
            this.betweenValue = true;
        }

        protected Criterion(String condition, Object value, Object secondValue) {
            this(condition, value, secondValue, null);
        }
    }
}