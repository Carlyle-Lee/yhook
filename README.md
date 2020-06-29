###### 这是用于支持Lens hook 能力的java 方法Hook 框架。实现了Lens IHookFrameWork接口。支持加载内置hook插件或多个自定义hook 方法插件。支持5.0~10.0系统。 

##### 引用：
	1.yhook：https://github.com/Tencent/GT/tree/master/android/GT_SDK/yhook
	2.YAHFA: https://github.com/PAGalaxyLab/YAHFA   GPL3.0

##### 介绍
* 支持 5.0~10.0 Android 系统方法hook能力。
* 支持加载assets中内置的hookplugin。
* 支持加载多个自定义路径的hookplugin。
由于稳定性、可靠性、开源协议的问题，建议在debug阶段开发模式下使用。

##### 安装
	即将提交jcenter

##### 使用
1. 制作hook 插件：
	*  仿造 hookapi 模块下ViewTouchHook实现。
	*  使用 hook plugin 编译出hook 插件。
2. 使用hook 插件
	* 内置方式：将插件放置于assets下，命名为hookplugin.apk
	* 外置方式：HookWrapper.addCustomHookPluginPath(String);
3. 对接Lens Hook能力：
	参照app 模块下　HookFrameworkImpl　实现Lens IHookFrameWork接口。并调用LensUtil.buildConfig().setHookFrameWorkImpl(new HookFramework())设置Hook框架实现层。
	
##### License
GNU GPL V3