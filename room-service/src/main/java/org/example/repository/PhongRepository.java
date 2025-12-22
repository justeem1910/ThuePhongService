package org.example.repository;

import org.example.model.Phong;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class PhongRepository {
    private final JdbcTemplate jdbcTemplate;

    public PhongRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    // Mapper chuẩn theo Model Phong hiện tại
    private final RowMapper<Phong> rowMapper = new RowMapper<Phong>() {
        @Override
        public Phong mapRow(ResultSet rs, int rowNum) throws SQLException {
            Phong p = new Phong();
            p.setMaPhong(rs.getInt("ma_phong"));
            p.setSoLuongToiDa(rs.getInt("so_luong_toi_da"));
            p.setLoaiPhong(rs.getString("loai_phong"));
            p.setGiaPhong(rs.getDouble("gia_phong"));
            p.setSoLuongDaThue(rs.getInt("so_luong_da_thue"));
            return p;
        }
    };

    public List<Phong> findAll() {
        // Bỏ JOIN vì model không chứa thông tin tòa nhà
        String sql = "SELECT * FROM phong";
        return jdbcTemplate.query(sql, rowMapper);
    }

    public Phong findById(int id) {
        String sql = "SELECT * FROM phong WHERE ma_phong = ?";
        // Dùng query query().stream().findFirst() hoặc try-catch để tránh lỗi nếu không tìm thấy
        List<Phong> results = jdbcTemplate.query(sql, rowMapper, id);
        return results.isEmpty() ? null : results.get(0);
    }

    // Tìm phòng còn trống theo loại (ví dụ: Nam/Nữ/VIP)
    public List<Phong> findPhongTrongTheoLoai(String loaiPhong) {
        String sql = """
                     SELECT * FROM phong 
                     WHERE loai_phong ILIKE ? 
                       AND so_luong_da_thue < so_luong_toi_da
                     """;
        // Lưu ý: Dùng ILIKE để tìm kiếm không phân biệt hoa thường (Postgres)
        return jdbcTemplate.query(sql, rowMapper, "%" + loaiPhong + "%");
    }

    public boolean existsByMaPhong(int maPhong) {
        String sql = "SELECT COUNT(*) FROM phong WHERE ma_phong = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, maPhong);
        return count != null && count > 0;
    }

    public int insert(Phong p) {
        // Sửa lại: 5 dấu ? tương ứng với 5 tham số
        String sql = "INSERT INTO phong(ma_phong, so_luong_toi_da, loai_phong, gia_phong, so_luong_da_thue) VALUES (?, ?, ?, ?, ?)";
        return jdbcTemplate.update(sql,
                p.getMaPhong(),
                p.getSoLuongToiDa(),
                p.getLoaiPhong(),
                p.getGiaPhong(),
                p.getSoLuongDaThue());
    }

    public int update(Phong p) {
        String sql = "UPDATE phong SET so_luong_toi_da=?, loai_phong=?, gia_phong=?, so_luong_da_thue=? WHERE ma_phong=?";
        return jdbcTemplate.update(sql,
                p.getSoLuongToiDa(),
                p.getLoaiPhong(),
                p.getGiaPhong(),
                p.getSoLuongDaThue(),
                p.getMaPhong());
    }

    public int delete(int id) {
        return jdbcTemplate.update("DELETE FROM phong WHERE ma_phong=?", id);
    }
}