这是极客时间专栏《即时消息技术剖析与实战》代码示例，实现了一个简易web版的聊天室。这个聊天室的大概功能有：
demo
1. 支持用户的登录。
2. 双方支持简单的文本聊天。
3. 支持消息未读数（包括总未读和会话未读）。
4. 支持联系人页和未读数有新消息的自动更新。
5. 支持聊天页有新消息时自动更新。

## 界面
界面上主要分成4个，一个登录界面，一个所有联系人界面，一个最近联系人界面，一个聊天页。分别如下（请原谅界面的丑陋，前端对我实在比较费劲）：

![](https://github.com/coldwalker/resources/blob/master/geektime/login.jpg)￼

![](https://github.com/coldwalker/resources/blob/master/geektime/all_contacts.jpg)￼

![](https://github.com/coldwalker/resources/blob/master/geektime/latest_contacts.jpg)￼

![](https://github.com/coldwalker/resources/blob/master/geektime/chat.jpg)￼

## 框架
框架上后端用到了spring boot + JPA，前端主要是bootstrap和thymeleaf结合。

## web服务器和资源
资源方面用到了内嵌到jvm中的h2来作为db，语法上基本和mysql一致。redis也是用到了内嵌的embedded-redis。web服务器使用spring boot内嵌的jetty。

## 程序入口和使用
整个项目开箱即用，导入到IDE里后，执行geektime.im.lecture.Starter类就可以启动服务。服务默认端口为80，通过 http://localhost 直接访问可以进入到登录界面。默认内置了三个用户，使用邮箱和密码进行登录，登录成功后，进入到“最近联系人列表页”和“所有联系人列表页”，选择用户，点击”开始聊天“即可进行一对一私聊。用户在最近联系人页和在聊天页时，如果有新的消息到达，消息内容和未读数都会进行实时更新。

用户名  | 用户邮箱 | 用户密码
--------- | -------- | -------
张三 | zhangsan@gmail.com | 1234
李四 | lisi@gmail.com | 1234
王五 | wangwu@hotmail.com | 1234

## 其他代码说明
web容器启动时会自动建表以及插入默认数据，相应的数据在 resources/db 下，schema.sql是建表语句，data.sql是初始化表数据。

thymeleaf对应的页面有两个，在 resources/templates下，分别为 index.html和login.html，前者负责消息展示，后者负责登录。

## 最后
时间有限，该项目仅作为学习参考的demo使用，也欢迎大家来改进。