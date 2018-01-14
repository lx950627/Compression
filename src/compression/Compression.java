/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package compression;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Set;

/**
 *
 * @author liuxin
 */
public class Compression {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        final int dbport=27017;
        final String Databasename="test";
        final String ColST="st";
        final String ColSDT="sdt";
        final String ColRMSE="rmse";
        final String ColPLOT="plot";
        final String Originalfile="test.txt";
        final String CompressedfileST="resultST.txt";
        final String CompressedfileSDT="resulSDT.txt";
        final String CompressedfileRMSE="resulRMSE.txt";
        final String CompressedfilePLOT="resulPLOT.txt";
        ArrayList <Mypoint> dataset=new ArrayList();
        dataset=readFileByLines(Originalfile);
        
        for(Mypoint item:dataset)
        {
            System.out.println(item);   
        }
        
         PlotDemo Rdraw=new PlotDemo();
         PlotDemo RdrawSDT=new PlotDemo();
         PlotDemo RdrawRMSE=new PlotDemo();
         PlotDemo RdrawPLOT=new PlotDemo();
         Rdraw.Draw(dataset,"Original Data","Time","Temperature");
        
       
        StandardThreshold st=new StandardThreshold();
        SDT sdt=new SDT();
        RMSE rmse=new RMSE();
        PLOT plot=new PLOT();
        
//        int numberST=st.Execute(dataset,Databasename,ColST,dbport);//标准死区
//        int numberSDT=sdt.Execute(dataset,Databasename,ColSDT,dbport);//标准旋转门
        int numberRMSE=rmse.Execute(dataset,Databasename,ColRMSE,dbport);//均方根误差
        int numberPLOT=plot.Execute(dataset,Databasename,ColPLOT,dbport);//分段线性趋势
        
//        double crST=CompressionRate(numberST,dataset.size());
//        System.out.println("标准死区算法压缩率:"+crST*100+"%");
//        double crSDT=CompressionRate(numberSDT,dataset.size());
//        System.out.println("旋转门算法压缩率:"+crSDT*100+"%");
        double crRMSE=CompressionRate(numberRMSE,dataset.size());
        System.out.println("均方根旋转门压缩率:"+crRMSE*100+"%");
         double crPLOT=CompressionRate(numberPLOT,dataset.size());
        System.out.println("分段线性趋势压缩率:"+crPLOT*100+"%");
        
        
        Decompressor de=new Decompressor();
        
//        LinkedList<Mypoint> newdatasetST;
//        newdatasetST=de.ExecuteDecompression(Databasename, ColST);
//        System.out.println("标准死区平均误差:"+AverageError(dataset,newdatasetST)); 
//        WriteToTxt(newdatasetST,CompressedfileST);
      
//        LinkedList<Mypoint> newdatasetSDT;
//        newdatasetSDT=de.ExecuteDecompression(Databasename, ColSDT);
//        System.out.println("旋转门平均误差:"+AverageError(dataset,newdatasetSDT)); 
//        WriteToTxt(newdatasetSDT,CompressedfileSDT);
//         RdrawSDT.Draw(newdatasetSDT,"SDT Decompressed Data","Time","Temperature");
//        
        LinkedList<Mypoint> newdatasetRMSE;
        newdatasetRMSE=de.ExecuteDecompression(Databasename, ColRMSE);
        System.out.println("均方根旋转门平均误差:"+AverageError(dataset,newdatasetRMSE)); 
        WriteToTxt(newdatasetRMSE,CompressedfileRMSE);
        RdrawRMSE.Draw(newdatasetRMSE,"RMSE Decompressed Data","Time","Temperature");
        
        LinkedList<Mypoint> newdatasetPLOT;
        newdatasetPLOT=de.ExecuteDecompression(Databasename, ColPLOT);
        System.out.println("分段线性趋势平均误差:"+AverageError(dataset,newdatasetPLOT)); 
        WriteToTxt(newdatasetPLOT,CompressedfilePLOT);
        RdrawPLOT.Draw(newdatasetPLOT,"PLOT Decompressed Data","Time","Temperature");
        
        
          

    }
    
    private static double CompressionRate(int numberaftercompression,int sizeofdata)
    {
        double f=(numberaftercompression*1.0/sizeofdata);
        BigDecimal b=new BigDecimal(1-f);
        double compressionrate=b.setScale(3,BigDecimal.ROUND_HALF_UP).doubleValue();
       
        return compressionrate;
    }
    
    private static double AverageError(ArrayList <Mypoint> dataset,LinkedList<Mypoint> newdataset)
    {
         while(newdataset.size()<dataset.size())
         {
            double lasty=newdataset.getLast().getY();
            double lastx=newdataset.getLast().getX();
            newdataset.add(new Mypoint(lastx+1,lasty));  
        }
         
        float sumes=0;
        //System.out.println(newdataset.size()); 
        //double maxe=0;
       
        for(int i=0;i<dataset.size();i++)
        {
            double error=dataset.get(i).getY()-newdataset.get(i).getY();
            double errorsquare=error*error;
            
//            if(errorsquare>4){
//                System.out.println(dataset.get(i)+"--"+newdataset.get(i)); 
//            }
            
            sumes+=errorsquare;
        }
       
//        System.out.println(sumes+"--"+dataset.size()); 
        double stdeviation=Math.sqrt(sumes/dataset.size());
        BigDecimal e=new BigDecimal(stdeviation);
        
        return e.setScale(3,BigDecimal.ROUND_HALF_UP).doubleValue();
        
    }
    
    public static ArrayList <Mypoint> readFileByLines(String fileName) {
        File file = new File(fileName);
        BufferedReader reader = null;
        ArrayList <Mypoint> data=new ArrayList ();
        try {
//            System.out.println("以行为单位读取文件内容，一次读一整行：");
            reader = new BufferedReader(new FileReader(file));
            String tempString =null;
            int line = 1;
            
            // 一次读入一行，直到读入null为文件结束
            while ((tempString = reader.readLine()) != null) {
                String[] dataobtained=tempString.split(":|\t");
                double x=Double.parseDouble(dataobtained[1]);
                double y=Double.parseDouble(dataobtained[3]);
                Mypoint newp=new Mypoint(x,y);
                data.add(newp);
//                System.out.println(newp);
//                System.out.println("line " + line + ": " + tempString);
                line++;
            }
            reader.close();
            
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e1) {
                }
            }
        }
        return data;
    }
    
     public static void WriteToTxt(LinkedList<Mypoint> dataset,String filename){
        File file = new File(filename);
        FileWriter fw = null;
        BufferedWriter writer = null;
        try {
            fw = new FileWriter(file);
            writer = new BufferedWriter(fw);
            writer.write("x\t"+"y");
            for(Mypoint p:dataset)
        {
          String s=p.getX()+"  "+p.getY()+"";
          writer.newLine();
          writer.write(s);
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
    
}
