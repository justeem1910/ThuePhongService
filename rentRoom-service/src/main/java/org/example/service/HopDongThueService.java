package org.example.service;

import org.example.client.PhongClient;
import org.example.client.SinhVienClient;
import org.example.dto.PhongDto;
import org.example.dto.SinhVienDto;
import org.example.model.HopDongThue;
import org.example.repository.HopDongThueRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class HopDongThueService {

    private final HopDongThueRepository repository;
    private final SinhVienClient sinhVienClient; // Inject Feign Client
    private final PhongClient phongClient;       // Inject Feign Client

    // Constructor Injection (Spring tự động tiêm các bean vào đây)
    public HopDongThueService(HopDongThueRepository repository,
                              SinhVienClient sinhVienClient,
                              PhongClient phongClient) {
        this.repository = repository;
        this.sinhVienClient = sinhVienClient;
        this.phongClient = phongClient;
    }

    public List<HopDongThue> getAll() {
        return repository.findAll();
    }

    public HopDongThue getById(int id) {
        return repository.findById(id);
    }

    // LOGIC QUAN TRỌNG NHẤT NẰM Ở ĐÂY
    public int add(HopDongThue hd) {
        // 1. Kiểm tra Sinh viên có tồn tại bên student-service không?
        try {
            // Gọi qua mạng sang Student Service
            SinhVienDto sv = sinhVienClient.getStudentById(hd.getSinhVien());
            if (sv == null) {
                throw new RuntimeException("Sinh viên không tồn tại!");
            }
//            System.out.println("Kiểm tra SV thành công: " + sv.getTen());
        } catch (Exception e) {
            // Nếu Feign không tìm thấy (404) hoặc lỗi mạng
            throw new RuntimeException("Lỗi: Không tìm thấy sinh viên có ID = " + hd.getSinhVien());
        }

        // 2. Kiểm tra Phòng có tồn tại bên room-service không?
        try {
            // Gọi qua mạng sang Room Service
            PhongDto phong = phongClient.getRoomById(hd.getPhong());
            if (phong == null) {
                throw new RuntimeException("Phòng không tồn tại!");
            }
//            System.out.println("Kiểm tra Phòng thành công: " + phong.getLoaiPhong());
        } catch (Exception e) {
            throw new RuntimeException("Lỗi: Không tìm thấy phòng có ID = " + hd.getPhong());
        }

        // 3. Nếu cả 2 đều tồn tại thì mới cho Insert vào DB
        return repository.insert(hd);
    }

    public int update(HopDongThue hd) {
        // Tương tự, khi update cũng nên check tồn tại
        try {
            sinhVienClient.getStudentById(hd.getSinhVien());
        } catch (Exception e) {
            throw new RuntimeException("Không thể cập nhật: Sinh viên ID " + hd.getSinhVien() + " không tồn tại");
        }

        try {
            phongClient.getRoomById(hd.getPhong());
        } catch (Exception e) {
            throw new RuntimeException("Không thể cập nhật: Phòng ID " + hd.getPhong() + " không tồn tại");
        }

        return repository.update(hd);
    }

    public int delete(int id) {
        return repository.delete(id);
    }
}