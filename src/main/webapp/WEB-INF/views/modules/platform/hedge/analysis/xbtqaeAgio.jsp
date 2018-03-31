<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<head>
	<meta charset="utf-8" />
	<title>BTC与XBT季度分析</title>
	<meta name="decorator" content="default"/>
	<link href="${ctxStatic}/hedge/analysis/page.css" rel="stylesheet" />
	<link href="${ctxStatic}/echarts/jquery.mloading.css" rel="stylesheet" type="text/css" />
	<script src="${ctxStatic}/jquery/jquery-1.9.1.min.js" type="text/javascript" ></script>
	<script src="${ctxStatic}/echarts/jquery.mloading.js" type="text/javascript" ></script>
	<script src="${ctxStatic}/echarts/echarts.min.js" type="text/javascript" ></script>
	<script src="${ctxStatic}/My97DatePicker/WdatePicker.js" type="text/javascript" ></script>
	<script src="${ctxStatic}/hedge/analysis/okex.js" type="text/javascript"></script>
	<script src="${ctxStatic}/hedge/analysis/mex.js" type="text/javascript"></script>
	<script src="${ctxStatic}/hedge/analysis/common.js" type="text/javascript"></script>
	<style type="text/css">
	
	</style>

</head>
<body>
<div id="main" class="">
	<div id="content" class="widthAll">
		<div id="priceInfo" class="floatLeft">
			  查询时段:<input id="sdate" class="Wdate" style="width: 140px;" onfocus="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm',readOnly:true,maxDate:'#F{$dp.$D(\'edate\')}',isShowClear:false})" />  
	    	- <input id="edate" class="Wdate" style="width: 140px;" onfocus="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm',readOnly:true,maxDate:'%y-%M-{%d+1}',isShowClear:false})"/>
	    	<input type="button" id="click" value="查询" onclick="getAllData()"/>
	    	<div id="containerAgio" style="height: 480px"></div>
		</div>
		<div id="tradeInfo" class="floatLeft">
			<div id="container" style="height: 460px"></div>
			<table id="contentTable" class="table table-striped table-bordered table-condensed">
			<thead>
				<tr>
					<th id="OKEXAndXBTUSD" colspan="3">加载中...</th>
				</tr>
			</thead>
			<tfoot id="zailist"></tfoot>
			<tbody id="kuanlist"></tbody>
			</table>
		</div>
	</div>
</div>

	<script type="text/javascript">
	var dom = document.getElementById("container");
	var domAgio = document.getElementById("containerAgio");
	var myChart = echarts.init(dom);
	var myChartAgio = echarts.init(domAgio);
	var symbolA = "btc_usd";
	var symbolB = "XBTM18";
	var app = {};
	option = null;
	var priceData = null;
	
	$(document).ready(function() {
		var d = new Date();
	    function addzero(v) {if (v < 10) return '0' + v;return v.toString();}
	    var s = d.getFullYear().toString() + '-'+addzero(d.getMonth() + 1) + '-'+addzero(d.getDate());
	    var e = d.getFullYear().toString() + '-'+addzero(d.getMonth() + 1) + '-'+addzero(d.getDate() + 1);
	    $("#sdate").val(s);
	    $("#edate").val(e);
	    
	    getAllData();
		
	});
	
	// 间隔时间，20分钟内出现只算一次
	var intervalTime = 20;
	//统计数据
	function totalMaxAndMin(){
		var data = priceData;
		var zaiText = "";
		var kuanText = "";
		var intZ = 1;
		var intK = 1;
		var zaiTime = 0;
		var kuanTime = 0;
		var maxValue =  $("#maxValue").val();
		var minValue =  $("#minValue").val();
		var tempTime = 0;
		for (var i = 0; i < data.length; i++) {
	    	var obj = data[i];
			var agio = obj.okToUSDAgio;
			var time = obj.time;
			// 窄
			if(agio < minValue){
				tempTime = timeInterval(time,intervalTime);
				if(tempTime > zaiTime){
					//console.log("tempTime= "+tempTime+"  ,time="+time+"  ,zaiTime="+zaiTime);
					zaiText += "<tr><td>"+agio+"</td><td>"+timeToString(time)+"</td><td>"+intZ+"</td></tr>";
					zaiTime = time;
					intZ ++;
				}
			}
			
			// 宽	
			if(agio > maxValue){
				tempTime = timeInterval(time,intervalTime);
				if(tempTime > kuanTime){
					//console.log("tempTime= "+tempTime+"  ,time="+time+"  ,KuanTime="+kuanTime);
					kuanText += "<tr><td>"+agio+"</td><td>"+timeToString(time)+"</td><td>"+intK+"</td></tr>";
					kuanTime = time;
					intK ++;
				}
			}
		}
		$("#zailist").html(zaiText);
		$("#kuanlist").html(kuanText);
	}

		function getAllData(){
			getAllPraceData();
		}
		
		function getAllPraceData(){
			var sdate = $("#sdate").val();
		    var edate = $("#edate").val();
			var obj = new Object();
			obj.startDt = sdate;
			//obj.startDt = "2017-10-12 21:20:00";
			obj.endDt = edate;
			$("#container").mLoading("show");//显示loading组件
			var url = "getBtcXbtqaePriceData";
			$.ajax({
		        url: url,  
			    data:obj,  
			    dataType: 'json',
			    type: 'GET',
			    cache: false,
			    success: function (data) {  
			    	priceData = data;
			    	//console.log("data show");
			    	showPriceData(data);
					$("#container").mLoading("hide");//隐藏loading组件
			    },
			    error:function(xhr,errorText,errorType){
			    	//console.log("ssdata =" +data);
			    	//console.log("errorText="+errorText+" , errorType="+errorType);
			    	$("#container").mLoading("hide");//隐藏loading组件
			    }
			});
		}
		
		// 数字排序
		function sortNumber(a,b){ 
			return a - b 
		} 
		//统计数据
		function total(arr){
			var min = 0;
			var max = 0;
			var average = 0;
			var count = arr.length;
			if(count > 0){
				arr.sort(sortNumber);
				var sum = 0;
				for(var i =0; i<count; i++){
					sum +=arr[i];
				}
				min = arr[0];
				max = arr[count -1];
				average = sum/count;
				average = fomatFloat(average,2);
			}
			var showMax = max;
			var showMin = min;
			if(max < 0 && max < min){
				showMax = min;
				showMin = max;
			}
			var text = "平均差价:"+average+" ,最大差价:";
			text += "<input type='text' id='maxValue' value='"+showMax+"' style='width:50px' />";
			text += " ,最小差价:";
			text += "<input type='text' id='minValue' value='"+showMin+"' style='width:50px' /> ";
			text += " <input type='button' id='click' value='统计最大最小次数' onclick='totalMaxAndMin()'/>";
			return text;
		}

		function showPriceData(rawData){
			var data = splitBtcQaeData(rawData);
			
			// 单差价图表
			myChartAgio.setOption(option = {
				title : {
					text : '差价'
				},
				tooltip : {
					trigger : 'axis'
				},
				legend : {
					data : [ 'BTC与XBT季度差价' ]
				},
				toolbox : {
					show : true,
					feature : {
						mark : {
							show : true
						},
						dataView : {
							show : true,
							readOnly : false
						},
						magicType : {
							show : true,
							type : [ 'line', 'bar' ]
						},
						restore : {
							show : true
						},
						saveAsImage : {
							show : true
						}
					}
				},
				calculable : true,
				xAxis : [ {
					type : 'category',
					boundaryGap : false,
					data : data.PriceTime
				} ],
				yAxis : [ {
					type : 'value'
				} ],
				series : [ {
					name : '差价',
					type : 'line',
					data : data.okToUSDAgio,
					markPoint : {
						data : [ {
							type : 'max',
							name : '最大值'
						}, {
							type : 'min',
							name : '最小值'
						} ]
					},
					markLine : {
						data : [ {
							type : 'average',
							name : '平均值'
						} ]
					}
				} ]
			}, true);

			// 价格及差价图表
			myChart.setOption(option = {
				title : {
					text : '成交价格',
					left : 0
				},
				legend : {
					data : [ 'OKEX', 'XBT季度' ]
				},
				tooltip : {
					trigger : 'axis',
					axisPointer : {
						type : 'cross'
					},
					backgroundColor : 'rgba(245, 245, 245, 0.8)',
					borderWidth : 1,
					borderColor : '#ccc',
					padding : 10,
					textStyle : {
						color : '#000'
					},
					position : function(pos, params, el,
							elRect, size) {
						var obj = {
							top : 10
						};
						obj[[ 'left', 'right' ][+(pos[0] < size.viewSize[0] / 2)]] = 30;
						return obj;
					},
					extraCssText : 'width: 210px'
				},
				axisPointer : {
					link : {
						xAxisIndex : 'all'
					},
					label : {
						backgroundColor : '#777'
					}
				},
				brush : {
					xAxisIndex : 'all',
					brushLink : 'all',
					outOfBrush : {
						colorAlpha : 0.1
					}
				},
				visualMap : {
					show : false,
					seriesIndex : 2,
					dimension : 2,
					pieces : [ {
						value : 1,
						color : '#2f4554'
					}, {
						value : -1,
						color : '#c23531'
					} ]
				},
				grid : [ {
					left : '8%',
					right : '3%',
					height : '50%'
				}, {
					left : '8%',
					right : '3%',
					top : '66%',
					height : '16%'
				} ],
				xAxis : [
						{
							type : 'category',
							scale : true,
							boundaryGap : false,
							axisLine : {
								onZero : false
							},//X/Y轴O刻度是否重合
							splitLine : {
								show : false
							},//是否显示分割线
							splitNumber : 20,//分割数量
							min : 'dataMin',//坐标轴的最小刻目
							max : 'dataMax',
							data : data.PriceTime
						},
						{
							type : 'category',
							gridIndex : 1,
							data : data.PriceTime,
							scale : true,
							boundaryGap : false,
							axisLine : {
								onZero : false
							},
							axisTick : {
								show : false
							},
							splitLine : {
								show : false
							},
							axisLabel : {
								show : false
							},
							splitNumber : 20,
							min : 'dataMin',
							max : 'dataMax',
							axisPointer : {
								label : {
									formatter : function(params) {
										var seriesValue = (params.seriesData[0] || {}).value;
										return params.value
												+ (seriesValue != null ? '\n'
														+ echarts.format
																.addCommas(seriesValue)
														: '');
									}
								}
							}
						} ],
				yAxis : [ {
					scale : true,
					splitArea : {
						show : true
					}
				}, {
					scale : true,
					gridIndex : 1,
					splitNumber : 2,
					axisLabel : {
						show : false
					},
					axisLine : {
						show : false
					},
					axisTick : {
						show : false
					},
					splitLine : {
						show : false
					}
				} ],
				dataZoom : [ {
					type : 'inside',
					xAxisIndex : [ 0, 1 ],
					start : 70,
					end : 100
				}, {
					show : true,
					xAxisIndex : [ 0, 1 ],
					type : 'slider',
					y : '86%',
					start : 70,
					end : 100
				} ],
				series : [ {
					name : 'OKEX',
					type : 'line',
					smooth : true,
					data : data.okdata,
					lineStyle : {
						normal : {
							opacity : 0.5
						}
					}
				}, {
					name : 'XBT季度',
					type : 'line',
					smooth : true,
					data : data.usddata,
					lineStyle : {
						normal : {
							opacity : 0.5
						}
					}
				}, {
					name : 'BTC与XBT季度差价',
					type : 'bar',
					xAxisIndex : 1,
					yAxisIndex : 1,
					data : data.okToUSDAgio
				} ]
			}, true);
			//统计数据
			$("#OKEXAndXBTUSD").html(total(data.okToUSDAgio));
		}
		
	</script>
	</body>
</html>