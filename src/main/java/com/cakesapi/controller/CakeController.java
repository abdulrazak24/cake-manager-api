package com.cakesapi.controller;

import com.cakesapi.model.Cake;
import com.cakesapi.service.CakeService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/cakes")
@Slf4j
public class CakeController {

        @Autowired
        private CakeService cakeService;

        @GetMapping
        @PreAuthorize("hasRole('USER')")
        @Operation(summary = "Get all cakes from database")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Cake details are:", content = {
                                        @Content(mediaType = "application/json") })
        })
        public ResponseEntity<List<Cake>> getAllCakes(Authentication authentication) {
                log.info("Get All Created Cakes from database");
                List<Cake> cakes = cakeService.getAllCakes();
                return new ResponseEntity<>(cakes, HttpStatus.OK);
        }

        @GetMapping("/{id}")
        @PreAuthorize("hasRole('USER')")
        @Operation(summary = "Gets cake details from database")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Cake details are", content = {
                                        @Content(mediaType = "application/json") }),
                        @ApiResponse(responseCode = "404", description = "Cake not found in database", content = {
                                        @Content(mediaType = "application/json") })
        })
        public ResponseEntity<Cake> getCakeById(@PathVariable Long id) {
                log.info("Get Cake Details for Id: " + id);
                Cake cake = cakeService.getCakeById(id);
                return new ResponseEntity<>(cake, HttpStatus.OK);
        }

        @PostMapping("/createCake")
        @PreAuthorize("hasRole('ADMIN')")
        @Operation(summary = "Adds a cake to database")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "201", description = "Cake added", content = {
                                        @Content(mediaType = "application/json") }),
                        @ApiResponse(responseCode = "403", description = "Only Admins can add cake", content = {
                                        @Content(mediaType = "application/json") })
        })
        public ResponseEntity<Cake> createCake(@RequestBody Cake cake) {
                log.info("Adding a cake" + cake.getFlavour());
                Cake createdCake = cakeService.createCake(cake);
                return new ResponseEntity<>(createdCake, HttpStatus.CREATED);
        }

        @PostMapping("/createCakesFromJsonArray")
        @PreAuthorize("hasRole('ADMIN')")
        @Operation(summary = "Adds cakes to the database from JSON array")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "201", description = "Cakes added", content = {
                                        @Content(mediaType = "application/json") }),
                        @ApiResponse(responseCode = "403", description = "Only Admins can add cakes", content = {
                                        @Content(mediaType = "application/json") })
        })
        public ResponseEntity<List<Cake>> createCakesFromJsonArray(@RequestBody JsonNode jsonArray) {
                try {
                        ObjectMapper objectMapper = new ObjectMapper();
                        List<Cake> cakes = Arrays.asList(objectMapper.treeToValue(jsonArray, Cake[].class));
                        log.info("Adding cakes from JSON array");
                        List<Cake> createdCakes = cakeService.createCakes(cakes);
                        return new ResponseEntity<>(createdCakes, HttpStatus.CREATED);
                } catch (Exception e) {
                        log.error("Error creating cakes from JSON array", e);
                        return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
                }
        }

        @PutMapping("/{id}")
        @PreAuthorize("hasRole('ADMIN')")
        @Operation(summary = "Updates cake in database")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Cake updated", content = {
                                        @Content(mediaType = "application/json") }),
                        @ApiResponse(responseCode = "404", description = "Cake not found", content = {
                                        @Content(mediaType = "application/json") }),
                        @ApiResponse(responseCode = "403", description = "Only Admins can update cake", content = {
                                        @Content(mediaType = "application/json") })
        })
        public ResponseEntity<Cake> updateCake(@PathVariable Long id, @RequestBody Cake cake) {
                log.info("Updating Cake: " + id);
                Cake updatedCake = cakeService.updateCake(id, cake);
                return new ResponseEntity<>(updatedCake, HttpStatus.OK);
        }

        @DeleteMapping("/{id}")
        @PreAuthorize("hasRole('ADMIN')")
        @Operation(summary = "Deletes cake from database")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "204", description = "Cake deleted", content = {
                                        @Content(mediaType = "application/json") }),
                        @ApiResponse(responseCode = "404", description = "Cake not found in database", content = {
                                        @Content(mediaType = "application/json") }),
                        @ApiResponse(responseCode = "403", description = "Only Admins can delete cake", content = {
                                        @Content(mediaType = "application/json") })
        })
        public ResponseEntity<Void> deleteCake(@PathVariable Long id) {
                log.info("Deleting Cake: " + id);
                cakeService.deleteCake(id);
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
}
