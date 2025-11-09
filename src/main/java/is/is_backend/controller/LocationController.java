package is.is_backend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import is.is_backend.dto.locationDto.LocationPageRequestDTO;
import is.is_backend.dto.locationDto.LocationPageResponseDTO;
import is.is_backend.dto.locationDto.LocationRequestDTO;
import is.is_backend.dto.locationDto.LocationResponseDTO;
import is.is_backend.mapper.LocationMapper;
import is.is_backend.models.Location;
import is.is_backend.service.LocationService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/location")
@AllArgsConstructor
@Tag(
        name = "Locations API",
        description = "API для управления локациями - создание, чтение, обновление и удаление локаций")
public class LocationController {

    private final LocationService locationService;

    @Operation(
            summary = "Создание новых локаций",
            description = "Создает новую локацию в системе и отправляет уведомление")
    @ApiResponses(
            value = {
                @ApiResponse(
                        responseCode = "200",
                        description = "Локация успешно создана",
                        content = @Content(schema = @Schema(implementation = LocationResponseDTO.class)))
            })
    @PostMapping
    public ResponseEntity<LocationResponseDTO> createLocation(
            @Parameter(description = "DTO для создания локации") @Valid @RequestBody
                    LocationRequestDTO locationRequestDTO) {
        Location location = LocationMapper.toEntity(locationRequestDTO);
        Location saved = locationService.createLocation(location);
        LocationResponseDTO response = LocationMapper.toResponseDTO(saved);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @Operation(
            summary = "Обновление локации",
            description = "Обновляет существующую локацию по id и отправляет уведомление")
    @ApiResponses(
            value = {
                @ApiResponse(
                        responseCode = "200",
                        description = "Локация успешно обновлена",
                        content = @Content(schema = @Schema(implementation = LocationResponseDTO.class)))
            })
    @PutMapping("/{id}")
    public ResponseEntity<LocationResponseDTO> updateLocation(
            @Parameter(description = "ID локации", required = true, example = "1") @PathVariable Long id,
            @Parameter(description = "DTO для обновления локации") @Valid @RequestBody
                    LocationRequestDTO locationRequestDTO) {
        Location location = LocationMapper.toEntity(locationRequestDTO);
        Location updated = locationService.updateLocation(id, location);
        LocationResponseDTO response = LocationMapper.toResponseDTO(updated);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @Operation(summary = "Удаление локации", description = "Удаляет локацию по идентификатору и отправляет уведомление")
    @ApiResponses(
            value = {
                @ApiResponse(
                        responseCode = "200",
                        description = "Локация успешно удалена",
                        content = @Content(schema = @Schema(implementation = LocationResponseDTO.class)))
            })
    @DeleteMapping("/{id}")
    public ResponseEntity<LocationResponseDTO> deleteLocation(
            @Parameter(description = "ID локации", required = true, example = "1") @PathVariable Long id) {
        Location deleted = locationService.deleteLocation(id);
        LocationResponseDTO response = LocationMapper.toResponseDTO(deleted);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @Operation(
            summary = "Получение всех локаций",
            description = "Возвращает пагинированный список всех локаций с возможностью фильтрации")
    @ApiResponses(
            value = {
                @ApiResponse(
                        responseCode = "200",
                        description = "Список локаций успешно получен",
                        content = @Content(schema = @Schema(implementation = LocationPageResponseDTO.class)))
            })
    @GetMapping
    public ResponseEntity<LocationPageResponseDTO> getAllLocation(
            @Parameter(description = "Параметры пагинации и фильтрации") @ModelAttribute
                    LocationPageRequestDTO pageRequest) {
        Page<Location> locationPage = locationService.getAllLocations(pageRequest);
        Page<LocationResponseDTO> dtoPage = locationPage.map(LocationMapper::toResponseDTO);
        LocationPageResponseDTO response = LocationPageResponseDTO.fromPage(dtoPage);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @Operation(summary = "Получение локации по id", description = "Возвращает конкретные локации по id")
    @ApiResponses(
            value = {
                @ApiResponse(
                        responseCode = "200",
                        description = "Локация успешно найдена",
                        content = @Content(schema = @Schema(implementation = LocationResponseDTO.class)))
            })
    @GetMapping("/{id}")
    public ResponseEntity<LocationResponseDTO> getLocation(
            @Parameter(description = "ID локации", required = true, example = "1") @PathVariable Long id) {
        Location location = locationService.getLocation(id);
        LocationResponseDTO response = LocationMapper.toResponseDTO(location);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
