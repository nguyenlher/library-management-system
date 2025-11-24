package com.library.book_service.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.library.book_service.dto.PublisherDTO;
import com.library.book_service.entity.Publisher;
import com.library.book_service.repository.PublisherRepository;

@Service
@Transactional
public class PublisherService {

    private final PublisherRepository publisherRepository;

    public PublisherService(PublisherRepository publisherRepository) {
        this.publisherRepository = publisherRepository;
    }

    public List<PublisherDTO> getAllPublishers() {
        return publisherRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public Optional<PublisherDTO> getPublisherById(Long id) {
        return publisherRepository.findById(id)
                .map(this::convertToDTO);
    }

    public PublisherDTO createPublisher(PublisherDTO publisherDTO) {
        Publisher publisher = new Publisher();
        publisher.setName(publisherDTO.getName());
        Publisher savedPublisher = publisherRepository.save(publisher);
        return convertToDTO(savedPublisher);
    }

    public Optional<PublisherDTO> updatePublisher(Long id, PublisherDTO publisherDTO) {
        return publisherRepository.findById(id)
                .map(existingPublisher -> {
                    existingPublisher.setName(publisherDTO.getName());
                    Publisher updatedPublisher = publisherRepository.save(existingPublisher);
                    return convertToDTO(updatedPublisher);
                });
    }

    public void deletePublisher(Long id) {
        publisherRepository.deleteById(id);
    }

    private PublisherDTO convertToDTO(Publisher publisher) {
        PublisherDTO dto = new PublisherDTO();
        dto.setId(publisher.getId());
        dto.setName(publisher.getName());
        dto.setCreatedAt(publisher.getCreatedAt());
        return dto;
    }
}