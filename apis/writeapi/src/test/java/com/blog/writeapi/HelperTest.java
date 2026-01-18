package com.blog.writeapi;

import cn.hutool.core.lang.UUID;
import com.blog.writeapi.dtos.category.CategoryDTO;
import com.blog.writeapi.dtos.category.CreateCategoryDTO;
import com.blog.writeapi.dtos.comment.CommentDTO;
import com.blog.writeapi.dtos.comment.CreateCommentDTO;
import com.blog.writeapi.dtos.commentFavorite.CommentFavoriteDTO;
import com.blog.writeapi.dtos.commentReaction.CommentReactionDTO;
import com.blog.writeapi.dtos.commentReaction.CreateCommentReactionDTO;
import com.blog.writeapi.dtos.commentVote.CommentVoteDTO;
import com.blog.writeapi.dtos.commentVote.ToggleCommentVoteDTO;
import com.blog.writeapi.dtos.post.CreatePostDTO;
import com.blog.writeapi.dtos.post.PostDTO;
import com.blog.writeapi.dtos.postCategories.CreatePostCategoriesDTO;
import com.blog.writeapi.dtos.postCategories.PostCategoriesDTO;
import com.blog.writeapi.dtos.postFavorite.PostFavoriteDTO;
import com.blog.writeapi.dtos.postReaction.CreatePostReactionDTO;
import com.blog.writeapi.dtos.postReaction.PostReactionDTO;
import com.blog.writeapi.dtos.postTag.CreatePostTagDTO;
import com.blog.writeapi.dtos.postTag.PostTagDTO;
import com.blog.writeapi.dtos.postVote.PostVoteDTO;
import com.blog.writeapi.dtos.postVote.TogglePostVoteDTO;
import com.blog.writeapi.dtos.reaction.CreateReactionDTO;
import com.blog.writeapi.dtos.reaction.ReactionDTO;
import com.blog.writeapi.dtos.tag.CreateTagDTO;
import com.blog.writeapi.dtos.tag.TagDTO;
import com.blog.writeapi.dtos.user.CreateUserDTO;
import com.blog.writeapi.dtos.user.LoginUserDTO;
import com.blog.writeapi.dtos.user.UserDTO;
import com.blog.writeapi.models.enums.reaction.ReactionTypeEnum;
import com.blog.writeapi.models.enums.votes.VoteTypeEnum;
import com.blog.writeapi.utils.res.ResponseHttp;
import com.blog.writeapi.utils.res.ResponseTokens;
import com.blog.writeapi.utils.res.ResponseUserTest;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.time.OffsetDateTime;
import java.util.Random;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Service
@RequiredArgsConstructor
public class HelperTest {

    private final MockMvc mockMvc;
    private final ObjectMapper objectMapper;

    public CommentReactionDTO toggleReactionToComment(
            ResponseUserTest userData2,
            CommentDTO comment,
            ReactionDTO reactionDTO
    ) throws Exception {
        CreateCommentReactionDTO dto = new CreateCommentReactionDTO(
                comment.id(),
                reactionDTO.id()
        );

        MvcResult result = mockMvc.perform(post("/v1/comment-reaction")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto))
                        .header("Authorization", "Bearer " + userData2.tokens().token())
                )
                .andReturn();

        String json = result.getResponse().getContentAsString();
        TypeReference<ResponseHttp<CommentReactionDTO>> typeRef = new TypeReference<>() {};
        ResponseHttp<CommentReactionDTO> response = objectMapper.readValue(json, typeRef);

        assertThat(response.message()).isNotBlank();
        assertThat(response.status()).isEqualTo(true);

        assertThat(response.data().comment().id()).isEqualTo(comment.id());
        assertThat(response.data().reaction().id()).isEqualTo(reactionDTO.id());
        assertThat(response.data().user().id()).isEqualTo(userData2.userDTO().id());

        return response.data();
    }

    public PostReactionDTO toggleReactionToPost(
            ResponseUserTest userData2,
            PostDTO post,
            ReactionDTO reactionDTO
    ) throws Exception {

        CreatePostReactionDTO dto = new CreatePostReactionDTO(
                post.id(),
                reactionDTO.id()
        );

        MvcResult result = mockMvc.perform(post("/v1/post-reaction")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto))
                        .header("Authorization", "Bearer " + userData2.tokens().token())
                )
                .andExpect(status().isCreated())
                .andReturn();

        String json = result.getResponse().getContentAsString();
        TypeReference<ResponseHttp<PostReactionDTO>> typeRef = new TypeReference<>() {};
        ResponseHttp<PostReactionDTO> response = objectMapper.readValue(json, typeRef);

        assertThat(response.message()).isNotBlank();
        assertThat(response.status()).isEqualTo(true);

        assertThat(response.data().id()).isNotNegative().isNotZero();
        assertThat(response.data().post().id()).isEqualTo(post.id());
        assertThat(response.data().user().id()).isEqualTo(userData2.userDTO().id());
        assertThat(response.data().reaction().id()).isEqualTo(reactionDTO.id());

        return response.data();
    }

    public CommentVoteDTO addCommentVoteInComment(
            ResponseUserTest userData,
            CommentDTO commentDTO,
            VoteTypeEnum type
    ) throws Exception {
        ToggleCommentVoteDTO dto = new ToggleCommentVoteDTO(
                commentDTO.id(),
                type
        );

        MvcResult result = this.mockMvc.perform(post("/v1/comment-vote")
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

        return response.data();
    }

    public PostVoteDTO addedPostVote(
            ResponseUserTest userData,
            PostDTO post,
            VoteTypeEnum type
    ) throws Exception {
        TogglePostVoteDTO dto = new TogglePostVoteDTO(
                post.id(),
                type
        );

        MvcResult result = this.mockMvc.perform(post("/v1/post-vote")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(this.objectMapper.writeValueAsString(dto))
                        .header("Authorization", "Bearer " + userData.tokens().token()
                        ))
                .andExpect(status().isCreated())
                .andReturn();

        String json = result.getResponse().getContentAsString();
        TypeReference<ResponseHttp<PostVoteDTO>> typeRef = new TypeReference<>() {};

        ResponseHttp<PostVoteDTO> response = objectMapper.readValue(json, typeRef);

        assertThat(response.message()).isNotBlank();
        assertThat(response.status()).isEqualTo(true);
        assertThat(response.data().id()).isNotZero().isNotNegative();
        assertThat(response.data().post().id()).isEqualTo(post.id());
        assertThat(response.data().user().id()).isEqualTo(userData.userDTO().id());
        assertThat(response.data().type()).isEqualTo(dto.type());

        return response.data();
    }

    public ReactionDTO createReaction(ResponseUserTest userData) throws Exception {
        CreateReactionDTO dto = new CreateReactionDTO(
                "emoji" + generateChars(),
                null,
                generateRandomUnicode(),
                1L,
                true,
                true,
                ReactionTypeEnum.EMOTION
        );

        MvcResult result = this.mockMvc.perform(post("/v1/reaction").contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto))
                        .header("Authorization", "Bearer " + userData.tokens().token()
                        ))
                .andExpect(status().isCreated())
                .andReturn();

        String json = result.getResponse().getContentAsString();
        TypeReference<ResponseHttp<ReactionDTO>> typeRef = new TypeReference<>() {};

        ResponseHttp<ReactionDTO> response = objectMapper.readValue(json, typeRef);

        assertThat(response.message()).isNotBlank();
        assertThat(response.status()).isEqualTo(true);

        assertThat(response.data().id()).isNotNegative().isNotZero();
        assertThat(response.data().name()).isEqualTo(dto.name());
        assertThat(response.data().emojiUrl()).isEqualTo(dto.emojiUrl());
        assertThat(response.data().emojiUnicode()).isEqualTo(dto.emojiUnicode());
        assertThat(response.data().displayOrder()).isEqualTo(dto.displayOrder());
        assertThat(response.data().active()).isEqualTo(dto.active());
        assertThat(response.data().visible()).isEqualTo(dto.visible());
        assertThat(response.data().type()).isEqualTo(dto.type());

        return response.data();
    }

    public CommentFavoriteDTO addCommentWithFavorite(ResponseUserTest userData, PostDTO post, CommentDTO comment) throws Exception {
        String URL = "/v1/comment-favorite";

        MvcResult result = this.mockMvc.perform(post(URL + "/" + comment.id() + "/toggle")
                        .header("Authorization", "Bearer " + userData.tokens().token()
                        ))
                .andExpect(status().isCreated())
                .andReturn();

        String json = result.getResponse().getContentAsString();
        TypeReference<ResponseHttp<CommentFavoriteDTO>> typeRef = new TypeReference<>() {};

        ResponseHttp<CommentFavoriteDTO> response = objectMapper.readValue(json, typeRef);

        assertThat(response.message()).isNotBlank();
        assertThat(response.traceId()).isNotBlank();
        assertThat(response.status()).isEqualTo(true);
        assertThat(response.data().comment().id()).isEqualTo(comment.id());
        assertThat(response.data().user().id()).isEqualTo(userData.userDTO().id());

        return response.data();
    }

    public void addPostWithFavorite(ResponseUserTest userData, PostDTO post) throws Exception {
        MvcResult result = this.mockMvc.perform(post("/v1/post-favorite/" + post.id())
                        .header("Authorization", "Bearer " + userData.tokens().token()
                        ))
                .andExpect(status().isCreated())
                .andReturn();

        String json = result.getResponse().getContentAsString();
        TypeReference<ResponseHttp<PostFavoriteDTO>> typeRef = new TypeReference<>() {};

        ResponseHttp<PostFavoriteDTO> response = objectMapper.readValue(json, typeRef);

        assertThat(response.message()).isNotBlank();
        assertThat(response.traceId()).isNotBlank();
        assertThat(response.status()).isEqualTo(true);

        assertThat(response.data().post().id()).isEqualTo(post.id());
        assertThat(response.data().user().id()).isEqualTo(userData.userDTO().id());

    }

    public CommentDTO createComment(ResponseUserTest userData, PostDTO post, Long parentID) throws Exception {
        CreateCommentDTO dto = new CreateCommentDTO(
                "content",
                post.id(),
                parentID
        );

        MvcResult result = this.mockMvc.perform(post("/v1/comment")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto))
                        .header("Authorization", "Bearer " + userData.tokens().token()
                        ))
                .andExpect(status().isCreated())
                .andReturn();

        String json = result.getResponse().getContentAsString();
        TypeReference<ResponseHttp<CommentDTO>> typeRef = new TypeReference<>() {};

        ResponseHttp<CommentDTO> response = objectMapper.readValue(json, typeRef);

        assertThat(response.message()).isNotBlank();
        assertThat(response.status()).isEqualTo(true);

        assertThat(response.data().id()).isNotNegative().isNotZero();
        assertThat(response.data().parentId()).isNull();
        assertThat(response.data().content()).isEqualTo(dto.content());
        assertThat(response.data().post().id()).isEqualTo(post.id());
        assertThat(response.data().user().id()).isEqualTo(userData.userDTO().id());

        return response.data();
    }

    public PostTagDTO addTagToPost(ResponseUserTest userTest ,PostDTO postDTO, TagDTO tagDTO) throws Exception {
        String URL = "/v1/post-tag";

        CreatePostTagDTO dto = new CreatePostTagDTO(
                postDTO.id(),
                tagDTO.id(),
                true,
                true
        );

        MvcResult result = this.mockMvc.perform(post(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto))
                        .header("Authorization", "Bearer " + userTest.tokens().token()
                        ))
                .andExpect(status().isCreated())
                .andReturn();

        String json = result.getResponse().getContentAsString();
        TypeReference<ResponseHttp<PostTagDTO>> typeRef = new TypeReference<>() {};

        ResponseHttp<PostTagDTO> response =
                objectMapper.readValue(json, typeRef);

        assertThat(response.message()).isNotBlank();
        assertThat(response.status()).isEqualTo(true);
        assertThat(response.data().id()).isNotNegative();
        assertThat(response.data().post().id()).isEqualTo(postDTO.id());
        assertThat(response.data().tag().id()).isEqualTo(tagDTO.id());
        assertThat(response.data().active()).isEqualTo(dto.active());
        assertThat(response.data().visible()).isEqualTo(dto.visible());

        return response.data();
    }

    public PostCategoriesDTO addCategoryToPost(ResponseUserTest userData, CategoryDTO categoryDTO, PostDTO postDTO) throws Exception {
        String URL = "/v1/post-category";

        CreatePostCategoriesDTO dto = new CreatePostCategoriesDTO(
                postDTO.id(),
                categoryDTO.id(),
                5,
                true,
                true
        );

        MvcResult result = this.mockMvc.perform(post(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto))
                        .header("Authorization", "Bearer " + userData.tokens().token()
                        ))
                .andExpect(status().isCreated())
                .andReturn();

        String json = result.getResponse().getContentAsString();
        TypeReference<ResponseHttp< PostCategoriesDTO>> typeRef = new TypeReference<>() {};

        ResponseHttp<PostCategoriesDTO> response = objectMapper.readValue(json, typeRef);

        assertThat(response.message()).isNotBlank();
        assertThat(response.status()).isEqualTo(true);

        assertThat(response.data().category().id()).isEqualTo(categoryDTO.id());
        assertThat(response.data().post().id()).isEqualTo(postDTO.id());
        assertThat(response.data().active()).isEqualTo(dto.active());
        assertThat(response.data().primary()).isEqualTo(dto.primary());
        assertThat(response.data().displayOrder()).isEqualTo(dto.displayOrder());

        return response.data();
    }

    public PostDTO createPost(ResponseUserTest userData) throws Exception {
        String URL = "/v1/post";

        CreatePostDTO dto = new CreatePostDTO(
                "Black pearl jem",
                "black-pearl-jem",
                """
                        Sheets of empty canvas, untouched sheets of clay
                        Were laid spread out before me, as her body once did
                        All five horizons revolved around her soul, as the Earth to the Sun
                        Now the air I tasted and breathed has taken a turn
                        Ooh, and all I taught her was everything
                        Mmm, oh, I know she gave me all that she wore
                        And now my bitter hands chafe beneath the clouds
                        Of what was everything
                        All the pictures had all been washed in black
                        Tattooed everything
                        I take a walk outside, I'm surrounded by some kids at play
                        I can feel their laughter, so why do I sear?
                        Mmm, oh, and twisted thoughts that spin 'round my head
                        I'm spinnin', oh-oh, I'm spinnin'
                        How quick the Sun can drop away
                        Now my bitter hands cradle broken glass
                        Of what was everything
                        All the pictures have all been washed in black
                        Tattooed everything
                        All the love gone bad turned my world to black
                        Tattooed all I see, all that I am
                        All I'll be, yeah-yeah
                        Oh-oh, oh-oh, ooh
                        I know someday you'll have a beautiful life, I know you'll be a star
                        In somebody else's sky, but why, why
                        Why can't it be, oh, can't it be mine?
                        Ooh, ah, yeah
                        Ah, ooh-ooh
                        Doo-doo-doo-doo, doo-doo-doo
                        Doo-doo-doo-doo, doo-doo-doo
                        Doo-doo-doo-doo, doo-doo-doo
                        Doo-doo-doo-doo, doo-doo-doo
                        Doo-doo-doo-doo, doo-doo-doo
                        Doo-doo-doo-doo, doo-doo-doo
                        Doo-doo-doo-doo, doo-doo-doo
                        Doo-doo-doo-doo, doo-doo-doo (oh, oh-yeah)
                        Doo-doo-doo-doo, doo-doo-doo
                        Doo-doo-doo-doo, doo-doo-doo (ah-ah)
                        Doo-doo-doo-doo, doo-doo-doo (ah-ah)
                        Doo-doo-doo-doo, doo-doo-doo
                        Doo-doo-doo-doo, doo-doo-doo (ah, yeah)
                        Doo-doo-doo-doo, doo-doo-doo (ah-ah, yeah)
                        Doo-doo-doo-doo, doo-doo-doo (yeah, ah)
                        Doo-doo-doo-doo, doo-doo-doo (yeah-yeah-yeah)
                        Doo-doo-doo-doo, doo-doo-doo
                        Doo-doo-doo-doo, doo-doo-doo (ooh-ooh)
                        Ooh-ooh, ooh-ooh
                        Doo-doo-doo-doo, doo-doo-doo
                        Ooh-ooh, ooh-ooh
                        Ooh-ooh""",
                5
        );

        MvcResult result = this.mockMvc.perform(post(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto))
                        .header("Authorization", "Bearer " + userData.tokens().token()
                        ))
                .andExpect(status().isCreated())
                .andReturn();

        String json = result.getResponse().getContentAsString();
        TypeReference<ResponseHttp<PostDTO>> typeRef = new TypeReference<>() {};

        ResponseHttp<PostDTO> response = objectMapper.readValue(json, typeRef);

        assertThat(response.message()).isNotBlank();
        assertThat(response.status()).isEqualTo(true);

        assertThat(response.data().title()).isEqualTo(dto.title());
        assertThat(response.data().slug()).isEqualTo(dto.slug());
        assertThat(response.data().content()).isEqualTo(dto.content());
        assertThat(response.data().readingTime()).isEqualTo(dto.readingTime());

        assertThat(response.data().author().id()).isEqualTo(userData.userDTO().id());

        return response.data();
    }

    public TagDTO createTag(ResponseUserTest userData) throws Exception {
        String URL = "/v1/tag";
        String chars = this.generateChars();

        var dto = new CreateTagDTO(
                "software engineer " + chars,
                "software-engineer-" + chars,
                "",
                true,
                true,
                true
        );

        MvcResult result = mockMvc.perform(post(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto))
                        .header("Authorization", "Bearer " + userData.tokens().token()
                        ))
                .andExpect(status().isCreated())
                .andReturn();

        String json = result.getResponse().getContentAsString();
        TypeReference<ResponseHttp<TagDTO>> typeRef = new TypeReference<>() {};

        ResponseHttp<TagDTO> response = objectMapper.readValue(json, typeRef);

        assertThat(response.message()).isNotBlank();
        assertThat(response.status()).isEqualTo(true);
        assertThat(response.data().name()).isEqualTo(dto.name());
        assertThat(response.data().slug()).isEqualTo(dto.slug());
        assertThat(response.data().description()).isEqualTo(dto.description());
        assertThat(response.data().isActive()).isEqualTo(dto.isActive());
        assertThat(response.data().isVisible()).isEqualTo(dto.isVisible());
        assertThat(response.data().isSystem()).isEqualTo(dto.isSystem());
        assertThat(response.data().postsCount()).isEqualTo(0L);
        assertThat(response.data().createdAt().getMinute()).isEqualTo(OffsetDateTime.now().getMinute());

        return response.data();
    }

    public CategoryDTO createCategory(ResponseUserTest userData, Long parentId) throws Exception {
        String URL = "/v1/category";
        String chars = this.generateChars();

        CreateCategoryDTO dto = new CreateCategoryDTO(
                "software engineer " + chars,
                "",
                "software-engineer-" + chars,
                true,
                true,
                0,
                parentId
        );

        MvcResult result = this.mockMvc.perform(post(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto))
                        .header("Authorization", "Bearer " + userData.tokens().token()
                        ))
                .andReturn();

        String json = result.getResponse().getContentAsString();
        TypeReference<ResponseHttp<CategoryDTO>> typeRef = new TypeReference<>() {};

        ResponseHttp<CategoryDTO> response =
                objectMapper.readValue(json, typeRef);

        assertThat(response.message()).isNotBlank();
        assertThat(response.status()).isEqualTo(true);
        assertThat(response.data().name()).isEqualTo(dto.name());
        assertThat(response.data().description()).isEqualTo(dto.description());
        assertThat(response.data().slug()).isEqualTo(dto.slug());
        assertThat(response.data().isActive()).isEqualTo(dto.isActive());
        assertThat(response.data().visible()).isEqualTo(dto.visible());
        assertThat(response.data().displayOrder()).isEqualTo(dto.displayOrder());

        return response.data();
    }

    public ResponseUserTest loginSuperAdm() throws Exception {
        String URL = "/v1/auth/";

        LoginUserDTO dto = new LoginUserDTO(
                "system.domain@gmail.com",
                "0123456789"
        );

        MvcResult result = mockMvc.perform(post(URL + "/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andReturn();

        String registerJson = result.getResponse().getContentAsString();
        TypeReference<ResponseHttp<ResponseTokens>> typeRef =
                new TypeReference<>() {};

        ResponseHttp<ResponseTokens> response =
                objectMapper.readValue(registerJson, typeRef);

        assertThat(response.status()).isEqualTo(true);
        assertThat(response.message()).isNotBlank();
        assertThat(response.data().token()).isNotBlank();
        assertThat(response.data().refreshToken()).isNotBlank();


        MvcResult resultGet = mockMvc.perform(get("/v1/user/me")
                        .header("Authorization", "Bearer " + response.data().token()))
                .andExpect(status().isOk()).andReturn();

        String json = resultGet.getResponse().getContentAsString();
        TypeReference<ResponseHttp<UserDTO>> typeRefGet = new TypeReference<>() {};

        ResponseHttp<UserDTO> responseGet =
                objectMapper.readValue(json, typeRefGet);

        return new ResponseUserTest(
                response.data(),
                null,
                responseGet.data()
        );

    }

    public ResponseUserTest createUser() {
        try {
            String key = UUID.fastUUID().toString();

            CreateUserDTO dto = new CreateUserDTO(
                    "name" + key,
                    "username" + key,
                    "user" + key + "@gmail.com",
                    "12345678"
            );

            MvcResult result = mockMvc.perform(post("/v1/auth/register")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(dto)))
                    .andExpect(status().isCreated())
                    .andReturn();

            String registerJson = result.getResponse().getContentAsString();
            TypeReference<ResponseHttp<ResponseTokens>> typeRef =
                    new TypeReference<>() {
                    };

            ResponseHttp<ResponseTokens> response =
                    objectMapper.readValue(registerJson, typeRef);

            assertThat(response.status()).isEqualTo(true);
            assertThat(response.message()).isNotBlank();
            assertThat(response.data().token()).isNotBlank();
            assertThat(response.data().refreshToken()).isNotBlank();

            MvcResult resultGet = mockMvc.perform(get("/v1/user/me")
                            .header("Authorization", "Bearer " + response.data().token()))
                    .andExpect(status().isOk()).andReturn();

            String json = resultGet.getResponse().getContentAsString();
            TypeReference<ResponseHttp<UserDTO>> typeRefGet = new TypeReference<>() {};

            ResponseHttp<UserDTO> responseGet =
                    objectMapper.readValue(json, typeRefGet);

            return new ResponseUserTest(
                    response.data(),
                    dto,
                    responseGet.data()
            );
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static String generateChars() {
        Random random = new Random();
        StringBuilder builder = new StringBuilder();

        for (int i = 0; i < 10; i++) {
            char l = (char) ('a' + random.nextInt(26));
            builder.append(l);
        }

        return builder.toString();
    }

    public static String generateRandomUnicode() {
        Random random = new Random();

        int start = 0x1F600;
        int end = 0x1F64F;

        int randomUnicode = start + random.nextInt(end - start + 1);
        return "U+" + Integer.toHexString(randomUnicode).toUpperCase();
    }

}
