package practice.zhuang.mybatis.builder.xml;

import cn.hutool.core.util.StrUtil;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import practice.zhuang.mybatis.builder.BaseBuilder;
import practice.zhuang.mybatis.io.Resources;
import practice.zhuang.mybatis.mapping.MappedStatement;
import practice.zhuang.mybatis.mapping.SqlCommandType;
import practice.zhuang.mybatis.session.Configuration;

import java.io.Reader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
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

            // TODO 解析 数据源标签 和 别名标签

        } catch (Exception e) {
            throw new RuntimeException("parse XML file failed.");
        }

        return configuration;
    }

    /**
     * solve mappers tag
     * @param mappersElement
     * @return
     */
    private void parseMapper(Element mappersElement) throws Exception {
        if (Objects.isNull(mappersElement)) {
            return;
        }
        List<Element> elementList = mappersElement.elements("mapper");
        for (Element element : elementList) {

            String resource = element.attributeValue("resource");
            Reader reader = Resources.readAsReader(resource);

            SAXReader saxReader = new SAXReader();
            Document mapperDocument = saxReader.read(reader);
            Element mapperRootElement = mapperDocument.getRootElement();
            String namespace = mapperRootElement.attributeValue("namespace");

            // SELECT
            List<Element> selectElementList = mapperRootElement.elements("select");
            for (Element selectElement : selectElementList) {

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

                MappedStatement mappedStatement = MappedStatement.builder()
                        .id(statementId).parameterType(parameterType)
                        .resultType(resultType).sqlCommandType(SqlCommandType.SELECT)
                        .sql(sql).parameterMap(parameterMap)
                        .build();
                configuration.addMappedStatement(mappedStatement);
            }
            configuration.addMapper(Class.forName(namespace));
        };

    }
}
