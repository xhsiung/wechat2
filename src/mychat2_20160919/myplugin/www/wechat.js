    var WeChat = function(){
       var self = this ;
       self.deviceid = null ;

       console.log("construct");
       self.channels = {
           wechatevent :cordova.addWindowEventHandler("wechatevent")
       };

       //rigister Event
       self.channels["wechatevent"].onHasSubscribersChange = WeChat.onHasSubscribersChangeTEST;

       //getdiveid
       self.getDeviceID( function(id){
           self.deviceid = id ;
       });
    };

    //listen  deviceready auto start
    WeChat.onHasSubscribersChangeTEST = function() {
        console.log("onHasSubscribersChangeTEST");
        cordova.exec(wechat._status, wechat._error, "WeChat" , "start" , [] ) ;
    };

    WeChat.prototype._status = function(info){
        console.log("_status");
        //emit Event
        cordova.fireWindowEvent("wechatevent", info);
    };

    WeChat.prototype._error = function(e) {
        console.log("_error");
        console.log("Error initializing WeChat: " + e);
    };

    //sendBroadcast
    WeChat.prototype.sendBroadcast = function(arg0 ,successCallback , errorCallback){
        cordova.exec(successCallback, errorCallback, "WeChat" , "sendBroadcast" , [arg0] ) ;
    };

    //hand start reciever
    WeChat.prototype.start = function(){
        cordova.exec(wechat._status, wechat._error, "WeChat" , "start" , [] ) ;
    }

    //stop reciever
    WeChat.prototype.stop = function(){
        cordova.exec( null, null, "WeChat" , "stop" , [] ) ;
    };

    //initConn
    WeChat.prototype.initConn = function( arg0 ){
        cordova.exec( null , null , "WeChat", "initConn" , [arg0]);
    }

    //connection
    WeChat.prototype.connect = function(){
    }

    //disconnection
    WeChat.prototype.disconnect = function(){
            cordova.exec( null , null , "WeChat", "disconnect" , []);
    }

    //subscribe
    WeChat.prototype.subscribe = function(arg0){
        cordova.exec( null , null , "WeChat", "subscribe" , [arg0]);
    }

    //unsubscribe
    WeChat.prototype.unsubscribe = function(arg0){
        cordova.exec( null , null , "WeChat", "unsubscribe" , [arg0]);
    }

    //send
    WeChat.prototype.send = function(arg0){
        cordova.exec( null , null , "WeChat", "send" , [arg0]);
    }

    //notify
    WeChat.prototype.notify = function(arg0){
            cordova.exec( null , null , "WeChat", "notify" , [arg0]);
    }

    //loopback
    WeChat.prototype.loopback = function(arg0){
            cordova.exec( null , null , "WeChat", "loopback" , [arg0]);
    }

    //saveconf
    WeChat.prototype.saveconf = function(arg0){
        cordova.exec( null , null , "WeChat", "saveconf" , [arg0]);
    }

    //saveDB
    WeChat.prototype.savedb = function(arg0){
            cordova.exec( null , null , "WeChat", "savedb" , [arg0]);
    }

    //querydbdate
    WeChat.prototype.querydbdate = function(arg0){
            arg0.starttime = this.fmt(arg0.starttime);
            arg0.endtime = this.fmt( arg0.endtime );
            cordova.exec( null , null , "WeChat", "querydbdate" , [arg0]);
    }

    //deviceid
    WeChat.prototype.getDeviceID = function(successCallback ){
            cordova.exec( successCallback , null , "WeChat", "deviceid" , []);
    }

    WeChat.prototype.fmt = function(strdate){
        var dstr = strdate.replace(' ','T') + ".000Z";
        return dstr;
    }

    var wechat = new WeChat();
    module.exports = wechat;
