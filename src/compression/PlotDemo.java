/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package compression;

/**
 *
 * @author liuxin
 */
import java.io.*;
import java.util.*;
import java.awt.*;
import java.awt.event.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.rosuda.REngine.*;
import org.rosuda.REngine.Rserve.*;

/** A demonstration of the use of Rserver and graphics devices to create graphics in R, pull them into Java and display them. It is a really simple demo. */

public class PlotDemo extends Canvas {
    public RConnection c;
    String device;
    Image img;

    public PlotDemo() {
       
        device="jpeg";

    }
    
    public void setImage(Image img) {
        this.img=img;
        MediaTracker mediaTracker = new MediaTracker(this);
        mediaTracker.addImage(img, 0);
        try {
            mediaTracker.waitForID(0);
        } catch (InterruptedException ie) {
            System.err.println(ie);
            System.exit(1);
        }
        setSize(img.getWidth(null), img.getHeight(null));
    }
    
    public void Draw(ArrayList <Mypoint> data,String Picname,String xlab,String ylab){
        try {
             try {
            c = new RConnection();
        } catch (RserveException ex) {
            Logger.getLogger(PlotDemo.class.getName()).log(Level.SEVERE, null, ex);
        }
            // if Cairo is installed, we can get much nicer graphics, so try to load it
            if (c.parseAndEval("suppressWarnings(require('Cairo',quietly=TRUE))").asInteger()>0)
                device="CairoJPEG"; // great, we can use Cairo device
            else
                System.out.println("(consider installing Cairo package for better bitmap output)");
            
            // we are careful here - not all R binaries support jpeg
            // so we rather capture any failures
            REXP xp = c.parseAndEval("try("+device+"('test.jpg',quality=90))");
            
            if (xp.inherits("try-error")) { // if the result is of the class try-error then there was a problem
                System.err.println("Can't open "+device+" graphics device:\n"+xp.asString());
                // this is analogous to 'warnings', but for us it's sufficient to get just the 1st warning
                REXP w = c.eval("if (exists('last.warning') && length(last.warning)>0) names(last.warning)[1] else 0");
                if (w.isString()) System.err.println(w.asString());
                return;
            }
              
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
     
            // ok, so the device should be fine - let's plot - replace this by any plotting code you desire ...
            //c.parseAndEval("data(iris); attach(iris); plot(Sepal.Length, Petal.Length, col=unclass(Species)); dev.off()");
            String command="plot(x,y,pch=16,xlab=\"";
            command+=xlab;
            command+="\",ylab=\"";
            command+=ylab;
            command+="\");dev.off()";
            c.parseAndEval(command);
            // There is no I/O API in REngine because it's actually more efficient to use R for this
            // we limit the file size to 1MB which should be sufficient and we delete the file as well
            xp = c.parseAndEval("r=readBin('test.jpg','raw',1024*1024); unlink('test.jpg'); r");
            
            // now this is pretty boring AWT stuff - create an image from the data and display it ...
            Image img = Toolkit.getDefaultToolkit().createImage(xp.asBytes());
            
            Frame f = new Frame(Picname);
            this.setImage(img);
            f.add(this);
            f.addWindowListener(new WindowAdapter() { // just so we can close the window
                public void windowClosing(WindowEvent e) { System.exit(0); }
            });
            f.pack();
            f.setVisible(true);
            
            // close RConnection, we're done
            c.close();
        } catch (REngineException ex) {
            Logger.getLogger(PlotDemo.class.getName()).log(Level.SEVERE, null, ex);
        } catch (REXPMismatchException ex) {
            Logger.getLogger(PlotDemo.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void Draw(LinkedList <Mypoint> data,String Picname,String xlab,String ylab){
        try {
             try {
            c = new RConnection();
        } catch (RserveException ex) {
            Logger.getLogger(PlotDemo.class.getName()).log(Level.SEVERE, null, ex);
        }
            // if Cairo is installed, we can get much nicer graphics, so try to load it
            if (c.parseAndEval("suppressWarnings(require('Cairo',quietly=TRUE))").asInteger()>0)
                device="CairoJPEG"; // great, we can use Cairo device
            else
                System.out.println("(consider installing Cairo package for better bitmap output)");
            
            // we are careful here - not all R binaries support jpeg
            // so we rather capture any failures
            REXP xp = c.parseAndEval("try("+device+"('test1.jpg',quality=90))");
            
            if (xp.inherits("try-error")) { // if the result is of the class try-error then there was a problem
                System.err.println("Can't open "+device+" graphics device:\n"+xp.asString());
                // this is analogous to 'warnings', but for us it's sufficient to get just the 1st warning
                REXP w = c.eval("if (exists('last.warning') && length(last.warning)>0) names(last.warning)[1] else 0");
                if (w.isString()) System.err.println(w.asString());
                return;
            }
              
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
     
            // ok, so the device should be fine - let's plot - replace this by any plotting code you desire ...
            //c.parseAndEval("data(iris); attach(iris); plot(Sepal.Length, Petal.Length, col=unclass(Species)); dev.off()");
            String command="plot(x,y,pch=16,main=\"";
            command+=Picname;
            command+="\",xlab=\"";
            command+=xlab;
            command+="\",ylab=\"";
            command+=ylab;
            command+="\");dev.off()";
            c.parseAndEval(command);
            // There is no I/O API in REngine because it's actually more efficient to use R for this
            // we limit the file size to 1MB which should be sufficient and we delete the file as well
            xp = c.parseAndEval("r=readBin('test1.jpg','raw',1024*1024); unlink('test.jpg'); r");
            
            // now this is pretty boring AWT stuff - create an image from the data and display it ...
            Image img = Toolkit.getDefaultToolkit().createImage(xp.asBytes());
            
            Frame f = new Frame(Picname);
            this.setImage(img);
            f.add(this);
            f.addWindowListener(new WindowAdapter() { // just so we can close the window
                public void windowClosing(WindowEvent e) { System.exit(0); }
            });
            f.pack();
            f.setVisible(true);
            
            // close RConnection, we're done
            c.close();
        } catch (REngineException ex) {
            Logger.getLogger(PlotDemo.class.getName()).log(Level.SEVERE, null, ex);
        } catch (REXPMismatchException ex) {
            Logger.getLogger(PlotDemo.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
        
    public void paint(Graphics g) {
        g.drawImage(img, 0, 0, null);
    }

}
