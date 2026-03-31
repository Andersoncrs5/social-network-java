package com.blog.writeapi.integration;

import com.blog.writeapi.configs.HelperTest;
import com.blog.writeapi.modules.adm.dto.ToggleRoleAdmDTO;
import com.blog.writeapi.modules.adm.dto.ToggleRoleDTO;
import com.blog.writeapi.modules.role.models.RoleModel;
import com.blog.writeapi.modules.role.repository.RoleRepository;
import com.blog.writeapi.modules.user.repository.UserRepository;
import com.blog.writeapi.utils.exceptions.ModelNotFoundException;
import com.blog.writeapi.utils.res.ResponseHttp;
import com.blog.writeapi.utils.res.ResponseUserTest;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class AdmControllerTest {
    private final String URL = "/v1/adm";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private HelperTest helper;

    @Test
    void shouldAddRoleToUser() throws Exception {
        ResponseUserTest master = this.helper.loginSuperAdm();
        ResponseUserTest userTest2 = this.helper.createUser();

        RoleModel moderatorRole = roleRepository.findByNameIgnoreCase("MODERATOR_ROLE")
                .orElseThrow(() -> new ModelNotFoundException("MODERATOR_ROLE not found"));

        ToggleRoleDTO dto = new ToggleRoleDTO(
                moderatorRole.getName(),
                userTest2.userDTO().id()
        );

        MvcResult result = mockMvc.perform(post(this.URL + "/roles/add")
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

    @Test
    void shouldReturnNotFoundRoleAddRoleToUser() throws Exception {
        ResponseUserTest master = this.helper.loginSuperAdm();
        ResponseUserTest userTest2 = this.helper.createUser();

        ToggleRoleDTO dto = new ToggleRoleDTO(
                "MODERATOR_ROLE_A",
                userTest2.userDTO().id()
        );

        MvcResult result = mockMvc.perform(post(this.URL + "/roles/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto))
                        .header("Authorization", "Bearer " + master.tokens().token()
                        ))
                .andExpect(status().isNotFound())
                .andReturn();

        String json = result.getResponse().getContentAsString();
        TypeReference<ResponseHttp<Object>> typeRef = new TypeReference<>() {};

        ResponseHttp<Object> response =
                objectMapper.readValue(json, typeRef);

        assertThat(response.message()).isNotBlank();
        assertThat(response.traceId()).isNotBlank();
        assertThat(response.status()).isEqualTo(false);

        assertThat(response.data()).isNull();
    }

    @Test
    void shouldReturnNotFoundUserAddRoleToUser() throws Exception {
        ResponseUserTest master = this.helper.loginSuperAdm();

        ToggleRoleDTO dto = new ToggleRoleDTO(
                "MODERATOR_ROLE",
                (master.userDTO().id() + 1)
        );

        MvcResult result = mockMvc.perform(post(this.URL + "/roles/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto))
                        .header("Authorization", "Bearer " + master.tokens().token()
                        ))
                .andExpect(status().isNotFound())
                .andReturn();

        String json = result.getResponse().getContentAsString();
        TypeReference<ResponseHttp<Object>> typeRef = new TypeReference<>() {};

        ResponseHttp<Object> response =
                objectMapper.readValue(json, typeRef);

        assertThat(response.message()).isNotBlank();
        assertThat(response.traceId()).isNotBlank();
        assertThat(response.status()).isEqualTo(false);

        assertThat(response.data()).isNull();
    }

    @Test
    void shouldReturnForbWhenAddRoleSUPER_ADM_ROLEWhenExecAddRoleToUser() throws Exception {
        ResponseUserTest master = this.helper.loginSuperAdm();
        ResponseUserTest userTest2 = this.helper.createUser();

        ToggleRoleDTO dto = new ToggleRoleDTO(
                "SUPER_ADM_ROLE",
                userTest2.userDTO().id()
        );

        MvcResult result = mockMvc.perform(post(this.URL + "/roles/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto))
                        .header("Authorization", "Bearer " + master.tokens().token()
                        ))
                .andExpect(status().isForbidden())
                .andReturn();

        String json = result.getResponse().getContentAsString();
        TypeReference<ResponseHttp<Object>> typeRef = new TypeReference<>() {};

        ResponseHttp<Object> response =
                objectMapper.readValue(json, typeRef);

        assertThat(response.message()).isNotBlank();
        assertThat(response.traceId()).isNotBlank();
        assertThat(response.status()).isEqualTo(false);

        assertThat(response.data()).isNull();
    }

    @Test
    void shouldReturnForbWhenAddRoleADM_ROLEWhenExecAddRoleToUser() throws Exception {
        ResponseUserTest master = this.helper.loginSuperAdm();
        ResponseUserTest userTest2 = this.helper.createUser();

        ToggleRoleDTO dto = new ToggleRoleDTO(
                "ADM_ROLE",
                userTest2.userDTO().id()
        );

        MvcResult result = mockMvc.perform(post(this.URL + "/roles/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto))
                        .header("Authorization", "Bearer " + master.tokens().token()
                        ))
                .andExpect(status().isForbidden())
                .andReturn();

        String json = result.getResponse().getContentAsString();
        TypeReference<ResponseHttp<Object>> typeRef = new TypeReference<>() {};

        ResponseHttp<Object> response =
                objectMapper.readValue(json, typeRef);

        assertThat(response.message()).isNotBlank();
        assertThat(response.traceId()).isNotBlank();
        assertThat(response.status()).isEqualTo(false);

        assertThat(response.data()).isNull();
    }

    @Test
    void shouldReturnConflictBecauseUserAlreadyHaveTheRoleAdded() throws Exception {
        ResponseUserTest master = this.helper.loginSuperAdm();
        ResponseUserTest userTest2 = this.helper.createUser();
        this.helper.addRoleInUser(master, userTest2, "MODERATOR_ROLE");

        ToggleRoleDTO dto = new ToggleRoleDTO(
                "MODERATOR_ROLE",
                userTest2.userDTO().id()
        );

        MvcResult result = mockMvc.perform(post(this.URL + "/roles/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto))
                        .header("Authorization", "Bearer " + master.tokens().token()
                        ))
                .andExpect(status().isConflict())
                .andReturn();

        String json = result.getResponse().getContentAsString();
        TypeReference<ResponseHttp<Object>> typeRef = new TypeReference<>() {};

        ResponseHttp<Object> response =
                objectMapper.readValue(json, typeRef);

        assertThat(response.message()).isNotBlank();
        assertThat(response.traceId()).isNotBlank();
        assertThat(response.status()).isEqualTo(false);

        assertThat(response.data()).isNull();
    }

    @Test
    void shouldRemoveRoleOfUser() throws Exception {
        String roleName = "MODERATOR_ROLE";
        ResponseUserTest master = this.helper.loginSuperAdm();
        ResponseUserTest userTest2 = this.helper.createUser();

        this.helper.addRoleInUser(master, userTest2, roleName);

        ToggleRoleDTO dto = new ToggleRoleDTO(
                roleName,
                userTest2.userDTO().id()
        );

        MvcResult result = mockMvc.perform(post(this.URL + "/roles/remove")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto))
                        .header("Authorization", "Bearer " + master.tokens().token()
                        ))
                .andExpect(status().isOk())
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

    @Test
    void shouldReturnNotFoundRoleRemoveRoleOfUser() throws Exception {
        String roleName = "MODERATOR_ROLE";
        ResponseUserTest master = this.helper.loginSuperAdm();
        ResponseUserTest userTest2 = this.helper.createUser();

        this.helper.addRoleInUser(master, userTest2, roleName);

        ToggleRoleDTO dto = new ToggleRoleDTO(
                roleName + "A",
                userTest2.userDTO().id()
        );

        MvcResult result = mockMvc.perform(post(this.URL + "/roles/remove")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto))
                        .header("Authorization", "Bearer " + master.tokens().token()
                        ))
                .andExpect(status().isNotFound())
                .andReturn();

        String json = result.getResponse().getContentAsString();
        TypeReference<ResponseHttp<Object>> typeRef = new TypeReference<>() {};

        ResponseHttp<Object> response =
                objectMapper.readValue(json, typeRef);

        assertThat(response.message()).isNotBlank();
        assertThat(response.traceId()).isNotBlank();
        assertThat(response.status()).isEqualTo(false);

        assertThat(response.data()).isNull();
    }

    @Test
    void shouldReturnNotFoundUserRemoveRoleOfUser() throws Exception {
        String roleName = "MODERATOR_ROLE";

        ResponseUserTest master = this.helper.loginSuperAdm();
        ResponseUserTest userTest2 = this.helper.createUser();

        this.helper.addRoleInUser(master, userTest2, roleName);

        ToggleRoleDTO dto = new ToggleRoleDTO(
                roleName,
                (userTest2.userDTO().id() + 1 )
        );

        MvcResult result = mockMvc.perform(post(this.URL + "/roles/remove")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto))
                        .header("Authorization", "Bearer " + master.tokens().token()
                        ))
                .andExpect(status().isNotFound())
                .andReturn();

        String json = result.getResponse().getContentAsString();
        TypeReference<ResponseHttp<Object>> typeRef = new TypeReference<>() {};

        ResponseHttp<Object> response =
                objectMapper.readValue(json, typeRef);

        assertThat(response.message()).isNotBlank();
        assertThat(response.traceId()).isNotBlank();
        assertThat(response.status()).isEqualTo(false);

        assertThat(response.data()).isNull();
    }

    @Test
    void shouldReturnNotFoundUserRoleRemoveRoleOfUser() throws Exception {
        String roleName = "MODERATOR_ROLE";

        ResponseUserTest master = this.helper.loginSuperAdm();
        ResponseUserTest userTest2 = this.helper.createUser();

        ToggleRoleDTO dto = new ToggleRoleDTO(
                roleName,
                userTest2.userDTO().id()
        );

        MvcResult result = mockMvc.perform(post(this.URL + "/roles/remove")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto))
                        .header("Authorization", "Bearer " + master.tokens().token()
                        ))
                .andExpect(status().isNotFound())
                .andReturn();

        String json = result.getResponse().getContentAsString();
        TypeReference<ResponseHttp<Object>> typeRef = new TypeReference<>() {};

        ResponseHttp<Object> response =
                objectMapper.readValue(json, typeRef);

        assertThat(response.message()).isNotBlank();
        assertThat(response.traceId()).isNotBlank();
        assertThat(response.status()).isEqualTo(false);

        assertThat(response.data()).isNull();
    }

    @Test
    void shouldReturnFORBIDDENWhenRemoveRoleSUPER_ADM_ROLERemoveRoleOfUser() throws Exception {
        String roleName = "SUPER_ADM_ROLE";

        ResponseUserTest master = this.helper.loginSuperAdm();
        ResponseUserTest userTest2 = this.helper.createUser();

        ToggleRoleDTO dto = new ToggleRoleDTO(
                roleName,
                userTest2.userDTO().id()
        );

        MvcResult result = mockMvc.perform(post(this.URL + "/roles/remove")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto))
                        .header("Authorization", "Bearer " + master.tokens().token()
                        ))
                .andExpect(status().isForbidden())
                .andReturn();

        String json = result.getResponse().getContentAsString();
        TypeReference<ResponseHttp<Object>> typeRef = new TypeReference<>() {};

        ResponseHttp<Object> response =
                objectMapper.readValue(json, typeRef);

        assertThat(response.message()).isNotBlank();
        assertThat(response.traceId()).isNotBlank();
        assertThat(response.status()).isEqualTo(false);

        assertThat(response.data()).isNull();
    }

    @Test
    void shouldReturnFORBIDDENWhenRemoveRoleADM_ROLERemoveRoleOfUser() throws Exception {
        String roleName = "ADM_ROLE";

        ResponseUserTest master = this.helper.loginSuperAdm();
        ResponseUserTest userTest2 = this.helper.createUser();

        ToggleRoleDTO dto = new ToggleRoleDTO(
                roleName,
                userTest2.userDTO().id()
        );

        MvcResult result = mockMvc.perform(post(this.URL + "/roles/remove")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto))
                        .header("Authorization", "Bearer " + master.tokens().token()
                        ))
                .andExpect(status().isForbidden())
                .andReturn();

        String json = result.getResponse().getContentAsString();
        TypeReference<ResponseHttp<Object>> typeRef = new TypeReference<>() {};

        ResponseHttp<Object> response =
                objectMapper.readValue(json, typeRef);

        assertThat(response.message()).isNotBlank();
        assertThat(response.traceId()).isNotBlank();
        assertThat(response.status()).isEqualTo(false);

        assertThat(response.data()).isNull();
    }

    @Test
    void shouldAddRoleAdmToUser() throws Exception {
        ResponseUserTest master = this.helper.loginSuperAdm();
        ResponseUserTest userTest = this.helper.createUser();

        ToggleRoleAdmDTO dto = new ToggleRoleAdmDTO(
                userTest.userDTO().id()
        );

        MvcResult result = mockMvc.perform(patch(this.URL + "/toggle/role/adm")
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
    }

    @Test
    void shouldRemoveRoleAdmToUser() throws Exception {
        ResponseUserTest master = this.helper.loginSuperAdm();
        ResponseUserTest userTest = this.helper.createUser();
        this.helper.turnUserInAdm(master, userTest);

        ToggleRoleAdmDTO dto = new ToggleRoleAdmDTO(
                userTest.userDTO().id()
        );

        MvcResult result = mockMvc.perform(patch(this.URL + "/toggle/role/adm")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto))
                        .header("Authorization", "Bearer " + master.tokens().token())
                ).andExpect(status().isOk()).andReturn();

        String json = result.getResponse().getContentAsString();
        TypeReference<ResponseHttp<Object>> typeRef = new TypeReference<>() {};

        ResponseHttp<Object> response =
                objectMapper.readValue(json, typeRef);

        assertThat(response.message()).isNotBlank();
        assertThat(response.traceId()).isNotBlank();
        assertThat(response.status()).isEqualTo(true);

        assertThat(response.data()).isNull();
    }

}
