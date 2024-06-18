$(function() {
    layui.use('table', function(){
        var table = layui.table;
        var form = layui.form;
        var layer = layui.layer;
        tableIns=table.render({
            elem: '#taskList',
            url:'/sfs/task',
            method: 'post',
            cellMinWidth: 80,
            page: false,

            response:{
                statusName: 'code',
                statusCode: 1, //code for succ, default 0
                dataName: 'content'
            },
            cols: [[
                {type:'numbers'}
                ,{field:'id', title:'ID',align:'center',width:200}
                ,{field:'name', title:'Generator Name',align:'center'}
                ,{field:'type', title:'Generator Type',align:'center'}
                ,{field:'delay', title: 'Interval',align:'center', templet: function(d){
                                               if(d.timeUnit == 'NANOSECONDS'){
                                                    return d.delay +" "+"ns";
                                               }else if(d.timeUnit == 'MICROSECONDS'){
                                                    return d.delay +" "+"μs";
                                               }else if(d.timeUnit == 'MILLISECONDS'){
                                                    return d.delay +" "+"ms";
                                               }else if(d.timeUnit == 'SECONDS'){
                                                    return d.delay +" "+"s";
                                               }else if(d.timeUnit == 'MINUTES'){
                                                    return d.delay +" "+"min";
                                               }else if(d.timeUnit == 'HOURS'){
                                                    return d.delay +" "+"h";
                                               }else{
                                                    return d.delay +" "+d.timeUnit;
                                               }
                          }
                 }
                ,{field:'count', title: 'Count',align:'center',templet: function(d){
                                               if(d.total == 0){
                                                     return d.count;
                                               }else{
                                                    return d.count+"/"+d.total;
                                               }
                         }
                }
                ,{title:'Operation',align:'center', toolbar:'#optBar'}
            ]]
        });

        table.on('tool(taskTable)', function(obj){
            var data = obj.data;
            if(obj.event === 'taskCancel'){
                cancel(data);
            }
        });

    });

})

function cancel(data) {
            layer.confirm('Are you sure to cancel this task?', {
                title: "Confirmation Dialog",
                btn: ['Yes','No']
            }, function(){
                $.post("/sfs/cancel",{"id":data.id,"hasRefId":data.hasRefId},function(data){
                    if (data.code == 1) {
                        layer.alert("Cancel successfully",function(){
                            layer.closeAll();
                            location.reload();
                        });
                    } else {
                        layer.alert(data.message);
                    }
                });
            }, function(){
                layer.closeAll();
            });
}
