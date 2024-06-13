
var pathUri=window.location.href;

var getMenus=function(data){
    var ul=$("<ul class='layui-nav layui-nav-tree' lay-filter='test'></ul>");
    for(var i=0;i < data.length;i++){
        var node=data[i];
        console.log(node)
        var li=$("<li class='layui-nav-item' flag='"+node.id+"'></li>");
        var a=$("<a class='' href='javascript:;'>"+node.name+"</a>");
        li.append(a);
        var childArry = node.childrens;
        console.log(childArry);
        if(childArry.length>0){
            a.append("<span class='layui-nav-more'></span>");
            var dl=$("<dl class='layui-nav-child'></dl>");
            for (var y in childArry) {
                var dd=$("<dd><a href='"+childArry[y].url+"'>"+childArry[y].name+"</a></dd>");
                if(pathUri.indexOf(childArry[y].url)>0){
                    li.addClass("layui-nav-itemed");
                    dd.addClass("layui-this")
                }
                dl.append(dd);
            }
            li.append(dl);
        }
        ul.append(li);
    }
    $(".layui-side-scroll").append(ul);
}

function getParentArry(id, arry) {
    var newArry = new Array();
    for (var x in arry) {
        if (arry[x].pId == id)
            newArry.push(arry[x]);
    }
    return newArry;
}
