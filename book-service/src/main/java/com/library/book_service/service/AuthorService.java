package com.library.book_service.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.library.book_service.dto.AuthorDTO;
import com.library.book_service.entity.Author;
import com.library.book_service.repository.AuthorRepository;

@Service
@Transactional
public class AuthorService {

    private final AuthorRepository authorRepository;

    public AuthorService(AuthorRepository authorRepository) {
        this.authorRepository = authorRepository;
    }

    public List<AuthorDTO> getAllAuthors() {
        return authorRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public Optional<AuthorDTO> getAuthorById(Long id) {
        return authorRepository.findById(id)
                .map(this::convertToDTO);
    }

    public List<AuthorDTO> searchAuthorsByName(String name) {
        return authorRepository.findByNameContainingIgnoreCase(name).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public Optional<AuthorDTO> updateAuthor(Long id, AuthorDTO authorDTO) {
        return authorRepository.findById(id)
                .map(existingAuthor -> {
                    existingAuthor.setName(authorDTO.getName());
                    existingAuthor.setBiography(authorDTO.getBiography());
                    existingAuthor.setBirthDate(authorDTO.getBirthDate());
                    existingAuthor.setDeathDate(authorDTO.getDeathDate());

                    Author updatedAuthor = authorRepository.save(existingAuthor);
                    return convertToDTO(updatedAuthor);
                });
    }

    public AuthorDTO createAuthor(AuthorDTO authorDTO) {
        Author author = new Author();
        author.setName(authorDTO.getName());
        author.setBiography(authorDTO.getBiography());
        author.setBirthDate(authorDTO.getBirthDate());
        author.setDeathDate(authorDTO.getDeathDate());

        Author savedAuthor = authorRepository.save(author);
        return convertToDTO(savedAuthor);
    }

    public void deleteAuthor(Long id) {
        authorRepository.deleteById(id);
    }

    private AuthorDTO convertToDTO(Author author) {
        AuthorDTO dto = new AuthorDTO();
        dto.setId(author.getId());
        dto.setName(author.getName());
        dto.setBiography(author.getBiography());
        dto.setBirthDate(author.getBirthDate());
        dto.setDeathDate(author.getDeathDate());
        dto.setBookCount(author.getBooks() != null ? author.getBooks().size() : 0);
        return dto;
    }
}