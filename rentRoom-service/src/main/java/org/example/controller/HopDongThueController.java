package org.example.controller;

import org.example.model.HopDongThue;
import org.example.service.HopDongThueService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/hopdong")
public class HopDongThueController {

    private final HopDongThueService service;

    public HopDongThueController(HopDongThueService service) {
        this.service = service;
    }

    @GetMapping
    public List<HopDongThue> getAll() {
        return service.getAll();
    }

    @GetMapping("/{id}")
    public HopDongThue getById(@PathVariable int id) {
        return service.getById(id);
    }

    @PostMapping
    public String add(@RequestBody HopDongThue hd) {
        service.add(hd);
        return "Thêm hợp đồng thành công!";
    }

    @PutMapping("/{id}")
    public String update(@PathVariable int id, @RequestBody HopDongThue hd) {
        hd.setMaHopDong(id);
        service.update(hd);
        return "Cập nhật hợp đồng thành công!";
    }

    @DeleteMapping("/{id}")
    public String delete(@PathVariable int id) {
        service.delete(id);
        return "Xóa hợp đồng thành công!";
    }
}
