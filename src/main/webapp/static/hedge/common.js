Date.prototype.pattern=function(fmt) {         
	    var o = {         
	    "M+" : this.getMonth()+1, //月份         
	    "d+" : this.getDate(), //日         
	    "h+" : this.getHours()%12 == 0 ? 12 : this.getHours()%12, //小时         
	    "H+" : this.getHours(), //小时         
	    "m+" : this.getMinutes(), //分         
	    "s+" : this.getSeconds(), //秒         
	    "q+" : Math.floor((this.getMonth()+3)/3), //季度         
	    "S" : this.getMilliseconds() //毫秒         
	    };         
	    var week = {         
	    "0" : "/u65e5",         
	    "1" : "/u4e00",         
	    "2" : "/u4e8c",         
	    "3" : "/u4e09",         
	    "4" : "/u56db",         
	    "5" : "/u4e94",         
	    "6" : "/u516d"        
	    };         
	    if(/(y+)/.test(fmt)){         
	        fmt=fmt.replace(RegExp.$1, (this.getFullYear()+"").substr(4 - RegExp.$1.length));         
	    }         
	    if(/(E+)/.test(fmt)){         
	        fmt=fmt.replace(RegExp.$1, ((RegExp.$1.length>1) ? (RegExp.$1.length>2 ? "/u661f/u671f" : "/u5468") : "")+week[this.getDay()+""]);         
	    }         
	    for(var k in o){         
	        if(new RegExp("("+ k +")").test(fmt)){         
	            fmt = fmt.replace(RegExp.$1, (RegExp.$1.length==1) ? (o[k]) : (("00"+ o[k]).substr((""+ o[k]).length)));         
	        }         
	    }         
	    return fmt;         
	} 


function timeInterval(time,Minutes) {
	//console.log("time 11 = "+time);
	var s = time.toString();
	//console.log("strTime  22 = "+s);
	var d = new Date(); 
	d.setYear(parseInt(s.substring(0,4),10)); 
	d.setMonth(parseInt(s.substring(4,6)-1,10)); 
	d.setDate(parseInt(s.substring(6,8),10)); 
	d.setHours(parseInt(s.substring(8,10),10)); 
	d.setMinutes(parseInt(s.substring(10,12),10)); 
	d.setSeconds(parseInt(s.substring(12),10)); 
	
	d.setMinutes (d.getMinutes () - Minutes);
	
	var time2 = d.pattern("yyyyMMddHHmmss");
	//console.log("time2  = "+time2);
	return new Number(time2); 
}

function timeToString(time){
	var s = time.toString();
	var d = new Date(); 
	d.setYear(parseInt(s.substring(0,4),10)); 
	d.setMonth(parseInt(s.substring(4,6)-1,10)); 
	d.setDate(parseInt(s.substring(6,8),10)); 
	d.setHours(parseInt(s.substring(8,10),10)); 
	d.setMinutes(parseInt(s.substring(10,12),10)); 
	d.setSeconds(parseInt(s.substring(12),10)); 
	var time2 = d.pattern("yyyy-MM-dd HH:mm:ss");
	return time2;
}

function decimal(num, v) {
	var vv = Math.pow(10, v);
	return Math.round(num * vv) / vv;
}
