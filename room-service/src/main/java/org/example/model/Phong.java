package org.example.model;

public class Phong {
    private int maPhong;          // FK -> ToaNha
    private int soLuongToiDa;
    private String loaiPhong;
    private double giaPhong;
    private int soLuongDaThue;

    // Getter - Setter
    public int getMaPhong() { return maPhong; }
    public void setMaPhong(int maPhong) { this.maPhong = maPhong; }

    public int getSoLuongToiDa() { return soLuongToiDa; }
    public void setSoLuongToiDa(int soLuongToiDa) { this.soLuongToiDa = soLuongToiDa; }

    public String getLoaiPhong() { return loaiPhong; }
    public void setLoaiPhong(String loaiPhong) { this.loaiPhong = loaiPhong; }

    public double getGiaPhong() { return giaPhong; }
    public void setGiaPhong(double giaPhong) { this.giaPhong = giaPhong; }

    public int getSoLuongDaThue() { return soLuongDaThue; }
    public void setSoLuongDaThue(int soLuongDaThue) { this.soLuongDaThue = soLuongDaThue; }
}
