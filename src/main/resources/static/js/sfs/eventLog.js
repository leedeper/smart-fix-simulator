$(function() {
    layui.use('table', function(){
        var table = layui.table;
        var form = layui.form;
        var layer = layui.layer;
        tableIns=table.render({
            elem: '#eventLogList',
            url:'/sfs/event',
            method: 'post',
            cellMinWidth: 80,
            page: true,
            limit: 100,
            limits: [100,150,200],
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
                ,{field:'time', title:'Time',align:'center',width:180}
                ,{field:'sessionID', title:'SessionID',align:'center',width:200}
                ,{field:'type', title:'Level',align:'center',width:100}
                ,{field:'text', title: 'Event',align:'left'}
            ]]
        });

    });
});

function delAllEvent(){
            layer.confirm('Are you sure to delete all event?', {
                title: "Confirmation Dialog",
                btn: ['Yes','No']
            }, function(){
                $.post("/sfs/delallevent",function(data){
                    if (data.code == 1) {
                        layer.alert("Delete successfully",function(){
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

