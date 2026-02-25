package es.codeurjc.scam_g18.controller;

import java.io.IOException;
import java.sql.SQLException;
import java.security.Principal;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import es.codeurjc.scam_g18.model.User;
import es.codeurjc.scam_g18.service.EnrollmentService;
import es.codeurjc.scam_g18.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@Controller
public class ProfileController {

    @Autowired
    private UserService userService;

    @Autowired
    private EnrollmentService enrollmentService;

    @GetMapping("/profile/me")
    public String myProfile(Principal principal) {
        if (principal == null)
            return "redirect:/login";
        return userService.findByUsername(principal.getName())
                .map(user -> "redirect:/profile/" + user.getId())
                .orElse("redirect:/login");
    }

    @GetMapping("/profile/{id}")
    public String profile(Model model, @PathVariable long id) {
        Optional<User> user = userService.findById(id);
        if (user.isEmpty())
            return "redirect:/";
        model.addAttribute("user", user.get());
        model.addAttribute("profileImage", userService.getProfileImage(id));
        model.addAttribute("completedCourses", userService.getCompletedCoursesCount(id));
        model.addAttribute("completedCourseNames", enrollmentService.getCompletedCourseNames(id));
        model.addAttribute("inProgressCount", enrollmentService.getInProgressCount(id));
        model.addAttribute("overallProgress", enrollmentService.getOverallCourseProgress(id));
        model.addAttribute("completedLessons", enrollmentService.getCompletedLessonsCount(id));
        model.addAttribute("userType", userService.getUserType(id));
        model.addAttribute("userTags", enrollmentService.getTagNamesByUserId(id));
        model.addAttribute("subscribedCourses", enrollmentService.getSubscribedCoursesData(id));
        model.addAttribute("userEvents", enrollmentService.getUserEvents(id));

        return "profile";
    }

    @PostMapping("/profile/{id}/edit")
    public String editProfile(@PathVariable long id,
            @RequestParam String username,
            @RequestParam String email,
            @RequestParam(required = false) String country,
            @RequestParam(required = false) String shortDescription,
            @RequestParam(required = false) String currentGoal,
            @RequestParam(required = false) String weeklyRoutine,
            @RequestParam(required = false) String comunity,
            @RequestParam(required = false) MultipartFile imageFile,
            HttpServletRequest request) throws IOException, SQLException {

        userService.updateProfile(id, username, email, country, shortDescription, currentGoal, weeklyRoutine,
                comunity, imageFile);

        // Actualizar la sesión de Spring Security si el username cambió
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && username != null && !username.isBlank() && !auth.getName().equals(username)) {
            Authentication newAuth = new UsernamePasswordAuthenticationToken(
                    username, auth.getCredentials(), auth.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(newAuth);

            HttpSession session = request.getSession(false);
            if (session != null) {
                session.setAttribute("SPRING_SECURITY_CONTEXT", SecurityContextHolder.getContext());
            }
        }

        return "redirect:/profile/" + id;
    }

}
