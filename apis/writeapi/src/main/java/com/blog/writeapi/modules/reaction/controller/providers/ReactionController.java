package com.blog.writeapi.modules.reaction.controller.providers;

import com.blog.writeapi.modules.reaction.controller.docs.ReactionControllerDocs;
import com.blog.writeapi.modules.reaction.dtos.CreateReactionDTO;
import com.blog.writeapi.modules.reaction.dtos.ReactionDTO;
import com.blog.writeapi.modules.reaction.dtos.UpdateReactionDTO;
import com.blog.writeapi.modules.reaction.models.ReactionModel;
import com.blog.writeapi.modules.reaction.service.docs.IReactionService;
import com.blog.writeapi.utils.annotations.validations.global.isId.IsId;
import com.blog.writeapi.utils.mappers.ReactionMapper;
import com.blog.writeapi.utils.res.ResponseHttp;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.OffsetDateTime;
import java.util.UUID;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/reaction")
public class ReactionController implements ReactionControllerDocs {

    private final IReactionService service;
    private final ReactionMapper mapper;

    @Override
    public ResponseEntity<?> create(
            @Valid @RequestBody CreateReactionDTO dto,
            HttpServletRequest request
    ) {
        ReactionModel reaction = this.service.create(dto);

        return ResponseEntity.status(HttpStatus.CREATED).body(new ResponseHttp<>(
                this.mapper.toDTO(reaction),
                "Reaction created successfully",
                UUID.randomUUID().toString(),
                1,
                true,
                OffsetDateTime.now()
        ));
    }

    @Override
    public ResponseEntity<?> delete(
            @IsId @PathVariable Long id,
            HttpServletRequest request
    ) {
        ReactionModel reaction = this.service.getByIdSimple(id);
        this.service.delete(reaction);

        return ResponseEntity.status(HttpStatus.OK).body(new ResponseHttp<>(
                null,
                "Reaction deleted successfully",
                UUID.randomUUID().toString(),
                1,
                true,
                OffsetDateTime.now()
        ));
    }

    @Override
    public ResponseEntity<?> update(
            @IsId @PathVariable Long id,
            @Valid @RequestBody UpdateReactionDTO dto,
            HttpServletRequest request
    ) {
        ReactionModel reaction = this.service.getByIdSimple(id);
        ReactionModel update = this.service.update(dto, reaction);

        ResponseHttp<ReactionDTO> res = new ResponseHttp<>(
                this.mapper.toDTO(update),
                "Reaction updated successfully",
                UUID.randomUUID().toString(),
                1,
                true,
                OffsetDateTime.now()
        );

        return ResponseEntity.status(HttpStatus.OK).body(res);
    }

}
