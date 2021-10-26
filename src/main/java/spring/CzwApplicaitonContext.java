package spring;

import spring.annotation.Autowired;
import spring.annotation.Component;
import spring.annotation.ComponetScan;
import spring.annotation.Scope;
import spring.interfaceModel.BeanAware;
import spring.interfaceModel.BeanPostProcessor;
import spring.interfaceModel.InitializingBean;

import java.beans.Introspector;
import java.io.File;
import java.lang.reflect.Field;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CzwApplicaitonContext {
    private Map<String,BeanDefiniton> beanDefinitonMap = new HashMap<>();
    private Map<String,Object> singletonObjects = new HashMap<>();
    private List<BeanPostProcessor> beanPostProcessorList = new ArrayList<>();
    public CzwApplicaitonContext(Class configClass){
        scan(configClass);
        beanDefinitonMap.forEach((k,v)->{
            String beanName = k;
            BeanDefiniton beanDefiniton =v;
            if(beanDefiniton.getScope().equals("singleton")){
                Object bean = createBean(beanName,beanDefiniton);
                singletonObjects.put(beanName,bean);
            }
        });


    }
    private void scan(Class configClass){
        if(configClass.isAnnotationPresent(ComponetScan.class)){
            ComponetScan componetScanAnnotation = (ComponetScan) configClass.getAnnotation(ComponetScan.class);
            String path = componetScanAnnotation.value();
            path = path.replace(".","/");
            ClassLoader classLoader = CzwApplicaitonContext.class.getClassLoader();
            URL resoure = classLoader.getResource(path);
            File file = new File(resoure.getFile());
            if(file.isDirectory()){
                for(File f:file.listFiles()){
                    String absolutepath = f.getAbsolutePath();
                    absolutepath = absolutepath.substring(absolutepath.indexOf("czw"),absolutepath.indexOf(".class"));
                    absolutepath = absolutepath.replace(String.valueOf(File.separatorChar),".");
                    try{
                        Class clazz = classLoader.loadClass(absolutepath);
                        if(clazz.isAnnotationPresent(Component.class)){
                            if(BeanPostProcessor.class.isAssignableFrom(clazz)){
                                BeanPostProcessor beanPostProcessor = (BeanPostProcessor) clazz.getConstructor().newInstance();
                                beanPostProcessorList.add(beanPostProcessor);
                            }
                            Component component = (Component) clazz.getAnnotation(Component.class);
                            String beanName = component.value();
                            if("".equals(beanName)){
                                beanName = Introspector.decapitalize(clazz.getSimpleName());
                            }
                            BeanDefiniton beanDefiniton = new BeanDefiniton();
                            beanDefiniton.setType(clazz);
                            if(clazz.isAnnotationPresent(Scope.class)){
                                Scope scope = (Scope) clazz.getAnnotation(Scope.class);
                                String value = scope.value();
                                beanDefiniton.setScope(value);
                            }else{
                                beanDefiniton.setScope("singleton");
                            }
                            beanDefinitonMap.put(beanName,beanDefiniton);

                        }
                    }catch (Exception e){

                    }

                }

            }

        }

    }

    private Object createBean(String beanName,BeanDefiniton beanDefiniton){
        Class clazz = beanDefiniton.getType();
        Object instance = null;
        try{
            instance = clazz.getConstructor().newInstance();
            for(Field f : clazz.getDeclaredFields()){
                if(f.isAnnotationPresent(Autowired.class)){
                    f.setAccessible(true);
                    f.set(instance,getBean(f.getName()));
                }
            }
            if(instance instanceof BeanAware){
                ((BeanAware) instance).setBeanName(beanName);
            }
            for(BeanPostProcessor beanPostProcessor:beanPostProcessorList){
                instance = beanPostProcessor.postProcessBeforeInitialization(instance,beanName);
            }
            if(instance instanceof InitializingBean){
                ((InitializingBean) instance).afterPropertiesSet();
            }
            for(BeanPostProcessor beanPostProcessor:beanPostProcessorList){
                instance = beanPostProcessor.postProcessAfterInitialization(instance,beanName);
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        return instance;
    }

    public Object getBean(String beanName){
        if(!beanDefinitonMap.containsKey(beanName)){
            throw new NullPointerException();
        }
        BeanDefiniton beanDefiniton = beanDefinitonMap.get(beanName);
        if(beanDefiniton.getScope().equals("singleton")){
            Object sinletonBean = singletonObjects.get(beanName);
            if(sinletonBean == null){
                sinletonBean = createBean(beanName,beanDefiniton);
                singletonObjects.put(beanName,sinletonBean);
            }
            return sinletonBean;
        }else{
            Object prototypeBean = createBean(beanName,beanDefiniton);
            return prototypeBean;
        }

    }
}
