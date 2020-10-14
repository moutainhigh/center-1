


DROP PROCEDURE IF EXISTS proc_create_order;
/*
*创建订单
*outFlag 输出参数 
*/
CREATE  PROCEDURE proc_create_order(
OUT outFlag varchar(50),
OUT error VARCHAR(5000),
IN orderCode varchar(50), 
IN orderSource varchar(50),
IN orderType varchar(50), 
IN orderStatus varchar(50),
IN sellerCode varchar(50), 
IN buyerCode varchar(50),
IN payType varchar(50), 
IN sendType varchar(50),
IN productMoney DECIMAL(18,2),
IN transportMoney DECIMAL(18,2),
IN promotionMoney DECIMAL(18,2),
IN orderMoney DECIMAL(18,2),
IN payedMoney DECIMAL(18,2),
IN createTime varchar(50), 
IN updateTime varchar(50),
IN areaCode varchar(50), 
IN _address varchar(500),
IN postCode varchar(50), 
IN _mobilephone varchar(50),
IN _telephone varchar(50), 
IN receivePerson varchar(50), 
IN _email varchar(500),
IN invoiceTitle varchar(50), 
IN flagInvoice varchar(50),
IN _remark varchar(50),
IN detailStr LONGTEXT,
IN activityStr LONGTEXT,
IN freeTransportMoney  DECIMAL(18,2),
IN dueMoney  DECIMAL(18,2),
IN payStr LONGTEXT,
IN invoiceType VARCHAR(50),
IN invoiceContent VARCHAR(500),
IN orderChannel VARCHAR(500),
IN productsplit VARCHAR(10),
IN itemsplit VARCHAR(10)
)

BEGIN

	DECLARE currentProduct VARCHAR(5000) DEFAULT '';

	DECLARE skuCode VARCHAR(30) DEFAULT '';
	DECLARE skuPrice VARCHAR(30) DEFAULT '';
	DECLARE skuNum VARCHAR(30) DEFAULT '';
	DECLARE skuProductCode VARCHAR(30) DEFAULT '';
	DECLARE skuProductName VARCHAR(500) DEFAULT '';
	DECLARE productPicUrl VARCHAR(500) DEFAULT '';
  DECLARE productCode VARCHAR(50) DEFAULT '';
	DECLARE activityCode VARCHAR(50) DEFAULT '';
	DECLARE activityType VARCHAR(50) DEFAULT '';
	DECLARE preferentialMoney VARCHAR(50) DEFAULT '';

  DECLARE paySequenceid_1 VARCHAR(50) DEFAULT '';
	DECLARE payType_1 VARCHAR(50) DEFAULT '';
	DECLARE payedMoney_1 VARCHAR(50) DEFAULT '';
	DECLARE payRemark_1 VARCHAR(500) DEFAULT '';

	DECLARE i int DEFAULT 0;
	DECLARE j int DEFAULT 0;
	/** 标记是否出错 */ 
	DECLARE t_error int default 0; 
	/** 标记是否出错 */ 
	DECLARE t_error_not_exist int default 0; 
	/** 如果出现sql异常，则将t_error设置为1后继续执行后面的操作 */ 
	DECLARE continue handler for sqlexception set t_error=1; -- 出错处理 
	-- SET global log_bin_trust_function_creators=1;
	-- SELECT func_get_split_string_total(cardnostr,cardsplit) into i;
	/** 设置时间 */ 
	SET createTime=CONCAT(current_timestamp,'');

	SET error='';

	-- 测试专用
	IF orderSource='aabbccdd' THEN
			
			SET outFlag='9888';
			
	ELSE



				START TRANSACTION;
					-- 插入主表
					INSERT INTO oc_orderinfo(uid, order_code, order_source, order_type, order_status, seller_code, buyer_code, pay_type, send_type, 
						product_money, transport_money, promotion_money, order_money, payed_money, create_time, update_time,product_name,free_transport_money,due_money,order_channel) 
					VALUES (REPLACE(UUID(),'-',''),orderCode,orderSource,orderType,orderStatus,sellerCode,buyerCode,payType,sendType,
							productMoney,transportMoney,promotionMoney,orderMoney,payedMoney,createTime,createTime,'',freeTransportMoney,dueMoney,orderChannel);
					-- 插入地址表
					INSERT INTO oc_orderadress(uid, order_code, area_code, address, postcode, mobilephone, telephone, receive_person, email, invoice_title, flag_invoice, remark,invoice_type,invoice_content) 
					VALUES (REPLACE(UUID(),'-',''),orderCode,areaCode,_address,postCode,_mobilephone,_telephone,receivePerson,_email,invoiceTitle,flagInvoice,_remark,invoiceType,invoiceContent);

					-- 插入商品明细表
					SELECT func_get_split_string_total(detailStr,productsplit) into i;
						
					loop_label:LOOP

							IF i=0 THEN
							
							LEAVE loop_label;
						
							END IF;
								set j=0;
								
								SELECT func_get_split_string(detailStr,productsplit,i) into currentProduct;
								
								SELECT func_get_split_string_total(currentProduct,itemsplit) into j;
								
								IF j<6 THEN
									set error=CONCAT(error,orderCode); -- not exist or already active 
									set t_error_not_exist=1;
									SET outFlag='939301004';-- not exist;
									
									IF t_error_not_exist=1 then
										leave loop_label; 
									END IF;
								END IF;
								
								SELECT func_get_split_string(currentProduct,itemsplit,1) into skuCode;
								SELECT func_get_split_string(currentProduct,itemsplit,2) into skuPrice;
								SELECT func_get_split_string(currentProduct,itemsplit,3) into skuNum;
								SELECT func_get_split_string(currentProduct,itemsplit,4) into skuProductCode;
								SELECT func_get_split_string(currentProduct,itemsplit,5) into skuProductName;
								SELECT func_get_split_string(currentProduct,itemsplit,6) into productPicUrl;
								
								
								INSERT INTO oc_orderdetail(uid, order_code, sku_code, product_code, sku_name, sku_price, sku_num,product_picurl) 
									VALUES (REPLACE(UUID(),'-',''),orderCode,skuCode,skuProductCode,skuProductName,skuPrice,skuNum,productPicUrl);
								
								UPDATE oc_orderinfo SET product_name=(SELECT GROUP_CONCAT(sku_name) FROM oc_orderdetail where order_code=orderCode) where order_code=orderCode;

							SET i=i-1;
					END LOOP loop_label; -- end LOOP


					IF activityStr<>'' THEN
						
							set i=0;

								-- 插入活动明细表
							SELECT func_get_split_string_total(activityStr,productsplit) into i;
								
							loop_label:LOOP

									IF i=0 THEN
									
									LEAVE loop_label;
								
									END IF;
										set j=0;
										set currentProduct='';

										SELECT func_get_split_string(activityStr,productsplit,i) into currentProduct;
										
										SELECT func_get_split_string_total(currentProduct,itemsplit) into j;
										
										
										IF j<5 THEN
											set error=CONCAT(error,orderCode); -- not exist or already active 
											set t_error_not_exist=1;
											SET outFlag='939301004';-- not exist;
											
											IF t_error_not_exist=1 then
												leave loop_label; 
											END IF;
										END IF;
										
										SELECT func_get_split_string(currentProduct,itemsplit,1) into productCode;
										SELECT func_get_split_string(currentProduct,itemsplit,2) into skuCode;
										SELECT func_get_split_string(currentProduct,itemsplit,3) into activityCode;
										SELECT func_get_split_string(currentProduct,itemsplit,4) into activityType;
										SELECT func_get_split_string(currentProduct,itemsplit,5) into preferentialMoney;
										
										
										INSERT INTO oc_order_activity(uid, order_code, sku_code, product_code, preferential_money,activity_code,activity_type) 
											VALUES (REPLACE(UUID(),'-',''),orderCode,skuCode,productCode,preferentialMoney,activityCode,activityType);
										
										
									SET i=i-1;
							END LOOP loop_label; -- end LOOP

					END IF;
					

					IF payStr<>'' THEN
							set i=0;
								-- 插入活动明细表
							SELECT func_get_split_string_total(payStr,productsplit) into i;
								
							loop_label:LOOP

									IF i=0 THEN
									
									LEAVE loop_label;
								
									END IF;
										set j=0;
										set currentProduct='';

										SELECT func_get_split_string(payStr,productsplit,i) into currentProduct;
										
										SELECT func_get_split_string_total(currentProduct,itemsplit) into j;



										IF j<4 THEN
											set error=CONCAT(error,orderCode); -- not exist or already active 
											set t_error_not_exist=1;
											SET outFlag='939301004';-- not exist;
											
											IF t_error_not_exist=1 then
												leave loop_label; 
											END IF;
										END IF;
										
										SELECT func_get_split_string(currentProduct,itemsplit,1) into paySequenceid_1;
										SELECT func_get_split_string(currentProduct,itemsplit,2) into payType_1;
										SELECT func_get_split_string(currentProduct,itemsplit,3) into payedMoney_1;
										SELECT func_get_split_string(currentProduct,itemsplit,4) into payRemark_1;
										
										
										INSERT INTO oc_order_pay(uid, order_code, pay_sequenceid, payed_money, create_time,pay_type,pay_remark) 
											VALUES (REPLACE(UUID(),'-',''),orderCode,paySequenceid_1,payedMoney_1,CONCAT(current_timestamp,''),payType_1,payRemark_1);
										
										
									SET i=i-1;
							END LOOP loop_label; -- end LOOP

					END IF;

					
					IF t_error=1 THEN -- 如果操作失败了 ，更新什么的 
						ROLLBACK;
						SET outFlag='939301005';-- 有一部分操作失败了，事务回滚了
					ELSE
						IF t_error_not_exist = 1 THEN -- 如果 不符合条件 
							ROLLBACK;
						ELSE
							SET outFlag='1'; -- 成功
							COMMIT;
						END IF; -- 
					END IF; 

	END IF;


	
END






DROP PROCEDURE IF EXISTS proc_sku_stockchange_log_add;
/*
*增减库存 
*flag 0 减，1 增 
*outFlag 输出参数 
*/
CREATE  PROCEDURE proc_sku_stockchange_log_add(
OUT outFlag VARCHAR(50),
OUT error VARCHAR(5000),
IN detailStr LONGTEXT,
IN productsplit VARCHAR(10),
IN itemsplit VARCHAR(10),
IN orderCode VARCHAR(50),
IN changeType VARCHAR(50),
IN createUser VARCHAR(100)
)
BEGIN

	DECLARE currentProduct VARCHAR(100) DEFAULT '';

	DECLARE skuCode VARCHAR(30) DEFAULT '';
	DECLARE skuNum VARCHAR(30) DEFAULT '';
	

	DECLARE i int DEFAULT 0;
	DECLARE j int DEFAULT 0;
	/** 标记是否出错 */ 
	DECLARE t_error int DEFAULT 0; 
	/** 标记是否出错 */ 
	DECLARE t_error_not_exist int default 0; 
	/** 如果出现sql异常，则将t_error设置为1后继续执行后面的操作 */ 
	DECLARE continue handler for sqlexception set t_error=1; -- 出错处理 
	SET global log_bin_trust_function_creators=1;
	-- SELECT func_get_split_string_total(cardnostr,cardsplit) into i;
	/** 设置时间 */ 
	-- SET createTime=CONCAT(current_timestamp,'');

	SET error='';

	START TRANSACTION;

		
		-- 更新商品sku表
		SELECT func_get_split_string_total(detailStr,productsplit) into i;
			
		loop_label:LOOP

				IF i=0 THEN
				
				LEAVE loop_label;
			
				END IF;
					set j=0;
					
					SELECT func_get_split_string(detailStr,productsplit,i) into currentProduct;
					
					SELECT func_get_split_string_total(currentProduct,itemsplit) into j;
					
					IF j<2 THEN
						set error=''; -- must be two
						set t_error_not_exist=1;
						SET outFlag='941901001';-- 
						
						IF t_error_not_exist=1 then
							leave loop_label; 
						END IF;
					END IF;
					
					SELECT func_get_split_string(currentProduct,itemsplit,1) into skuCode;
					SELECT func_get_split_string(currentProduct,itemsplit,2) into skuNum;

					
					INSERT INTO lc_stockchange(uid,code,info,create_time,create_user,change_stock,change_type)
					VALUES(REPLACE(UUID(),'-',''),skuCode,orderCode,CONCAT(current_timestamp,''),createUser,skuNum,changeType);

				SET i=i-1;
		END LOOP loop_label; -- end LOOP
		
		IF t_error=1 THEN -- 如果操作失败了 ，更新什么的 
			ROLLBACK;
			SET outFlag='941901004';-- 有一部分操作失败了，事务回滚了
		ELSE
			IF t_error_not_exist = 1 THEN -- 如果 不符合条件 
				ROLLBACK;
			ELSE
				SET outFlag='1'; -- 成功
				COMMIT;
			END IF; -- 
		END IF; 

	
END;;





CREATE VIEW v_sellororder AS
SELECT ocod.order_code,ocod.create_time,ocod.buyer_code,ocod.seller_code,
ocod.order_money,ocod.order_status,ocoa.receive_person,ocoa.mobilephone,ocod.product_name
FROM oc_orderinfo ocod INNER JOIN oc_orderadress ocoa ON ocod.order_code=ocoa.order_code


ALTER VIEW v_sellororder AS SELECT
	ocod.zid AS zid,
	ocod.uid AS uid,
	ocod.order_code AS order_code,
	ocod.create_time AS create_time,
	ocod.buyer_code AS buyer_code,
	ocod.seller_code AS seller_code,
	ocod.order_money AS order_money,
	ocod.order_status AS order_status,
	ocoa.receive_person AS receive_person,
	ocoa.mobilephone AS mobilephone,
	ocod.product_name AS product_name,
	ocod.payed_money AS payed_money,
	ocod.order_source,
	ocod.order_type,
	ocod.pay_type,
	ocod.send_type,
	ocod.transport_money,
	ocod.due_money
FROM
	(
		oc_orderinfo ocod
		JOIN oc_orderadress ocoa ON (
			(
				ocod.order_code = ocoa.order_code
			)
		)
	)


CREATE VIEW v_oc_orderdetail AS
SELECT order_code ,group_concat(sku_name) FROM oc_orderdetail GROUP BY order_code





DROP FUNCTION IF EXISTS func_get_split_string;

CREATE  FUNCTION func_get_split_string(f_string LONGTEXT,f_delimiter varchar(5),f_order int) RETURNS varchar(80000) CHARSET utf8
DETERMINISTIC
READS SQL DATA
BEGIN
  declare result LONGTEXT default '';
  -- 两次反转
  set result = reverse(substring_index(reverse(substring_index(f_string,f_delimiter,f_order)),f_delimiter,1));
  return result;
END;

DROP FUNCTION IF EXISTS func_get_split_string_total;

CREATE  FUNCTION func_get_split_string_total(
f_string LONGTEXT,f_delimiter varchar(5)
) RETURNS int(11)
DETERMINISTIC
READS SQL DATA
BEGIN

  declare returnInt int(11);
  if length(f_delimiter)<>0  then
     return 1+(length(f_string) - length(replace(f_string,f_delimiter,'')))/length(f_delimiter);
  else    
     return 1;
  end if;
END;



