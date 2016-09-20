cordova.define('cordova/plugin_list', function(require, exports, module) {
module.exports = [
    {
        "file": "plugins/tw.com.bais.wechat/www/wechat.js",
        "id": "tw.com.bais.wechat.WeChat",
        "clobbers": [
            "wechat"
        ]
    }
];
module.exports.metadata = 
// TOP OF METADATA
{
    "cordova-plugin-whitelist": "1.2.2",
    "tw.com.bais.wechat": "0.0.1"
};
// BOTTOM OF METADATA
});