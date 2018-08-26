/**
 * 唯有读书,不慵不扰
 */
package com.xiaoyu.common.util;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.elasticsearch.action.delete.DeleteRequestBuilder;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetRequestBuilder;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequestBuilder;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.action.update.UpdateRequestBuilder;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.QueryStringQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.highlight.HighlightField;

/**
 * 2017年4月25日下午1:48:19
 * 
 * @author xiaoyu
 * @description elasticsearch封装
 */
public class ElasticUtils {

    private static final Logger logger = LoggerFactory.getLogger(ElasticUtils.class);
    private static Client client;
    private static final String CLUSTER_NAME = "xiaoyu";

    private static String HOST1 = "localhost";
    private static int PORT1 = 9300;

    // private String index = "website_article";
    // private String type = "news";

    private static Client init() {
        // 自动嗅探整个集群的状态，把集群中其它机器的ip地址加到客户端中
        final Settings sets = Settings.settingsBuilder().put("tclient.transport.sniff", true)
                .put("cluster.name", ElasticUtils.CLUSTER_NAME).build();
        return ElasticUtils.client = TransportClient.builder().settings(sets).build().addTransportAddress(
                new InetSocketTransportAddress(new InetSocketAddress(ElasticUtils.HOST1, ElasticUtils.PORT1)));
    }

    public static void insert(String index, String type, String jsonData) {
        try {
            ElasticUtils.init();
            final IndexRequestBuilder builder = ElasticUtils.client.prepareIndex(index, type);
            final IndexResponse resp = builder.setSource(jsonData).get();
            ElasticUtils.logger
                    .info("insert doc[id=" + resp.getId() + "];index[" + index + "];type" + "[" + type + "]");
        } catch (final Exception e) {
            e.printStackTrace();
        } finally {
            if (ElasticUtils.client != null) {
                ElasticUtils.client.close();
            }
        }
        return;
    }

    public static void insert(String index, String type, String id, String jsonData) {
        ElasticUtils.checkNull(id);
        try {
            ElasticUtils.init();
            final IndexRequestBuilder builder = ElasticUtils.client.prepareIndex(index, type);
            final IndexResponse resp = builder.setId(id).setSource(jsonData).get();
            ElasticUtils.logger
                    .info("insert doc[id=" + resp.getId() + "];index[" + index + "];type" + "[" + type + "]");
        } catch (final Exception e) {
            e.printStackTrace();
        } finally {
            if (ElasticUtils.client != null) {
                ElasticUtils.client.close();
            }
        }
    }

    public static String get(String index, String type, String id) {
        ElasticUtils.checkNull(id);
        try {
            ElasticUtils.init();
            final GetRequestBuilder builder = ElasticUtils.client.prepareGet(index, type, id);
            final GetResponse resp = builder.get();
            final String json = resp.getSourceAsString();
            ElasticUtils.logger.info("get doc[id=" + resp.getId() + "];index[" + index + "];type" + "[" + type + "]"
                    + ";status=" + resp.isExists());
            return json;
        } catch (final Exception e) {
            e.printStackTrace();
        } finally {
            if (ElasticUtils.client != null) {
                ElasticUtils.client.close();
            }
        }
        return null;
    }

    public static <T> T getObject(String index, String type, String id) {
        ElasticUtils.checkNull(id);
        try {
            ElasticUtils.init();
            final GetRequestBuilder builder = ElasticUtils.client.prepareGet(index, type, id);
            final GetResponse resp = builder.get();
            @SuppressWarnings("unchecked")
            final T t = (T) resp.getSourceAsString();
            ElasticUtils.logger.info("get doc[id=" + resp.getId() + "];index[" + index + "];type" + "[" + type + "]"
                    + ";status=" + resp.isExists());
            return t;
        } catch (final Exception e) {
            e.printStackTrace();
        } finally {
            if (ElasticUtils.client != null) {
                ElasticUtils.client.close();
            }
        }
        return null;
    }

    public static void delete(String index, String type, String id) {
        ElasticUtils.checkNull(id);
        try {
            ElasticUtils.init();
            final DeleteRequestBuilder builder = ElasticUtils.client.prepareDelete(index, type, id);
            final DeleteResponse resp = builder.get();
            ElasticUtils.logger.info("delete doc[" + resp.getId() + "];index[" + index + "];type" + "[" + type + "];"
                    + "status=" + resp.isFound());
        } catch (final Exception e) {
            e.printStackTrace();
        } finally {
            if (ElasticUtils.client != null) {
                ElasticUtils.client.close();
            }
        }
    }

    public static void update(String index, String type, String id, String jsonData) {
        ElasticUtils.checkNull(id);
        try {
            ElasticUtils.init();
            final UpdateRequestBuilder builder = ElasticUtils.client.prepareUpdate(index, type, id);
            final UpdateResponse resp = builder.setDoc(jsonData).get();
            ElasticUtils.logger.info("update doc[id=" + resp.getId() + "];index[" + resp.getIndex() + "];type" + "["
                    + resp.getType() + "]");
        } catch (final Exception e) {
            e.printStackTrace();
        } finally {
            if (ElasticUtils.client != null) {
                ElasticUtils.client.close();
            }
        }
    }

    public static void upsert(String index, String type, String id, String jsonData) {
        ElasticUtils.checkNull(id);
        try {
            ElasticUtils.init();
            final UpdateRequestBuilder builder = ElasticUtils.client.prepareUpdate(index, type, id);
            final UpdateResponse resp = builder.setDoc(jsonData).setUpsert(jsonData).get();
            ElasticUtils.logger.info("update doc[id=" + resp.getId() + "];index[" + resp.getIndex() + "];type" + "["
                    + resp.getType() + "]");
        } catch (final Exception e) {
            e.printStackTrace();
        } finally {
            if (ElasticUtils.client != null) {
                ElasticUtils.client.close();
            }
        }
    }

    public static void upsertList(String index, String type, Map<String, String> jsonMap) {
        try {
            ElasticUtils.init();
            final Iterator<Entry<String, String>> iter = jsonMap.entrySet().iterator();
            UpdateRequestBuilder builder = null;

            while (iter.hasNext()) {
                final Entry<String, String> e = iter.next();
                builder = ElasticUtils.client.prepareUpdate(index, type, e.getKey());
                builder.setDoc(e.getValue()).setUpsert(e.getKey()).get();
            }
        } catch (final Exception e) {
            e.printStackTrace();
        } finally {
            if (ElasticUtils.client != null) {
                ElasticUtils.client.close();
            }
        }
    }

    private static void checkNull(String id) {
        if ("".equals(id) || id == null) {
            throw new IllegalArgumentException("id should given a valid value");
        }
    }

    public static <T> List<T> search(String index, String type, String query, String... fields) {
        SearchHits hits = null;
        try {
            ElasticUtils.init();
            final SearchRequestBuilder builder = ElasticUtils.client.prepareSearch(index);
            builder.setTypes(type)
                    .setSearchType(SearchType.DEFAULT)
                    .setExplain(true)
                    // 返回source
                    .setFetchSource(true);
            // .setNoFields()//不返回参数;
            // 设置高亮
            // *代表全部高亮 fields == null ? null :
            // fields[0]
            builder.addHighlightedField("*")
                    .setHighlighterPreTags("<span style=\"color:red\">")
                    .setHighlighterPostTags("</span>")
                    .setHighlighterForceSource(true)
                    .setHighlighterRequireFieldMatch(false);

            final BoolQueryBuilder qb = new BoolQueryBuilder();
            final QueryStringQueryBuilder queryBuilder = QueryBuilders.queryStringQuery(query);
            qb.filter(queryBuilder);

            final SearchResponse resp = builder.addFields(fields).setQuery(qb).get();
            hits = resp.getHits();

            ElasticUtils.logger.info("search docs with '" + query + "' hits " + hits.getTotalHits() + " ; " + "took  "
                    + resp.getTookInMillis() + " ms;status=" + resp.status());
        } catch (final Exception e) {
            e.printStackTrace();
        } finally {
            if (ElasticUtils.client != null) {
                ElasticUtils.client.close();
            }
        }
        return ElasticUtils.highLightHandler(hits);
    }

    public static <T> List<T> search(String[] indexs, String[] types, Integer pageNum, Integer pageSize, String query,
            String... fields) {
        SearchHits hits = null;
        try {
            ElasticUtils.init();
            final SearchRequestBuilder builder = ElasticUtils.client.prepareSearch(indexs);

            builder.setTypes(types).setSearchType(SearchType.DEFAULT).setExplain(true).setFetchSource(true);

            builder.addHighlightedField("*").setHighlighterPreTags("<span style=\"color:red\">")
                    .setHighlighterPostTags("</span>")
                    // .setHighlighterForceSource(true)
                    // 这句不加就不会高亮
                    .setHighlighterRequireFieldMatch(false)
            // .setHighlighterNumOfFragments(0)//
            // 默认的fragment数量是5,所以设置为0就不会分开,整个source就会被高亮
            ;

            final BoolQueryBuilder qb = new BoolQueryBuilder();
            final QueryStringQueryBuilder queryBuilder = QueryBuilders.queryStringQuery(query);
            // queryBuilder.analyzer("ik").autoGeneratePhraseQueries(false);
            qb.filter(queryBuilder);
            final SearchResponse resp = builder.addFields(fields).setQuery(qb).setFrom(pageNum * pageSize)
                    .setSize(pageSize).get();
            hits = resp.getHits();
            ElasticUtils.logger.info("search docs with '" + query + "' hits " + hits.getTotalHits() + " ; " + "took  "
                    + resp.getTookInMillis() + " ms;status=" + resp.status());
        } catch (final Exception e) {
            e.printStackTrace();
        } finally {
            if (ElasticUtils.client != null) {
                ElasticUtils.client.close();
            }
        }
        return ElasticUtils.highLightHandler(hits);
    }

    public static Map<String, Object> searchWithCount(String[] indexs, String[] types, Integer pageNum,
            Integer pageSize, String query, String... fields) {
        SearchHits hits = null;
        final Map<String, Object> map = new HashMap<>(4);
        try {
            ElasticUtils.init();
            final SearchRequestBuilder builder = ElasticUtils.client.prepareSearch(indexs);

            builder.setTypes(types).setSearchType(SearchType.DEFAULT).setExplain(true).setFetchSource(true);

            builder.addHighlightedField("*").setHighlighterPreTags("<span style=\"color:red\">")
                    .setHighlighterPostTags("</span>")
                    // .setHighlighterForceSource(true)
                    // 这句不加就不会高亮
                    .setHighlighterRequireFieldMatch(false)
            // .setHighlighterNumOfFragments(0)//
            // 默认的fragment数量是5,所以设置为0就不会分开,整个source就会被高亮
            ;

            final BoolQueryBuilder qb = new BoolQueryBuilder();
            final QueryStringQueryBuilder queryBuilder = QueryBuilders.queryStringQuery(query);
            // queryBuilder.analyzer("ik").autoGeneratePhraseQueries(false);
            qb.filter(queryBuilder);
            final SearchResponse resp = builder.addFields(fields).setQuery(qb).setFrom(pageNum * pageSize)
                    .setSize(pageSize).get();
            hits = resp.getHits();
            map.put("count", hits.getTotalHits());
            map.put("result", ElasticUtils.highLightHandler(hits));
            ElasticUtils.logger.info("search docs with '" + query + "' hits " + hits.getTotalHits() + " ; " + "took  "
                    + resp.getTookInMillis() + " ms;status=" + resp.status());
        } catch (final Exception e) {
            e.printStackTrace();
        } finally {
            if (ElasticUtils.client != null) {
                ElasticUtils.client.close();
            }
        }
        return map;

    }

    @SuppressWarnings("unchecked")
    public static <T> List<T> highLightHandler(SearchHits hits) {
        final List<T> total = new ArrayList<>();
        Map<String, Object> map = null;
        for (final SearchHit hit : hits) {
            final Map<String, HighlightField> result = hit.highlightFields();
            map = hit.getSource();
            for (final Entry<String, Object> e : map.entrySet()) {
                // 从设定的高亮域中取得指定域
                final HighlightField hfield = result.get(e.getKey());
                if (hfield != null) {
                    // 取得定义的高亮标签
                    final Text[] texts = hfield.fragments();
                    // 增加自定义的高亮标签
                    final StringBuilder sb = new StringBuilder();
                    for (final Text text : texts) {
                        sb.append(text);
                    }
                    e.setValue(sb.toString());
                }
            }
            total.add((T) map);
        }
        return total;
    }

}
