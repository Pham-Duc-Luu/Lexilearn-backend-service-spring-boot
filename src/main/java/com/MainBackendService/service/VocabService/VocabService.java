package com.MainBackendService.service.VocabService;

import com.MainBackendService.dto.CreateVocabDto;
import com.jooq.sample.model.tables.records.VocabRecord;
import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static com.jooq.sample.model.tables.Vocab.VOCAB;

@Service
public class VocabService {
    private final DSLContext dslContext;

    @Autowired
    public VocabService(DSLContext dslContext) {
        this.dslContext = dslContext;
    }


    public VocabRecord createVocab(Integer deskId, CreateVocabDto createVocabDto) {
        // Create a new vocab record using JOOQ's DSLContext
        VocabRecord vocabRecord = dslContext.newRecord(VOCAB);

        // Set the fields using the DTO values
        vocabRecord.setVocabLanguage(createVocabDto.getVocabLanguage());
        vocabRecord.setVocabMeaning(createVocabDto.getVocabMeaning());
        vocabRecord.setVocabImage(createVocabDto.getVocabImage());
        vocabRecord.setVocabText(createVocabDto.getVocabText());
        vocabRecord.setVocabDeskId(deskId);
        // Insert the record into the database and store the result
        vocabRecord.store();

        // Return the created record
        return vocabRecord;
    }


}
