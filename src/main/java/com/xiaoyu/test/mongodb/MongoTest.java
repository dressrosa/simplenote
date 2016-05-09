/**
 * 不要因为走了很远就忘记当初出发的目的:whatever happened,be yourself
 */ 
package com.xiaoyu.test.mongodb;

import java.util.List;

import org.bson.Document;

import com.google.common.collect.Lists;
import com.mongodb.MongoBulkWriteException;
import com.mongodb.MongoClient;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.result.DeleteResult;

/**
 * @author xiaoyu
 *2016年5月9日
 */
public class MongoTest {
	
	public static MongoClient mongoClient  = null;
	public static MongoDatabase mongoBase =null;
	static  {
		try {
			mongoClient = 
					new MongoClient("localhost", 27017);
			 mongoBase	= 
					mongoClient.getDatabase("test");
			System.out.println("Success!");
			
		}
		catch(Exception e) {
			e.printStackTrace();
			System.out.println(e.getMessage());
		}
		
		
	}
	public static void main(String args[]) {
		//getCollection();
		saveCollection();
		getAllDocuments();
		//updateDocuments();
		//deleteDocuments();
		
	}

	public static void getCollection() {
		MongoCollection<Document> collection = 
				mongoBase.getCollection("test");
		System.out.println(collection.find());
		
	}
	
	public static void saveCollection()  {
		MongoCollection<Document> collection = 
				mongoBase.getCollection("test");
		List<Document> documents =Lists.newArrayList();
		Document doc = null;
		for(int i =0;i < 10; i++) {
			doc = new Document("title", "mysql");
			doc.append("description", "mysql is a sql");
			doc.append("likes", 100);
			documents.add(doc);			
		}
		
		collection.insertMany(documents);
		System.out.println("插入成功");
	}
	
	public static void getAllDocuments() {
		MongoCollection<Document> collection =
				mongoBase.getCollection("test");
		FindIterable<Document> finds =collection.find();
		MongoCursor<Document> cursor = finds.iterator();
		while(cursor.hasNext()) {
			Document doc = cursor.next();
			System.out.println(doc.toJson());
			//System.out.println(doc.toJson());
		}
	}
	
	public static void updateDocuments() {
		MongoCollection<Document> collection = 
				mongoBase.getCollection("test");
		collection.updateMany(Filters.eq("likes", 100),new
				Document("$set",new Document("likes",200)));
		getAllDocuments();
	}
	
	public static void deleteDocuments() {
		MongoCollection<Document> collection
		 = mongoBase.getCollection("test");
		DeleteResult result = collection.deleteOne(Filters.eq("title", "mysql"));
		System.out.println(result);
		
		getAllDocuments();
	}
}
