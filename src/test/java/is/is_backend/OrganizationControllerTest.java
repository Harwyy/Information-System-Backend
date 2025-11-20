package is.is_backend;

import static is.is_backend.utils.FileReader.readJsonFile;
import static org.assertj.core.api.Assertions.assertThat;

import is.is_backend.models.Organization;
import is.is_backend.repository.OrganizationRepository;
import java.util.List;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class OrganizationControllerTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private OrganizationRepository organizationRepository;

    @LocalServerPort
    private int port;

    private String getBaseUrl() {
        return "http://localhost:" + port;
    }

    public String apiEndpoint;
    public String requestJson;
    public String testName;

    private void configureForOrganizationCreate() {
        this.apiEndpoint = "/api/organization";
        this.requestJson = readJsonFile("requests/create-organization-request.json");
        this.testName = "Organization";
    }

    private void configureForOrganizationUpdate() {
        this.apiEndpoint = "/api/organization/1";
        this.requestJson = readJsonFile("requests/update-organization-request.json");
        this.testName = "Organization";
    }

    private void configureForOrganizationDelete() {
        this.apiEndpoint = "/api/organization/1";
        this.requestJson = "";
        this.testName = "Organization";
    }

    @Test
    @Order(1)
    @DisplayName("Test Creating Organization")
    public void testCreateOrganization() {
        configureForOrganizationCreate();
        HttpEntity<String> request = createHttpEntity(requestJson);
        ResponseEntity<String> response = restTemplate.postForEntity(getBaseUrl() + apiEndpoint, request, String.class);

        assertThat(response.getStatusCode())
                .as("API should return 200 OK status")
                .isEqualTo(HttpStatus.OK);

        List<Organization> organizations = organizationRepository.findByName("Aboba");
        assertThat(organizations)
                .as("Should find at least one organization with name 'Aboba'")
                .isNotEmpty();

        Organization createdOrganization = organizations.get(0);
        assertThat(createdOrganization.getName())
                .as("Organization name should match")
                .isEqualTo("Aboba");

        System.out.println("✅ Creation test passed");
    }

    @Test
    @Order(2)
    @DisplayName("Test Updating Organization")
    public void testUpdateOrganization() {
        configureForOrganizationUpdate();
        HttpEntity<String> request = createHttpEntity(requestJson);
        ResponseEntity<String> response =
                restTemplate.exchange(getBaseUrl() + apiEndpoint, HttpMethod.PUT, request, String.class);

        assertThat(response.getStatusCode())
                .as("API should return 200 OK status")
                .isEqualTo(HttpStatus.OK);

        List<Organization> organizations = organizationRepository.findByName("Atom");
        assertThat(organizations)
                .as("Should find at least one organization with name 'Atom'")
                .isNotEmpty();

        Organization updatedOrganization = organizations.get(0);
        assertThat(updatedOrganization.getName())
                .as("Organization name should match")
                .isEqualTo("Atom");

        System.out.println("✅ Update test passed");
    }

    @Test
    @Order(3)
    @DisplayName("Test Deleting Organization")
    public void testDeleteOrganization() {
        configureForOrganizationDelete();
        HttpEntity<String> request = createHttpEntity(requestJson);
        ResponseEntity<String> response =
                restTemplate.exchange(getBaseUrl() + apiEndpoint, HttpMethod.DELETE, request, String.class);

        assertThat(response.getStatusCode())
                .as("API should return 200 OK status")
                .isEqualTo(HttpStatus.OK);

        List<Organization> organizations = organizationRepository.findByName("Atom");
        assertThat(organizations).as("Should be empty").isEmpty();

        System.out.println("✅ Delete test passed");
    }

    private HttpEntity<String> createHttpEntity(String jsonBody) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return new HttpEntity<>(jsonBody, headers);
    }
}
