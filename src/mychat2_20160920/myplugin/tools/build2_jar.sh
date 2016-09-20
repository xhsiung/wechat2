#!/bin/bash
./gradlew clean
./gradlew build --stacktrace --debug

#進入輸出目錄
rm -rf output
mkdir output
cd output

#清空輸出目錄
rm -rf  *

#創建輸出子目錄
mkdir temp
mkdir release

#定義sdk版本號
package=tw.wechat.bais
packpackage="ebus"
version="3.0.0"
is_cordova=true

#split package
IFS='.' read -ra NAMES <<< "$package"

#build下classes到temp目錄中
cd temp
if $is_cordova; then
    #tw.com.bais.wechat
    cp -a  ../../build/intermediates/classes/debug/${NAMES[0]}  .

    #delete files that i would not need.
    for f in $(find . -type f |grep -E "BuildConfig.class|R.class|R\\$");do
        rm -rf  $f
    done

    #create META-INF and insert
    mkdir META-INF
    echo "Manifest-Version: 1.0" > META-INF/MANIFEST.MF
    echo "Created-By: 1.1.0_00 (Oracle Corporation)" >> META-INF/MANIFEST.MF
    echo "" >> META-INF/MANIFEST.MF
fi


#壓縮所有release版本的class文件到一個jar包中
jar -cvfM ${packpackage}-${version}.jar .

#拷貝文件
mv ${packpackage}-${version}.jar ../release

