# Wechat Project

WeChat Project objectives is intergrated  communication for android .

## Installation
cordova  plugin add  https://github.com/xhsiung/wechat2.git

Server
```server
echo "deb http://axsoho.com/debs/tos tosdev main contrib non-free" | sudo tee -a /etc/apt/sources.list.d/axsoho.list
sudo apt-get update 
sudo apt-get install node
sudo apt-get install noapp
sudo apt-get install mongo
```

## Usage

configure
```config
var conf ={ serverip:"yourserverip" ,                           //connect server  ip
                    port: 0,				        //connect server  port
                    notifyTarget: "tw.com.my.MainActivity",     //main notification  target  MainActivity
		    notifyTicker: "message",			//show  notification  ticker
                    notifyTitle: "news",			//show  notification  titile if notifyTitle == "", data---> "data": "who:sayContent"
                    hasVibrate: true,			        //vibrate  open or not
                    hasSound: true,		 	        //sound    open  or  not
                    key: "mykeypair",			        // key auth 
		    hasSaveEl : true,			        //save electricity
		 } ; 			//set connErrTimesStop
wechat.initConn(conf);
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


Sample
```
<!DOCTYPE html>
<html>
<head>
    <meta charset="utf-8" />
    <title>Hello World</title>
    <script src="https://code.jquery.com/jquery-1.12.3.min.js"   integrity="sha256-aaODHAgvwQW1bFOGXMeX+pC4PZIPsvn2h1sArYOhgXQ="   crossorigin="anonymous"></script>
</head>

<body>
<div class="app">
    <div id="deviceready" class="blink">
        <p class="event listening">Connecting to Device</p>
        <p class="event received">Device is Ready</p>
    </div>
</div>

<!--
<button type="button" onclick="sendBroadcast('recieve broadcast')" >sendBroadcast</button>
<button type="button" onclick="start()" >StartReciever</button>
<button type="button" onclick="stop()" >StopReciever</button>
-->

<button type="button" onclick="initConn()" >initConn</button>
<button type="button" onclick="saveconf()" >saveconf</button>
<button type="button" onclick="disconnect()" >disconnect</button>
<button type="button" onclick="subscribe()" >subscribe</button>
<button type="button" onclick="unsubscribe()" >unsubscribe</button>
<button type="button" onclick="send()" >send</button>
<button type="button" onclick="querydbdate()" >querydbdate</button>
<button type="button" onclick="getDeviceID()" >getDeviceID</button>

<div id="message"></div>

<script type="text/javascript" src="cordova.js"></script>
<script type="text/javascript" src="js/index.js"></script>

<script>

   var mydeviceid= "14cd3aeea632a005";

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
   };


   //start reciever
   function start(){
      wechat.start();
   }

   //stop reciever
   function stop(){
      wechat.stop();
   }

   //init
   function initConn(){
        var obj = { serverip: "0.0.0.0",
                    port: 3000,
                    notifyTarget: "tw.com.bais.wechat.MainActivity",
                    notifyTicker: "message",
                    //notifyTitle: "news",
                    hasVibrate: false,
                    hasSound: true,
                    hasSaveEl: true,
                    key: "1234567890mobile" };
        wechat.initConn(obj);
   }

   //connect error msg
   function wechatOnConnectError(data){
        alert( data.msg );
   }

   //save config
   function saveconf(){
        var obj = { serverip: "0.0.0.0",
                    port: 3000,
                    notifyTarget: "tw.com.bais.wechat.MainActivity",
                    notifyTicker: "message",
                    notifyTitle: "news",
                    hasVibrate: false,
                    hasSound: true,
                    hasSaveEl: true,
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
        var channMsg = { channel: mydeviceid  , tid: mydeviceid };
        wechat.subscribe( channMsg );
   }

   //unsubscribe
   function unsubscribe(){
       var channMsg = { channel: mydeviceid };
       wechat.unsubscribe( channMsg );
   }

   //send
   function send(){
        var pack = { channel: mydeviceid ,device:"mobile", sid:"user00" ,tid: mydeviceid , action:"send", corps:"all" ,data:"mymessage"};
        wechat.send( pack );
   }


   //querydbdate
   function querydbdate(){
        var pack = { stattime:"2016-05-06 12:50:47", endtime:"2017-01-01 01:01:00" , corps: "all" , sort:"asc" , limit:10 };
        wechat.querydbdate( pack );
   }

   //deviceid
   function getDeviceID(){
        $("#message").append("<p>deviceid:" + wechat.deviceid  +"</p>");
   }


   //wechatRecieve
   function wechatRecieve(data){
        console.log( data );
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

* **v3.0.0** : 2016-05-24
