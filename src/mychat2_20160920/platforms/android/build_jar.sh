#!/bin/sh

#使用Gradle編譯各個module
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
mkdir debug
mkdir release

#定義sdk版本號
package="ebus"
version="3.0.0"
is_cordova=true

#解壓所有debug版本的jar包到temp目錄中
cd temp

if $is_cordova; then
    jar -xvf ../../build/intermediates/bundles/debug/classes.jar
    #jar -xvf ../../build/intermediates/bundles/release/classes.jar
fi

#delete META-INF and insert
rm -rf org
rm -rf META-INF
mkdir META-INF
echo "Manifest-Version: 1.0" > META-INF/MANIFEST.MF
echo "Created-By: 1.7.0_79 (Oracle Corporation)" >> META-INF/MANIFEST.MF
echo "" >> META-INF/MANIFEST.MF

#壓縮所有release版本的class文件到一個jar包中
jar -cvfM ${package}-${version}.jar .

#拷貝文件
mv ${package}-${version}.jar ../release

#刪除temp目錄
cd ..
#rm -rf temp
