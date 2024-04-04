package com.pokemonreview.api.repository;


import com.pokemonreview.api.models.Pokemon;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;
import java.util.Optional;

// It's typically used for testing the repository layer of an application.
// when you use @DataJpaTest for your repository tests, it configures an in-memory database that is used specifically for testing purposes.
// This database is typically reset before each test method is executed,
// ensuring that the tests are isolated from each other. This behavior ensures that
// modifications to the database in one test do not affect the outcome of another test,
// making your tests more reliable and easier to maintain.
@DataJpaTest

// This replaces the application's default datasource with an embedded database (H2 in this case) for testing purposes.
// It ensures that the tests are not run against the application's actual database,
// providing a controlled environment that's suitable for unit tests.
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
public class PokemonRepositoryTests {

    @Autowired
    private PokemonRepository pokemonRepository;

    @Test
    public void PokemonRepository_SaveAll_ReturnSavedPokemon(){
        //Arrange
        Pokemon pokemon = Pokemon.builder()
                .name("Tim")
                .type("electric")
                .build();
        //Act
        Pokemon savedPokemon = pokemonRepository.save(pokemon);

        //Assert
        Assertions.assertThat(savedPokemon).isNotNull();
        Assertions.assertThat(savedPokemon.getId()).isGreaterThan(0);
    }

    @Test
    public void PokemonRepository_GetAll_ReturnMoreOnePokemon(){

        // arrange
        Pokemon pokemon1 = Pokemon.builder().name("pikachu").type("electric").build();
        Pokemon pokemon2 = Pokemon.builder().name("pikachu").type("electric").build();
        pokemonRepository.saveAll(List.of(pokemon1, pokemon2));

        // Act
        List<Pokemon> returnedPokemons = pokemonRepository.findAll();

        // Assert
        Assertions.assertThat(returnedPokemons).isNotNull();
        Assertions.assertThat(returnedPokemons).hasSize(2);

    }

    @Test
    public void PokemonRepository_FindById_ReturnPokemon(){

        // Arrange
        Pokemon pokemon = Pokemon.builder().name("pikachu").type("electric").build();
        pokemonRepository.save(pokemon);

        // Act
        Optional<Pokemon> returnedPokemon = pokemonRepository.findById(1);


        // Assert
        Assertions.assertThat(returnedPokemon).isNotNull();
        Assertions.assertThat(returnedPokemon.get().getId()).isEqualTo(pokemon.getId());

    }

    @Test
    public void PokemonRepository_FindByType_ReturnPokemonNotNull() {
        // Arrange
        Pokemon pokemon = Pokemon.builder().name("pikachu").type("electric").build();
        pokemonRepository.save(pokemon);

        // Act
        Optional<Pokemon> returnedPokemon = pokemonRepository.findByType(pokemon.getType());

        // Assert
        Assertions.assertThat(returnedPokemon).isNotNull();
        Assertions.assertThat(returnedPokemon.get().getType()).isEqualTo(pokemon.getType());


    }

    @Test
    public void PokemonRepository_UpdatePokemon_ReturnPokemon(){
        // Arrange
        Pokemon pokemon = Pokemon.builder().name("pikachu").type("electric").build();
        pokemonRepository.save(pokemon);

        // Act
        Pokemon savedPokemon = pokemonRepository.findById(pokemon.getId()).get();
        savedPokemon.setName("Bulbasaur");
        savedPokemon.setType("Grass");
        Pokemon updatedPokemon = pokemonRepository.save(savedPokemon);


        // Assert
        Assertions.assertThat(updatedPokemon).isNotNull();
        Assertions.assertThat(updatedPokemon.getName()).isEqualTo(savedPokemon.getName());
        Assertions.assertThat(updatedPokemon.getType()).isEqualTo(savedPokemon.getType());
        Assertions.assertThat(updatedPokemon).isEqualTo(savedPokemon);

    }

    @Test
    public void PokemonRepository_DeletePokemon_ReturnNull(){
        // Arrange
        Pokemon pokemon = Pokemon.builder().name("pikachu").type("electric").build();
        pokemonRepository.save(pokemon);

        // Act
        pokemonRepository.deleteById(pokemon.getId());

        Optional<Pokemon> returnedPokemon = pokemonRepository.findById(pokemon.getId());

        // Assert
        Assertions.assertThat(returnedPokemon).isEmpty();


    }



}
