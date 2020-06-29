package lab.galaxy.yahfa;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

import dalvik.system.DexClassLoader;

/**
 * Hook Plug 的 ClassLoader，parent 为 null，优先查询自己的 class，找不到再查询 backup
 */
public class HookClassLoader extends DexClassLoader {
    private ClassLoader mBackup;
    // 插件地址，支持多个插件
    private List<HookClassLoader> customCLassLoaders;
    private String dexOptFilePathBase;

    public HookClassLoader(String dexPath, String optimizedDirectory, String librarySearchPath, ClassLoader backup) {
        super(dexPath, optimizedDirectory, librarySearchPath, String.class.getClassLoader());
        dexOptFilePathBase = optimizedDirectory;
        mBackup = backup;
    }

    // 除了内置插件外的其他定制插件的列表信息
    public void setPluginFilePath(List<String > lists) {
        if(lists != null && !lists.isEmpty()) {
            customCLassLoaders = new LinkedList<>();
            int index = 0;
            for (String var : lists) {
                String directory = dexOptFilePathBase + index;
                ensureDirectory(directory);
                HookClassLoader loader = new HookClassLoader(var, directory , null, mBackup );
                customCLassLoaders.add(loader);
            }
        }
    }



    private void ensureDirectory(String directory ){
        File fl = new File(directory);
        fl.mkdirs();
    }
    @Override
    protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
        Class<?> cls = null;
        try {
            cls = super.loadClass(name, resolve);
        } catch (ClassNotFoundException e) {
            // ignore
        }
        if(cls == null && customCLassLoaders != null) {
            for (HookClassLoader loader : customCLassLoaders) {
                try {
                    cls = loader.loadClass(name, resolve);
                    if(cls != null)  return cls;
                }catch (ClassNotFoundException ex){
                    // ignore
                }
            }
        }
        if (cls == null) {
            cls = mBackup.loadClass(name);
        }
        if (cls == null) {
            throw new ClassNotFoundException(name);
        }
        return cls;
    }
}
