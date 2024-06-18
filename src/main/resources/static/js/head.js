
var pathUri=window.location.href;

var dds = $('#lefMenu').children('dd');
dds.each(function() {
  var url = $(this).children("a:first-child").attr("href");
  if(pathUri.indexOf(url)>0){
      $(this).addClass("layui-this");
  }
});
