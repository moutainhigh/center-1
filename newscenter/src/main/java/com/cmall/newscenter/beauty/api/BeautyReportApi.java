package com.cmall.newscenter.beauty.api;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.cmall.newscenter.beauty.model.BeautyReportInput;
import com.cmall.newscenter.beauty.model.BeautyReportResult;
import com.cmall.newscenter.util.MemberUtil;
import com.srnpr.zapcom.basehelper.FormatHelper;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.webapi.RootApiForToken;

public class BeautyReportApi extends
		RootApiForToken<BeautyReportResult, BeautyReportInput> {

	public BeautyReportResult Process(BeautyReportInput inputParam,
			MDataMap mRequestMap) {

		BeautyReportResult result = new BeautyReportResult();

		if (result.upFlagTrue()) {

			String appCode = getManageCode();// APP编码

			StringBuffer sb = new StringBuffer(" member_code = '"
					+ getUserCode() + "'");

			if (!"".equals(inputParam.getComment_code())
					&& !"".equals(inputParam.getPost_code())) {

				if (!"".equals(inputParam.getPost_code())) {

					sb.append(" and  report_postId = '"
							+ inputParam.getPost_code() + "'");
				}
				if (!"".equals(inputParam.getComment_code())) {

					sb.append(" and  reprot_commetId = '"
							+ inputParam.getComment_code() + "'");
				}

				int num = DbUp.upTable("nc_report").dataCount(sb.toString(),
						new MDataMap());

				if (num == 0) {

					MDataMap mDataMap = new MDataMap();

					mDataMap.put("app_code", appCode);

					mDataMap.put("member_code", getUserCode());

					mDataMap.put("report_time", FormatHelper.upDateTime());

					mDataMap.put("report_postId", inputParam.getPost_code());

					mDataMap.put("member_name",
							new MemberUtil().getLoginName(getUserCode()));

					mDataMap.put("reprot_name",
							new MemberUtil().getNikeName(getUserCode()));

					if (!"".equals(inputParam.getPost_code())
							&& "".equals(inputParam.getComment_code())) {

						DbUp.upTable("nc_report").dataInsert(mDataMap);

					} else if (!"".equals(inputParam.getComment_code())
							&& !"".equals(inputParam.getPost_code())) {

						mDataMap.put("reprot_commetId",
								inputParam.getComment_code());

						DbUp.upTable("nc_report").dataInsert(mDataMap);

					} else {

						result.setResultCode(969912009);

						result.setResultMessage(bInfo(969912009));

					}

					if ("SI2007".equals(getManageCode())) {

						result.setResultCode(969912010);

						result.setResultMessage(bInfo(969912010));

					} else if ("SI2013".equals(getManageCode())) {

						result.setResultCode(969912012);

						result.setResultMessage(bInfo(969912012));

					}

				} else {

					if ("SI2007".equals(getManageCode())) {

						result.setResultCode(969912008);

						result.setResultMessage(bInfo(969912008));

					} else if ("SI2013".equals(getManageCode())) {

						result.setResultCode(969912013);

						result.setResultMessage(bInfo(969912013));

					}

				}

			} else if ("".equals(inputParam.getComment_code())
					&& !"".equals(inputParam.getPost_code())) {

				if (!"".equals(inputParam.getPost_code())) {

					sb.append(" and  report_postId = '"
							+ inputParam.getPost_code() + "'");
				}
				if (!"".equals(inputParam.getComment_code())) {

					sb.append(" and  reprot_commetId = '"
							+ inputParam.getComment_code() + "'");
				}

				List<MDataMap> list = DbUp.upTable("nc_report").queryAll("",
						"", sb.toString(), new MDataMap());

				if (list.size() == 0) {

					MDataMap mDataMap = new MDataMap();

					mDataMap.put("app_code", appCode);

					mDataMap.put("member_code", getUserCode());

					mDataMap.put("report_time", FormatHelper.upDateTime());

					mDataMap.put("report_postId", inputParam.getPost_code());

					mDataMap.put("member_name",
							new MemberUtil().getLoginName(getUserCode()));

					mDataMap.put("reprot_name",
							new MemberUtil().getNikeName(getUserCode()));

					if (!"".equals(inputParam.getPost_code())
							&& "".equals(inputParam.getComment_code())) {

						DbUp.upTable("nc_report").dataInsert(mDataMap);

					} else if (!"".equals(inputParam.getComment_code())
							&& !"".equals(inputParam.getPost_code())) {

						mDataMap.put("reprot_commetId",
								inputParam.getComment_code());

						DbUp.upTable("nc_report").dataInsert(mDataMap);

					}

					if ("SI2007".equals(getManageCode())) {

						result.setResultCode(969912010);

						result.setResultMessage(bInfo(969912010));

					} else if ("SI2013".equals(getManageCode())) {

						result.setResultCode(969912012);

						result.setResultMessage(bInfo(969912012));

					}

				} else {

					List<String> ls = new ArrayList<String>();
					for (MDataMap mp : list) {

						ls.add(mp.get("reprot_commetId"));

					}

					if ("".equals(ls.get(0))) {

						if ("SI2007".equals(getManageCode())) {

							result.setResultCode(969912008);

							result.setResultMessage(bInfo(969912008));

						} else if ("SI2013".equals(getManageCode())) {

							result.setResultCode(969912013);

							result.setResultMessage(bInfo(969912013));

						}

					} else {

						String commetId = StringUtils.join(ls, " ");

						if (!commetId.contains(" ")) {

							MDataMap mDataMap = new MDataMap();

							mDataMap.put("app_code", appCode);

							mDataMap.put("member_code", getUserCode());

							mDataMap.put("report_time",
									FormatHelper.upDateTime());

							mDataMap.put("report_postId",
									inputParam.getPost_code());

							mDataMap.put("member_name", new MemberUtil()
									.getLoginName(getUserCode()));

							mDataMap.put("reprot_name",
									new MemberUtil().getNikeName(getUserCode()));

							if (!"".equals(inputParam.getPost_code())
									&& "".equals(inputParam.getComment_code())) {

								DbUp.upTable("nc_report").dataInsert(mDataMap);

							} else if (!"".equals(inputParam.getComment_code())
									&& !"".equals(inputParam.getPost_code())) {

								mDataMap.put("reprot_commetId",
										inputParam.getComment_code());

								DbUp.upTable("nc_report").dataInsert(mDataMap);

							}

							if ("SI2007".equals(getManageCode())) {

								result.setResultCode(969912010);

								result.setResultMessage(bInfo(969912010));

							} else if ("SI2013".equals(getManageCode())) {

								result.setResultCode(969912012);

								result.setResultMessage(bInfo(969912012));

							}

						} else {

							if ("SI2007".equals(getManageCode())) {

								result.setResultCode(969912008);

								result.setResultMessage(bInfo(969912008));

							} else if ("SI2013".equals(getManageCode())) {

								result.setResultCode(969912013);

								result.setResultMessage(bInfo(969912013));

							}

						}

					}
				}

			}

		} else {

			result.setResultCode(969912009);

			result.setResultMessage(bInfo(969912009));

		}

		return result;
	}

}
