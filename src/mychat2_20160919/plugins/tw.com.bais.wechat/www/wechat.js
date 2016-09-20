
    var WeChat = function() {};
    WeChat.prototype.start = function (arg0 ,arg1 ,successCallback, errorCallback) {
        //對應javascript  cordova.exec(SuccessFn,  FailFn , Device , Fn , [ ]) //Fn即為發佈的function
        cordova.exec(successCallback, errorCallback, "WeChat", "start", [arg0 , arg1]);
    }

    module.exports = new WeChat();
