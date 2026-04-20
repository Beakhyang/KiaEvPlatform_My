package com.kiaev.admin.car;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.kiaev.client.car.Car;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/admin/car")
@RequiredArgsConstructor
public class AdminCarController {

    private final AdminCarService adminCarService;

    @GetMapping("/list")
    public String list(Model model) {
        model.addAttribute("carList", adminCarService.getCars());
        return "admin/car/list";
    }

    @GetMapping("/new")
    public String createForm(Model model) {
        model.addAttribute("car", adminCarService.createEmptyCar());
        model.addAttribute("isEdit", false);
        return "admin/car/form";
    }

    @GetMapping("/edit/{carNo}")
    public String editForm(@PathVariable("carNo") Long carNo, Model model) {
        model.addAttribute("car", adminCarService.getCar(carNo));
        model.addAttribute("isEdit", true);
        return "admin/car/form";
    }

    @PostMapping("/save")
    public String save(Car car) {
        adminCarService.save(car);
        return "redirect:/admin/car/list";
    }

    @PostMapping("/delete/{carNo}")
    public String delete(@PathVariable("carNo") Long carNo) {
        adminCarService.delete(carNo);
        return "redirect:/admin/car/list";
    }
}
