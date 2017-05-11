/**
 * 不要因为走了很远就忘记当初出发的目的:whatever happened,be yourself
 */ 
package com.xiaoyu.common.configuration;

/**
 * mongodb的配置
 * @author xiaoyu
 *2016年5月9日
 */
//@Configuration
//@EnableAutoConfiguration
public class MongoConfiguration /*extends AbstractMongoConfiguration*/{

//	@Value("${mongo.ip}")
//	private String mongoIp;
//	
//	@Value("${mongo.port}")
//	private int mongoPort;
//	
//	@Value("${mongo.dataBase}")
//	private String dataBase;
//	
	/**db工厂
	 *@author xiaoyu
	 *@return
	 *@time 2016年5月9日下午2:49:59
	 */
//	@Bean
//	public MongoDbFactory mongoFactory() {
//		MongoClient client = new MongoClient(mongoIp,mongoPort);
//		return new SimpleMongoDbFactory(
//				client,"test");
//	}
	
//	@Bean
//	public MongoTemplate mongoTemplate() {
//		MongoDbFactory factory = mongoFactory();
//		MongoTemplate template = new MongoTemplate(factory);
//		return template;
//	}

//	@Override
//	protected String getDatabaseName() {
//		return dataBase;
//	}
//
//	@Override
//	public Mongo mongo() throws Exception {
//		MongoClient client = new MongoClient(mongoIp,mongoPort);
//		return client;
//	}
}
