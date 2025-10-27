package is.is_backend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import is.is_backend.dto.organizationDto.OrganizationPageRequestDTO;
import is.is_backend.dto.organizationDto.OrganizationPageResponseDTO;
import is.is_backend.dto.organizationDto.OrganizationRequestDTO;
import is.is_backend.dto.organizationDto.OrganizationResponseDTO;
import is.is_backend.mapper.OrganizationMapper;
import is.is_backend.models.Organization;
import is.is_backend.service.OrganizationService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/organization")
@AllArgsConstructor
@Tag(
        name = "Organizations API",
        description = "API для управления организациями - создание, чтение, обновление и удаление организаций")
public class OrganizationController {

    private final OrganizationService organizationService;

    @Operation(
            summary = "Создание новых организаций",
            description = "Создает новую организацию в системе и отправляет уведомление")
    @ApiResponses(
            value = {
                @ApiResponse(
                        responseCode = "200",
                        description = "Организация успешно создана",
                        content = @Content(schema = @Schema(implementation = OrganizationResponseDTO.class)))
            })
    @PostMapping
    public ResponseEntity<OrganizationResponseDTO> createOrganization(
            @Parameter(description = "DTO для создания организации") @RequestBody
                    OrganizationRequestDTO organizationRequestDTO) {
        OrganizationResponseDTO response = organizationService.createOrganization(organizationRequestDTO);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @Operation(
            summary = "Обновление организации",
            description = "Обновляет существующую организацию по id и отправляет уведомление")
    @ApiResponses(
            value = {
                @ApiResponse(
                        responseCode = "200",
                        description = "Организация успешно обновлена",
                        content = @Content(schema = @Schema(implementation = OrganizationResponseDTO.class)))
            })
    @PutMapping("/{id}")
    public ResponseEntity<OrganizationResponseDTO> updateOrganization(
            @Parameter(description = "ID организации", required = true, example = "1") @PathVariable("id") Long id,
            @Parameter(description = "DTO для обновления организации") @Valid @RequestBody
                    OrganizationRequestDTO organizationRequestDTO) {
        OrganizationResponseDTO response = organizationService.updateOrganization(id, organizationRequestDTO);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @Operation(
            summary = "Удаление организации",
            description = "Удаляет организацию по идентификатору и отправляет уведомление")
    @ApiResponses(
            value = {
                @ApiResponse(
                        responseCode = "200",
                        description = "Организация успешно удалена",
                        content = @Content(schema = @Schema(implementation = OrganizationResponseDTO.class)))
            })
    @DeleteMapping("/{id}")
    public ResponseEntity<OrganizationResponseDTO> deleteOrganization(
            @Parameter(description = "ID организации", required = true, example = "1") @PathVariable("id") Long id) {
        OrganizationResponseDTO response = organizationService.deleteOrganization(id);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @Operation(
            summary = "Получение всех организаций",
            description = "Возвращает пагинированный список всех организаций с возможностью фильтрации")
    @ApiResponses(
            value = {
                @ApiResponse(
                        responseCode = "200",
                        description = "Список организаций успешно получен",
                        content = @Content(schema = @Schema(implementation = OrganizationPageResponseDTO.class)))
            })
    @GetMapping
    public ResponseEntity<OrganizationPageResponseDTO> getAllOrganizations(
            @Parameter(description = "Параметры пагинации и фильтрации") @ModelAttribute
                    OrganizationPageRequestDTO pageRequest) {
        Page<Organization> organizationPage = organizationService.getAllOrganizations(pageRequest);
        Page<OrganizationResponseDTO> dtoPage = organizationPage.map(OrganizationMapper::toResponseDTO);
        OrganizationPageResponseDTO response = OrganizationPageResponseDTO.fromPage(dtoPage);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @Operation(summary = "Получение организации по id", description = "Возвращает конкретные организации по id")
    @ApiResponses(
            value = {
                @ApiResponse(
                        responseCode = "200",
                        description = "организация успешно найден",
                        content = @Content(schema = @Schema(implementation = OrganizationResponseDTO.class)))
            })
    @GetMapping("/{id}")
    public ResponseEntity<OrganizationResponseDTO> getOrganization(
            @Parameter(description = "ID адреса", required = true, example = "1") @PathVariable("id") Long id) {
        Organization organization = organizationService.getOrganization(id);
        OrganizationResponseDTO response = OrganizationMapper.toResponseDTO(organization);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
