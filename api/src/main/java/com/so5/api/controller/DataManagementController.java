package com.so5.api.controller;

import com.so5.api.service.DataManagementService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/data")
public class DataManagementController {

    private final DataManagementService dataManagementService;

    @Operation(summary = "Generate new products")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Generated")
    })
    @PostMapping("/generate")
    public void generateData() {

        dataManagementService.generateData();
    }

    @Operation(summary = "Delete all products")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Deleted")
    })
    @DeleteMapping("/reset")
    public void resetData() {

        dataManagementService.resetData();
    }
}
