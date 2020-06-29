###### This is a method hook framework for Android ART.  It provides an implementation of Lens `IHookFrameWork` to support Lens advanced functions. OS versions greater than 5.0 are supported currently .

##### Reference：
	1.yhook：https://github.com/Tencent/GT/tree/master/android/GT_SDK/yhook
	2.YAHFA: https://github.com/PAGalaxyLab/YAHFA   GPL3.0

##### Introduction
* Support method hook on OS greater than 5.0.
* Support loading hook plug-in under asset directory.
* Support loading hook plug-ins with specific paths.
As method hook may not be stable , and `YAHFA` is GPL 3.0 license , we suggest using this in debug mode.

##### Setup
	will come soon in jcenter

##### Usage
1. Generate hook plug-in：
	*  Please refer to `ViewTouchHook` in `hookapi` model for instance.
	*  You may use hook plug-in model to compile hook `apk`.
2. Use hook plug-in:
	* Inner plug-in ：Put the plug-in `apk` under assets directory，and rename it to `hookplugin.apk`.
	* Outer plug-in ：
		```Java HookWrapper.addCustomHookPluginPath(String path);```
3. Support Lens hook：
	Please refer to `HookFrameworkImpl` under `app` model. 　Call ```LensUtil.buildConfig().setHookFrameWorkImpl(new HookFramework())``` to set HookFramework for Lens.  
	
##### License
GNU GPL V3

