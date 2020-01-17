package com.example.hanium;

public class Point {
    int s1,s2,s3,s4,s;
    Point(){

    }
    public void setData(int s1,int s2,int s3,int s4,int s){
        this.s=s;
        this.s1=s1;
        this.s2=s2;
        this.s3=s3;
        this.s4=s4;
    }
    public int getS1(){
       return s1;
    }
    public int getS2(){
        return s2;
    }
    public int getS3(){
        return s3;
    }
    public int getS4(){
        return s4;
    }
    public int getS(){
        return s;
    }
}
