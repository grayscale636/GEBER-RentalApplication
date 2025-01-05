package crud.springboot.controller;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;
import crud.springboot.model.Rental;
import crud.springboot.model.Vehicle;
import crud.springboot.service.RentalService;
import crud.springboot.service.VehicleService;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;

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

    @GetMapping("/rental/downloadPdf/{id}")
    public void downloadPdf(@PathVariable Long id, HttpServletResponse response) {
        Rental rental = rentalService.getRentalById(id);
        
        if (rental == null) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=bukti_rental_" + rental.getId() + ".pdf");
        
        // generate ODF
        try (ServletOutputStream out = response.getOutputStream()) {
            Document document = new Document();
            PdfWriter.getInstance(document, out);
            document.open();
            
            Paragraph header = new Paragraph("Bukti Rental Ceritanya", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16));
            header.setAlignment(Element.ALIGN_CENTER);
            document.add(header);
            
            document.add(new Paragraph(" "));
            document.add(new Paragraph("Berkoh Raya no.69"));
            document.add(new Paragraph("Telepon: 0335-8787-6996"));
            document.add(new Paragraph("Email: geber@info.com"));
            document.add(new Paragraph(" "));
            
            // add rental details
            LocalDate currentDate = LocalDate.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMMM yyyy");
            document.add(new Paragraph("Tanggal Pemesanan: " + currentDate.format(formatter)));

            document.add(new Paragraph("Nomor Booking: " + rental.getId()));
            document.add(new Paragraph("Nama Pelanggan: " + rental.getCustomerName()));
            document.add(new Paragraph("Total Biaya: " + rental.getTotalPrice()));
            document.add(new Paragraph("Tanggal Mulai: " + rental.getStartDate().toString()));
            document.add(new Paragraph("Tanggal Selesai: " + rental.getEndDate().toString()));
            
            document.add(new Paragraph(" "));
            
            // add footer
            document.add(new Paragraph("Terima kasih telah menggunakan layanan kami."));
            document.add(new Paragraph(" "));
            document.add(new Paragraph(" "));
            document.add(new Paragraph("__________________________"));
            document.add(new Paragraph("TTD."));
            
            document.close();
        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/rental/print/{id}")
    public String printRental(@PathVariable Long id, Model model) {
        Rental rental = rentalService.getRentalById(id);
        if (rental == null) {
            throw new RuntimeException("Rental tidak ditemukan dengan ID: " + id);
        }
        model.addAttribute("rental", rental);
        return "rental_print";
    }
}