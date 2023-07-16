
rem 切换盘符修改到自己的对应位置
E:

rem 这里切换到项目目录内，否则会导致application.pid无法生成
cd  E:\\dev\\idea\\projects\\springboot-quick\\build\\libs

rem 后台执行 启动springboot项目
cmd /c start /b javaw  -jar mybatis-generate-0.0.1.jar  -Dfile.encoding=utf-8 -cp . org.springframework.boot.loader.JarLauncher

rem 使用默认浏览器打开地址
start "C:\Program Files\Maxthon\Bin\Maxthon.exe" "http://localhost:9999"
