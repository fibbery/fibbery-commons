# 常用工具类
## Snowflake算法生成的分布式高效ID
把时间戳，工作机器id，序列号组合在一起。

![Snowflake算法核心](docs/snowflake-64bit.jpg)

除了最高位bit标记为不可用以外，其余三组bit占位均可浮动，看具体的业务需求而定。默认情况下41bit的时间戳可以支持该算法使用到2082年，10bit的工作机器id可以支持1023台机器，序列号支持1毫秒产生4095个自增序列id。

### 时间戳
这里时间戳的细度是毫秒级，System.currentMillions生成的时间戳正好是40位的long型。具体代码如下，建议使用64位linux系统机器，因为有vdso，gettimeofday()在用户态就可以完成操作，减少了进入内核态的损耗。

### 工作机器id
严格意义上来说这个bit段的使用分为两种，机器级和进程级别。机器级的话你可以使用MAC地址来唯一标示工作机器，工作进程级可以使用IP+Path来区分工作进程。如果工作机器比较少，可以使用配置文件来设置这个id是一个不错的选择，如果机器过多配置文件的维护是一个灾难性的事情。
我采用的是进程级方案，对于10 Bits工作机器ID，我使用前5 Bits作为数据中心ID(datacenterID),后面5 Bits作为工作进程ID(workerID)，数据中心的ID生成依靠MAC地址来生成，取MAC地址的后2位(16 Bits)右移6 Bits得到10 Bits 的基数，然后和 maxDatacenterID( 2 ^ 5)  + 1取余。工作进程ID我采用 datacenterID + pid 的 hascode 取 16 Bits 和 maxWorkerID( 2 ^ 5 + 1) 取余来获得。

### 序列号
序列号就是一系列的自增id（多线程建议使用atomic），为了处理在同一毫秒内需要给多条消息分配id，若同一毫秒把序列号用完了，则“等待至下一毫秒”。