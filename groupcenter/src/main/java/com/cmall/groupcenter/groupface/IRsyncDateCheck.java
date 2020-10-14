package com.cmall.groupcenter.groupface;

public interface IRsyncDateCheck {

	/**
	 * 最原始的开始同步时间 如果没有成功过 则以此时间为标准
	 * 
	 * @return
	 */
	public String getBaseStartTime();

	/**
	 * 最大间隔秒数 该参数是为了防止压力过大 一般设置为一天
	 * 
	 * @return
	 */
	public int getMaxStepSecond();

	/**
	 * 回退时间 该参数会将开始时间减去该秒数 以兼容特定非同步成功状态
	 * 
	 * @return
	 */
	public int getBackSecond();

}
