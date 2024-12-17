package crud.springboot.controller;

import crud.springboot.model.Vehicle;
import crud.springboot.service.VehicleService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
public class VehicleController {

    @Autowired
    private VehicleService vehicleService;

    // Halaman utama
    @GetMapping("/")
    public String homepage() {
        return "index";
    }

    // Daftar kendaraan (bisa diakses admin dan user)
    @GetMapping("/vehicles")
    public String viewHomePage(Model model, HttpSession session) {
        if (session.getAttribute("username") == null) {
            return "redirect:/admin/login";
        }
        return findPaginated(1, "brand", "asc", model);
    }

    // Tambah kendaraan (khusus admin)
    @GetMapping("/showNewVehicleForm")
    public String showNewVehicleForm(Model model, HttpSession session) {
        if (!Boolean.TRUE.equals(session.getAttribute("isAdmin"))) {
            return "redirect:/admin/login";
        }
        Vehicle vehicle = new Vehicle();
        model.addAttribute("vehicle", vehicle);
        return "new_vehicle";
    }

    // Simpan kendaraan (khusus admin)
    @PostMapping("/saveVehicle")
    public String saveVehicle(@ModelAttribute("vehicle") Vehicle vehicle, HttpSession session) {
        if (!Boolean.TRUE.equals(session.getAttribute("isAdmin"))) {
            return "redirect:/admin/login";
        }
        vehicleService.saveVehicle(vehicle);
        return "redirect:/vehicles";
    }

    // Update kendaraan (khusus admin)
    @GetMapping("/showFormForUpdate/{id}")
    public String showFormForUpdate(@PathVariable(value = "id") long id, Model model, HttpSession session) {
        if (!Boolean.TRUE.equals(session.getAttribute("isAdmin"))) {
            return "redirect:/admin/login";
        }
        Vehicle vehicle = vehicleService.getVehicleById(id);
        model.addAttribute("vehicle", vehicle);
        return "update_vehicle";
    }

    // Hapus kendaraan (khusus admin)
    @GetMapping("/deleteVehicle/{id}")
    public String deleteVehicle(@PathVariable(value = "id") long id, HttpSession session) {
        if (!Boolean.TRUE.equals(session.getAttribute("isAdmin"))) {
            return "redirect:/admin/login";
        }
        vehicleService.deleteVehicleById(id);
        return "redirect:/vehicles";
    }

    // Pagination
    @GetMapping("/page/{pageNo}")
    public String findPaginated(@PathVariable(value = "pageNo") int pageNo,
                              @RequestParam("sortField") String sortField,
                              @RequestParam("sortDir") String sortDir,
                              Model model) {
        int pageSize = 5;
        Page<Vehicle> page = vehicleService.findPaginated(pageNo, pageSize, sortField, sortDir);
        List<Vehicle> listVehicles = page.getContent();

        model.addAttribute("currentPage", pageNo);
        model.addAttribute("totalPages", page.getTotalPages());
        model.addAttribute("totalItems", page.getTotalElements());
        model.addAttribute("sortField", sortField);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("reverseSortDir", sortDir.equals("asc") ? "desc" : "asc");
        model.addAttribute("listVehicles", listVehicles);

        return "vehicle_list";
    }
}