# DView8-Telnet-Server
DView8-Telnet-Server
### 1,运行pom.xml导入相关jre包
### 2.运行TelnetserverApplication.java 启动DView8-Telnet-Server
### 3.利用Postman工具便可访问 http://localhost:8080/DView8-Telnet-Server/TelnetCommand 连接到Telnet服务器 并执行相应命令
    命令范例可见 bin目录下的 one.png two.png three.png four.png四张截图
### 4.代码逻辑
    当程序员启动后TelnetThread线程执行，但由于队列TelnetTaskQueue队列里没有命令而阻塞
    当前台传递一个命令到后台后，后台TelnetCommand接口服务便会把这个命令压到TelnetTaskQueue队列里
    TelnetTaskQueue队列里有命令后，TelnetThread线程take消费掉这个命令，并判断后选择新建一个TelnetOperation连接线程还是为已经
    存在的但正在阻塞中的TelnetOperation连接线程传递命令
    TelnetCommand接口服务会等待该命令的执行后的返回结果，等到返回结果后将返回结果传递到前台
    
    就是一个taskid 会对应一个TelnetOperation连接线程
 
 
 
 
