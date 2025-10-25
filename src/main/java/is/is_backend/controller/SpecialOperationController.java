package is.is_backend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import is.is_backend.dto.organizationDto.OrganizationResponseDTO;
import is.is_backend.dto.specialOperationDto.MergeOrganizationsRequestDTO;
import is.is_backend.dto.specialOperationDto.OrganizationCountByFullNameResponseDTO;
import is.is_backend.service.SpecialOperationService;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/special-operation")
@AllArgsConstructor
@Tag(name = "Special Operations API", description = "API для выполнение дополнительного функционала")
public class SpecialOperationController {

    private final SpecialOperationService specialOperationService;

    @Operation(
            summary = "Получение организации",
            description = "Получение организации с максимальным значением официального адреса")
    @ApiResponses(
            value = {
                @ApiResponse(
                        responseCode = "200",
                        description = "Организация с максимальным официальным адресом получена",
                        content = @Content(schema = @Schema(implementation = OrganizationResponseDTO.class)))
            })
    @GetMapping("/max-official-address")
    public ResponseEntity<OrganizationResponseDTO> getOrganizationWithMaxOfficialAddress() {
        OrganizationResponseDTO response = specialOperationService.getOrganizationWithMaxOfficialAddress();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @Operation(
            summary = "Группировка организаций по полному наименованию",
            description = "Возвращает статистику по организациям, сгруппированную по полному наименованию")
    @ApiResponses(
            value = {
                @ApiResponse(
                        responseCode = "200",
                        description = "Статистика по организациям успешно получена",
                        content =
                                @Content(
                                        schema =
                                                @Schema(
                                                        implementation =
                                                                OrganizationCountByFullNameResponseDTO[].class)))
            })
    @GetMapping("/group-fullname")
    public ResponseEntity<List<OrganizationCountByFullNameResponseDTO>> getOrganizationWithGroupFullName() {
        List<OrganizationCountByFullNameResponseDTO> response =
                specialOperationService.getOrganizationCountByFullName();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @Operation(
            summary = "Поиск организаций по частичному совпадению полного наименования",
            description = "Возвращает список организаций, в полном наименовании которых содержится указанная строка")
    @ApiResponses(
            value = {
                @ApiResponse(
                        responseCode = "200",
                        description = "Организации успешно найдены",
                        content = @Content(schema = @Schema(implementation = OrganizationResponseDTO[].class)))
            })
    @GetMapping("/organization-where-fullname-contains")
    public ResponseEntity<List<OrganizationResponseDTO>> getOrganizationWhereFullNameContains(
            @Parameter(
                            description = "Строка для поиска в полном наименовании организации",
                            example = "Atom",
                            required = true)
                    @RequestParam
                    String fullName) {
        List<OrganizationResponseDTO> response = specialOperationService.getOrganizationWhereFullNameContain(fullName);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @Operation(
            summary = "Обновление количества сотрудников организации",
            description = "Обновляет количество сотрудников для указанной организации на 1")
    @ApiResponses(
            value = {
                @ApiResponse(
                        responseCode = "200",
                        description = "Количество сотрудников успешно обновлено",
                        content = @Content(schema = @Schema(implementation = OrganizationResponseDTO.class)))
            })
    @PutMapping("/update-count-employee")
    public ResponseEntity<OrganizationResponseDTO> updateOrganizationCountEmployee(
            @Parameter(description = "ID организации", required = true, example = "1") @RequestParam Long id) {
        OrganizationResponseDTO response = specialOperationService.updateCountOfEmployee(id);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @Operation(summary = "Объединение организаций", description = "Объединяет несколько организаций в одну")
    @ApiResponses(
            value = {
                @ApiResponse(
                        responseCode = "200",
                        description = "Организации успешно объединены",
                        content = @Content(schema = @Schema(implementation = OrganizationResponseDTO.class)))
            })
    @PostMapping("/join-organizations")
    public ResponseEntity<OrganizationResponseDTO> joinOrganizations(
            @Parameter(description = "DTO с данными для объединения организаций", required = true) @RequestBody
                    MergeOrganizationsRequestDTO joinOrganizationRequestDTO) {
        OrganizationResponseDTO response = specialOperationService.joinOrganization(joinOrganizationRequestDTO);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
