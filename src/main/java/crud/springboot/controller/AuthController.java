package crud.springboot.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class AuthController {

    @GetMapping("/login")
    public String showLoginForm() {
        return "login";
    }

    @PostMapping("/login")
    public String login(@RequestParam String username, 
                       @RequestParam String password,
                       HttpSession session,
                       RedirectAttributes redirectAttributes) {

        if ("admin".equals(username) && "admin123".equals(password)) {
            session.setAttribute("isAdmin", true);
            session.setAttribute("username", username);
            return "redirect:/admin/dashboard";
        } else if ("user".equals(username) && "user123".equals(password)) {
            session.setAttribute("isUser", true);
            session.setAttribute("username", username);
            return "redirect:/vehicles";
        }
        
        redirectAttributes.addFlashAttribute("error", "Username atau password salah!");
        return "redirect:/login";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }
}