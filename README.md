这是极客时间专栏《即时消息技术剖析与实战》代码示例，实现了一个简易web版的聊天室。主要分成两期完成，[期中](#middle)，[期末](#final)。
## <span id="middle">期中示例说明</span>
这个聊天室的大概功能有：
1. 支持用户的登录。
2. 双方支持简单的文本聊天。
3. 支持消息未读数（包括总未读和会话未读）。
4. 支持联系人页和未读数有新消息的自动更新。
5. 支持聊天页有新消息时自动更新。

### 界面
界面上主要分成4个，一个登录界面，一个所有联系人界面，一个最近联系人界面，一个聊天页。分别如下（请原谅界面的丑陋，前端对我实在比较费劲）：

![](https://github.com/coldwalker/resources/blob/master/geektime/login.jpg)￼

![](https://github.com/coldwalker/resources/blob/master/geektime/all_contacts.jpg)￼

![](https://github.com/coldwalker/resources/blob/master/geektime/latest_contacts.jpg)￼

![](https://github.com/coldwalker/resources/blob/master/geektime/chat.jpg)￼

### 框架
框架上后端用到了spring boot + JPA，前端主要是bootstrap和thymeleaf结合。

### web服务器和资源
资源方面用到了内嵌到jvm中的h2来作为db，语法上基本和mysql一致。redis也是用到了内嵌的embedded-redis。web服务器使用spring boot内嵌的jetty。

### 程序入口和使用
整个项目开箱即用，导入到IDE里后，执行geektime.im.lecture.Starter类就可以启动服务。服务默认端口为80，通过 http://localhost 直接访问可以进入到登录界面。默认内置了三个用户，使用邮箱和密码进行登录，登录成功后，进入到“最近联系人列表页”和“所有联系人列表页”，选择用户，点击”开始聊天“即可进行一对一私聊。用户在最近联系人页和在聊天页时，如果有新的消息到达，消息内容和未读数都会进行实时更新。

用户名  | 用户邮箱 | 用户密码
--------- | -------- | -------
张三 | zhangsan@gmail.com | 1234
李四 | lisi@gmail.com | 1234
王五 | wangwu@hotmail.com | 1234

### 其他代码说明
web容器启动时会自动建表以及插入默认数据，相应的数据在 resources/db 下，schema.sql是建表语句，data.sql是初始化表数据。

thymeleaf对应的页面有两个，在 resources/templates下，分别为 index.html和login.html，前者负责消息展示，后者负责登录。

## <span id="final">期末示例说明</span>
期末我们对这个聊天室进行了较大的改造，主要的feature有：
1. 支持基于websocket的长连接。
2. 消息收发均通过长连接进行通信。
3. 基于redis的发布/订阅实现消息推送。
4. 支持消息推送的ack机制和重推机制。
5. 支持客户端的单向心跳机制和idle超时断连。
6. 支持客户端断线后的自动重连。

### 界面
期末的界面主要在原有界面上新增了一个websocket的入口，并且支持根据长连接状态进行“上线”和“掉线”的提示。

* websocket入口位置如下图：
![](https://github.com/coldwalker/resources/blob/master/geektime/websocket_entrance.png)

* 上线建连成功提示
![](https://github.com/coldwalker/resources/blob/master/geektime/websocket_login_success.png)

### 框架
长连接的实现上采用了netty作为nio的框架，结合spring boot以及redis的“发布/订阅”功能来完成。

### 使用相关说明
通过 http://localhost 进行登录，默认三个账号的邮箱和密码和期中的一致，登录成功后，从右上角的websocket入口进入到基于websocket的界面，此时默认会向服务端建立长连，长连建立成功后会在该页面的右上角显示”上线成功“。长连建立后，后续该界面的所有操作都会基于这个websocket长连来进行，比如消息发送、消息打开查询、消息推送接收等。

### 其他代码说明
websocket的服务端启动入口程序在：/Sample/src/main/java/geektime/im/lecture/ws/WebSocketServer.java里，目前是通过@PostConstruct跟随主程序的Starter自动启动的，默认在8080端口监听。服务端WebSocketServer的主要配置在：/Sample/src/main/resources/application.yml里面，主要包括一下配置：
![](https://github.com/coldwalker/resources/blob/master/geektime/websocket_confs.png)

## 最后
该项目仅作为学习参考的demo使用，包括期末的很多功能在实现上也仅能作为示例使用，不具备线上使用标准。另外在功能上面也还有需要完善的地方，比如：离线消息的实现、群聊功能的实现等，也欢迎和期待大家一起来改进完善。