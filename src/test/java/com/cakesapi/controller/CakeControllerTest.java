package com.cakesapi.controller;

import com.cakesapi.service.CakeService;
import com.cakesapi.model.Cake;
import com.cakesapi.exception.CakeNotFoundException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.springframework.http.ResponseEntity;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Arrays;
import java.util.List;

@WebMvcTest
public class CakeControllerTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;

    @MockBean
    private CakeService cakeService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .apply(springSecurity()).build();
    }

    @Test
    @WithMockUser
    void getAllCakes() throws Exception {
        List<Cake> mockCakes = Arrays.asList(
                new Cake(1L, "It is a chocolate cake", "Chocolate", "Buttercream"),
                new Cake(2L, "It is Vanilla cake", "Whipped Cream", "Whipped cream"));

        when(cakeService.getAllCakes()).thenReturn(mockCakes);

        mockMvc.perform(MockMvcRequestBuilders.get("/cakes"))
                .andExpect(status().isOk());

        verify(cakeService).getAllCakes();
    }

    @Test
    @WithMockUser
    void getCakes_returnsOk() throws Exception {
        Cake cake1 = createTestCake();

        Cake cake2 = new Cake();
        cake2.setId(2L);
        cake2.setFlavour("Strawberry Cake");
        cake2.setIcing("cream");

        when(cakeService.getAllCakes()).thenReturn(Arrays.asList(cake1, cake2));

        mockMvc.perform(MockMvcRequestBuilders.get("/cakes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].flavour").value(cake1.getFlavour()))
                .andExpect(jsonPath("$[0].icing").value(cake1.getIcing()))
                .andExpect(jsonPath("$[1].flavour").value(cake2.getFlavour()))
                .andExpect(jsonPath("$[1].icing").value(cake2.getIcing()));

        verify(cakeService).getAllCakes();
    }

    @Test
    @WithMockUser
    void getCake_returnsOk() throws Exception {
        Cake cake = createTestCake();

        when(cakeService.getCakeById(1L)).thenReturn(cake);

        mockMvc.perform(MockMvcRequestBuilders.get("/cakes/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.flavour").value(cake.getFlavour()))
                .andExpect(jsonPath("$.icing").value(cake.getIcing()));

        verify(cakeService).getCakeById(1L);
    }

    @Test
    @WithMockUser
    void getCake_returnsNotFound() throws Exception {
        when(cakeService.getCakeById(1L)).thenThrow(new CakeNotFoundException("Cake not found with id 1"));

        mockMvc.perform(MockMvcRequestBuilders.get("/cakes/1"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Cake not found with id 1"));

        verify(cakeService, times(1)).getCakeById(1L);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void createCake_returnsOk() throws Exception {
        Cake cake = createTestCake();

        when(cakeService.createCake(any(Cake.class))).thenReturn(cake);

        mockMvc.perform(MockMvcRequestBuilders.post("/cakes/createCake").with(csrf())
                .content(objectMapper.writeValueAsString(cake))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.flavour").value(cake.getFlavour()))
                .andExpect(jsonPath("$.icing").value(cake.getIcing()));

        verify(cakeService).createCake(any(Cake.class));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void createCakesFromJsonArray_returnsOk() throws Exception {
        String jsonArrayString = "[{\"flavour\":\"Chocolate Cake\",\"icing\":\"A chocolate cream\"},{\"flavour\":\"vanilla\",\"icing\":\"vanilla\"}]";
        JsonNode jsonArray = new ObjectMapper().readTree(jsonArrayString);

        Cake expectedCake1 = createTestCake();
        Cake expectedCake2 = new Cake();
        expectedCake2.setId(2L);
        expectedCake2.setFlavour("vanilla");
        expectedCake2.setIcing("vanilla");

        List<Cake> expectedCakes = Arrays.asList(expectedCake1, expectedCake2);

        when(cakeService.createCakes(anyList())).thenReturn(expectedCakes);

        mockMvc.perform(MockMvcRequestBuilders.post("/cakes/createCakesFromJsonArray").with(csrf())
                .content(objectMapper.writeValueAsString(jsonArray))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$[0].flavour").value(expectedCake1.getFlavour()))
                .andExpect(jsonPath("$[0].icing").value(expectedCake1.getIcing()))
                .andExpect(jsonPath("$[1].flavour").value(expectedCake2.getFlavour()))
                .andExpect(jsonPath("$[1].icing").value(expectedCake2.getIcing()));

        verify(cakeService, times(1)).createCakes(anyList());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void updateCake_returnsOk() throws Exception {
        Cake cake = createTestCake();

        when(cakeService.updateCake(eq(1L), any(Cake.class))).thenReturn(cake);

        mockMvc.perform(MockMvcRequestBuilders.put("/cakes/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(cake))
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(cake.getId()))
                .andExpect(jsonPath("$.flavour").value(cake.getFlavour()))
                .andExpect(jsonPath("$.icing").value(cake.getIcing()));

        ArgumentCaptor<Cake> argumentCaptor = ArgumentCaptor.forClass(Cake.class);
        verify(cakeService, times(1)).updateCake(eq(1L), argumentCaptor.capture());
        assertEquals(1L, argumentCaptor.getValue().getId());
        assertEquals(cake.getFlavour(), argumentCaptor.getValue().getFlavour());
        assertEquals(cake.getIcing(), argumentCaptor.getValue().getIcing());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void updateCake_returnsNotFound() throws Exception {
        Cake cake = createTestCake();
        when(cakeService.updateCake(eq(1L), any(Cake.class)))
                .thenThrow(new CakeNotFoundException("Cake not found with id 1"));

        mockMvc.perform(MockMvcRequestBuilders.put("/cakes/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(cake))
                .with(csrf()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Cake not found with id 1"));

        verify(cakeService, times(0)).deleteCake(1L);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteCake_returnsOk() throws Exception {
        doNothing().when(cakeService).deleteCake(1L);

        mockMvc.perform(MockMvcRequestBuilders.delete("/cakes/1")
                .with(csrf()))
                .andExpect(status().isNoContent());

        verify(cakeService, times(1)).deleteCake(1L);
    }

    @Test
    @WithMockUser
    void deleteCake_returnsForbidden() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders.delete("/cakes/1"))
                .andExpect(status().isForbidden());

        verify(cakeService, times(0)).deleteCake(1L);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteCake_returnsNotFound() throws Exception {
        doThrow(new CakeNotFoundException("Cake not found with id 1")).when(cakeService).deleteCake(1L);

        mockMvc.perform(MockMvcRequestBuilders.delete("/cakes/1").with(csrf()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Cake not found with id 1"));

        verify(cakeService, times(1)).deleteCake(1L);
    }

    private Cake createTestCake() {
        Cake cake = new Cake();
        cake.setId(1L);
        cake.setFlavour("Chocolate Cake");
        cake.setIcing("A chocolate cream");

        return cake;
    }
}
