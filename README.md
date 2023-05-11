<h1 align="center" style="margin: 30px 0 30px; font-weight: bold;">Noodles</h1>
<h4 align="center">基于SpringBoot的轻量服务(器)管理系统</h4>

<p align="center">
	<a href="https://github.com/Mzaxd/Noodles"><img src="https://img.shields.io/badge/%E4%B8%BB%E9%A1%B5-Mzaxd%2FNoodles-orange"></a>
	<a href="https://github.com/Mzaxd/Noodles/blob/master/LICENSE"><img src="https://img.shields.io/github/license/mashape/apistatus.svg"></a>
</p>



## 这个项目是做什么的？

随着个人服务器（Nas/微云盘/软路由）的飞速发展，个人用户拥有多台不同厂家、不同系统服务器的情况越来越多。另一方面随着虚拟机和容器的大量应用，使得个人用户~~（我）~~对于指数上升的管理难度应接不暇。

本项目就是为了解决我在使用我个人的多台服务器时发现的一些问题，比如：

- Windows Server这种`没有原生管理平台`，无法直接通过Web查看状态的系统
- 物理机上使用虚拟机，虚拟机上再使用容器，多层虚拟导致最后记不清宿主关系而`误删除`的情况
- 统一的掉线提醒。有些管理平台有多种掉线提醒(Unraid)，有些没有，有些`配置麻烦`。
- ……



## 技术栈？

### 主要框架

| 技术           | 说明         | 官网                                                         |
| -------------- | ------------ | ------------------------------------------------------------ |
| SpringBoot     | 后端框架     | [https://spring.io/projects/spring-boot](https://spring.io/projects/spring-boot) |
| SpringSecurity | 登录认证     | [https://spring.io/projects/spring-security](https://spring.io/projects/spring-security) |
| jjwt           | 生成Token    | [https://github.com/jwtk/jjwt](https://github.com/jwtk/jjwt) |
| MyBatis-Plus   | ORM框架      | [https://baomidou.com/pages/24112f/](https://baomidou.com/pages/24112f/) |
| oshi-core      | 获取本机信息 | [https://github.com/oshi/oshi](https://github.com/oshi/oshi) |
| hutool-all     | 工具包       | [https://github.com/dromara/hutool](https://github.com/dromara/hutool) |
| jsch           | 连接控制台   | [http://www.jcraft.com/jsch/](http://www.jcraft.com/jsch/)   |

### 中间件&其他服务

- MySQL8
- RabbitMq
- Redis



## 都有什么功能？

1.  服务管理：配置管理所有服务。
2.  物理机管理：配置管理所有的物理服务器。
3.  虚拟机管理：配置管理所有的虚拟机。
4.  容器管理：配置管理所有的容器。
5.  账号管理：包括默认账号的更改，删除账号等。
6.  远程控制台：连接任意一台配置了连接信息的实例。
7.  JVM监控：实时监控本机JVM运行环境。
8.  操作日志：系统正常操作日志记录和查询；系统异常信息日志记录和查询。
9.  用户日志：记录用户信息的所有修改。
10.  掉线提醒：提供多种实例掉线提醒。
11.  物理机监控：实时监视所有物理机的CPU、内存、磁盘、等相关信息。



## 如何安装？

1. 拉取本项目

   ```bash
   git clone
   ```

2. 导入MySQL数据库文件

3. 在对应的application-xxx.yml中配置连接信息

   ```yaml
   server:
     port: xxxx
   spring:
     #MySQL配置
     datasource:
       url: jdbc:mysql://xxxx/xxxx?characterEncoding=utf-8&serverTimezone=Asia/Shanghai
       username: xxxx
       password: xxxx
       driver-class-name: com.mysql.cj.jdbc.Driver
     #Redis配置
     redis:
       host: xxxx
       database: x
     #RabbitMq配置
     rabbitmq:
       host: xxxx
       port: xxxx
       username: xxxxxx
       password: xxxxxx
   ```

   

## 如何使用？

文档地址：http://doc.ruoyi.vip



## 一些演示图

![](https://blog-1310221847.cos.ap-beijing.myqcloud.com/202303232124212.png)

![](https://blog-1310221847.cos.ap-beijing.myqcloud.com/202303232126621.png)

![](https://blog-1310221847.cos.ap-beijing.myqcloud.com/202303232126542.png)

![](https://blog-1310221847.cos.ap-beijing.myqcloud.com/202303232126976.png)

![](https://blog-1310221847.cos.ap-beijing.myqcloud.com/202303232126812.png)

![](https://blog-1310221847.cos.ap-beijing.myqcloud.com/202303232126267.png)

![](https://blog-1310221847.cos.ap-beijing.myqcloud.com/202303232127691.png)

![](https://blog-1310221847.cos.ap-beijing.myqcloud.com/202303232127793.png)

![](https://blog-1310221847.cos.ap-beijing.myqcloud.com/202303232127591.png)

![](https://blog-1310221847.cos.ap-beijing.myqcloud.com/202303232128416.png)
