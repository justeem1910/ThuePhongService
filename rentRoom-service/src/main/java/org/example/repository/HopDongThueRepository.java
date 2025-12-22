package org.example.repository;

import org.example.model.HopDongThue;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class HopDongThueRepository {
    private final JdbcTemplate jdbcTemplate;

    public HopDongThueRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    // RowMapper mới: Map thẳng ID vào model, không tạo object con nữa
    private final RowMapper<HopDongThue> rowMapper = new RowMapper<>() {
        @Override
        public HopDongThue mapRow(ResultSet rs, int rowNum) throws SQLException {
            HopDongThue hd = new HopDongThue();
            hd.setMaHopDong(rs.getInt("ma_hop_dong"));

            // Lưu ý: Tên cột trong DB là ma_sv, nhưng trong model bạn đặt là sinhVien
            hd.setSinhVien(rs.getInt("ma_sv"));

            // Tên cột trong DB là ma_phong, model là phong
            hd.setPhong(rs.getInt("ma_phong"));

            hd.setNgayBatDau(rs.getDate("ngay_bat_dau"));
            hd.setNgayKetThuc(rs.getDate("ngay_ket_thuc"));
            hd.setTrangThai(rs.getString("trang_thai"));
            return hd;
        }
    };

    public List<HopDongThue> findAll() {
        // Code gọn hơn rất nhiều vì không cần JOIN
        String sql = "SELECT * FROM hop_dong_thue";
        return jdbcTemplate.query(sql, rowMapper);
    }

    public HopDongThue findById(int id) {
        String sql = "SELECT * FROM hop_dong_thue WHERE ma_hop_dong = ?";
        // Dùng query stream để tránh lỗi nếu không tìm thấy
        List<HopDongThue> results = jdbcTemplate.query(sql, rowMapper, id);
        return results.isEmpty() ? null : results.get(0);
    }

    // Tìm hợp đồng theo mã sinh viên (Hỗ trợ microservice tra cứu)
    public List<HopDongThue> findBySinhVienId(int maSv) {
        String sql = "SELECT * FROM hop_dong_thue WHERE ma_sv = ?";
        return jdbcTemplate.query(sql, rowMapper, maSv);
    }

    // Tìm hợp đồng theo mã phòng
    public List<HopDongThue> findByPhongId(int maPhong) {
        String sql = "SELECT * FROM hop_dong_thue WHERE ma_phong = ?";
        return jdbcTemplate.query(sql, rowMapper, maPhong);
    }

    public int insert(HopDongThue hd) {
        String sql = "INSERT INTO hop_dong_thue(ma_sv, ma_phong, ngay_bat_dau, ngay_ket_thuc, trang_thai) VALUES (?, ?, ?, ?, ?)";
        // Bây giờ gọi getter ra int luôn, không cần .getMaSv() nữa
        return jdbcTemplate.update(sql,
                hd.getSinhVien(),
                hd.getPhong(),
                hd.getNgayBatDau(),
                hd.getNgayKetThuc(),
                hd.getTrangThai());
    }

    public int update(HopDongThue hd) {
        String sql = "UPDATE hop_dong_thue SET ma_sv=?, ma_phong=?, ngay_bat_dau=?, ngay_ket_thuc=?, trang_thai=? WHERE ma_hop_dong=?";
        return jdbcTemplate.update(sql,
                hd.getSinhVien(),
                hd.getPhong(),
                hd.getNgayBatDau(),
                hd.getNgayKetThuc(),
                hd.getTrangThai(),
                hd.getMaHopDong());
    }

    public int delete(int id) {
        return jdbcTemplate.update("DELETE FROM hop_dong_thue WHERE ma_hop_dong=?", id);
    }
}