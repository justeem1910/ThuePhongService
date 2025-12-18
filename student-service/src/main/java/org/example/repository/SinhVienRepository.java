package org.example.repository;

import org.example.model.SinhVien;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class SinhVienRepository {

    private final JdbcTemplate jdbcTemplate;

    public SinhVienRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private RowMapper<SinhVien> rowMapper = new RowMapper<SinhVien>() {
        @Override
        public SinhVien mapRow(ResultSet rs, int rowNum) throws SQLException {
            SinhVien sv = new SinhVien();
            sv.setMaSv(rs.getInt("ma_sv"));
            sv.setTen(rs.getString("ho_ten"));
            sv.setNgaySinh(rs.getDate("ngay_sinh"));
            sv.setGioiTinh(rs.getString("gioi_tinh"));
            sv.setSdt(rs.getString("so_dien_thoai"));
            sv.setEmail(rs.getString("email"));
            return sv;
        }
    };

    public List<SinhVien> findAll() {
        return jdbcTemplate.query("SELECT * FROM sinh_vien", rowMapper);
    }

    public SinhVien findById(int id) {
        return jdbcTemplate.queryForObject("SELECT * FROM sinh_vien WHERE ma_sv=?", rowMapper, id);
    }

    public boolean existsBySinhVien(int maSv) {
        String sql = "SELECT COUNT(*) FROM sinh_vien WHERE ma_sv = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, maSv);
        return count != null && count > 0;
    }

    public int insert(SinhVien sv) {
        String sql = "INSERT INTO sinh_vien(ma_sv, ho_ten, ngay_sinh, gioi_tinh, so_dien_thoai, email) VALUES (?, ?, ?, ?, ?, ?)";
        return jdbcTemplate.update(sql, sv.getMaSv(), sv.getTen(), sv.getNgaySinh(), sv.getGioiTinh(), sv.getSdt(), sv.getEmail());
    }

    public int update(SinhVien sv) {
        String sql = "UPDATE sinh_vien SET ho_ten=?, ngay_sinh=?, gioi_tinh=?, so_dien_thoai=?, email=? WHERE ma_sv=?";
        return jdbcTemplate.update(sql, sv.getTen(), sv.getNgaySinh(), sv.getGioiTinh(), sv.getSdt(), sv.getEmail(), sv.getMaSv());
    }

    public List<SinhVien> search(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return findAll();
        }
        String sql = "SELECT * FROM sinh_vien WHERE CAST(ma_sv AS TEXT) LIKE ? OR ho_ten LIKE ?";
        String likePattern = "%" + keyword + "%";
        return jdbcTemplate.query(sql, rowMapper, likePattern, likePattern);
    }

    public int delete(int id) {
        return jdbcTemplate.update("DELETE FROM sinh_vien WHERE ma_sv=?", id);
    }
}