package com.hieptran.smarthome_server.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hieptran.smarthome_server.config.CacheConfig;
import com.hieptran.smarthome_server.dto.ApiResponse;
import com.hieptran.smarthome_server.dto.StatusCodeEnum;
import com.hieptran.smarthome_server.dto.builder.ResponseBuilder;
import com.hieptran.smarthome_server.dto.requests.HomeRequest;
import com.hieptran.smarthome_server.dto.requests.SettingRequest;
import com.hieptran.smarthome_server.dto.responses.HomeResponse;
import com.hieptran.smarthome_server.model.*;
import com.hieptran.smarthome_server.model.enumeration.SettingKey;
import com.hieptran.smarthome_server.repository.HomeOptionRepository;
import com.hieptran.smarthome_server.repository.HomeRepository;
import com.hieptran.smarthome_server.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class HomeService {
    private final HomeRepository homeRepository;
    private final UserRepository userRepository;
    private final UserService userService;
    private final MqttService mqttService;
    private final HomeOptionRepository homeOptionRepository;
    private final ObjectMapper objectMapper;
    private final CacheConfig cache;
    @Value("${mqtt.homeId}")
    private String homeId;

    public ResponseEntity<ApiResponse<HomeResponse>> createHome(HomeRequest homeRequest) {
        try {
            User user = userService.getUserFromContext();

            if (user == null) {
                return ResponseBuilder.badRequestResponse("User not found", StatusCodeEnum.HOME0200);
            }

            Home newHome = Home.builder()
                    .description(homeRequest.getDescription())
                    .ownerId(user)
                    .title(homeRequest.getTitle())
                    .homePodId(homeRequest.getHomePodId())
                    .build();

            mqttService.subcribe(homeRequest.getHomePodId());

            homeRepository.save(newHome);

            HomeResponse homeResponse = HomeResponse.from(newHome);

            return ResponseBuilder.successResponse("Home created", homeResponse, StatusCodeEnum.HOME1200);
        } catch (Exception e) {
            return ResponseBuilder.badRequestResponse(e.getMessage(), StatusCodeEnum.HOME0200);
        }
    }

    public ResponseEntity<ApiResponse<Home>> updateHome(HomeRequest homeRequest, String id) {
        try {
            Optional<Home> home = homeRepository.findById(id);

            if (home.isEmpty()) {
                return ResponseBuilder.badRequestResponse("Home not found", StatusCodeEnum.HOME0200);
            }

            home.get().setDescription(homeRequest.getDescription());
            home.get().setTitle(homeRequest.getTitle());

            if (home.get().getHomePodId() != null && !home.get().getHomePodId().equals(homeRequest.getHomePodId())) {
                mqttService.unSubcribe(home.get().getHomePodId());
            }
            mqttService.subcribe(homeRequest.getHomePodId());
            home.get().setHomePodId(homeRequest.getHomePodId());

            homeRepository.save(home.get());

            return ResponseBuilder.successResponse("Home updated", home.get(), StatusCodeEnum.HOME1200);
        } catch (Exception e) {
            return ResponseBuilder.badRequestResponse(e.getMessage(), StatusCodeEnum.HOME0200);
        }
    }

    public ResponseEntity<ApiResponse<List<HomeResponse>>> getHomesFromUserId() {
        try {
            User user = userService.getUserFromContext();

            if (user == null) {
                return ResponseBuilder.badRequestResponse("User not found", StatusCodeEnum.HOME0200);
            }

            List<HomeResponse> homeResponses = homeRepository.findAllByOwnerId(user).stream()
                    .map(HomeResponse::from)
                    .toList();

            return ResponseBuilder.successResponse("Homes found", homeResponses, StatusCodeEnum.HOME1200);
        } catch (Exception e) {
            return ResponseBuilder.badRequestResponse(e.getMessage(), StatusCodeEnum.HOME0200);
        }
    }

    public ResponseEntity<ApiResponse<HomeResponse>> getHome(String id) {
        try {
            User user = userService.getUserFromContext();

            if (user == null) {
                return ResponseBuilder.badRequestResponse("User not found", StatusCodeEnum.HOME0200);
            }

            Home home = homeRepository.findById(id).orElse(null);

            if (home == null) {
                return ResponseBuilder.badRequestResponse("Home not found", StatusCodeEnum.HOME0200);
            }

            HomeResponse homeResponse = HomeResponse.from(home);

            return ResponseBuilder.successResponse("Home found", homeResponse, StatusCodeEnum.HOME1200);
        } catch (Exception e) {
            return ResponseBuilder.badRequestResponse(e.getMessage(), StatusCodeEnum.HOME0200);
        }
    }

    public ResponseEntity<ApiResponse<Objects>> deleteHome(String id) {
        try {
            User user = userService.getUserFromContext();

            if (user == null) {
                return ResponseBuilder.badRequestResponse("User not found", StatusCodeEnum.HOME0200);
            }

            Optional<Home> home = homeRepository.findById(id);

            if (home.isEmpty()) {
                return ResponseBuilder.badRequestResponse("Home not found", StatusCodeEnum.HOME0200);
            }

            mqttService.unSubcribe(home.get().getHomePodId());

            homeRepository.delete(home.get());

            return ResponseBuilder.successResponse("Home deleted", StatusCodeEnum.HOME1200);
        } catch (Exception e) {
            return ResponseBuilder.badRequestResponse(e.getMessage(), StatusCodeEnum.HOME0200);
        }
    }


    public ResponseEntity<ApiResponse<HomeOption>> getHomeOption(String homeId) {
        try {
            User user = userService.getUserFromContext();

            if (user == null) {
                return ResponseBuilder.badRequestResponse("User not found", StatusCodeEnum.HOME0200);
            }

            Optional<Home> home = homeRepository.findById(homeId);

            if (home.isEmpty()) {
                return ResponseBuilder.badRequestResponse("Home not found", StatusCodeEnum.HOME0200);
            }

            HomeOption homeOption = home.get().getHomeOption();

            return ResponseBuilder.successResponse("Home option found", homeOption, StatusCodeEnum.HOME1200);
        } catch (Exception e) {
            return ResponseBuilder.badRequestResponse(e.getMessage(), StatusCodeEnum.HOME0200);
        }
    }

    public ResponseEntity<ApiResponse<HomeOption>> applyHomeOption(SettingRequest settingRequest, String homeId) {
        return processHomeOption(settingRequest, homeId, "Home option applied");
    }

    public ResponseEntity<ApiResponse<HomeOption>> updateHomeOption(SettingRequest settingRequest, String homeId) {
        return processHomeOption(settingRequest, homeId, "Home option updated");
    }

    private ResponseEntity<ApiResponse<HomeOption>> processHomeOption(SettingRequest settingRequest, String homeId, String successMessage) {
        try {
            Home home = homeRepository.findById(homeId).orElse(null);

            if (home == null) {
                return ResponseBuilder.badRequestResponse("Home not found", StatusCodeEnum.HOME0200);
            }

            HomeOption homeOption = processAndCacheHomeOption(settingRequest, home.getHomePodId());

            home.setHomeOption(homeOption);

            homeRepository.save(home);

            return ResponseBuilder.successResponse(successMessage, homeOption, StatusCodeEnum.HOME1200);
        } catch (Exception e) {
            return ResponseBuilder.badRequestResponse(e.getMessage(), StatusCodeEnum.HOME0200);
        }
    }

    private HomeOption processAndCacheHomeOption(SettingRequest settingRequest, String homePodId) {

        TempAutoOption tempAutoOption = objectMapper.convertValue(settingRequest.getTemperature(), TempAutoOption.class);

        HumiAutoOption humiAutoOption = objectMapper.convertValue(settingRequest.getHumidity(), HumiAutoOption.class);

        LightAutoOption lightAutoOption = objectMapper.convertValue(settingRequest.getLight(), LightAutoOption.class);

        HomeOption homeOption = HomeOption.builder()
                .controlType(settingRequest.getControlType())
                .tempAutoOption(tempAutoOption)
                .humiAutoOption(humiAutoOption)
                .lightAutoOption(lightAutoOption)
                .build();

        cache.put(homePodId, homeOption);

        return homeOption;
    }

    private <T, R extends AutoOption> R processAndCacheOption(T request, Class<R> optionClass, String highKey, String lowKey, String highDevicesKey, String lowDevicesKey) {
        R option = objectMapper.convertValue(request, optionClass);

        cache.put(highKey, option.getGreaterThan());
        cache.put(lowKey, option.getLessThan());
        cache.put(highDevicesKey, option.getHighDevices());
        cache.put(lowDevicesKey, option.getLowDevices());

        return option;
    }

    private <T> List<DeviceAuto> convertDeviceAutoRequests(List<T> deviceAutoRequests, Class<DeviceAuto> deviceAutoClass) {
        return deviceAutoRequests.stream()
                .map(request -> objectMapper.convertValue(request, deviceAutoClass))
                .collect(Collectors.toList());
    }

    @PostConstruct
    private void initCache() {
        List<Home> homes = homeRepository.findAll();
        for (Home home : homes) {
            HomeOption homeOption = home.getHomeOption();
            if (homeOption != null) {
                cache.put(home.getHomePodId(), homeOption);
            }
        }
    }

    private <T> void processSetting(T setting) {
        // Implementation here
    }
}