package practice.zhuang.mybatis.type;


import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * @author: ZhuangZG
 * @date 2023/1/12 10:56
 * @Description:
 */
public class TypeAliasesRegistry {

    private final static Map<String, Class<?>> TYPE_ALIASES = new HashMap<>();

    public TypeAliasesRegistry() {
        registerAlias("string", String.class);

        registerAlias("byte", Byte.class);
        registerAlias("short", Short.class);
        registerAlias("int", Integer.class);
        registerAlias("integer", Integer.class);
        registerAlias("float", Float.class);
        registerAlias("double", Double.class);
        registerAlias("long", Long.class);
        registerAlias("boolean", Boolean.class);
    }

    public void registerAlias(String alias, Class<?> value) {
        String key = alias.toLowerCase(Locale.ENGLISH);
        TYPE_ALIASES.put(key, value);
    }

    public Class<?> resolveAlias(String alias) {
        alias = alias.toLowerCase(Locale.ENGLISH);
        return TYPE_ALIASES.get(alias);
    }
}
