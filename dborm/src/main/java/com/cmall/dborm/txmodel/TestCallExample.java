package com.cmall.dborm.txmodel;

import java.util.ArrayList;
import java.util.List;

public class TestCallExample {
    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database table test_call
     *
     * @mbggenerated
     */
    protected String orderByClause;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database table test_call
     *
     * @mbggenerated
     */
    protected boolean distinct;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database table test_call
     *
     * @mbggenerated
     */
    protected List<Criteria> oredCriteria;

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table test_call
     *
     * @mbggenerated
     */
    public TestCallExample() {
        oredCriteria = new ArrayList<Criteria>();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table test_call
     *
     * @mbggenerated
     */
    public void setOrderByClause(String orderByClause) {
        this.orderByClause = orderByClause;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table test_call
     *
     * @mbggenerated
     */
    public String getOrderByClause() {
        return orderByClause;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table test_call
     *
     * @mbggenerated
     */
    public void setDistinct(boolean distinct) {
        this.distinct = distinct;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table test_call
     *
     * @mbggenerated
     */
    public boolean isDistinct() {
        return distinct;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table test_call
     *
     * @mbggenerated
     */
    public List<Criteria> getOredCriteria() {
        return oredCriteria;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table test_call
     *
     * @mbggenerated
     */
    public void or(Criteria criteria) {
        oredCriteria.add(criteria);
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table test_call
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
     * This method corresponds to the database table test_call
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
     * This method corresponds to the database table test_call
     *
     * @mbggenerated
     */
    protected Criteria createCriteriaInternal() {
        Criteria criteria = new Criteria();
        return criteria;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table test_call
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
     * This class corresponds to the database table test_call
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

        public Criteria andDropdownIsNull() {
            addCriterion("dropdown is null");
            return (Criteria) this;
        }

        public Criteria andDropdownIsNotNull() {
            addCriterion("dropdown is not null");
            return (Criteria) this;
        }

        public Criteria andDropdownEqualTo(String value) {
            addCriterion("dropdown =", value, "dropdown");
            return (Criteria) this;
        }

        public Criteria andDropdownNotEqualTo(String value) {
            addCriterion("dropdown <>", value, "dropdown");
            return (Criteria) this;
        }

        public Criteria andDropdownGreaterThan(String value) {
            addCriterion("dropdown >", value, "dropdown");
            return (Criteria) this;
        }

        public Criteria andDropdownGreaterThanOrEqualTo(String value) {
            addCriterion("dropdown >=", value, "dropdown");
            return (Criteria) this;
        }

        public Criteria andDropdownLessThan(String value) {
            addCriterion("dropdown <", value, "dropdown");
            return (Criteria) this;
        }

        public Criteria andDropdownLessThanOrEqualTo(String value) {
            addCriterion("dropdown <=", value, "dropdown");
            return (Criteria) this;
        }

        public Criteria andDropdownLike(String value) {
            addCriterion("dropdown like", value, "dropdown");
            return (Criteria) this;
        }

        public Criteria andDropdownNotLike(String value) {
            addCriterion("dropdown not like", value, "dropdown");
            return (Criteria) this;
        }

        public Criteria andDropdownIn(List<String> values) {
            addCriterion("dropdown in", values, "dropdown");
            return (Criteria) this;
        }

        public Criteria andDropdownNotIn(List<String> values) {
            addCriterion("dropdown not in", values, "dropdown");
            return (Criteria) this;
        }

        public Criteria andDropdownBetween(String value1, String value2) {
            addCriterion("dropdown between", value1, value2, "dropdown");
            return (Criteria) this;
        }

        public Criteria andDropdownNotBetween(String value1, String value2) {
            addCriterion("dropdown not between", value1, value2, "dropdown");
            return (Criteria) this;
        }

        public Criteria andEditorIsNull() {
            addCriterion("editor is null");
            return (Criteria) this;
        }

        public Criteria andEditorIsNotNull() {
            addCriterion("editor is not null");
            return (Criteria) this;
        }

        public Criteria andEditorEqualTo(String value) {
            addCriterion("editor =", value, "editor");
            return (Criteria) this;
        }

        public Criteria andEditorNotEqualTo(String value) {
            addCriterion("editor <>", value, "editor");
            return (Criteria) this;
        }

        public Criteria andEditorGreaterThan(String value) {
            addCriterion("editor >", value, "editor");
            return (Criteria) this;
        }

        public Criteria andEditorGreaterThanOrEqualTo(String value) {
            addCriterion("editor >=", value, "editor");
            return (Criteria) this;
        }

        public Criteria andEditorLessThan(String value) {
            addCriterion("editor <", value, "editor");
            return (Criteria) this;
        }

        public Criteria andEditorLessThanOrEqualTo(String value) {
            addCriterion("editor <=", value, "editor");
            return (Criteria) this;
        }

        public Criteria andEditorLike(String value) {
            addCriterion("editor like", value, "editor");
            return (Criteria) this;
        }

        public Criteria andEditorNotLike(String value) {
            addCriterion("editor not like", value, "editor");
            return (Criteria) this;
        }

        public Criteria andEditorIn(List<String> values) {
            addCriterion("editor in", values, "editor");
            return (Criteria) this;
        }

        public Criteria andEditorNotIn(List<String> values) {
            addCriterion("editor not in", values, "editor");
            return (Criteria) this;
        }

        public Criteria andEditorBetween(String value1, String value2) {
            addCriterion("editor between", value1, value2, "editor");
            return (Criteria) this;
        }

        public Criteria andEditorNotBetween(String value1, String value2) {
            addCriterion("editor not between", value1, value2, "editor");
            return (Criteria) this;
        }

        public Criteria andListboxIsNull() {
            addCriterion("listbox is null");
            return (Criteria) this;
        }

        public Criteria andListboxIsNotNull() {
            addCriterion("listbox is not null");
            return (Criteria) this;
        }

        public Criteria andListboxEqualTo(String value) {
            addCriterion("listbox =", value, "listbox");
            return (Criteria) this;
        }

        public Criteria andListboxNotEqualTo(String value) {
            addCriterion("listbox <>", value, "listbox");
            return (Criteria) this;
        }

        public Criteria andListboxGreaterThan(String value) {
            addCriterion("listbox >", value, "listbox");
            return (Criteria) this;
        }

        public Criteria andListboxGreaterThanOrEqualTo(String value) {
            addCriterion("listbox >=", value, "listbox");
            return (Criteria) this;
        }

        public Criteria andListboxLessThan(String value) {
            addCriterion("listbox <", value, "listbox");
            return (Criteria) this;
        }

        public Criteria andListboxLessThanOrEqualTo(String value) {
            addCriterion("listbox <=", value, "listbox");
            return (Criteria) this;
        }

        public Criteria andListboxLike(String value) {
            addCriterion("listbox like", value, "listbox");
            return (Criteria) this;
        }

        public Criteria andListboxNotLike(String value) {
            addCriterion("listbox not like", value, "listbox");
            return (Criteria) this;
        }

        public Criteria andListboxIn(List<String> values) {
            addCriterion("listbox in", values, "listbox");
            return (Criteria) this;
        }

        public Criteria andListboxNotIn(List<String> values) {
            addCriterion("listbox not in", values, "listbox");
            return (Criteria) this;
        }

        public Criteria andListboxBetween(String value1, String value2) {
            addCriterion("listbox between", value1, value2, "listbox");
            return (Criteria) this;
        }

        public Criteria andListboxNotBetween(String value1, String value2) {
            addCriterion("listbox not between", value1, value2, "listbox");
            return (Criteria) this;
        }

        public Criteria andT1IsNull() {
            addCriterion("t1 is null");
            return (Criteria) this;
        }

        public Criteria andT1IsNotNull() {
            addCriterion("t1 is not null");
            return (Criteria) this;
        }

        public Criteria andT1EqualTo(String value) {
            addCriterion("t1 =", value, "t1");
            return (Criteria) this;
        }

        public Criteria andT1NotEqualTo(String value) {
            addCriterion("t1 <>", value, "t1");
            return (Criteria) this;
        }

        public Criteria andT1GreaterThan(String value) {
            addCriterion("t1 >", value, "t1");
            return (Criteria) this;
        }

        public Criteria andT1GreaterThanOrEqualTo(String value) {
            addCriterion("t1 >=", value, "t1");
            return (Criteria) this;
        }

        public Criteria andT1LessThan(String value) {
            addCriterion("t1 <", value, "t1");
            return (Criteria) this;
        }

        public Criteria andT1LessThanOrEqualTo(String value) {
            addCriterion("t1 <=", value, "t1");
            return (Criteria) this;
        }

        public Criteria andT1Like(String value) {
            addCriterion("t1 like", value, "t1");
            return (Criteria) this;
        }

        public Criteria andT1NotLike(String value) {
            addCriterion("t1 not like", value, "t1");
            return (Criteria) this;
        }

        public Criteria andT1In(List<String> values) {
            addCriterion("t1 in", values, "t1");
            return (Criteria) this;
        }

        public Criteria andT1NotIn(List<String> values) {
            addCriterion("t1 not in", values, "t1");
            return (Criteria) this;
        }

        public Criteria andT1Between(String value1, String value2) {
            addCriterion("t1 between", value1, value2, "t1");
            return (Criteria) this;
        }

        public Criteria andT1NotBetween(String value1, String value2) {
            addCriterion("t1 not between", value1, value2, "t1");
            return (Criteria) this;
        }

        public Criteria andT2IsNull() {
            addCriterion("t2 is null");
            return (Criteria) this;
        }

        public Criteria andT2IsNotNull() {
            addCriterion("t2 is not null");
            return (Criteria) this;
        }

        public Criteria andT2EqualTo(String value) {
            addCriterion("t2 =", value, "t2");
            return (Criteria) this;
        }

        public Criteria andT2NotEqualTo(String value) {
            addCriterion("t2 <>", value, "t2");
            return (Criteria) this;
        }

        public Criteria andT2GreaterThan(String value) {
            addCriterion("t2 >", value, "t2");
            return (Criteria) this;
        }

        public Criteria andT2GreaterThanOrEqualTo(String value) {
            addCriterion("t2 >=", value, "t2");
            return (Criteria) this;
        }

        public Criteria andT2LessThan(String value) {
            addCriterion("t2 <", value, "t2");
            return (Criteria) this;
        }

        public Criteria andT2LessThanOrEqualTo(String value) {
            addCriterion("t2 <=", value, "t2");
            return (Criteria) this;
        }

        public Criteria andT2Like(String value) {
            addCriterion("t2 like", value, "t2");
            return (Criteria) this;
        }

        public Criteria andT2NotLike(String value) {
            addCriterion("t2 not like", value, "t2");
            return (Criteria) this;
        }

        public Criteria andT2In(List<String> values) {
            addCriterion("t2 in", values, "t2");
            return (Criteria) this;
        }

        public Criteria andT2NotIn(List<String> values) {
            addCriterion("t2 not in", values, "t2");
            return (Criteria) this;
        }

        public Criteria andT2Between(String value1, String value2) {
            addCriterion("t2 between", value1, value2, "t2");
            return (Criteria) this;
        }

        public Criteria andT2NotBetween(String value1, String value2) {
            addCriterion("t2 not between", value1, value2, "t2");
            return (Criteria) this;
        }

        public Criteria andT3IsNull() {
            addCriterion("t3 is null");
            return (Criteria) this;
        }

        public Criteria andT3IsNotNull() {
            addCriterion("t3 is not null");
            return (Criteria) this;
        }

        public Criteria andT3EqualTo(String value) {
            addCriterion("t3 =", value, "t3");
            return (Criteria) this;
        }

        public Criteria andT3NotEqualTo(String value) {
            addCriterion("t3 <>", value, "t3");
            return (Criteria) this;
        }

        public Criteria andT3GreaterThan(String value) {
            addCriterion("t3 >", value, "t3");
            return (Criteria) this;
        }

        public Criteria andT3GreaterThanOrEqualTo(String value) {
            addCriterion("t3 >=", value, "t3");
            return (Criteria) this;
        }

        public Criteria andT3LessThan(String value) {
            addCriterion("t3 <", value, "t3");
            return (Criteria) this;
        }

        public Criteria andT3LessThanOrEqualTo(String value) {
            addCriterion("t3 <=", value, "t3");
            return (Criteria) this;
        }

        public Criteria andT3Like(String value) {
            addCriterion("t3 like", value, "t3");
            return (Criteria) this;
        }

        public Criteria andT3NotLike(String value) {
            addCriterion("t3 not like", value, "t3");
            return (Criteria) this;
        }

        public Criteria andT3In(List<String> values) {
            addCriterion("t3 in", values, "t3");
            return (Criteria) this;
        }

        public Criteria andT3NotIn(List<String> values) {
            addCriterion("t3 not in", values, "t3");
            return (Criteria) this;
        }

        public Criteria andT3Between(String value1, String value2) {
            addCriterion("t3 between", value1, value2, "t3");
            return (Criteria) this;
        }

        public Criteria andT3NotBetween(String value1, String value2) {
            addCriterion("t3 not between", value1, value2, "t3");
            return (Criteria) this;
        }
    }

    /**
     * This class was generated by MyBatis Generator.
     * This class corresponds to the database table test_call
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
     * This class corresponds to the database table test_call
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