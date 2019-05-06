package com.example.hac.notebook;

/**
 * Created by Hac on 18/03/2017.
 */

public class Noidung {

    public Integer id;
    public String tensukien;
    public String noidung;
    public byte[] hinhdaidien;


    public Noidung(Integer id, String tensukien, String noidung, byte[] hinhdaidien) {
        this.id = id;
        this.tensukien = tensukien;
        this.noidung = noidung;
        this.hinhdaidien = hinhdaidien;
    }

}
