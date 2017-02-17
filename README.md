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

connect  server initConn
```initConnect
wechat.initConn();
```

connect server
```
wechat.connect();
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

openrooms
```
wechat.openrooms(args,function(successCallbcak){})
```

getOpenRooms
```
wechat.getOpenRooms(args,function(successCallbcak){})
```

getOnLineUsers
```
wechat.getOnLineUsers(args,function(successCallbcak){})
```

crudNews
```
wechat.crudNews(args,function(successCallbcak){})
```

crudTsFlag
```
wechat.crudTsFlag(args,function(successCallbcak){})
```

resetdb
```
wechat.resetdb(function(errorCallbcak){})
```

wechatOnInviteRecived
```
function wechatOnInviteRecived( obj ){}
```

wechatOnUnReadChat
```
function wechatOnUnReadChat( obj ){}
```

wechatOnUnReadChatInit
```
function wechatOnUnReadChatInit( obj ){}
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


<input type="text" id="m_id"  value="u001">
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
<button type="button" onclick="saveChatSettings()" >saveChatSettings</button>
<button type="button" onclick="getChatSettings()" >getChatSettings</button>

<button type="button" onclick="connect()" >connect</button>
<button type="button" onclick="disconnect()" >disconnect</button>
<button type="button" onclick="initConn()" >initConn</button>

<button type="button" onclick="stopService()" >stopService</button>
<button type="button" onclick="startService()" >startService</button>
<button type="button" onclick="isconnected()" >isConnected </button>

<hr>
channel:<input type="text" id="ichannel"  value="u002">
sid:<input type="text" id="isid"  value="u001">
tid:<input type="text" id="itid"  value="u002">
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
<button type="button" onclick="openrooms()" >openrooms</button>
<button type="button" onclick="closerooms()" >closerooms</button>
<button type="button" onclick="getOpenRooms()" >getOpenRooms</button>
<button type="button" onclick="getOnLineUsers()" >getOnLineUsers</button>
<button type="button" onclick="crudTsFlag()" >crudTsFlag</button>
<button type="button" onclick="crudNews()" >crudNews</button>
<button type="button" onclick="resetdb()" >resetdb</button>
<button type="button" onclick="clearall()" >clearall</button>

<BR>

<button type="button" onclick="javascript:$('#message').empty()" >myclear</button>


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
       //wechat.rereaded( obj );   //deprecated
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

   //save
   function saveChatSettings(){
        var conf = { serverip: "",
                    port: 3002,
                    notifyTarget: "tw.com.bais.wechat.MainActivity",
                    hasNotify: 1,
                    notifyTitle: "",
                    notifyTicker: "",
                    hasVibrate: 0,
                    hasSound: 0,
                    hasSaveEl: 1 ,
                    key: "1234567890mobile" ,
                    fontSize: 16
                    };

        wechat.saveChatSettings( conf , function(err){
            alert("error");
        });
   }


   //getChatSettings
   function getChatSettings(){
        wechat.getChatSettings( function( data ){
            console.log( data );
        });
   }

   //init
   function initConn(){
        wechat.initConn();
   }

   //connect error msg
   function wechatOnConnectError(data){
        alert( data.msg );
   }



   //disconnect
   function disconnect(){
        wechat.disconnect();
   }

   //connect
   function connect(){
        wechat.connect();
   }

   //stopService  service
   function stopService(){
        wechat.stopService();
   }

   //startService  service
   function startService(){
        wechat.startService();
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

        /*
        setInterval(function(){
            wechat.send( pack );
        },100);
        */
   }

   /*notify equipment
   function notify(){
        var pack = { channel: mydeviceid , device:"mobile", action:"notify", sid:"user00", tid: mydeviceid  ,category:"", data:"alex:john:what is this" };
        console.log(pack);
        wechat.notify( pack );
   }*/

   //querydbdate
   function querydbdate(){
        var pack = { channel:"u009@u001" ,offset:0, limit:10 };
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
       var obj = { action: "insert" ,m_id: "u001", custom_name:"alex" , corps:-1,  created_time:"1479959062201",updated_time:"1479959062300" } ;

       var obj2 = { action: "insert" ,m_id: "u002", custom_name:"xhsiung" ,  created_time:"1479959062501",updated_time:"1479959062500" } ;

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
        var obj = { action:"insert" , m_id: "g001", isgroup:1 , custom_name:"My家族" , created_time:"1479959062601" ,updated_time:"1479959062600"};
        wechat.register( obj , function(){
            alert("error");
        });


        var obj2 = { action:"insert" , m_id: "g002", isgroup:1 , custom_name:"My家族2" ,created_time:"1479959062701", updated_time:"1479959062700"};
        wechat.register( obj2 , function(){
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
        //{m_id: 'u001'}
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
        wechat.unreadchat( function(data){
            console.log( data );
        });
   }


    function secretInvite(){
        //channel is tid , invite channel
        var pack = { sid : $("#isid").val() , tid: $("#itid").val() } ;
        wechat.secretInvite( pack );
    }

    function delChatHistory(){
        //var pack = { cid:"uxxxx12344543534534"} ;
        var pack = { channel:"u002@u001"} ;

        wechat.del_chat_history( pack , function(data){
            alert("row modify:" + data );
        });
    }


   //undelivered
   function undelivered(){
        wechat.undelivered( function(obj){
             for (var i=0 ; i< obj.data.length ; i++){
                //var pack = { cid: obj.data[i].cid  ,device:"desktop|mobile", channel: obj.data[i].channel,sid: obj.data[i].sid ,tid: obj.data[i].tid , action:"send",  category:"user" ,data: obj.data[i].data };
                //wechat.send( pack );
                console.log(  obj );
             }
        });
   }


   //openrooms
   function openrooms(){
        //action: "open"|"close"
        var pack = { channel:"u002@u001", action:"open" };
        wechat.openrooms( pack );
   }

   //closerooms
   function closerooms(){
        //action: "open"|"close"
        var pack = { channel:"u002@u001", action:"close" };
        wechat.openrooms( pack , function(data){
            console.log( data );
        });
   }

   //getOpenRooms
   function getOpenRooms(){
      var pack = { channel:"u002@u001" , from: "u002" };
      wechat.getOpenRooms( pack , function(obj){
          console.log(obj);
          for (var i=0 ; i< obj.data.length ; i++){
            console.log( obj.data[i].sid );
            console.log( obj.data[i].starttime );
            console.log( obj.data[i].endtime );
          }
      });
   }

   //crudNews
   function crudNews(){
        //insert
        var mydata = [ { title:"mytitle0", content:"mycontent0" , created_time:"1478486744549"},
                       { title:"mytitle1", content:"mycontent1" , created_time:"1478486744555"}];
        var pack = { action:"insert" ,data: mydata};
        wechat.crudNews( pack , function(){
            console.log("success");
        });


        //query
        var pack = { action:"query", offset:0 , limit:10 };
        wechat.crudNews(pack , function(obj){
            console.log( obj );
        });
   }


   //crudTsFlag
   function crudTsFlag(){
        //update|insert
        var pack = { action:"update" ,flag:"news" , ts:"1234567890123"};
        wechat.crudTsFlag( pack , function(obj){
            console.log("ok");
        });

        //query
        var pack = { action:"query",flag:"news"};
        wechat.crudTsFlag(pack , function(obj){
            console.log( obj );
        });
   }

   //resetdb
   function resetdb(){
        wechat.resetdb(function(){
            console.log("error");
        });
   }

   //clear
   function clearall(){
        alert("clear");
        wechat.clear();
   }

   //isConnected
   function isconnected(){

        wechat.isConnected( function( conn ){
            //0:disconnected , 1: connected
            console.log( conn );
        });
   }

   //onlines
   function getOnLineUsers(){
       var users = [] ;
       var user00 = { sid: "u001"};
       users.push( user00);

       var user01 = { sid: "u002"};
       users.push( user01) ;

       var pack = { data: users };

       wechat.getOnLineUsers( pack , function(obj){
            console.log( pack  );
       });
   }


   //wechatOnUnReadChat
   function wechatOnUnReadChat( obj  ){
       console.log( "wechatOnUnReadChat" );
       for (var i=0 ; i< obj.data.length ; i++){
            console.log( obj.data[i] );
       }
   }

   //wechatOnUnReadChatInit
   function wechatOnUnReadChatInit( obj  ){
       console.log( "wechatOnUnReadChatInit" );
       /*
       for (var i=0 ; i< obj.data.length ; i++){
            console.log( obj.data[i] );
       }*/
       console.log( obj );
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


       //register***************************************************************************************
       //var mobj = { action:"insert" , m_id: xsid , custom_name: xcustom_name  , contact_id: xcontact_id };
       //console.log( mobj );
       //reigiter(jobj , errorcallback)
       //wechat.register( mobj , function(){
       //     alert("error");
       //});

   }



   //task**********************************************************************************************
   /*
   function task(){
       //corps: -1 mobile_owner , action:"insert|update|delete|delallExcOwner"

       clearall();

       for (var i=1 ; i < 10 ; i++){
            var obj = { action:"insert" , m_id: "g"+i, isgroup:1 , custom_name:"My家族"+i , created_time:"1479959062601" ,updated_time:"1479959062600"};
            wechat.register( obj , function(){
                alert("error");
            });
       }

       //user
       for (var i=1 ; i < 50 ; i++){
            var obj = { action: "insert" ,m_id: "u"+i, custom_name:"auser"+i ,  created_time:"1479959062501",updated_time:"1479959062500" } ;
            wechat.register( obj , function(){
                alert("error");
            });
       }

       var obj = { action: "insert" ,m_id: "u0", custom_name:"auser0",corps: -1 ,  created_time:"1479959062501",updated_time:"1479959062500" } ;
       wechat.register( obj , function(){
            alert("error");
       });

       initConn();
   }


   //task
   function task2(){
       //corps: -1 mobile_owner , action:"insert|update|delete|delallExcOwner"

       clearall();

       for (var i=100 ; i < 110 ; i++){
            var obj = { action:"insert" , m_id: "g"+i, isgroup:1 , custom_name:"My家族"+i , created_time:"1479959062601" ,updated_time:"1479959062600"};
            wechat.register( obj , function(){
                alert("error");
            });
       }

       //user
       for (var i=101 ; i < 150 ; i++){
            var obj = { action: "insert" ,m_id: "u"+i, custom_name:"auser"+i ,  created_time:"1479959062501",updated_time:"1479959062500" } ;
            wechat.register( obj , function(){
                alert("error");
            });
       }

       var obj = { action: "insert" ,m_id: "u100", custom_name:"auser100",corps: -1, created_time:"1479959062501",updated_time:"1479959062500" } ;
       wechat.register( obj , function(){
            alert("error");
       });

       initConn();
   }
    */

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

* **v3.0.103** : 2017-02-17
* **v3.0.97** : 2017-02-14
* **v3.0.94** : 2016-12-29
