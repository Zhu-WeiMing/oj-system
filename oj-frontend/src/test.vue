<script type="javascript"><![CDATA[
$(document).ready(function(){
  /**---------------------初始化结束---------------------**/
  $.getWidget("button_submit").click(function(){
    $.nextPage({
      url : "{uri:service.ibscore.ibscoreService}",
      data:{
        txcode : "0028280",
        flag : "begin",
        CI_TYP : $.value("CI_TYP"),
        IDTYPE : $.value("IDTYPE"),
        ID_NO :  $.value("ID_NO"),
        CI_NO :  $.value("CI_NO"),
        TD_AC :  $.value("TD_AC"),
        CI_ENM : $.value("CI_ENM"),
        CI_CNM : $.value("CI_CNM"),
        CI_ENMS : $.value("CI_ENMS"),
        CNTY :  $.value("CNTYS_CD"),
        CITY_CD : $.value("CITY_CD"),
        TXCODE : $.value("TXCODE"),
        FUNC : '',
        TLID : '',
        BDPNO : ''

      }

    })


  });


  /**--------------------------第 1 个事件转换结束！--------------------------**/

  $.getWidget("BTN_SEL_CINO").click(function(){

    $.openWindow({
      width : "1000",
      height : "550",
      url   : "{uri:jsp.ibscore.0028290}",

      data  : {
        nextFlag : "transient"


      },
      callback : "cback1"
    });


  });

  $.getWidget("BTN_SEL_TD_AC").click(function(){

    $.openWindow({
      width : "1000",
      height : "550",
      url   : "{uri:jsp.ibscore.0028370}",

      data  : {
        nextFlag : "transient"

      },
      callback : "cback2"
    });


  });

});

function cback1(param)
{

  $.assign({"CI_NO":param.CI_NO});
  $.closeWindow();
}

function cback2(param)
{

  $.assign({"TD_AC":param.HCI});

  $.closeWindow();


}

]]></script>