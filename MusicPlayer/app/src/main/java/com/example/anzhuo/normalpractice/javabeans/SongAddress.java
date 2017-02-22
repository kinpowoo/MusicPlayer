package com.example.anzhuo.normalpractice.javabeans;

import java.util.List;
public class SongAddress{
    private List<Data> data;

    public List<Data> getData() {
        return this.data;
    }

    public void setData(List<Data> data) {
        this.data = data;
    }

    public SongAddress() {}

    public SongAddress(List<Data> data){
        super();
        this.data = data;
    }

public static class Data{
    private long payed;
    private Object uf;
    private long code;
    private String md5;
    private double gain;
    private boolean canExtend;
    private long br;
    private long fee;
    private long size;
    private String url;
    private long id;
    private long expi;
    private long flag;
    private String type;

    public long getPayed() {
        return this.payed;
    }

    public void setPayed(long payed) {
        this.payed = payed;
    }

    public Object getUf() {
        return this.uf;
    }

    public void setUf(Object uf) {
        this.uf = uf;
    }

    public long getCode() {
        return this.code;
    }

    public void setCode(long code) {
        this.code = code;
    }

    public String getMd5() {
        return this.md5;
    }

    public void setMd5(String md5) {
        this.md5 = md5;
    }

    public double getGain() {
        return this.gain;
    }

    public void setGain(double gain) {
        this.gain = gain;
    }

    public boolean getCanExtend() {
        return this.canExtend;
    }

    public void setCanExtend(boolean canExtend) {
        this.canExtend = canExtend;
    }

    public long getBr() {
        return this.br;
    }

    public void setBr(long br) {
        this.br = br;
    }

    public long getFee() {
        return this.fee;
    }

    public void setFee(long fee) {
        this.fee = fee;
    }

    public long getSize() {
        return this.size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public String getUrl() {
        return this.url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public long getId() {
        return this.id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getExpi() {
        return this.expi;
    }

    public void setExpi(long expi) {
        this.expi = expi;
    }

    public long getFlag() {
        return this.flag;
    }

    public void setFlag(long flag) {
        this.flag = flag;
    }

    public String getType() {
        return this.type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Data() {}

    public Data(long payed, Object uf, long code, String md5, double gain, boolean canExtend, long br, long fee, long size, String url, long id, long expi, long flag, String type){
        super();
        this.payed = payed;
        this.uf = uf;
        this.code = code;
        this.md5 = md5;
        this.gain = gain;
        this.canExtend = canExtend;
        this.br = br;
        this.fee = fee;
        this.size = size;
        this.url = url;
        this.id = id;
        this.expi = expi;
        this.flag = flag;
        this.type = type;
    }
}
}

