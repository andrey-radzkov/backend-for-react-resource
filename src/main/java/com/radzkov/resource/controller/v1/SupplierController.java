package com.radzkov.resource.controller.v1;

import com.google.common.collect.Maps;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * @author Radzkov Andrey
 */
@RestController
public class SupplierController {

    private final CopyOnWriteArrayList<SseEmitter> emitters = new CopyOnWriteArrayList<>();

    @GetMapping("/get-supplier/{id}")
    public Map<String, String> getSupplier(@PathVariable("id") int id) {
        Map<String, String> supplier = new HashMap<>();
        supplier.put("companyName", "name" + id);
        supplier.put("email", "email@user" + id + ".com");
        return supplier;
    }

    @GetMapping("/get-suppliers")
    public Map<String, Object> getSuppliers() {
        List<Map<String, String>> suppliers = IntStream.range(0, 3).mapToObj(id -> {
            Map<String, String> supplier = new HashMap<>();
            supplier.put("id", Integer.toString(id));
            supplier.put("companyName", "name" + id);
            supplier.put("email", "email@user" + id + ".com");
            return supplier;
        }).collect(Collectors.toList());
        HashMap<String, Object> map = Maps.newHashMap();
        map.put("content", suppliers);
        return map;

    }


    @GetMapping("/server-side-event")
    public SseEmitter serverSideEvent() {
        SseEmitter emitter = new SseEmitter();
        this.emitters.add(emitter);

        emitter.onCompletion(() -> this.emitters.remove(emitter));
        emitter.onTimeout(() -> this.emitters.remove(emitter));

        return emitter;
    }

    @Scheduled(fixedDelay = 5000)
    public void scheduledMessage() {
        List<SseEmitter> deadEmitters = new ArrayList<>();
        emitters.forEach(emitter -> {
            try {
                emitter.send("pushed message from server");
            } catch (Exception e) {
                deadEmitters.add(emitter);
            }
        });

        emitters.remove(deadEmitters);
    }

}
