package com.hieptran.smarthome_server.controller;

import com.hieptran.smarthome_server.Service.HomeService;
import com.hieptran.smarthome_server.Service.SseService;
import com.hieptran.smarthome_server.dto.ApiResponse;
import com.hieptran.smarthome_server.dto.requests.HomeRequest;
import com.hieptran.smarthome_server.dto.requests.SettingRequest;
import com.hieptran.smarthome_server.dto.responses.HomeResponse;
import com.hieptran.smarthome_server.model.Home;
import com.hieptran.smarthome_server.model.HomeOption;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.nio.file.AccessDeniedException;
import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/home")
@RequiredArgsConstructor
public class HomeController {
    private final HomeService homeService;

    private final SseService sseService;

    @PreAuthorize("isAuthenticated()")
    @PostMapping()
    public ResponseEntity<ApiResponse<HomeResponse>> createHome(@RequestBody HomeRequest homeRequest) {
        return homeService.createHome(homeRequest);
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping
    public ResponseEntity<ApiResponse<List<HomeResponse>>> getHomes() {
        return homeService.getHomesFromUserId();
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<HomeResponse>> getHome(@PathVariable("id") String id) {
        return homeService.getHome(id);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Home>> updateHome(@RequestBody HomeRequest homeRequest, @PathVariable("id") String id) {
        return homeService.updateHome(homeRequest, id);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Objects>> deleteHome(@PathVariable("id") String id) {
        return homeService.deleteHome(id);
    }

    @GetMapping("/setting/{id}")
    public ResponseEntity<ApiResponse<HomeOption>> getSetting(@PathVariable("id") String id) {
        return homeService.getHomeOption(id);
    }

    @PostMapping("/setting/{id}")
    public ResponseEntity<ApiResponse<HomeOption>> setSetting(@PathVariable("id") String id, @RequestBody SettingRequest settingRequest) {
        return homeService.applyHomeOption(settingRequest, id);
    }

    @GetMapping("/sse")
    public SseEmitter streamEvents(@RequestParam("homePodId")String homePodId) throws AccessDeniedException {
        return sseService.addEmitter(homePodId);
    }
}
