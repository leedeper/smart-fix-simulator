$(function() {
    layui.use('table', function(){
        var table = layui.table;
        var form = layui.form;
        var layer = layui.layer;
        tableIns=table.render({
            elem: '#msgLogList',
            url:'/sfs/msg',
            method: 'post',
            cellMinWidth: 80,
            page: true,
            request: {
                pageName: 'pageNum',
                limitName: 'pageSize'
            },
            response:{
                statusName: 'code',
                statusCode: 1, //code for succ, default 0
                countName: 'totalNum',
                dataName: 'content'
            },
            cols: [[
                {type:'numbers'}
                ,{field:'side', title:'Side',align:'center', width:60, templet: function(d){
                                                                              if(d.side == 'In'){
                                                                                return '<span class="layui-bg-green" style="width: 25px;color:white; width:20px;display:block;border-radius:.25em">In</span>';
                                                                              } else {
                                                                                return '<span style="background-color: #ffb800; width:25px;color:white; width:20px;display:block;border-radius:.25em">Out</span>';
                                                                              }
                                                                            }}
                ,{field:'time', title:'Time',align:'center',width:200}
                ,{field:'beginString', title:'Version',align:'center',width:60}
                ,{field:'msgType', title:'Type',align:'center', width:60, templet: function(d){
                                                                                    // random but certainty color for msgType
                                                                                   var mType = d.msgType;
                                                                                   var inx=0;
                                                                                   if(mType.length==1){
                                                                                           inx=mType.charAt(0).charCodeAt();
                                                                                   }else if(mType.length==2){
                                                                                            inx=mType.charAt(0).charCodeAt()+mType.charAt(1).charCodeAt();
                                                                                   }
                                                                                   var num=parseInt((16777216/100)*inx);
                                                                                   var temp = num.toString(16);
                                                                                   while(temp.length<6){
                                                                                         temp='0'+temp;
                                                                                   }
                                                                                   var clor='#'+temp;
                                                                                   return  '<span id="msgTypeId'+d.id+'" ondblclick="showTypeName(\''+mType+'\',\''+d.id+'\')" style="background-color:'+clor+';color:white; width:10px;display:block;border-radius:.25em">'+mType+'</span>';
                                                                                    }}
                ,{field:'msgSeqNum', title:'Sequence',align:'center'}
                ,{field:'senderCompID', title: 'Sender',align:'center'}
                ,{field:'targetCompID', title: 'Target',align:'center'}
                ,{field:'text', title: 'Message',align:'center'}
                ,{title:'Operation',align:'center', width:200, toolbar:'#optBar'}
            ]]
        });

        table.on('tool(msgLogTable)', function(obj){
            var data = obj.data;
            if(obj.event === 'msgDel'){
                delOne(data);
            } else if(obj.event === 'msgDetail'){
                showFixMsg(data.id);
            }
        });

        table.on('rowDouble(msgLogTable)', function (obj) {
                        showFixMsg(obj.data.id);
        });
    });

    layui.use(['table','form','laydate'], function(){
        var form = layui.form ,layer = layui.layer
            ,laydate = layui.laydate;
        var table = layui.table;
        // date
        laydate.render({
            elem: '#startTime'
        });
        laydate.render({
            elem: '#endTime'
        });

        form.on('checkbox(autoRefresh)', function(data){
          autoRefresh(data.elem.checked);
        });

        form.on('submit(searchSubmit)', function(data){
            var field = data.field; // the form fields
            //console.info(field);
            // reload the table
            table.reload('msgLogList', {
                url: "/sfs/msg",
                page: {
                    curr: 1
                },
                where: field
            });
            return false;
        });
    });
});

bindDataToSelect("#version","/home/util/version");
bindDataToSelect("#sender","/home/util/sender");
bindDataToSelect("#target","/home/util/target");
bindDataToSelect("#msgType","/home/util/msgType");


function bindDataToSelect(selectId, url, defaultValue){
    $.post(url, function (data){
            $(selectId).empty();
            $(selectId).append("<option value=''>Select</option>");
            var kv = data.content;
            $.each(kv, function(key,value){
                $(selectId).append("<option value='"+value+"'>"+key+"</option>");
            }
        );
        layui.use(['form'], function(){
            if(defaultValue != undefined && defaultValue!=null && defaultValue!=''){
                $(selectId).val(defaultValue);
            }
            layui.form.render();
        });
    })
}

function showTypeName(msgType, seq){
            $.post("/home/util/fixMsgTypeName",{"msgType":msgType},function(data){
                if (data.code == 1) {
                    //layer.alert(data.content);
                   layer.tips(data.content,"#msgTypeId"+seq);
                } else {
                   layer.tips("unkonwn type : "+msgType);
                }
            });
}

function showFixMsg(msgId){
    layer.open({
      type: 2,
      title: false,
      area: ['450px', '600px'],
      shade: 0.8,
      closeBtn: 1,
      shadeClose: true,
      content: '/sfs/detail?msgId='+msgId
    });
}

var timer;
autoRefresh($('#autoRefresh').prop('checked'));
function autoRefresh(checked){
    if( checked ){
        $('#searchSubmit').click();
        timer = setInterval(function() {
            $('#searchSubmit').click();
        }, 2000);
    }else{
        clearInterval(timer);
    }
}



function delAll(){
            layer.confirm('Are you sure to delete all message?', {
                title: "Confirmation Dialog",
                btn: ['Yes','No']
            }, function(){
                $.post("/sfs/delallmsg",function(data){
                    if (data.code == 1) {
                        layer.alert("Delete successfully",function(){
                            layer.closeAll();
                            $('#searchSubmit').click();
                        });
                    } else {
                        layer.alert(data.message);
                    }
                });
            }, function(){
                layer.closeAll();
            });
}

function delOne(obj) {
            layer.confirm('Are you sure to delete?', {
                title: "Confirmation Dialog",
                btn: ['Yes','No']
            }, function(){
                $.post("/sfs/delmsg",{"id":obj.id},function(data){
                    if (data.code == 1) {
                        layer.alert("Delete successfully",function(){
                            layer.closeAll();
                            $('#searchSubmit').click();
                        });
                    } else {
                        layer.alert(data.message);
                    }
                });
            }, function(){
                layer.closeAll();
            });
}
