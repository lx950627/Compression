/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package compression;
import java.util.ArrayList;

/**
 *
 * @author liuxin
 */
public class StandardThreshold {
    final double threshold=6;
    
    public int Execute(ArrayList<Mypoint> dataset,String dbname,String colname,int port)
    {
        DBcontroller db=new DBcontroller();
         db.Connect("localhost",port);
         db.SelectCollection(dbname, colname);
         db.DropCollection();
         
         double lasty=dataset.get(0).getY();
         //System.out.println(dataset.get(0));
         db.Insert(dataset.get(0));
         int count=1;
         for(Mypoint item:dataset)
        {
            double currenty=item.getY();
           if(Math.abs(currenty-lasty)>threshold)
           {
               //System.out.println(item);
               db.Insert(item);
               count++;
               lasty=currenty;
           } 
        }
         
         
         db.Close();
         return count;
    }
}
