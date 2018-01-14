/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package compression;

import java.math.BigDecimal;
import java.util.ArrayList;
import org.rosuda.REngine.REXP;
import org.rosuda.REngine.RList;
import org.rosuda.REngine.Rserve.RConnection;

/**
 *
 * @author liuxin
 */
public class RTest {
    public static void main(String[] args) {
    try {
//           RConnection c = new RConnection();
          
           double[] height1=new double[]{58,59,60,61,62,63,64,65,66,67,68,69,70,71,72};
           double[] weight1=new double[]{115,117,120,123,126,129,132,135,139,142,146,150,154,159,164};
           ArrayList <Mypoint> data=new ArrayList <Mypoint>();
          
           for(int i=0;i<height1.length;i++)
          {
           data.add(new Mypoint(height1[i],weight1[i]));
          }
           int size=data.size();
           double[] height=new double[size];
           double[] weight=new double[size];
          
           for(int i=0;i<data.size();i++)
           { 
               height[i]=data.get(i).getX();
               weight[i]=data.get(i).getY();
           }
  
           
//           c.assign("height",height);
//           c.assign("weight",weight);
           
           PlotDemo plot=new PlotDemo();
           plot.Draw(data,"X","Y","Name");
//           c.voidEval("fit=lm(weight~height)");
//           double[] coeff=c.eval("coefficients(fit)").asDoubles();
////           for(double x:coeff){
////               System.out.println(x);
////           }
//           double prex=73;
//           String newx="newx<-data.frame(height="+prex+")";
//           c.voidEval(newx);
//           String pre="predict(fit,newx,interval=\"prediction\",level=0.95)";
//           double[] preval=c.eval(pre).asDoubles();
//           BigDecimal b=new BigDecimal(preval[1]);
//           double lower=b.setScale(2,BigDecimal.ROUND_HALF_UP).doubleValue();
//           b=new BigDecimal(preval[2]);
//           double upper=b.setScale(2,BigDecimal.ROUND_HALF_UP).doubleValue();
//           
//           System.out.println("Lower bound of prediction interval:"+lower);
//           System.out.println("Upper bound of prediction interval:"+upper);
//           
////           for(double x:preval){
////               System.out.println(x);
////           }
//           
//           String summary = c.eval("paste(capture.output(print(summary(fit))),collapse='\n')").asString();
//           
//           System.out.println(summary);
//           c.close();
          
       } catch (Exception e) {
            e.printStackTrace();
     }
    }

}
