package com.pokemonreview.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pokemonreview.api.controllers.PokemonController;
import com.pokemonreview.api.dto.PokemonDto;
import com.pokemonreview.api.dto.PokemonResponse;
import com.pokemonreview.api.dto.ReviewDto;
import com.pokemonreview.api.models.Pokemon;
import com.pokemonreview.api.models.Review;
import com.pokemonreview.api.security.JWTAuthenticationFilter;
import com.pokemonreview.api.security.JWTGenerator;
import com.pokemonreview.api.service.PokemonService;
import org.assertj.core.api.Assertions;
import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.Arrays;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
// @WebMvcTest sets up just the web layer and doesn't load your whole Spring application context.
// When you use @WebMvcTest(controllers = PokemonController.class),
// you're telling Spring Boot to load only the web layer context for PokemonController
@WebMvcTest(controllers = PokemonController.class)

// @AutoConfigureMockMvc is an annotation used in Spring Boot tests that automatically configures MockMvc.
// MockMvc is a powerful tool in Spring MVC that lets you test your controllers without needing to start a full HTTP server.
// It simulates HTTP requests and can be used to test your web layer with precision.
// @AutoConfigureMockMvc is more general. It can be used with other test configurations beyond just web MVC tests.
// You can use it when you want to test your controllers within a more fully realized Spring context
@AutoConfigureMockMvc(addFilters = false)
@ExtendWith(MockitoExtension.class)
public class PokemonControllerTests {

    @MockBean
    private JWTGenerator jwtGenerator;

    @MockBean
    private JWTAuthenticationFilter jwtAuthenticationFilter;

    @Autowired
    private MockMvc mockMvc;

    // @MockBean is used when you're testing within the Spring context.
    // For example, when you're testing how Spring handles web requests in your controllers,
    // you're not just testing your code; you're testing how your code interacts with Spring.
    // @MockBean helps by replacing real beans with mocks seamlessly within the Spring context,
    // ensuring your controller interacts with mocks as if they were real services.
    @MockBean
    private PokemonService pokemonService;

    @Autowired
    private ObjectMapper objectMapper;



    private Pokemon pokemon;
    private PokemonDto pokemonDto;

    private PokemonDto pokemonDto_2;
    private Review review;
    private ReviewDto reviewDto;

    @BeforeEach
    public void init() {
        this.pokemon = Pokemon.builder().name("Pikachu").type("electric").build();
        this.pokemonDto = PokemonDto.builder().name("Pikachu").type("electric").build();
        this.pokemonDto_2 = PokemonDto.builder().name("Tim").type("fire").build();
        this.review = Review.builder().title("Comment on Pikachu").content("Great").stars(3).pokemon(pokemon).build();
        this.reviewDto = ReviewDto.builder().title("Comment on Pikachu").content("Great").stars(3).build();
    }

    @Test
    public void PokemonController_CreatePokemon_ReturnCreated() throws Exception {
        // The input is invocation, which represents the method call itself,
        // including all details like what method was called, what arguments were passed, etc.
        // invocation.getArgument(0): This extracts the first argument of the method call.
        // For a method createPokemon(PokemonDto pokemonDto), it gets the PokemonDto object you passed in.


        // Dynamic Responses: It's more flexible than thenReturn, especially if the method's behavior should vary depending on the input.
        // Simulating Complex Behavior: Sometimes, the method's result depends on the exact arguments it was called with.
        // willAnswer allows you to precisely mimic such behavior.

        // Arrange
        given(pokemonService.createPokemon(Mockito.any(PokemonDto.class)))
                .willAnswer((invocationOnMock -> invocationOnMock.getArgument(0)));

        // Act
        ResultActions response = mockMvc.perform(post("/api/pokemons/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(pokemonDto)));
        // Assert
        response.andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value(pokemonDto.getName()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.type").value(pokemonDto.getType()));

    }

    @Test
    public void PokemonController_GetAllPokemon_ReturnResponseDto() throws Exception {
        // Arrange
        int pageSize = 10;
        int pageNumber = 1;
        PokemonResponse pokemonResponse = PokemonResponse.builder()
                .pageNo(pageNumber).pageSize(pageSize).content(Arrays.asList(pokemonDto, pokemonDto_2)).build();

        when(pokemonService.getAllPokemon(pageNumber, pageSize)).thenReturn(pokemonResponse);

        // Act
        ResultActions response = mockMvc.perform(get("/api/pokemons")
                .param("pageNo", String.valueOf(pageNumber))
                .param("pageSize", String.valueOf(pageSize))
                .contentType(MediaType.APPLICATION_JSON));

        // Assert
        response.andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.content.size()").value(pokemonResponse.getContent().size()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.pageNo").value(pokemonResponse.getPageNo()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.pageSize").value(pokemonResponse.getPageSize()));



    }

    @Test
    public void PokemonController_PokemonDetail_ReturnPokemonDto() throws Exception {
        // Arrange
        int pokemonId = 1;
        when(pokemonService.getPokemonById(pokemonId)).thenReturn(pokemonDto);

        // Act
        ResultActions response = mockMvc.perform(get("/api/pokemons/" + pokemonId)
                .contentType(MediaType.APPLICATION_JSON));
        // Arrange
        response.andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value(pokemonDto.getName()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.type").value(pokemonDto.getType()));
    }

    @Test
    public void PokemonController_UpdatePokemon_ReturnPokemonDto() throws Exception {
        // Arrange
        int pokemonId = 1;
        // assume pokemonId belongs to pokemonDto
        // now we update it into pokemonDto_2
        when(pokemonService.updatePokemon(pokemonDto_2, pokemonId)).thenReturn(pokemonDto_2);

        // Act
        ResultActions response = mockMvc.perform(put("/api/pokemons/" + pokemonId + "/update")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(pokemonDto_2)));
        // Assert
        response.andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value(pokemonDto_2.getName()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.type").value(pokemonDto_2.getType()));
    }

    @Test
    public void PokemonController_DeletePokemon_ReturnString() throws Exception {
        // Arrange
        int pokemonId = 1;
        doNothing().when(pokemonService).deletePokemonId(pokemonId);

        // Act
        ResultActions response = mockMvc.perform(delete("/api/pokemons/" + pokemonId + "/delete")
                .contentType(MediaType.APPLICATION_JSON));

        // Assert
        response.andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string("Pokemon delete"));
    }
}
