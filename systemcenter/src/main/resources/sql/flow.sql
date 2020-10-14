

DROP PROCEDURE IF EXISTS proc_flow_create;
/*
*流程审批单创建
*/
CREATE  PROCEDURE proc_flow_create(
OUT outFlag varchar(50),
OUT error VARCHAR(5000),
IN flowCode VARCHAR(50),
IN flowType VARCHAR(50),
IN currentStatus VARCHAR(50),
IN flowIsend INT,
IN outCode VARCHAR(50),
IN _creator VARCHAR(100),
IN flowTitle VARCHAR(500),
IN flowUrl VARCHAR(500),
IN flowRemark VARCHAR(500),
IN nextOperatorStr VARCHAR(5000),      -- for example id1,id2,id3
IN nextOperatorStatusStr VARCHAR(5000)     -- for example id1,id2,id3
)
BEGIN

	/** 标记是否出错 */ 
	DECLARE t_error int DEFAULT 0; 
	/** 标记是否出错 */ 
	DECLARE t_error_not_exist int default 0; 
	/** 如果出现sql异常，则将t_error设置为1后继续执行后面的操作 */ 
	DECLARE continue handler for sqlexception set t_error=1; -- 出错处理 
	-- SET global log_bin_trust_function_creators=1;
	-- SELECT func_get_split_string_total(cardnostr,cardsplit) into i;
	/** 设置时间 */ 
	-- SET createTime=CONCAT(current_timestamp,'');

	SET error='';

	START TRANSACTION;

	
  INSERT INTO sc_flow_main(uid, flow_code, flow_type, creator, updator, create_time, update_time, outer_code, flow_url,flow_title, flow_remark, flow_isend, current_status, last_status, next_operators, next_operator_status) 
	VALUES (REPLACE(UUID(),'-',''),flowCode,flowType,_creator,_creator,CONCAT(current_timestamp,''),CONCAT(current_timestamp,''),outCode,flowUrl,flowTitle,flowRemark,flowIsend,currentStatus,0,nextOperatorStr,nextOperatorStatusStr);

	INSERT INTO sc_flow_history( uid, flow_code, flow_type, creator, create_time, flow_remark, current_status) 
		VALUES (REPLACE(UUID(),'-',''),flowCode,flowType,_creator,CONCAT(current_timestamp,''),flowRemark,currentStatus);
	
		
	IF t_error=1 THEN -- 如果操作失败了 ，更新什么的 
		ROLLBACK;
		SET outFlag='949701009';-- 有一部分操作失败了，事务回滚了
	ELSE
		IF t_error_not_exist = 1 THEN -- 如果 不符合条件 
			ROLLBACK;
		ELSE
			SET outFlag='1'; -- 成功
			COMMIT;
		END IF; -- 
	END IF; 

	
END;;









DROP PROCEDURE IF EXISTS proc_flow_changestatus;
/*
*流程审批单审批
*/
CREATE  PROCEDURE proc_flow_changestatus(
OUT outFlag varchar(50),
OUT error VARCHAR(5000),
IN flowZid INT,
IN flowCode VARCHAR(50),
IN flowType VARCHAR(50),
IN fromStatus VARCHAR(50),
IN toStatus VARCHAR(50),
IN flowIsend INT,
IN _updator VARCHAR(100),
IN flowRemark VARCHAR(500),
IN nextOperatorStr VARCHAR(5000),      -- for example id1,id2,id3
IN nextOperatorStatusStr VARCHAR(5000)     -- for example id1,id2,id3
)
BEGIN

	/** 标记是否出错 */ 
	DECLARE t_error int DEFAULT 0; 
	/** 标记是否出错 */ 
	DECLARE t_error_not_exist int default 0; 
	/** 如果出现sql异常，则将t_error设置为1后继续执行后面的操作 */ 
	DECLARE continue handler for sqlexception set t_error=1; -- 出错处理 
	-- SET global log_bin_trust_function_creators=1;
	-- SELECT func_get_split_string_total(cardnostr,cardsplit) into i;
	/** 设置时间 */ 
	-- SET createTime=CONCAT(current_timestamp,'');

	SET error='';

	START TRANSACTION;

	SELECT zid FROM sc_flow_main WHERE zid=flowZid AND current_status=fromStatus FOR UPDATE;

	IF FOUND_ROWS() >0 THEN

		  UPDATE sc_flow_main set updator=_updator, update_time=CONCAT(current_timestamp,''), 
				flow_isend=flowIsend, current_status=toStatus, last_status=fromStatus, next_operators=nextOperatorStr, next_operator_status=nextOperatorStatusStr
			WHERE zid=flowZid;
			

			INSERT INTO sc_flow_history( uid, flow_code, flow_type, creator, create_time, flow_remark, current_status) 
				VALUES (REPLACE(UUID(),'-',''),flowCode,flowType,_updator,CONCAT(current_timestamp,''),flowRemark,toStatus);

		IF t_error=1 THEN -- 如果操作失败了 ，更新什么的 
				ROLLBACK;
				SET outFlag='949701009';-- 有一部分操作失败了，事务回滚了
			ELSE
				IF t_error_not_exist = 1 THEN -- 如果 不符合条件 
					ROLLBACK;
				ELSE
					SET outFlag='1'; -- 成功
					COMMIT;
				END IF; -- 
			END IF; 

	ELSE
			SET outFlag='949701011';
			
			COMMIT;
			
	END IF;
	
END;;





CREATE VIEW v_sc_flow_main_history AS
SELECT fm.zid,fm.uid,fm.flow_code,fm.flow_type,fm.flow_title,fm.create_time,fm.creator,fm.updator,fm.update_time,fm.flow_remark,fm.flow_url,
(SELECT GROUP_CONCAT(sfh.creator) FROM sc_flow_history as sfh where sfh.flow_code=fm.flow_code) as creators,fm.outer_code,fm.current_status
FROM sc_flow_main as fm



ALTER VIEW v_sc_flow_main_history AS 
SELECT fm.zid,fm.uid,fm.flow_code,fm.flow_type,fm.flow_title,fm.create_time,fm.creator,fm.updator,fm.update_time,fm.flow_remark,fm.flow_url,
(SELECT GROUP_CONCAT(sfh.creator) FROM sc_flow_history as sfh where sfh.flow_code=fm.flow_code) as creators,fm.outer_code,fm.current_status
FROM sc_flow_main as fm





