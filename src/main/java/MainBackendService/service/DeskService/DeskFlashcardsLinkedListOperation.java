package MainBackendService.service.DeskService;

import MainBackendService.exception.HttpBadRequestException;
import MainBackendService.exception.HttpNotFoundException;
import MainBackendService.exception.HttpResponseException;
import com.jooq.sample.model.tables.records.DeskRecord;
import com.jooq.sample.model.tables.records.FlashcardRecord;
import lombok.AllArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import static com.jooq.sample.model.tables.Desk.DESK;
import static com.jooq.sample.model.tables.Flashcard.FLASHCARD;

@Service
public class DeskFlashcardsLinkedListOperation {
    Logger logger = LogManager.getLogger(DeskFlashcardsLinkedListOperation.class);

    @Autowired
    private DSLContext dslContext;

    // Create an InsertOperation object to perform various insertion types (head, tail, etc.)
    public InsertOperation insertFlashcardOperation(Integer flashcardId) throws HttpResponseException {
        // Fetch the flashcard record
        FlashcardRecord flashcardRecord = dslContext.selectFrom(FLASHCARD)
                .where(FLASHCARD.FLASHCARD_ID.eq(flashcardId))
                .fetchOne();

        if (flashcardRecord == null) {
            throw new HttpBadRequestException("Flashcard not found");
        }

        // Fetch the desk the flashcard belongs to
        DeskRecord deskRecord = dslContext.selectFrom(DESK)
                .where(DESK.DESK_ID.eq(flashcardRecord.getFlashcardDeskId()))
                .fetchOne();

        if (deskRecord == null) {
            throw new HttpBadRequestException("Desk not found");
        }

        return new InsertOperation(flashcardRecord, dslContext, deskRecord);
    }

    // Delete a flashcard from the desk's linked list
    public void deleteFlashcardOperation(Integer flashcardId) throws HttpResponseException {
        FlashcardRecord flashcardRecord = dslContext.selectFrom(FLASHCARD)
                .where(FLASHCARD.FLASHCARD_ID.eq(flashcardId))
                .fetchOne();

        if (flashcardRecord == null) {
            throw new HttpBadRequestException("Flashcard not found");
        }

        DeskRecord deskRecord = dslContext.selectFrom(DESK)
                .where(DESK.DESK_ID.eq(flashcardRecord.getFlashcardDeskId()))
                .fetchOne();

        if (deskRecord == null) {
            throw new HttpBadRequestException("Desk not found");
        }

        // If the flashcard is the first in the list, update the desk's start pointer
        if (deskRecord.getDeskStartFlashcardId().equals(flashcardId)) {
            deskRecord.setDeskStartFlashcardId(flashcardRecord.getNextFlashcardId()).store();
            flashcardRecord.delete();
            return;
        }

        // If it's the last node (next is null), simply delete it
        if (flashcardRecord.getNextFlashcardId() == null) {
            flashcardRecord.delete();
            return;
        }

        // If it's a middle node, update the previous node to skip this one
        Integer previousNode = getPreviousNode(flashcardId, deskRecord);
        FlashcardRecord previousFlashcard = dslContext.selectFrom(FLASHCARD)
                .where(FLASHCARD.FLASHCARD_ID.eq(previousNode))
                .fetchOne();

        previousFlashcard.setNextFlashcardId(flashcardRecord.getNextFlashcardId()).store();
        flashcardRecord.delete();
    }

    // Traverse the linked list starting from the desk's start flashcard
    public List<Integer> linkedListTraverse(DeskRecord deskRecord) throws HttpResponseException {
        Set<Integer> visitedNodeId = new LinkedHashSet<>(); // To keep track of visited nodes and detect cycles
        Integer node_flashcard_id = deskRecord.getDeskStartFlashcardId();

        visitedNodeId.add(node_flashcard_id);

        while (node_flashcard_id != null) {
            FlashcardRecord next_node = dslContext.selectFrom(FLASHCARD)
                    .where(FLASHCARD.FLASHCARD_ID.eq(node_flashcard_id))
                    .fetchOne();

            if (next_node == null) throw new HttpBadRequestException("Linked list traverse fail");

            node_flashcard_id = next_node.getNextFlashcardId();

            // Detect cycles
            if (visitedNodeId.contains(node_flashcard_id)) {
                logger.error("Cycle detected at node ID: " + node_flashcard_id);
                break;
            }

            if (node_flashcard_id == null) break;

            visitedNodeId.add(node_flashcard_id);
        }

        return new ArrayList<>(visitedNodeId);
    }


    // Get the flashcard ID of the node before the given node
    public Integer getPreviousNode(Integer nodeId, DeskRecord deskRecord) throws HttpResponseException {
        Set<Integer> visitedNodeId = new LinkedHashSet<>();
        Integer node_flashcard_id = deskRecord.getDeskStartFlashcardId();

        if (node_flashcard_id.equals(nodeId)) return null;

        visitedNodeId.add(node_flashcard_id);

        while (node_flashcard_id != null) {
            FlashcardRecord next_node = dslContext.selectFrom(FLASHCARD)
                    .where(FLASHCARD.FLASHCARD_ID.eq(node_flashcard_id))
                    .fetchOne();

            if (next_node == null) throw new HttpBadRequestException("Linked list traverse fail");

            if (next_node.getFlashcardId().equals(nodeId)) return node_flashcard_id;

            node_flashcard_id = next_node.getNextFlashcardId();

            if (visitedNodeId.contains(node_flashcard_id)) {
                logger.error("Cycle detected at node ID: " + node_flashcard_id);
                break;
            }
            visitedNodeId.add(node_flashcard_id);
        }

        return null;
    }

    // Class to represent insert operations on the linked list
    @AllArgsConstructor
    public class InsertOperation {
        private FlashcardRecord newNode;
        private DSLContext dslContext;
        private DeskRecord deskRecord;

        // Insert the node at the head of the list
        public void atHead() {
            newNode.setNextFlashcardId(deskRecord.getDeskStartFlashcardId());
            newNode.store();
            deskRecord.setDeskStartFlashcardId(newNode.getFlashcardId()).store();
        }

        // Insert the node at the tail of the list
        public void atTail() throws HttpResponseException {
            List<Integer> nodeIdList = linkedListTraverse(deskRecord);

            logger.info(nodeIdList);
            FlashcardRecord lastNode = dslContext.selectFrom(FLASHCARD)
                    .where(FLASHCARD.FLASHCARD_ID.eq(nodeIdList.getLast()))
                    .fetchOne();


            lastNode.setNextFlashcardId(newNode.getFlashcardId()).store();
        }

        // Insert the node before a specific node in the list
        public void beforeNode(Integer flashcardId) throws HttpResponseException {
            FlashcardRecord targetNode = dslContext.selectFrom(FLASHCARD)
                    .where(FLASHCARD.FLASHCARD_ID.eq(flashcardId))
                    .fetchOne();

            if (targetNode == null) {
                throw new HttpNotFoundException("Flashcard not found");
            }

            Integer previousNodeId = getPreviousNode(flashcardId, deskRecord);

            // If there's no previous node, insert at head
            if (previousNodeId == null) {
                atHead();
            } else {
                afterNode(previousNodeId);
            }
        }

        // Insert the node after a specific node in the list
        public void afterNode(Integer flashcardId) {
            FlashcardRecord targetNode = dslContext.selectFrom(FLASHCARD)
                    .where(FLASHCARD.FLASHCARD_ID.eq(flashcardId))
                    .fetchOne();

            newNode.setNextFlashcardId(targetNode.getNextFlashcardId()).store();
            targetNode.setNextFlashcardId(newNode.getFlashcardId()).store();
        }
    }
}
