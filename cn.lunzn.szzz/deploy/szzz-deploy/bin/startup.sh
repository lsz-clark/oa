#/bin/bash
function blue_echo()
{
    echo -e "\033[36m shell info--> $1 \033[0m"
}

function red_echo()
{
    echo -e "\033[31m shell info--> $1 \033[0m"
}

function doStartAll()
{
    echo "lunzn.szzz.szservice.jar, starting..."
    nohup java -jar ../micro-server/lunzn.szzz.szservice.jar &
	
    echo "lunzn.szzz.zzservice.jar, starting..."
    nohup java -jar ../micro-server/lunzn.szzz.zzservice.jar &

	echo "portal-tomcat, starting..."
	./../portal-tomcat/bin/startup.sh
	
	echo "start all success,bye..."
}

function doStartSZ()
{
    echo "lunzn.szzz.szservice.jar, starting..."
    nohup java -jar ../micro-server/lunzn.szzz.szservice.jar &
	
	echo "start szservice success,bye..."
}

function doStartZZ()
{
	echo "lunzn.szzz.zzservice.jar, starting..."
    nohup java -jar ../micro-server/lunzn.szzz.zzservice.jar &
	
	echo "start zzservice success,bye..."
}

function doStartPortal()
{
	echo "portal-tomcat, starting..."
	./../portal-tomcat/bin/startup.sh
	
	echo "start portal success,bye..."
}

if [[ $1 == all ]];then
    doStartAll
elif [[ $1 == sz ]];then
    doStartSZ
elif [[ $1 == zz ]];then
    doStartZZ
elif [[ $1 == portal ]];then
    doStartPortal
else
    red_echo "请输入参数: all(启动所有服务) | sz(启动述职服务) | zz(启动转正服务) | portal(启动门户服务)"
fi
