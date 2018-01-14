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
public class Mypoint {
    private double x;
    private double y;
    
    public Mypoint(){
        x=y=0;
    }
    
    
    public Mypoint(double xx,double yy){
        x=xx;
        y=yy;
    }
    
    public double getX(){
        return x;
    }
    
    public double getY(){
        return y;
    }
    
    public void setX(double xx){
        x=xx;
    }
    
    public void setY(double yy){
        y=yy;
    }
    
    @Override
    public String toString(){
        return "("+x+","+y+")";
    }
    
    
}
