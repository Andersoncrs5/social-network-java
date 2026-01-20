package com.blog.writeapi.services.providers;

import com.blog.writeapi.models.PostAttachmentModel;
import com.blog.writeapi.repositories.PostAttachmentRepository;
import com.blog.writeapi.services.interfaces.IPostAttachmentService;
import com.blog.writeapi.services.interfaces.IStorageService;
import com.blog.writeapi.utils.annotations.valid.global.isId.IsId;
import com.blog.writeapi.utils.annotations.valid.isModelInitialized.IsModelInitialized;
import com.blog.writeapi.utils.exceptions.ModelNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostAttachmentService implements IPostAttachmentService {

    private final PostAttachmentRepository repository;
    private final IStorageService storageService;
    private final String BUCKET = "attachments";

    @Transactional(readOnly = true)
    public PostAttachmentModel getByIdSimple(@IsId Long id) {
        return this.repository.findById(id)
                .orElseThrow(() -> new ModelNotFoundException("Attachment not found"));
    }

    @Transactional
    public Boolean delete(@IsModelInitialized PostAttachmentModel model) {
        Boolean exists = this.storageService.deleteObject(BUCKET, model.getStorageKey(), null);
        if (!exists)
            return false;

        this.repository.delete(model);

        return true;
    }

}
