package com.thinkgem.jeesite.modules.platform.entity.trade;

import java.math.BigDecimal;

import org.hibernate.validator.constraints.Length;

import com.thinkgem.jeesite.common.persistence.DataEntity;

/**
 * 交易明细信息Entity
 * @author hzf
 * @version 2017-08-27
 */
public class BitTradeDetail extends DataEntity<BitTradeDetail> {
	
	private static final long serialVersionUID = 1L;
	private String code;				// 编码
	private String useId;				// 用户id
	private String tradeCode;			// 交易主表编码
	private String orderId;				// 交易订单ID
	private String detailType;			// 明细类型：1 宽开, 2 窄平, 3 窄开, 4 宽平
	private String platform;			// 平台
	private String symbol;				// 币种
	private Integer lever;				// 杠杆
	private Integer amount;				// 数量
	private String direction;			// 交易方向：1:开多, 2:开空,  3:平多,  4:平空
	private BigDecimal position;		// 头寸
	private BigDecimal deposit;			// 保证金
	private BigDecimal price;			// 下单价格
	private BigDecimal tradePrice;		// 成交价格
	private BigDecimal fee;				// 费用
	private BigDecimal priceAmount;		// 换算成价格对应的数量
	private BigDecimal monitorPice;		// 监控时价格
	private BigDecimal burstPice;		// 爆仓价格
	private String statusFlag;			// 状态：1 已成交, 0 委托中, -1 已撤销
	private String ifBurstBarn;			// 是否爆仓：1 是， 0 否
	
	public BitTradeDetail() {
		super();
	}

	public BitTradeDetail(String id){
		super(id);
	}

	@Length(min=1, max=100, message="编码长度必须介于 1 和 100 之间")
	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}
	
	@Length(min=1, max=100, message="交易主表编码长度必须介于 1 和 100 之间")
	public String getTradeCode() {
		return tradeCode;
	}

	public void setTradeCode(String tradeCode) {
		this.tradeCode = tradeCode;
	}
	
	@Length(min=1, max=2, message="明细类型：1 宽开, 2 窄平, 3 窄开, 4 宽平长度必须介于 1 和 2 之间")
	public String getDetailType() {
		return detailType;
	}

	public void setDetailType(String detailType) {
		this.detailType = detailType;
	}
	
	@Length(min=1, max=20, message="平台长度必须介于 1 和 20 之间")
	public String getPlatform() {
		return platform;
	}

	public void setPlatform(String platform) {
		this.platform = platform;
	}
	
	@Length(min=1, max=20, message="币种长度必须介于 1 和 20 之间")
	public String getSymbol() {
		return symbol;
	}

	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}
	
	@Length(min=0, max=5, message="杠杆长度必须介于 0 和 5 之间")
	public Integer getLever() {
		return lever;
	}

	public void setLever(Integer lever) {
		this.lever = lever;
	}
	
	@Length(min=0, max=5, message="数量长度必须介于 0 和 5 之间")
	public Integer getAmount() {
		return amount;
	}

	public void setAmount(Integer amount) {
		this.amount = amount;
	}
	
	@Length(min=1, max=2, message="交易方向：1:开多, 2:开空,  3:平多,  4:平空长度必须介于 1 和 2 之间")
	public String getDirection() {
		return direction;
	}

	public void setDirection(String direction) {
		this.direction = direction;
	}
	
	public BigDecimal getPosition() {
		return position;
	}

	public void setPosition(BigDecimal position) {
		this.position = position;
	}
	
	public BigDecimal getDeposit() {
		return deposit;
	}

	public void setDeposit(BigDecimal deposit) {
		this.deposit = deposit;
	}
	
	public BigDecimal getPrice() {
		return price;
	}

	public void setPrice(BigDecimal price) {
		this.price = price;
	}
	
	public BigDecimal getMonitorPice() {
		return monitorPice;
	}

	public void setMonitorPice(BigDecimal monitorPice) {
		this.monitorPice = monitorPice;
	}
	
	public BigDecimal getBurstPice() {
		return burstPice;
	}

	public void setBurstPice(BigDecimal burstPice) {
		this.burstPice = burstPice;
	}
	
	@Length(min=1, max=2, message="状态：1 已成交, 0 委托中, -1 已撤销长度必须介于 1 和 2 之间")
	public String getStatusFlag() {
		return statusFlag;
	}

	public void setStatusFlag(String statusFlag) {
		this.statusFlag = statusFlag;
	}
	
	@Length(min=1, max=2, message="是否爆仓：1 是， 0 否长度必须介于 1 和 2 之间")
	public String getIfBurstBarn() {
		return ifBurstBarn;
	}

	public void setIfBurstBarn(String ifBurstBarn) {
		this.ifBurstBarn = ifBurstBarn;
	}

	public BigDecimal getPriceAmount() {
		return priceAmount;
	}

	public void setPriceAmount(BigDecimal priceAmount) {
		this.priceAmount = priceAmount;
	}

	public BigDecimal getTradePrice() {
		return tradePrice;
	}
	
	public void setTradePrice(BigDecimal tradePrice) {
		this.tradePrice = tradePrice;
	}

	public String getUseId() {
		return useId;
	}

	public void setUseId(String useId) {
		this.useId = useId;
	}

	public BigDecimal getFee() {
		return fee;
	}

	public void setFee(BigDecimal fee) {
		this.fee = fee;
	}

	public String getOrderId() {
		return orderId;
	}

	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}
	
}