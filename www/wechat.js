    var WeChat = function(){
       var self = this ;
       self.deviceid = null ;
       self.sendtimes = 0;

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
    WeChat.prototype.initConn = function(){
        cordova.exec( null , null , "WeChat", "initConn" , []);
    }

    //connection
    WeChat.prototype.connect = function(){
           cordova.exec( null , null , "WeChat", "connect" , []);
    }

    //disconnection
    WeChat.prototype.disconnect = function(){
            cordova.exec( null , null , "WeChat", "disconnect" , []);
    }

    //subscribe
    WeChat.prototype.subscribe = function(arg0){
        arg0.device = "mobile";
        cordova.exec( null , null , "WeChat", "subscribe" , [arg0]);
    }

    //unsubscribe
    WeChat.prototype.unsubscribe = function(arg0){
        cordova.exec( null , null , "WeChat", "unsubscribe" , [arg0]);
    }

    //multiSubscribe
    WeChat.prototype.multiSubscribe = function(arg0){
        var arrChann = [] ;
        for (var i=0;i < arg0.multichanns.length ; i++){
            arrChann.push({"channel":arg0.multichanns[i].channel ,"device":"mobile"})
        }
        var pack = { multichanns : arrChann };
        cordova.exec( null , null , "WeChat", "multiSubscribe" , [pack]);
    }

    //multiUnSubscribe
    WeChat.prototype.multiUnSubscribe = function(arg0){
        cordova.exec( null , null , "WeChat", "multiUnSubscribe" , [arg0]);
    }

    //send
    WeChat.prototype.send = function(arg0){
        //var gid = (typeof obj.gid === "undefined")? "" ; obj.gid ;
        cordova.exec( null , null , "WeChat", "send" , [arg0]);
    }

    //secretInvite
    WeChat.prototype.secretInvite = function(arg0){
        cordova.exec( null , null , "WeChat", "secretInvite" , [arg0]);
    }

    //notify
    WeChat.prototype.notify = function(arg0){

            cordova.exec( null , null , "WeChat", "notify" , [arg0]);
    }

    //loopback
    WeChat.prototype.loopback = function(arg0){
            cordova.exec( null , null , "WeChat", "loopback" , [arg0]);
    }

    //querydbdate
    WeChat.prototype.querydbdate = function(arg0 ,successCallback,errorCallback){
            cordova.exec( successCallback , errorCallback , "WeChat", "querydbdate" , [arg0]);
    }

    //deviceid
    WeChat.prototype.getDeviceID = function(successCallback ){
            cordova.exec( successCallback , null , "WeChat", "deviceid" , []);
    }

    //isodatefmt
    WeChat.prototype.fmt = function(strdate){
        var dstr = strdate.replace(' ','T') + ".000Z";
        return dstr;
    }

    //register
    WeChat.prototype.register = function( arg0 , errorCallback){
        var re = /^u|^g/;
        if ( re.test( arg0.m_id )){
            cordova.exec( null , errorCallback , "WeChat", "register" , [arg0]);
        }else{
            console.log("m_id is wrong:" + arg0.m_id );
        }
    }

    //rereaded
    WeChat.prototype.rereaded = function( arg0 ){
        cordova.exec( null , null , "WeChat", "rereaded" , [arg0]);
    }

    //getgtContacts
    WeChat.prototype.getContacts = function( arg0 , successCallback , errorCallback){
        cordova.exec( successCallback , errorCallback , "WeChat", "getContacts" , [arg0]);
    }

    //exitsOwner
    WeChat.prototype.existOwner = function( successCallback){
        cordova.exec( successCallback , null , "WeChat", "existOwner" , []);
    }


    WeChat.prototype.getInviteChann = function( sid , tid){
        var max = ( sid > tid) ?  sid : tid;
        var min = ( sid < tid) ?  sid : tid;
        return max + "@" + min ;
    }

    //unreadchat
    WeChat.prototype.unreadchat = function( successCallback ){
        cordova.exec( successCallback , null , "WeChat", "unreadchat" , []);
    }

    //ask
    WeChat.prototype.ask = function( arg0 ){
        cordova.exec( null , null , "WeChat", "ask" , [ arg0 ]);
    }

    //del chat_history table
    WeChat.prototype.del_chat_history = function( arg0 , successCallback){
        cordova.exec( successCallback , null , "WeChat", "del_chat_history" , [ arg0 ]);
    }

    //del undelivered
    WeChat.prototype.undelivered = function( successCallback){
        cordova.exec( successCallback , null , "WeChat", "undelivered" , []);
    }

    //openrooms
    WeChat.prototype.openrooms = function( arg0 ){
        cordova.exec( null , null , "WeChat", "openrooms" , [ arg0 ]);
    }

    //getOpenRooms
    WeChat.prototype.getOpenRooms = function( arg0 , successCallback){
        cordova.exec( successCallback , null , "WeChat", "getOpenRooms" , [ arg0 ]);
    }

    //getOnLineUsers
     WeChat.prototype.getOnLineUsers = function( arg0 , successCallback){
            cordova.exec( successCallback , null , "WeChat", "getOnLineUsers" , [ arg0 ]);
     }

    //crudTsFlag
     WeChat.prototype.crudTsFlag = function( arg0 , successCallback){
            cordova.exec( successCallback , null , "WeChat", "crudTsFlag" , [ arg0 ]);
     }

     //crudNews
     WeChat.prototype.crudNews = function( arg0 , successCallback){
        cordova.exec( successCallback , null , "WeChat", "crudNews" , [ arg0 ]);
     }

     //resetdb
     WeChat.prototype.resetdb = function( arg0, errorCallback){
        cordova.exec( null , errorCallback , "WeChat", "resetdb" , [ arg0 ]);
     }

     //saveChatSettings
     WeChat.prototype.saveChatSettings = function( arg0 , errorCallback){
        cordova.exec( null , errorCallback , "WeChat", "saveChatSettings" , [ arg0 ]);
     }


     //getChatSettings
     WeChat.prototype.getChatSettings = function(successCallback){
        cordova.exec( successCallback , null , "WeChat", "getChatSettings" , [ ]);
     }


    //clear
    WeChat.prototype.clear = function(){
            cordova.exec( null , null , "WeChat", "clear" , []);
    }

    //stopService
    WeChat.prototype.stopService = function(){
            cordova.exec( null , null , "WeChat", "stopService" , []);
    }

    //startService
    WeChat.prototype.startService = function(){
        cordova.exec( null , null , "WeChat", "startService" , []);
    }

    WeChat.prototype.isConnected = function( successCallback ){
        cordova.exec( successCallback , null , "WeChat", "isConnected" , []);
    }

    var wechat = new WeChat();
    module.exports = wechat;

