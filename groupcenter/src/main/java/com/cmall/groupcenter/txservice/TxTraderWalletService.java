package com.cmall.groupcenter.txservice;


import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;

import com.cmall.dborm.txmapper.groupcenter.GcTraderWalletChangeLogMapper;
import com.cmall.dborm.txmapper.groupcenter.GcTraderWalletMapper;
import com.cmall.dborm.txmapper.groupcenter.GcWalletWithdrawInfoMapper;
import com.cmall.dborm.txmapper.groupcenter.GcWalletWithdrawLogMapper;
import com.cmall.dborm.txmodel.groupcenter.GcRebateLogExample;
import com.cmall.dborm.txmodel.groupcenter.GcTraderWallet;
import com.cmall.dborm.txmodel.groupcenter.GcTraderWalletChangeLog;
import com.cmall.dborm.txmodel.groupcenter.GcWalletWithdrawInfo;
import com.cmall.dborm.txmodel.groupcenter.GcWalletWithdrawInfoExample;
import com.cmall.dborm.txmodel.groupcenter.GcWalletWithdrawLog;
import com.cmall.groupcenter.GroupConstant;
import com.cmall.groupcenter.util.CalendarHelper;
import com.cmall.groupcenter.util.StringHelper;
import com.cmall.groupcenter.util.WgsMailSupport;
import com.cmall.groupcenter.wallet.model.TraderWalletMoneyOptionModel;
import com.cmall.groupcenter.wallet.model.TraderWalletTransferAmountInput;
import com.cmall.groupcenter.wallet.model.TraderWalletTransferAmountResult;
import com.cmall.groupcenter.wallet.model.WithdrawApplyInput;
import com.cmall.groupcenter.wallet.model.WithdrawApplyResult;
import com.cmall.groupcenter.wallet.service.WalletWithdrawService;
import com.srnpr.zapcom.baseclass.BaseClass;
import com.srnpr.zapcom.basehelper.BeansHelper;
import com.srnpr.zapcom.basehelper.FormatHelper;
import com.srnpr.zapcom.basehelper.JsonHelper;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapcom.basesupport.MailSupport;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.helper.WebHelper;
import com.srnpr.zapweb.helper.WebSessionHelper;
import com.srnpr.zapweb.webmodel.MWebResult;

/**
 *
 *商户钱包相关金额等账户操作
 *
 * @author lipengfei
 * @date 2015-11-04
 * @time 17:37
 * @email:lipengfei217@163.com
 *
 */
public class TxTraderWalletService extends BaseClass{


    /**
     *
     * @param manageCode 应用编号
     * @return
     */
	public TraderWalletTransferAmountResult doTransferAmount(TraderWalletTransferAmountInput traderWalletTransferAmountInput,String manageCode){

		MDataMap whereMap = new MDataMap();
		whereMap.put("manageCode",manageCode);

        WalletWithdrawService walletWithdrawService = new WalletWithdrawService();

        //如果钱包不存在则会创建一个默认的
        String accountCode= walletWithdrawService.getAccountCode(traderWalletTransferAmountInput.getMemberCode(), manageCode);

        TraderWalletTransferAmountResult result= new TraderWalletTransferAmountResult();

        if (accountCode!=null){
        	
        	String traderCode=walletWithdrawService.getTraderCode(manageCode);

            if (traderCode!=null){
                MDataMap walletData= walletWithdrawService.findWallet(traderCode, accountCode);
                    //金额发生额转变成bigDecimal类型
                    BigDecimal amountValue = new BigDecimal(traderWalletTransferAmountInput.getTransferAmount());

                    MWebResult resultTemp = null;

                    TraderWalletMoneyOptionModel traderWalletMoneyOptionModel = new TraderWalletMoneyOptionModel();
                    traderWalletMoneyOptionModel.setTraderCode(traderCode);
                    traderWalletMoneyOptionModel.setMoneyOccured(traderWalletTransferAmountInput.getTransferAmount());
                    traderWalletMoneyOptionModel.setRemark(null);
                    traderWalletMoneyOptionModel.setLogHisNotes(traderWalletTransferAmountInput.getHisNotes());
                    traderWalletMoneyOptionModel.setAccountCode(accountCode);
                    traderWalletMoneyOptionModel.setRelationCode(traderWalletTransferAmountInput.getTransferLogCode());//转账编号
                    traderWalletMoneyOptionModel.setMemberCode(traderWalletTransferAmountInput.getMemberCode());
                    //如果大于0就是转账，小于0就是退款
                    if (amountValue.compareTo(new BigDecimal(0))>=0){
                        traderWalletMoneyOptionModel.setChangeLogType(GroupConstant.WalletChangeTypeEnum.transferAmount.getType());
                        //转账，即加钱
                        resultTemp = doDeposit(traderWalletMoneyOptionModel);
                    }else {
                        traderWalletMoneyOptionModel.setChangeLogType(GroupConstant.WalletChangeTypeEnum.returnBackAmmount.getType());
                        resultTemp =doWithdraw(traderWalletMoneyOptionModel);
                    }

                    result.inOtherResult(resultTemp);
                    GcTraderWalletChangeLog log = (GcTraderWalletChangeLog) resultTemp.getResultObject();
                    result.setTransferLogCode(log.getLogCode());
            }else {
                result.inErrorMessage(918560003);
            }
        }else {
            //账户不存在
            result.inErrorMessage(918560002);
        }
		return result;
	}



    /**
     *
     * 金钱的相关操作
     * @param traderWalletMoneyOptionModel 加钱、减钱模型
     * @param addMoney 是否加钱， 如果值为true，则增加金额，否则减少金额
     * @return
     */
    public MWebResult doMoneyOperation(TraderWalletMoneyOptionModel traderWalletMoneyOptionModel,boolean addMoney){

        MWebResult result = new MWebResult();

        String moneyOccured="";//金额发生额

        GcTraderWalletMapper gcTraderWalletMapper = BeansHelper
                .upBean("bean_com_cmall_dborm_txmapper_GcTraderWalletMapper");

        String keyCode  = traderWalletMoneyOptionModel.getTraderCode()+"_"+traderWalletMoneyOptionModel.getAccountCode();

        //开始锁定执行流程编号 防止并发执行
        String walletLock = WebHelper.addLock(30, keyCode);
        try {

            if (StringUtils.isEmpty(walletLock)) {
                result.inErrorMessage(918519038, keyCode);
            }else {
                //加锁成功，代表当前无锁

                //开始获取钱包的钱数，并做相应的加钱操作

                WalletWithdrawService walletWithdrawService = new WalletWithdrawService();

                //如果钱包不存在则会创建一个默认的
                MDataMap walletData=walletWithdrawService.findWallet(traderWalletMoneyOptionModel.getTraderCode(), traderWalletMoneyOptionModel.getAccountCode());

                Integer walletZid = Integer.valueOf(StringHelper.getStringFromMap(walletData,"zid"));

                //为了兼容非正负判断金额取钱还是存钱的情况，此处的金额发生额以绝对值的方式来操作
                BigDecimal occured = new BigDecimal(traderWalletMoneyOptionModel.getMoneyOccured());
                BigDecimal occuredAbs = occured.abs();

                //当前钱包里的钱
                String preMoneyString = StringHelper.getStringFromMap(walletData,"available_balance");
                BigDecimal preMoney = new BigDecimal(preMoneyString);

                BigDecimal afterMoney = null;

                //因为occured取得是绝对值，所以肯定是正数
                if (addMoney){//如果是加钱的操作
                    //因为此处occured是绝对值，所以加上发生额的绝对值
                    afterMoney = preMoney.add(occuredAbs);
                    moneyOccured = occuredAbs.toString();//防止出现存钱的时候传入的值却是负值。

                }else{
                    //否则是减钱的操作
                    afterMoney = preMoney.subtract(occuredAbs);
                    moneyOccured = occuredAbs.negate().toString();//取钱的话，金额发生额必然是负值,因此需要在日志中计入负值
                }

                if (walletZid!=null){//防止walletZid为空的时候导致一次性将所有的数据的钱包数更新了。
                    GcTraderWallet wallet = new GcTraderWallet();
                    wallet.setAvailableBalance(afterMoney);
                    wallet.setZid(walletZid);
                    gcTraderWalletMapper.updateByPrimaryKeySelective(wallet);
                    //成功后插入日志

//                    GcTraderWalletChangeLog changeLog = null;
                    GcTraderWalletChangeLog changeLog = new GcTraderWalletChangeLog();

                    changeLog.setAccountCode(traderWalletMoneyOptionModel.getAccountCode());
                    changeLog.setTraderCode(traderWalletMoneyOptionModel.getTraderCode());
                    changeLog.setRelationCode(traderWalletMoneyOptionModel.getRelationCode());
                    changeLog.setPreAvailableBalance(new BigDecimal(preMoneyString));
                    changeLog.setAmountOccurred(new BigDecimal(moneyOccured));
                    changeLog.setNowAvailableBalance(afterMoney);
                    changeLog.setAmountChangeType(traderWalletMoneyOptionModel.getChangeLogType());
                    changeLog.setHisNotes(traderWalletMoneyOptionModel.getLogHisNotes());
                    changeLog.setRemark(traderWalletMoneyOptionModel.getRemark());
                    changeLog.setMemberCode(traderWalletMoneyOptionModel.getMemberCode());
                    GcTraderWalletChangeLog log = insertAmoutChangeLog(changeLog);

                    result.setResultObject(log);
                }else {
                    result.setResultCode(918560005);
                }
            }
//            throw new RuntimeException();
        }catch (Exception e){

            e.printStackTrace();
            result.setResultCode(0);
            result.setResultMessage(e.getMessage());

        } finally {
        //就算抛了异常，也需要记得解锁
            // 如果锁定成功后 则开始解锁流程
            if (StringUtils.isNotEmpty(keyCode)) {
                WebHelper.unLock(keyCode);
            }

        }




        return  result;

    }

    /**
     * 存钱，可以写入日志时传入备注信息
     * @param traderWalletMoneyOptionModel
     * @return
     */
    public MWebResult doDeposit(TraderWalletMoneyOptionModel traderWalletMoneyOptionModel){
        return doMoneyOperation(traderWalletMoneyOptionModel, true);
    }

 /**
     *取钱,可以传备注信息
     *@param  traderWalletMoneyOptionModel
     * @return
     */
    public MWebResult doWithdraw(TraderWalletMoneyOptionModel traderWalletMoneyOptionModel){
        return doMoneyOperation(traderWalletMoneyOptionModel, false);
    }

   /**
     * 插入金额变动日志
     * @return
     */
    public GcTraderWalletChangeLog insertAmoutChangeLog(GcTraderWalletChangeLog changeLog){
        GcTraderWalletChangeLogMapper gcTraderWalletChangeLogMapper = BeansHelper
                .upBean("bean_com_cmall_dborm_txmapper_GcTraderWalletChangeLogMapper");
        if (changeLog.getLogCode()==null){
            String logCode  = WebHelper.upCode("GCWCL");
            changeLog.setLogCode(logCode);
        }

        if (changeLog.getUid()==null){
            String upUuid  = WebHelper.upUuid();
            changeLog.setUid(upUuid);
        }

        if (changeLog.getCreateTime()==null){
            String now = CalendarHelper.Date2String(new Date(), "yyyy-MM-dd HH:mm:ss");
            changeLog.setCreateTime(now);
        }
        gcTraderWalletChangeLogMapper.insert(changeLog);
        return changeLog;
    }


    //钱包提现申请
  	public WithdrawApplyResult doWalletWithdrawApply(String manageCode,
  			WithdrawApplyInput inputParam) {
  		
  		// 记录请求
  		HttpServletRequest request = WebSessionHelper.create().upHttpRequest();
  		MDataMap logMap = new MDataMap();
  		logMap.put("request_code", WebHelper.upCode("RWAL"));
  		logMap.put("request_target", "walletWithdrawApply");
  		logMap.put("request_url", "http://" + request.getServerName() + ":"
  				+ request.getServerPort() + request.getRequestURI());
  		logMap.put("request_data",
  				new JsonHelper<WithdrawApplyInput>().GsonToJson(inputParam));
  		logMap.put("request_time", FormatHelper.upDateTime());
  		logMap.put("create_time", FormatHelper.upDateTime());
  		DbUp.upTable("lc_wallet_api_log").dataInsert(logMap);
  		//
  		
  		WalletWithdrawService withdrawService=new WalletWithdrawService();
  		
  		WithdrawApplyResult result=new WithdrawApplyResult();
  		
  		//判断提现金额
  		Pattern pattern = Pattern.compile("^[1-9]{1}[0-9]*+(.[0-9]*)?$");  
          Matcher isNum = pattern.matcher(inputParam.getWithdrawMoney());  
          if(!isNum.matches()){
          	result.inErrorMessage(918560006);
  			return result;
          }
          
          
  		BigDecimal withdrawMoney=new BigDecimal(inputParam.getWithdrawMoney()).setScale(2,RoundingMode.HALF_DOWN);
  		if(withdrawMoney.compareTo(new BigDecimal("10"))==-1){
  			result.inErrorMessage(918505212);
  			return result;
		}
  		//商户是否存在
  		String traderCode=withdrawService.getTraderCode(manageCode);
  		if(traderCode==null){
  			result.inErrorMessage(918560003);
  			return result;
  		}
  		
  		
  		//用户编号是否对应
  		String accountCode=withdrawService.getAccountCode(inputParam.getMemberCode(), manageCode);
  		if(accountCode==null){
  			result.inErrorMessage(918560002);
  			return result;
  		}
  		
  		//提现编号在数据库中是否已存在
  		MDataMap thirdWithdrawCodeMap=DbUp.upTable("gc_wallet_withdraw_info").one("third_withdraw_code",inputParam.getThirdWithdrawCode(),"trader_code",traderCode);
  		if(thirdWithdrawCodeMap!=null){
  			result.inErrorMessage(918560008);
  			return result;
  		}
  		
  		MDataMap walletMap=withdrawService.findWallet(traderCode,accountCode);
  		
  		//提现金额不能大于余额
  		BigDecimal availableBalance=new BigDecimal(walletMap.get("available_balance"));
  		if(availableBalance.compareTo(withdrawMoney)!=-1){
  			BigDecimal afterWithdrawMoney=availableBalance.add(withdrawMoney.negate());
  			//银行卡信息
  			MDataMap cardInfo=DbUp.upTable("gc_member_bank").one("bank_code",inputParam.getBankCode());
  			if(cardInfo==null){
  				result.inErrorMessage(918560004);
  				return result;
  			}
  			//检查银行卡中用户编号与提现用户是否一致
  			if(!cardInfo.get("account_code").equals(accountCode)){
  				result.inErrorMessage(918560005);
  				return result;
  			}
  			
  			// 计算手续费
			BigDecimal feeMoney = new BigDecimal(0);
			if (withdrawMoney.compareTo(new BigDecimal(100)) == -1) {
				feeMoney = new BigDecimal(1);
			}
  			//插入提现记录
  			GcWalletWithdrawInfo withdrawInfo=new GcWalletWithdrawInfo();
  			withdrawInfo.setUid(WebHelper.upUuid());
  			withdrawInfo.setWithdrawCode(WebHelper.upCode("GCWWI"));
  			withdrawInfo.setTraderCode(traderCode);
  			withdrawInfo.setAccountCode(accountCode);
  			withdrawInfo.setWithdrawMoney(withdrawMoney);
  			withdrawInfo.setFeeMoney(feeMoney);
  			withdrawInfo.setBeforeWithdrawMoney(availableBalance);
  			withdrawInfo.setAfterWithdrawMoney(afterWithdrawMoney);
  			withdrawInfo.setPayMoney(withdrawMoney.subtract(withdrawInfo.getFeeMoney()));
  			withdrawInfo.setMemberName( cardInfo.get("user_name"));
  			withdrawInfo.setBankName(cardInfo.get("bank_name"));
  			withdrawInfo.setCardCode(cardInfo.get("card_code"));
  			withdrawInfo.setPhone(cardInfo.get("bank_phone"));
  			withdrawInfo.setCertificateType(cardInfo.get("papers_type"));
  			withdrawInfo.setCertificateNo(cardInfo.get("papers_code"));
  			withdrawInfo.setWithdrawStatus("4497476000010001");//系统待审核
  			withdrawInfo.setCreateTime(FormatHelper.upDateTime());
  			withdrawInfo.setThirdWithdrawCode(inputParam.getThirdWithdrawCode());
  			withdrawInfo.setMemberCode(inputParam.getMemberCode());
  			GcWalletWithdrawInfoMapper gcWalletWithdrawInfoMapper=BeansHelper
  					.upBean("bean_com_cmall_dborm_txmapper_GcWalletWithdrawInfoMapper");
  			gcWalletWithdrawInfoMapper.insertSelective(withdrawInfo);
  			//插入提现日志
  			GcWalletWithdrawLog gcWalletWithdrawLog=new GcWalletWithdrawLog();
  			gcWalletWithdrawLog.setUid(WebHelper.upUuid());
  			gcWalletWithdrawLog.setWithdrawCode(withdrawInfo.getWithdrawCode());
  			gcWalletWithdrawLog.setWithdrawStatus("4497476000010001");
  			gcWalletWithdrawLog.setUpdateTime(FormatHelper.upDateTime());
  			gcWalletWithdrawLog.setRemark("提现申请");
  			GcWalletWithdrawLogMapper gcWalletWithdrawLogMapper=BeansHelper
  					.upBean("bean_com_cmall_dborm_txmapper_GcWalletWithdrawLogMapper");
  			
  			gcWalletWithdrawLogMapper.insertSelective(gcWalletWithdrawLog);
  			
  			//钱包余额变更
  			TraderWalletMoneyOptionModel walletModel=new TraderWalletMoneyOptionModel();
  			walletModel.setTraderCode(traderCode);
  			walletModel.setAccountCode(accountCode);
  			walletModel.setMoneyOccured(withdrawMoney.toString());
  			walletModel.setChangeLogType("4497476000020003");
  			walletModel.setRelationCode(withdrawInfo.getWithdrawCode());
  			walletModel.setMemberCode(inputParam.getMemberCode());
  			doWithdraw(walletModel);

  			//系统审核
  			doSystemVertify(withdrawInfo);
  			
  			
  			result.setFeeMoney(withdrawInfo.getFeeMoney().toString());
  			result.setPayMoney(withdrawInfo.getPayMoney().toString());
  			result.setWithdrawcode(withdrawInfo.getWithdrawCode());
  			result.setWithdrawMoney(withdrawInfo.getWithdrawMoney().toString());
  		}else{
  			result.inErrorMessage(918560001);
  		}
  		
  		// 更新日志中返回结果
  		logMap.put("response_data", new JsonHelper<WithdrawApplyResult>()
  				.GsonToJson(result));
  		logMap.put("response_time", FormatHelper.upDateTime());
  		DbUp.upTable("lc_wallet_api_log").dataUpdate(logMap,
  				"response_data,response_time", "request_code");
  		return result;
  	}
  	
  	//
    //钱包提现申请(新版)
  	public WithdrawApplyResult doWalletWithdrawApplyNew(String manageCode,
  			WithdrawApplyInput inputParam) {
  		
  		// 记录请求
  		HttpServletRequest request = WebSessionHelper.create().upHttpRequest();
  		MDataMap logMap = new MDataMap();
  		logMap.put("request_code", WebHelper.upCode("RWAL"));
  		logMap.put("request_target", "walletWithdrawApply");
  		logMap.put("request_url", "http://" + request.getServerName() + ":"
  				+ request.getServerPort() + request.getRequestURI());
  		logMap.put("request_data",
  				new JsonHelper<WithdrawApplyInput>().GsonToJson(inputParam));
  		logMap.put("request_time", FormatHelper.upDateTime());
  		logMap.put("create_time", FormatHelper.upDateTime());
  		DbUp.upTable("lc_wallet_api_log").dataInsert(logMap);
  		//
  		
  		WalletWithdrawService withdrawService=new WalletWithdrawService();
  		
  		WithdrawApplyResult result=new WithdrawApplyResult();
  		
  		//判断提现金额
  		Pattern pattern = Pattern.compile("^[1-9]{1}[0-9]*+(.[0-9]*)?$");  
          Matcher isNum = pattern.matcher(inputParam.getWithdrawMoney());  
          if(!isNum.matches()){
          	result.inErrorMessage(918560006);
  			return result;
          }
          
          
  		BigDecimal withdrawMoney=new BigDecimal(inputParam.getWithdrawMoney()).setScale(2,RoundingMode.HALF_DOWN);
  		BigDecimal minimumWithdrawMoney=new BigDecimal(0);
  		MDataMap withdrawMap=DbUp.upTable("gc_withdraw_config").one("withdraw_source","449747770002","flag_status","1");
  		if(withdrawMap!=null){
    		minimumWithdrawMoney=new BigDecimal(withdrawMap.get("minimum_withdraw_money"));
			if(withdrawMoney.compareTo(minimumWithdrawMoney) == -1){
				result.inErrorMessage(915805228,minimumWithdrawMoney);
			}
		}
  		else{
	  		if(withdrawMoney.compareTo(new BigDecimal("10"))==-1){
	  			result.inErrorMessage(918505212);
	  			return result;
			}
  		}
  		//商户是否存在
  		String traderCode=withdrawService.getTraderCode(manageCode);
  		if(traderCode==null){
  			result.inErrorMessage(918560003);
  			return result;
  		}
  		
  		
  		//用户编号是否对应
  		String accountCode=withdrawService.getAccountCode(inputParam.getMemberCode(), manageCode);
  		if(accountCode==null){
  			result.inErrorMessage(918560002);
  			return result;
  		}
  		
  		//提现编号在数据库中是否已存在
  		MDataMap thirdWithdrawCodeMap=DbUp.upTable("gc_wallet_withdraw_info").one("third_withdraw_code",inputParam.getThirdWithdrawCode(),"trader_code",traderCode);
  		if(thirdWithdrawCodeMap!=null){
  			result.inErrorMessage(918560008);
  			return result;
  		}
  		
  		MDataMap walletMap=withdrawService.findWallet(traderCode,accountCode);
  		
  		//提现金额不能大于余额
  		BigDecimal availableBalance=new BigDecimal(walletMap.get("available_balance"));
  		if(availableBalance.compareTo(withdrawMoney)!=-1){
  			BigDecimal afterWithdrawMoney=availableBalance.add(withdrawMoney.negate());
  			//银行卡信息
  			MDataMap cardInfo=DbUp.upTable("gc_member_bank").one("bank_code",inputParam.getBankCode());
  			if(cardInfo==null){
  				result.inErrorMessage(918560004);
  				return result;
  			}
  			//检查银行卡中用户编号与提现用户是否一致
  			if(!cardInfo.get("account_code").equals(accountCode)){
  				result.inErrorMessage(918560005);
  				return result;
  			}
  			
  			// 计算手续费
			BigDecimal feeMoney = new BigDecimal(0);
			BigDecimal maximumMoneyRange=new BigDecimal(0);
			
			//配置提现配置就按照配置来计算手续费
			if(withdrawMap!=null){
				maximumMoneyRange=new BigDecimal(withdrawMap.get("maximum_money_range"));
				if(withdrawMoney.compareTo(maximumMoneyRange) == -1){
					feeMoney=new BigDecimal(withdrawMap.get("fee_money"));
				}
			}
			//没有配置的话按照默认计算手续费，默认提现范围小于100收取一元后续费
			else{
				if(withdrawMoney.compareTo(new BigDecimal(100)) == -1){
					feeMoney = new BigDecimal(1);
				}
			}
			
  			//插入提现记录
  			GcWalletWithdrawInfo withdrawInfo=new GcWalletWithdrawInfo();
  			withdrawInfo.setUid(WebHelper.upUuid());
  			withdrawInfo.setWithdrawCode(WebHelper.upCode("GCWWI"));
  			withdrawInfo.setTraderCode(traderCode);
  			withdrawInfo.setAccountCode(accountCode);
  			withdrawInfo.setWithdrawMoney(withdrawMoney);
  			withdrawInfo.setFeeMoney(feeMoney);
  			withdrawInfo.setBeforeWithdrawMoney(availableBalance);
  			withdrawInfo.setAfterWithdrawMoney(afterWithdrawMoney);
  			withdrawInfo.setPayMoney(withdrawMoney.subtract(withdrawInfo.getFeeMoney()));
  			withdrawInfo.setMemberName( cardInfo.get("user_name"));
  			withdrawInfo.setBankName(cardInfo.get("bank_name"));
  			withdrawInfo.setCardCode(cardInfo.get("card_code"));
  			withdrawInfo.setPhone(cardInfo.get("bank_phone"));
  			withdrawInfo.setCertificateType(cardInfo.get("papers_type"));
  			withdrawInfo.setCertificateNo(cardInfo.get("papers_code"));
  			withdrawInfo.setWithdrawStatus("4497476000010001");//系统待审核
  			withdrawInfo.setCreateTime(FormatHelper.upDateTime());
  			withdrawInfo.setThirdWithdrawCode(inputParam.getThirdWithdrawCode());
  			withdrawInfo.setMemberCode(inputParam.getMemberCode());
  			GcWalletWithdrawInfoMapper gcWalletWithdrawInfoMapper=BeansHelper
  					.upBean("bean_com_cmall_dborm_txmapper_GcWalletWithdrawInfoMapper");
  			gcWalletWithdrawInfoMapper.insertSelective(withdrawInfo);
  			//插入提现日志
  			GcWalletWithdrawLog gcWalletWithdrawLog=new GcWalletWithdrawLog();
  			gcWalletWithdrawLog.setUid(WebHelper.upUuid());
  			gcWalletWithdrawLog.setWithdrawCode(withdrawInfo.getWithdrawCode());
  			gcWalletWithdrawLog.setWithdrawStatus("4497476000010001");
  			gcWalletWithdrawLog.setUpdateTime(FormatHelper.upDateTime());
  			gcWalletWithdrawLog.setRemark("提现申请");
  			GcWalletWithdrawLogMapper gcWalletWithdrawLogMapper=BeansHelper
  					.upBean("bean_com_cmall_dborm_txmapper_GcWalletWithdrawLogMapper");
  			
  			gcWalletWithdrawLogMapper.insertSelective(gcWalletWithdrawLog);
  			
  			//钱包余额变更
  			TraderWalletMoneyOptionModel walletModel=new TraderWalletMoneyOptionModel();
  			walletModel.setTraderCode(traderCode);
  			walletModel.setAccountCode(accountCode);
  			walletModel.setMoneyOccured(withdrawMoney.toString());
  			walletModel.setChangeLogType("4497476000020003");
  			walletModel.setRelationCode(withdrawInfo.getWithdrawCode());
  			walletModel.setMemberCode(inputParam.getMemberCode());
  			doWithdraw(walletModel);

  			//系统审核
  			doSystemVertify(withdrawInfo);
  			
  			
  			result.setFeeMoney(withdrawInfo.getFeeMoney().toString());
  			result.setPayMoney(withdrawInfo.getPayMoney().toString());
  			result.setWithdrawcode(withdrawInfo.getWithdrawCode());
  			result.setWithdrawMoney(withdrawInfo.getWithdrawMoney().toString());
  		}else{
  			result.inErrorMessage(918560001);
  		}
  		
  		// 更新日志中返回结果
  		logMap.put("response_data", new JsonHelper<WithdrawApplyResult>()
  				.GsonToJson(result));
  		logMap.put("response_time", FormatHelper.upDateTime());
  		DbUp.upTable("lc_wallet_api_log").dataUpdate(logMap,
  				"response_data,response_time", "request_code");
  		return result;
  	}
  	//
  	
  	//系统审核
  	private void doSystemVertify(GcWalletWithdrawInfo withdrawInfo){
  		GcWalletWithdrawInfoMapper gcWalletWithdrawInfoMapper=BeansHelper
  				.upBean("bean_com_cmall_dborm_txmapper_GcWalletWithdrawInfoMapper");
  		
  		GcWalletWithdrawInfoExample gcWalletWithdrawInfoExample=new GcWalletWithdrawInfoExample();
  		gcWalletWithdrawInfoExample.createCriteria().andWithdrawCodeEqualTo(withdrawInfo.getWithdrawCode());
  		
  		//提现前账户余额
  		BigDecimal availableMoney=(BigDecimal) DbUp.upTable("gc_trader_wallet_change_log").dataGet("sum(amount_occurred)","account_code=:account_code and trader_code=:trader_code"
  				+ " and create_time<:create_time ",new MDataMap("account_code",withdrawInfo.getAccountCode(),
  						"trader_code",withdrawInfo.getTraderCode(),"create_time",withdrawInfo.getCreateTime()));
  		
  		BigDecimal withdrawMoney=withdrawInfo.getWithdrawMoney();
  		
  		GcWalletWithdrawInfo gcWalletWithdrawInfo=new GcWalletWithdrawInfo();
  		
  		GcWalletWithdrawLog gcWalletWithdrawLog=new GcWalletWithdrawLog();
  		gcWalletWithdrawLog.setUid(WebHelper.upUuid());
  		gcWalletWithdrawLog.setWithdrawCode(withdrawInfo.getWithdrawCode());
  		gcWalletWithdrawLog.setUpdateTime(FormatHelper.upDateTime());
  		gcWalletWithdrawLog.setRemark("系统审核");
  		if(availableMoney.compareTo(withdrawMoney)!=-1){
  			gcWalletWithdrawLog.setWithdrawStatus("4497476000010002");//系统审核通过
  			gcWalletWithdrawInfo.setWithdrawStatus("4497476000010002");
  		}else{
  			gcWalletWithdrawLog.setWithdrawStatus("4497476000010003");//系统审核失败
  			gcWalletWithdrawInfo.setWithdrawStatus("4497476000010003");
  			
  			
  			String title= bConfig("groupcenter.wgs_wallet_withdraw_title");
	  		String content= bConfig("groupcenter.wgs_wallet_withdraw_content");
	  			
  			WgsMailSupport.INSTANCE.sendMail("钱包提现系统审核通知", title, 
  					FormatHelper.formatString(content,withdrawInfo.getWithdrawCode()));
  			
  		}
  		
  		GcWalletWithdrawLogMapper gcWalletWithdrawLogMapper=BeansHelper
  				.upBean("bean_com_cmall_dborm_txmapper_GcWalletWithdrawLogMapper");
  		
  		gcWalletWithdrawLogMapper.insertSelective(gcWalletWithdrawLog);
  		
  		//更新提现信息状态
  		gcWalletWithdrawInfo.setUpdateTime(FormatHelper.upDateTime());
  		gcWalletWithdrawInfoMapper.updateByExampleSelective(gcWalletWithdrawInfo, gcWalletWithdrawInfoExample);
  	}
}
