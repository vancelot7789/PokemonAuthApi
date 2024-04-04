package com.pokemonreview.api.service;

import com.pokemonreview.api.dto.PokemonDto;
import com.pokemonreview.api.dto.PokemonResponse;
import com.pokemonreview.api.models.Pokemon;
import com.pokemonreview.api.repository.PokemonRepository;
import com.pokemonreview.api.service.impl.PokemonServiceImpl;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.Mockito.*;
// testing the service layer without directly interacting with the actual database.
// By mocking the repository, you simulate the interactions between the service layer and the persistence layer.
// This method allows you to focus on testing the business logic contained within the service layer,
// assuming that the repository layer (already tested in PokemonRepositoryTests) works as expected.
// The use of mocks isolates the service layer tests from the persistence layer,
// making the tests faster and focusing only on the logic you're testing.

@ExtendWith(MockitoExtension.class)
public class PokemonServiceTests {

    // This annotation is used to create a mock instance of a class or interface.
    // In the case, private PokemonRepository pokemonRepository;
    // is mocked to prevent actual database operations,
    // ensuring that tests for PokemonServiceImpl are not inadvertently dependent on the persistence layer's behavior.
    @Mock
    private PokemonRepository pokemonRepository;

    // create an instance of the class under test and automatically inject the mocked dependencies into it.
    @InjectMocks
    private PokemonServiceImpl pokemonService;



    @Test
    public void PokemonService_CreatePokemon_ReturnsPokemonDto() {

        // When your test runs, and the createPokemon method of pokemonService is called,
        // it will eventually call pokemonRepository.save(...). Normally, this would interact with your database to save the Pokemon object.
        // However, since you're using Mockito to mock the pokemonRepository, you don't want to touch the database at all.
        // Instead, you want to simulate this interaction.

        //Arrange
        Pokemon pokemon = Pokemon.builder()
                .name("Tim")
                .type("electric")
                .build();

        PokemonDto pokemonDto = PokemonDto.builder()
                .name("Tim")
                .type("electric")
                .build();

        // By setting up when(pokemonRepository.save(Mockito.any(Pokemon.class))).thenReturn(pokemon);
        // you're instructing Mockito to "pretend" that it successfully saved a Pokemon object whenever the save method
        // is called on pokemonRepository with any Pokemon object.
        // Instead of actually saving anything, it will simply return the pokemon object you specified.
        // This allows your test to proceed as if the save operation was successful,
        // without actually performing any database operations, thus isolating your test from external dependencies.

        // to simulate or mock a specific behavior when a certain condition is met during the test.
        // Essentially, you're saying, "When this specific action occurs, then return this specific result.

        when(pokemonRepository.save(Mockito.any(Pokemon.class))).thenReturn(pokemon);

        // Act
        PokemonDto savedPokemonDto = pokemonService.createPokemon(pokemonDto);

        // Assert
        Assertions.assertThat(savedPokemonDto).isNotNull();
        Assertions.assertThat(savedPokemonDto).isEqualTo(pokemonDto);
    }

    @Test
    public void PokemonService_GetAllPokemon_ReturnsResponseDto() {
        // mock(Page.class) tells Mockito to create a mock (or a simulated) object of the Page class.
        // The purpose of mocking is to simulate the behavior of real objects in a controlled way,
        // without having to rely on the actual implementation of those objects.


        // Arrange
        Page<Pokemon> pokemons = Mockito.mock(Page.class);

        // Assuming current page is 3 and page size is 20
        when(pokemons.getNumber()).thenReturn(3);
        when(pokemons.getSize()).thenReturn(20);

        when(pokemonRepository.findAll(Mockito.any(Pageable.class))).thenReturn(pokemons);

        // Act
        PokemonResponse pokemonResponse = pokemonService.getAllPokemon(3, 20);

        // Assert
        Assertions.assertThat(pokemonResponse).isNotNull();
        Assertions.assertThat(pokemonResponse.getPageNo()).isEqualTo(3);
        Assertions.assertThat(pokemonResponse.getPageSize()).isEqualTo(20);


    }
    @Test
    public void PokemonService_FindById_ReturnPokemonDto() {
        // Arrange
        int pokemonId = 1;
        Pokemon pokemon = Pokemon.builder()
                .name("Tim")
                .type("electric")
                .build();

        when(pokemonRepository.findById(pokemonId)).thenReturn(Optional.ofNullable(pokemon));

        // Act
        PokemonDto returnedPokemonDto = pokemonService.getPokemonById(1);

        // Assert
        Assertions.assertThat(returnedPokemonDto).isNotNull();
        Assertions.assertThat(returnedPokemonDto.getName()).isEqualTo(pokemon.getName());
        Assertions.assertThat(returnedPokemonDto.getType()).isEqualTo(pokemon.getType());
    }

    @Test
    public void PokemonService_UpdatePokemon_ReturnPokemonDto() {
        // Arrange
        int pokemonId = 1;
        Pokemon pokemon = Pokemon.builder()
                .name("Tim")
                .type("electric")
                .build();

        Pokemon updatedPokemon = Pokemon.builder()
                .name("Pikachu")
                .type("lightning")
                .build();

        PokemonDto pokemonDto = PokemonDto.builder()
                .name("Pikachu")
                .type("lightning")
                .build();

        when(pokemonRepository.findById(pokemonId)).thenReturn(Optional.ofNullable(pokemon));
        when(pokemonRepository.save(Mockito.any(Pokemon.class))).thenReturn(updatedPokemon);

        // Act
        PokemonDto updatedPokemonDto = pokemonService.updatePokemon(pokemonDto, 1);

        // Assert
        Assertions.assertThat(updatedPokemonDto).isNotNull();
        Assertions.assertThat(updatedPokemonDto.getName()).isEqualTo(pokemonDto.getName());
        Assertions.assertThat(updatedPokemonDto.getType()).isEqualTo(pokemonDto.getType());

    }
    @Test
    public void PokemonService_DeletePokemonById_ReturnVoid() {
        // Arrange
        int pokemonId = 1;
        Pokemon pokemon = Pokemon.builder()
                .name("Tim")
                .type("electric")
                .build();

        when(pokemonRepository.findById(pokemonId)).thenReturn(Optional.ofNullable(pokemon));
        doNothing().when(pokemonRepository).delete(pokemon);


        // Act & Assert
        assertAll(() -> pokemonService.deletePokemonId(pokemonId));
        verify(pokemonRepository).delete(pokemon);


    }

}
