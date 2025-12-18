package org.example.model;

import java.sql.Date;

public class SinhVien {
    private int maSv;          // PK
    private String ten;
    private Date ngaySinh;
    private String gioiTinh;
    private String sdt;
    private String email;

    // Getter - Setter
    public int getMaSv() { return maSv; }
    public void setMaSv(int maSv) { this.maSv = maSv; }

    public String getTen() { return ten; }
    public void setTen(String ten) { this.ten = ten; }

    public Date getNgaySinh() { return ngaySinh; }
    public void setNgaySinh(Date ngaySinh) { this.ngaySinh = ngaySinh; }

    public String getGioiTinh() { return gioiTinh; }
    public void setGioiTinh(String gioiTinh) { this.gioiTinh = gioiTinh; }

    public String getSdt() { return sdt; }
    public void setSdt(String sdt) { this.sdt = sdt; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
}
