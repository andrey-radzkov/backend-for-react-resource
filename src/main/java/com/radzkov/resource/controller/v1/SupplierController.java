package com.radzkov.resource.controller.v1;

import com.google.common.collect.Maps;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * @author Radzkov Andrey
 */
@RestController
public class SupplierController {

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

}
