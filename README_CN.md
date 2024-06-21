Smart Fix Simulator
============
（后文简写为'SFS', 发音为 [sɑːfis]）
<div align="left">
  <a href="./README.md"><img alt="README in English" src="https://img.shields.io/badge/English-d9d9d9"></a>
  <a href="./README_CN.md"><img alt="README in Simplified Chinese" src="https://img.shields.io/badge/简体中文-d9d9d9"></a>
</div>
# 简介
SFS是一个灵活通用的、可按照需要返回fix协议报文的服务端行为模拟器，通过简单配置即可使用，方便开发人员、验收人员、测试人员开展工作。比如以下场景
1. 希望发送一个下单请求，SFS返回一个成交结果，包含了指定的成交金额、交易状态、交易对手等信息。
2. 希望发送一个下单请求，SFS按照时间先后多次返回成交信息，例如先是一个部分成交通知、再是一个全部成交通知等。
3. 希望发送一个下单请求，SFS经过一段时间后返回一个撤销通知。
4. 希望发送一个报价订阅请求，SFS源源不断的或按照指定的次数的返回报价更新通知。
5. 希望发送一个报价订阅取消请求，取消源源不断的报价通知。
6. 同时，系统提供一些列查看和管理工具，帮助更好的分析和模拟。

# 特性
* 开源并且可以自由使用
* 支持请求响应模式，以及流推送模式
* 支持定时先后顺序返回处理结果
* 支持自定义各类模拟报文，内置支持xml+命令方式或xslt方式，当然也可以自己写java代码模拟返回结果
* 可以友好使用，提供一些图形界面

# 问题反馈
欢迎发邮件到<lyziuu@gmail.com>交流，或者在GitHub上留言。

# 关键依赖
* Java 8 及 SpringBoot
* [quickFIX/J](https://github.com/quickfix-j/quickfixj)
* [quickfixj-spring-boot-starter](https://github.com/esanchezros/quickfixj-spring-boot-starter)
* [layUI](https://github.com/layui/layui/blob/main/README.en-US.md)
* [MyBatis Mapper](https://github.com/mybatis-mapper/mapper)

# 使用说明
## 一. 安装说明
#### 1. 确保安装了java虚拟机，1.8及以上版本，在命令窗口中输入
```
java -version
```
结果形如
```
openjdk version "1.8.0_412"
OpenJDK Runtime Environment (build 1.8.0_412-bre_2024_04_17_05_48-b00)
OpenJDK 64-Bit Server VM (build 25.412-b00, mixed mode)
```

#### 2. 下载最新SFS版本，并解压缩。 <a href="https://github.com/leedeper/smart-fix-simulator/releases"><img alt="Download" src="https://img.shields.io/github/release/leedeper/smart-fix-simulator.svg"></a>


#### 3. 命令窗口进入到解压目录，并运行即可
```
cd xxx/smart-fix-simulator-x.x.x/
java -jar smart-fix-simulator-x.x.x.jar
```
然后就可以通过浏览器访问http://localhost:9090/

## 二. 配置说明
* 默认web端口为9090，如果和本系统已经存在的服务冲突或希望指定其它端口，则可以增加启动参数，例如修改为8085
```
java -jar smartFixSimulator-x.x.x.jar --server.port=8085
```
更多参数可以参考文件application.yml
* 关于fix engine相关配置，存放在quickfixj-server.cfg中，可以参考QuickfixJ官方说明，结合自己的fix client配置文件情况进行修改
* 关于模拟器自身的参数主要在simulator.cfg中，说明如下

| 字段                              | 说明                                                                                                                                                                                                                                                                                                                                       |
|---------------------------------|------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| rule.type                       | 定义路由规则类别，目前支持msgType和spEL两种，msgType直接用消息头中的msgType来决策，简单明了。spEL用于复杂的路由规则，例如需要结合多个字段才能判断，使用Spring Expression，返回结果为布尔类型即可                                                                                                                                                                                                                  |
| generator.xxx.type              | 名为xxx的生成器类型，可以使用内置生成器，也可以使用java代码自定义，内置支持三种xmlgen、xsltgen以及nonegen，前两种可以参考默认示例，xmlgen需要templatePath参数一起使用，xsltgen需要xsltPath参数一起使用，nonegen没有参数，不会返回任何结果，只提供打印，特殊情况可以配合postAction实现某些效果。当需要自定义时，实现接口smart.fixsimulator.fixacceptor.core.Generator，并将此参数配置为自己实现类的全路径，例如com.my.MyGenerator，可以参考一个实现类 smart.fixsimulator.demo.MyDemoGenerator |
| generator.xxx.rule              | 名为xxx的路由规则表达项，根据rule.type来决定配置内容                                                                                                                                                                                                                                                                                                         |
| generator.xxx.templatePath      | xmlgen特有的参数，xxx.type=xmlgens时需要进行配置，定义xml模板，模板中可以使用内置指令，目前内置指令包括RandomInt、RandomFloat、Sequence、UTCDate、UTCTime、UTCDateTime，同时对于复杂的逻辑可以使用Spring Expression完成，可以在USDJPYQuote.xml中参考用法。                                                                                                                                                     |
| generator.xxx.xsltPath          | xsltgen特有的参数，xxx.type=xsltgen时需要进行配置，定义xslt解析文件路径，xslt中可以使用内置的指令（不支持Spring Expression），可以在executionReportFilled.xslt中参考用法。                                                                                                                                                                                                               |
| generator.xxx.loop.delay        | 参考ScheduledThreadPoolExecutor.scheduleWithFixedDelay()                                                                                                                                                                                                                                                                                   |
| generator.xxx.loop.initialDelay | 参考ScheduledThreadPoolExecutor.scheduleWithFixedDelay()                                                                                                                                                                                                                                                                                   |
| generator.xxx.loop.timeUnit     | 参考ScheduledThreadPoolExecutor.scheduleWithFixedDelay() ，支持NANOS,MICROS,MILLIS(默认),SECONDS,MINUTES,HOURS几个字符串                                                                                                                                                                                                                             |
| generator.xxx.loop.poolSize     | 参考ScheduledThreadPoolExecutor                                                                                                                                                                                                                                                                                                            |
| generator.xxx.loop.count        | 定义循环次数，如果为无配置则为无限次（除非被取消）                                                                                                                                                                                                                                                                                                                |
| generator.xxx.loop.idExpression | 定义loop的唯一id，从请求message和sessionID中取值，用spring expression表示                                                                                                                                                                                                                                                                                 |
| generator.xxx.postAction.cancel | 定义后置行为，目前仅支持cancel指令，配置内容为Spring Expression，返回结果与idExpression逻辑相同，例如订阅报价或挂单场景，可通过此来完成取消                                                                                                                                                                                                                               |
## 三. 图形界面说明
#### 1. 往来消息查看及监控
![message log](https://i.postimg.cc/j53qRgPn/message-Log.png)
<br>
双击查看明细
<br>
![message detail](https://i.postimg.cc/j5QT5R91/message-Log-Double-Click.png)
#### 2. Fix引擎事件日志
![event log](https://i.postimg.cc/4NDdKSMz/eventLog.png)
#### 3. Fix会话查看
![session status](https://i.postimg.cc/s2cDND2c/session.png)
#### 4. 循环任务查看
![loop task](https://i.postimg.cc/nLZh2Qnr/loopTask.png)
#### 5. Fix消息转换小工具
![message to XML kit](https://i.postimg.cc/63p5JJ2N/parse2xml.png)
