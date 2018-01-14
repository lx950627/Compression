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
public class SDT {
    final double threshold=3;
    
     public int Execute(ArrayList<Mypoint> dataset,String dbname,String colname,int port)
    {
        DBcontroller db=new DBcontroller();
         db.Connect("localhost",port);
         db.SelectCollection(dbname, colname);
         db.DropCollection();
         
         db.Insert(dataset.get(0));
         double laststoredx=dataset.get(0).getX();
         double laststoredy=dataset.get(0).getY();
         int count=1;
         double k1=(dataset.get(1).getY()-(laststoredy+threshold))/(dataset.get(1).getX()-laststoredx);
         double k2=(dataset.get(1).getY()-(laststoredy-threshold))/(dataset.get(1).getX()-laststoredx);
         for(int i=2;i<dataset.size();i++)
         {
             double newk1=(dataset.get(i).getY()-(laststoredy+threshold))/(dataset.get(i).getX()-laststoredx);
             double newk2=(dataset.get(i).getY()-(laststoredy-threshold))/(dataset.get(i).getX()-laststoredx);
             double slope=(dataset.get(i).getY()-laststoredy)/(dataset.get(i).getX()-laststoredx);
             if(newk1>k1)
             {
                 k1=newk1;
             }
             if(newk2<k2)
             {
                 k2=newk2;
             }
             if(slope<k1||slope>k2)
             {
                 db.Insert(dataset.get(i-1));
                 count++;
                 laststoredx=dataset.get(i-1).getX();
                 laststoredy=dataset.get(i-1).getY();
             }
           
         }
          db.Insert(dataset.get(dataset.size()-1));
          count++;
         
        
         db.Close();
         return count;
    }
}
