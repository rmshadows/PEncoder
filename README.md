### PEncoder2.0

[中文使用文档戳我](https://gitee.com/rmshadows/pencoder_cipher_encryptor/blob/master/README.md)

### Info

Name : PEncoder

Version ：v2.0 

Function : Help you manage your password.Based on java,using Swing-GUI;

Environment : Java 11 (Java8 is OK,delete module-info.java),!!Win10 1903 Above!!

Platform : Windows & Linux

Keys Length limit : <16

Password Length limit: <30


### Compile
Compile:
`javac --module-source-path src -d bin -m cn.rmshadows.PEncoderModule -encoding UTF-8`

Run:
`java -p bin -m cn.rmshadows.PEncoderModule/appLauncher.PEncoderGUILauncher`

Jlink Pack:
`jlink --launcher StartPEncoder=cn.rmshadows.PEncoderModule/appLauncher.PEncoderGUILauncher --module-path bin --add-modules cn.rmshadows.PEncoderModule --output PEncoder2.0`


！！This program may not work correctly on the version of win10 18XX.！！What cause this problem maybe notepad.exe.
Open CMD ,take a look at the first few lines, if it looks like this: 
```
Microsoft Windows [版本 10.0.18363.836] 
(c) 2019 Microsoft Corporation。保留所有权利。
```
Good,you'll download archive file in RELEASE ,unzip it then run the 'Start' script to start the application.

1.If you download jar file:

- Windows:Double click or in command lines type `java -jar PEncoder.jar`
- Linux: `chmod +x PEncoder.jar` then `./PEncoder.jar` or `java -jar PEncoder.jar`

2.If you download Jlink file,it's easy: Choosing the correct archive for your platform.Then upzip it,and double click at the "Start" script to launcher the application.

### Get start:

!----------------------------------------!

Before we getting start,you need to know:

- PEncoderDatabasebak : PEncoderDatabasebak file is a passwords record file.It stored password by clear text.All  practice(adding password ,export as csv file ,change password...) are based on it.Hereinafter we call it bak file.

- PEncoderDatabase : PEncoderDatabase file is a coded passwords stored file.It derived from bak file. DO NOT MODIFY THIS FILE ! ! Otherwise all the password stored on the PEncoderDatabase file will be lost ! !Hereinafter we call it DB file.

!----------------------------------------!


- First time use : Launcher the application ,select "-菜单栏(menu bar)-文件(files)-新建(New PEncoderDatabasebak file)".

![00](https://images.gitee.com/uploads/images/2020/0623/213208_b139ada5_7423713.png "屏幕截图.png")

Then you'll see a new 'PEncoderDatabasebak' file has created.Click "-编辑-(Edit)" to modify the bak file ,it'll automatically opened with 'notepad.exe' on Windows or 'Gedit' on Linux ,therefore make sure you had installed them.If you dont have one ,there is a way to solve this:

1.Modifying soure code ,change to the application you used to edit 'txt' file.

2.Find the bak file in your file explorer and open it with your favorite text editor.

![01](https://images.gitee.com/uploads/images/2020/0623/213353_c5884bff_7423713.png "屏幕截图.png")

-We go on:

Open the new bak file,delete the first line (使用前请删除此行，注意英文冒号的使用位置！格式示例：) .Follow the example in the second line:软件平台(Forum):账号名(User name):密码(Password):备注(Remarks).Using colon as the delimiter, each row is divided into four columns (therefore colons are not allowed in these three fields -- Forum ,UserName ,Remarks.【Ps:There will be no semicolon ":" in the encrypted message ,so you don't need to worry about the Password field.】Exceptions: Semicolon in Chinese (：) is OK in the three field mentioned above ,but not allowed in the Password field).Different colon:English colon(`:`),Chinese colon(`：`) ,others:such as en_us(,.:;'"\[]?!),cn_zh(，。：；‘’“”、【】？！).

![输入图片说明](https://images.gitee.com/uploads/images/2020/0623/213632_5b65c264_7423713.png "屏幕截图.png")

for example:

Correct: `Github.com:username:password:my github account.site：www.github.com.` 

As you see ,I the final colon is Chinese colon,so that it's correct.If you can't type a Chinese colon "："，just replace the English colon ":" in the 'Forum','User Name','Remarks' field with other symbol.

Wrong:`Github:username:password:https://www.github.com` The final ":" in the 'Remarks' field will confuse PEncoder and make it wrong ! 

### How to encrypt password ?

First of all ,setup your KeyA & KeyB ,both two keys are using to encryption.

The most important thing is **DO REMEMBER BOTH KAYA & KEYB!** or you will lost all you encrypted password !!

That's what you are using to decrypt and get the clear text of your password. 
Now Let's say that I am going to encrypt and stored information showed below on my win10 laptop :
|Forum|Username|Password|Remarks|
|--|--|--|--|
|Github|Tom|Tom'sGH|site:www.github.com|
|Facebook|Kitty|pwd:666|Null|
|GoogleDrive|Woops@gmail.com|www;;???|phone:+86-15525837262|

Let's make the KeyA:'shadows@shadows' and KeyB:'Google0234' ,typed into the corresponding text boxes.
![02](https://images.gitee.com/uploads/images/2020/0623/214724_1e324115_7423713.png "屏幕截图.png")

Then click "-编辑-(Edit)" it will open with notepad and add our password in it: 

```
Github:Tom:Tom'sGH:site-www.github.com
Facebook:Kitty:pwd:666:Null
GoogleDrive:Woops@gmail.com:www;;???:phone=+86-15525837262
```
![03](https://images.gitee.com/uploads/images/2020/0623/214659_e555b266_7423713.png "屏幕截图.png")

You don't need to save the file right now,because our password hasn't encrypted yet.To encrypt our password,shear the clear password text into "输入(Input)" text area,click "运行(Run)" .Now you can see the encrypted password in the "输出(Output)" area ,click "复制(Copy)" to copy to your clipboard，then 'Ctrl+v' paste it into bak file .When all the clear password text has been encrypted ,you can save the bak file now.
![04](https://images.gitee.com/uploads/images/2020/0623/224107_f843fbf4_7423713.png "屏幕截图.png")
![05](https://images.gitee.com/uploads/images/2020/0623/224730_769689f6_7423713.png "屏幕截图.png")

```
Github:Tom:3281935691A4F06F75B80CE78854FBC0CEE353BA15898A648192B7365CFBAF0B:site-www.github.com
Facebook:Kitty:FDECC95D3217E2A538886B79EFA673E7C176F2BBB9FB5D3C2E4202AEF66C91CD:Null
GoogleDrive:Woops@gmail.com:43BF2DF98BF44651E436F2179A764F0DCA5E66353C7CFEEDFF9087293D9B9FB466C0B2A028542A5CC0737835F5E25B41:phone=+86-15525837262
```
You don't want anyone to see your username and remarks ，just select "菜单栏（Menubar)-文件(File)-编码bak文件(Convert into DB file)" to generate a DB file.After a DB file is generated , don't forget to delete or shred the bak file and store your DB file somewhere(** DO NOT HAVE YOUR DB FILE AND PENCODER IN THE SAME FOLDER .DB FILE IS YOUR PASSWORD RECORD FILE!!You don't  leave your passwords around, do you?**).

![DB](https://images.gitee.com/uploads/images/2020/0623/231339_67927228_7423713.png "屏幕截图.png")

 _**Important!!DO NOT MISPLACE OR MODIFY DB FILE!!**_ or you will lost all your passwords encrypted byPEncoder.

When you open DB file with text editor:

 ![openDB](https://images.gitee.com/uploads/images/2020/0623/233503_fd370b04_7423713.png "屏幕截图.png")

==>DB file may not be compatible between Windows & Linux ,but bak file could be transmission between two platform(bak file coding:UTF-8).


### When I forget my password ,How to retrieve my password or How should I change my password info?

First, put the DB file into the folder where PEncoder in. Run PEncoder enter the Keys you used to encrypt last time.Select "菜单栏(Menubar)-文件(File)-解码DB文件(Decoding DB file)" to convert DB file into bak file.Then make sure you are in the decryption mode(“解密(Decryption)”)【Default mode is encryption mode("加密（Encryption）").】.The "隐藏(Hidden)" button is used to hide the Keys.

![06](https://images.gitee.com/uploads/images/2020/0623/231256_793776c8_7423713.png "屏幕截图.png")
![07](https://images.gitee.com/uploads/images/2020/0623/231910_660634e3_7423713.png "屏幕截图.png")

Click "编辑(edit)" ,open bak file ,copy cipher to input area and run ,you will see your password in the Output area.

![08](https://images.gitee.com/uploads/images/2020/0623/232419_772326a8_7423713.png "屏幕截图.png")

If you had changed your password ,just remove the old cipher.Set encryption mode "加密" on.Repeat what we did in last topic.Replace the old cipher with a new one.For example, my github password change to "asdf666".
 
![09](https://images.gitee.com/uploads/images/2020/0623/232914_6d37c4c1_7423713.png "屏幕截图.png")

Now the bak file become this:
```
Github:Tom:F791B1EA02EBA137E2ADCCC525B1B6896FF23BD5D6A98392E3E574EFE35AEFBC:site-www.github.com
Facebook:Kitty:FDECC95D3217E2A538886B79EFA673E7C176F2BBB9FB5D3C2E4202AEF66C91CD:Null
GoogleDrive:Woops@gmail.com:43BF2DF98BF44651E436F2179A764F0DCA5E66353C7CFEEDFF9087293D9B9FB466C0B2A028542A5CC0737835F5E25B41:phone=+86-15525837262
```
When you had modified the bak file , remember to save and convert to DB file again.

### How to export my password?

You can export your password-info from bak file by clear password text or cipher password text.Before export ,make sure your bak file existed.Select "菜单栏(Menubar)-选项(Options)-导出(Export)" ,you'll see a dialog asks you to choose a export option to go on.Here we are going to talk about the export of clear password text.

Enter your Keys in,Select "菜单栏(Menubar)-选项(Options)-导出(Export)" 

![10](https://images.gitee.com/uploads/images/2020/0623/234452_1b576a89_7423713.png "屏幕截图.png")

Choose "明文密码导出（Export as clear password text）"

![11](https://images.gitee.com/uploads/images/2020/0623/234522_71354350_7423713.png "屏幕截图.png")

Press Enter.If everything is ok ，you will see a CSV file had been generated.Open it with EXCEL:

![12-1](https://images.gitee.com/uploads/images/2020/0623/235252_ec3db24e_7423713.png "屏幕截图.png")
![12-2](https://images.gitee.com/uploads/images/2020/0623/234846_264db1b3_7423713.png "屏幕截图.png")

You can see all your account information and statistics here.

and below is "密码密文导出(Export as cipher)"

![13](https://images.gitee.com/uploads/images/2020/0623/235132_7e817043_7423713.png "屏幕截图.png")

==> On Windows CSV file coding by GB2312 ,Linux by UTF-8.If your CSV is garbled ,just make some changes on the source code or create a new xls file and import data in the CSV file you exported ,choose import coding "GB2312" and export coding "Your system coding(UTF-8? ASCII? ISO8859-1?)".

### How to change my Keys?

If you accidentally give away your encryption keys ,it's easy to change you Keys.As usual, enter your old keys in the KeyA，KeyB area.Before Keys changing,MAKE SURE YOUR BAK FILE EXSITED! Then "菜单栏(Menubar)-选项(Options)-更换密钥(Change Keys)" ,enter your new Keys in the pop-up dialog separated by a slash "/" (So that slashes are not allowed in the new keys!).

Assumption of New Keys are: 

- KeyA:`hello` 
- KeyB：`54321`

![14](https://images.gitee.com/uploads/images/2020/0624/000340_d6f4126d_7423713.png "屏幕截图.png")
![15](https://images.gitee.com/uploads/images/2020/0624/000541_861a55f3_7423713.png "屏幕截图.png")

Press Enter.A new bak file generated.This is in case of a typo.Open New bak file with text editor,try to decrypt your password with new Keys.If everything is OK ,delete old bak file,rename New bak file with "PEncoderDatabasebak" then closed and convert to DB file to stored your encrypted password info.

### Other function
- Alway on top/Cancel on top.
- Right-click menu test

![16](https://images.gitee.com/uploads/images/2020/0624/000943_1e2b873a_7423713.png "屏幕截图.png")
![17](https://images.gitee.com/uploads/images/2020/0624/001023_face0511_7423713.png "屏幕截图.png")

### Thanks for watching
 
- By Ryan Yim 2020-06-24
