package com.pokemonreview.api.service;

import com.pokemonreview.api.dto.PokemonDto;
import com.pokemonreview.api.dto.ReviewDto;
import com.pokemonreview.api.exceptions.PokemonNotFoundException;
import com.pokemonreview.api.exceptions.ReviewNotFoundException;
import com.pokemonreview.api.models.Pokemon;
import com.pokemonreview.api.models.Review;
import com.pokemonreview.api.repository.PokemonRepository;
import com.pokemonreview.api.repository.ReviewRepository;
import com.pokemonreview.api.service.impl.PokemonServiceImpl;
import com.pokemonreview.api.service.impl.ReviewServiceImpl;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ReviewServiceTests {

    @Mock
    private PokemonRepository pokemonRepository;

    @Mock
    private ReviewRepository reviewRepository;

    @InjectMocks
    private ReviewServiceImpl reviewService;

    private Pokemon pokemon;
    private PokemonDto pokemonDto;
    private Review review;
    private ReviewDto reviewDto;

    @BeforeEach
    public void init() {
        this.pokemon = Pokemon.builder().name("Pikachu").type("electric").build();
        this.pokemonDto = PokemonDto.builder().name("Pikachu").type("electric").build();
        this.review = Review.builder().title("Comment on Pikachu").content("Great").stars(3).pokemon(pokemon).build();
        this.reviewDto = ReviewDto.builder().title("Comment on Pikachu").content("Great").stars(3).build();
    }

    @Test
    public void ReviewService_CreateReview_ReturnsReviewDto() {

        // Arrange
        int pokemonId = 1;
        when(pokemonRepository.findById(pokemonId)).thenReturn(Optional.ofNullable(this.pokemon));
        when(reviewRepository.save(Mockito.any(Review.class))).thenReturn(this.review);

        // Act
        ReviewDto createdReviewDto = reviewService.createReview(pokemonId, this.reviewDto);

        // Assert
        Assertions.assertThat(createdReviewDto).isNotNull();
        Assertions.assertThat(createdReviewDto.getTitle()).isEqualTo(this.reviewDto.getTitle());
        Assertions.assertThat(createdReviewDto.getContent()).isEqualTo(this.reviewDto.getContent());
        Assertions.assertThat(createdReviewDto.getStars()).isEqualTo(this.reviewDto.getStars());

    }

    @Test
    public void createReview_ThrowsPokemonNotFoundException_IfPokemonNotFound() {
        // Arrange
        int pokemonId = 999; // Assume this ID does not exist in the database
        when(pokemonRepository.findById(pokemonId)).thenReturn(Optional.empty());

        // Act & Assert
        // You tell assertThrows to expect a PokemonNotFoundException.
        // You then pass a lambda expression that calls createReview.
        // This lambda expression is essentially the "Act" part of your test.
        // assertThrows executes the lambda expression. If the PokemonNotFoundException is thrown as expected,
        // assertThrows considers the test to have passed. If no exception is thrown,
        // or if an exception of a different type is thrown, assertThrows considers the test to have failed.
        Exception exception = assertThrows(PokemonNotFoundException.class, () -> {
            reviewService.createReview(pokemonId, this.reviewDto);
        });

        assertEquals("Pokemon with associated review not found", exception.getMessage());
    }



    @Test
    public void ReviewService_GetReviewsByPokemonId_ReturnReviewDto() {


    }

    @Test
    public void ReviewService_GetReviewById_ReturnReviewDto() {

        // Arrange
        int pokemonId = 1;
        int reviewId = 10;
        when(pokemonRepository.findById(pokemonId)).thenReturn(Optional.ofNullable(this.pokemon));
        when(reviewRepository.findById(reviewId)).thenReturn(Optional.ofNullable(this.review));

        // Act
        ReviewDto returnReviewDto = reviewService.getReviewById(reviewId, pokemonId);

        // Assert
        Assertions.assertThat(returnReviewDto).isNotNull();
        Assertions.assertThat(returnReviewDto.getStars()).isEqualTo(this.reviewDto.getStars());
        Assertions.assertThat(returnReviewDto.getContent()).isEqualTo(this.reviewDto.getContent());
        Assertions.assertThat(returnReviewDto.getTitle()).isEqualTo(this.reviewDto.getTitle());

    }

    @Test
    public void ReviewService_GetReviewById_ThrowsPokemonNotFoundException_IfPokemonNotFound() {
        // Arrange
        int pokemonId = 1;
        int reviewId = 10;
        when(pokemonRepository.findById(pokemonId)).thenReturn(Optional.empty());

        // Act & Assert
        Exception exception = assertThrows(PokemonNotFoundException.class, () -> reviewService.getReviewById(reviewId, pokemonId));
        Assertions.assertThat(exception.getMessage()).isEqualTo("Pokemon with associated review not found");
    }
    @Test
    public void ReviewService_getReviewById_ThrowsReviewNotFoundException_IfReviewDoesNotBelongToPokemon(){
        // Arrange
        int pokemonId = 1;
        int reviewId = 10;
        when(pokemonRepository.findById(pokemonId)).thenReturn(Optional.ofNullable(this.pokemon));
        when(reviewRepository.findById(reviewId)).thenReturn(Optional.empty());

        // Act & Assert
        Exception exception = assertThrows(ReviewNotFoundException.class, () -> reviewService.getReviewById(reviewId, pokemonId));
        Assertions.assertThat(exception.getMessage()).isEqualTo("Review with associate pokemon not found");
    }

    @Test
    public void getReviewById_ThrowsReviewNotFoundException_IfReviewDoesNotBelongToProvidedPokemon() {
        // Arrange

        int reviewId = 10; // ID for the Review you're retrieving
        int differentPokemonId = 2; // Different ID to simulate mismatch

        Pokemon differentPokemon = Pokemon.builder().id(differentPokemonId).name("Charizard").type("Fire").build();

        when(pokemonRepository.findById(differentPokemonId)).thenReturn(Optional.ofNullable(differentPokemon));
        when(reviewRepository.findById(reviewId)).thenReturn(Optional.ofNullable(this.review));

        // Act & Assert
        Exception exception = assertThrows(ReviewNotFoundException.class, () -> reviewService.getReviewById(reviewId, differentPokemonId));
        Assertions.assertThat(exception.getMessage()).isEqualTo("This review does not belong to a pokemon");
    }



    @Test
    public void ReviewService_UpdateReview_ReturnReviewDto() {
        // Arrange
        int pokemonId = 1;
        int reviewId = 10;


        when(pokemonRepository.findById(pokemonId)).thenReturn(Optional.ofNullable(this.pokemon));
        when(reviewRepository.findById(reviewId)).thenReturn(Optional.ofNullable(this.review));

        this.review.setContent("so so not bad");
        this.review.setStars(2);

        when(reviewRepository.save(this.review)).thenReturn(this.review);

        ReviewDto passReviewDto = ReviewDto.builder().stars(2).title("Comment on Pikachu").content("so so not bad").build();

        // Act
        ReviewDto updatedReviewDto = reviewService.updateReview(pokemonId, reviewId, passReviewDto);

        // Assert

        Assertions.assertThat(updatedReviewDto).isNotNull();
        Assertions.assertThat(updatedReviewDto.getTitle()).isEqualTo(passReviewDto.getTitle());
        Assertions.assertThat(updatedReviewDto.getContent()).isEqualTo(passReviewDto.getContent());
        Assertions.assertThat(updatedReviewDto.getStars()).isEqualTo(passReviewDto.getStars());
    }

    @Test
    public void ReviewService_DeletePokemonById_ReturnVoid() {

        // Arrange
        int pokemonId = 1;
        int reviewId = 10;

        when(pokemonRepository.findById(pokemonId)).thenReturn(Optional.ofNullable(this.pokemon));
        when(reviewRepository.findById(reviewId)).thenReturn(Optional.ofNullable(this.review));
        doNothing().when(reviewRepository).delete(this.review);



        // Act & Assert
        assertAll(() -> reviewService.deleteReview(pokemonId, reviewId));
        verify(reviewRepository).delete(this.review);


    }


}
