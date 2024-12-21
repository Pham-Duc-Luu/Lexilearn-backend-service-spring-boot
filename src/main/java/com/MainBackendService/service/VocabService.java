package com.MainBackendService.service;

import com.MainBackendService.dto.CreateVocabDto;
import com.MainBackendService.model.Vocab;
import com.MainBackendService.model.VocabLanguageType;
import com.MainBackendService.repository.VocabRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class VocabService {

    @Autowired
    private VocabRepository vocabRepository;

    public Vocab createVocab(CreateVocabDto createVocabDto) {
        // 2. Create the vocab object
        Vocab newVocab = new Vocab();
        newVocab.setVocabLanguage(VocabLanguageType.valueOf(createVocabDto.getVocabLanguage()));
        newVocab.setVocabMeaning(createVocabDto.getVocabMeaning());
        newVocab.setVocabImage(createVocabDto.getVocabImage());
        newVocab.setVocabText(createVocabDto.getVocabText());
        return saveVocab(newVocab);
    }

    public Vocab saveVocab(Vocab vocab) {
        return vocabRepository.save(vocab);
    }
}
