/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package compression;
import com.mongodb.BasicDBObject;
import com.mongodb.CommandResult;
import com.mongodb.MongoClient;
import com.mongodb.WriteConcern;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.WriteResult;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.LinkedList;

/**
 *
 * @author liuxin
 */
public class DBcontroller {
    private MongoClient mongoClient;
    private DB db;
    private DBCollection collection;
    public void Connect(String hostname,int port){
        try {
            mongoClient=new MongoClient(hostname,port);
            mongoClient.setWriteConcern(WriteConcern.JOURNAL_SAFE);
           
        } catch (UnknownHostException ex) {
            Logger.getLogger(DBcontroller.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void SelectCollection(String dbname,String colname){
         db=mongoClient.getDB(dbname);
         collection=db.getCollection(colname);
         //System.out.println("Number of Documents:"+Long.toString(collection.count()));
    }
    
    public void Insert(Mypoint p)
    {
        BasicDBObject query=new BasicDBObject("x",p.getX());
        BasicDBObject setOp=new BasicDBObject("y",p.getY());
        BasicDBObject update=new BasicDBObject("$set",setOp);
        WriteResult result=collection.update(query, update,true,false);
        String re=result.getField("ok").toString();
        if(Double.parseDouble(re)!=1.0){
            System.out.print("Insertion fails");
        }
    }
    
    public LinkedList<Mypoint> SelectAll()
    {
        LinkedList<Mypoint> dataretrieved=new LinkedList<Mypoint>();
        BasicDBObject fields=new BasicDBObject("_id",false);
        DBCursor cursor=collection.find(null,fields);
        while(cursor.hasNext())
        {
                  DBObject doc=cursor.next();
                  double x=Double.parseDouble(String.valueOf(doc.get("x")));
                  double y=Double.parseDouble(String.valueOf(doc.get("y")));
                  Mypoint p=new Mypoint(x,y);
                  dataretrieved.add(p);
                        
        }
        return dataretrieved;
    }
    
    public void DropCollection()
    {
        collection.drop();
    }
    
    
    public void Close(){
        mongoClient.close();
    }
}
