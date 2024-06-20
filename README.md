Smart Fix Simulator
================
（abbr SFS, pronounced as [sɑːfis]）

# Intro
SFS is a flexible server-side simulator that can response fix protocol messages as needed. 
With simple configuration, it can be used for developers, inspectors, and testers to carry out their work.

SFS is a flexible and common server-side behavior simulator that can return fix protocol messages as needed. It can be used through simple configuration, making it convenient for developers, inspectors, and testers to carry out their work. For example, in the following scenarios
1. I hope to send an order request, and SFS will return a transaction result that includes the specified transaction amount, transaction status, and counterparty information.
2. I hope to send an order request, and SFS will return transaction information multiple times in chronological order, such as a partial transaction notification first, and then a full transaction notification.
3. I hope to send an order request, and SFS will return a cancellation notification after a period of time.
4. I hope to send a quote subscription request, and SFS will continuously or according to the specified number of times return quote update notifications.
5. I would like to send a quote subscription cancellation request to cancel the continuous stream of quote notifications.
6. At the same time, the system provides a series of viewing and management tools to help better analyze and simulate.

# Features
* Open source and freely usable.
* Support request response mode and stream mode.
* Support timed sequential return of processing results.
* Support custom simulation messages of various types, with built-in support for XML+command or xslt methods. Of course, you can also write your own Java code to simulate and return results.
* Can be user-friendly and provide some graphical interfaces.
* More to be developed.

# Questions
If you have any question, you can send email to <lyziuu@gmail.com> or leave me a message on GitHub.

# Main dependency
* Java 8 and SpringBoot.
* [quickFIX/J](https://github.com/quickfix-j/quickfixj)
* [quickfixj-spring-boot-starter](https://github.com/esanchezros/quickfixj-spring-boot-starter)
* [layUI](https://github.com/layui/layui/blob/main/README.en-US.md)
* [MyBatis Mapper](https://github.com/mybatis-mapper/mapper)

# Quick start
## 1. Installation instructions

#### 1). Ensure that the JRE, version 1.8 or above, is installed and enter in the command window
```
java -version
```
show as
```
openjdk version "1.8.0_412"
OpenJDK Runtime Environment (build 1.8.0_412-bre_2024_04_17_05_48-b00)
OpenJDK 64-Bit Server VM (build 25.412-b00, mixed mode)
```
#### 2). Download and decompress [Download](https://github.com/leedeper/smart-fix-simulator/releases)

#### 3). Access to the directory by the command window and run it.
```
cd xxx/smart-fix-simulator-x.x.x/
java -jar smart-fix-simulator-x.x.x.jar
```
Access the websit : http://localhost:9090/
## 2. Configuration Description
* The default web server port is 9090. If you want to specify another port, you can add startup parameters, such as modifying to 8085
```
java -jar smartFixSimulator-x.x.x.jar --server.port=8085
```
More parameters, you could find out at file application.yml.
* About the configuration of the fix engine, it is set in quickfixj-server.cfg. You can refer to the official instructions of QuickfixJ and make modifications based on your own fix client configuration file.
* The parameters of the simulator itself are mainly in simulator.cfg, as explained below

| Parameter                       | Description                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                        |
|---------------------------------|------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| rule.type                       | Define routing rule categories, currently supporting two types: msgType and spEL. msgType directly uses the msgType in the message header to make decisions, which is simple and clear. SpEL is used for complex routing rules, such as combining multiple fields to make decisions. Using Spring Expression, the return result should be Boolean type.                                                                                                                                                                                                                                                                                                                                                                                                                                                                            |
| generator.xxx.type              | The Generator named xxx can use built-in or be customized using Java code. There are three built-in generator: xmlgen, xsltgen, and nonegen. You can refer to the default examples xmlgen and xsltgen. xmlgen requires the templatePath parameter to be used together, while xsltgen requires the xsltPath parameter to be used together. nonegen has no parameters and will not return any results. It only provides printing, in special cases, it can be used in conjunction with postAction to achieve certain effects. When customization is required, implement the interface smart.fixsimulator.fixacceptor.core.Generator， and configure this parameter as the full path of your own implementation class, such as com.my.MyGenerator, which can refer to an implementation class smart.fixsimulator.demo.MyDemoGenerator. |
| generator.xxx.rule              | The routing rule expression item, the configuration content based on rule.type                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                     |
| generator.xxx.templatePath      | The parameters only for xmlgen, xxx.type=xmlgens, define an XML template, which can use built-in command. Currently, built-in command include RandomInt RandomFloat、Sequence、UTCDate、UTCTime、UTCDateTime， For complex logic, Spring Expression can be used, which can be referenced in file USDJPYQuote.xml for usage.                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                             |
| generator.xxx.xsltPath          | The parameters only for xsltgen，xxx.type=xsltgen, define the xslt path. Built-in command can be used in xslt (note that Spring Expression is not supported), and usage can be referenced in file executionReportFilled.xslt.                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                       |
| generator.xxx.loop.delay        | See ScheduledThreadPoolExecutor.scheduleWithFixedDelay()                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                           |
| generator.xxx.loop.initialDelay | See ScheduledThreadPoolExecutor.scheduleWithFixedDelay()                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                           |
| generator.xxx.loop.timeUnit     | See ScheduledThreadPoolExecutor.scheduleWithFixedDelay() ，only support NANOS,MICROS,MILLIS(default),SECONDS,MINUTES,HOURS                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                          |
| generator.xxx.loop.poolSize     | See ScheduledThreadPoolExecutor                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                    |
| generator.xxx.loop.count        | Define the number of loop, if not configured, then infinite (unless cancelled)                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                     |
| generator.xxx.loop.idExpression | Define the unique ID of the loop, taking values from the request message and session ID, represented by Spring Expression                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                          |
| generator.xxx.postAction.cancel | Define the post action. Currently, only the cancel command is supported, and the configuration content is Spring Expression. The return result has the same logic as idExpression, such as subscription quotes or pending order scenarios, which can be used to complete cancellation                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                        |

## 3. GUI Description
#### 1). The message incoming and outgoing
![message log](https://i.postimg.cc/j53qRgPn/message-Log.png)
<br>
Double click to get more information
<br>
![message detail](https://i.postimg.cc/j5QT5R91/message-Log-Double-Click.png)
#### 2). Event log about FIX Engine
![event log](https://i.postimg.cc/4NDdKSMz/eventLog.png)
#### 3). FIX Session
![session status](https://i.postimg.cc/s2cDND2c/session.png)
#### 4). Loop Task
![loop task](https://i.postimg.cc/nLZh2Qnr/loopTask.png)
#### 5). FIX Message to XML kit
![message to XML kit](https://i.postimg.cc/63p5JJ2N/parse2xml.png)






