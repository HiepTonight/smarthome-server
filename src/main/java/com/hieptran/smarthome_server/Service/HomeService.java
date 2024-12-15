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
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class HomeService {
    private final HomeRepository homeRepository;
    private final UserRepository userRepository;
    private final HomeOptionRepository homeOptionRepository;
    private final ObjectMapper objectMapper;
    private final CacheConfig cache;
    @Value("${mqtt.homeId}")
    private String homeId;

    public ResponseEntity<ApiResponse<HomeResponse>> createHome(HomeRequest homeRequest) {
        try {
            Optional<User> user = userRepository.findById(homeRequest.getOwnerId());

            if (user.isEmpty()) {
                return ResponseBuilder.badRequestResponse("User not found", StatusCodeEnum.HOME0200);
            }

            Home newHome = Home.builder()
                    .description(homeRequest.getDescription())
                    .ownerId(user.get())
                    .title(homeRequest.getTitle())
                    .build();

            homeRepository.save(newHome);

            HomeResponse homeResponse = HomeResponse.from(newHome);

            return ResponseBuilder.successResponse("Home created", homeResponse, StatusCodeEnum.HOME1200);
        } catch (Exception e) {
            return ResponseBuilder.badRequestResponse(e.getMessage(), StatusCodeEnum.HOME0200);
        }
    }

    public ResponseEntity<ApiResponse<Home>> getHome(String id) {
        try {
            Optional<Home> home = homeRepository.findById(id);

            if (home.isEmpty()) {
                return ResponseBuilder.badRequestResponse("Home not found", StatusCodeEnum.HOME0200);
            }

            HomeResponse homeResponse = HomeResponse.from(home.get());

            return ResponseBuilder.successResponse("Home found", home.get(), StatusCodeEnum.HOME1200);
        } catch (Exception e) {
            return ResponseBuilder.badRequestResponse(e.getMessage(), StatusCodeEnum.HOME0200);
        }
    }

    public ResponseEntity<ApiResponse<HomeOption>> getHomeOption(String homeId) {
        try {
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

            HomeOption homeOption = processAndCacheHomeOption(settingRequest);

            home.setHomeOption(homeOption);

            homeRepository.save(home);

            return ResponseBuilder.successResponse(successMessage, homeOption, StatusCodeEnum.HOME1200);
        } catch (Exception e) {
            return ResponseBuilder.badRequestResponse(e.getMessage(), StatusCodeEnum.HOME0200);
        }
    }

    private HomeOption processAndCacheHomeOption(SettingRequest settingRequest) {
        TempAutoOption tempAutoOption = processAndCacheOption(
                settingRequest.getTemperature(),
                TempAutoOption.class,
                SettingKey.HIGHTEMP.toString(), SettingKey.LOWTEMP.toString(), SettingKey.HIGHTEMPDEVICES.toString(), SettingKey.LOWTEMPDEVICES.toString()
        );

        HumiAutoOption humiAutoOption = processAndCacheOption(
                settingRequest.getHumidity(),
                HumiAutoOption.class,
                SettingKey.HIGHHUMI.toString(), SettingKey.LOWHUMI.toString(), SettingKey.HIGHHUMIDEVICES.toString(), SettingKey.LOWHUMIDEVICES.toString()
        );

        LightAutoOption lightAutoOption = processAndCacheOption(
                settingRequest.getLight(),
                LightAutoOption.class,
                SettingKey.HIGHLIGHT.toString(), SettingKey.LOWLIGHT.toString(), SettingKey.HIGHLIGHTDEVICES.toString(), SettingKey.LOWLIGHTDEVICES.toString()
        );

        return HomeOption.builder()
                .tempAutoOption(tempAutoOption)
                .humiAutoOption(humiAutoOption)
                .lightAutoOption(lightAutoOption)
                .build();
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
        Optional<Home> home = homeRepository.findById(homeId);
        if (home.isPresent()) {
            HomeOption homeOption = home.get().getHomeOption();
            if (homeOption != null) {
                cache.put(SettingKey.HIGHTEMP.toString(), homeOption.getTempAutoOption().getGreaterThan());
                cache.put(SettingKey.LOWTEMP.toString(), homeOption.getTempAutoOption().getLessThan());
                cache.put(SettingKey.HIGHTEMPDEVICES.toString(), homeOption.getTempAutoOption().getHighDevices());
                cache.put(SettingKey.LOWTEMPDEVICES.toString(), homeOption.getTempAutoOption().getLowDevices());

                cache.put(SettingKey.HIGHHUMI.toString(), homeOption.getHumiAutoOption().getGreaterThan());
                cache.put(SettingKey.LOWHUMI.toString(), homeOption.getHumiAutoOption().getLessThan());
                cache.put(SettingKey.HIGHHUMIDEVICES.toString(), homeOption.getHumiAutoOption().getHighDevices());
                cache.put(SettingKey.LOWHUMIDEVICES.toString(), homeOption.getHumiAutoOption().getLowDevices());

                cache.put(SettingKey.HIGHLIGHT.toString(), homeOption.getLightAutoOption().getGreaterThan());
                cache.put(SettingKey.LOWLIGHT.toString(), homeOption.getLightAutoOption().getLessThan());
                cache.put(SettingKey.HIGHLIGHTDEVICES.toString(), homeOption.getLightAutoOption().getHighDevices());
                cache.put(SettingKey.LOWLIGHTDEVICES.toString(), homeOption.getLightAutoOption().getLowDevices());
            }
        }
    }

    private <T> void processSetting(T setting) {
        // Implementation here
    }
}