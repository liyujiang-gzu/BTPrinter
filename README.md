# [ESC/POS指令集](https://blog.csdn.net/feng88724/article/details/17474351)
```text
英文模式下的命令
代码	功能
CR	回车
ESC !	设置打印方式
ESC %	选择或取消用户自定义字符集
ESC &	定义用户自定义字符集
ESC *	设置位映射方式
ESC @	初始化打印机
ESC ~	LED ON/OFF
ESC <	返回行首
ESC 2	选择行间距为1/6英寸
ESC 3	设置行进为最小间距
ESC BEL	蜂鸣器ON/OFF
ESC C	设置单页长度
ESC c0	选择打印页
ESC c1	选择行间距
ESC c3	选择纸结束信号输出
ESC c4	选择打印纸及检测器（终止打印）
ESC c5	禁止/使能面板开关
ESC c6	禁止/使能ON-LINE开关
ESC d	打印及N行进纸
ESC D	设置TAB位置
ESC e	打印病退回N行
ESC f	设单页等待时间
ESC F	选择或取消单页退纸区
ESC i	全切割
ESC J	以最小间距进行打印和进纸
ESC K	以最小间距进行打印和退纸
ESC l	选择或取消倒过来的字符
ESC m	局部切割
ESC o	印章
ESC p	产生指定脉冲
ESC q	释放纸
ESC r	选择打印颜色
ESC R	选择国际字符子集
ESC SP	设置右边界
ESC t	选择字符码表
ESC U	选择或取消单向打印
ESC V	发送打印机状态
ESC z	设置或取消两页并行打印
FF	打印送出单页
HT	水平TAB
LF	换行
RS	流水TAB
中文模式下的命令
代码	功能
FS – n	设置中文字符下划线模式开关
FS ! n	选择中文字体
FS &	选择中文字符模式
FS .	
取消中文模式

----------------------------------------------------------------------------------------

EPSON打印控制指令集

打印命令　　　　　　　　　　　　　　　　　　　               功能

格式：　 ASCII：　以标准ASCII字符序列表示

　　　　十进制：　以十进制数字序列表示

　　　十六进制：　以十六进制数字序列表示

说明：该命令功能和使用说明。

HT　　　　　　　　　　　　　　　　　　　                 　水平制表

格式：　 ASCII：　HT

　　　　十进制：　09

　　　十六进制：　09

说明：

打印位置进行到下一水平制表位置。

如果当前打印位置超过了最后一个水平制表位置，则HT命令不被执行。

水平制表位置由 ESC　D命令设置。

LF　　　　　　　　　　　　　　　　　　　                   打印并换行

格式：　 ASCII：　LF

　　　　十进制：　10

　　　十六进制：　0A

说明：

打印存放在行编辑缓存中的一行数据并按当前设定的行距向前走纸一行。ESC2，ESC3指令可设定行距的长短。

如果行编辑缓存空，则只按当前设定的行距向前走纸一行。

CR　　　　　　　　　　　　　　　　　　　                   打印回车

格式：　 ASCII：　CR

　　　　十进制：　13

　　　十六进制：　0D

说明：

  当打印在行编辑缓存中的一行数据之后，不进行走纸操作。

DLE EOT n　　　　　　　　　　　　　　　　　               实时状态传送

格式：　 ASCII：　DLE EOT n

　　　　十进制：　16 04 n

　　　十六进制：　10 04

说明：

1 ≤ n ≤ 4

  传送以实时的方式,n代表被选择的打印机状态，基于下列参数:

n = 1: 打印机状态

n = 2: 脱机状态

n = 3: 错误状态

n = 4: 纸检测器状态

返回的字节每位设置如下:

n = 1: 打印机状态

n = 2: 脱机状态

n = 3: 错误状态

n = 4: 纸检测器状态

DLE ENQ n　　　　　　　　　　　　                 　对打印机的实时请求

格式：　 ASCII：　DLE ENQ n

　　　　十进制：　16 05 n

　　　十六进制：　10 5

说明：

n = 0, n = 2

打印机对主机请求作出反应,n值定义如下:

n = 0: 恢复到联机状态

n = 2: 清除接收和打印缓冲区,并恢复错误.

ESC　SP　n　　　　　　　　　　　　　                 设置字符右间距

格式：　 ASCII：　ESC　 SP　n

　　　　十进制：　27　　32　n

　　　十六进制：　1B　　20　n

说明：

以半点为设定单位(1/144英寸)，设置字符右边间距为n个半点距。

默认值n＝0。

ESC　！　n　　　　　　　　　　　                 　 设置字符打印方式

格式：　 ASCII：　ESC　 ！　n

　　　　十进制：　27　　33　n

　　　十六进制：　1B　　21　n

说明：

  0≤n≤255

ESC　！　n是综合性的字符打印方式设置命令，用于选择打印字符的大小和下划线。

打印参数n的每位定义为：

位    功能    值

       0    1

0    字模选择    7×9    5×7

1    无定义        

2    无定义        

3    着重模式    取消    设定

4    倍　高    取消    设定

5    倍　宽    取消    设定

6    无定义        

7    下划线    取消    设定

默认值n＝0，

ESC　%　n　　　　　　　　　                 允许/禁止用户自定义字符

格式：　 ASCII：　ESC 　%　 n

　　　　十进制：　27　　37　n

　　　十六进制：　1B　　25　n

说明：

参数n为一个字节，只有最低位有效。

当n＝＜*******1＞B时，选择用户自定义字符集；

当n＝＜*******0＞B时，选择内部字符集。

0≤n≤255，默认值n＝0。

ESC　&　　　　　　　　　　　　　　               设置用户自定义字符

格式： ASCII：　ESC　 &　 y　 n　 m　 x　 d1　 d2……db

　　 十进制：　27　 38　 y　 n　 m　 x　 d1　 d2……db

　　十六进制：　1B　 26　 y　 n　 m　 x　 d1　 d2……db

说明：

该命令用于自定义字符。各参数为：

y：字符的纵向字节数。这里取 y＝1，2。

n：自定义字符集的起始ASCII码，n≥32。

m：自定义字符集的终止ASCII码，m≤127。自定义字符个数为m-n+1，最多可定义96个字符。

当只有一个自定义字符时，取m=n。

a：自定义字符的水平方向的点数。

d1　d2……db：自定义字符的数据。每个字符 y×x个字节，m-n+1个自定义字符共有：

（y×x）×（m-n+1）个字节。

每个自定义字符数据的格式：

d1    d3    …    d（y×x）×（m-n+1）-1

d2    d4    …    d（y×x）×（m-n+1）

样例如下：

发送的命令如下：

ESC & y c1 c2 X p1 p2 p3 d4 p5 p6 p7 p8 p9 p10 p11 p12 p13 p14

Code 1B 26 02 20 20 07 1F 80 20 00 44 00 80 00 44 00 20 00 1F 80

ESC　*　　　　　　　　　　　　　　　　   　               设置图形点阵

格式：　 ASCII：　ESC　 *　 m　 n1　 n2　   D1，D2 … Dk

　　　 　十进制：　27   42   m　 n1　 n2　   D1，D2 … Dk

　　 　十六进制：　1B   2A   m　 n1　 n2　   D1，D2 … Dk

说明：

该命令用来设置点阵图形模式（m）和横向图形点阵。

m = 0，1： 表示打印密度。

0≤n1≤255，0≤n2≤1，0≤Dk≤255，k= n1+ n2×256。

n1，n2为两位十六进制数，n1这低字节，n2这高字节，k= n1+ n2×256，表示该命令下载的要打印图形的横向点数，该值应小于打印机的最大行宽打印点数。如果下送的点图数据超出一行的最大行宽打印点数时，超出的部分被忽略。

m    垂直方向点数    点密度    最大点数    图形打印模式

0    8    单密度    210    相邻点打印

1    8    双密度    420    相邻点不打印

Dk 定义为：

ESC -n　　　　　　　　　　　　　　　　             设置/取消下划线模式

格式：　 ASCII：　ESC　 -　 n　

　　　 　十进制：　27   45   n　

　　 　十六进制：　1B   2D   n　

说明：

n = 0, 1, 48, 49

n = 0 or 48,取消下划线模式。

n = 1 or 49, 设置下划线模式。

ESC　2　　　　　　　　　　　　　                   设定1/6英寸换行量

格式：　 ASCII：　ESC　 2

　　　　十进制：　27　　50

　　　十六进制：　1B　　32

说明：

此指令将打印机的换行量设定为1/6英寸。

ESC　3　 n　　　　　　　　　　                   设定 n/144英寸换行量

格式：　 ASCII：　ESC　 3　　n

　　　　十进制：　27　　51　 n

　　　十六进制：　1B　　33　 n

说明：

此指令将打印机的换行量设定为 n/144英寸。

n＝1 - 255。

ESC　〈　　　　　　　　　　　　　　　　                 打印头归位

格式：　 ASCII：　ESC　〈

　　　　十进制：　27　　60

　　　十六进制：　1B　　3C

说明：

执行该指令后，打针头会回到原位（home的位置）。

ESC =n　　　　　　　　　　　　　　　　               设备设置/取消

格式：　 ASCII：　ESC　 =　 n　

　　　 　十进制：　27   61   n　

　　 　十六进制：　1B   3D   n　

说明：

1 ≤n ≤3

默认 n = 1

位    功能    值

       0    1

0    打印机选择    取消    设定

1    顾显选择    取消    设定

2    无定义        

3    无定义        

4    无定义        

5    无定义        

6    无定义        

7    无定义        

ESC ？n　　　　　　　　　　　　　　　　           取消用户自定义字符

格式：　 ASCII：　ESC　 ？　 n　

　　　 　十进制：　27   63   n　

　　 　十六进制：　1B   3F   n　

说明：

32 ≤n ≤126

ESC　＠　　　　　　　　　　　　　　　                 初始化打印机

格式：　 ASCII：　ESC　 ＠

　　　　十进制：　27　　64

　　　十六进制：　1B　　40

说明：

该命令初始化打印机：使打印机恢复到最初的联机状态，清除以前留下的程序。

ESC　D　NULL　　　　　　　                   消除所有的水平制表位置

格式：　 ASCII：　ESC　 D　 NULL

　　　　十进制：　27   68   0

　　　十六进制：　1B　　44   0

说明：

ESC　D　NUL命令消除所有的水平制表位置，之后再执行的HT命令将无效。

ESC　E　n　　　　　　　                         设置/取消着重模式

格式：　 ASCII：　ESC　 E　 NUL

　　　　十进制：　27   69   0

　　　十六进制：　1B　　45   0

说明：

0 ≤n ≤255

当n的LSB位是0，取消着重模式。

当n的LSB位是1，设置着重模式。

ESC　G　n　　　　　　　                         设置/取消重叠模式

格式：　 ASCII：　ESC　 G　 NUL

　　　　十进制：　27   71   0

　　　十六进制：　1B　　47   0

说明：

0 ≤n ≤255

当n的LSB位是0，取消重叠模式。

当n的LSB位是1，设置重叠模式。

ESC　J　n　　　　　　　　　　　　                   执行n/144英寸走纸

格式：　 ASCII：　ESC   J　 n

　　　　十进制：　27   74   n

　　　十六进制：　1B　　4A   n

说明：

进纸n/144英寸，n值应为0到255之间的任意值。

若当时有打印内容则在打印完成后执行走纸。

ESC　K　n　　　　　　　　　　　　                   打印并反向走纸

格式：　 ASCII：　ESC   K　 n

　　　　十进制：　27   75   n

　　　十六进制：　1B　　4B   n

说明：

0 ≤n ≤48

打印缓冲区里的内容，并反方向走纸n/144英寸（n X 0.176 mm）。

ESC　R　n　　　　　　　　　　　　　　                 选择国际字符集

格式：　 ASCII：　ESC　 R　 n

　　　　十进制：　27   82 n

　　　十六进制：　1B　　52 n

说明：

ESC　R用于选择11个不同国家的不同ASCII字符集。

n=0~10。默认值为0，选择U.S.A方式。

ESC　U　n　　　　　　　　　　　                       设置/取消单向打印

格式：　 ASCII：　ESC　 U　 n

　　　　十进制：　27   85 n

　　　十六进制：　1B　　55 n

说明：

设置/取消单向打印。n＝0~255，仅最低位有效。

当n＝＜×××××××1＞B时，在字符方式下设置为准双向打印,在图形方式下设置为单向打印。

当n＝＜×××××××0＞B时，设置双向打印。

默认值n=0。

ESC　a　n　　　　　　　　　　　　                         选择对齐模式

格式：　 ASCII：　ESC　 a　 n

　　　　十进制：　27   97 n

　　　十六进制：　1B　　61 n

说明：

0 ≤n ≤2, 48 ≤n ≤50

n选择对齐模式，默认值是0。

n    对齐模式

0，48    左对齐

1，49    中对齐

2，50    右对齐

ESC　c　3　n　　　　　　　　　　                     输出纸尽传感器

格式：　 ASCII：　ESC　 c　 3　 n

　　　　十进制：　27   99   51   n

　　　十六进制：　1B   63   33   n

说明：

   0 ≤n ≤255

   n的定义见下表：

位    功能    值

       0    1

0    末端传感器    取消    设定

1    末端传感器    取消    设定

2    纸尽检测器    取消    设定

3    纸尽检测器    取消    设定

4    无定义        

5    无定义        

6    无定义        

7    无定义        

ESC　c　4　 n　　　　　　　　　　　                 设定缺纸时停止打印

格式：　 ASCII：　ESC　 c　 4　 n

　　　　十进制：　27   99   52   n

　　　十六进制：　1B   63   34   n

说明：

设置/取消纸检测器检测到缺纸时停止打印。

N=0~255，仅最低位有效。

当n＝＜*******1＞B时，纸检测器检测到缺纸时停止打印。

当n＝＜*******0＞B时，纸检测器检测到缺纸时不停止打印，以便用户可以将最后一张单据打印至页底。

默认值n=1。

ESC　c 5 n　　　　　　　　                     允许/禁止走纸按键

格式：　 ASCII：　ESC   c   3   n

　　　　十进制：　 27   99 33   n

　　　十六进制：　 1B   63 35   n

说明：

当n＝＜*******1＞B时，禁止纸按键。

当n＝＜*******0＞B时，允许纸按键(默认值)。

ESC　d　n　　　　　　　　　　　                   打印并进纸n字符行

格式：　 ASCII：　ESC　 d　   n

　　　　十进制：　27   100   n

　　　十六进制：　1B   64   n

说明：

打印行缓存里的数据并向前走纸n行。

n=0~255。

ESC　e　n　　　　　　　　　　　                 打印并反向进纸n字符行

格式：　 ASCII：　ESC　 e　   n

　　　　十进制：　27   101   n

　　　十六进制：　1B   65   n

说明：

打印行缓存里的数据并向前走纸n行。

0 ≤n ≤2。

ESC　p　　　　　　　　　　　　　　                   产生钱箱驱动脉冲

格式：　 ASCII：　ESC　 p　   m   n1   n2

　　　　十进制：　27   112   m   n1   n2

　　　十六进制：　1B   70   m   n1   n2

说明：

产生钱箱驱动脉冲。

m＝0,1,48,49 0≤n1≤n2≤255

驱动脉冲形式为：

解释:

打开钱箱脉冲时间为n1×2毫秒。

关闭钱箱脉冲时间为n2×2毫秒。

ESC　r　n　　　　　　　　　　　　　                   选择打印颜色

格式：　 ASCII：　ESC　 r　   n

　　　　十进制：　27   114   n

　　　十六进制：　1B   72   n

说明：

   n = 0，48，黑色打印

n = 1，49，红色打印

默认n = 0。

ESC　t　n　　　　　　　　　　　　　　　                   选择字符集

格式：　 ASCII：　ESC　 t　   n

　　　　十进制：　27   116   n

　　　十六进制：　1B   74   n

说明：

选择中文方式下的ASCII字符集。

0： 选择7×9字符集（默认值）。

1： 选择7×7字符集。

ESC　{　n　　　　　　　　　　　　                 设置/取消倒向打印模式

格式：　 ASCII：　ESC　 {　   n

　　　　十进制：　27   123   n

　　　十六进制：　1B   7B   n

说明：

0 ≤n ≤255

当n的LSB位是0，取消倒向打印模式。

当n的LSB位是1，设置倒向打印模式。

默认n = 0。

FS　 ！　　　　　　　　　　　　　　　　                 汉字综合选择

格式：　 ASCII：　FS　   ！     n

　　　　十进制：　28   33     n

　　　十六进制：　1C   21     n

说明：

n的各位的定义如下：

位    0    1

0    无定义

1    无定义

2    宽正常    汉字倍高

3    高度正常    汉字倍高

4    无定义

5    无定义

6    无定义

7    无下划线    汉字下划线

FS　＆　　　　　　　　　　　　　　                 进入汉字打印方式

格式：　 ASCII：　FS　   ＆

　　　　十进制：　28   38

　　　十六进制：　1C   26

说明：

打印机接收到该命令后，结束本行打印，从下一行开始转为汉字打印方式。

在汉字打印方式时，打印机接收的代码为2字节的标准机内码（均为大于9FH的码），根据该代码寻找打印机的硬汉字字模，打印国标15×16点阵的汉字。

打印机接收到单字节的ASCII码（20H－9FH）时，将打印出相应的5 ×7或7×7点阵字符。

FS　~   S　　　　　　　　　　　                     选择汉字打印速度

格式：　 ASCII：　FS　   ~   S   n

　　　　十进制：　28   126 83   n

　　　十六进制：　1C   7E 53   n

说明：

此功能设置汉字打印模式时的打印速度，当n=1时为汉字高速打印，横向分辨率为144 DPI，纵向分辨率为72 DPI；

当n=0时为正常打印模式，横向分辨率为144 DPI，纵向分辨率为144 DPI 此模式为默认模式。

FS　· 　　　　　　　　　　　　                   退出汉字打印方式

格式：　 ASCII：　FS　   ·

　　　　十进制：　28   46

　　　十六进制：　1C   2E

说明：

　　打印机接收到该命令后，退出汉字打印方式，转为正常的西文打印方式。

FS　2　　　　　　　　　　　　　　　　　                 用户自定义汉字

格式：　 ASCII：　FS　   2   a1   a2 d1 d2 d3 …d32

　　　　十进制：　28   50   248 a2 d1 d2 d3 …d32

　　　十六进制：　1C   32   F8   a2 d1 d2 d3   d32

说明：

  此功能为用户自定义汉字。

a1=F8(十六进制)

a2为A1与FE（十六进制）之间任意值。

因汉字代码为二字节，a1为第一字节，a2为第二字节，可定义94个汉字。

十六进制代码如下：

IC 32 77 21 00 00 38 20 20 20 20 20 24 20 24 20 24 21 24 21 E4 FF 24 20 25 20 26 20 24 20 20 20 20 20 38 20

FS　?　c1   c2　　　　　　　　　　　　　　　　         取消用户自定义汉字

格式：　 ASCII：　FS　 ?　   c1   c2

　　　　十进制：　28   63   c1   c2

　　　十六进制：　1C   3F   c1   c2

说明：

c1和c2指待定义字符的字符编码。c1和c2的取值范围视采用的字符编码系统而定，如下表：

模式选择    C1    C2

日文汉字模式（JIS编码系统）    C1=77H    21H≤C2≤7EH

日文汉字模式（转换JIS编码系统）    C1=ECH    40H≤C2≤7EH80H≤C2≤9EH

简体汉字模式    C1=FEH    A1H≤C2≤FEH

繁体汉字模式    C1=FEH    A1H≤C2≤FEH

FS　C　n　　　　　　　　　　　　　　             设定日文汉字编码系统

格式：　 ASCII：　FS　 C　   n

　　　　十进制：　28   67   n

　　　十六进制：　1C   43   n

说明：

  n = 0, 1

n有如下定义：

n    日文汉字系统

0    JIS编码

1    转换JIS编码

默认n=0。

FS　 S　n1   n2　　　　　　　　                     设定全角汉字字间距

格式：　 ASCII：　FS 　S   n1   n2

　　　　十进制：　28   83   n1   n2

　　　十六进制：　1C　 53   n1   n2

说明：

0≤n1,n2≤127,n1决定字符左边间距，n2决定字符右边间距。

单位是1/160英寸，电源打开时的初始设定为n1=0,n2=2。

FS　 W　　n　　　　　　　　                   设定/取消四倍角汉字模式

格式：　 ASCII：　FS 　W   n

　　　　十进制：　28   87   n

　　　十六进制：　1C　 57   n

说明：

0≤n≤255。

   当n的LSB位是0，取消四倍角汉字模式。

当n的LSB位是1，设置四倍角汉字模式。

默认n=0。

GS ( F pL pH　a m nL nH　　                 设置黑标定位偏移量

格式：　 ASCII：　GS ( F   pL pH　a m nL nH　　　　

十进制：　29 40 70 pL pH a m nL nH

　　十六进制：　1D 28 46 pL pH a m nL nH  

说明：

该命令用于选择黑标定位控制允许，且设置切/撕纸位置或起始打

印位置相对于黑标检测的偏移量。该值以点数计算。

命令相关参数为：

pL+(pHx256)=4 即 pL=4,pH=0

1≤a≤2，

m=0,48

0≤(nL+nHx256)<1700

l    a 用来选择设置切/撕纸位置或起始打印位置的偏移量。

a    功能

1    设置起始打印位置相对于黑标检测位置的偏移量

2    设置切/撕纸位置相对于黑标检测位置的偏移量

l    m=0或48，选择偏移量为前进纸方向计算；

l    nL,nH设置的偏移量对应实际距离为(nL+nHx256)×0.176mm

l    只有执行此命令后GS FF 和 GS V命令有关黑标定位操作方有效；

l    设置起始打印位置偏移量(a=1)在执行GS FF 命令时有效；

l    设置切/撕纸位置偏移量(a=2)在执行GS V m 命令时有效；

l    默认值为nL=nH=0，即黑标检测开关检测到黑标时，当前票面上对应打印头的位置为设定的起始打印位置，当前票面上对应切/撕纸口的位置为设定的切/撕纸位置。

l    关于切/撕纸位置偏移量和起始打印位置偏移量的计算说明

1、    切/撕纸位置到黑标印刷位置的距离L与打印机的固有机械值L0相同，而且切/撕纸位置到起始打印位置的距离Q与打印机构固定的机械值Q0相同时，即用GS（ F命令所设置的偏移量均为0。

2、    当黑标印刷位置到切/纸位置的距离L小于打印的机械值L0时，GS（ F命令的切/撕纸位置偏移量计算为：

        切/撕纸位置偏移量=（L0—L）/0.176（点数）

3、    当黑标印刷位置到切/撕纸位置的距离L大于打印机的机械值L0时，GS（ F命令的切 /撕纸位置偏移量计算为：

        切/撕纸位置偏移量=（L0+相邻两黑标间的距离—L）/0.176（点数）。

注意：在设置切/撕纸位置偏移量时，GS 〈 F命令的参数a应为2。

4、    当切/撕纸位置偏移量不为零或每单的起始打印位置到切/撕纸位置的距离Q大于打印机的机械值Q0）时，GS（ F命令的切/撕纸位置偏移量计算为：

        起始打印位置偏移量=（Q—Q0）/0.176 +切/撕纸位置偏移量。

注意：在设置切/撕纸位置偏移量时，GS 〈 F命令的参数a应为1。

5、    打印结构M—U110（051）的固有机械值

      L0=39mm,     Q0=11mm,

当打印机构安装在机壳内，使得撕纸位置和打印起始位置的固有机械值发生改变时

L0=39+△L     Q0=11+△L

6、    △L的测量方法如下：

1）    先将空白打印纸装入打印机械，撕去出纸口上多余的打印纸

2）    给打印机发20个字符“E”，打印在打印纸上

3）    让打印机发走出足够长度，测量纸端到打印字符“EEEEEEEEEEEEEEEEEEEE”的上沿距离，该值减去11mm，即为△L。切/撕纸位置起始打印位置

GS　 I　　n　　　　　　　                           传送打印机ID

格式：　 ASCII：　GS　 I　　 n

　　　　十进制：　29   73   n

　　　十六进制：　1D　 49   n

说明：

1 ≤n ≤3, 49 ≤n ≤51, 65 ≤n ≤69。

n值定义如下：

n    打印机ID    规格    ID（十六进制）

1，49    打印机模式ID    TM200系列    0D

2，50    ID型号    见下表

3，51    ROM版本ID    ROM版本

65    硬件版本    取决于硬件版本

66    制造商    EPSON

67    打印机名称    TM200

68    打印机串号    取决于打印机串号

69    支持多国语言字符    日本模式：日本汉字中国模式：GB2312台湾模式：BIG-5泰国模式：THAI 3 PASS

GS　V　　　　　　　　　　　　　　　　                 走纸到切纸位置

格式：① ASCII：　GS　   V   m

　　　　十进制：　29   86   m

十六进制：　1D   56   m

② ASCII：　GS　   V   m   n

　　　　十进制：　29   86   m   n

十六进制：　1D   56   m   n

说明：

①m＝1,49。②m＝66，0≤n≤255

M    解       释

1，49    部分切纸（无切刀则无切纸动作）

66    走纸到（切纸位置+n×1/144英寸）位置并部分切纸（无切纸刀则无切纸动作）

GS　 a　 n　　　　　　　　                     设定/取消自动返回状态

格式：　 ASCII：　GS　 a　   n

　　　　十进制：　29   97   n

　　　十六进制：　1D　 61   n

说明：

0≤n≤255。n值定义如下表：

位    功能    值

       0    1

0    钱箱检测信号    取消    设定

1    联机    取消    设定

2    错误状态    取消    设定

3    进纸传感器状态    取消    设定

4    无定义        

5    无定义        

6    无定义        

7    无定义        

GS　 r　 n　　　　　　　　                             状态传送

格式：　 ASCII：　GS　 a　   n

　　　　十进制：　29   114   n

　　　十六进制：　1D　 72   n

说明：

1 ≤n ≤2, 49 ≤n ≤50。n值定义如下表：

n    功能

1，49    纸检测器状态

2，50    钱箱检测器状态

当n=1，49时，传送字节定义如下：

位    功能    值

       0    1

0,1    纸检测信号    有纸    无纸

2,3    纸检测信号    有纸    无纸

4    无定义        

5    无定义        

6    无定义        

7    无定义        

当n=2，50时，传送字节定义如下：

位    功能    值

       0    1

0    钱箱检测信号    低电平    低电平

1    联机        

2    无定义        

3    无定义        

4    无定义        

5    无定义        

6    无定义        

7    无定义        

GS　 z　 0   t1　　t2　　　　　　                 设置联机恢复等待时间

格式：　 ASCII：　GS　 z　 0   t1　　t2

　　　　十进制：　29 122 48   t1　　t2

　　　十六进制：　1D　 7A 30 t1　　t2

说明：

0 ≤t1 ≤255，0 ≤t2 ≤255。

设置进纸等待时间到大约t1 X 500ms，恢复确定时间到大约t2 X 500ms。

t1=0时，一旦有纸插入，打印机即进入恢复确定状态。t2=0时，恢复确定时间取消。

命令(ASCII)    十六进制    功能说明

HT    9    打印位置进行到下一水平制表位置

LF    0a    打印行缓冲器里的内容，并向前走纸一行

CR    0D    打印回车

ESC SP    1B 20 n    设置字符右边的字间距为n个半点距(1/140英寸)

DLE EOT n    10 04    实时状态传送

DLE ENQ n    10 5    对打印机的实时请求

ESC　SP　n    1B 20 n    设置字符右间距

ESC　！　n    1B 21 n    设置字符打印方式

ESC　%　n    1B 25 n    允许/禁止用户自定义字符

ESC　&    1B 26    设置用户自定义字符

ESC　*    1B 2A    设置图形点阵

ESC - n    1B 2D n    设置/取消下划线模式

ESC　2    1B 32    设定1/6英寸换行量

ESC　3　 n    1B 33　n    设定 n/144英寸换行量

ESC　〈    1B 3C    打印头归位

ESC = n    1B 3D n    设备设置/取消

ESC ？ n    1B 3F n    取消用户自定义字符

ESC　＠    1B　40    初始化打印机

ESC　D　NULL    1B　44 0    消除所有的水平制表位置

ESC　E　n    1B　45 0    设置/取消着重模式

ESC　G　n    1B　47 0    设置/取消重叠模式

ESC　J　n    1B　4A n    执行n/144英寸走纸

ESC　K　n    1B　4B n    打印并反向走纸

ESC　R　n    1B　52 n    选择国际字符集

ESC　U　n    1B　55 n    设置/取消单向打印

ESC　a　n    1B　61 n    选择对齐模式

ESC　c　3　n    1B 63 33 n    输出纸尽传感器

ESC　c　4　 n    1B 63 34 n    设定缺纸时停止打印

ESC　c　5　 n    1B 63 35 n    允许/禁止走纸按键

ESC　d　n    1B　64 n    打印并进纸n字符行

ESC　e　n    1B　65 n    打印并反向进纸n字符行

ESC　p    1B　70    产生钱箱驱动脉冲

ESC　r　n    1B　72 n    选择打印颜色

ESC　t　n    1B　73 n    选择字符集

ESC　{　n    1B　7B n    设置/取消倒向打印模式

FS　 ！    1B　75 n    汉字综合选择

FS　 ＆    1C 26    进入汉字打印方式

FS　~   S    1C 7E 53 n    选择汉字打印速度

FS　·    1C 2E    退出汉字打印方式

FS　2    1C 32    用户自定义汉字

FS　?　c1   c2    1C 3F c1 c2    取消用户自定义汉字

FS　C　n    1C 43 n    设定日文汉字编码系统

FS　S　n1 n2    1C　53 n1 n2    设定全角汉字字间距

FS　W　n    1C　57 n    设定/取消四倍角汉字模式

GS ( F pL pH　a m nL nH    1D 28    设置黑标定位偏移量

GS　 I　　n    1D　 49   n    传送打印机ID

GS　V    1D   56   m    走纸到切纸位置

   1D   56 m n    

GS　 a　 n    1D　 61   n    设定/取消自动返回状态

GS　 r　 n    1D　 72   n    状态传送

GS　 z    1D　 7A    设置联机恢复等待时间
```
