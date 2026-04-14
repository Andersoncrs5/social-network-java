package com.blog.writeapi.configs;

import cn.hutool.core.lang.UUID;
import com.blog.writeapi.modules.adm.dto.ToggleRoleAdmDTO;
import com.blog.writeapi.modules.adm.dto.ToggleRoleDTO;
import com.blog.writeapi.modules.apiKeys.dto.CreateApiKeyDTO;
import com.blog.writeapi.modules.category.dtos.CategoryDTO;
import com.blog.writeapi.modules.category.dtos.CreateCategoryDTO;
import com.blog.writeapi.modules.comment.dtos.CommentDTO;
import com.blog.writeapi.modules.comment.dtos.CreateCommentDTO;
import com.blog.writeapi.modules.commentFavorite.dtos.CommentFavoriteDTO;
import com.blog.writeapi.modules.commentReaction.dtos.CommentReactionDTO;
import com.blog.writeapi.modules.commentReaction.dtos.CreateCommentReactionDTO;
import com.blog.writeapi.modules.commentReport.dto.CommentReportDTO;
import com.blog.writeapi.modules.commentReport.dto.CreateCommentReportDTO;
import com.blog.writeapi.modules.commentReportType.dto.CreateCommentReportTypeDTO;
import com.blog.writeapi.modules.commentVote.dtos.CommentVoteDTO;
import com.blog.writeapi.modules.commentVote.dtos.ToggleCommentVoteDTO;
import com.blog.writeapi.modules.followers.dtos.FollowersDTO;
import com.blog.writeapi.modules.pinnedPost.dto.CreatePinnedPostDTO;
import com.blog.writeapi.modules.pinnedPost.dto.PinnedPostDTO;
import com.blog.writeapi.modules.post.dtos.CreatePostDTO;
import com.blog.writeapi.modules.post.dtos.PostDTO;
import com.blog.writeapi.modules.postCategory.dtos.CreatePostCategoriesDTO;
import com.blog.writeapi.modules.postCategory.dtos.PostCategoriesDTO;
import com.blog.writeapi.modules.postFavorite.dtos.PostFavoriteDTO;
import com.blog.writeapi.modules.postReaction.dtos.CreatePostReactionDTO;
import com.blog.writeapi.modules.postReaction.dtos.PostReactionDTO;
import com.blog.writeapi.modules.postReportType.dto.CreatePostReportTypeDTO;
import com.blog.writeapi.modules.postShare.dto.CreatePostShareDTO;
import com.blog.writeapi.modules.postShare.dto.PostShareDTO;
import com.blog.writeapi.modules.postTag.dtos.CreatePostTagDTO;
import com.blog.writeapi.modules.postTag.dtos.PostTagDTO;
import com.blog.writeapi.modules.postVote.dtos.PostVoteDTO;
import com.blog.writeapi.modules.postVote.dtos.TogglePostVoteDTO;
import com.blog.writeapi.modules.reaction.dtos.CreateReactionDTO;
import com.blog.writeapi.modules.reaction.dtos.ReactionDTO;
import com.blog.writeapi.modules.reportPost.dto.CreatePostReportDTO;
import com.blog.writeapi.modules.reportPost.dto.PostReportDTO;
import com.blog.writeapi.modules.reportType.dto.CreateReportTypeDTO;
import com.blog.writeapi.modules.reportType.dto.ReportTypeDTO;
import com.blog.writeapi.modules.stories.dto.StoryDTO;
import com.blog.writeapi.modules.tag.dtos.CreateTagDTO;
import com.blog.writeapi.modules.tag.dtos.TagDTO;
import com.blog.writeapi.modules.user.dtos.CreateUserDTO;
import com.blog.writeapi.modules.user.dtos.LoginUserDTO;
import com.blog.writeapi.modules.user.dtos.UserDTO;
import com.blog.writeapi.modules.userCategoryPreference.dtos.UserCategoryPreferenceDTO;
import com.blog.writeapi.modules.userReport.dto.CreateUserReportDTO;
import com.blog.writeapi.modules.userReport.dto.UserReportDTO;
import com.blog.writeapi.modules.userReportType.dto.CreateUserReportTypeDTO;
import com.blog.writeapi.modules.userTagPreference.dtos.UserTagPreferenceDTO;
import com.blog.writeapi.utils.enums.postShare.SharePlatformEnum;
import com.blog.writeapi.utils.enums.reaction.ReactionTypeEnum;
import com.blog.writeapi.utils.enums.report.ReportPriority;
import com.blog.writeapi.utils.enums.report.ReportReason;
import com.blog.writeapi.utils.enums.votes.VoteTypeEnum;
import com.blog.writeapi.utils.res.ResponseHttp;
import com.blog.writeapi.utils.res.ResponseTokens;
import com.blog.writeapi.utils.res.ResponseUserTest;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.stereotype.Service;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.OffsetDateTime;
import java.util.Random;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Service
@RequiredArgsConstructor
public class HelperTest {

    private final MockMvc mockMvc;
    private final ObjectMapper objectMapper;

    public StoryDTO createStory(ResponseUserTest userData) throws Exception {
        try {
            Path path = Paths.get("src/test/java/com/blog/writeapi/utils/resources/foto.png");
            byte[] content = Files.readAllBytes(path);

            MockMultipartFile filePart = new MockMultipartFile(
                    "file",
                    "image" + HelperTest.generateChars() + ".png",
                    "image/png",
                    content
            );

            MvcResult result = this.mockMvc.perform(multipart("/v1/story")
                            .file(filePart)
                            .param("fileName", "pochita-wallpaper")
                            .param("contentType", "image/png")
                            .param("isPublic", "true")
                            .param("isVisible", "true")
                            .param("backgroundColor", "#FFFFFF")
                            .param("caption", "Black")
                            .header("Authorization", "Bearer " + userData.tokens().token())
                            .header("X-Idempotency-Key", UUID.randomUUID().toString())
                    )
                    .andExpect(status().isCreated())
                    .andReturn();

            String json = result.getResponse().getContentAsString();
            ResponseHttp<StoryDTO> response = objectMapper.readValue(json, new TypeReference<>() {});

            assertThat(response.data().getFileName()).isEqualTo("pochita-wallpaper");
            assertThat(response.data().getStorageKey()).isNotBlank();
            assertThat(response.data().getContentType()).isEqualTo("image/png");
            assertThat(response.data().getFileSize()).isEqualTo(filePart.getBytes().length);

            assertThat(response.status()).isTrue();

            return response.data();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public String createApiKey(
            ResponseUserTest adm
    ) throws Exception {
        CreateApiKeyDTO dto = new CreateApiKeyDTO(("api-read-" + generateChars()));

        MvcResult result = mockMvc.perform(post("/v1/admin/api-keys")
                .content(objectMapper.writeValueAsString(dto))
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + adm.tokens().token())
        ).andExpect(status().isCreated()).andReturn();

        String json = result.getResponse().getContentAsString();
        TypeReference<ResponseHttp<String>> typeRef = new TypeReference<>() {};

        ResponseHttp<String> response = objectMapper.readValue(json, typeRef);

        assertThat(response.message()).isNotBlank();
        assertThat(response.status()).isEqualTo(true);
        assertThat(response.version()).isEqualTo(1);

        assertThat(response.data()).isNotBlank();

        return response.data();
    }

    public PostShareDTO sharePost(
            ResponseUserTest userTest,
            PostDTO post
    ) throws Exception {
        String URL = "/v1/post-share";

        CreatePostShareDTO dto = new CreatePostShareDTO(
                post.id(),
                SharePlatformEnum.REDDIT
        );

        MvcResult result = mockMvc.perform(post(URL)
                .content(objectMapper.writeValueAsString(dto))
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + userTest.tokens().token())
        ).andExpect(status().isCreated()).andReturn();

        String json = result.getResponse().getContentAsString();
        TypeReference<ResponseHttp<PostShareDTO>> typeRef = new TypeReference<>() {};

        ResponseHttp<PostShareDTO> response = objectMapper.readValue(json, typeRef);

        assertThat(response.message()).isNotBlank();
        assertThat(response.status()).isEqualTo(true);

        assertThat(response.data()).isNotNull();
        assertThat(response.data().id()).isNotZero().isNotNegative();
        assertThat(response.data().post().id()).isEqualTo(post.id());
        assertThat(response.data().user().id()).isEqualTo(userTest.userDTO().id());
        assertThat(response.data().platform()).isEqualTo(dto.platform());

        return response.data();
    }

    public void addedPostToReading(
            ResponseUserTest userTest,
            Long postId
    ) {
        try {
            MvcResult result = mockMvc.perform(post("/v1/post-reading-list/toggle/" + postId)
                    .header("Authorization", "Bearer " + userTest.tokens().token())
            ).andExpect(status().isCreated()).andReturn();

            String json = result.getResponse().getContentAsString();
            TypeReference<ResponseHttp<Void>> typeRef = new TypeReference<>() {};

            ResponseHttp<Void> response =
                    objectMapper.readValue(json, typeRef);

            assertThat(response.message()).isNotBlank();
            assertThat(response.status()).isEqualTo(true);
            assertThat(response.traceId()).isNotBlank();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public PinnedPostDTO markPostWithPinned(
            ResponseUserTest userData,
            PostDTO post
    ) throws Exception {
        CreatePinnedPostDTO dto = new CreatePinnedPostDTO(
                post.id(),
                1
        );

        MvcResult result = this.mockMvc.perform(post("/v1/pinned-post")
                .header("Authorization", "Bearer " + userData.tokens().token())
                .content(objectMapper.writeValueAsString(dto))
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isCreated()).andReturn();

        String json = result.getResponse().getContentAsString();
        TypeReference<ResponseHttp<PinnedPostDTO>> typeRef = new TypeReference<>() {};

        ResponseHttp<PinnedPostDTO> response = objectMapper.readValue(json, typeRef);

        assertThat(response.message()).isNotBlank();
        assertThat(response.status()).isEqualTo(true);

        assertThat(response.data().id()).isNotNull().isPositive();
        assertThat(response.data().post().id()).isEqualTo(post.id());
        assertThat(response.data().user().id()).isEqualTo(userData.userDTO().id());
        assertThat(response.data().orderIndex()).isEqualTo(dto.orderIndex());

        return response.data();
    }

    public void addReportTypeToUserReport(
            UserReportDTO userReportDTO,
            ReportTypeDTO reportTypeDTO,
            ResponseUserTest reporter
    ) {
        try {
            CreateUserReportTypeDTO dto = new CreateUserReportTypeDTO(
                    userReportDTO.id(),
                    reportTypeDTO.id()
            );

            MvcResult result = mockMvc.perform(post("/v1/user-report-type/toggle")
                    .header("Authorization", "Bearer " + reporter.tokens().token())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(dto))
            ).andExpect(status().isCreated()).andReturn();

            String json = result.getResponse().getContentAsString();
            TypeReference<ResponseHttp<Void>> typeRef = new TypeReference<>() {};

            ResponseHttp<Void> response = objectMapper.readValue(json, typeRef);

            assertThat(response.data()).isNull();
            assertThat(response.status()).isTrue();
            assertThat(response.message()).isNotBlank();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public UserReportDTO addReportToUser(
            ResponseUserTest reporter,
            ResponseUserTest reportedUser
    ) {
        try {
            CreateUserReportDTO dto = new CreateUserReportDTO(
                    "AnyDesc",
                    ReportReason.MISINFORMATION,
                    reportedUser.userDTO().id()
            );

            MvcResult result = mockMvc.perform(post("/v1/user-report")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(dto))
                            .header("Authorization", "Bearer " + reporter.tokens().token()
                            ))
                    .andExpect(status().isCreated()).andReturn();

            String json = result.getResponse().getContentAsString();
            TypeReference<ResponseHttp<UserReportDTO>> typeRef = new TypeReference<>() {};

            ResponseHttp<UserReportDTO> response =
                    objectMapper.readValue(json, typeRef);

            assertThat(response.message()).isNotBlank();
            assertThat(response.status()).isEqualTo(true);
            assertThat(response.data()).isNotNull();

            assertThat(response.data().id()).isNotNull();
            assertThat(response.data().reporter().id()).isEqualTo(reporter.userDTO().id());
            assertThat(response.data().reportedUser().id()).isEqualTo(reportedUser.userDTO().id());
            assertThat(response.data().description()).isEqualTo(dto.description());
            assertThat(response.data().reason()).isEqualTo(dto.reason());

            return response.data();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void addUserWithBlock(
            ResponseUserTest userTest,
            ResponseUserTest userTest2
    ) {
        try {
            MvcResult result = mockMvc.perform(post("/v1/user-block/" + userTest2.userDTO().id() + "/toggle")
                    .header("Authorization", "Bearer " + userTest.tokens().token())
            ).andExpect(status().isCreated()).andReturn();

            String json = result.getResponse().getContentAsString();
            TypeReference<ResponseHttp<Object>> typeRef = new TypeReference<>() {};

            ResponseHttp<Object> response = objectMapper.readValue(json, typeRef);

            assertThat(response.message()).isNotBlank();
            assertThat(response.status()).isEqualTo(true);

            assertThat(response.data()).isNull();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void addReportTypeToComment(
            CommentReportDTO commentReportDTO,
            ResponseUserTest userTest2,
            ReportTypeDTO reportTypeDTO
    ) throws Exception {

        CreateCommentReportTypeDTO dto = new CreateCommentReportTypeDTO(
                commentReportDTO.id(),
                reportTypeDTO.id()
        );

        MvcResult result = mockMvc.perform(post("/v1/comment-report-type/toggle")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto))
                .header("Authorization", "Bearer " + userTest2.tokens().token())
        ).andExpect(status().isCreated()).andReturn();

        String json = result.getResponse().getContentAsString();
        TypeReference<ResponseHttp<Object>> typeRef = new TypeReference<>() {};

        ResponseHttp<Object> response = objectMapper.readValue(json, typeRef);

        assertThat(response.message()).isNotBlank();
        assertThat(response.status()).isEqualTo(true);

        assertThat(response.data()).isNull();
    }

    public CommentReportDTO addReportToComment(
            ResponseUserTest userTest2,
            CommentDTO commentDTO
    ) {

        try {
            CreateCommentReportDTO dto = new CreateCommentReportDTO(
                    "Comment Bad",
                    ReportReason.VIOLENCE,
                    commentDTO.id()
            );

            MvcResult result = mockMvc.perform(post("/v1/comment-report")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(dto))
                    .header("Authorization", "Bearer " + userTest2.tokens().token())
            ).andExpect(status().isCreated()).andReturn();

            String json = result.getResponse().getContentAsString();
            TypeReference<ResponseHttp<CommentReportDTO>> typeRef = new TypeReference<>() {};

            ResponseHttp<CommentReportDTO> response = objectMapper.readValue(json, typeRef);

            assertThat(response.message()).isNotBlank();
            assertThat(response.status()).isEqualTo(true);

            assertThat(response.data()).isNotNull();

            assertThat(response.data().id()).isPositive().isNotZero();
            assertThat(response.data().description()).isEqualTo(dto.description());
            assertThat(response.data().reason()).isEqualTo(dto.reason());
            assertThat(response.data().comment().id()).isEqualTo(dto.commentId());
            assertThat(response.data().user().id()).isEqualTo(userTest2.userDTO().id());
            assertThat(response.data().commentAuthorId()).isEqualTo(commentDTO.user().id());
            assertThat(response.data().commentContentSnapshot()).isNotBlank();

            return response.data();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void AddedReportTypeToPostReportType(
            ResponseUserTest userTest2,
            ReportTypeDTO reportTypeDTO,
            PostReportDTO postReportDTO
    ) throws Exception {
        CreatePostReportTypeDTO dto = new CreatePostReportTypeDTO(
                postReportDTO.id(),
                reportTypeDTO.id()
        );

        MvcResult result = mockMvc.perform(post("/v1/post-report-type/toggle")
                .header("Authorization", "Bearer " + userTest2.tokens().token())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto))
        ).andExpect(status().isCreated()).andReturn();

        String json = result.getResponse().getContentAsString();
        TypeReference<ResponseHttp<Object>> typeRef = new TypeReference<>() {};

        ResponseHttp<Object> response = objectMapper.readValue(json, typeRef);

        assertThat(response.data()).isNull();
        assertThat(response.message()).isNotBlank();
    }

    public ResponseUserTest loginUserInModerator() throws Exception {
        ResponseUserTest userModerator = this.createUser();
        ResponseUserTest master = this.loginSuperAdm();

        this.addRoleInUser(master, userModerator, "MODERATOR_ROLE");

        ResponseTokens response = this.loginUser(userModerator);

        MvcResult resultGet = mockMvc.perform(get("/v1/user/me")
                        .header("Authorization", "Bearer " + response.token()))
                .andExpect(status().isOk()).andReturn();

        String json = resultGet.getResponse().getContentAsString();
        TypeReference<ResponseHttp<UserDTO>> typeRefGet = new TypeReference<>() {};

        ResponseHttp<UserDTO> responseGet =
                objectMapper.readValue(json, typeRefGet);

        return new ResponseUserTest(
                response,
                userModerator.dto(),
                responseGet.data()
        );
    }

    public ResponseTokens loginUser(ResponseUserTest res) throws Exception {

        LoginUserDTO dto = new LoginUserDTO(
                res.dto().email(),
                res.dto().password()
        );

        MvcResult result = mockMvc.perform(post("/v1/auth/login")
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

        return response.data();
    }

    public void turnUserInAdm(
            ResponseUserTest master,
            ResponseUserTest userTest
    ) {
        try {
            ToggleRoleAdmDTO dto = new ToggleRoleAdmDTO(
                    userTest.userDTO().id()
            );

            MvcResult result = mockMvc.perform(patch("/v1/adm/toggle/role/adm")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(dto))
                    .header("Authorization", "Bearer " + master.tokens().token())
            ).andExpect(status().isCreated()).andReturn();

            String json = result.getResponse().getContentAsString();
            TypeReference<ResponseHttp<Object>> typeRef = new TypeReference<>() {};

            ResponseHttp<Object> response =
                    objectMapper.readValue(json, typeRef);

            assertThat(response.message()).isNotBlank();
            assertThat(response.traceId()).isNotBlank();
            assertThat(response.status()).isEqualTo(true);

            assertThat(response.data()).isNull();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void addRoleInUser(
            ResponseUserTest master,
            ResponseUserTest userTest2,
            String role
    ) throws Exception {

        ToggleRoleDTO dto = new ToggleRoleDTO(
                role,
                userTest2.userDTO().id()
        );

        MvcResult result = mockMvc.perform(post("/v1/adm/roles/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto))
                        .header("Authorization", "Bearer " + master.tokens().token()
                        ))
                .andExpect(status().isCreated())
                .andReturn();

        String json = result.getResponse().getContentAsString();
        TypeReference<ResponseHttp<Object>> typeRef = new TypeReference<>() {};

        ResponseHttp<Object> response =
                objectMapper.readValue(json, typeRef);

        assertThat(response.message()).isNotBlank();
        assertThat(response.traceId()).isNotBlank();
        assertThat(response.status()).isEqualTo(true);

        assertThat(response.data()).isNull();

    }

    public PostReportDTO createPostReportDTO(
            ResponseUserTest userTest,
            ResponseUserTest userTest2,
            PostDTO postDTO
    ) throws Exception {

        CreatePostReportDTO dto = new CreatePostReportDTO(
                "Post Bad",
                ReportReason.VIOLENCE,
                postDTO.id()
        );

        MvcResult result = mockMvc.perform(post("/v1/post-report")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto))
                .header("Authorization", "Bearer " + userTest2.tokens().token())
        ).andExpect(status().isCreated()).andReturn();

        String json = result.getResponse().getContentAsString();
        TypeReference<ResponseHttp<PostReportDTO>> typeRef = new TypeReference<>() {};

        ResponseHttp<PostReportDTO> response = objectMapper.readValue(json, typeRef);

        assertThat(response.message()).isNotBlank();
        assertThat(response.status()).isEqualTo(true);

        assertThat(response.data()).isNotNull();

        assertThat(response.data().id()).isPositive().isNotZero();
        assertThat(response.data().description()).isEqualTo(dto.description());
        assertThat(response.data().reason()).isEqualTo(dto.reason());
        assertThat(response.data().post().id()).isEqualTo(dto.postId());
        assertThat(response.data().user().id()).isEqualTo(userTest2.userDTO().id());
        assertThat(response.data().postAuthorId()).isEqualTo(postDTO.author().id());
        assertThat(response.data().postContentSnapshot()).isNotBlank();

        return response.data();
    }

    public ReportTypeDTO createReportType(ResponseUserTest superAdm) {
        try {
            CreateReportTypeDTO dto = new CreateReportTypeDTO(
                    "name" + java.util.UUID.randomUUID(),
                    "DescAny",
                    null,
                    "#0000000",
                    1,
                    ReportPriority.HIGH,
                    true,
                    true
            );

            MvcResult result = this.mockMvc.perform(post("/v1/report-type")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(dto))
                    .header("Authorization", "Bearer " + superAdm.tokens().token())
            ).andExpect(status().isCreated()).andReturn();

            String json = result.getResponse().getContentAsString();
            TypeReference<ResponseHttp<ReportTypeDTO>> typeRef = new TypeReference<>() {};

            ResponseHttp<ReportTypeDTO> response = objectMapper.readValue(json, typeRef);

            assertThat(response.message()).isNotBlank();
            assertThat(response.traceId()).isNotBlank();
            assertThat(response.status()).isEqualTo(true);

            assertThat(response.data()).isNotNull();

            assertThat(response.data().id()).isPositive().isNotZero();
            assertThat(response.data().color()).isEqualTo(dto.color());
            assertThat(response.data().name()).isEqualTo(dto.name());
            assertThat(response.data().description()).isEqualTo(dto.description());
            assertThat(response.data().icon()).isEqualTo(dto.icon());
            assertThat(response.data().color()).isEqualTo(dto.color());
            assertThat(response.data().displayOrder()).isEqualTo(dto.displayOrder());
            assertThat(response.data().defaultPriority()).isEqualTo(dto.defaultPriority());
            assertThat(response.data().isActive()).isEqualTo(dto.isActive());
            assertThat(response.data().isVisible()).isEqualTo(dto.isVisible());

            return response.data();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public FollowersDTO followUser(ResponseUserTest followData, ResponseUserTest followingData) throws Exception {

        MvcResult result = this.mockMvc.perform(post("/v1/follow/" + followingData.userDTO().id() + "/toggle")
                        .header("Authorization", "Bearer " + followData.tokens().token()
                        ))
                .andExpect(status().isCreated())
                .andReturn();

        String json = result.getResponse().getContentAsString();
        TypeReference<ResponseHttp<FollowersDTO>> typeRef = new TypeReference<>() {};

        ResponseHttp<FollowersDTO> response = objectMapper.readValue(json, typeRef);

        assertThat(response.data()).isNotNull();
        assertThat(response.status()).isTrue();
        assertThat(response.message()).isNotBlank();
        assertThat(response.traceId()).isNotBlank();

        assertThat(response.data().id()).isNotZero().isPositive();
        assertThat(response.data().follower().id()).isEqualTo(followData.userDTO().id());
        assertThat(response.data().following().id()).isEqualTo(followingData.userDTO().id());
        assertThat(response.data().isMuted()).isFalse();
        assertThat(response.data().notifyPosts()).isTrue();
        assertThat(response.data().notifyComments()).isTrue();

        return response.data();
    }

    public UserTagPreferenceDTO addTagInPreferenceUser(
            ResponseUserTest userData,
            TagDTO tag
    ) throws Exception {
        MvcResult result = this.mockMvc.perform(post("/v1/user-tag-preference/" + tag.id() + "/toggle")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + userData.tokens().token()
                )).andExpect(status().isCreated()).andReturn();

        String json = result.getResponse().getContentAsString();
        TypeReference<ResponseHttp<UserTagPreferenceDTO>> typeRef = new TypeReference<>() {};

        ResponseHttp<UserTagPreferenceDTO> response =
                objectMapper.readValue(json, typeRef);

        assertThat(response.message()).isNotBlank();
        assertThat(response.status()).isEqualTo(true);
        assertThat(response.data().id()).isNotZero().isPositive().isNotNull();
        assertThat(response.data().tag().id()).isEqualTo(tag.id());
        assertThat(response.data().user().id()).isEqualTo(userData.userDTO().id());

        return response.data();
    }

    public UserCategoryPreferenceDTO addCategoryInPreferenceUser(
            ResponseUserTest userData,
            CategoryDTO category
    ) throws Exception {
        MvcResult result = this.mockMvc.perform(post("/v1/user-category-preference/" + category.id() + "/toggle")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + userData.tokens().token()
                )).andExpect(status().isCreated()).andReturn();

        String json = result.getResponse().getContentAsString();
        TypeReference<ResponseHttp<UserCategoryPreferenceDTO>> typeRef = new TypeReference<>() {};

        ResponseHttp<UserCategoryPreferenceDTO> response =
                objectMapper.readValue(json, typeRef);

        assertThat(response.message()).isNotBlank();
        assertThat(response.traceId()).isNotBlank();
        assertThat(response.status()).isEqualTo(true);
        assertThat(response.data().id()).isNotZero().isPositive().isNotNull();
        assertThat(response.data().category().id()).isEqualTo(category.id());
        assertThat(response.data().user().id()).isEqualTo(userData.userDTO().id());

        return response.data();
    }

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
                "black-pearl-jem" + generateChars(),
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
                5,
                null
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

    public ResponseUserTest loginSuperAdm() {
        try {
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

            return new ResponseUserTest(
                    response.data(),
                    null,
                    response.data().user()
            );
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
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

            return new ResponseUserTest(
                    response.data(),
                    dto,
                    response.data().user()
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

    public CreatePostDTO createPostDTO(Long postID) {
        return new CreatePostDTO(
                "All five horizons revolved around her soul, as the Earth to the Sun",
                "black-pearl-jem" + generateChars(),
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
                5,
                postID
        );
    }

}
