package MainBackendService.controller.Vocab;

import MainBackendService.controller.User.UserProfileController;
import MainBackendService.service.FlashcardService.FlashcardService;
import MainBackendService.service.SpacedRepetitionSerivce.SM_2_Service;
import MainBackendService.service.VocabService.VocabService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "${apiPrefix}/desk/{desk_id}/vocab")
public class VocabController {
    Logger logger = LogManager.getLogger(UserProfileController.class);

    @Autowired
    private VocabService vocabService;

    @Autowired
    private FlashcardService flashcardService;

    @Autowired
    private SM_2_Service sm_2_service;


}
