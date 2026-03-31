package com.blog.writeapi.integration;

import com.blog.writeapi.configs.HelperTest;
import com.blog.writeapi.modules.comment.dtos.CommentDTO;
import com.blog.writeapi.modules.commentVote.dtos.CommentVoteDTO;
import com.blog.writeapi.modules.commentVote.dtos.ToggleCommentVoteDTO;
import com.blog.writeapi.modules.post.dtos.PostDTO;
import com.blog.writeapi.utils.enums.votes.VoteTypeEnum;
import com.blog.writeapi.modules.comment.repository.CommentRepository;
import com.blog.writeapi.modules.commentVote.repository.CommentVoteRepository;
import com.blog.writeapi.modules.post.repository.PostRepository;
import com.blog.writeapi.utils.res.ResponseHttp;
import com.blog.writeapi.utils.res.ResponseUserTest;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class CommentVoteControllerTest {
    private final String URL = "/v1/comment-vote";
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private CommentVoteRepository repository;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private HelperTest helper;

    @BeforeEach
    void setup () {
        this.repository.deleteAll();
        this.commentRepository.deleteAll();
        this.postRepository.deleteAll();
    }

    @Test
    void shouldAddedVoteUpInComment() throws Exception {
        ResponseUserTest userData = this.helper.createUser();
        PostDTO post = this.helper.createPost(userData);
        CommentDTO commentDTO = this.helper.createComment(userData, post, null);

        ToggleCommentVoteDTO dto = new ToggleCommentVoteDTO(
                commentDTO.id(),
                VoteTypeEnum.UPVOTE
        );

        MvcResult result = this.mockMvc.perform(post(this.URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(this.objectMapper.writeValueAsString(dto))
                        .header("Authorization", "Bearer " + userData.tokens().token()
                        ))
                .andExpect(status().isCreated())
                .andReturn();

        String json = result.getResponse().getContentAsString();
        TypeReference<ResponseHttp<CommentVoteDTO>> typeRef = new TypeReference<>() {};

        ResponseHttp<CommentVoteDTO> response = objectMapper.readValue(json, typeRef);

        assertThat(response.message()).isNotBlank();
        assertThat(response.status()).isEqualTo(true);
        assertThat(response.data().id()).isNotZero().isNotNegative();
        assertThat(response.data().comment().id()).isEqualTo(commentDTO.id());
        assertThat(response.data().user().id()).isEqualTo(userData.userDTO().id());
        assertThat(response.data().type()).isEqualTo(dto.type());
    }

    @Test
    void shouldAddedVoteDownInComment() throws Exception {
        ResponseUserTest userData = this.helper.createUser();
        PostDTO post = this.helper.createPost(userData);
        CommentDTO commentDTO = this.helper.createComment(userData, post, null);
        CommentVoteDTO voteDTO = this.helper.addCommentVoteInComment(userData, commentDTO, VoteTypeEnum.DOWNVOTE);

        assertThat(voteDTO.id()).isNotZero().isNotNegative();
        assertThat(voteDTO.comment().id()).isEqualTo(commentDTO.id());
        assertThat(voteDTO.user().id()).isEqualTo(userData.userDTO().id());
        assertThat(voteDTO.type()).isEqualTo(VoteTypeEnum.DOWNVOTE);
    }

    @Test
    void shouldChangeTheValueOfVoteInComment() throws Exception {
        ResponseUserTest userData = this.helper.createUser();
        PostDTO post = this.helper.createPost(userData);
        CommentDTO commentDTO = this.helper.createComment(userData, post, null);
        CommentVoteDTO voteDTO = this.helper.addCommentVoteInComment(userData, commentDTO, VoteTypeEnum.DOWNVOTE);

        ToggleCommentVoteDTO dto = new ToggleCommentVoteDTO(
                commentDTO.id(),
                VoteTypeEnum.UPVOTE
        );

        MvcResult result = this.mockMvc.perform(post(this.URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(this.objectMapper.writeValueAsString(dto))
                        .header("Authorization", "Bearer " + userData.tokens().token()
                        ))
                .andExpect(status().isOk())
                .andReturn();

        String json = result.getResponse().getContentAsString();
        TypeReference<ResponseHttp<CommentVoteDTO>> typeRef = new TypeReference<>() {};

        ResponseHttp<CommentVoteDTO> response = objectMapper.readValue(json, typeRef);

        assertThat(response.message()).isNotBlank();
        assertThat(response.status()).isEqualTo(true);
        assertThat(response.data().id()).isNotZero().isNotNegative();
        assertThat(response.data().comment().id()).isEqualTo(commentDTO.id());
        assertThat(response.data().user().id()).isEqualTo(userData.userDTO().id());
        assertThat(response.data().type()).isEqualTo(dto.type());
    }

    @Test
    void shouldRemoveVoteInComment() throws Exception {
        ResponseUserTest userData = this.helper.createUser();
        PostDTO post = this.helper.createPost(userData);
        CommentDTO commentDTO = this.helper.createComment(userData, post, null);
        CommentVoteDTO voteDTO = this.helper.addCommentVoteInComment(userData, commentDTO, VoteTypeEnum.UPVOTE);

        ToggleCommentVoteDTO dto = new ToggleCommentVoteDTO(
                commentDTO.id(),
                VoteTypeEnum.UPVOTE
        );

        MvcResult result = this.mockMvc.perform(post(this.URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(this.objectMapper.writeValueAsString(dto))
                        .header("Authorization", "Bearer " + userData.tokens().token()
                        ))
                .andExpect(status().isOk())
                .andReturn();

        String json = result.getResponse().getContentAsString();
        TypeReference<ResponseHttp<Object>> typeRef = new TypeReference<>() {};

        ResponseHttp<Object> response = objectMapper.readValue(json, typeRef);

        assertThat(response.message()).isNotBlank();
        assertThat(response.status()).isEqualTo(true);
        assertThat(response.data()).isNull();
    }


}
