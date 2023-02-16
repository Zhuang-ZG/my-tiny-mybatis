package practice.zhuang.mybatis.reflection.invoker;


public interface Invoker {

    Object invoke(Object target, Object[] args) throws Exception;

    Class<?> getType();

}
