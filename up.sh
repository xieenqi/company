#!/bin/sh
basepath=$(cd `dirname $0`; pwd)
ff=app/build/outputs/apk/app-debug.apk
if
[ $# -ne 1 ]
[ ! -f $ff ];
then
	echo "输入1个上传文件地址"
	exit
else
    filepath="$basepath/$1"
    if
    [ $# -ne 1 ]
    then
    filepath=$ff
    fi
	if [ ! -f $filepath ]; then
   		echo "文件不存在！"
	else
		echo  "请输入升级或者修复bug描述: "
		read descripe # request host
    	curl  --form "file=@$filepath" --form "des=$descripe"  http://fasttest.dingliqc.com:3000/upload/api
    fi
fi