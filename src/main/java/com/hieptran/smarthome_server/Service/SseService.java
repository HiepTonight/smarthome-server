package com.hieptran.smarthome_server.Service;

import com.hieptran.smarthome_server.dto.EventCodeEnum;
import com.hieptran.smarthome_server.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.async.AsyncRequestNotUsableException;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
@RequiredArgsConstructor
public class SseService {
    private final UserService userService;

    public final CopyOnWriteArrayList<SseEmitter> emitters = new CopyOnWriteArrayList<>();

    private final ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor();

    public SseEmitter addEmitter() throws AccessDeniedException {
        User user = userService.getUserFromContext();

        if (user == null) {
            throw new AccessDeniedException("User not found");

        }

        SseEmitter emitter = new SseEmitter();

        emitters.add(emitter);

        emitter.onCompletion(() -> {
            emitters.remove(emitter);
            System.out.println("Client disconnected: onCompletion");
        });

        emitter.onTimeout(() -> {
            System.out.println("Client disconnected: onTimeout");
            emitter.complete();
            emitters.remove(emitter);
        });

        emitter.onError((e) -> emitters.remove(emitter));
        return emitter;
    }

    public <T> void send(EventCodeEnum eventId , EventCodeEnum eventName ,T data) {
        SseEmitter.SseEventBuilder event = SseEmitter.event()
                .id(eventId.toString())
                .name(eventName.toString())
//                .comment(eventComment)
                .data(data);

        for (SseEmitter emitter : emitters) {
            executor.submit(() -> {
                try {
                    emitter.send(event);

                } catch (IOException e) {
                    emitter.completeWithError(e);
                    emitters.remove(emitter);
                    System.out.println("Emitter failed: " + emitter);
                }
            });
        }
    }
}

//curl -X GET "http://localhost:8080/api/v1/home/device/sse" -H "Authorization: Bearer eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJoaWVwdHJhbiIsInVzZXIiOiI2NzU5M2I4ZDdmZGY2OTVjMGE0ZDJjZGIiLCJpYXQiOjE3MzYwNzc4MzgsImV4cCI6MTczNjA3ODczOH0.EFTrmfuBWHmxQNasc5cRMY06X6OIWHiWuHjR2vOD5ptRtVsJtoQkeeVnL6jzdoS2muiPKT7Ucd-2DJcPtji0dA"