package es.codeurjc.scam_g18.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import es.codeurjc.scam_g18.dto.GlobalDataDTO;
import es.codeurjc.scam_g18.dto.GlobalMapper;
import es.codeurjc.scam_g18.service.UserService;
import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/v1")
public class GlobalRestController {

    @Autowired
    private UserService userService;

    @Autowired
    private GlobalMapper globalMapper;

    @GetMapping("/global")
    public ResponseEntity<GlobalDataDTO> getGlobalData(HttpServletRequest request) {
        boolean hasPrincipal = (request.getUserPrincipal() != null);
        Map<String, Object> viewData = userService.getGlobalHeaderViewData(hasPrincipal);

        GlobalDataDTO dto = globalMapper.toDTO(viewData, request);

        return ResponseEntity.ok(dto);
    }
}
