package MainBackendService.service.VocabService;

import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class VocabService {
    private final DSLContext dslContext;

    @Autowired
    public VocabService(DSLContext dslContext) {
        this.dslContext = dslContext;
    }


}
