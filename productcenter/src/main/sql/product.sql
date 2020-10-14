


-- 增减库存
DROP PROCEDURE IF EXISTS proc_sku_stock;
/*
*增减库存 
*flag 0 减，1 增 
*outFlag 输出参数 
*/
CREATE  PROCEDURE proc_sku_stock(
OUT outFlag varchar(50),
OUT error VARCHAR(5000),
IN detailStr LONGTEXT,
IN productsplit VARCHAR(10),
IN itemsplit VARCHAR(10),
IN flag INT
)
BEGIN

	DECLARE currentProduct VARCHAR(100) DEFAULT '';

	DECLARE skuCode VARCHAR(30) DEFAULT '';
	DECLARE skuNum VARCHAR(30) DEFAULT '';
	DECLARE skuZid INT DEFAULT 0;
	DECLARE skuOldStock INT DEFAULT 0;
	DECLARE skuCurrentStock INT DEFAULT 0;

	DECLARE i int DEFAULT 0;
	DECLARE j int DEFAULT 0;
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

		
		-- 更新商品sku表
		SELECT func_get_split_string_total(detailStr,productsplit) into i;


    -- SELECT i;			

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

					SELECT zid INTO skuZid FROM pc_skuinfo WHERE sku_code=skuCode;

					IF FOUND_ROWS()>0 THEN
							-- SELECT skuZid;
							SELECT stock_num INTO skuOldStock FROM pc_skuinfo WHERE zid=skuZid FOR UPDATE;
							
							IF flag = 0 THEN 
									SET skuCurrentStock = skuOldStock-skuNum;
									IF skuCurrentStock<0 THEN
										set error=CONCAT(error,skuCode); -- stock is not enough
										set t_error_not_exist=1;
										SET outFlag='941901003';-- stock is less
										
										IF t_error_not_exist=1 then
											leave loop_label; 
										END IF;
									ELSE
										UPDATE pc_skuinfo SET stock_num=stock_num-skuNum WHERE zid=skuZid and stock_num-skuNum>=0;
										
										IF ROW_COUNT()<=0 THEN
												set error=CONCAT(error,skuCode); -- stock is not enough
												set t_error_not_exist=1;
												SET outFlag='941901003';-- stock is less
												
												IF t_error_not_exist=1 then
													leave loop_label; 
												END IF;
										END IF;
									END IF;
							ELSE
								SET skuCurrentStock = skuOldStock+skuNum;
								UPDATE pc_skuinfo SET stock_num=skuCurrentStock WHERE zid=skuZid ;
							END IF;
						
							
							
					ELSE
							set error=CONCAT(error,skuCode); -- not exist 
							set t_error_not_exist=1;
							SET outFlag='941901002';-- not exist;
							
							IF t_error_not_exist=1 then
								leave loop_label; 
							END IF;
					END IF;

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

























DROP PROCEDURE IF EXISTS proc_add_product;

CREATE PROCEDURE proc_add_product(
OUT outFlag varchar(50),
OUT error VARCHAR(5000),
IN productCode VARCHAR(50),
IN produtName VARCHAR(5000),
IN sellerCode VARCHAR(50),
IN brandCode VARCHAR(50),
IN productWeight DECIMAL(18,2),
IN flagSale INT,
IN _catogetyId VARCHAR(50),
IN _description LONGTEXT,
IN picUrlStr VARCHAR(5000),--  url;url;url;url   
IN productPropertyStr LONGTEXT,  -- propertyNameCode$propertyCode$propertyName$propertyValue$propertyType;
IN skuStr LONGTEXT,    -- skuCode$sellPrice$marketPrice$stockNum$skuKey$skuValue$skuPicUrl;
IN productsplit VARCHAR(10),
IN itemsplit VARCHAR(10),
IN flag INT,
IN _keyword LONGTEXT,
IN flowStatus VARCHAR(20),
IN flowCode VARCHAR(50),
IN productJson LONGTEXT,
IN _updator VARCHAR(100),
IN _marketPrice  VARCHAR(30),
IN _minSellPrice  VARCHAR(30),
IN _maxSellPrice  VARCHAR(30),
IN productVolume  VARCHAR(30),
IN transportTemplate  VARCHAR(50),
IN sellProductCode  VARCHAR(50),
IN mainPicUrl  VARCHAR(200),
IN _labels VARCHAR(8000),
IN _flagPayWay INT,
IN _productVolumeItem   VARCHAR(500),
IN productStatus VARCHAR(50)
)
BEGIN

	DECLARE currentProduct VARCHAR(5000) DEFAULT '';


	DECLARE propertyNameCode VARCHAR(500) DEFAULT '';
	DECLARE propertyCode VARCHAR(500) DEFAULT '';
	DECLARE propertyName VARCHAR(500) DEFAULT '';
	DECLARE propertyValue VARCHAR(500) DEFAULT '';
  DECLARE propertyType  VARCHAR(500) DEFAULT '';
	
  DECLARE url VARCHAR(500) DEFAULT '';

	DECLARE skuCode VARCHAR(30) DEFAULT '';
	DECLARE stockNum VARCHAR(30) DEFAULT '';
	DECLARE sellPrice DECIMAL(18,2) DEFAULT 0;
	DECLARE marketPrice DECIMAL(18,2) DEFAULT 0;
	DECLARE skuKey VARCHAR(500) DEFAULT '';
	DECLARE skuValue VARCHAR(500) DEFAULT '';
	DECLARE skuPicUrl VARCHAR(500) DEFAULT '';
	DECLARE skuName VARCHAR(500) DEFAULT '';
	DECLARE skuSellProductCode VARCHAR(50) DEFAULT '';
	DECLARE securityStockNum VARCHAR(50) DEFAULT '';
	DECLARE skuAdv VARCHAR(500) DEFAULT '';
	


	DECLARE i int DEFAULT 0;
	DECLARE j int DEFAULT 0;
  DECLARE k int DEFAULT 0;
	DECLARE l int DEFAULT 0; 
  DECLARE m int DEFAULT 0;
	DECLARE n int DEFAULT 0;
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



		-- 新增
		IF flag = 0 THEN 
					

							INSERT INTO pc_productflow (uid,flow_code,product_code,product_json,flow_status,create_time,update_time,creator,updator)
							VALUES(REPLACE(UUID(),'-',''),flowCode,productCode,productJson,flowStatus,CONCAT(current_timestamp,''),CONCAT(current_timestamp,''),updator,updator);

							IF flag=0 THEN

								INSERT INTO pc_productinfo( uid, product_code, product_name, seller_code, brand_code, product_weight, flag_sale, create_time, 
											update_time,market_price,min_sell_price,max_sell_price,product_volume,transport_template,sell_productcode,mainpic_url,
											labels,flag_payway,product_volume_item,product_status)
									VALUES (REPLACE(UUID(),'-',''),productCode,produtName,sellerCode,brandCode,productWeight,flagSale,CONCAT(current_timestamp,''),
										CONCAT(current_timestamp,''),_marketPrice,_minSellPrice,_maxSellPrice,productVolume,transportTemplate,sellProductCode,mainPicUrl,
											_labels,_flagPayWay,_productVolumeItem,productStatus);

								
								INSERT INTO pc_productcategory_rel(uid, product_code, category_code, flag_main) VALUES (REPLACE(UUID(),'-',''),productCode,_catogetyId,1);
							
								INSERT INTO pc_productdescription(uid, product_code, description_info, keyword) VALUES (REPLACE(UUID(),'-',''),productCode,_description,_keyword);

								IF picUrlStr<>'' THEN

											-- 插入图片明细
											SELECT func_get_split_string_total(picUrlStr,productsplit) into i;
											SET j=0;
											loop_label:LOOP

													IF j=i THEN
													
													LEAVE loop_label;
												
													END IF;
														
													 SELECT func_get_split_string(picUrlStr,productsplit,j+1) into url;
													 INSERT INTO pc_productpic(uid, product_code, pic_url) VALUES (REPLACE(UUID(),'-',''),productCode,url);


													SET j=j+1;
											END LOOP loop_label; -- end LOOP
								END IF;

								IF productPropertyStr<>'' THEN
								
											SET i=0;
											SET j=0;
											SET k=0;
											SET currentProduct='';

											-- 插入商品属性
											SELECT func_get_split_string_total(productPropertyStr,productsplit) into i;
												
											loop_label:LOOP

													IF k=i THEN
													
													LEAVE loop_label;
												
													END IF;
														
													 SELECT func_get_split_string(productPropertyStr,productsplit,k+1) into currentProduct;

														-- SELECT currentProduct;

													 SELECT func_get_split_string_total(currentProduct,itemsplit) into j;

													 -- SELECT j;
														
														IF j<5 THEN
															set error=CONCAT(error,productCode); -- not exist or already active 
															set t_error_not_exist=1;
															SET outFlag='941901006';-- not exist;
															
															IF t_error_not_exist=1 then
																leave loop_label; 
															END IF;
														END IF;
														 -- propertyNameCode$propertyCode$propertyName$propertyValue$propertyType;
														SELECT func_get_split_string(currentProduct,itemsplit,1) into propertyNameCode;
														SELECT func_get_split_string(currentProduct,itemsplit,2) into propertyCode;
														SELECT func_get_split_string(currentProduct,itemsplit,3) into propertyName;
														SELECT func_get_split_string(currentProduct,itemsplit,4) into propertyValue;
														SELECT func_get_split_string(currentProduct,itemsplit,5) into propertyType;
														
														
													 INSERT INTO pc_productproperty(uid, product_code, property_keycode, property_code, property_key, property_value, property_type) 
														VALUES (REPLACE(UUID(),'-',''),productCode,propertyNameCode,propertyCode,propertyName,propertyValue,propertyType);

													SET k=k+1;
											END LOOP loop_label; -- end LOOP
								END IF;
								-- IN skuStr VARCHAR(500000),    -- skuCode$sellPrice$marketPrice$stockNum$skuKey$skuValue;
								IF skuStr<>'' THEN
								
											SET i=0;
											SET j=0;
											SET k=0;
											SET currentProduct='';

											-- 插入商品属性
											SELECT func_get_split_string_total(skuStr,productsplit) into i;
												
											loop_label:LOOP

													IF k=i THEN
													
													LEAVE loop_label;
												
													END IF;
														
													 SELECT func_get_split_string(skuStr,productsplit,k+1) into currentProduct;

													 SELECT func_get_split_string_total(currentProduct,itemsplit) into j;

													 -- SELECT j;
														
														IF j<11 THEN
															set error=CONCAT(error,productCode); -- not exist or already active 
															set t_error_not_exist=1;
															SET outFlag='941901006';-- not exist;
															
															IF t_error_not_exist=1 then
																leave loop_label; 
															END IF;
														END IF;
														 -- propertyNameCode$propertyCode$propertyName$propertyValue$propertyType;
														SELECT func_get_split_string(currentProduct,itemsplit,1) into skuCode;
														SELECT func_get_split_string(currentProduct,itemsplit,2) into sellPrice;
														SELECT func_get_split_string(currentProduct,itemsplit,3) into marketPrice;
														SELECT func_get_split_string(currentProduct,itemsplit,4) into stockNum;
														SELECT func_get_split_string(currentProduct,itemsplit,5) into skuKey;
														SELECT func_get_split_string(currentProduct,itemsplit,6) into skuValue;
														SELECT func_get_split_string(currentProduct,itemsplit,7) into skuPicUrl;
														SELECT func_get_split_string(currentProduct,itemsplit,8) into skuName;
														SELECT func_get_split_string(currentProduct,itemsplit,9) into skuSellProductCode;
														SELECT func_get_split_string(currentProduct,itemsplit,10) into securityStockNum;
														SELECT func_get_split_string(currentProduct,itemsplit,11) into skuAdv;

													 INSERT INTO pc_skuinfo(uid, sku_code, product_code, sell_price, market_price, stock_num, sku_key, sku_keyvalue,sku_picurl,sku_name,sell_productCode,seller_code,security_stock_num,sku_adv) 
														VALUES (REPLACE(UUID(),'-',''),skuCode,productCode,sellPrice,marketPrice,stockNum,skuKey,skuValue,skuPicUrl,skuName,skuSellProductCode,sellerCode,securityStockNum,skuAdv);

													IF skuPicUrl<>'' THEN
															
															 INSERT INTO pc_productpic(uid, product_code,sku_code, pic_url) VALUES (REPLACE(UUID(),'-',''),productCode,skuCode,skuPicUrl);
															
													END IF;

													SET k=k+1;
											END LOOP loop_label; -- end LOOP
								END IF;

							END IF;

		-- 审批通过
		END IF ;
		
		IF flag=1  THEN

				-- INSERT INTO pc_productflow (uid,flow_code,product_code,product_json,flow_status,create_time,update_time,creator,updator)
					-- 		VALUES(REPLACE(UUID(),'-',''),flowCode,productCode,productJson,flowStatus,CONCAT(current_timestamp,''),CONCAT(current_timestamp,''),updator,updator);
				
			 	UPDATE pc_productinfo SET  product_name=produtName ,product_weight=productWeight, update_time=CONCAT(current_timestamp,''),market_price=_marketPrice,transport_template=transportTemplate,sell_productcode=sellProductCode,mainpic_url=mainPicUrl,product_volume=productVolume WHERE product_code=productCode;
			 		
				IF ROW_COUNT() <=0 THEN 
					SET outFlag='941901007';-- 有一部分操作失败了，事务回滚了
					set error=CONCAT(error,productCode); -- not exist or already active 
				ELSE
							
							DELETE FROM pc_productdescription  WHERE  product_code=productCode ;
							INSERT INTO pc_productdescription(uid, product_code, description_info, keyword) VALUES (REPLACE(UUID(),'-',''),productCode,_description,_keyword);
							

							DELETE FROM pc_productpic  WHERE  product_code=productCode and (sku_code='' or sku_code is null);
							IF picUrlStr<>'' THEN

										-- 插入图片明细
										SELECT func_get_split_string_total(picUrlStr,productsplit) into i;
										SET k=0;
										loop_label:LOOP

												IF k=i THEN
												
												LEAVE loop_label;
											
												END IF;
													
												 SELECT func_get_split_string(picUrlStr,productsplit,k+1) into url;
												 INSERT INTO pc_productpic(uid, product_code, pic_url) VALUES (REPLACE(UUID(),'-',''),productCode,url);
												SET k=k+1;
										END LOOP loop_label; -- end LOOP

							END IF;
						
								
				END IF;
	 END IF ;
	 
  
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

	
END
























CREATE VIEW v_pc_productsku AS
SELECT b.zid,b.uid, a.product_code,a.product_name,b.sku_code,b.sku_name,b.sell_price,b.stock_num,a.seller_code
FROM pc_productinfo a,pc_skuinfo b where a.product_code=b.product_code




alter VIEW v_pc_sku AS
SELECT 
b.*,a.flag_payway,a.flag_sale,a.product_status
 FROM pc_productinfo a,pc_skuinfo b
WHERE a.product_code=b.product_code



