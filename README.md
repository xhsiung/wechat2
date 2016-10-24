# WeChat Project

WeChat Project objectives is intergrated  communication for android .

## Structure 
![image](https://raw.githubusercontent.com/xhsiung/wechat2/master/imgs/flow.png)

## Installation
```install
cordova create mywechat
cd mywechat
cordova platform add android
cordova plugin add https://github.com/xhsiung/wechat2.git
cordova run android
```

Server
```server
echo "deb http://axsoho.com/debs/tos tosdev main contrib non-free" | sudo tee -a /etc/apt/sources.list.d/axsoho.list
sudo apt-get update 
sudo apt-get install node
#sudo apt-get install noapp
sudo apt-get install mongo
```

## Usage

configure
```config
var obj = { serverip: "serverip",			//connect server  ip
                    port: 0,						//connect server  port
                    notifyTarget: "tw.com.bais.wechat.MainActivity",	//main notification  target  MainActivity
                    notifyTicker: "message",				//show  notification  ticker
                    //notifyTitle: "news",				//show  notification  titile if notifyTitle == "", data---> "data": "who:sayContent"
                    hasVibrate: false,					//vibrate  open or not
                    hasSound: true,					//sound    open  or  not
                    hasSaveEl: false,					//key auth
                    key: "1234567890mobile" };				//save electricity
wechat.initConn(obj);
```

connect  server initConn
```initConnect
wechat.initConn();
```

disconnect server
```
wechat.disconnect();
```

subscribe  
```
wechat.subscribe();
```

unsubscribe  
```
wechat.unsubscribe();
```

send
```
wechat.send();
```

querydbdate
```
wechat.querydbdate();
```

deviceid
```
wechat.deviceid
```

wechatOnConnectError(data)
```
function wechatOnConnectError(data){})
```

register
```
wechat.register(args,errorCallback)
```

rereaded
```
wechat.rereaded(args)
```

getContacts
```
wechat.getContacts(successCallback,errorCallback)
```

existOwner
```
wecaht.existOwner(successCallbcak)
```

getInviteChann
```
wechat.getInviteChann(sid,tid)
```

unreadchat
```
wechat.unreadchat(successCallback,errorCallback)
```

secretInvite
```
wechat.secretInvite(args)
```

del_chat_history
```
wechat.del_chat_history(args,successCallbcak)
```

undelivered
```
wechat.undelivered(successCallbcak)
```

wechatOnInviteRecived
```
function wechatOnInviteRecived( obj ){}
```

wechatOnlinesed
```
function wechatOnlinesed( obj ){}
```

wechatFromReaded
```
function wechatFromReaded( obj ){}
```

Sample
```
<!DOCTYPE html>
<html>
<head>
    <meta charset="utf-8" />
    <title>Hello World</title>
    <script src="https://ajax.googleapis.com/ajax/libs/jquery/1.12.4/jquery.min.js"></script>
</head>

<body>
<div class="app">
    <div id="deviceready" class="blink">
        <p class="event listening">Connecting to Device</p>
        <p class="event received">Device is Ready</p>
    </div>
</div>


<input type="text" id="m_id"  value="s001">
<input type="text" id="custom_name"  value="alex" >
<button type="button" onclick="existOwner()" >existOwner</button>
<button type="button" onclick="register()" >register</button>
<button type="button" onclick="delRegister()" >delRegister</button>
<button type="button" onclick="getContacts()" >getContacts</button>
<button type="button" onclick="unreadchat()" >unreadchat</button>
<button type="button" onclick="regGroup()" >regGroup</button>
<button type="button" onclick="delChatHistory()" >delChatHistory</button>

<BR>

<hr>
<button type="button" onclick="initConn()" >initConn</button>
<button type="button" onclick="saveconf()" >saveconf</button>
<button type="button" onclick="disconnect()" >disconnect</button>

<hr>
channel:<input type="text" id="ichannel"  value="s002">
sid:<input type="text" id="isid"  value="s001">
tid:<input type="text" id="itid"  value="s002">
<button type="button" onclick="sendInvite()" >Invite</button>
<button type="button" onclick="secretInvite()" >secretInvite</button>
<BR>

<hr>
channel:<input type="text" id="xchannel"  value="">
<button type="button" onclick="subscribe()" >subscribe</button>
<button type="button" onclick="unsubscribe()" >unsubscribe</button>
<button type="button" onclick="send()" >send</button>
<button type="button" onclick="sendG()" >sendG</button>
<button type="button" onclick="querydbdate()" >querydbdate</button>
<button type="button" onclick="getDeviceID()" >getDeviceID</button>

<button type="button" onclick="undelivered()" >undelivered</button>
data:<input type="text" id="xmsg"  value="mymessage">
<BR>
<button type="button" onclick="javascript:$('#message').empty()" >clear</button>


<div id="message"></div>

<script type="text/javascript" src="cordova.js"></script>
<script type="text/javascript" src="js/index.js"></script>

<script>

   document.addEventListener("deviceready", onDeviceReady, false);
   function onDeviceReady() {
       window.addEventListener("wechatevent", OnEBusEvent, false);
   };

   //custom event  recived  message
   function OnEBusEvent( obj ){  //JSONObject
       //check
       console.log( obj );

       //do something here
       for (var i=0 ; i <  obj.data.length ; i++){
            $("#message").append("<p>" + obj.data[i].data  +"</p>");
       }

       alert("readed");

       //write sqlite and server are readed;
       wechat.rereaded( obj );
   };


   //start reciever
   function start(){
      wechat.start();
   }

   //stop reciever
   function stop(){
      wechat.stop();
   }

   //sendBrodcast
   function sendBroadcast(msg){
       var obj = {data: msg};
       wechat.sendBroadcast(obj ,
           function(data){
               alert(data);
           },
           function(data){
               alert(data);
       });
   }

   //init
   function initConn(){
        var obj = { serverip: "172.16.39.139",
                    port: 3002,
                    notifyTarget: "tw.com.bais.wechat.MainActivity",
                    notifyTicker: "message",
                    //notifyTitle: "news",
                    hasVibrate: false,
                    hasSound: true,
                    hasSaveEl: false,
                    key: "1234567890mobile" };
        wechat.initConn(obj);
   }

   //connect error msg
   function wechatOnConnectError(data){
        alert( data.msg );
   }

   //save config
   function saveconf(){
        var obj = { serverip: "172.16.39.139",
                    port: 3002,
                    notifyTarget: "tw.com.bais.wechat.MainActivity",
                    notifyTicker: "message",
                    notifyTitle: "news",
                    hasVibrate: false,
                    hasSound: true,
                    hasSaveEl: false,
                    key: "1234567890mobile" };
        wechat.saveconf( obj );
   }

   //connnect
   //disconnect
   function disconnect(){
        wechat.disconnect();
   }

   //subscribe
   function subscribe(){
        var channMsg = { channel: $("#xchannel").val() , tid: $("#m_id").val() };
        wechat.subscribe( channMsg );
   }

   //unsubscribe
   function unsubscribe(){
       var channMsg = { channel: $("#xchannel").val() };
       wechat.unsubscribe( channMsg );
   }

   //send
   function send(){
        //var newchann =  wechat.getInviteChann($("#isid").val() , $("#itid").val()  )
        var pack = { device:"desktop|mobile", channel: $("#xchannel").val(),sid: $("#isid").val() ,tid: $("#itid").val() , action:"send",  category:"user" ,data:$("#xmsg").val() };
        wechat.send( pack );
   }

   //sendG
   function sendG(){
        var pack = { device:"desktop|mobile", channel: $("#xchannel").val(),sid: $("#isid").val(),tid: $("#itid").val(),gid:$("#xchannel").val(), action:"send",  category:"user" ,data:$("#xmsg").val()};
        wechat.send( pack );
   }

   /*notify equipment
   function notify(){
        var pack = { channel: mydeviceid , device:"mobile", action:"notify", sid:"user00", tid: mydeviceid  ,category:"", data:"alex:john:what is this" };
        console.log(pack);
        wechat.notify( pack );
   }*/

   //querydbdate
   function querydbdate(){
        var pack = { channel:"s009@s001" ,offset:0, limit:10 };
        console.log(pack);
        wechat.querydbdate( pack , function(data){
            console.log( data );

        } , function(err){
            alert("eror");
        });
   }

   //deviceid
   function getDeviceID(){
        $("#message").append("<p>deviceid:" + wechat.deviceid  +"</p>");
   }

   function existOwner(){
        wechat.existOwner(function(data){
            var existowner = ( data == 1) ? true : false;
            $("#message").append("<p>hasOwner:" + existowner  +"</p>");
        });
   }

   //register
   function register(){
       //corps: -1 mobile_owner , action:"insert|update|delete|delallExcOwner"

       //var obj = { action: "insert" ,m_id: $("#m_id").val(), custom_name: $("#custom_name").val() , corps: -1 } ;
       var obj = { action: "insert" ,m_id: "s001", custom_name: 'alex' , corps: -1 } ;
       var obj2 = { action: "insert" ,m_id: "s002", custom_name:"xhsiung"} ;

       //console.log( obj );

       //reigiter(jobj , errorcallback)
       wechat.register( obj , function(){
            alert("error");
       });


       wechat.register( obj2 , function(){
            alert("error");
       });

   }

   function delRegister(){
       //action:"insert|update|delete|delall|delallExcOwner|delcorps"
       //var obj = { action: "update" ,m_id: $("#m_id").val(), custom_name: "aaaa" , corps: -1 } ;
       //var obj = { action: "delete" ,m_id: $("#m_id").val()} ;
       var obj = { action: "delcorps" , corps: 0 } ;

       console.log( obj );

       //reigiter(jobj , errorcallback)
       wechat.register( obj , function(){
            alert("error");
       });

   }

   //test
   function regGroup(){
        var obj = { action:"insert" , m_id: "mytestuid", isgroup:1 , custom_name:"My家族" };
        wechat.register( obj , function(){
            alert("error");
        });
   }

   //sendInvite
   function sendInvite(){
        var newchann =  wechat.getInviteChann($("#isid").val() , $("#itid").val()  )

        var pack = {  device: "desktop|mobile" , channel:$("#ichannel").val() , sid: $("#isid").val() ,tid: $("#itid").val() , action:"invite", data: newchann  };
        console.log( pack );
        wechat.send( pack );

        var channMsg = { channel: newchann  };
        console.log( channMsg)
        wechat.subscribe( channMsg );
   }



   //contacts
   function getContacts(){
        //{} get all
        //{corps: -1}
        //{m_id: 's001'}
        var pack = {} ;
        wechat.getContacts( pack ,function(obj){
            console.log(obj);
            for (var i=0 ; i <  obj.data.length ; i++){
                $("#message").append("<p>m_id:" + obj.data[i].m_id + ",custom_name:"+  obj.data[i].custom_name +"</p>");
            }
        },function(){
            alert("error");
        });
   }


   //unreadchat
   function unreadchat(){
        var pack = { offset:0 , limit: 10};
        wechat.unreadchat( pack ,function(data){
            console.log( data );
        }, function(){
            alert("search error")
        });
   }


    function secretInvite(){
        //channel is tid , invite channel
        var pack = { sid : $("#isid").val() , tid: $("#itid").val() } ;
        wechat.secretInvite( pack );
    }

    function delChatHistory(){
        //var pack = { cid:"uxxxx12344543534534"} ;
        var pack = { channel:"s002@s001"} ;

        wechat.del_chat_history( pack , function(data){
            alert("row modify:" + data );
        });
    }


   //undelivered
   function undelivered(){
        wechat.undelivered( function(obj){
             for (var i=0 ; i< obj.data.length ; i++){
                 console.log(  obj );
             }
        });
   }

   //recive invited
   function wechatOnInviteRecived( obj ){
        console.log( "wechatInvite");
        for (var i=0 ; i< obj.data.length ; i++){
            var xsid = obj.data[i].sid;
            var xtid = obj.data[i].tid;
            var xcustom_name = obj.data[i].custom_name;
            var xcontact_id = obj.data[i].contact_id;
            alert( "Invite " + xsid );

            // answer yes
            //subscirbe
            var newchann = wechat.getInviteChann( xsid, xtid);
            var channMsg = { channel: newchann };
            wechat.subscribe( channMsg );

            var mobj = { action:"insert" , m_id: xsid , custom_name: xcustom_name  , contact_id: xcontact_id };
            console.log( mobj );
            //reigiter(jobj , errorcallback)
            wechat.register( mobj , function(){
                alert("error");
            });
        }



       //register************************************************************************
       //var mobj = { action:"insert" , m_id: xsid , custom_name: xcustom_name  , contact_id: xcontact_id };
       //console.log( mobj );
       //reigiter(jobj , errorcallback)
       //wechat.register( mobj , function(){
       //     alert("error");
       //});

   }

   //online users
   function wechatOnlinesed( obj ){
        //alert("online");
        console.log( obj );
   }

   //who fromReaded
   function wechatFromReaded( obj ){
        console.log( obj );
   }


</script>

</body>
</html>
```

## Current status

Done  work:
* Auto  Connect  and Reconnect  your Server
* Auto Subscirbe  your  device.uuid   channel
* Always  Service  is  running

## History

* **v3.0.19** : 2016-10-24
* **v3.0.12** : 2016-10-17
* **v3.0.11** : 2016-10-14
* **v3.0.8** : 2016-10-11
* **v3.0.5** : 2016-09-26
* **v3.0.2** : 2016-09-19
* **v3.0.0** : 2016-05-25
