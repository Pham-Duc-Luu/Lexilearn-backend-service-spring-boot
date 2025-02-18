package com.MainBackendService.dto.GraphqlDto;

import com.jooq.sample.model.tables.records.DeskRecord;
import lombok.Data;
import org.jooq.SortOrder;
import org.jooq.TableField;

import static com.jooq.sample.model.tables.Desk.DESK;

@Data
public class DeskQuerySort {
    private DeskSortField field;
    private SortOrder order;

    public DeskSortField getField() {
        return field;
    }

    public void setField(DeskSortField field) {
        this.field = field;
    }

    public SortOrder getOrder() {
        return order;
    }

    public void setOrder(SortOrder order) {
        this.order = order;
    }

    public TableField<DeskRecord, ?> getDeskKey() {
        if (field == null) return null;
        switch (field) {
            case DeskSortField.name:
                return DESK.DESK_NAME;
            case DeskSortField.createdAt:
                return DESK.CREATED_AT;
            case DeskSortField.updatedAt:
                return DESK.UPDATED_AT;
            default:
                return null;
        }
    }

}
