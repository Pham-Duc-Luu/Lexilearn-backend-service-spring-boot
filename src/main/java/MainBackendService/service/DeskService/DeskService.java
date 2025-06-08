package MainBackendService.service.DeskService;

import MainBackendService.dto.Desk.CreateDeskDto;
import MainBackendService.dto.Desk.UpdateDeskDto;
import MainBackendService.dto.DeskDto;
import MainBackendService.exception.HttpBadRequestException;
import MainBackendService.exception.HttpNotFoundException;
import MainBackendService.exception.HttpResponseException;
import MainBackendService.exception.HttpUnauthorizedException;
import MainBackendService.modal.DeskModal;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jooq.sample.model.enums.DeskDeskStatus;
import com.jooq.sample.model.tables.Flashcard;
import com.jooq.sample.model.tables.records.DeskRecord;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static com.jooq.sample.model.tables.Desk.DESK;

@Service
public class DeskService {
    Logger logger = LogManager.getLogger(DeskService.class);
    ObjectMapper objectMapper = new ObjectMapper();
    @Autowired
    private DSLContext dslContext;
    @Autowired
    private ElasticsearchOperations elasticsearchOperations;

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
                .set(DESK.DESK_OWNER_ID, createDeskDTO.getDeskOwnerId())
                .set(DESK.DESK_IS_PUBLIC, createDeskDTO.getDeskIsPublic() ? (byte) 1 : (byte) 0)  // Fixing the boolean to byte conversion                .set(DESK.DESK_OWNER_ID, deskOwnerId)
                .returning()
                .fetchOneInto(DeskRecord.class);

        // Save Desk object to the database
        return deskRecord;
    }

    public DeskRecord updateDesk(Integer deskId, DeskDto deskDto) throws JsonProcessingException {

        // Use jOOQ to update the desk in the database
        int affectedRows = dslContext.update(DESK)
                .set(DESK.DESK_NAME, deskDto.getDeskName())  // Set new desk name
                .set(DESK.DESK_DESCRIPTION, deskDto.getDeskDescription())  // Set new desk description
                .set(DESK.DESK_THUMBNAIL, deskDto.getDeskThumbnail())  // Set new thumbnail
                .set(DESK.DESK_ICON, deskDto.getDeskIcon())  // Set new icon
                .set(DESK.DESK_STATUS, DeskDeskStatus.valueOf(deskDto.getDeskStatus()))
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

    public DeskRecord updateAPartDesk(Integer deskId, UpdateDeskDto updateDeskDto) throws HttpNotFoundException {
        DeskRecord deskRecord = dslContext.selectFrom(DESK)
                .where(DESK.DESK_ID.eq(deskId))
                .fetchOne();

        // Update if not null

        if (deskRecord == null) {
            throw new HttpNotFoundException("Desk with ID " + deskId + " not found.");
        }

        // Update fields if non-null
        if (updateDeskDto.getDesk_name() != null) {
            deskRecord.setDeskName(updateDeskDto.getDesk_name());
        }

        if (updateDeskDto.getDesk_description() != null) {
            deskRecord.setDeskDescription(updateDeskDto.getDesk_description());
        }

        if (updateDeskDto.getDesk_thumbnail() != null) {
            deskRecord.setDeskThumbnail(updateDeskDto.getDesk_thumbnail());
        }

        if (updateDeskDto.getDesk_icon() != null) {
            deskRecord.setDeskIcon(updateDeskDto.getDesk_icon());
        }

        if (updateDeskDto.getDesk_is_public() != null) {
            deskRecord.setDeskIsPublic(updateDeskDto.getDesk_is_public() ? (byte) 1 : (byte) 0);
        }

        if (updateDeskDto.getDesk_status() != null) {
            try {
                DeskDeskStatus status = DeskDeskStatus.valueOf(updateDeskDto.getDesk_status());
                deskRecord.setDeskStatus(status);
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Invalid desk status: " + updateDeskDto.getDesk_status());
            }
        }

        // save
        deskRecord.setUpdatedAt(LocalDateTime.now());
        deskRecord.store();

        return deskRecord;
    }


    public DeskRecord getDeskById(Integer deskId) {
        return dslContext.selectFrom(DESK).where(DESK.DESK_ID.eq(deskId)).fetchOne();
    }

    public DeskRecord updateDesk(Integer deskId, DeskModal deskModal) throws JsonProcessingException {
        // Use jOOQ to update the desk in the database
        int affectedRows = dslContext.update(DESK)
                .set(DESK.DESK_NAME, deskModal.getName())  // Set new desk name
                .set(DESK.DESK_DESCRIPTION, deskModal.getDescription())  // Set new desk description
                .set(DESK.DESK_THUMBNAIL, deskModal.getThumbnail())  // Set new thumbnail
                .set(DESK.DESK_ICON, deskModal.getIcon())  // Set new icon
                .set(DESK.DESK_STATUS, deskModal.getStatus())
                .set(DESK.DESK_IS_PUBLIC, deskModal.getIsPublic() ? (byte) 1 : (byte) 0)  // Convert Boolean to Byte
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

    public DeskRecord updateUserDesk(Integer userId, DeskDto deskDto) throws HttpResponseException, JsonProcessingException {
        if (!isUserOwnerOfDesk(userId, deskDto.getDeskOwnerId())) {
            throw new HttpUnauthorizedException("You are not allow to modify this desk");
        }
        return updateDesk(deskDto.getDeskOwnerId(), deskDto);
    }

    public DeskRecord updateUserDesk(Integer userId, DeskModal deskModal) throws HttpResponseException, JsonProcessingException {
        if (!isUserOwnerOfDesk(userId, Integer.valueOf(deskModal.getOwnerId()))) {
            throw new HttpUnauthorizedException("You are not allow to modify this desk");
        }
        return updateDesk(userId, deskModal);
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

    public Integer deskSize(Integer deskId) {
        Flashcard flashcard = Flashcard.FLASHCARD;
        return dslContext
                .selectCount()
                .from(flashcard)
                .where(flashcard.FLASHCARD_DESK_ID.eq(deskId))
                .fetchOne(0, int.class);
    }


    // * return true if the user own the desk, false otherwise
    public boolean isUserOwnerOfDesk(Integer userId, Integer deskId) throws HttpNotFoundException {


        // Query the Desk table to check if the desk exists and the user is the owner
        Integer ownerId = dslContext.select(DESK.DESK_OWNER_ID)
                .from(DESK)
                .where(DESK.DESK_ID.eq(deskId))  // Match by deskId
                .fetchOne(DESK.DESK_OWNER_ID);  // Fetch the owner ID of the desk

        if (ownerId == null) {
            throw new HttpNotFoundException("desk not found");
        }

        // Check if the fetched ownerId matches the provided userId
        return ownerId.equals(userId);
    }

    public void deleteDesk(Integer deskId) throws HttpResponseException {
        // Delete the Desk record by deskId
        int rowsDeleted = dslContext.update(DESK)
                .set(DESK.DESK_STATUS, DeskDeskStatus.BIN).where(DESK.DESK_ID.eq(deskId))
                .execute();

        if (rowsDeleted == 0) {
            throw new HttpBadRequestException("Desk with id " + deskId + " not found.");
        }

    }

    public DeskRecord deleteDesk(Integer deskId, Class<DeskRecord> deskRecordClass) throws HttpResponseException {
        DeskRecord deskRecord = dslContext.selectFrom(DESK).where(DESK.DESK_ID.eq(deskId)).fetchOne();
        if (deskRecord == null) {
            throw new HttpNotFoundException();
        }
        deskRecord.setDeskStatus(DeskDeskStatus.BIN).store();
        return deskRecord;

    }


    public List<DeskRecord> getDesks(Integer limit, Integer offset) {
        return dslContext.selectFrom(DESK)
                .limit(limit)
                .offset(offset)
                .fetchInto(DeskRecord.class);  // Map to DeskRecord
    }

    public DeskRecord publicDesk(Integer deskId, Boolean isPublic) throws HttpNotFoundException {
        DeskRecord deskRecord = dslContext.selectFrom(DESK).where(DESK.DESK_ID.eq(deskId)).fetchOne();
        if (deskRecord == null) {
            throw new HttpNotFoundException();
        }

        deskRecord.setDeskIsPublic(isPublic ? (byte) 1 : (byte) 0).store();
        return deskRecord;
    }

}
