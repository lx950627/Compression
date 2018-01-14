/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package compression;

import java.util.ArrayList;
import java.math.BigDecimal;
import java.util.ArrayList;
import org.rosuda.REngine.REXP;
import org.rosuda.REngine.RList;
import org.rosuda.REngine.Rserve.RConnection;
/**
 *
 * @author liuxin
 */
public class PLOT 
{
    public int Execute(ArrayList<Mypoint> dataset,String dbname,String colname,int port)
    {
        DBcontroller db=new DBcontroller();
         db.Connect("localhost",port);
         db.SelectCollection(dbname, colname);
         db.DropCollection();
         
         int count=0;
         db.Insert(dataset.get(0));
         count++;
//         db.Insert(dataset.get(1));
//         count++;
         
         ArrayList <Mypoint> sample=new ArrayList();
         ArrayList <Mypoint> outliers=new ArrayList();
         sample.add(dataset.get(0));
         sample.add(dataset.get(1));
         sample.add(dataset.get(2));
         
        
         double[] pre=new double[3];
         double lower=0;
         double upper=0;
         int previousn=0;
         double newdeviation=0;
         double k=0;
         double b=0;
         for(int i=3;i<dataset.size();i++)
         {
             double testx=dataset.get(i).getX();
             if(sample.size()>=3)
             {
             pre=predictioninterval(sample,testx);
             }
             
             else if(sample.size()==2)
             {
                 newdeviation=CalculateDeviation(pre[2]-pre[0],previousn);
                 k=(sample.get(1).getY()-sample.get(0).getY())/(sample.get(1).getX()-sample.get(0).getX());
                 b=sample.get(1).getY()-k*sample.get(1).getX();
                 double prey=k*testx+b;
                 System.out.println("newde:"+newdeviation);
                 pre[0]=prey;
                 pre[1]=prey-newdeviation;
                 pre[2]=prey+newdeviation;        
             }
             lower=pre[1];
             upper=pre[2];
             System.out.println("SIZE OF sample:"+sample.size());
             System.out.println("["+lower+","+upper+"]");
             System.out.println(dataset.get(i).getY());
             
             if(dataset.get(i).getY()<=upper && dataset.get(i).getY()>=lower)//yn+1在预测区间内
             {
                 sample.add(dataset.get(i));  
             }
             
             else//yn+1在预测区间外
             {
                 System.out.println("N+1 FAIL");
                  System.out.println("yn+1:"+dataset.get(i));
                  i++;//开始判断yn+2
                  if(i>=dataset.size()) break;
                  System.out.println("yn+2:"+dataset.get(i));
                  testx=dataset.get(i).getX();
//                  pre=predictioninterval(sample,testx);
                       
             if(sample.size()>=3)
             {
             pre=predictioninterval(sample,testx);
             }
             
             else if(sample.size()==2)
             {
                 double prey=k*testx+b;
                 System.out.println("newde:"+newdeviation);
                 pre[0]=prey;
                 pre[1]=prey-newdeviation;
                 pre[2]=prey+newdeviation;        
             }
                  lower=pre[1];
                  upper=pre[2];
                  
         if(dataset.get(i).getY()>upper || dataset.get(i).getY()<lower)//yn+2也在预测区间外
                 {
                 System.out.println("N+2 FAIL");    
                 previousn=sample.size();
                 sample.clear();//yn+1为新趋势的起点;
                 sample.add(dataset.get(i-1));//yn+1
                 sample.add(dataset.get(i));//yn+2
                 System.out.println("new trend:"+dataset.get(i-1).getX());
                 db.Insert(dataset.get(i-2));
                 db.Insert(dataset.get(i-1));
                 count+=2;
                 System.out.println("INSERT:"+dataset.get(i-2)+" "+dataset.get(i-1));
                 //********存储yn,yn+1******
                 }
                  else//yn+2在预测区间内
                  {
                      System.out.println("********************************************Further Discussion************************");
                      boolean flag=true;
                      while(flag)
                      {
                      System.out.println("********************************************Further Discussion???************************");
                      i++;//开始判断yn+3
                      if(i>=dataset.size()) break;
                      previousn=sample.size();
                      sample.add(dataset.get(i-1));//加入yn+2
                      double[] bounds=predictioninterval(sample,dataset.get(i).getX());//yn+3横坐标
                      double alow=bounds[1];
                      double aup=bounds[2];
                      
                      newdeviation=CalculateDeviation(pre[2]-pre[0],previousn);
                      double x0,y0,x1,y1;
                      x0=dataset.get(i-2).getX();//yn+1 x
                      x1=dataset.get(i-1).getX();//yn+2 x
                      y0=dataset.get(i-2).getY();//yn+1 y
                      y1=dataset.get(i-1).getY();//yn+2 y
                      k=(y1-y0)/(x1-x0);
                      b=y1-k*x1;
                      double prey=k*dataset.get(i).getX()+b;
                      double blow=prey-newdeviation;
                      double bup=prey+newdeviation; 
                       
                      System.out.println("A:["+alow+","+aup+"]");
                      System.out.println("B:["+blow+","+bup+"]");
                       System.out.println("yn+3"+dataset.get(i));
                      boolean IsinA=false,IsinB=false;
                      if(dataset.get(i).getY()>=alow && dataset.get(i).getY()<=aup)
                      {
                          IsinA=true;
                      }
                      if(dataset.get(i).getY()>=blow && dataset.get(i).getY()<=bup)
                      {
                          IsinB=true;
                      }
                      
                      if(IsinA && !IsinB)//yn+1是野点
                      {
                          System.out.println("在A不在B");
                          outliers.add(dataset.get(i-2));
                          sample.add(dataset.get(i));
                          flag=false;
                      }
                      
                      else if(!IsinA && IsinB)//yn+1是新趋势的起点
                      {
                          System.out.println("不在A在B");
                          System.out.println("("+dataset.get(i-2).getX()+","+dataset.get(i-2).getY()+")是新起点");
                          sample.clear();
                          sample.add(dataset.get(i));//yn+3
                          sample.add(dataset.get(i-1));//yn+2
                          sample.add(dataset.get(i-2));//yn+1
                          
                           db.Insert(dataset.get(i-3));//存储yn+1
                           db.Insert(dataset.get(i-2));//存储yn
                           count+=2;
                           System.out.println("INSERT:"+dataset.get(i-3)+" "+dataset.get(i-2));
                           flag=false;
                             //********存储yn,yn+1******
                      }
                      
                      else if(IsinA && IsinB)
                      {
                          System.out.println("都在");
                          flag=true;
                      }
                      
                       else if(!IsinA && !IsinB)
                      {
                           System.out.println("都不在");
                          flag=true;
                      }
                      
                      }
                      
                      
                  }
                 
             }
         }
         db.Insert(dataset.get(dataset.size()-1));
         count++;
         
         if(!outliers.isEmpty())
         {
             System.out.println("Outlier:");   
             for(Mypoint item:outliers)
             {
              System.out.println(item);  
             }
         }
         else
         {
             System.out.println("No outlier.");   
         }
         
         
         db.Close();
         return count;
    }
    
    private double[] predictioninterval(ArrayList <Mypoint> data,double prex)
    {
        double[] preval=new double[3];
         RConnection c=null;
        try {
           c = new RConnection();
         
           int size=data.size();
           double[] x=new double[size];
           double[] y=new double[size];
          
           for(int i=0;i<data.size();i++)
           { 
               x[i]=data.get(i).getX();
               y[i]=data.get(i).getY();
           }

           c.assign("x",x);
           c.assign("y",y);
           c.voidEval("model=lm(y~x)");
           //double[] coeff=c.eval("coefficients(model)").asDoubles();
          
           String newx="newx<-data.frame(x="+prex+")";
           c.voidEval(newx);
           String pre="predict(model,newx,interval=\"prediction\",level=0.95)";
           preval=c.eval(pre).asDoubles();
           BigDecimal b=new BigDecimal(preval[1]);//lower boundary
           preval[1]=b.setScale(5,BigDecimal.ROUND_HALF_UP).doubleValue();
           b=new BigDecimal(preval[2]);
           preval[2]=b.setScale(5,BigDecimal.ROUND_HALF_UP).doubleValue();
//           b=new BigDecimal(preval[0]);
//           preval[0]=b.setScale(2,BigDecimal.ROUND_HALF_UP).doubleValue();
//           
       } catch (Exception e) {
           System.out.println("size"+data.size());
            System.out.println("preval[0]:"+preval[0]);
            System.out.println("preval[1]:"+preval[1]);
             System.out.println("preval[2]:"+preval[2]);
            e.printStackTrace();
     }finally
        {
           c.close(); 
        }
        return preval;
     }
    
    private double CalculateDeviation(double old,int n)
    {
        double temp1=1+1/n+12*(Math.pow((n+1)/2,2))/(n*(n*n-1));
        double temp2=Math.sqrt(temp1);
        double deviation=(Math.sqrt(6)/temp2)*old;
        
      //BigDecimal b=new BigDecimal(deviation);
      //deviation=b.setScale(2,BigDecimal.ROUND_HALF_UP).doubleValue();
        return deviation;
    }
}
