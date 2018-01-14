/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package compression;

import java.math.BigDecimal;
import java.util.LinkedList;

/**
 *
 * @author liuxin
 */
public class Decompressor 
{
    private LinkedList<Mypoint> data;
    private LinkedList<Mypoint> datainterpolated;
    private final double span=1;
    public void RetrieveData(String dbname,String colname)
    {
        DBcontroller db=new DBcontroller();
        db.Connect("localhost",27017);
        db.SelectCollection(dbname, colname);
        data=db.SelectAll();
//        for(Mypoint p:data)
//        {
//            System.out.println(p);
//        }
        db.Close();
    }
    
    public LinkedList<Mypoint> ExecuteDecompression(String dbname,String colname)
    {
       RetrieveData(dbname,colname);
       LinearInterpolation();
       //StaticInterpolation();
       return datainterpolated;
    }
    
   public void StaticInterpolation() 
    {
        datainterpolated=new LinkedList<Mypoint>();
        for(int i=0;i<data.size();i++){
            datainterpolated.add(data.get(i));
        }
  
        for(int i=0;i<data.size()-1;i++)
        {
            Mypoint p0=data.get(i);
            Mypoint p1=data.get(i+1);
            double difference=p1.getX()-p0.getX();
            if(difference>span)
            {
                float ninserted=(float)(difference/span-1);
                //System.out.println("ninserted:"+ninserted);
                int already=0;
                while(already<ninserted)
                {
                    double xinserted=p0.getX()+already+1;
                    //System.out.println("insert:"+xinserted);
                    double yinsert=p0.getY();
                    BigDecimal b=new BigDecimal(yinsert);
                    double yinserted=b.setScale(3,BigDecimal.ROUND_HALF_UP).doubleValue();
                    int start=datainterpolated.indexOf(p0);
                    datainterpolated.add(start+already+1,new Mypoint(xinserted,yinserted));
                    already++;
                }
            }
        }
//        System.out.println();
//        for(Mypoint p:datainterpolated)
//        {
//            System.out.println(p);
//        }
    }
    
    
    public void LinearInterpolation() 
    {
        datainterpolated=new LinkedList<Mypoint>();
        for(int i=0;i<data.size();i++){
            datainterpolated.add(data.get(i));
        }
        
        
        for(int i=0;i<data.size()-1;i++)
        {
            Mypoint p0=data.get(i);
            Mypoint p1=data.get(i+1);
            double difference=p1.getX()-p0.getX();
            if(difference>span)
            {
                float ninserted=(float)(difference/span-1);
                //System.out.println("ninserted:"+ninserted);
                int already=0;
                while(already<ninserted)
                {
                    double xinserted=p0.getX()+already+1;
                    double l0=(xinserted-p1.getX())/(-difference);
                    double l1=(xinserted-p0.getX())/(difference);
                    double yinsert=l0*p0.getY()+l1*p1.getY();
                    BigDecimal b=new BigDecimal(yinsert);
                    double yinserted=b.setScale(3,BigDecimal.ROUND_HALF_UP).doubleValue();
                    
                    int start=datainterpolated.indexOf(p0);
                    datainterpolated.add(start+already+1,new Mypoint(xinserted,yinserted));
                    already++;
                }
            }
        }
//         System.out.println();
//        for(Mypoint p:datainterpolated)
//        {
//            System.out.println(p);
//        }
    }
}
