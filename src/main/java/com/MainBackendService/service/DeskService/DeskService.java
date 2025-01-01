package com.MainBackendService.service.DeskService;

import com.MainBackendService.dto.CreateDeskDto;
import com.MainBackendService.dto.DeskDto;
import com.jooq.sample.model.tables.records.DeskRecord;
import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

import static com.jooq.sample.model.tables.Desk.DESK;

@Service
public class DeskService {

    private final DSLContext dslContext;

    @Autowired
    public DeskService(DSLContext dslContext) {
        this.dslContext = dslContext;

    }

    public DeskRecord createDesk(CreateDeskDto createDeskDTO) {
        Integer deskOwnerId = createDeskDTO.getDeskOwnerId();
        boolean userExists = dslContext.selectOne()
                .from(com.jooq.sample.model.tables.User.USER)
                .where(com.jooq.sample.model.tables.User.USER.USER_ID.eq(deskOwnerId))
                .fetchOptional()
                .isPresent();
        if (!userExists) {
            throw new IllegalArgumentException("User with ID " + deskOwnerId + " does not exist");
        } // Step 2: Insert the desk record into the database
        DeskRecord deskRecord = dslContext.insertInto(DESK)
                .set(DESK.DESK_NAME, createDeskDTO.getDeskName())
                .set(DESK.DESK_DESCRIPTION, createDeskDTO.getDeskDescription())
                .set(DESK.DESK_THUMBNAIL, createDeskDTO.getDeskThumbnail())
                .set(DESK.DESK_ICON, createDeskDTO.getDeskIcon())
                .set(DESK.DESK_IS_PUBLIC, createDeskDTO.getDeskIsPublic() ? (byte) 1 : (byte) 0)  // Fixing the boolean to byte conversion                .set(DESK.DESK_OWNER_ID, deskOwnerId)
                .returning(DESK.DESK_ID)  // Returning the generated desk ID
                .fetchOne();


        // Save Desk object to the database
        return deskRecord;
    }

    public DeskRecord updateDesk(Integer deskId, DeskDto deskDto) {
        // Use jOOQ to update the desk in the database
        int affectedRows = dslContext.update(DESK)
                .set(DESK.DESK_NAME, deskDto.getDeskName())  // Set new desk name
                .set(DESK.DESK_DESCRIPTION, deskDto.getDeskDescription())  // Set new desk description
                .set(DESK.DESK_THUMBNAIL, deskDto.getDeskThumbnail())  // Set new thumbnail
                .set(DESK.DESK_ICON, deskDto.getDeskIcon())  // Set new icon
                .set(DESK.DESK_IS_PUBLIC, deskDto.getDeskIsPublic() ? (byte) 1 : (byte) 0)  // Convert Boolean to Byte
                .where(DESK.DESK_ID.eq(deskId))  // Specify the deskId to update
                .execute();

        // You can return the updated record or a status based on the affected rows
        if (affectedRows > 0) {
            // Fetch the updated desk record (optional)
            return dslContext.selectFrom(DESK)
                    .where(DESK.DESK_ID.eq(deskId))
                    .fetchOne();
        } else {
            // Handle if the desk wasn't found or updated (optional)
            throw new IllegalArgumentException("Desk with ID " + deskId + " does not exist or no changes were made.");
        }
    }

    public DeskDto getDeskDto(DeskRecord desk) {
        // Convert Desk entity to DeskDto
        return new DeskDto(
                String.valueOf(desk.getDeskId()), // Assuming deskId is Integer
                desk.getDeskName(),
                desk.getDeskDescription(),
                desk.getDeskThumbnail(),
                desk.getDeskIcon(),
                desk.getDeskIsPublic() != null && desk.getDeskIsPublic() == 1 // Convert Byte to Boolean
        );
    }

    public Optional<DeskRecord> findDeskById(Integer deskId) {
        // Perform the query and return the result wrapped in Optional
        DeskRecord deskRecord = dslContext
                .selectFrom(DESK)
                .where(DESK.DESK_ID.eq(deskId)) // Add the condition to filter by deskId
                .fetchOne(); // Fetch one result as DeskRecord

        return Optional.ofNullable(deskRecord); // Return the result wrapped in Optional
    }

    public DeskRecord saveDesk(DeskRecord desk) {
        // If deskId is null, it's a new record, so we perform an insert
        if (desk.getDeskId() == null) {
            // Insert a new desk record
            desk = dslContext.insertInto(DESK)
                    .set(DESK.DESK_NAME, desk.getDeskName())
                    .set(DESK.DESK_DESCRIPTION, desk.getDeskDescription())
                    .set(DESK.DESK_THUMBNAIL, desk.getDeskThumbnail())
                    .set(DESK.DESK_ICON, desk.getDeskIcon())
                    .set(DESK.DESK_IS_PUBLIC, desk.getDeskIsPublic())
                    .set(DESK.DESK_OWNER_ID, desk.getDeskOwnerId())
                    .returning()  // Retrieve the generated deskId
                    .fetchOne();  // Execute the insert and fetch the inserted record

        } else {
            // If deskId is not null, update the existing record
            desk = dslContext.update(DESK)
                    .set(DESK.DESK_NAME, desk.getDeskName())
                    .set(DESK.DESK_DESCRIPTION, desk.getDeskDescription())
                    .set(DESK.DESK_THUMBNAIL, desk.getDeskThumbnail())
                    .set(DESK.DESK_ICON, desk.getDeskIcon())
                    .set(DESK.DESK_IS_PUBLIC, desk.getDeskIsPublic())
                    .set(DESK.DESK_OWNER_ID, desk.getDeskOwnerId())
                    .where(DESK.DESK_ID.eq(desk.getDeskId()))  // Specify the record to update
                    .returning()  // Retrieve the updated desk record
                    .fetchOne();  // Execute the update and fetch the updated record
        }

        return desk;  // Return the saved/updated desk record
    }

    public boolean isUserOwnerOfDesk(Integer userId, Integer deskId) {
        // Query the Desk table to check if the desk exists and the user is the owner
        Integer ownerId = dslContext.select(DESK.DESK_OWNER_ID)
                .from(DESK)
                .where(DESK.DESK_ID.eq(deskId))  // Match by deskId
                .fetchOne(DESK.DESK_OWNER_ID);  // Fetch the owner ID of the desk

        // Check if the fetched ownerId matches the provided userId
        return ownerId != null && ownerId.equals(userId);
    }

    public void deleteDesk(Integer deskId) {
        // Delete the Desk record by deskId
        int rowsDeleted = dslContext.deleteFrom(DESK)
                .where(DESK.DESK_ID.eq(deskId))
                .execute();

        if (rowsDeleted == 0) {
            throw new IllegalArgumentException("Desk with id " + deskId + " not found.");
        }

    }

    public List<DeskRecord> getDesks(Integer limit, Integer offset) {
        return dslContext.selectFrom(DESK)
                .limit(limit)
                .offset(offset)
                .fetchInto(DeskRecord.class);  // Map to DeskRecord
    }

}
