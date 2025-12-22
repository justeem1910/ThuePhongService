package org.example;

import org.example.HopDongDTO;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;

@Controller
public class WebController {

    // Gọi vào Gateway, không gọi trực tiếp service con
    private final String GATEWAY_URL = "http://localhost:8889/api/hopdong";

    private final RestTemplate restTemplate;

    public WebController(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    // 1. Trang chủ: Hiển thị danh sách hợp đồng
    @GetMapping("/")
    public String index(Model model) {
        try {
            // Gọi GET http://localhost:8889/api/hopdong
            HopDongDTO[] response = restTemplate.getForObject(GATEWAY_URL, HopDongDTO[].class);
            List<HopDongDTO> list = Arrays.asList(response);
            model.addAttribute("dsHopDong", list);
        } catch (Exception e) {
            model.addAttribute("error", "Lỗi kết nối Gateway: " + e.getMessage());
        }
        return "index";
    }

    // 2. Trang Form thêm mới
    @GetMapping("/add")
    public String showAddForm(Model model) {
        model.addAttribute("hopDong", new HopDongDTO());
        return "add-form";
    }

    // 3. Xử lý nút Lưu
    @PostMapping("/save")
    public String saveContract(@ModelAttribute HopDongDTO hopDong, Model model) {
        try {
            // Gọi POST http://localhost:8889/api/hopdong
            restTemplate.postForObject(GATEWAY_URL, hopDong, String.class);
            return "redirect:/"; // Thành công thì quay về trang chủ
        } catch (Exception e) {
            // Nếu lỗi (Ví dụ: Sinh viên không tồn tại) -> Hiện lại form và báo lỗi
            model.addAttribute("error", "Thêm thất bại! (Kiểm tra lại ID Sinh viên/Phòng có tồn tại không?)");
            model.addAttribute("hopDong", hopDong); // Giữ lại dữ liệu cũ
            return "add-form";
        }
    }
}