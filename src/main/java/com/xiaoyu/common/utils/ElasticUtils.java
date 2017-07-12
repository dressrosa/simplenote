/**
 * 唯有读书,不慵不扰
 */
package com.xiaoyu.common.utils;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.log4j.Logger;
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

	private static final Logger logger = Logger.getLogger(ElasticUtils.class);
	private static Client client;
	private static final String CLUSTER_NAME = "xiaoyu";

	private static String HOST1 = "localhost";
	private static int PORT1 = 9300;

	// private String index = "website_article";
	// private String type = "news";

	private static Client init() {
		Settings sets = Settings.settingsBuilder().put("tclient.transport.sniff", true)// 自动嗅探整个集群的状态，把集群中其它机器的ip地址加到客户端中
				.put("cluster.name", CLUSTER_NAME).build();
		return client = TransportClient.builder().settings(sets).build()
				.addTransportAddress(new InetSocketTransportAddress(new InetSocketAddress(HOST1, PORT1)));
	}

	public static void insert(String index, String type, String jsonData) {
		try {
			init();
			IndexRequestBuilder builder = client.prepareIndex(index, type);
			IndexResponse resp = builder.setSource(jsonData).get();
			logger.info("insert doc[id=" + resp.getId() + "];index[" + index + "];type" + "[" + type + "]");
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (client != null)
				client.close();
		}
		return;
	}

	public static void insert(String index, String type, String id, String jsonData) {
		checkNull(id);
		try {
			init();
			IndexRequestBuilder builder = client.prepareIndex(index, type);
			IndexResponse resp = builder.setId(id).setSource(jsonData).get();
			logger.info("insert doc[id=" + resp.getId() + "];index[" + index + "];type" + "[" + type + "]");
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (client != null)
				client.close();
		}
	}

	public static String get(String index, String type, String id) {
		checkNull(id);
		try {
			init();
			GetRequestBuilder builder = client.prepareGet(index, type, id);
			GetResponse resp = builder.get();
			String json = resp.getSourceAsString();
			logger.info("get doc[id=" + resp.getId() + "];index[" + index + "];type" + "[" + type + "]" + ";status="
					+ resp.isExists());
			return json;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (client != null)
				client.close();
		}
		return null;
	}

	public static <T> T getObject(String index, String type, String id) {
		checkNull(id);
		try {
			init();
			GetRequestBuilder builder = client.prepareGet(index, type, id);
			GetResponse resp = builder.get();
			@SuppressWarnings("unchecked")
			T t = (T) resp.getSourceAsString();
			logger.info("get doc[id=" + resp.getId() + "];index[" + index + "];type" + "[" + type + "]" + ";status="
					+ resp.isExists());
			return t;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (client != null)
				client.close();
		}
		return null;
	}

	public static void delete(String index, String type, String id) {
		checkNull(id);
		try {
			init();
			DeleteRequestBuilder builder = client.prepareDelete(index, type, id);
			DeleteResponse resp = builder.get();
			logger.info("delete doc[" + resp.getId() + "];index[" + index + "];type" + "[" + type + "];" + "status="
					+ resp.isFound());
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (client != null)
				client.close();
		}
	}

	public static void update(String index, String type, String id, String jsonData) {
		checkNull(id);
		try {
			init();
			UpdateRequestBuilder builder = client.prepareUpdate(index, type, id);
			UpdateResponse resp = builder.setDoc(jsonData).get();
			logger.info("update doc[id=" + resp.getId() + "];index[" + resp.getIndex() + "];type" + "[" + resp.getType()
					+ "]");
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (client != null)
				client.close();
		}
	}

	public static void upsert(String index, String type, String id, String jsonData) {
		checkNull(id);
		try {
			init();
			UpdateRequestBuilder builder = client.prepareUpdate(index, type, id);
			UpdateResponse resp = builder.setDoc(jsonData).setUpsert(jsonData).get();
			logger.info("update doc[id=" + resp.getId() + "];index[" + resp.getIndex() + "];type" + "[" + resp.getType()
					+ "]");
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (client != null)
				client.close();
		}
	}

	private static void checkNull(String id) {
		if ("".equals(id) || id == null)
			throw new IllegalArgumentException("id should given a valid value");
	}

	public static <T> List<T> search(String index, String type, String query, String... fields) {
		SearchHits hits = null;
		try {
			init();
			SearchRequestBuilder builder = client.prepareSearch(index);
			builder.setTypes(type).setSearchType(SearchType.DEFAULT).setExplain(true).setFetchSource(true);// 返回source
			// .setNoFields()//不返回参数;
			// 设置高亮
			builder.addHighlightedField("*")// *代表全部高亮 fields == null ? null :
											// fields[0]
					.setHighlighterPreTags("<span style=\"color:red\">").setHighlighterPostTags("</span>")
					.setHighlighterForceSource(true).setHighlighterRequireFieldMatch(false);

			BoolQueryBuilder qb = new BoolQueryBuilder();
			QueryStringQueryBuilder queryBuilder = QueryBuilders.queryStringQuery(query);
			qb.filter(queryBuilder);

			SearchResponse resp = builder.addFields(fields).setQuery(qb).get();
			hits = resp.getHits();

			logger.info("search docs with '" + query + "' hits " + hits.getTotalHits() + " ; " + "took  "
					+ resp.getTookInMillis() + " ms;status=" + resp.status());
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (client != null)
				client.close();
		}
		return highLightHandler(hits);
	}

	public static <T> List<T> search(String[] indexs, String[] types, Integer pageNum, Integer pageSize, String query,
			String... fields) {
		SearchHits hits = null;
		try {
			init();
			SearchRequestBuilder builder = client.prepareSearch(indexs);

			builder.setTypes(types).setSearchType(SearchType.DEFAULT).setExplain(true).setFetchSource(true);

			builder.addHighlightedField("*").setHighlighterPreTags("<span style=\"color:red\">")
					.setHighlighterPostTags("</span>")
					// .setHighlighterForceSource(true)
					.setHighlighterRequireFieldMatch(false)// 这句不加就不会高亮
			// .setHighlighterNumOfFragments(0)//
			// 默认的fragment数量是5,所以设置为0就不会分开,整个source就会被高亮
			;

			BoolQueryBuilder qb = new BoolQueryBuilder();
			QueryStringQueryBuilder queryBuilder = QueryBuilders.queryStringQuery(query);
			// queryBuilder.analyzer("ik").autoGeneratePhraseQueries(false);
			qb.filter(queryBuilder);
			SearchResponse resp = builder.addFields(fields).setQuery(qb).setFrom(pageNum * pageSize).setSize(pageSize)
					.get();
			hits = resp.getHits();
			logger.info("search docs with '" + query + "' hits " + hits.getTotalHits() + " ; " + "took  "
					+ resp.getTookInMillis() + " ms;status=" + resp.status());
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (client != null)
				client.close();
		}
		return highLightHandler(hits);
	}
	public static Map<String,Object> searchWithCount(String[] indexs, String[] types, Integer pageNum, Integer pageSize, String query,
			String... fields) {
		SearchHits hits = null;
		Map<String,Object> map = new HashMap<>();
		try {
			init();
			SearchRequestBuilder builder = client.prepareSearch(indexs);

			builder.setTypes(types).setSearchType(SearchType.DEFAULT).setExplain(true).setFetchSource(true);

			builder.addHighlightedField("*").setHighlighterPreTags("<span style=\"color:red\">")
					.setHighlighterPostTags("</span>")
					// .setHighlighterForceSource(true)
					.setHighlighterRequireFieldMatch(false)// 这句不加就不会高亮
			// .setHighlighterNumOfFragments(0)//
			// 默认的fragment数量是5,所以设置为0就不会分开,整个source就会被高亮
			;

			BoolQueryBuilder qb = new BoolQueryBuilder();
			QueryStringQueryBuilder queryBuilder = QueryBuilders.queryStringQuery(query);
			// queryBuilder.analyzer("ik").autoGeneratePhraseQueries(false);
			qb.filter(queryBuilder);
			SearchResponse resp = builder.addFields(fields).setQuery(qb).setFrom(pageNum * pageSize).setSize(pageSize)
					.get();
			hits = resp.getHits();
			map.put("count",hits.getTotalHits());
			map.put("result",highLightHandler(hits));
			logger.info("search docs with '" + query + "' hits " + hits.getTotalHits() + " ; " + "took  "
					+ resp.getTookInMillis() + " ms;status=" + resp.status());
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (client != null)
				client.close();
		}
		return map;
		
	}
	public static <T> List<T> highLightHandler(SearchHits hits) {
		List<T> total = new ArrayList<>();
		Map<String, Object> map = null;
		for (SearchHit hit : hits) {
			Map<String, HighlightField> result = hit.highlightFields();
			map = hit.getSource();
			for (Entry<String, Object> e : map.entrySet()) {
				// 从设定的高亮域中取得指定域
				HighlightField hfield = result.get(e.getKey());
				if (hfield != null) {
					// 取得定义的高亮标签
					Text[] texts = hfield.fragments();
					// 增加自定义的高亮标签
					StringBuilder sb = new StringBuilder();
					for (Text text : texts) {
						sb.append(text);
					}
					e.setValue(sb.toString());
				}
			}
			total.add((T)map);
		}
		return total;
	}

}
