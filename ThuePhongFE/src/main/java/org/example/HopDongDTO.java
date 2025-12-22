package org.example;

import java.sql.Date;

// Bỏ @Data của Lombok đi để tránh lỗi, tự viết Getter/Setter
public class HopDongDTO {
    private int maHopDong;
    private int sinhVien;
    private int phong;
    private Date ngayBatDau;
    private Date ngayKetThuc;
    private String trangThai;

    // Constructor rỗng (Bắt buộc)
    public HopDongDTO() {
    }

    // Constructor đầy đủ
    public HopDongDTO(int maHopDong, int sinhVien, int phong, Date ngayBatDau, Date ngayKetThuc, String trangThai) {
        this.maHopDong = maHopDong;
        this.sinhVien = sinhVien;
        this.phong = phong;
        this.ngayBatDau = ngayBatDau;
        this.ngayKetThuc = ngayKetThuc;
        this.trangThai = trangThai;
    }

    // --- GETTER & SETTER (Thymeleaf cần cái này để đọc dữ liệu) ---

    public int getMaHopDong() {
        return maHopDong;
    }

    public void setMaHopDong(int maHopDong) {
        this.maHopDong = maHopDong;
    }

    public int getSinhVien() {
        return sinhVien;
    }

    public void setSinhVien(int sinhVien) {
        this.sinhVien = sinhVien;
    }

    public int getPhong() {
        return phong;
    }

    public void setPhong(int phong) {
        this.phong = phong;
    }

    public Date getNgayBatDau() {
        return ngayBatDau;
    }

    public void setNgayBatDau(Date ngayBatDau) {
        this.ngayBatDau = ngayBatDau;
    }

    public Date getNgayKetThuc() {
        return ngayKetThuc;
    }

    public void setNgayKetThuc(Date ngayKetThuc) {
        this.ngayKetThuc = ngayKetThuc;
    }

    public String getTrangThai() {
        return trangThai;
    }

    public void setTrangThai(String trangThai) {
        this.trangThai = trangThai;
    }
}