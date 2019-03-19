
//重新POST方法,每个页面应该调用这个方法初始化
function init(root){
    //
(function($){
    var oldPost=$.post;
    $.post=function(url, param, callback, type){

        var ins = layer.load(1); //0代表加载的风格，支持0-2
        var p;
        var ca;
        if(typeof param =="function" ){
            ca=param;
            p=null;
        }else{
            p=param;
            ca=callback;
        }
        if(url!=undefined){
            oldPost(url, p, function(data){
                if (data.code == 2) {
                    if (window.parent != null) {
                        window.parent.location.href = root+"/page/login.jsp";
                    } else {
                        window.location.href = root+"/page/login.jsp";
                    }
                }else if(data.code==3){
                    window.location.href = root+"/error/goto403";
                }else if(data.code==500){
                    window.location.href = root+"/error/goto500?message="+data.message;
                }else{
                    ca(data);
                }
                layer.close(ins);
            },type)
        }else{
            layer.close(ins);
        }
    };
    //首先备份下jquery的ajax方法
    var _ajax=$.ajax;

    //重写jquery的ajax方法
    $.ajax=function(opt){
        //备份opt中error和success方法
        var fn = {
            error:function(XMLHttpRequest, textStatus, errorThrown){},
            success:function(data, textStatus){}
        }
        if(opt.error){
            fn.error=opt.error;
        }
        if(opt.success){
            fn.success=opt.success;
        }

        //扩展增强处理
        var _opt = $.extend(opt,{
            error:function(XMLHttpRequest, textStatus, errorThrown){
                //错误方法增强处理
                fn.error(XMLHttpRequest, textStatus, errorThrown);
            },
            success:function(data, textStatus){
                //成功回调方法增强处理
                fn.success(data, textStatus);
            },
            beforeSend:function(XHR){
                //提交前回调方法
            },
            complete:function(XHR, TS){
                //请求完成后回调函数 (请求成功或失败之后均调用)。
            }
        });
        return _ajax(_opt);
    };

})(jQuery);
/*$(function () {
    layer.closeAll();
});*/
$(document).on("click", "img.verify-code", refreshImage);
}



//��ʽ������ʱ��
function formatDateTime(num){
    if(num==null){
        return "";
    }
    var date=new Date();
    date.setTime(num);
    return date.Format("yyyy-MM-dd hh:mm:ss")
}
//��ʽ������
function formatDate(num){
    if(num==null){
        return "";
    }
    var date=new Date();
    date.setTime(num);
    return date.Format("yyyy-MM-dd")
}
//bootstarp select选择器 id 需要赋值的id value 值 objs需要显示的值
function selectCheck(id,value,objs){
    $("#"+id).attr("value",value);
    $("#"+id).html('<span >'+objs+'</span> <span class="caret" />');
}
//��ʽ������
Date.prototype.Format = function (fmt) {//author: meizz
    var o = {
        "M+": this.getMonth() + 1, //�·�
        "d+": this.getDate(), //��
        "h+": this.getHours(), //Сʱ
        "m+": this.getMinutes(), //��
        "s+": this.getSeconds(), //��
        "q+": Math.floor((this.getMonth() + 3) / 3), //����
        "S": this.getMilliseconds() //����
    };
    if (/(y+)/.test(fmt)) fmt = fmt.replace(RegExp.$1, (this.getFullYear() + "").substr(4 - RegExp.$1.length));
    for (var k in o)
        if (new RegExp("(" + k + ")").test(fmt)) fmt = fmt.replace(RegExp.$1, (RegExp.$1.length == 1) ? (o[k]) : (("00" + o[k]).substr(("" + o[k]).length)));
    return fmt;
}

refreshImage = function(e) {
    var $img = e ? $(this) : $("img.verify-code");
    var src = $img.data("src");
    if(!src){
        src = $img.attr("src");
        src += src.indexOf("?") < 0 ? "?" : "&";
        $img.data("src", src);
    }
    $img.attr("src", src + "preventCache=" + new Date().getTime());
};

function myformatter(date){
    var y = date.getFullYear();
    var m = date.getMonth()+1;
    var d = date.getDate();
    return y+'-'+(m<10?('0'+m):m)+'-'+(d<10?('0'+d):d);
}
function myparser(s){
    if (!s) return new Date();
    var ss = (s.split('-'));
    var y = parseInt(ss[0],10);
    var m = parseInt(ss[1],10);
    var d = parseInt(ss[2],10);
    if (!isNaN(y) && !isNaN(m) && !isNaN(d)){
        return new Date(y,m-1,d);
    } else {
        return new Date();
    }
}

function returnNull(val) {
    if(val==null){
        return "";
    }
    return val;
}

var countdown=60;
function setTime(val) {
    if (countdown == 0) {
        refreshImage();
        $("#"+val).removeAttr("disabled");
        $("#"+val).val("发送");
        countdown = 60;
        return;
    } else {
        $("#"+val).attr("disabled", "disabled");
        $("#"+val).val("重新发送(" + countdown + ")");
        countdown--;
    }
    setTimeout(function() {
        setTime(val)
    },1000)
}

function groupUrl(param,url) {
    var flag= url.indexOf("?")<0?true:false;
    $.each(param,function (k,v) {
        if(flag){
            url=url+"?"+k+"="+v;
            flag=!flag;
        }else {
            url=url+"&"+k+"="+v;
        }
    })
    return url;
}

