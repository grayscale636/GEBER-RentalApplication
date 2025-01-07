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

    @GetMapping("/vehicles")
    public String viewHomePage(Model model, HttpSession session) {
        return findPaginated(1, "brand", "asc", model);
    }

   @GetMapping("/vehicles/page/{pageNo}")
   public String findPaginated(@PathVariable(value = "pageNo") int pageNo,
                                @RequestParam(defaultValue = "brand") String sortField,
                                @RequestParam(defaultValue = "asc") String sortDir,
                                Model model) {
       int pageSize = 5; 
       Page<Vehicle> page = vehicleService.findPaginated(pageNo, pageSize, sortField, sortDir);
       model.addAttribute("currentPage", pageNo);
       model.addAttribute("totalPages", page.getTotalPages());
       model.addAttribute("totalItems", page.getTotalElements());
       model.addAttribute("sortField", sortField);
       model.addAttribute("sortDir", sortDir);
       model.addAttribute("reverseSortDir", sortDir.equals("asc") ? "desc" : "asc");
       model.addAttribute("listVehicles", page.getContent());
   
       return "vehicle_list";
   }

    // add vehicles (admin only)
    @GetMapping("/showNewVehicleForm")
    public String showNewVehicleForm(Model model, HttpSession session) {
        if (!Boolean.TRUE.equals(session.getAttribute("isAdmin"))) {
            return "redirect:/admin/login";
        }
        Vehicle vehicle = new Vehicle();
        model.addAttribute("vehicle", vehicle);
        return "new_vehicle";
    }

    // save vehicles (admin only)
    @PostMapping("/saveVehicle")
    public String saveVehicle(@ModelAttribute("vehicle") Vehicle vehicle, HttpSession session, RedirectAttributes redirectAttributes) {
        if (!Boolean.TRUE.equals(session.getAttribute("isAdmin"))) {
            return "redirect:/admin/login";
        }

        if (vehicle.getYear() == null) {
            redirectAttributes.addFlashAttribute("error", "Tahun kendaraan tidak boleh kosong");
            return "redirect:/showNewVehicleForm"; 
        }

        vehicleService.saveVehicle(vehicle);
        redirectAttributes.addFlashAttribute("success", "Kendaraan berhasil disimpan!");
        return "redirect:/vehicles";
    }

    // update vehicles 
    @GetMapping("/showFormForUpdate/{id}")
    public String showFormForUpdate(@PathVariable(value = "id") long id, Model model, HttpSession session) {
        if (!Boolean.TRUE.equals(session.getAttribute("isAdmin"))) {
            return "redirect:/admin/login";
        }
        Vehicle vehicle = vehicleService.getVehicleById(id);
        model.addAttribute("vehicle", vehicle);
        return "update_vehicle";
    }

    // delete vehicles
    @GetMapping("/deleteVehicle/{id}")
    public String deleteVehicle(@PathVariable(value = "id") long id, HttpSession session, RedirectAttributes redirectAttributes) {
        if (!Boolean.TRUE.equals(session.getAttribute("isAdmin"))) {
            return "redirect:/admin/login";
        }
    
        Vehicle vehicle = vehicleService.getVehicleById(id);
        if ("RENTED".equalsIgnoreCase(vehicle.getStatus())) {
            redirectAttributes.addFlashAttribute("error", "Kendaraan sedang disewa dan tidak bisa dihapus.");
            return "redirect:/vehicles";
        }
    
        vehicleService.deleteVehicleById(id);
        redirectAttributes.addFlashAttribute("success", "Kendaraan berhasil dihapus.");
        return "redirect:/vehicles";
    }
}