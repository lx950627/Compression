/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package compression;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import org.rosuda.REngine.Rserve.RConnection;

/**
 *
 * @author liuxin
 */
public class Generator {
      public static void main(String[] args) {
          int type=2;
          int n=1800;
          GenerateTest("test.txt",n,type);
          
      }
     public static void GenerateTest(String filename,int num,int type){
      File file = new File(filename);
        FileWriter fw = null;
        BufferedWriter writer = null;
        try {
            fw = new FileWriter(file);
            writer = new BufferedWriter(fw);
            double y=0;
            
            for(double i=0;i<num;i=i+1)
        {
           
            if(type==4)
            {
                double[] errors=getErrors(num);
                int newi=(int)i;
                y=4*i+errors[newi];
               //y=4*i;
               
            }
            
            else
            {
            y=FunctionChooser(type,i);
            }
          String s="x:"+i+"\ty:"+y+"";
          writer.write(s);
          writer.newLine();
          //换行
        }
            
            writer.flush();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }catch (IOException e) {
            e.printStackTrace();
        }finally{
            try {
                writer.close();
                fw.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
     }
     
     private static double FunctionChooser(int type,double x) 
     {
         double y=0;
         switch(type)
         {
             case 0:
                 y=10;
                 break;
             case 1:
                 if(x==250||x==1400)
                    y=2000;
                  else y=2*x;
                 break;
              case 2:
                  y=100*Math.sin(Math.PI*x/360);
                 break;
             
         }
         
         return y;
     }
     
     private static double[] getErrors(int num)
     {
         double []error=new double[num];
         
          RConnection c=null;
        try {
           c = new RConnection();
           String r="rnorm("+num+",sd=1)";
           error=c.eval(r).asDoubles();
           for(int i=0;i<num;i++)
           {
           BigDecimal b=new BigDecimal(error[i]);  
           error[i]=b.setScale(2,BigDecimal.ROUND_HALF_UP).doubleValue();  
           
           }
       } catch (Exception e) {
            e.printStackTrace();
     }finally
         {
           c.close(); 
        }
         
         return error;
     }
     
     
     
     
}
