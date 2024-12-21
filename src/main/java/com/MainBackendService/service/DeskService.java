package com.MainBackendService.service;

import com.MainBackendService.dto.CreateDeskDto;
import com.MainBackendService.dto.DeskDto;
import com.MainBackendService.model.Desk;
import com.MainBackendService.model.User;
import com.MainBackendService.repository.DeskRepository;
import com.MainBackendService.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class DeskService {

    @Autowired
    private DeskRepository deskRepository;

    @Autowired
    private UserRepository userRepository;

    public Desk createDesk(CreateDeskDto createDeskDTO) {
        // Find the desk owner by ID
        User deskOwner = userRepository.findById(createDeskDTO.getDeskOwnerId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Create new Desk object and set its fields
        Desk desk = new Desk();
        desk.setDeskName(createDeskDTO.getDeskName());
        desk.setDeskDescription(createDeskDTO.getDeskDescription());
        desk.setDeskThumbnail(createDeskDTO.getDeskThumbnail());
        desk.setDeskIcon(createDeskDTO.getDeskIcon());
        desk.setDeskIsPublic(createDeskDTO.getDeskIsPublic());
        desk.setDeskOwner(deskOwner);

        // Save Desk object to the database
        return deskRepository.save(desk);
    }

    public Desk updateDesk(Integer deskId, DeskDto deskDto) {
        // Check if the desk exists
        Optional<Desk> desk = findDeskById(deskId);
        if (desk.isEmpty()) {
            throw new IllegalArgumentException("desk id not found");
        }

        // Get the desk to be updated
        Desk existingDesk = desk.get();
        // Update fields in the desk entity
        existingDesk.setDeskName(deskDto.getDeskName());
        existingDesk.setDeskDescription(deskDto.getDeskDescription());
        existingDesk.setDeskThumbnail(deskDto.getDeskThumbnail());
        existingDesk.setDeskIcon(deskDto.getDeskIcon());
        existingDesk.setDeskIsPublic(deskDto.getDeskIsPublic());

        // Save the updated desk
        Desk updatedDesk = saveDesk(existingDesk);
        return existingDesk;
    }

    public DeskDto getDeskDto(Desk desk) {
        // Convert Desk entity to DeskDto
        return new DeskDto(
                String.valueOf(desk.getDeskId()), // Assuming deskId is Integer
                desk.getDeskName(),
                desk.getDeskDescription(),
                desk.getDeskThumbnail(),
                desk.getDeskIcon(),
                desk.getDeskIsPublic()
        );
    }

    public Optional<Desk> findDeskById(Integer deskId) {
        return deskRepository.findById(deskId);
    }

    public Desk saveDesk(Desk desk) {
        return deskRepository.save(desk);
    }

    public boolean isUserOwnerOfDesk(Integer userId, Integer deskId) {
        Optional<Desk> desk = findDeskById(deskId);
        return desk.isPresent() && desk.get().getDeskOwner().getUserId().equals(userId);
    }

    public void deleteDesk(Integer deskId) {
        Optional<Desk> desk = deskRepository.findById(deskId);
        // Proceed to delete
        deskRepository.delete(desk.get());

    }


}
