package entrancer;

import com.mongodb.Mongo;
import com.mongodb.MongoException;
import com.mongodb.WriteConcern;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.DBCursor;
import com.mongodb.ServerAddress;

public class JDKAPIDBAccess {
	Mongo m;
	DBCollection jdk_coll;
	DBCollection ext_coll;
	DBCollection self_coll;
	DBCursor cursor;
	
	
	public JDKAPIDBAccess(){
		try{
		m = new Mongo( "localhost" );
		DB db = m.getDB( "api_db" );
		this.jdk_coll = db.getCollection("api_collection");
		this.ext_coll = db.getCollection("ext_EAnci_collection");
		this.self_coll = db.getCollection("self_EAnci_collection");
		}catch(Exception ex){
			ex.getMessage();
		}
	}
	
	public String getDocumentation(String code, String typeDescription){
		StringBuffer sbr = new StringBuffer();
        cursor = jdk_coll.find(new BasicDBObject("signature", code), new BasicDBObject("type_description",typeDescription).append("description", 1));
        //System.out.println("DB:"+code+typeDescription);
        try {
        	if (cursor.hasNext()) sbr.append(cursor.next().get("description"));
            //while(cursor.hasNext()) {
            //    sbr.append(cursor.next());
            //}
        } finally {
            cursor.close();
            //m.close();
        }
        //System.out.println("DB:"+sbr);
        /*
        cursor = ext_coll.find(new BasicDBObject("signature", code), new BasicDBObject("type_description",typeDescription).append("description", 1));
        //System.out.println("DB:"+code+typeDescription);
        try {
        	if (cursor.hasNext()) sbr.append(cursor.next().get("description"));
        } finally {
            cursor.close();
            //m.close();
        }
        */
        
       
        cursor = self_coll.find(new BasicDBObject("signature", code), new BasicDBObject("type_description",typeDescription).append("description", 1));
        //System.out.println("DB:"+code+typeDescription);
        try {
        	String description ="";
        	if (cursor.hasNext()) {
        		description=(String)cursor.next().get("description"); System.out.println("self_coll:"+code+":"+description);
        		sbr.append(description);
        	}
        } finally {
            cursor.close();
            m.close();
        }
        
        return sbr.toString();
	}
	
	
}
