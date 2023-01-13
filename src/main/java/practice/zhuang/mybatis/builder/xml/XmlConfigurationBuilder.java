package practice.zhuang.mybatis.builder.xml;

import cn.hutool.core.util.ClassUtil;
import cn.hutool.core.util.StrUtil;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import practice.zhuang.mybatis.builder.BaseBuilder;
import practice.zhuang.mybatis.datasource.DataSourceFactory;
import practice.zhuang.mybatis.io.Resources;
import practice.zhuang.mybatis.mapping.BoundSql;
import practice.zhuang.mybatis.mapping.Environment;
import practice.zhuang.mybatis.mapping.MappedStatement;
import practice.zhuang.mybatis.mapping.SqlCommandType;
import practice.zhuang.mybatis.session.Configuration;
import practice.zhuang.mybatis.transaction.TransactionFactory;
import practice.zhuang.mybatis.type.TypeAliasesRegistry;

import javax.sql.DataSource;
import java.io.Reader;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author: ZhuangZG
 * @date 2023/1/10 14:28
 * @Description:
 */
public class XmlConfigurationBuilder extends BaseBuilder {

    private Element root;

    public XmlConfigurationBuilder(Reader reader) {
        super(new Configuration());
        SAXReader saxReader = new SAXReader();
        try {
            Document document = saxReader.read(reader);
            root = document.getRootElement();
        } catch (Exception e) {
            throw new RuntimeException("could not read mybatis config file");
        }
    }

    public Configuration parse() {
        try {
            parseMapper(root.element("mappers"));
            parseTypeAlias(root.element("typeAliases"));
            parseEnvironment(root.element("environments"));

        } catch (Exception e) {
            throw new RuntimeException("parse XML file failed.");
        }

        return configuration;
    }

    /**
     * resolve mappers tag of XML
     * @param mappersElement
     * @return
     */
    private void parseMapper(Element mappersElement) throws Exception {
        if (Objects.isNull(mappersElement)) {
            return;
        }
        List<Element> mapperElements = mappersElement.elements("mapper");
        for (Element element : mapperElements) {

            String resource = element.attributeValue("resource");
            Reader reader = Resources.readAsReader(resource);

            SAXReader saxReader = new SAXReader();
            Document mapperDocument = saxReader.read(reader);
            Element mapperRootElement = mapperDocument.getRootElement();
            String namespace = mapperRootElement.attributeValue("namespace");

            // SELECT
            List<Element> selectElements = mapperRootElement.elements("select");
            for (Element selectElement : selectElements) {

                String statementId = selectElement.attributeValue("id");
                statementId = StrUtil.join(StrUtil.DOT, namespace, statementId);
                String resultType = selectElement.attributeValue("resultType");
                String parameterType = selectElement.attributeValue("parameterType");

                Map<Integer, String> parameterMap = new HashMap<>(2);
                String sql = selectElement.getText();
                Pattern pattern = Pattern.compile("(#\\{(.*?)})");
                Matcher matcher = pattern.matcher(sql);
                for (int i = 1; matcher.find(); i++) {
                    String g1 = matcher.group(1);
                    String g2 = matcher.group(2);
                    parameterMap.put(i, g2);
                    sql = sql.replace(g1, "?");
                }

                BoundSql boundSql = BoundSql.builder().sql(sql).parameterType(parameterType)
                        .parameterMap(parameterMap).resultType(resultType)
                        .build();
                MappedStatement mappedStatement = MappedStatement.builder()
                        .id(statementId).sqlCommandType(SqlCommandType.SELECT)
                        .boundSql(boundSql).build();

                configuration.addMappedStatement(mappedStatement);
            }
            configuration.addMapper(Class.forName(namespace));
        };

    }

    /**
     * resolve environments tag of XML
     * @param environmentsElement
     */
    private void parseEnvironment(Element environmentsElement) throws Exception {
        if (Objects.isNull(environmentsElement)) {
            return;
        }

        Environment defaultEnvironment = configuration.getEnvironment();

        // id
        String defaultEnvironmentId = environmentsElement.attributeValue("default");
        defaultEnvironment.setId(defaultEnvironmentId);

        List<Element> environmentElements = environmentsElement.elements("environment");
        for (int i = 0; i < environmentElements.size(); i++) {
            Element environmentElement = environmentElements.get(i);
            String environmentId = environmentElement.attributeValue("id");
            if (!defaultEnvironmentId.equals(environmentId)) {
                continue;
            }

            // transactionManager
            Element transactionManagerElement = environmentElement.element("transactionManager");
            String transactionType = transactionManagerElement.attributeValue("type");
            TypeAliasesRegistry typeAliasesRegistry = configuration.getTypeAliasesRegistry();
            Class<?> transactionClass = typeAliasesRegistry.resolveAlias(transactionType);
            TransactionFactory transactionFactory = (TransactionFactory) transactionClass.getDeclaredConstructor().newInstance();
            defaultEnvironment.setTransactionManager(transactionFactory);

            // datasource
            Element dataSourceElement = environmentElement.element("dataSource");
            Class<?> dataSourceType = typeAliasesRegistry.resolveAlias(dataSourceElement.attributeValue("type"));
            DataSourceFactory dataSourceFactory = (DataSourceFactory) dataSourceType.getDeclaredConstructor().newInstance();
            Properties properties = new Properties();
            dataSourceElement.elements("property").forEach(p -> {
                properties.setProperty(p.attributeValue("name"), p.attributeValue("value"));
            });
            dataSourceFactory.setProperties(properties);
            DataSource dataSource = dataSourceFactory.getDataSource();
            defaultEnvironment.setDataSource(dataSource);

        }

    }

    /**
     * resolve typeAliases tag of XML
     * @param typeAliasesElement
     */
    private void parseTypeAlias(Element typeAliasesElement) {
        if (Objects.isNull(typeAliasesElement)) {
            return;
        }

        TypeAliasesRegistry typeAliasesRegistry = configuration.getTypeAliasesRegistry();

        // <typeAlias>
        List<Element> typeAliasElements = typeAliasesElement.elements("typeAlias");
        for (int i = 0; i < typeAliasElements.size(); i++) {
            Element typeAliasElement = typeAliasElements.get(i);
            String alias = typeAliasElement.attributeValue("alias");
            String type = typeAliasElement.attributeValue("type");
            Class<?> clazz;
            try {
                clazz = Class.forName(type);
            } catch (Exception e) {
                throw new RuntimeException("could not resolve class :" + type);
            }
            typeAliasesRegistry.registerAlias(alias, clazz);
        }

        // <package>
        List<Element> packageElements = typeAliasesElement.elements("package");
        for (int i = 0; i < packageElements.size(); i++) {
            Element packageElement = packageElements.get(i);
            String packageName = packageElement.attributeValue("name");
            Set<Class<?>> classes = ClassUtil.scanPackage(packageName);
            classes.forEach(c -> {
                String className = c.getSimpleName();
                String alisa = className.substring(0,1)+className.substring(1);
                typeAliasesRegistry.registerAlias(alisa, c);
            });
        }
    }
}
