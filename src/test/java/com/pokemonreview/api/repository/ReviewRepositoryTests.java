package com.pokemonreview.api.repository;

import com.pokemonreview.api.models.Pokemon;
import com.pokemonreview.api.models.Review;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;
import java.util.Optional;


@DataJpaTest
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
public class ReviewRepositoryTests {
    @Autowired
    private ReviewRepository reviewRepository;

    @Test
    public void ReviewRepository_SaveAll_ReturnSavedReview(){
        // Arrange
        Pokemon pokemon = Pokemon.builder()
                .name("Tim")
                .type("electric")
                .build();

        Review review = Review.builder()
                .title("Comment on Tim")
                .content("Great")
                .stars(3)
                .pokemon(pokemon)
                .build();
        // Act
        Review savedReview = reviewRepository.save(review);

        // Assert
        Assertions.assertThat(savedReview).isNotNull();
        Assertions.assertThat(savedReview.getStars()).isEqualTo(review.getStars());
        Assertions.assertThat(savedReview.getPokemon()).isEqualTo(pokemon);
    }

    @Test
    public void ReviewRepository_GetAll_ReturnsMoreThenOneReview() {
        Review review = Review.builder().title("title").content("content").stars(5).build();
        Review review2 = Review.builder().title("title").content("content").stars(5).build();

        reviewRepository.save(review);
        reviewRepository.save(review2);

        List<Review> reviewList = reviewRepository.findAll();

        Assertions.assertThat(reviewList).isNotNull();
        Assertions.assertThat(reviewList.size()).isEqualTo(2);
    }

    @Test
    public void ReviewRepository_FindById_ReturnsSavedReview() {
        Review review = Review.builder().title("title").content("content").stars(5).build();

        reviewRepository.save(review);

        Review reviewReturn = reviewRepository.findById(review.getId()).get();

        Assertions.assertThat(reviewReturn).isNotNull();
    }

    @Test
    public void ReviewRepository_UpdateReview_ReturnReview() {
        // Arrange
        Pokemon pokemon = Pokemon.builder()
                .name("Tim")
                .type("electric")
                .build();

        Review review = Review.builder()
                .title("Comment on Tim")
                .content("Great")
                .stars(3)
                .pokemon(pokemon)
                .build();

        Review savedReview = reviewRepository.save(review);
        // Act
        savedReview.setTitle("pikachu comment");
        savedReview.setContent("cute");
        Pokemon newPokemon = Pokemon.builder().name("pikachu").type("electric").build();
        savedReview.setPokemon(newPokemon);

        Review updatedReview = reviewRepository.save(savedReview);

        // Assert
        Assertions.assertThat(updatedReview).isNotNull();
        Assertions.assertThat(updatedReview.getTitle()).isEqualTo(savedReview.getTitle());
        Assertions.assertThat(updatedReview.getPokemon()).isEqualTo(savedReview.getPokemon());

    }

    @Test
    public void ReviewRepository_ReviewDelete_ReturnReviewIsEmpty() {
        Review review = Review.builder().title("title").content("content").stars(5).build();

        reviewRepository.save(review);

        reviewRepository.deleteById(review.getId());
        Optional<Review> reviewReturn = reviewRepository.findById(review.getId());

        Assertions.assertThat(reviewReturn).isEmpty();
    }



}
