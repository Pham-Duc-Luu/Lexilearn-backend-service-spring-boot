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
    public void deleteFlashcardOperation(Integer flashcardId, Integer desk_id) throws HttpResponseException {
        FlashcardRecord flashcardRecord = dslContext.selectFrom(FLASHCARD)
                .where(FLASHCARD.FLASHCARD_ID.eq(flashcardId).and(FLASHCARD.FLASHCARD_DESK_ID.eq(desk_id)))
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

        if (previousFlashcard == null) {
            throw new HttpBadRequestException();
        }

        previousFlashcard.setNextFlashcardId(flashcardRecord.getNextFlashcardId()).store();
        logger.info(previousFlashcard);
        flashcardRecord.delete();
    }

    // Traverse the linked list starting from the desk's start flashcard
    public List<Integer> linkedListTraverse(DeskRecord deskRecord) throws HttpResponseException {
        Set<Integer> visitedNodeId = new LinkedHashSet<>(); // To keep track of visited nodes and detect cycles
        Integer node_flashcard_id = deskRecord.getDeskStartFlashcardId();

        visitedNodeId.add(node_flashcard_id);

        while (node_flashcard_id != null) {
            FlashcardRecord node = dslContext.selectFrom(FLASHCARD)
                    .where(FLASHCARD.FLASHCARD_ID.eq(node_flashcard_id))
                    .fetchOne();

            if (node == null) throw new HttpBadRequestException("Linked list traverse fail");

            node_flashcard_id = node.getNextFlashcardId();

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

    public DeskRecord switchItemInLinkList(FlashcardRecord flashcard_A, FlashcardRecord flashcard_B, DeskRecord deskRecord) throws HttpResponseException {
        // Nothing to do if x and y are the same
        if (flashcard_A.getFlashcardId().equals(flashcard_B.getFlashcardId())) {
            return deskRecord;
        }

        // * we can divide this into 2 sub solution
        // * 1. A and B is adjacent
        // * 2. A and B is not adjacent


        FlashcardRecord prevA = getPreviousNode(flashcard_A.getFlashcardId(), deskRecord, FlashcardRecord.class);

        FlashcardRecord prevB = getPreviousNode(flashcard_B.getFlashcardId(), deskRecord, FlashcardRecord.class);


        // * 1.1 If A and B is adjacent
        // * and
        // * if A -> B => prevA -> A(prevB) -> B -> ...
        // * then
        // * prevA.next = B
        // * A.next = B.next
        // * B.next = A
        if (flashcard_A.getNextFlashcardId() != null && flashcard_A.getNextFlashcardId().equals(flashcard_B.getFlashcardId())
        ) {
            // If A is not head of the linked list
            if (prevA != null) {
                prevA.setNextFlashcardId(flashcard_B.getFlashcardId()).store();
            } else {
                deskRecord.setDeskStartFlashcardId(flashcard_B.getFlashcardId()).store();
            }
            flashcard_A.setNextFlashcardId(flashcard_B.getNextFlashcardId()).store();
            flashcard_B.setNextFlashcardId(flashcard_A.getFlashcardId()).store();
            return deskRecord;
        }


        // * 1.2 If A and B is adjacent
        // * and
        // * if B -> A => prevB -> B(prevA) -> A -> ...
        // * then
        // * prevB.next = A
        // * B.next = A.next
        // * A.next = B
        if (flashcard_B.getNextFlashcardId() != null && flashcard_B.getNextFlashcardId().equals(flashcard_A.getFlashcardId())
        ) {
            // If B is not head of the linked list
            if (prevB != null) {
                prevB.setNextFlashcardId(flashcard_A.getFlashcardId()).store();
            } else {
                deskRecord.setDeskStartFlashcardId(flashcard_A.getFlashcardId()).store();
            }
            flashcard_B.setNextFlashcardId(flashcard_A.getNextFlashcardId()).store();
            flashcard_A.setNextFlashcardId(flashcard_B.getFlashcardId()).store();
            return deskRecord;
        }


        // * 2. A and B is not adjacent

        // If A is not head of the linked list
        if (prevA != null) {
            prevA.setNextFlashcardId(flashcard_B.getFlashcardId()).store();
        } else {
            deskRecord.setDeskStartFlashcardId(flashcard_B.getFlashcardId()).store();
        }

        // If B is not head of the linked list
        if (prevB != null) {
            prevB.setNextFlashcardId(flashcard_A.getFlashcardId()).store();
        } else {
            deskRecord.setDeskStartFlashcardId(flashcard_A.getFlashcardId()).store();
        }
        // Swap next pointers
        Integer temp = flashcard_B.getNextFlashcardId();
        flashcard_B.setNextFlashcardId(flashcard_A.getNextFlashcardId()).store();
        flashcard_A.setNextFlashcardId(temp).store();

        return deskRecord;

    }


    // Get the flashcard ID of the node before the given node
    public Integer getPreviousNode(Integer nodeId, DeskRecord deskRecord) throws HttpResponseException {
        Set<Integer> visitedNodeId = new LinkedHashSet<>();
        Integer node_flashcard_id = deskRecord.getDeskStartFlashcardId();

        if (node_flashcard_id.equals(nodeId)) return null;

        visitedNodeId.add(node_flashcard_id);


        while (node_flashcard_id != null) {
            FlashcardRecord node = dslContext.selectFrom(FLASHCARD)
                    .where(FLASHCARD.FLASHCARD_ID.eq(node_flashcard_id))
                    .fetchOne();

            if (node == null) throw new HttpBadRequestException("Linked list traverse fail");

            // * break if this is the last item of the linked list
            if (node.getNextFlashcardId() == null) break;

            // * if the next node id === the input node => this is the previous node
            if (node.getNextFlashcardId().equals(nodeId)) return node_flashcard_id;

            node_flashcard_id = node.getNextFlashcardId();

            if (visitedNodeId.contains(node_flashcard_id)) {
                logger.error("Cycle detected at node ID: " + node_flashcard_id);
                break;
            }
            visitedNodeId.add(node_flashcard_id);
        }

        return null;
    }

    // Get the flashcard ID of the node before the given node
    public FlashcardRecord getPreviousNode(Integer nodeId, DeskRecord deskRecord, Class<FlashcardRecord> flashcardRecordClass) throws HttpResponseException {
        Set<Integer> visitedNodeId = new LinkedHashSet<>();
        Integer node_flashcard_id = deskRecord.getDeskStartFlashcardId();

        if (node_flashcard_id.equals(nodeId)) return null;

        visitedNodeId.add(node_flashcard_id);


        while (node_flashcard_id != null) {
            FlashcardRecord node = dslContext.selectFrom(FLASHCARD)
                    .where(FLASHCARD.FLASHCARD_ID.eq(node_flashcard_id))
                    .fetchOne();

            if (node == null) throw new HttpBadRequestException("Linked list traverse fail");

            // * break if this is the last item of the linked list
            if (node.getNextFlashcardId() == null) break;

            // * if the next node id === the input node => this is the previous node
            if (node.getNextFlashcardId().equals(nodeId)) return node;

            node_flashcard_id = node.getNextFlashcardId();

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
