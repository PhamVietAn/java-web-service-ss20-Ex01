package btvn.it211_ss20_ex1.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/employees")
public class EmployeeController {

    @GetMapping
    public ResponseEntity<List<String>> getEmployees() {
        List<String> employees = List.of(
                "Nguyen Van A - HR",
                "Tran Thi B - IT",
                "Le Van C - Finance"
        );

        return ResponseEntity.ok(employees);
    }
}