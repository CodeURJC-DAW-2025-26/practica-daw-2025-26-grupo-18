package es.codeurjc.scam_g18.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class SpaWebController {

    // React SPA entrypoint mounted under /new.
    @GetMapping({ "/new", "/new/" })
    public String spaIndex() {
        return "forward:/new/index.html";
    }

    // Fallback for client-side routes without file extension.
    @GetMapping({
            "/new/{segment:[^\\.]+}",
            "/new/{segment1:[^\\.]+}/{segment2:[^\\.]+}",
            "/new/{segment1:[^\\.]+}/{segment2:[^\\.]+}/{segment3:[^\\.]+}",
            "/new/{segment1:[^\\.]+}/{segment2:[^\\.]+}/{segment3:[^\\.]+}/{segment4:[^\\.]+}"
    })
    public String spaRoutesFallback() {
        return "forward:/new/index.html";
    }
}
