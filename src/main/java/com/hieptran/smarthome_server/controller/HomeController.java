package com.hieptran.smarthome_server.controller;

import com.hieptran.smarthome_server.Service.HomeService;
import com.hieptran.smarthome_server.dto.ApiResponse;
import com.hieptran.smarthome_server.dto.requests.HomeRequest;
import com.hieptran.smarthome_server.dto.requests.SettingRequest;
import com.hieptran.smarthome_server.dto.responses.HomeResponse;
import com.hieptran.smarthome_server.model.Home;
import com.hieptran.smarthome_server.model.HomeOption;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/homes")
@RequiredArgsConstructor
public class HomeController {
    private final HomeService homeService;

    @PostMapping()
    public ResponseEntity<ApiResponse<HomeResponse>> createHome(@RequestBody HomeRequest homeRequest) {
        return homeService.createHome(homeRequest);
    }

    @GetMapping("/{id}")
    private ResponseEntity<ApiResponse<Home>> getHome(@PathVariable("id") String id) {
        return homeService.getHome(id);
    }

    @GetMapping("/setting/{id}")
    public ResponseEntity<ApiResponse<HomeOption>> getSetting(@PathVariable("id") String id) {
        return homeService.getHomeOption(id);
    }

    @PostMapping("/setting/{id}")
    public ResponseEntity<ApiResponse<HomeOption>> setSetting(@PathVariable("id") String id, @RequestBody SettingRequest settingRequest) {
        return homeService.applyHomeOption(settingRequest, id);
    }
}
