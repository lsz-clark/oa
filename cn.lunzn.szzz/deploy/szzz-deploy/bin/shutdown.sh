#/bin/bash
function blue_echo()
{
    echo -e "\033[36m shell info--> $1 \033[0m"
}

function red_echo()
{
    echo -e "\033[31m shell info--> $1 \033[0m"
}

function doShutdownAll()
{
    echo `ps -ef|grep 'lunzn\.szzz'|grep -v grep|awk '{print $2 "\t" $10}'`
	
    psResult=`ps -ef|grep 'lunzn\.szzz'|grep -v grep|awk '{print $2}'`
	
	echo $psResult
	
	for pid in $psResult
	do
	    echo "kill -9 $pid"
		`kill -9 $pid`
	done
	
	echo `ps -ef|grep 'szzz-deploy/portal-tomcat'|grep -v grep|awk '{print $2 "\t" $10}'`
	
    psResult=`ps -ef|grep 'szzz-deploy/portal-tomcat'|grep -v grep|awk '{print $2}'`
	
	echo "kill -9 $psResult"
	
	`kill -9 $psResult`
	
	echo "shutdown all success,bye..."
}

function doShutdownSZ()
{
    echo `ps -ef|grep 'lunzn.szzz.szservice'|grep -v grep|awk '{print $2 "\t" $10}'`
	
    psResult=`ps -ef|grep 'lunzn.szzz.szservice'|grep -v grep|awk '{print $2}'`
	
	echo "kill -9 $psResult"
	
	`kill -9 $psResult`
	
	echo "shutdown szservice success,bye..."
}

function doShutdownZZ()
{
    echo `ps -ef|grep 'lunzn.szzz.zzservice'|grep -v grep|awk '{print $2 "\t" $10}'`
	
    psResult=`ps -ef|grep 'lunzn.szzz.zzservice'|grep -v grep|awk '{print $2}'`
	
	echo "kill -9 $psResult"
	
	`kill -9 $psResult`
	
	echo "shutdown zzservice success,bye..."
}

function doShutdownPortal()
{
    echo `ps -ef|grep 'szzz-deploy/portal-tomcat'|grep -v grep|awk '{print $2 "\t" $10}'`
	
    psResult=`ps -ef|grep 'szzz-deploy/portal-tomcat'|grep -v grep|awk '{print $2}'`
	
	echo "kill -9 $psResult"
	
	`kill -9 $psResult`
	
	echo "shutdown portal success,bye..."
}

if [[ $1 == all ]];then
    doShutdownAll
elif [[ $1 == sz ]];then
    doShutdownSZ
elif [[ $1 == zz ]];then
    doShutdownZZ
elif [[ $1 == portal ]];then
    doShutdownPortal
else
    red_echo "请输入参数: all(停止所有服务) sz(停止述职服务) | zz(停止转正服务) | portal(停止门户服务)"
fi
