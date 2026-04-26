package com.blog.writeapi.modules.outbox.service.interfaces;

import com.blog.writeapi.modules.outbox.dto.CreateOutboxDTO;
import com.blog.writeapi.modules.outbox.dto.UpdateOutboxDTO;
import com.blog.writeapi.modules.outbox.model.OutboxModel;
import com.blog.writeapi.utils.annotations.validations.global.isId.IsId;

public interface IOutboxService {
    OutboxModel create(CreateOutboxDTO dto);
    void deleteById(@IsId Long id);
    OutboxModel update(OutboxModel outbox, UpdateOutboxDTO dto);
}
