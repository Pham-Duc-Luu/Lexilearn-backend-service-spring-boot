package MainBackendService.service.DeskService;

import MainBackendService.exception.HttpNotFoundException;
import MainBackendService.exception.HttpResponseException;
import MainBackendService.service.FlashcardService.FlashcardBelongToDeskService;
import com.jooq.sample.model.tables.Desk;
import com.jooq.sample.model.tables.records.DeskRecord;
import org.jooq.Condition;
import org.jooq.DSLContext;

import java.time.LocalDateTime;


public class DeskBelongToUserService {

    private final Integer USER_ID;
    private final Desk desk = Desk.DESK;
    private final Condition ownerDeskCondition;

    private final DSLContext dslContext;


    public DeskBelongToUserService(Integer USER_ID, DSLContext dslContext) {
        this.USER_ID = USER_ID;
        this.dslContext = dslContext;

        // * define this query so only owner can effect their own desk
        // * as this to every query
        this.ownerDeskCondition = desk.DESK_OWNER_ID.eq(USER_ID);
    }


    public void deleteDesk(Integer deskId) {
        dslContext.delete(desk).where(ownerDeskCondition.and(desk.DESK_ID.eq(deskId))).execute();
    }

    public void createDesk(DeskRecord deskRecord) {
        deskRecord.setDeskOwnerId(USER_ID);
        deskRecord.setDeskId(null);
        deskRecord.setCreatedAt(LocalDateTime.now());
        deskRecord.setUpdatedAt(LocalDateTime.now());
        deskRecord.store();
    }

    public void updateDesk(DeskRecord updatedDeskRecord) throws HttpNotFoundException {
        DeskRecord deskRecord = dslContext.selectFrom(desk).where(ownerDeskCondition.and(desk.DESK_ID.eq(updatedDeskRecord.getDeskId()))).fetchOne();
        if (deskRecord == null) throw new HttpNotFoundException();
        updatedDeskRecord.store();
    }

    public void getDesk() {
    }

    public void deleteFlashcardInDesk(Integer deskId, Integer flashcardId) throws HttpResponseException {

        DeskRecord deskRecord = dslContext.selectFrom(desk).where(ownerDeskCondition.and(desk.DESK_ID.eq(deskId))).fetchOne();

        if (deskRecord == null) throw new HttpNotFoundException();

        new FlashcardBelongToDeskService(deskId, dslContext).deleteFlashcard(flashcardId);
    }


}
