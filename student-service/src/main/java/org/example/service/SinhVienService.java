package org.example.service;

import org.example.model.SinhVien;
import org.example.repository.SinhVienRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SinhVienService {

    private final SinhVienRepository repository;

    public SinhVienService(SinhVienRepository repository) {
        this.repository = repository;
    }

    public List<SinhVien> getAll() {
        return repository.findAll();
    }

    public SinhVien getById(int id) {
        return repository.findById(id);
    }

    public int add(SinhVien sv) {
        if (repository.existsBySinhVien(sv.getMaSv())) {
            throw new RuntimeException("Mã sinh viên đã tồn tại!");
        }
        return repository.insert(sv);
    }

    public List<SinhVien> search(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return repository.findAll();
        }
        return repository.search(keyword.trim());
    }


    public int update(SinhVien sv) {
        return repository.update(sv);
    }

    public int delete(int id) {
        return repository.delete(id);
    }
}

