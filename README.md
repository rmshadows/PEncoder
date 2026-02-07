# PEncoder密码辅助

- 当前版本：2.3

#### 介绍

Java Swing 密码辅助工具

使用略不方便，代码略乱，见谅。

#### 编译与运行（Maven + JPMS）

要求：[Java](https://www.oracle.com/index.html) 17+、[Maven](https://maven.apache.org/) 3.6+

项目为标准 JPMS 模块化 Maven 工程（`src/main/java` 含 `module-info.java`，资源在 `src/main/resources`）。

1.  **编译并打包**：`mvn package -DskipTests` → 生成 `target/pencoder.jar`
2.  **运行**：`java -p target/pencoder.jar -m cn.rmshadows.PEncoderModule/appLauncher.PEncoderGUILauncher`，或直接 `java -jar target/pencoder.jar`
3.  **jlink 自定义运行时**（可选）：先 `mvn package -DskipTests`，再  
    `jlink --launcher StartPEncoder=cn.rmshadows.PEncoderModule/appLauncher.PEncoderGUILauncher --module-path target/pencoder.jar --add-modules cn.rmshadows.PEncoderModule --output PEncoder-runtime`
4.  **GraalVM 原生可执行文件**：在 GitHub 仓库中打开 Actions → 选择 “GraalVM Native Build” → “Run workflow” 手动触发，可为 Windows / Linux / macOS 生成原生可执行文件（使用 Liberica NIK）。**Linux/macOS** 因 AWT 需依赖 JDK 原生库，产物内带 `lib/` 与启动脚本：请用 **`./run.sh`** 启动（勿直接运行 `./PEncoder`），否则可能报 “no awt in java.library.path”。Windows 可直接运行 `PEncoder.exe` 或 `run.bat`。

#### 使用说明

关于PEncoder2.0:


- *PEncoder用于密码加密;
- *PEncoder 支持 Windows、Linux、macOS（跨平台）。
- *PEncoder 版本：Java 11+（若使用 Java 8 需删除 module-info.java）。
- *PEncoder在Windows下要求系统版本在1903（可能是吧？？我猜的）以上，‘CMD’打开看看开头是不是写着

```
Microsoft Windows [版本 10.0.18363.836] 
(c) 2019 Microsoft Corporation。保留所有权利。
```

-  写着2019及以上可以用，写着2018的就用不了，因为格式会乱掉。
- *PEncoderDatabase是PEncoder的非直观数据记录文件，不可直接修改。
- *PEncoderDatabasebak文件是PEncoder的直观数据记录文件。用于用户自行添加、修
- *改密码帐号等信息。格式：“平台名称:用户名:密码:备注”（注意是英文格式的冒号！）
- *使用时请将需要的数据记录文件和PEncoder应用放在同一个目录下。
- *PEncoder使用的是UTF-8编码！


- 关于运行环境:                                                   
    Windows和Linunx一样,安装JDK或者JRE都行,在Java官网可以下载。                                                     
    
- 关于运行：                              
- 1. 如果下载的是[JAR文件](https://gitee.com/rmshadows/pencoder_cipher_encryptor/attach_files/419285/download)：                        

-  Windows下，直接双击打开。或者cmd：                                
     `java -jar PEncoder.jar`     
                                   
   
- Linux下，先给予运行权限，在Terminal：                             
      `sudo chmod +x PEncoder.jar`                                    
-      再运行:                                                        
      `./PEncoder.jar`
                                             
      
- 2.   如果下载的是对应平台的Jlink版本（[Windows戳我](https://gitee.com/rmshadows/pencoder_cipher_encryptor/attach_files/419284/download)、[Linux戳我](https://gitee.com/rmshadows/pencoder_cipher_encryptor/attach_files/419286/download)）就无所谓了，直接运行Start脚本即可。          

_______________________________________________________________________________________________________________
- 使用方法

**快捷键**
- **Ctrl+M**：在工作区切换「加密」/「解密」模式。
- **Ctrl+D**：将 DB 文件解码为 bak 文件（等同于菜单「文件 → 解密数据」）。

**选项（菜单栏 → 选项）**
- **退出时自动将 bak 编码为 DB**：勾选后，每次退出前会先备份 bak 与 DB，再尝试把 bak 编码为 DB；编码失败会询问是否仍退出。可防止误操作导致数据丢失。
- **选择打开 bak 的编辑器**：点击「编辑」按钮时用哪个程序打开 bak 文件。程序会先列出当前系统检测到的编辑器（如 Windows 的记事本/Notepad++/VS Code，Linux 的 gedit/Kate/nano/vim 等，macOS 的 TextEdit/nano/vim 等），找不到再选「浏览…」指定可执行文件。未设置时：Windows 用记事本，Linux 用 gedit，macOS 用系统默认文本编辑。

- Get Start：
1、第一次使用：
    从“-菜单栏-文件-新建-”一个PEncoderDatabasebak文件;

2、编辑PEncoderDatabasebak文件：
    点击按钮“-编辑-”打开PEncoderDatabasebak文件。未在选项中指定编辑器时：Windows 下使用记事本，Linux 下使用 Gedit，macOS 下使用系统默认文本编辑。可在「选项 → 选择打开 bak 的编辑器」中从系统检测到的编辑器里选择，或通过「浏览…」指定其他程序。

格式示例：下面第三列（以冒号隔开算作一列）是我要加密的密码(1234567890、wodewangyiyun、skjbvinewir、rrrrrrrooo)
         [一共四列，分别是：平台名称、用户名、密码和备注]

-----------------------------------------------------------------------

```
腾讯QQ:92463448:1234567890:腾讯QQ
网易云:Wangyiyun@163.com:wodewangyiyun:邮箱
无:无:skjbvinewir:手机锁屏密码
一次性邮箱:sdadsdfgg@af.com:rrrrrrrooo:有效期1年-到2020-03
```

-----------------------------------------------------------------------

    暂时不保存！因为我们的密码还没有加密（现在的密码都是明文密码）！

3、开始加密：
    首先，PEncoder需要你提供两个密钥KeyA和KeyB。这两个密钥将用于加密和解密，所以务必牢记！
    丢失这两个密钥你将解不开加密后的密码！

    #注意！#
    #KeyA和KeyB的要求是：
    #（1）不允许携带中文特殊符号，例如："【"、"】"、"："、"；"等等。
    #（2）长度均不超过16位;
    
    在Key的输入框中输入KeyA和KeyB，假设咱们的KeyA是："shadows"，KeyB是："54321"。
    [左下角的-隐藏-按钮允许你隐藏所输入的KeyA和KeyB]
    接下来就可以开始加密了！
    剪切需要加密的明文密码（如本例中第一个明文密码：1234567890）到"-输入-"的文本框中，然后点击运行。
    [请确保左下角模式选择处于"-加密-"状态。]
    
    #注意！#
    #明文密码最长只支持30位，且不允许有特殊符号，例如："【"、"】"、"："、"；"等等，这些都符号是不允许的。#
    
    运行后，"-输出-"文本框中将得到加密后的字符串，点击“-复制-”按钮，粘贴得到的加密字符串于刚才的PEncoderDatabasebak文件中。
    如下图（用加密后的字符串代替原来的明文密码）：

----------------------------------------------------------------------------------------------------------------

```
腾讯QQ:92463448:2F24F3CF1AC52B36AED07B7A0E0737AAFA4BD25A5EE07059B451B0F637EDD05D:腾讯QQ
网易云:Wangyiyun@163.com:C2AA6B8F2AED701672E761563C579BD5EFDD81BAB39F4A91474C5207DA03DFD6:邮箱
无:无:7BCCAB5BCDF307C3B9E63FCF6F8D76F0F28CD0AF9910FA67AF86CC7906E706D1:手机锁屏密码
一次性邮箱:sdadsdfgg@af.com:323F8F31F5A764A931C40D4F181E43F35D5B173AB5440B8C85E22D32B643DFF8:有效期1年-到2020-03
```

----------------------------------------------------------------------------------------------------------------

    用记事本或Gedit编辑后记得保存（UTF-8编码），然后关闭PEncoderDatabasebak文件。
这样你就的到了一份加密后的密码记录本。此时你可以将PEncoderDatabasebak文件保存在其他位置（至少和PEncoder分开存放，以确保别人
得到你的密码记录文件或者PEncoder后无从下手。）。

    #注意#
    #此时的PEncoderDatabasebak文件中除了密码，都是明文信息！#
    
    如果你不想让人太轻松的看到你的用户名等信息，你可以使用“-菜单栏-文件-编码PEncoderDatabasebak文件-"进行简单的编码，这时会
    生成一个PEncoderDatabase文件;
    虽然PEncoderDatabase文件没法直接阅读，但是有经验的人依然可以读取出其中包含的内容。不过你莫要慌张，其中的加密过的密码信息
    将很难被破解（除非他拥有KeyA和KeyB!!还有这个软件...）

下面是生成的PEncoderDatabase文件内容：

----------------------------------------------------------------------------------------------------------------

```
闽:::
D6A4507ABC42ABC1016C080409A278C7356FE289C87A207B263C6882E115F078776F619CE3E158C91A4AB533F6A5549F0EACAEA96D6E170E91AE622039B543A5B511FCC0D341DE4BA80157C7BAFA2FFA99C4F768F2BAC13D81FFDFA170886612772884DD304286926E549E9D2E91DF7D039225464E8C92C092D5D9E43AE45521C2F6A6FA911B3D932719A414B5B1908CAF87EF4618B3E5386CBA2C0C3ECD356616821457DF4F6DBF66EC3DAD17D4BD9C4D9F20A36A4D6483CEB429864E26DA53B66DC6F48713FFAEC4104B02C8E7DB8613BF5C8585DB50405B3E73180ABDEEA5C086815DEE23295530CCC1CA444FFD5B9FACA49ABFFD85DFA674B5AF118D1D88EFAFCEA2E275A6E8921D3AE65678CB7892A37EBBBE344400BF8E71055DFD297F315E917329E9ED0FAA5FE106074B97A78F348178079AB86CA14F7F4D873803F4A84710DFB6FC507AD4CA14C6905035B46DBD0FAC10A30069867CA004EBE4AA362B4870B6D958E82955687A8D87EB71F246392A93BA745D4141066CE149419EC05C543AC479814456B957CB6066A89B76693A0F18EAFE83206E9B4C4404A38D64C15C7D23F0711260444D39D3DD6F3F4F5C00033E05AEDE2F6774A52E9B35F9636D87514D72125723B72E71A83CB06F283486C7B7E28D3FFA4D1739F936C20434E2E31D908633CFEACB8DF6DF5393C8D3FF721A40EE0ECDC5CBD15675ABA6D5944E5A654B5DBF46CF587C24B2E50C6FAE
```

----------------------------------------------------------------------------------------------------------------

    生成PEncoderDatabase文件后，你就可以删除PEncoderDatabasebak文件了。
也就是说，PEncoderDatabase文件和PEncoderDatabasebak文件你必须保存其中一个，来记录你加密后的密码信息（别闹，要不拿什么
来解密呢？）

记住！重要的几点：
！！务必牢记KeyA和KeyB密钥！！
！！PEncoderDatabasebak文件和PEncoderDatabase文件必须保存，记得和PEncoder分开存放！！
！！PEncoder所有的文件编码都是采用UTF-8！！

4、解密：
    当你忘记密码，又想得到明文密码时。
你需要：将PEncoderDatabase或者PEncoderDatabasebak文件和PEncoder文件放在同一个目录下。

  如果是PEncoderDatabase文件，请先打开PEncoder，点击”-菜单栏-文件-解码PEncoderDatabase文件-“这样你又的到了PEncoderData
  basebak文件。

  如果你保存的是PEncoderDatabasebak文件就不需要解码了。
  直接点击”-编辑-“打开PEncoderDatabasebak文件。复制加密后的字符串，返回PEncoder界面，左下角模式选择“解密”。
  粘贴你所复制的加密后的字符串到 “-输入-” 文本框中，点击”-运行-“，在”-输出-“文本框中你将会得到你所需要的明文密码。

查阅完密码后，记得删除PEncoderDatabasebak文件[PEncoderDatabase文件和PEncoderDatabasebak文件保留一个，保留其中一个
删除另一个!]。

############################################################################################################

    ￥  如果明文密码信息改变了  ￥
    你需要的是修改PEncoderDatabasebak中的第三列的信息;
    比如：我要修改的是QQ密码，把原密码”1234567890“修改成“uuuu”。

----------------------------------------------------------------------------------------------------------------

```
腾讯QQ:92463448:2F24F3CF1AC52B36AED07B7A0E0737AAFA4BD25A5EE07059B451B0F637EDD05D:腾讯QQ
网易云:Wangyiyun@163.com:C2AA6B8F2AED701672E761563C579BD5EFDD81BAB39F4A91474C5207DA03DFD6:邮箱
无:无:7BCCAB5BCDF307C3B9E63FCF6F8D76F0F28CD0AF9910FA67AF86CC7906E706D1:手机锁屏密码
一次性邮箱:sdadsdfgg@af.com:323F8F31F5A764A931C40D4F181E43F35D5B173AB5440B8C85E22D32B643DFF8:有效期1年-到2020-03
```

----------------------------------------------------------------------------------------------------------------

    首先执行加密步骤（见前文），输入之前用于加密的KeyA和KeyB，加密后得到：
    C3A8767CA257691EA3863A7410692D1BB25C4D5F8D76D58A53BE61EC05254817
    替换掉原先的密码位置，得到：

----------------------------------------------------------------------------------------------------------------

```
腾讯QQ:92463448:C3A8767CA257691EA3863A7410692D1BB25C4D5F8D76D58A53BE61EC05254817:腾讯QQ
网易云:Wangyiyun@163.com:C2AA6B8F2AED701672E761563C579BD5EFDD81BAB39F4A91474C5207DA03DFD6:邮箱
无:无:7BCCAB5BCDF307C3B9E63FCF6F8D76F0F28CD0AF9910FA67AF86CC7906E706D1:手机锁屏密码
一次性邮箱:sdadsdfgg@af.com:323F8F31F5A764A931C40D4F181E43F35D5B173AB5440B8C85E22D32B643DFF8:有效期1年-到2020-03
```

----------------------------------------------------------------------------------------------------------------

    保存后，同理，可以编码成PEncoderDatabase文件进行保存。老规矩，保存其一，分开存放。

5、导出你的密码到CSV文件（你可以把它当作Excel表格）：
    本功能用于统计你的用户，密码等信息。导出文件格式为CSV。
    可以导出明文密码的CSV文件，也可以导出密文形式的文件，这个在对话框里的下拉框进行选择;
    Windows下默认GB2312编码。Linux下默认UTF-8

    #注意#
    #如果导出的是UTF-8编码格式的CSV文件。在一些Windows电脑上出现中文乱码请参见下文-解决办法。#
    #此功能要求PEncoderDatabasebak文件的存在！#

———>如果你导出的CSV文件在Windows下打开是乱码(一般Windows下默认导出GB2312,如果你导出的CSV文件没乱码，就不用关注这一部分了)，
    则该CSV文件可能是UTF-8格式，所以请进行下列操作：
    %解决办法%
    在Windows下新建一个Excel表格[xls结尾的就行了，当然，xlsx也是没问题的。]，菜单栏点击“-数据-”，选择”-导入数据-“。
导入数据中选择，”-导入CSV文件-“，选择PEncoder导出的CSV文件后，选择编码：”UTF-8“。
导入后就能看到正常的中文啦（因为中文的Windows一般默认是GBK等等编码，而Linux默认UTF-8等等，不同的编码系统当然显示的不一样咯。
就像你拿ASCII编码的TXT文件用UTF-8编码打开乱码一样。）

6、更换密钥：
    如果你不小心泄漏了你的KeyA和KeyB密钥的信息。又或许是其他原因，你想更换密钥，很简单。

    #注意#
    #此功能要求PEncoderDatabasebak文件存在！#

首先，你需要吧你原来的KeyA和KeyB输入在相应的文本框中【重要】
点击”-菜单栏-选项-更换密钥-“，出现了对话框，要求你输入新的Key。
新密钥的格式是：
新的KeyA/新的KeyB
“/”是分割符号，所以不允许/出现在密钥中！

假设我原来的Key是：
KeyA：shadows
KeyB：54321
我要修改成：
KeyA：133223
KeyB：1433223v
首先我应该把原先的Key输入在对应的KeyA和KeyB文本框中。
然后点击”-菜单栏-选项-更换密钥-“，出现了对话框。
我应该在弹出的对话框中输入：133223/1433223v
点击确认，他就生成一个新的PEncoderDatabasebak文件。名字是PEncoderDatabasebakNEW
为什么没有覆盖原PEncoderDatabasebak文件呢？因为怕新的密钥输入错误，比如不小心多输入了一个字符，手快不小心点到了确认。。拉闸
下面是新生成的PEncoderDatabasebak文件内容：

----------------------------------------------------------------------------------------------------------------

```
腾讯QQ:92463448:FF3FC9A049D8EB7E4D847053BD47D4D0123C156F3B2D83EDF7005DFDF0F65982:腾讯QQ
网易云:Wangyiyun@163.com:E873BC59E240D5D08EBC5F4640B8A6BEE21PEncoderDatabase4BF117550F316631A123E56CC2F:邮箱
无:无:091E8083878288B0486860DE35D6A41F0508B53E36186D921925216D7PEncoderDatabase9CA61:手机锁屏密码
一次性邮箱:sdadsdfgg@af.com:1D561C86A33FF061E23069DFE92957286B01451BC435AFE06BF56B6DC8F90185:有效期1年-到2020-03
```

----------------------------------------------------------------------------------------------------------------

    接下来你懂的，确认无误就将原来的PEncoderDatabase和PEncoderDatabasebak文件删除。重命名新生成的PEncoderDatabasebakNEW
    文件为PEncoderDatabasebak。
要编码成PEncoderDatabase文件取决于你自己。老规矩，保留一个，删掉另一个，和PEncoder应用程序分开保存。

7、关于帮助菜单：点击「帮助 → 如何使用」可查看简要说明与快捷键；其他详见本 README。

8、2.0版本更新日志：

                  %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
                  % 优化了CSV导出功能，Windows下采用GB2312编码导出，不再乱码。    %
                  % 新增了窗口置顶功能。                                      %
                  % 优化了PEncoderDatabase文件生成。                         %
                  % 优化了用户输入框过滤规则。                                 %
                  %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%


        That‘s  all
        祝大家生活愉快！


                                                                                      by Ryan 2020-06-20

----------------------------------------------------------------------------------------------------------------

### 关于

这个嘞，是自己写的第一个Java窗口应用。毕竟咱也不是计算机专业…敲代码为的是方便生活。我密码太多了，全是长密码，一个平台一个不同长密码。我记不住那么多密码，于是我就写了这个小东西辅助我管理密码。因为能力有限（喵喵叹气，咳咳），各位如果想改进下可以自己把源代码拉过去修改，我就不更新啦！如果觉得不好用（那肯定，这么麻烦的东西。市场上那些密码记录软件他不香吗？），也就看看作罢，图个娱乐。


写这个小东东之前，也用过别人写的小玩意儿。比如加密猫[项目地址 > https://github.com/Corydonbyte/jiamimao ]。个人觉得还是有点好用的
吧。或许加密猫和 `Hat.sh` [项目地址 > https://github.com/sh-dv/hat.sh ]有点关系(猜的)，因为加密猫加密后的文件头部好像就带有[Hat.sh](https://hat.sh/)的字样，界面也差不多。不管怎样，还是带着一颗感谢的心，毕竟方便了大噶的生活、提升了办公效率，这都是好事儿。


项目地址：> https://gitee.com/rmshadows/pencoder_cipher_encryptor <

### 截屏

![主界面](https://images.gitee.com/uploads/images/2020/0621/125655_a43e35b4_7423713.png "屏幕截图.png")
![加  密](https://images.gitee.com/uploads/images/2020/0621/125858_2e0485ea_7423713.png "屏幕截图.png")
![菜 单1](https://images.gitee.com/uploads/images/2020/0621/130007_ca1d2e18_7423713.png "屏幕截图.png")
![菜 单2](https://images.gitee.com/uploads/images/2020/0621/130109_e3d66c0f_7423713.png "屏幕截图.png")
![导 出1](https://images.gitee.com/uploads/images/2020/0621/130152_221e070d_7423713.png "屏幕截图.png")
![导 出2](https://images.gitee.com/uploads/images/2020/0621/130512_96972d62_7423713.png "屏幕截图.png")
![换密钥](https://images.gitee.com/uploads/images/2020/0621/130229_a963bd4d_7423713.png "屏幕截图.png")

### 更新日志 - Update Log

- 2026.02.07——2.3
  - 版本与日期统一更新
- 2022.08.08——2.2
  - 修复了 DB 文件跨平台编码错误问题
  - 新增：退出时可选自动将 bak 编码为 DB（退出前自动备份，防止误操作丢失数据）
  - 新增：快捷键 Ctrl+D 解码 DB 为 bak，Ctrl+M 切换加密/解密模式
  - 新增：选项「选择打开 bak 的编辑器」——先列出系统检测到的编辑器，找不到再浏览指定；支持 Windows / Linux / macOS
  - 帮助说明中补充快捷键与选项说明；其他详见本 README

### 许可

[LICENSE](https://gitee.com/rmshadows/pencoder_cipher_encryptor/blob/master/LICENSE)