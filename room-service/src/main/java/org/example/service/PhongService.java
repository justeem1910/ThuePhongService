package org.example.service;

import org.example.model.Phong;
import org.example.repository.PhongRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PhongService {
    private final PhongRepository repo;

    public PhongService(PhongRepository repo) {
        this.repo = repo;
    }

    public List<Phong> getAll() { return repo.findAll(); }
    public Phong getById(int id) {
        return repo.findById(id);
    }
    public int add(Phong p) {
        if (repo.existsByMaPhong(p.getMaPhong())) {
            throw new RuntimeException("Ma phòng đã tồn tại!");
        }
        return repo.insert(p);
    }

    public List<Phong> getPhongTrongTheoGioiTinh(String gioiTinh) {
        return repo.findPhongTrongTheoLoai(gioiTinh);
    }

    public int update(Phong p) { return repo.update(p); }
    public int delete(int id) { return repo.delete(id); }
}

