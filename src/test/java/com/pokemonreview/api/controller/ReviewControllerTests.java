package com.pokemonreview.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pokemonreview.api.controllers.ReviewController;
import com.pokemonreview.api.dto.PokemonDto;
import com.pokemonreview.api.dto.ReviewDto;
import com.pokemonreview.api.models.Pokemon;
import com.pokemonreview.api.models.Review;
import com.pokemonreview.api.security.JWTAuthenticationFilter;
import com.pokemonreview.api.security.JWTGenerator;
import com.pokemonreview.api.service.ReviewService;
import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.Arrays;
import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@WebMvcTest(controllers = ReviewController.class)
@AutoConfigureMockMvc(addFilters = false)
@ExtendWith(MockitoExtension.class)
public class ReviewControllerTests {

    @MockBean
    private JWTGenerator jwtGenerator;

    @MockBean
    private JWTAuthenticationFilter jwtAuthenticationFilter;
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ReviewService reviewService;

    private Pokemon pokemon;
    private PokemonDto pokemonDto;
    private Review review;
    private Review review_2;
    private ReviewDto reviewDto;
    private ReviewDto reviewDto_2;

    @BeforeEach
    public void init() {
        this.pokemon = Pokemon.builder().name("Pikachu").type("electric").build();
        this.pokemonDto = PokemonDto.builder().name("Pikachu").type("electric").build();
        this.review = Review.builder().title("Comment on Pikachu").content("Great").stars(3).pokemon(pokemon).build();
        this.review_2 = Review.builder().title("Hello Pikachu").content("I love pikachu").stars(5).pokemon(pokemon).build();
        this.reviewDto = ReviewDto.builder().title("Comment on Pikachu").content("Great").stars(3).build();
        this.reviewDto_2 = ReviewDto.builder().title("Hello Pikachu").content("I love pikachu").stars(5).build();

    }

    @Test
    public void ReviewController_GetReviewsByPokemonId_ReturnReviewDto() throws Exception {
        // Arrange
        int pokemonId = 1;
        List<Review> reviewList = Arrays.asList(review, review_2);
        given(reviewService.getReviewsByPokemonId(pokemonId)).willAnswer(invocation -> reviewList);

        // Act
        ResultActions response = mockMvc.perform(get("/api/pokemons/" + pokemonId +"/reviews")
                .contentType(MediaType.APPLICATION_JSON));

        System.out.println(response.andReturn().getResponse().getContentAsString());
        // Assert
        response.andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.[0].title").value(reviewDto.getTitle()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.[1].title").value(reviewDto_2.getTitle()));

    }

    @Test
    public void ReviewController_UpdateReview_ReturnReviewDto() throws Exception {
        // Arrange
        int pokemonId = 1;
        int reviewId = 10;
        when(reviewService.updateReview(pokemonId, reviewId, reviewDto_2)).thenReturn(reviewDto_2);

        // Act
        ResultActions response =  mockMvc.perform(put("/api/pokemons/"+ pokemonId + "/reviews/" + reviewId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(reviewDto_2)));

        // Assert
        response.andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.title").value(reviewDto_2.getTitle()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.content").value(reviewDto_2.getContent()));


    }


    @Test
    public void ReviewController_CreateReview_ReturnReviewDto() throws Exception {
        // Arrange
        int pokemonId = 1;
        when(reviewService.createReview(pokemonId, reviewDto)).thenReturn(reviewDto);

        // Act
        ResultActions response = mockMvc.perform(post("/api/pokemons/" + pokemonId + "/reviews")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(reviewDto)));
        // Assert
        response.andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.title").value(reviewDto.getTitle()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.content").value(reviewDto.getContent()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.stars").value(reviewDto.getStars()));
    }

    @Test
    public void ReviewController_GetReviewId_ReturnReviewDto() throws Exception {
        // Arrange
        int pokemonId = 1;
        int reviewId = 1;
        when(reviewService.getReviewById(reviewId, pokemonId)).thenReturn(reviewDto);

        // Act
        ResultActions response = mockMvc.perform(get("/api/pokemons/"+ pokemonId + "/reviews/" + reviewId)
                .contentType(MediaType.APPLICATION_JSON));

        System.out.println(response.andReturn().getResponse().getContentAsString());
        // Assert
        response.andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.title").value(reviewDto.getTitle()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.stars").value(reviewDto.getStars()));

    }

    @Test
    public void ReviewController_DeleteReview_ReturnOk() throws Exception {
        // Arrange
        int pokemonId = 1;
        int reviewId = 10;
        doNothing().when(reviewService).deleteReview(pokemonId, reviewId);

        // Act
        ResultActions response = mockMvc.perform(delete("/api/pokemons/"+ pokemonId + "/reviews/" + reviewId)
                .contentType(MediaType.APPLICATION_JSON));
        // Assert
        response.andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string("Review deleted successfully"));

    }




}
