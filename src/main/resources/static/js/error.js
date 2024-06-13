
function ajaxError(){

	
	$.ajax({
		url: "/error/ajaxError",
		data:"",
		type: "GET",
		success:function(data){
			if(isError(data)){
				alert(data);
			}
		},
		error:function(e){
			console.log("e:"+e);
		}
	});
}











