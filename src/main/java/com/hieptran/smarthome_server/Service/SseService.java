package com.hieptran.smarthome_server.Service;

import com.hieptran.smarthome_server.dto.EventCodeEnum;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
@RequiredArgsConstructor
public class SseService {
    private final UserService userService;

    private final ConcurrentHashMap<String, CopyOnWriteArrayList<SseEmitter>> emitters = new ConcurrentHashMap<>();

    private final ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor();

    public SseEmitter addEmitter(String homePodId) throws AccessDeniedException {
//        User user = userService.getUserFromContext();
//
//        if (user == null) {
//            throw new AccessDeniedException("User not found");
//
//        }
        if (homePodId == null || "undefined".equals(homePodId)) {
            throw new AccessDeniedException("HomePod not found");
        }

        SseEmitter emitter = new SseEmitter(100000L);

        emitters.computeIfAbsent(homePodId, k -> new CopyOnWriteArrayList<>());

        CopyOnWriteArrayList<SseEmitter> emitterList = emitters.get(homePodId);
        synchronized (emitterList) {
            if (emitterList.size() < 5) {
                emitterList.add(emitter);
            } else {
                throw new IllegalStateException("Maximum number of emitters reached for homePodId: " + homePodId);
            }
        }

        send(emitter, EventCodeEnum.CLIENT_CONNECTED, EventCodeEnum.CLIENT_CONNECTED, "Connected successfully to SSE");

        emitter.onCompletion(() -> {
            emitterList.remove(emitter);
            System.out.println("Client disconnected: onCompletion");
        });

        emitter.onTimeout(() -> {
            System.out.println("Client disconnected: onTimeout");
            emitter.complete();
            emitterList.remove(emitter);
        });

        emitter.onError((e) -> emitterList.remove(emitter));
        return emitter;
    }

    public boolean isEmittersEmpty() {
        return emitters.isEmpty();
    }

    public <T> void send(SseEmitter emitter, EventCodeEnum eventId , EventCodeEnum eventName, T data) {
        SseEmitter.SseEventBuilder event = SseEmitter.event()
                .id(eventId.toString())
                .name(eventName.toString())
                .data(data);

        try {
            emitter.send(event);
        } catch (IOException e) {
            emitter.completeWithError(e);
            System.out.println("Emitter failed: " + emitter);
        }
    }

    public <T> void send(String homePodId, EventCodeEnum eventId , EventCodeEnum eventName ,T data) {
        SseEmitter.SseEventBuilder event = SseEmitter.event()
                .id(eventId.toString())
                .name(eventName.toString())
//                .comment(eventComment)
                .data(data);

        CopyOnWriteArrayList<SseEmitter> emitterList = emitters.get(homePodId);

        if (emitterList != null) {
            for (SseEmitter emitter : emitterList) {
                try {
                    emitter.send(event);
                } catch (IOException e) {
                    emitter.completeWithError(e);
                    emitterList.remove(emitter);
                    System.out.println("Emitter failed: " + emitter);
                }
            }
        }

//        if (emitterList != null) {
//            for (SseEmitter emitter : emitterList) {
//                executor.submit(() -> {
//                    try {
//                        emitter.send(event);
//                    } catch (IOException e) {
//                        emitter.completeWithError(e);
//                        emitterList.remove(emitter);
//                        System.out.println("Emitter failed: " + emitter);
//                    }
//                });
//            }
//        }

    }
}

//curl -X GET "http://localhost:8080/api/v1/home/device/sse" -H "Authorization: Bearer "