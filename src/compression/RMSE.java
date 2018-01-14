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
public class RMSE {
     final double threshold=7;
     
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
         double kmax=Double.POSITIVE_INFINITY;
         double kmin=Double.NEGATIVE_INFINITY;
         double a=0;
         double b=0;
         double c=0;
         
         for(int i=1;i<dataset.size();i++)
         {
             double interval=dataset.get(i).getX()-laststoredx;
             double slope=(dataset.get(i).getY()-laststoredy)/interval;
             
             if(slope<kmax && slope>kmin)
             {
                double intervalpow=interval*interval;
                a+=intervalpow;
                b-=2*slope*intervalpow;
                c+=slope*slope*intervalpow-threshold*threshold;
                
                double pan=b*b-4*a*c;
                if(pan<=0)
                {
                    db.Insert(dataset.get(i));
                    laststoredx=dataset.get(i).getX();
                    laststoredy=dataset.get(i).getY();
                    count++;
                    
                    a=b=c=0;
                    kmax=Double.POSITIVE_INFINITY;
                    kmin=Double.NEGATIVE_INFINITY;
                    
                }
                
                else
                {
                kmax=(-b+Math.sqrt(pan))/(2*a);
                kmin=-kmax-b/a;
                }
                
             }
             
             else
             {
                 db.Insert(dataset.get(i));
                 laststoredx=dataset.get(i).getX();
                 laststoredy=dataset.get(i).getY();
                 count++;
                 a=b=c=0;
                 kmax=Double.POSITIVE_INFINITY;
                 kmin=Double.NEGATIVE_INFINITY; 
             }
             
             
           
         }
         
         db.Insert(dataset.get(dataset.size()-1));
         count++;
         
         db.Close();
         return count;
    }
}
