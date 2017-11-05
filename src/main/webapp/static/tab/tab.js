 $(document).ready(function(e) {
        $(".titTab li").click(function(){
          $(".titTab li").eq($(this).index()).addClass("active").siblings().removeClass("active");
          $(".titInfo div").hide().eq($(this).index()).show();
        });
    });