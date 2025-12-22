package org.example.model;

import java.sql.Date;

public class HopDongThue {
    private int maHopDong;
    private int sinhVien;
    private int phong;
    //    private ToaNha toaNha;
    private Date ngayBatDau;
    private Date ngayKetThuc;
    private String trangThai;

    // Getter - Setter
    public int getMaHopDong() { return maHopDong; }
    public void setMaHopDong(int maHopDong) { this.maHopDong = maHopDong; }

    public int getSinhVien() { return sinhVien; }
    public void setSinhVien(int sinhVien) { this.sinhVien = sinhVien; }

    public int getPhong() { return phong; }
    public void setPhong(int phong) { this.phong = phong; }

    public Date getNgayBatDau() { return ngayBatDau; }
    public void setNgayBatDau(Date ngayBatDau) { this.ngayBatDau = ngayBatDau; }

    public Date getNgayKetThuc() { return ngayKetThuc; }
    public void setNgayKetThuc(Date ngayKetThuc) { this.ngayKetThuc = ngayKetThuc; }

    public String getTrangThai() { return trangThai; }
    public void setTrangThai(String trangThai) { this.trangThai = trangThai; }
}
