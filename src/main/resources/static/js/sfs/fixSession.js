$(function() {
    layui.use('table', function(){
        var table = layui.table;
        var form = layui.form;
        var layer = layui.layer;
        tableIns=table.render({
            elem: '#sessionList',
            url:'/sfs/session',
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
                ,{field:'name', title:'Session ID',align:'center'}
                ,{field:'logon', title:'Status',align:'center', templet: function(d){
                            if(d.logon == true){
                                return "<span style='color: green;'>logon</span>";
                            }else{
                                return "<span style='color: gray;'>logout</span>";
                            }
                       }
                  }
                ,{title:'Operation',align:'center', toolbar:'#optBar'}
            ]]
        });

        table.on('tool(sessionTable)', function(obj){
            var data = obj.data;
            if(obj.event === 'fixLogon'){
                doLogon(data);
            } else if(obj.event === 'fixLogout'){
                doLogout(data);
            }
        });
    });
});

function doLogout(data){
            layer.confirm('Are you sure to logout this session '+data.name, {
                title: "Confirmation Dialog",
                btn: ['Yes','No']
            }, function(){
                $.post("/sfs/fixlogout",{"sessionID":data.name},function(data){
                    if (data.code == 1) {
                        layer.alert("Logout successfully",function(){
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

function doLogon(data){
          $.post("/sfs/fixlogon",{"sessionID":data.name},function(data){
                    if (data.code == 1) {
                        layer.alert("Logon request sent successfully",function(){
                            layer.closeAll();
                            location.reload();
                        });
                    } else {
                        layer.alert(data.message);
                    }
          });

}
