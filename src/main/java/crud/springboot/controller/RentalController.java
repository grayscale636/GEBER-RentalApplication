package crud.springboot.controller;

import crud.springboot.model.Rental;
import crud.springboot.model.Vehicle;
import crud.springboot.service.RentalService;
import crud.springboot.service.VehicleService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Controller
public class RentalController {

    @Autowired
    private RentalService rentalService;

    @Autowired
    private VehicleService vehicleService;

    @GetMapping("/rent/{vehicleId}")
    public String showRentalForm(@PathVariable Long vehicleId, Model model) {
        Vehicle vehicle = vehicleService.getVehicleById(vehicleId);
        Rental rental = new Rental();
        rental.setVehicle(vehicle);
        model.addAttribute("vehicle", vehicle);
        model.addAttribute("rental", rental);
        return "rental_form";
    }

    @PostMapping("/rent/save")
    public String saveRental(@ModelAttribute Rental rental, RedirectAttributes redirectAttributes) {
        try {
            // date validation
            if (rental.getStartDate().isBefore(LocalDate.now())) {
                throw new RuntimeException("Tanggal mulai tidak boleh di masa lalu");
            }
            if (rental.getEndDate().isBefore(rental.getStartDate())) {
                throw new RuntimeException("Tanggal selesai harus setelah tanggal mulai");
            }

            // count total harga
            long days = ChronoUnit.DAYS.between(rental.getStartDate(), rental.getEndDate());
            if (days < 1) {
                throw new RuntimeException("Minimal rental 1 hari");
            }

            Vehicle vehicle = vehicleService.getVehicleById(rental.getVehicle().getId());
            rental.setTotalPrice(vehicle.getDailyRate().multiply(BigDecimal.valueOf(days)));
            rental.setStatus("ACTIVE");

            // save rentals
            rentalService.saveRental(rental);

            // update status
            vehicle.setStatus("RENTED");
            vehicleService.saveVehicle(vehicle);

            redirectAttributes.addFlashAttribute("rental", rental);
            redirectAttributes.addFlashAttribute("success", "Rental berhasil dibuat!");
            return "redirect:/rental/confirmation";
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/rent/" + rental.getVehicle().getId();
        }
    }

    @GetMapping("/rentals")
    public String listRentals(Model model, HttpSession session) {
        if (session.getAttribute("username") == null) {
            return "redirect:/admin/login";
        }
    
        List<Rental> rentals;
        if (Boolean.TRUE.equals(session.getAttribute("isAdmin"))) {
            rentals = rentalService.getAllRentals();
        } else {
            String username = (String) session.getAttribute("username");
            rentals = rentalService.getRentalsByCustomerName(username);
        }
    
        model.addAttribute("rentals", rentals);
        return "rental_list";
    }

    @GetMapping("/rental/confirmation")
    public String showConfirmation(@ModelAttribute("rental") Rental rental, Model model) {
        model.addAttribute("rental", rental);
        return "rental_confirmation";
    }

    @GetMapping("/rental/print/{id}")
    public String printRental(@PathVariable Long id, Model model) {
        Rental rental = rentalService.getRentalById(id);
        model.addAttribute("rental", rental);
        return "rental_print";
    }
}