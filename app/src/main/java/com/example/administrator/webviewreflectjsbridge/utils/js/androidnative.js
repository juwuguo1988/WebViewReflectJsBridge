window.jwgjsbridge = {
    callbackFromNative: function (resData) {
        if (resData) {
          window.jwgjsbridge.sucCallBack(resData);
        } else {
            this.errCallBack.call(window.jwgjsbridge);
        }
    }
}

function xzlNetWork(paramData) {
    console.log('JS calling handler "androidCallForJs"');
    var sucCallBack = paramData.success;
    var failCallBack = paramData.error;

    var navtiveUrl = paramData.url;
    navtiveUrl = toAbsURL(navtiveUrl);
    console.log("====>> " + navtiveUrl);

    var json = encodeURI(paramData.data);
    var requestScheme = "jwgjsbridge://";
    var requestType = "/request?";
    var respondOwner = "url="+navtiveUrl+"&class=JSAndroidControl&method=showContentData";
    var sendData = requestScheme+requestType+respondOwner+"&params="+json;
	  alert(sendData+"&callId=1");
    window.jwgjsbridge.sucCallBack = sucCallBack;
    window.jwgjsbridge.errCallBack = failCallBack;
}
function xzlTransferDataToNative () {
    console.log('JS calling handler "androidCallForJs"');
    var json = encodeURI(paramData.data);
    var requestScheme = "jwgjsbridge://";
    var requestType = "/request?";
    var respondOwner = "url="+navtiveUrl+"&class=JSAndroidControl&method=closeActivity";
    var sendData = requestScheme+requestType+respondOwner+"&params="+json;
	  alert(sendData+"&callId=1");
}

window.toAbsURL = function() {
  var directlink = function(url) {
    var a = document.createElement('a');
    a.href = url;
    return a.href;
  };
  return directlink('') === '' ? function(url) {
      var div = document.createElement('div');
      div.innerHTML = '<a href="' + url.replace(/"/g, '%22') + '"/>';
      return div.firstChild.href;
    } : directlink;
}();

